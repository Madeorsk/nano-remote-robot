package com.madeorsk.nrr;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class of application properties.
 */
public class ApplicationProperties
{
	/**
	 * Properties to fill with data.
	 */
	private static final Properties appProperties = new Properties();

	static
	{
		// Default values.
		appProperties.setProperty("host", "localhost");
		appProperties.setProperty("port", "8056");

		appProperties.setProperty("username", "root");
		appProperties.setProperty("password", "0000");

		try
		{ // Try to load application properties file.
			appProperties.load(new FileInputStream("./app.properties"));
		} catch (IOException e)
		{
			System.err.println("Cannot load application properties.");
		}
	}

	/**
	 * Get loaded properties.
	 * @return Properties.
	 */
	public static Properties get()
	{
		return appProperties;
	}
}
