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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Properties;

/**
 *
 */
public class EmbeddedMongoDBRule extends ExternalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedMongoDBRule.class);
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
                if (proxy != null && !proxy.isEmpty()) {
                    URL proxyUrl = new URL(proxy);
                    return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort()));
                }
            } catch (MalformedURLException ex) {
                LOGGER.error("Malformed HTTP Proxy", ex);
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
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();


        Command command = Command.MongoD;
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(command)
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(command)
                        .download(new DownloadConfigBuilder()
                                .defaultsForCommand(command)
                                .proxyFactory(new SystemProxy())))
                .build();


        MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
        mongoExec = runtime.prepare(conf);


        mongoProc = mongoExec.start();

        client = new MongoClient(new ServerAddress(conf.net().getServerAddress(), conf.net().getPort()));

        // set the property for our config...
        writeConfig(conf);
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

    void writeConfig(IMongodConfig conf) throws IOException {
        File propsFile = File.createTempFile("hygieia", "temp");
        propsFile.deleteOnExit();
        Properties props = new Properties();
        props.setProperty("dbhost", conf.net().getServerAddress().getHostAddress());
        props.setProperty("dbport", Integer.toString(conf.net().getPort()));

        try (OutputStream out = new FileOutputStream(propsFile);) {
            props.store(out, "");
        }
        System.setProperty("dashboard.prop", propsFile.getAbsolutePath());
    }
}
