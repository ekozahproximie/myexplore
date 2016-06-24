package com.trimble.ag.ats.acdc.applicationIndentity.req;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;




public class Encryptor {
   
   /*
    * This secret key will be shared by both the server and the client.
    * The secret key must be eight bytes or characters long.
    */ 
   private static final String SHARED_SECRET_KEY = "a507f222";
   
   private static final String ENCRYPTION_ALGORITHM  = "DES/CBC/PKCS5Padding";
   
   private static final String ENCRYPTION_KEY_TYPE = "DES";

    private Cipher cipher;
    private SecretKeySpec key;
    
    // Initialize the cryptographic engine.
    // The key array should be at least 8 bytes long.
    public Encryptor( byte[] keybytes ){
    	//defines the encryption algorithm to use by instantiating the appropriate encryption engine, in this case DESEngine, which represents the 56-bit DES algorithm
 
       try {
         cipher =  Cipher.getInstance(ENCRYPTION_ALGORITHM);
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
         throw new IllegalArgumentException();
      } catch (NoSuchPaddingException e) {
         e.printStackTrace();
         throw new IllegalArgumentException();
      }
        
       byte[] paddedKey = padKeyToLength(keybytes, DESKeySpec.DES_KEY_LEN);
       key = new SecretKeySpec(paddedKey, ENCRYPTION_KEY_TYPE);
    }
    
    private byte[] padKeyToLength(byte[] key, int len) {
       byte[] newKey = new byte[len];
       System.arraycopy(key, 0, newKey, 0, Math.min(key.length, len));
       return newKey;
     }
    

    // Initialize the cryptographic engine.
    // The string should be at least 8 chars long.
    public Encryptor( String key ){
        this( key.getBytes() );
    }
    
    // Initialize the cryptographic engine with the default shared key.
    public Encryptor() {
       this( SHARED_SECRET_KEY.getBytes() );
    }

    // Private routine that does the gritty work.
    private byte[] callCipher( byte[] data , int mode)
                        throws GeneralSecurityException {
        
       IvParameterSpec iv = new IvParameterSpec(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });
       cipher.init(mode, key, iv);
       return cipher.doFinal(data);
    }

    // Encrypt arbitrary byte array, returning the
    // encrypted data in a different byte array.
    public synchronized byte[] encrypt( byte[] data )
                  throws GeneralSecurityException {
        if( data == null || data.length == 0 ){
            return new byte[0];
        }
        return callCipher( data, Cipher.ENCRYPT_MODE );
    }

    // Encrypts a string.
    public byte[] encryptString( String data )
                  throws GeneralSecurityException {
        if( data == null || data.length() == 0 ){
            return new byte[0];
        }

        return encrypt( data.getBytes() );
    }

    // Decrypts arbitrary data.
    public synchronized byte[] decrypt( byte[] data )
                  throws GeneralSecurityException {
        if( data == null || data.length == 0 ){
            return new byte[0];
        }

        return callCipher( data, Cipher.DECRYPT_MODE );
    }

    // Decrypts a string that was previously encoded
    // using encryptString.
    public String decryptString( byte[] data )
                    throws GeneralSecurityException {
        if( data == null || data.length == 0 ){
            return "";
        }

        return new String( decrypt( data ) );
    }
}
