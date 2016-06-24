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
 *	    SFTPUtility.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Aug 19, 201412:00:27 AM
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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.trimble.scout.encode.VideoJob;

import java.io.File;


/**
 * @author sprabhu
 *
 */
public class SFTPUtility {
   private static final String LOG=SFTPUtility.class.getSimpleName();
   
 //  private static final String USERNAME="agcftrimble";
   
  // private static final String PASSWORD="ag#2014";
   
   private static final String USERNAME="connectedfarm";
   
   private static final String PASSWORD="AgM@ntr@";
   
   private static final String HOST="locust.trimble.com";
   
   private static final int PORT=22;

   /**
    * 
    */
   public SFTPUtility() {
      // TODO Auto-generated constructor stub
   }
   public static  boolean main(final VideoJob videoJob) {
     
      boolean isUploaded=false;
      final File uploadFile = videoJob.getFileToUpload();
      if(! uploadFile.exists()){
         Log.e(LOG, "file not exist:"+uploadFile);
         return isUploaded;
      }
      Session session = null;
      Channel channel = null;
      try {
          JSch ssh = new JSch();
          //ssh.setKnownHosts("/home/connectedfarm/public_html/scout_glass/log.txt");
          session = ssh.getSession(USERNAME, HOST, PORT);
          session.setConfig("StrictHostKeyChecking", "no");
          session.setPassword(PASSWORD);
          session.connect();
          channel = session.openChannel("sftp");
          channel.connect();
          ChannelSftp sftp = (ChannelSftp) channel;
          sftp.put(uploadFile.getAbsolutePath(), "/home/connectedfarm/public_html/scout_glass/");
         // sftp.put(uploadFile.getAbsolutePath(), "/home/connectedfarm/public_html/scout_glass/"+uploadFile.getName());
          isUploaded=true;
      } catch (JSchException ex) {
         Log.e(LOG, ex.getMessage(),ex);
      } catch (SftpException ex) {
         Log.e(LOG, ex.getMessage(),ex);
      } finally {
          if (channel != null) {
              channel.disconnect();
          }
          if (session != null) {
              session.disconnect();
          }
      }
      return isUploaded;
  }
}
