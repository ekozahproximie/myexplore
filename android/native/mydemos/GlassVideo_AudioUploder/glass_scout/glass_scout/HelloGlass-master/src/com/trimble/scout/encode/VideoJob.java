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
 *      com.trimble.scout.encode
 *
 * File name:
 *	    VideoJob.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Jun 14, 20147:05:38 PM
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
package com.trimble.scout.encode;

import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.AgJob;
import com.trimble.agmantra.entity.AttributeEntity;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.entity.JobTransaction;
import com.trimble.agmantra.job.JobListener;
import com.trimble.agmantra.job.JobRecorder;
import com.trimble.agmantra.jobsync.JobSyncService;
import com.trimble.agmantra.layers.BoundingBox;
import com.trimble.agmantra.layers.FGPPoint;
import com.trimble.agmantra.layers.GSObjectLayer;
import com.trimble.agmantra.layers.GeoPoint;
import com.trimble.agmantra.utils.Mercator;

import android.content.Context;
import android.location.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * @author sprabhu
 *
 */
public class VideoJob {

   /**
    * 
    */
   
   private File fileToUpload;
   private Location location;
   private JobRecorder                        mJobRecInstance = null;
   
   public static final int                    DEFAULT_JOB_ID  = -1;
   
   //public static final String IP_ADDRESS="10.40.76.103";
   
   public static final String IP_ADDRESS="locust.trimble.com";
   
   
   
   //private static final String VIDEO_UPLOADED_URL="Video:http://%s:8080/TpassFileServer/FileServlet/%s";
   
   //private static final String AUDIO_UPLOADED_URL="Audio:http://%s:8080/TpassFileServer/FileServlet/%s";
   
   private static final String VIDEO_UPLOADED_URL="Video:http://%s/~connectedfarm/scout_glass/%s";
   
   private static final String AUDIO_UPLOADED_URL="Audio:http://%s/~connectedfarm/scout_glass/%s";

   
   private boolean isVideo=false;
   
   private transient FarmWorksContentProvider contentProvider = null;
   private Context context =null;
   public VideoJob(final File fileToUpload,final Location location,final Context context,
         boolean isVideo) {
     this.fileToUpload =fileToUpload;
     this.location =location;
     this.context=context;
     this.isVideo=isVideo;
     contentProvider=FarmWorksContentProvider.getInstance(context);
   }
   
   
   /**
    * @return the fileToUpload
    */
   public File getFileToUpload() {
      return fileToUpload;
   }
   
   
   /**
    * @return the location
    */
   public Location getLocation() {
      return location;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
    
      return "FN:"+fileToUpload.getName()+",length:"+fileToUpload.length();
   }
   
   public void createJobSendFile(final Location userLocation) {
      final long lJobID = createJobID(AgDataStoreResources.JOB_TYPE_MAP_SCOUTING_NAME);
      startJob(lJobID);
      addPoint(userLocation, lJobID);
      confirmJobCompletion(lJobID);
   }

   public long createJobID(String stJobType) {
      AgJob mAgJob = contentProvider.getNewAgJob(stJobType);
      final long lJobID = mAgJob.getJobId();
      // set current location for auto detect field
      long lFieldID = setFieldInfo(null);
      setFieldIdForJob(lJobID, lFieldID);
      if (mAgJob != null) {
         Field field = mAgJob.getField();
         if (field != null) {

         } else {
            field = contentProvider
                  .getFieldByFieldId(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID);
            if (field != null) {
               mAgJob.setFieldId(field.getId());
               mAgJob.setField(field);
               contentProvider.updateAgjobInfo(lJobID, mAgJob);

            }
         }

      }
      return lJobID;
   }

   private void startJob(long lJobID) {
      if (lJobID == DEFAULT_JOB_ID) {
         return;
      }

      mJobRecInstance = new JobRecorder(lJobID, contentProvider);
      final int iAutoCloseDistance = 0;
      boolean isAutoCloseEnabled = false;
      mJobRecInstance.setJobRelatedValues(new JobListener() {

         @Override
         public void sendMapUpdate(long iFeatureId, int eObjectType,
               GeoPoint mPoint, boolean isFinished, boolean isOffsetPresent) {

         }

         @Override
         public void removeFeature(long iFeatureId, int eObjectType) {

         }

         @Override
         public void initAllLayers(Vector<GSObjectLayer> vecOverlayLayers) {

         }

         @Override
         public void featureIncompletePreviously(long id) {

         }

         @Override
         public void autoCloseFeature() {

         }
      }, isAutoCloseEnabled, iAutoCloseDistance);
      mJobRecInstance.startTask();

   }

   private void confirmJobCompletion(final long lJobID) {
      if (lJobID != DEFAULT_JOB_ID) {
         if (true) {
            boolean isCurrRecValid = mJobRecInstance.isFeatureValid();
            boolean isCorrectionRequired = mJobRecInstance
                  .isFeatureCorrectionReq();

            boolean isTaskValid = mJobRecInstance.isTaskValid();
            boolean isFeatureValid = mJobRecInstance.stopFeatureRecording(-1,
                  isCorrectionRequired);

            if (false == isTaskValid) {
               // confirmCancelJob(Constants.Dialog.ALERT_USER_FOR_CANCEL_TASK);
               // deleteJob();
               // showAppExitDialog();
            } else {
               completeRecordingJob(lJobID);
            }
         }
      }

   }

   private FGPPoint makeFlagFGPPoint(Location fixData) {
      if (fixData == null) {
         return null;
      }

      FGPPoint fgpPoint = new FGPPoint();
      fgpPoint.iX = (int) Mercator.lonToX(fixData.getLongitude());
      fgpPoint.iY = (int) Mercator.latToY(fixData.getLatitude());
      fgpPoint.iSpeed = (int) fixData.getSpeed();
      fgpPoint.iHeading = (int) fixData.getBearing();
      fgpPoint.iQuality = 1;
      fgpPoint.iBooms = -1;
      fgpPoint.iTime = (int) (fixData.getTime() / 1000);
      fgpPoint.iTime_ms = (int) (fixData.getTime() % 1000);
      fgpPoint.iOffset = 0;
      return fgpPoint;
   }

   private void addPoint(Location fixData, final long lJobID) {
      long lLogFeatureID = mJobRecInstance
            .addLoggedPoint(makeFlagFGPPoint(fixData));
      final long lTemplateID = 5;
      ArrayList<AttributeEntity> attList = new ArrayList<AttributeEntity>(3);
      final long iAttributeID[] = { 30, 31, 32 };
      final String stFlagName = "OT - "
            + getFlagCountByTemplateID(lLogFeatureID, lTemplateID);
      // final String stVideoURL="VIT test video MyGlass video";
      final String stFilePath=fileToUpload.getName();
     final String stDataURL=String.format(isVideo ?
           VIDEO_UPLOADED_URL:AUDIO_UPLOADED_URL, IP_ADDRESS,stFilePath);
      int i = 0;
      for (i = 0; i < 3; i++) {
         AttributeEntity attribute = new AttributeEntity();
         // set feature ID
         attribute.setFeatureId(lLogFeatureID);

         // set attribute info ID
         attribute.setAttributeInfoId(iAttributeID[i]);

         // set attribute value
         if (iAttributeID[i] == 30) {
            attribute.setValue(stFlagName);

         } else if (iAttributeID[i] == 32) {
            attribute.setValue(stDataURL);
         } else {
            // if value is not set, pass attribute's default value
            attribute.setValue("");
         }

         // add to attribute list
         attList.add(attribute);
      }

      contentProvider.insertAttribute(attList, lJobID, lTemplateID);
      updateTemplateTypeInDB(lTemplateID, lLogFeatureID, lJobID);
   }

   public void updateTemplateTypeInDB(final long lTemplateID,
         final long lFeatureID, final long lJobID) {
      // create a dummy 'JobTransaction' entity
      JobTransaction jobTxn = new JobTransaction();
      jobTxn.setJobId(lJobID);
      jobTxn.setTemplateTypeId(lTemplateID);
      jobTxn.setFeatureId(lFeatureID);
      contentProvider.updateJobTxn(jobTxn, true);
   }

   public long getFlagCountByTemplateID(long lJobID, long lTemplateID) {
      // query DB to get the current running number in attrib value
      // table
      if (contentProvider == null) {
         return 1;
      }
      long lRunningNumber = contentProvider.getTemplateCounterForJob(lJobID,
            lTemplateID);
      if (-1 == lRunningNumber)
         lRunningNumber = 1; // as no records are currently present for this
                             // template type in DB
      else
         lRunningNumber++; // as the flag counter returned is the
                           // no.of.records currently present in DB

      return lRunningNumber;
   }

   private void completeRecordingJob(final long lJobID) {
      boolean isJobValid_complete = mJobRecInstance.completeTask();
      if (isJobValid_complete) {

         boolean isSuccess = JobSyncService.startJobSyncService(lJobID, context);

      }

   }

   private long setFieldInfo(Location position) {
      // Code for selecting the current field based on current
      // location.
      long lFieldId = -1;
      if (position != null && (int) position.getLatitude() != 0
            && (int) position.getLongitude() != 0) {
         lFieldId = setFieldIdFrmCurrLoc(
               (int) Mercator.lonToX(position.getLongitude()),
               (int) Mercator.latToY(position.getLatitude()));
      }
      return lFieldId;
   }

   private void setFieldIdForJob(long lJobID, long lFieldId) {
      if (lJobID != -1) {
         if (-1 != lFieldId) {
            AgJob mAgJob = contentProvider.getAgjobByJobId(lJobID);
            if (null != mAgJob) {
               mAgJob.setFieldId(lFieldId);
               contentProvider.updateAgjobInfo(lJobID, mAgJob);
               lFieldId = -1;
            }
         }
      }
   }

   /**
    * This method is a utility function, to retrieve the Field Id based on the
    * user's current location by communicating to the data base.
    * 
    * @param mCurrLoc
    *           the m curr loc
    * @return none
    */

   private long setFieldIdFrmCurrLoc(int iX, int iY) {
      long lFieldId = -1;
      List<com.trimble.agmantra.entity.Field> lFieldsList = contentProvider
            .getAllFieldsList();
      if ((null != lFieldsList) && (lFieldsList.size() > 0)) {
         for (com.trimble.agmantra.entity.Field field : lFieldsList) {
            if (null != field.getBottomRightX()) {
               int right = field.getBottomRightX();
               int bottom = field.getBottomRightY();
               int left = field.getTopLeftX();
               int top = field.getTopLeftY();

               BoundingBox mRect = new BoundingBox(left, bottom, right, top);
               if (mRect.overlap(iX, iY)) {
                  lFieldId = field.getId();
                  break;
               }
            }
         }
      }
      return lFieldId;
   }

   
}
