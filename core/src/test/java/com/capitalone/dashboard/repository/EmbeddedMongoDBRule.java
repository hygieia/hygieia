package com.capitalone.dashboard.repository;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.IProxyFactory;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.StringTokenizer;

/**
 *
 */
@SuppressWarnings("deprecation")
public class EmbeddedMongoDBRule extends ExternalResource {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EmbeddedMongoDBRule.class);
	private static final String MONGO_PORT_PROP = "MONGO_PORT";
	private MongodExecutable mongoExec;
	private MongodProcess mongoProc;
	private MongoClient client;

	static class SystemProxy implements IProxyFactory {
		@Override
		public Proxy createProxy() {

			String proxy = System.getenv("HTTP_PROXY");

			if (proxy == null || proxy.isEmpty()) {
				proxy = System.getProperty("HTTP_PROXY");
			}
			try {
				URL proxyUrl = new URL(proxy);

				// Case for Proxy authentication required
				try {
					String proxyUserInfo = proxyUrl.getUserInfo();
					if (proxyUserInfo != null) {
						StringTokenizer tokenizedUrl = new StringTokenizer(proxyUserInfo, ":");
						if (tokenizedUrl.hasMoreTokens()) {
							final String authUser = tokenizedUrl.nextToken();
							if (tokenizedUrl.hasMoreTokens()) {
								final String authPassword = tokenizedUrl
										.nextToken();

								if ((proxy != null && !proxy.isEmpty())
										&& (authUser != null && !authUser.isEmpty())
										&& (authUser != null && !authPassword
												.isEmpty())) {

									Authenticator.setDefault(new Authenticator() {
										public PasswordAuthentication getPasswordAuthentication() {
											return new PasswordAuthentication(
													authUser, authPassword
															.toCharArray());
										}
									});

									System.setProperty("http.proxyUser", authUser);
									System.setProperty("http.proxyPassword",
											authPassword);

								}
							} else {
								LOGGER.warn("Proxy Authentication did not contain a valid password parameter\nSkipping Authenticated proxy step.");
							}
						} else {
							LOGGER.warn("Proxy Authentication did not contain user info\nSkipping Authenticated proxy step.");
						}
					} else {
						LOGGER.info("Proxy did not contain authentication parameters - assuming non-authenticated proxy");
					}
				} catch (IllegalArgumentException e) {
					LOGGER.warn(
							"Malformed Proxy Authentication Credentials for HTTP Proxy in "
									+ this.getClass().getName(), e);
				}

				// Configuring proxy
				if (proxy != null && !proxy.isEmpty()) {
					return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
							proxyUrl.getHost(), proxyUrl.getPort()));
				}
			} catch (MalformedURLException ex) {
				LOGGER.error("Malformed HTTP Proxy for "
						+ this.getClass().getName(), ex);
			} catch (NullPointerException npe) {
				LOGGER.error(
						"Unexpectedly, something in your proxy configuration was blank or misreferenced for "
								+ this.getClass().getName(), npe);
			}
			return Proxy.NO_PROXY;
		}
	}

	@Override
	public void before() throws Throwable {

		int port = Network.getFreeServerPort();
		String portProp = System.getProperty(MONGO_PORT_PROP);
		if (portProp != null && !portProp.isEmpty()) {
			port = Integer.valueOf(portProp);
		}

		IMongodConfig conf = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(port, Network.localhostIsIPv6())).build();

		Command command = Command.MongoD;
		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(command, LOGGER)
				.artifactStore(
						new ArtifactStoreBuilder().defaults(command).download(
								new DownloadConfigBuilder().defaultsForCommand(
										command)
										.proxyFactory(new SystemProxy())))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		mongoExec = runtime.prepare(conf);

		mongoProc = mongoExec.start();

		client = new MongoClient(new ServerAddress(conf.net()
				.getServerAddress(), conf.net().getPort()));

		// set the property for our config...
		System.setProperty("dbhost", conf.net().getServerAddress()
				.getHostAddress());
		System.setProperty("dbport", Integer.toString(conf.net().getPort()));
	}

	@Override
	public void after() {
		if (client != null) {
			client.close();
			client = null;
		}

		if (mongoProc != null) {
			mongoProc.stop();
			mongoProc = null;
		}
		if (mongoExec != null) {
			mongoExec.stop();
			mongoExec = null;
		}
	}

	public MongoClient client() {
		return client;
	}
}
