package com.fmis.fmis_proxy_intf.fmis_proxy_intf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main application class for the FMIS Proxy Interface.
 * Extends {@link SpringBootServletInitializer} to support traditional deployment (e.g., WAR files).
 */
@SpringBootApplication
public class FmisProxyIntfApplication extends SpringBootServletInitializer {

	/**
	 * Main method which acts as the starting point of the Spring Boot application.
	 *
	 * @param args Command-line arguments passed during application startup.
	 */
	public static void main(String[] args) {
		SpringApplication.run(FmisProxyIntfApplication.class, args);
	}
}