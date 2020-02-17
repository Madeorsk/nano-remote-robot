package com.madeorsk.nrr;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utils
{
	/**
	 * Make SHA-256 hash with base64 encoding for the given string.
	 * @param str - String to hash.
	 * @return Base64 encoding of the hashed string.
	 */
	public static String sha256b64(String str)
	{
		try
		{ // SHA-256 algorithm.
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes()); // String bytes input.
			return new String(
					Base64.getEncoder().encode(messageDigest.digest()) // Encoding hashed string to base64.
			); // String bytes output.
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Make an authentication token from the root username and password.
	 * @return A token generated from the root username and password (sha256-base64).
	 */
	public static String makeAuthenticationToken()
	{
		return sha256b64(ApplicationProperties.get().getProperty("username") + "::" + ApplicationProperties.get().getProperty("password"));
	}
}
