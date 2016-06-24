
package com.spime.client;

import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class TestSSLClientActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            // System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
            // System.setProperty("javax.net.ssl.keyStorePassword", "123456");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("JKS");

            InputStream keyInput = new FileInputStream(
                    "E:\\Java\\MyServer\\build\\classes\\mySrvKeystore");
            keyStore.load(keyInput, "123456".toCharArray());
            keyInput.close();

            keyManagerFactory.init(keyStore, "123456".toCharArray());

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null
            , new SecureRandom());
            SSLSocketFactory sslsocketfactory = context.getSocketFactory();
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket("supl.google.com", 7275);
            String[] suites = sslsocket.getSupportedCipherSuites();
            sslsocket.setEnabledCipherSuites(suites);

            sslsocket.setUseClientMode(true);
            sslsocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {

                public void handshakeCompleted(HandshakeCompletedEvent hce) {
                    Certificate[] certificates = null;
                    try {
                        certificates = (Certificate[]) hce.getPeerCertificates();
                    } catch (SSLPeerUnverifiedException ex) {
                        ex.printStackTrace();
                    }

                    for (Certificate certificate : certificates) {
                        System.out.println(certificate);
                    }

                }
            });
            sslsocket.setNeedClientAuth(true);
            sslsocket.setEnableSessionCreation(true);
            sslsocket.startHandshake();
            PrintWriter output = new PrintWriter(
                    new OutputStreamWriter(sslsocket.getOutputStream()));
            String userName = "hello";
            output.println(userName);
            String password = "123";
            output.println(password);
            output.flush();
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    sslsocket.getInputStream()));
            String response = input.readLine();
            System.out.println(response);

            InputStream inputStream = System.in;
            inputStream.read();
            output.close();
            input.close();
            sslsocket.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
