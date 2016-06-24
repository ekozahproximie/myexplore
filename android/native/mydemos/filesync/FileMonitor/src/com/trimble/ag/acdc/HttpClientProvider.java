package com.trimble.ag.acdc;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import com.trimble.ag.filemonitor.utils.Utils;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;

public final class HttpClientProvider {

   private static HttpClientProvider HTTP_CLIENT_PROVIDER = null;

   private AbstractHttpClient                httpClient           = null;
   
   private transient  HttpContext localContext =null;
   
   private static final String LOG=HttpClientProvider.class.getSimpleName();
   

   private HttpClientProvider(final Application application) {
      if (HTTP_CLIENT_PROVIDER != null) {
         throw new IllegalAccessError("use getInstance for obtain object !");
      }
      HTTP_CLIENT_PROVIDER=this;
      httpClient = initHttpClient(application);
      // Create a local instance of cookie store
      final CookieStore cookieStore = new BasicCookieStore();
      Cookie cookie = JsonClient.getCookie(application);
      if (cookie != null) {
         Log.i(LOG, "Persistent cookie found");
         cookieStore.addCookie(cookie);
      }else{
         Log.i(LOG, "No persistent cookie found");
      }
      // Create local HTTP context
      localContext = new BasicHttpContext();
      // Bind custom cookie store to the local context
      localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

   }

   
   /**
    * @return the localContext
    */
   public HttpContext getLocalContext() {
      return localContext;
   }
   
   private String getUserAgent(String defaultHttpClientUserAgent,
         final Application application) {
      String versionName;
      try {
         versionName = application.getPackageManager().getPackageInfo(
               application.getPackageName(), 0).versionName;
      } catch (NameNotFoundException e) {
         throw new RuntimeException(e);
      }
      StringBuilder ret = new StringBuilder();
      ret.append(application.getPackageName());
      ret.append("/");
      ret.append(versionName);
      ret.append(" (");
      ret.append("Linux; U; Android ");
      ret.append(Build.VERSION.RELEASE);
      ret.append("; ");
      ret.append(Locale.getDefault());
      ret.append("; ");
      ret.append(Build.PRODUCT);
      ret.append(")");
      if (defaultHttpClientUserAgent != null) {
         ret.append(" ");
         ret.append(defaultHttpClientUserAgent);
      }
      return ret.toString();
   }

   
   public synchronized static HttpClientProvider getInstance(
         final Application application) {
      if (HTTP_CLIENT_PROVIDER == null) {
         HTTP_CLIENT_PROVIDER = new HttpClientProvider(application);
      }
      return HTTP_CLIENT_PROVIDER;
   }

   
   /**
    * @return the httpClient
    */
   public HttpClient getHttpClient() {
      return httpClient;
   }
// Wait this many milliseconds max for the TCP connection to be established
   private static final int CONNECTION_TIMEOUT = 60 * 1000;

// Wait this many milliseconds max for the server to send us data once the
// connection has been established
   private static final int SO_TIMEOUT         = 5 * 60 * 1000;
   
   /**
    * @return the httpClient
    */
   public AbstractHttpClient getDefalutHTTPClient() {
      return httpClient;
   }

   private AbstractHttpClient initHttpClient(final Application application) {
      final HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
      
      DefaultHttpClient  httpClient = new DefaultHttpClient() {

         @Override
         protected ClientConnectionManager createClientConnectionManager() {
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                  .getSocketFactory(), 80));
            SocketFactory socketFactory = getHttpsSocketFactory();
           
            ((SSLSocketFactory) socketFactory).setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry
                  .register(new Scheme("https", socketFactory, 443));
            HttpParams params = getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                  CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
           
            HttpProtocolParams.setUserAgent(
                  params,
                  getUserAgent(HttpProtocolParams.getUserAgent(params),
                        application));
            
            return new ThreadSafeClientConnManager(params, registry);
         }
         private MySSLSocketFactory getNoCertificateValidationSocketFactory() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException{
            final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
           final MySSLSocketFactory mySSLSocketFactory = new MySSLSocketFactory(trustStore);
            return mySSLSocketFactory;
         }
       
         /**
          * Gets an HTTPS socket factory with SSL Session Caching if such
          * support is available, otherwise falls back to a non-caching factory
          * 
          * @return
          */
         protected SocketFactory getHttpsSocketFactory() {
            try {
              
             
               //http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https/6378872#6378872
               
              final MySSLSocketFactory socketFactory = getNoCertificateValidationSocketFactory();
               
                /*
                Class<?> sslSessionCacheClass = Class
                     .forName("android.net.SSLSessionCache");
               Object sslSessionCache = sslSessionCacheClass.getConstructor(
                     Context.class).newInstance(application);
                      
              Class<?> sslCertificateFactory=Class.forName(
                    "android.net.SSLCertificateSocketFactory");
               Method getHttpSocketFactory = sslCertificateFactory.getMethod(
                     "getHttpSocketFactory",
                     new Class[] { int.class, sslSessionCacheClass });
                  if(Build.VERSION.SDK_INT >= 14){
                  Method setTrustManager = sslCertificateFactory.getMethod(
                        "setTrustManagers", new Class[] { TrustManager.class });
                  TrustManager[] myTrustManagerArray = new TrustManager[] { new TrustEveryoneManager() };
                  // http://developer.android.com/training/articles/security-ssl.html#HttpsExample
                  setTrustManager.invoke(null, (Object) myTrustManagerArray);
               
                  }
               final SocketFactory sf=  (SocketFactory) getHttpSocketFactory.invoke(null,
                     CONNECTION_TIMEOUT, sslSessionCache);
               
               */
               return socketFactory ;
            } catch (Exception e) {
               Log.e("ACDC",
                     "Unable to use android.net.SSLCertificateSocketFactory to get a SSL session caching socket factory, falling back to a non-caching socket factory",
                     e);
               return SSLSocketFactory.getSocketFactory();
            }

         }
      };
      
      // Set verifier     
//    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
      return httpClient;
   }

public void clearCookie(){
   if(httpClient != null){
      httpClient.getCookieStore().clear();
   }
}
   private static final String SHUTDOWN_THREAD="shutdownThread";
   public void clear(){
      if(!Utils.isThreadRunning(SHUTDOWN_THREAD)){
      final Thread thread =  new Thread(){
        public void run(){
           if(httpClient != null){
              httpClient.getCookieStore().clear();
              httpClient.getConnectionManager().shutdown();   
           }
        }
      };
      thread.setName(SHUTDOWN_THREAD);
      thread.start();
      }
      
      clearInstance();
    
   }
   private  static void clearInstance(){
      HTTP_CLIENT_PROVIDER=null;
   }
}