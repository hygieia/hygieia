package com.capitalone.dashboard.datafactory.jira.sdk.util;

import java.io.IOException;
import java.net.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.capitalone.dashboard.datafactory.jira.sdk.config.ApiPropertiesSupplier;

/**
 * This class retains the ability to gather user and application system meta-data when
 * and where needed.  This can be used to either inform a given application's actions
 * on a given system, or to send an application tracking packet as a header to
 * Jira for performance monitoring purposes.  This is intended to be used only for application monitoring
 * purposes.
 * 
 * @author KFK884
 */
public class SystemInfo {
	/**
	 * Houses the concatenated String for the returned Jira API transaction header
	 */
	protected String systemInfoMsg;
	/**
	 * Houses the user name of a given system using this library
	 */
	protected String userName;
	/**
	 * Houses the Java main class name using this library
	 */
	protected String mainClass;
	
	/**
	 * Initialized class for the logging mechanism for this class
	 */
	private static final Log log = LogFactory.getLog(ApiPropertiesSupplier.class);

	/**
	 * Default constructor for SystemInfo class
	 */
	public SystemInfo() {
		systemInfoMsg = "";
		userName = "";
		mainClass = "";
	}
	
    /**
    * This method search into Thread Stack Trace and get the bottom entry of
    * Stack, which would be the main class name of a given application using this
    * library.  It also pulls the user name who is running this program based on
    * their individual system account name.  Other meta-data will also be gathered
    * this method and added to the header.
    * 
     * @return : systemInfoMsg A string concatenation of local application system and
     * user meta-data to be added to a given transaction header.
    */
    public String generateApplicationUseHeader() {
    	systemInfoMsg = "";
    	
    	this.setUserName();
    	this.setClassName();
    	
        systemInfoMsg = "SYSTEM INFO HEADER FOR JIRA API TRANSACTION: " + getUserName() + "; " + getClassName();
        log.info("USER-AGENT-HDR:  [" + systemInfoMsg + "]");
        return systemInfoMsg;
    }
    
    /**
     * Sets the local value of a user name based on system settings
     */
    private void setUserName() {
    	userName = "User Name of System: " + System.getProperty("user.name");
    }
    
    /**
     * Manually provide a given application's user name (not required for use of this
     * class).  Useful if the user name will be a non-expiring account or system
     * account. 
     */
    public void setUserName(String userName) {
    	this.userName = userName;
    }
    
    /**
     * Sets the local value of the main class of a given application using this library
     */
    private void setClassName() {
    	mainClass = System.getProperty("java.class.path");
    	mainClass = mainClass.replaceAll(" ", "%20");
    	mainClass = mainClass.replaceAll(";", ",");
    	mainClass = mainClass.replaceAll("[^a-zA-Z0-9/\\\\s_%,]+", "_");
        mainClass = "Main Application Jar: " + mainClass;
    }
    
    /**
     * Manually provide a given application's main class name (not required for use of
     * this class).  Useful if the application which is leveraging this library is using
     * Java EE beans or non-standard Java main method/class structure. 
     */
    public void setClassName(String mainClass) {
    	this.mainClass = mainClass;
    }
    
    /**
     * Retrieves the currently set user name for the system and application using this
     * Java library
     * 
     * @return userName String user name
     */
    public String getUserName() {
    	return this.userName;
    }
    
    /**
     * Retrieves the currently set main class name for this Java application using
     * the Jira Client library
     * 
     * @return mainClass String main class name of the Java application
     */
    public String getClassName() {
    	return this.mainClass;
    }
    
    /**
     * Retrieves the containing Jar URL and name that is using this connector package at
     * runtime.
     * 
     * @author Nirav Thaker
     * @link http://blog.nirav.name/2008/03/how-to-find-which-jar-file-contains.html?showComment=1204706820000
     * 
     * @return The Jar URL and name as a string of the currently containing Jar at runtime
     */
    protected String getJarURL() {
        URL clsUrl = getClass().getResource(getClass().getSimpleName() + ".class");
        
        if (clsUrl != null) {
            try {
                URLConnection conn = clsUrl.openConnection();
                if (conn instanceof JarURLConnection) {
                    JarURLConnection connection = (JarURLConnection) conn;
                    return connection.getJarFileURL().toString();
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        return "No Jar or class name retrieved";
    }
    
    /**
	 * Method returns code source of given class. This is URL of classpath
	 * folder, zip or jar file. If code source is unknown, returns a string
	 * indicating that nothing was retrieved.
	 * 
	 * @author Andrei Solntsev
	 * @link http://asolntsev.blogspot.com/2008/03/how-to-find-which-jar-file-
	 *       contains.html
	 * 
	 * @param clazz
	 *            Will only take the SystemInfo class as a parameter, for
	 *            security purposes
	 * @return A class directory: For example,
	 *         "file:/C:/jdev10/jdev/mywork/classes/" or
	 *         "file:/C:/works/projects/classes12.zip"
	 */
    protected String getCodeSource(Class<SystemInfo> clazz) {
	    if (clazz == null || clazz.getProtectionDomain() == null || clazz.getProtectionDomain().getCodeSource() == null || clazz.getProtectionDomain().getCodeSource().getLocation() == null) {
	       // This typically happens for system classloader
	       // (java.lang.* etc. classes)
	       return "No Jar or class name retrieved";
	    }

	    return clazz.getProtectionDomain().getCodeSource().getLocation().toString();
    }
}