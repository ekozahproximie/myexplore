package com.navteq.lpa;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import android.util.Log;

/**
 * NavteqLPASecurity.java
 * 
 * This is a convenience class that performs some common security
 * tasks required by the LPA system.
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 *
 */
public class NavteqLPASecurity
{
	public NavteqLPASecurity()
	{
		
	}
	
	public static String getHashedString(String str)
	{
		return convertBinary2Hexadecimal(oneWayHash(str));
	}
	
	/**
	 * Performs a SHA-1 hash on the specified string and returns the result.
	 * 
	 * @param toHash
	 * @return
	 */
	private static byte[] oneWayHash(String toHash)
	{
		MessageDigest sha;
		
		byte[] ret = null;
		
		try {
			byte[] data = toHash.getBytes();
			sha = MessageDigest.getInstance("SHA-1");
			
			if(sha != null)
			{
				sha.update(data);
				ret = sha.digest();
			}
			
		} catch (NoSuchAlgorithmException e) {
			
			ret = new byte[0];
			
			Log.e("NavteqLPASecurity", "Cannot obtain SHA-1 hasher");
		}
		
		return ret;
	}
	
    private static String convertBinary2Hexadecimal(byte[] binary) {
    	StringBuffer buf = new StringBuffer();
    	
    	for (int i=0; i<binary.length; i++) 
    	{
    		buf.append(String.format("%02x", (binary[i]  & 0xff)));
    	}
     
    	return buf.toString();
    }
    
    /**
     * Generates a hashed security token using the supplied privateKey, seed,
     * and affiliateNameTag.
     * 
     * @param privateKey
     * @param seed
     * @param affiliateNameTag
     * @return
     */
    public static String generateSecurityToken(String privateKey, String seed, String affiliateNameTag)
    {
    	String ret = "";
    	
    	byte[] hashBytes = NavteqLPASecurity.oneWayHash(privateKey+seed+affiliateNameTag);
    	
    	for(int i=0; i < hashBytes.length; i++)
    	{
    		ret = ret.concat(String.format("%s%d", (i == 0 ? "" : "%20"), (hashBytes[i]  & 0xff)));
    	}
    	
    	return ret;
    }
    
    /**
     * Generates a security key
     * 
     * @return
     */
    public static String generateSecurityKey()
    {
    	Random randGen = new Random();
    	return ""+ Math.abs((randGen.nextInt() % 100000000));
    }
	
}