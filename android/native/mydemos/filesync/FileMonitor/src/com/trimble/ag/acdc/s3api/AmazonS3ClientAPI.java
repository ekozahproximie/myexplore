/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.acdc.s3api
 *
 * File name: AmazonS3ClientAPI.java
 *
 * Author: sprabhu
 *
 * Created On: Nov 3, 20142:12:23 PM
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.acdc.s3api;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.trimble.ag.acdc.ACDCApi;
import com.trimble.ag.acdc.file.FileUploadedNotificationRequest;
import com.trimble.ag.acdc.file.FileUploadedNotificationResponse;
import com.trimble.ag.acdc.s3.StorageCredentialsResponse;
import com.trimble.ag.acdc.s3.StorageKeyRequest;
import com.trimble.ag.acdc.s3.StorageKeyResponse;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * @author sprabhu
 *
 */
public final class AmazonS3ClientAPI {

   private static AmazonS3ClientAPI amazonS3ClientAPI = null;

   private AmazonS3Client           mS3Client;
   private AWSCredentials           mCredProvider;
   private TransferManager          mTransferManager;
   private ACDCApi                  acdcApi           = null;

   /**
    * 
    */
   private AmazonS3ClientAPI(final Context context) {
      if (amazonS3ClientAPI != null) {
         throw new IllegalAccessError("use getInstance");
      }
      acdcApi = ACDCApi.getInstance(context);
   }

   public static synchronized AmazonS3ClientAPI getInstance(
         final Context context) {
      if (amazonS3ClientAPI == null) {
         amazonS3ClientAPI = new AmazonS3ClientAPI(context);

      }
      return amazonS3ClientAPI;
   }

   public void clearAWSCredentials() {
      mCredProvider = null;
      mS3Client = null;
      mTransferManager = null;
   }

   private AWSCredentials getCredProvider(final String stAWS_Access_Key,
         final String stAWS_Secret_Key, final String stSession_Token) {
      if (mCredProvider == null) {
         mCredProvider = new BasicSessionCredentials(stAWS_Access_Key,
               stAWS_Secret_Key, stSession_Token);
      }
      return mCredProvider;
   }

   private AmazonS3Client getS3Client(final String stAWS_Access_Key,
         final String stAWS_Secret_Key, final String stSession_Token) {
      if (mS3Client == null) {
         mS3Client = new AmazonS3Client(getCredProvider(stAWS_Access_Key,
               stAWS_Secret_Key, stSession_Token));
      }
      return mS3Client;
   }

   public boolean doesBucketExist(final String stBucket_Name,
         final AmazonS3Client mS3Client) {
      if (stBucket_Name == null || mS3Client == null) {
         return false;
      }
      return mS3Client.doesBucketExist(stBucket_Name.toLowerCase(Locale.US));
   }

   public TransferManager getTransferManager(final String stAWS_Access_Key,
         final String stAWS_Secret_Key, final String stSession_Token) {
      if (mTransferManager == null) {
         mTransferManager = new TransferManager(getCredProvider(
               stAWS_Access_Key, stAWS_Secret_Key, stSession_Token));
      }
      return mTransferManager;
   }

   public UploadResult doUpload(final Context context, final String stFile,
         final String stSource) throws UnknownHostException, IOException,
         AmazonServiceException, AmazonClientException, InterruptedException {
      UploadResult uploadResult = null;
      final File file = new File(stFile);
      final String stFileName = file.getName();
      final Uri uri = Uri.fromFile(file);
      final String stEnvironment = acdcApi.getDomainName();
      final StorageKeyRequest keyRequest = new StorageKeyRequest(stEnvironment,
            stSource, stFileName);
      if (acdcApi.getTicket() == null) {
         acdcApi.login();
      }

      if (acdcApi.getTicket() != null) {
         final StorageKeyResponse storageKeyResponse = acdcApi
               .getStorageKey(keyRequest);
         if (storageKeyResponse.isSuccess()) {
            final String stBucketName = storageKeyResponse.getBucketname();
            final StorageCredentialsResponse storageCredentialsResponse = storageKeyResponse
                  .getStorageCredentialsResponse();
            if (storageCredentialsResponse != null) {
               final AmazonS3Client amazonS3Client = getS3Client(
                     storageCredentialsResponse.getAWSAccessKey(),
                     storageCredentialsResponse.getAWSSecretAccessKey(),
                     storageCredentialsResponse.getAWSSessionToken());
               final boolean isBucketExists = amazonS3Client
                     .doesBucketExist(stBucketName);
               if (isBucketExists) {
                  getTransferManager( storageCredentialsResponse.getAWSAccessKey(),
                        storageCredentialsResponse.getAWSSecretAccessKey(),
                        storageCredentialsResponse.getAWSSessionToken()) ;
                  final UploadModel uploadModel = upload(context, uri,
                        stBucketName,
                        storageCredentialsResponse.getAWSAccessKey());
                  final Upload upload = uploadModel.upload();
                  uploadResult = upload.waitForUploadResult();
                  if (upload.isDone()
                        && upload.getState() == com.amazonaws.mobileconnectors.s3.transfermanager.Transfer.TransferState.Completed) {
                     final FileUploadedNotificationRequest fileUploadedNotificationRequest = new FileUploadedNotificationRequest(
                           storageKeyResponse.getFileId(), stFileName,
                           storageKeyResponse.getStorageKey(), stSource);
                     final FileUploadedNotificationResponse fileUploadedNotificationResponse = acdcApi
                           .doFileUploadedNotification(fileUploadedNotificationRequest);

                  }
               }
            }
         }
      }

      return uploadResult;
   }

   /* We use a new thread for upload because we have to copy the file */
   private UploadModel upload(final Context context, Uri uri,
         final String stBucketName, final String stAWSAccessKeyId) {
     
      if (mTransferManager == null) {
         return null;
      }
      final UploadModel model = new UploadModel(context, uri, mTransferManager,
            stBucketName, stAWSAccessKeyId);
      return model;

   }

}
