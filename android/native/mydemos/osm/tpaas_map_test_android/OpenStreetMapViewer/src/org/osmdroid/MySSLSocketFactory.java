/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.lono.acdc
 *
 * File name:
 *	    MySSLSocketFactory.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Mar 4, 20142:41:12 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *  Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package org.osmdroid;

import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * @author sprabhu
 *
 */
public class MySSLSocketFactory extends SSLSocketFactory {

   
   
   private SSLContext sslContext = SSLContext.getInstance("TLS");
   /**
    * @param truststore
    * @throws NoSuchAlgorithmException
    * @throws KeyManagementException
    * @throws KeyStoreException
    * @throws UnrecoverableKeyException
    */
   public MySSLSocketFactory(KeyStore truststore)
         throws NoSuchAlgorithmException, KeyManagementException,
         KeyStoreException, UnrecoverableKeyException {
      super(truststore);
      final TrustEveryoneManager tm = new TrustEveryoneManager();
      sslContext.init(null, new TrustManager[] { tm }, null);
   }

   /**
    * @param keystore
    * @param keystorePassword
    * @throws NoSuchAlgorithmException
    * @throws KeyManagementException
    * @throws KeyStoreException
    * @throws UnrecoverableKeyException
    */
   public MySSLSocketFactory(KeyStore keystore, String keystorePassword)
         throws NoSuchAlgorithmException, KeyManagementException,
         KeyStoreException, UnrecoverableKeyException {
      super(keystore, keystorePassword);
     
   }

   /**
    * @param keystore
    * @param keystorePassword
    * @param truststore
    * @throws NoSuchAlgorithmException
    * @throws KeyManagementException
    * @throws KeyStoreException
    * @throws UnrecoverableKeyException
    */
   public MySSLSocketFactory(KeyStore keystore, String keystorePassword,
         KeyStore truststore) throws NoSuchAlgorithmException,
         KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      super(keystore, keystorePassword, truststore);
      
   }
   
   /* (non-Javadoc)
    * @see org.apache.http.conn.ssl.SSLSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
    */
   @Override
   public Socket createSocket(Socket socket, String host, int port,
         boolean autoClose) throws IOException, UnknownHostException {
      return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
   }
   /* (non-Javadoc)
    * @see org.apache.http.conn.ssl.SSLSocketFactory#createSocket()
    */
   @Override
   public Socket createSocket() throws IOException {
      return sslContext.getSocketFactory().createSocket();
   }

   /**
    * @param algorithm
    * @param keystore
    * @param keystorePassword
    * @param truststore
    * @param random
    * @param nameResolver
    * @throws NoSuchAlgorithmException
    * @throws KeyManagementException
    * @throws KeyStoreException
    * @throws UnrecoverableKeyException
    */
   public MySSLSocketFactory(String algorithm, KeyStore keystore,
         String keystorePassword, KeyStore truststore, SecureRandom random,
         HostNameResolver nameResolver) throws NoSuchAlgorithmException,
         KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      super(algorithm, keystore, keystorePassword, truststore, random,
            nameResolver);
      
   }
   public static class TrustEveryoneManager implements X509TrustManager {
      public void checkClientTrusted(X509Certificate[] arg0, String arg1){}
      public void checkServerTrusted(X509Certificate[] arg0, String arg1){}
      public X509Certificate[] getAcceptedIssuers() {
          return null;
      }
  };
}
