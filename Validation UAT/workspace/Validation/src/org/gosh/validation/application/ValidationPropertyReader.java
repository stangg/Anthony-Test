/**
 * 
 */
package org.gosh.validation.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author gayathri.polavaram
 */
public class ValidationPropertyReader {
	
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	String major = "0";
	String minor = "0";
	String build = "0";
	
	Properties propertiesInstance;
	
	String configurationLocation = "";
	
	String confFileName = "buildinfo.properties";
	
	public ValidationPropertyReader() {
		propertiesInstance = new Properties();
	}
	
	public void readProperties() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configurationLocation + confFileName);
	    try {
	    	if (inputStream != null) {
				propertiesInstance.load(inputStream);
				major = propertiesInstance.getProperty("major");
				minor = propertiesInstance.getProperty("minor");
				build = propertiesInstance.getProperty("build");
				log.info("Configuration loaded");
	    	}
		} catch (IOException e) {
			log.error("Configuration could not be read because of the following error: " + e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	public String getVersion() {
		return major + "." + minor + "." + build;
	}
	
	public String getProperty(String key) {
		return propertiesInstance.getProperty(key);
	}
	
	public void setConfLocation(String location) {
		configurationLocation = location;
	}
}


