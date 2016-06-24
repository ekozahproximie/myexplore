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
 *      com.trimble.scout.net
 *
 * File name:
 *	    MultipartUtility.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Jun 14, 20146:36:58 PM
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
package com.trimble.scout.net;

import android.util.Log;

import com.trimble.scout.encode.VideoJob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * @author sprabhu
 *
 */
/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * @author www.codejava.net
 *
 */
public class MultipartUtility {
    /**
    * 
    */
   private static final int MAX_BUFFER_SIZE = 1024*1;
   private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
 
    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public MultipartUtility(String requestURL, String charset)
            throws IOException {
        this.charset = charset;
         
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
         
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
      //  httpConn.setChunkedStreamingMode(MAX_BUFFER_SIZE);
        httpConn.setRequestProperty("Connection", "Keep-Alive");
                
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        httpConn.setRequestProperty("Test", "Bonjour");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }
 
    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }
 
    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        
        long bytesAvailable = inputStream.available();
        
        long bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
        
        byte[] buffer = new byte[(int)bufferSize];
        int bytesRead = -1;
        
        bytesRead = inputStream.read(buffer);
        while (bytesRead > 0) {
           
               outputStream.write(buffer, 0, (int)bufferSize);
           
           bytesAvailable = inputStream.available();
           bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
           bytesRead = inputStream.read(buffer, 0, (int)bufferSize);
       }
        
       
        outputStream.flush();
        inputStream.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
    }
 
    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }
     
    /**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();
 
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
 
        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
 
        return response;
    }
    private static final String LOG="test";
    public static  boolean main(final VideoJob videoJob) {
       String charset = "UTF-8";
       boolean isUploaded=false;
       final File uploadFile = videoJob.getFileToUpload();
       if(! uploadFile.exists()){
          Log.e(LOG, "file not exist:"+uploadFile);
          return isUploaded;
       }
       String requestURL = "http://"+VideoJob.IP_ADDRESS+":8080/TpassFileServer/FileUploadServlet";

       try {
           MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            
           multipart.addHeaderField("User-Agent", "AndroidDevice");
           multipart.addHeaderField("Test-Header", "Header-Value");
            
           multipart.addFormField("description", "Cool Pictures");
           multipart.addFormField("keywords", "Java,upload");
            
           multipart.addFilePart("fileUpload", uploadFile);
     

           List<String> response = multipart.finish();
            
           Log.d(LOG,"SERVER REPLIED:");
            
           for (String line : response) {
               Log.d(LOG, line);
           }
           isUploaded=true;
       } catch (IOException ex) {
          Log.e(LOG, ex.getMessage(),ex);
           
       }
       return isUploaded;
   }
}
