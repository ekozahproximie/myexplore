package com.trimble.agmantra.jobencoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.AgJob;
import com.trimble.agmantra.entity.AttributeEntity;
import com.trimble.agmantra.entity.AttributeInfoEntity;
import com.trimble.agmantra.entity.Client;
import com.trimble.agmantra.entity.Commodity;
import com.trimble.agmantra.entity.Crop;
import com.trimble.agmantra.entity.Farm;
import com.trimble.agmantra.entity.Feature;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.entity.Job;
import com.trimble.agmantra.entity.JobTransaction;
import com.trimble.agmantra.entity.People;
import com.trimble.agmantra.entity.PickList;
import com.trimble.agmantra.entity.Units;
import com.trimble.agmantra.filecodec.AgTxtCodec;
import com.trimble.agmantra.filecodec.JobGpeAttribs;
import com.trimble.agmantra.filecodec.ShapeGpeAttribs;
import com.trimble.agmantra.filecodec.fdt.AttributesInfo;
import com.trimble.agmantra.filecodec.fdt.CropCondition;
import com.trimble.agmantra.filecodec.fdt.Disease;
import com.trimble.agmantra.filecodec.fdt.FDTHeader;
import com.trimble.agmantra.filecodec.fdt.FDTWrapper;
import com.trimble.agmantra.filecodec.fdt.Insect;
import com.trimble.agmantra.filecodec.fdt.NDVI;
import com.trimble.agmantra.filecodec.fdt.NDVIRef;
import com.trimble.agmantra.filecodec.fdt.Other;
import com.trimble.agmantra.filecodec.fdt.Photo;
import com.trimble.agmantra.filecodec.fdt.Weed;
import com.trimble.agmantra.filecodec.fgp.FGPCodec;
import com.trimble.agmantra.filecodec.fop.FOPEncoder;
import com.trimble.agmantra.filecodec.fop.OpListValues;
import com.trimble.agmantra.filecodec.fop.Unit;
import com.trimble.agmantra.filecodec.shp.ShpFileEncoder;
import com.trimble.agmantra.filecodec.xml.FieldInfo;
import com.trimble.agmantra.filecodec.xml.FieldInfoValues;
import com.trimble.agmantra.layers.BoundingBox;
import com.trimble.agmantra.layers.FGPPoint;
import com.trimble.agmantra.logger.Log;
import com.trimble.agmantra.utils.Utils;

@SuppressLint("UseSparseArrays")
public class JobEncoder {

   private static JobEncoder        jobEncoder = null;
   private FarmWorksContentProvider dbMangr    = null;

   private Map<Integer, String>     mapPath    = null;

   private String                   stJobTime  = null;

   private AgJob                    agJob      = null;

   private static final String      TAG        = "JobEncoder";
   public String                    stProjName = null;

   private long                     iProjId    = 0;

   private Context                  context    = null;

   private boolean                  isLogger   = true;

   private JobEncoder(Context context) {
      this.context = context;
      dbMangr = FarmWorksContentProvider.getInstance(context);
      mapPath = new HashMap<Integer, String>();

   }

   /**
    * 
    * @return
    */
   public static synchronized JobEncoder getInstance(Context context) {

      if (jobEncoder == null) {
         jobEncoder = new JobEncoder(context);
      }
      return jobEncoder;
   }

   /**
    * 
    * @param lJobId
    */
   public boolean generateJobFiles(long lJobId,String[] stStatus)  {

      boolean isWrite = false;
		boolean isWriteShpFile = false;
      synchronized (dbMangr) {
         agJob = dbMangr.getAgjobByJobId(lJobId);
         setJobTime();
      }

      //dbMangr.updateProjectId(678381912);

      iProjId = dbMangr.getProjectId();

      stProjName = Utils.getNoPrefixHexaStringFromLong(iProjId)
            + Constants.PROJECT_FILE_EXTENS;

      if (agJob == null) {

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
               "agJob is invalid or null");
         return isWrite;
      }

      int iJobType = 0;

      if (agJob.getJobTypeId() != null) {
         // Commenting since it has been moved before scheduling the thread.
    	  //com.trimble.agmantra.entity.Job mJob = agJob.getJob();
    	  //mJob.setStatus(AgDataStoreResources.JOB_STATUS_ENCODING);

    	  //dbMangr.updateJob(mJob);

         iJobType = Integer.valueOf(agJob.getJobTypeId().toString());
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "JobId-" + iJobType);
      }

      long lFieldId = 0;

      if (agJob.getFieldId() != null) {
         lFieldId = Long.valueOf(agJob.getFieldId().toString());
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "FieldId-" + lFieldId);
      }

      switch (iJobType) {

         case AgDataStoreResources.JOB_TYPE_BOUNDARY_MAPPING:

            // Update Bounding box
            updateFiledBoundingBox(agJob.getField());

            // Update Field area
            updateFiledArea(agJob.getField());

            File delDir=null;
            String stShpFileName=null;
            try {
               delDir = new File(Utils.getBoundaryJobLoc(stProjName));
               
               Utils.deleteJobFileDir(delDir);

               stShpFileName = Utils.getBoundaryJobFileLoc(lJobId,
                     getJobTime(), stProjName,dbMangr);
               
               if(stShpFileName==null){
                  return false;
               }
            } catch (FileNotFoundException e) {
               
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               
               e.printStackTrace();
               if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
                  Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - generateJobFiles" );
                  break;
               }
            }

           
	
			boolean isShpFileReq = false;
			
			List<JobTransaction> mJobTxn = dbMangr.getFeatureInfoByTTId(
					lJobId, -1);
			if (null != mJobTxn) {
				if (mJobTxn.size() > 0) {
					isShpFileReq = true;
				}
			}

			if (true == isShpFileReq) {
				isWriteShpFile = createShapeFile(stShpFileName, lJobId,
						lFieldId,stStatus);
				Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
						"Boundary File Creation Result-" + isWriteShpFile);
			} else {
				Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
						"Boundary File Creation is not required");
			}
			// Todo print log here , if shape file is not generated.	
			isWrite = createJobFilesByTemplates(lJobId, lFieldId,stStatus);

			Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
					"Boundary File (Scout Job) Creation Result-" + isWrite);

            break;
            
         case AgDataStoreResources.JOB_TYPE_PHOTO:   
         case AgDataStoreResources.JOB_TYPE_MAP_SCOUTING:

            isWrite = createJobFilesByTemplates(lJobId, lFieldId,stStatus);

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "Job File Creation Result-" + isWrite);

            break;

         default:

            break;
      }

		return (isWrite || isWriteShpFile);
   }

   /**
    * 
    * Create Job files based on the template types
    * 
    * @param lJobId
    * @param lFieldID
    * @return
    */
   private boolean createJobFilesByTemplates(long lJobId, long lFieldID,String[] stStatus) {

      boolean isWrite = false;

      String stJobFileName = "";

      File delDir =null;
      try {
         delDir = new File(Utils.getScoutJobLoc(stProjName));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
            if(Utils.isSDCardMount()){
               stStatus[0] = Constants.SDCARD_NO_SPACE;
            }
            Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
            return false;
         }
         
      }

      Utils.deleteJobFileDir(delDir);

      List<Feature> features = getFeaturesByTemplate(lJobId,
            AgDataStoreResources.ATT_TYPE_WEEDS);

      if (features != null && features.size() > 0) {
         try {
            stJobFileName = Utils.getScoutJobFileLoc(lJobId,
                  AgDataStoreResources.ATT_TYPE_WEEDS, getJobTime(), stProjName);
            if(stJobFileName==null){
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"Sdcard Mounted - createJobFilesByTemplates" );
               return false;
               
            }
         } catch (FileNotFoundException e) {
            
            if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
               return false;
            }
            e.printStackTrace();
         }
         isWrite = createAllJobFiles(stJobFileName, features, lJobId, lFieldID,
               AgDataStoreResources.ATT_TYPE_WEEDS,stStatus);

         features = null;

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "Weed Job files -"
               + isWrite);
      }

      stJobFileName = "";

      features = getFeaturesByTemplate(lJobId,
            AgDataStoreResources.ATT_TYPE_INSECTS);

      if (features != null && features.size() > 0) {

         try {
            stJobFileName = Utils.getScoutJobFileLoc(lJobId,
                  AgDataStoreResources.ATT_TYPE_INSECTS, getJobTime(), stProjName);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            
            if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
               return false;
            }
         }

         isWrite = createAllJobFiles(stJobFileName, features, lJobId, lFieldID,
               AgDataStoreResources.ATT_TYPE_INSECTS,stStatus);

         features = null;

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "Insects Job files -"
               + isWrite);

      }
      stJobFileName = "";
      features = getFeaturesByTemplate(lJobId,
            AgDataStoreResources.ATT_TYPE_DISEASE);

      if (features != null && features.size() > 0) {
         try {
            stJobFileName = Utils.getScoutJobFileLoc(lJobId,
                  AgDataStoreResources.ATT_TYPE_DISEASE, getJobTime(), stProjName);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
               return false;
            }
         }

         isWrite = createAllJobFiles(stJobFileName, features, lJobId, lFieldID,
               AgDataStoreResources.ATT_TYPE_DISEASE,stStatus);

         features = null;

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "Disease Job files -"
               + isWrite);

      }
      stJobFileName = "";
      features = getFeaturesByTemplate(lJobId,
            AgDataStoreResources.ATT_TYPE_CROP_CONDITION);

      if (features != null && features.size() > 0) {
         try {
            stJobFileName = Utils.getScoutJobFileLoc(lJobId,
                  AgDataStoreResources.ATT_TYPE_CROP_CONDITION, getJobTime(),
                  stProjName);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
               return false;
            }
         }

         isWrite = createAllJobFiles(stJobFileName, features, lJobId, lFieldID,
               AgDataStoreResources.ATT_TYPE_CROP_CONDITION,stStatus);
         
         features = null;

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
               "Crop Condition Job files -" + isWrite);

      }
      stJobFileName = "";

      features = getFeaturesByTemplate(lJobId,
            AgDataStoreResources.ATT_TYPE_OTHERS);

      if (features != null && features.size() > 0) {

         try {
            stJobFileName = Utils.getScoutJobFileLoc(lJobId,
                  AgDataStoreResources.ATT_TYPE_OTHERS, getJobTime(), stProjName);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
               return false;
            }
         }

         isWrite = createAllJobFiles(stJobFileName, features, lJobId, lFieldID,
               AgDataStoreResources.ATT_TYPE_OTHERS,stStatus);

         features = null;

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "Others Job files -"
               + isWrite);

      }
      stJobFileName = "";

      features = getFeaturesByTemplate(lJobId,
            AgDataStoreResources.ATT_TYPE_NDVI);

      if (features != null && features.size() > 0) {

         try {
            stJobFileName = Utils.getScoutJobFileLoc(lJobId,
                  AgDataStoreResources.ATT_TYPE_NDVI, getJobTime(), stProjName);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
               return false;
            }
         }

         isWrite = createAllJobFiles(stJobFileName, features, lJobId, lFieldID,
               AgDataStoreResources.ATT_TYPE_NDVI,stStatus);

         features = null;

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "NDVI Job files -"
               + isWrite);

      }
      
      stJobFileName = "";

      features = getFeaturesByTemplate(lJobId,
            AgDataStoreResources.ATT_TYPE_NDVI_REF);

      if (features != null && features.size() > 0) {

         try {
            stJobFileName = Utils.getScoutJobFileLoc(lJobId,
                  AgDataStoreResources.ATT_TYPE_NDVI_REF, getJobTime(), stProjName);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
               return false;
            }
         }

         isWrite = createAllJobFiles(stJobFileName, features, lJobId, lFieldID,
               AgDataStoreResources.ATT_TYPE_NDVI_REF,stStatus);

         features = null;

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "NDVIRef Job files -"
               + isWrite);

      }
      stJobFileName = "";

      features = getFeaturesByTemplate(lJobId,
            AgDataStoreResources.ATT_TYPE_IMAGE);

      if (features != null && features.size() > 0) {

         try {
            stJobFileName = Utils.getScoutJobFileLoc(lJobId,
                  AgDataStoreResources.ATT_TYPE_IMAGE, getJobTime(), stProjName);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
               if (Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createJobFilesByTemplates" );
               return false;
            }
         }

         isWrite = createAllJobFiles(stJobFileName, features, lJobId, lFieldID,
               AgDataStoreResources.ATT_TYPE_IMAGE,stStatus);
         
         features = null;

         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "Photo Job files -"
               + isWrite);

      }
      stJobFileName = "";
      return isWrite;
   }

   /**
    * 
    * Get all features by by template id
    * 
    * @param iTemplateId
    * @return
    */
   private List<Feature> getFeaturesByTemplate(long lJobId, int iTemplateId) {

      List<Feature> features = null;

      List<JobTransaction> jobTransList = dbMangr.getFeatureInfoByTTId(lJobId,
            iTemplateId);

      if (jobTransList != null && jobTransList.size() > 0) {

         features = new ArrayList<Feature>(jobTransList.size());

         for (JobTransaction jobTransaction : jobTransList) {

            features.add(jobTransaction.getFeature());
         }
      }
      return features;
   }

   /**
    * 
    * @param stFilePath
    * @param features
    * @param lJobId
    * @param lFieldID
    * @param iTemplateId
    */

   private boolean createAllJobFiles(String stFilePath, List<Feature> features,
         long lJobId, long lFieldID, int iTemplateId,String[] stStatus) {

      boolean isReturn = false;

      String folderName = null;
      try {
         folderName = Utils.getFieldScoutLoc(lJobId, iTemplateId, getJobTime(),
               stProjName);
         
         if(folderName==null){
            
            Log.printLog(isLogger, Log.LOG_TYPE_INFO,
                  Constants.TAG_JOB_ENCODER,
                  "Sdcard Mounted - createAllJobFiles");
            return false;
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         if (e.getMessage().equals(Utils.UNABLE_TO_CREATE)) {
            if(Utils.isSDCardMount()){
               stStatus[0] = Constants.SDCARD_NO_SPACE;
            }

            Log.printLog(isLogger, Log.LOG_TYPE_INFO,
                  Constants.TAG_JOB_ENCODER,
                  "No memory space in sdcard - createAllJobFiles");
            return false;
         }
      }
    
         mapPath.put(iTemplateId, folderName);
     
      isReturn = createFopFile(stFilePath.toString(),
            Constants.FGP_VERSION, lJobId, iTemplateId,stStatus);
      
      if(!isReturn){
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "FopFile files not created"
               + isReturn);
         return false;
      }


      isReturn =  createFGP(stFilePath.toString(), lJobId, features, iTemplateId,stStatus);
      
      if(!isReturn){
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "FGP files not created"
               + isReturn);
         return false;
      }

      isReturn = createFDTFile(stFilePath.toString(), features, iTemplateId,stStatus);
      
      if(!isReturn){
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "FDT files not created"
               + isReturn);
         return false;
      }
      
      String stDevFilePath=stFilePath.toString();
      stDevFilePath=stDevFilePath.substring(0,stDevFilePath.lastIndexOf(File.separator));
      
      Utils.dumpDevInfoTextFile(stDevFilePath, context);

      JobGpeAttribs jobGpeAttribs = new JobGpeAttribs(
            Constants.DEFAULT_VAL_JOB_BOOM_OFFSET,
            Constants.DEFAULT_VAL_JOB_BOOM);

      String stFileName = Utils.getScoutJobFilename(lJobId, iTemplateId,
            getJobTime(), true);
      
      if(stFileName==null){
         return false;
      }
      
      isReturn = createAgTxtFile(stFilePath.toString(), stFileName,
            Constants.GPE_FILE_EXTENS, AgTxtCodec.TYPE_JOB_GPE, jobGpeAttribs,stStatus);
      
      if(!isReturn){
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "AgTxt files not created"
               + isReturn);
         return false;
      }

      Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
            "All Job files created succesfully -" + isReturn);

      return isReturn;
   }

   /**
    * 
    * @param stFilePath
    * @param lJobID
    * @param features
    * @param iTemplateId
    * @return
    */
   private boolean createFGP(String stFilePath, long lJobID,
         List<Feature> features, int iTemplateId,String[] stStatus) {

      boolean isSuccess = false;

      StringBuilder stFullFilePath = new StringBuilder();
      stFullFilePath.append(stFilePath);
      stFullFilePath.append(Constants.FGP_FILE_EXTENS);

      Vector<FGPPoint> vecFgpPoint = getFeaturePointsList(lJobID, features,
            iTemplateId);

      FGPCodec fgpCodec = new FGPCodec(stFullFilePath.toString(), lJobID);
      fgpCodec.writeHeader();

      isSuccess = fgpCodec.addVertex(vecFgpPoint,stStatus);
      
      if(isSuccess)
      Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "FGP files created" + isSuccess);

      return isSuccess;

   }

   /**
    * 
    * @param stFilePath
    * @param fgpVertion
    * @param lJobId
    * @return
    */
   private boolean createFopFile(String stFilePath, int fgpVertion,
         long lJobId, long iTemplateId,String[] stStatus) {

      boolean isWrite = false;

      // AgJob agJob =
      // dbMangr.getNewAgJob(AgDataStoreResources.jobType_name[0]);

      StringBuilder stFullFilePath = new StringBuilder();
      stFullFilePath.append(stFilePath);
      stFullFilePath.append(Constants.FOP_FILE_EXTENS);

      AgJob agJob = dbMangr.getAgjobByJobId(lJobId);
     
      Client client  = null;
      Farm farm = null;
      Field field = null;
      Crop crop = null;
      Job job = null;
      Units units = null;
      
      if(agJob!=null){        
         job = agJob.getJob();
         field = agJob.getField();
         crop = agJob.getCrop();         
      }
      
      if (field != null) {
         
         farm = field.getFarm();
         units = field.getUnits();
         
         if (field.getDesc() != null
               && field.getDesc().equals(FarmWorksContentProvider.UNKNOWN)) {
           // field.setId(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID);
         }
      }
      
      if (crop != null) {
         if (crop.getDesc() != null
               && crop.getDesc().equals(FarmWorksContentProvider.UNKNOWN)) {

            //crop.setId(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID);
         }
      }
                 
      if (farm != null) {
         
         client = farm.getClient();
         
         if (farm.getDesc() != null
               && farm.getDesc().equals(FarmWorksContentProvider.UNKNOWN)) {
           // farm.setId(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID);
         }

      }   
            
      if (client != null) {
         if (client.getDesc() != null
               && client.getDesc().equals(FarmWorksContentProvider.UNKNOWN)) {

            //client.setId(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID);
         }
      }

      
      People people = new People();
      people.setId((long) 241);

      Commodity commodity = null;

      if (crop != null) {
         // commodity = crop.getCommodity();
      }

      // TODO - Verify the opwidth value      
		String stPrjName = Utils.getProjIdHexStringFromLong(iProjId);
            
      
      OpListValues oplist = new OpListValues(client, farm, field, commodity,
            crop, units, people, job, agJob, 0.0, (int) iTemplateId,stPrjName);

      FOPEncoder fopEncode = new FOPEncoder(stFullFilePath.toString(), lJobId,
            fgpVertion, oplist,dbMangr);

      isWrite = fopEncode.createFOPFile(stStatus);
      
      if(isWrite)
      Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "FGP files created -"
            + isWrite);

      return isWrite;

   }

   /**
    * 
    * @param stFilePath
    * @param features
    * @param iTempTypeID
    * @param lJobId
    */
   private boolean createFDTFile(String stFilePath, List<Feature> features,
         int iTempTypeID,String[] stStatus) {

      boolean isSuccess = false;

      StringBuilder stFullFilePath = new StringBuilder();
      stFullFilePath.append(stFilePath);
      stFullFilePath.append(Constants.FDT_FILE_EXTENS);

      Vector<AttributesInfo> vecHeaderVal = getRecordHeader(iTempTypeID);

      Vector<Object[]> recValues = getRecordValues(features, iTempTypeID);

      FDTHeader fdtHeader = new FDTHeader(vecHeaderVal, iTempTypeID);

      FDTWrapper fdtWrap = new FDTWrapper(stFullFilePath.toString(), fdtHeader);

      isSuccess = fdtWrap.createFDTFile(stStatus);

      isSuccess = isSuccess && fdtWrap.addRecordValues(recValues,stStatus);
      // fdtWrap.addRecordValues(recValues);

      fdtWrap.closeFDTWriter();

      if(isSuccess)
      Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "FDT files created -"
            + isSuccess);

      return isSuccess;

   }

   /**
    * 
    * @param stFilePath
    * @param lJobID
    * @param lFieldId
    */
   private boolean createShapeFile(String stFilePath, long lJobID, long lFieldId,String[] stStatus) {

      boolean isWrite = false;

      String folderName = null;
      try {
         folderName = Utils.getFieldBoundsLoc(lJobID, getJobTime(),
               stProjName,dbMangr);
         if(folderName == null){
             Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"createShapeFile return null 1" );
            return false;
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
            Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - createShapeFile" );
            if(Utils.isSDCardMount()){
               stStatus[0] = Constants.SDCARD_NO_SPACE;
            }
            return false;
         }
      }

      mapPath.put((int) lFieldId, folderName);

      StringBuilder stFullFilePath = new StringBuilder();
      stFullFilePath.append(stFilePath);
      stFullFilePath.append(Constants.SHP_FILE_EXTENS);

      String stFileName = Utils.getBoundaryJobFilename(lJobID, getJobTime(),dbMangr);
      if(stFileName==null){
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "Sdcard not mount"
               + isWrite);
         return false;
      }

      ShpFileEncoder shpEncoder = new ShpFileEncoder();

      isWrite = shpEncoder.createBoundaryShapeFile(lJobID,
            stFullFilePath.toString(),stStatus,dbMangr);
         if(!isWrite){
            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "SHAPE files not created"
                  + isWrite);
            return false;
         }
      ShapeGpeAttribs shpAttribs = new ShapeGpeAttribs(
            Constants.DEFAULT_VAL_SHP_DATUM, Constants.DEFAULT_VAL_SHP_SYSTEM,
            Constants.DEFAULT_VAL_SHP_ZONE);

      isWrite = createAgTxtFile(stFilePath, stFileName,
                  Constants.GPE_FILE_EXTENS, AgTxtCodec.TYPE_SHAPE_GPE,
                  shpAttribs,stStatus);
      
      if(!isWrite){
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "AgTxtFile files not created"
               + isWrite);
         return false;
      }

      isWrite = createShpInfoFile(stFilePath, lJobID, lFieldId,stStatus);
      
      if(!isWrite){
         Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "ShpInfoFile files not created"
               + isWrite);
         return false;
      }

      String stDevFilePath=stFilePath.toString();
      stDevFilePath=stDevFilePath.substring(0,stDevFilePath.lastIndexOf(File.separator));
      
      Utils.dumpDevInfoTextFile(stDevFilePath, context);
      
      Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "SHAPE files creation -"
            + isWrite);

      return isWrite;
   }

   /**
    * 
    * @param stFilePath
    * @param stFileName
    * @param stFileExtens
    * @param iFileType
    * @param agTxtObj
    * @return
    */
   private boolean createAgTxtFile(String stFilePath, String stFileName,
         String stFileExtens, int iFileType, Object agTxtObj,String[] stStatus) {

      AgTxtCodec agTxtCodec = new AgTxtCodec(stFilePath, stFileExtens,
            iFileType, agTxtObj);

      boolean isWrite = agTxtCodec.writeFile(stStatus);
      
      if(isWrite){
      Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
            "Text files files created -" + isWrite);
      }

      return isWrite;

   }

   /**
    * Create the shp info file
    * 
    * @param iJobId
    * @param lFieldId
    * @return
    */
   private boolean createShpInfoFile(String stFilePath, long iJobId,
         long lFieldId,String[] stStatus) {
      boolean isSuccess = false;

      int iRevision = 0;

      FieldInfoValues fieldInfoVal = new FieldInfoValues();

      Field field = dbMangr.getAgjobByJobId(iJobId).getField();
      Farm farm = dbMangr.getAgjobByJobId(iJobId).getField().getFarm();
      Client client = dbMangr.getAgjobByJobId(iJobId).getField().getFarm()
            .getClient();

		if (client != null) {
			if (client.getId() != null) {
				if (client.getDesc().equals(FarmWorksContentProvider.UNKNOWN)) {
					fieldInfoVal.sClientID = Utils
							.getDeFaultId(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID);
				} else {
					fieldInfoVal.sClientID = Utils.getHexaStringFromLong(client
							.getId());
				}
			}
			if (client.getDesc() != null) {
				fieldInfoVal.sClientDesc = client.getDesc().toString();
			}

		}

		if (farm != null) {
			int iFarmStatus = farm.getStatus();
			long lFarmId = farm.getId();
			if (0 != (iFarmStatus & AgDataStoreResources.STATUS_AUTOGENERATED)) {
				lFarmId = FarmWorksContentProvider.D_UNKNOWN_CFFE_ID;
			}

			fieldInfoVal.sFarmID = Utils.getHexaStringFromLong(lFarmId);

			
			if (farm.getDesc() != null) {
				fieldInfoVal.sFarmDesc = farm.getDesc();
			}
		}

      if (field != null) {

         if (field.getId() != null) {
            
            if(field.getDesc().equals(FarmWorksContentProvider.UNKNOWN)){
               fieldInfoVal.sFieldID = Utils.getDeFaultId(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID);
            }else{
               fieldInfoVal.sFieldID = Utils.getHexaStringFromLong(field.getId());
            }   
                        
            if (field.getDesc() != null) {
               fieldInfoVal.sFieldDesc = field.getDesc();
            }
            // if (field.getUnitId() != null) {
            fieldInfoVal.sFieldAreaUnitID = String.valueOf(Unit.UNIT_TYPE_ACRE); // field.getUnits().getType()
            fieldInfoVal.sFarmAreaUnitID = String.valueOf(Unit.UNIT_TYPE_ACRE);
            // }
            // if (field.getUnits() != null) {
            // TODO - change it into unit desc
            fieldInfoVal.sFarmAreaUnitDesc = Unit.UNIT_ACRE_SHORT; // field.getUnits().getUshort().toString();
            // }
            // if (field.getUnits() != null) {
            // TODO - UnitDesc needs
            fieldInfoVal.sFieldAreaUnitDesc = Unit.UNIT_ACRE_SHORT; // field.getUnits().getUshort().toString();

            // }

				float fAreaVal = 0;
            if (field.getArea() != null) {
               fAreaVal = (float)Utils.getAcresFromSquareMeter(Float.valueOf(field.getArea()));
            }

				fieldInfoVal.fFarmArea = fAreaVal;

            fieldInfoVal.fFieldArea = fAreaVal;

            fieldInfoVal.nFieldBdryModified = 1; // field.getBoundaryModified();

            if (field.getBoundaryRevision() != null) {
               iRevision = field.getBoundaryRevision() + 1;
               fieldInfoVal.nFieldBdryRevision = iRevision;
            } else {
               iRevision += 1;
            }

         }

         // TODO - to be discuss
         fieldInfoVal.sPrjID = Utils.getHexaStringFromLong(iProjId);

         StringBuilder stPathBuild = new StringBuilder();

         stPathBuild.append(stFilePath);
         stPathBuild.append(Constants.XML_FILE_EXTENS);

         FieldInfo fieldInfo = new FieldInfo(stPathBuild.toString(),
               fieldInfoVal);
         isSuccess = fieldInfo.ConstructXML(stStatus);

         updateFiledBndryChanges(field, iRevision, isSuccess);

      }

      Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG, "Shape file info xml -"
            + isSuccess);

      return isSuccess;

   }

   /**
    * Get List of Vertex belongs to the feature based on fieldI
    * 
    * @param iFiledId
    * @return
    */
   public Vector<FGPPoint> getFeaturePointsList(long iFiledId) {

      List<Feature> features = dbMangr.getFeaturesByFieldId(iFiledId);

      Vector<FGPPoint> fgpPoints = null;
      if (features != null && features.size() > 0) {

         fgpPoints = new Vector<FGPPoint>();

         for (Feature feature : features) {

            byte[] blob = feature.getVertex();
            if (blob != null) {
               Vector<FGPPoint> vecFgpPoint = FGPCodec.getFGPPointListFromBlob(
                     Constants.FGP_VERSION, blob);
               fgpPoints.addAll(vecFgpPoint);
            }

         }

      }
      return fgpPoints;
   }

   /**
    * Get List of Vertex belongs to all the featurs based on fieldID & feature
    * type
    * 
    * @param iFiledId
    * @param features
    * @param iFeatureType
    * @return
    */
   public Vector<FGPPoint> getFeaturePointsList(long iFiledId,
         List<Feature> features, int iFeatureType) {

      Vector<FGPPoint> fgpPoints = null;

      if (features != null && features.size() > 0) {

         fgpPoints = new Vector<FGPPoint>();

         for (Feature feature : features) {

            byte[] blob = feature.getVertex();
            if (blob != null) {
               Vector<FGPPoint> vecFgpPoint = FGPCodec.getFGPPointListFromBlob(
                     Constants.FGP_VERSION, blob);
               fgpPoints.addAll(vecFgpPoint);
            }

         }

      }
      return fgpPoints;
   }

   /**
    * 
    * @param iTemplateId
    * @return
    */
   // get the record headers based on Template id
   private Vector<AttributesInfo> getRecordHeader(int iTemplateId) {

      Vector<AttributesInfo> vecAtt = null;
      AttributesInfo attribInfoHeader = null;

      List<AttributeInfoEntity> attribInfoList = dbMangr
            .getAttributeInfolistByTTId(iTemplateId);

      if (attribInfoList != null && attribInfoList.size() > 0) {
         vecAtt = new Vector<AttributesInfo>();
         String stDefaultval = "";

         if (iTemplateId == AgDataStoreResources.ATT_TYPE_IMAGE) {
            stDefaultval = AgDataStoreResources.PHOTO;
         } else {
            stDefaultval = AgDataStoreResources.TEMPLATETYPE_TEMPLATENAME_TFDTTAG[iTemplateId - 1];
         }

         attribInfoHeader = new AttributesInfo(AgDataStoreResources.FLAGTYPE,
               AttributesInfo.ATT_TYPE_DATA_STRING, stDefaultval,
               Constants.DBF_OTHERS_DECIMAL_COUNT,
               AgDataStoreResources.FLAGTYPE,
               Constants.MAX_DBF_SHORT_STRING_FIELD_LENGTH);

         vecAtt.add(attribInfoHeader);

         attribInfoHeader = null;

         for (AttributeInfoEntity attribInfo : attribInfoList) {
             if(attribInfo.getEncode() == 0){
                 continue;
             }
            
            String stTagName = attribInfo.getFdtTag();
            int iDataType = attribInfo.getDataType();
            
            if(iDataType==AgDataStoreResources.DATATYPE_PICKLIST && ! attribInfo.getName().equals(AgDataStoreResources.CROP_NPER)){
               stTagName =Constants.ST_BACKWORD_SLASH+stTagName;
            }
            
            String stDefalutValue=getAttributeDefalutValue(attribInfo);
            attribInfoHeader = new AttributesInfo(stTagName,
                  iDataType,stDefalutValue,
                  attribInfo.getDataType(), attribInfo.getTemplateType()
                        .getName(), attribInfo.getLength());

            vecAtt.add(attribInfoHeader);
         }
      }
      return vecAtt;
   }

   /**
    * 
    * @param features
    * @param iTemplateType
    * @param lJobId
    * @return
    */

   private Vector<Object[]> getRecordValues(List<Feature> features,
         int iTemplateType) {

      Vector<Object[]> vecRecValues = new Vector<Object[]>();
      int iStartIndex = 0;
      int iCurrIndex = 0;

      if (iTemplateType != -1) {

         if (features != null && features.size() > 0) {

            for (Feature feature : features) {
               int iPos = -1;

               if (null == feature) continue;

               JobTransaction jobTransaction = dbMangr.getFeatureInfoFromTxn(
                     agJob.getJobId(), feature.getId());

               if (jobTransaction != null) {

                  iPos = jobTransaction.getAttrindexId();
                  if (iPos > 1) {

                     iCurrIndex = iPos - 2;
                     getFeatureRecord(feature, iStartIndex, iCurrIndex,
                           vecRecValues, iTemplateType);
                     iStartIndex = iCurrIndex + 1;
                  }
               }
            }
         }
      }
      return vecRecValues;
   }

   private String getAttributeValue(AttributeEntity attributeEntity){
	   String stData=null;
	   if(attributeEntity == null){
		   return null;
	   }
	   stData=attributeEntity.getValue();
	   AttributeInfoEntity  entity=attributeEntity.getAttributeInfoEntity();
	   if(entity == null){
		return stData;   
	   }
	   try{
	   if(entity.getDataType() == AgDataStoreResources.DATATYPE_PICKLIST
               && entity.getId() != AgDataStoreResources.NDVI_NPER_ID){
		   PickList list=dbMangr.getPickListByID(Long.parseLong(stData));
		   if(list != null){
			   stData=list.getItem();
		   }
	   }
	   }catch (NumberFormatException e) {
		   //for 1.0.0.21 
		
	   }
	   
	   return stData;
   }
   private String getAttributeDefalutValue(AttributeInfoEntity infoEntity){
	   String stData=null;
	   if(infoEntity == null){
		   return null;
	   }
	   stData=infoEntity.getDefaultValue();
	  
	   try{
	   if(stData != null && stData.trim().length() != 0 &&
			   infoEntity.getDataType() == AgDataStoreResources.DATATYPE_PICKLIST
               && infoEntity.getId() != AgDataStoreResources.NDVI_NPER_ID){
		   PickList list=dbMangr.getPickListByID(Long.parseLong(stData));
		   if(list != null){
			   stData=list.getItem();
		   }
	   }
	   }catch (NumberFormatException e) {
		   //for 1.0.0.21 
		
	   }
	   
	   return stData;
   }
   
   private String getUTF8EncodeString(String stData){
	   if(stData == null || stData.trim().length() == 0){
		   return null;
	   }
	   /*try {
		stData=new String(stData.getBytes(),"UTF-8");
	} catch (UnsupportedEncodingException e) {
	
		e.printStackTrace();
	}*/
	   
	   return stData;
   }
   /**
    * 
    * @param feature
    * @return
    */
   // get the record values
   private boolean getFeatureRecord(Feature feature, int iStartIndex,
         int iCurrentIndex, Vector<Object[]> vecRecValues, int iTempType) {

      boolean bRet = false;
      Object[] recValues = null;

      List<AttributeEntity> attribEntity = null;

      if ((feature == null) || (vecRecValues == null)) {
         return bRet;
      }

      // No need to check
      // if (iStartIndex > iCurrentIndex) return bRet;

      switch (iTempType) {

         case AgDataStoreResources.ATT_TYPE_INSECTS:

            Insect insect = new Insect();
            AttributeInfoEntity attribInfo = null;

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "getFeatureRecord -insects");

            attribEntity = dbMangr.getAttributesByFeatureId(feature.getId());

            if (attribEntity != null && attribEntity.size() > 0) {

               for (AttributeEntity attributeEntity : attribEntity) {

                  attribInfo = attributeEntity.getAttributeInfoEntity();

                  insect.stFlagType = attribInfo.getTemplateType().getFdtTag();

                  if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.FLAGNAME)) {
                     insect.stFlagName = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.DETAIL)) {
                     insect.stDetails =  getUTF8EncodeString ( getAttributeValue(attributeEntity));
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.NOTES)) {
                     insect.stNotes = getUTF8EncodeString (attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.__IMAGE)) {

                     Uri photoUri = Uri.parse(attributeEntity.getValue());

                     String stImgInfo = photoUri.getPath();

                     if (stImgInfo != null
                           && !stImgInfo.equals(Constants.ST_EMPTY)) {

                        stImgInfo = Utils.getRandomImageName();
                        String stImgFilePath = makeJobImageFile(photoUri,
                              stImgInfo, AgDataStoreResources.ATT_TYPE_INSECTS);
                        
                        if (stImgFilePath != null) {
                           photoUri = Uri.parse(stImgFilePath);

//                           updateImgAttributes(photoUri.toString(),
//                                 feature.getId(),
//                                 AgDataStoreResources.ATT_TYPE_INSECTS);
                        }else{
                            if(! Utils.isSDCardMount()){
                                Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER," makeJobImageFile photo not access" );
                                return false;
                            }else{
                           stImgInfo = Constants.ST_EMPTY;
                            }
                        }
                     }
                     
                     insect.stPhoto = stImgInfo;

                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.SEVERITY)) {
                     insect.stSeverity = getUTF8EncodeString ( getAttributeValue(attributeEntity));
                    
                  }
               }
            }
            recValues = insect.getArrObj();
            break;
         case AgDataStoreResources.ATT_TYPE_WEEDS:

            Weed weed = new Weed();

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "getFeatureRecord -weeds");

            attribEntity = dbMangr.getAttributesByFeatureId(feature.getId());

            if (attribEntity != null && attribEntity.size() > 0) {

               for (AttributeEntity attributeEntity : attribEntity) {

                  attribInfo = attributeEntity.getAttributeInfoEntity();

                  weed.stFlagType = attribInfo.getTemplateType().getFdtTag();

                  if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.FLAGNAME)) {
                     weed.stFlagName = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.DETAIL)) {
                     weed.stDetails =   getUTF8EncodeString ( getAttributeValue(attributeEntity));
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.NOTES)) {
                     weed.stNotes = getUTF8EncodeString(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.__IMAGE)) {

                     Uri photoUri = Uri.parse(attributeEntity.getValue());

                     String stImgInfo = photoUri.getPath();

                     if (stImgInfo != null
                           && !stImgInfo.equals(Constants.ST_EMPTY)) {
                        stImgInfo = Utils.getRandomImageName();
                        String stImgFilePath = makeJobImageFile(photoUri,
                              stImgInfo, AgDataStoreResources.ATT_TYPE_WEEDS);
                        if (stImgFilePath != null) {
                        photoUri = Uri.parse(stImgFilePath);

//                        updateImgAttributes(photoUri.toString(),
//                              feature.getId(),
//                              AgDataStoreResources.ATT_TYPE_WEEDS);
                        }else{
                            if(! Utils.isSDCardMount()){
                                Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER," makeJobImageFile photo not access" );
                                return false;
                            }else{
                           stImgInfo = Constants.ST_EMPTY;
                            }
                        }
                     }

                     weed.stPhoto = stImgInfo;

                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.SEVERITY)) {
                     weed.stSeverity =  getUTF8EncodeString (getAttributeValue(attributeEntity));
                    
                  }

               }
            }

            recValues = weed.getArrObj();

            break;

         case AgDataStoreResources.ATT_TYPE_DISEASE:

            Disease disease = new Disease();

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "getFeatureRecord - dISEASE");

            attribEntity = dbMangr.getAttributesByFeatureId(feature.getId());

            if (attribEntity != null && attribEntity.size() > 0) {

               for (AttributeEntity attributeEntity : attribEntity) {

                  attribInfo = attributeEntity.getAttributeInfoEntity();

                  disease.stFlagType = attribInfo.getTemplateType().getFdtTag();

                  if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.FLAGNAME)) {
                     disease.stFlagName = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.DETAIL)) {
                     disease.stDetails =   getUTF8EncodeString ( getAttributeValue(attributeEntity));
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.NOTES)) {
                     disease.stNotes = getUTF8EncodeString(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.__IMAGE)) {

                     Uri photoUri = Uri.parse(attributeEntity.getValue());

                     String stImgInfo = photoUri.getPath();

                     if (stImgInfo != null
                           && !stImgInfo.equals(Constants.ST_EMPTY)) {

                        stImgInfo = Utils.getRandomImageName();
                        String stImgFilePath = makeJobImageFile(photoUri,
                              stImgInfo, AgDataStoreResources.ATT_TYPE_DISEASE);
                        
                        if (stImgFilePath != null) {

                        photoUri = Uri.parse(stImgFilePath);

//                        updateImgAttributes(photoUri.toString(),
//                              feature.getId(),
//                              AgDataStoreResources.ATT_TYPE_DISEASE);
                        }else{
                            if(! Utils.isSDCardMount()){
                                Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER," makeJobImageFile photo not access" );
                                return false;
                            }else{
                           stImgInfo = Constants.ST_EMPTY;
                            }
                        }
                     }
                     disease.stPhoto = stImgInfo;

                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.SEVERITY)) {
                     disease.stSeverity = getUTF8EncodeString (getAttributeValue(attributeEntity));
                     
                  }
               }
            }
            recValues = disease.getArrObj();

            break;

         case AgDataStoreResources.ATT_TYPE_CROP_CONDITION:

            CropCondition cropcontn = new CropCondition();

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "getFeatureRecord - CROP_CONDITION");

            attribEntity = dbMangr.getAttributesByFeatureId(feature.getId());

            if (attribEntity != null && attribEntity.size() > 0) {

               for (AttributeEntity attributeEntity : attribEntity) {

                  attribInfo = attributeEntity.getAttributeInfoEntity();

                  cropcontn.stFlagType = attribInfo.getTemplateType()
                        .getFdtTag();

                  if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.FLAGNAME)) {
                     cropcontn.stFlagName = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.CONDITION)) {
                     cropcontn.stCondition = getUTF8EncodeString( getAttributeValue(attributeEntity));
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.NOTES)) {
                     cropcontn.stNotes = getUTF8EncodeString(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.__IMAGE)) {

                     Uri photoUri = Uri.parse(attributeEntity.getValue());

                     String stImgInfo = photoUri.getPath();

                     if (stImgInfo != null
                           && !stImgInfo.equals(Constants.ST_EMPTY)) {
                        stImgInfo = Utils.getRandomImageName();
                        String stImgFilePath = makeJobImageFile(photoUri,
                              stImgInfo,
                              AgDataStoreResources.ATT_TYPE_CROP_CONDITION);
                        if (stImgFilePath != null) {

                        photoUri = Uri.parse(stImgFilePath);

//                        updateImgAttributes(photoUri.toString(),
//                              feature.getId(),
//                              AgDataStoreResources.ATT_TYPE_CROP_CONDITION);
                        }else{
                            if(! Utils.isSDCardMount()){
                                Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER," makeJobImageFile photo not access" );
                                return false;
                            }else{
                           stImgInfo = Constants.ST_EMPTY;
                            }
                        }
                     }
                     cropcontn.stPhoto = stImgInfo;

                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.CROP)) {
                     cropcontn.stCrop =   getUTF8EncodeString ( getAttributeValue(attributeEntity));
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.GROWTHSTG)) {
                     cropcontn.stGrowthstage =  getUTF8EncodeString(getAttributeValue(attributeEntity));
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.PLANTCOUNT)) {
                     cropcontn.stPlantCount = attributeEntity.getValue();
                  }
               }
            }
            recValues = cropcontn.getArrObj();

            break;

         case AgDataStoreResources.ATT_TYPE_OTHERS:

            Other others = new Other();

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "getFeatureRecord -OTHERS");

            attribEntity = dbMangr.getAttributesByFeatureId(feature.getId());

            if (attribEntity != null && attribEntity.size() > 0) {

               for (AttributeEntity attributeEntity : attribEntity) {

                  attribInfo = attributeEntity.getAttributeInfoEntity();
                  others.stFlagType = attribInfo.getTemplateType().getFdtTag();

                  if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.FLAGNAME)) {
                     others.stFlagName = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.NOTES)) {
                     others.stNotes = getUTF8EncodeString (attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.__IMAGE)) {

                     Uri photoUri = Uri.parse(attributeEntity.getValue());

                     String stImgInfo = photoUri.getPath();

                     if (stImgInfo != null
                           && !stImgInfo.equals(Constants.ST_EMPTY)) {

                        stImgInfo = Utils.getRandomImageName();

                        String stImgFilePath = makeJobImageFile(photoUri,
                              stImgInfo, AgDataStoreResources.ATT_TYPE_OTHERS);
                        if (stImgFilePath != null) {

                        photoUri = Uri.parse(stImgFilePath);

//                        updateImgAttributes(photoUri.toString(),
//                              feature.getId(),
//                              AgDataStoreResources.ATT_TYPE_OTHERS);
                        }else{
                            if(! Utils.isSDCardMount()){
                                Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER," makeJobImageFile photo not access" );
                                return false;
                            }else{
                           stImgInfo = Constants.ST_EMPTY;
                            }
                        }
                     }

                     others.stPhoto = stImgInfo;

                  }
               }
            }

            recValues = others.getArrObj();

            break;

         case AgDataStoreResources.ATT_TYPE_IMAGE:

            Photo photo = new Photo();

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "getFeatureRecord  - IMAGE");

            attribEntity = dbMangr.getAttributesByFeatureId(feature.getId());

            if (attribEntity != null && attribEntity.size() > 0) {

               for (AttributeEntity attributeEntity : attribEntity) {

                  attribInfo = attributeEntity.getAttributeInfoEntity();

                  photo.stFlagType = attribInfo.getTemplateType().getFdtTag();

                  String stURI = attributeEntity.getValue();
                  Uri photoUri = Uri.parse(stURI);

                  String stImgInfo = photoUri.getPath();

                  if (stImgInfo != null
                        && !stImgInfo.equals(Constants.ST_EMPTY)) {

                     Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                           "Valid image  - " + stImgInfo);

                     stImgInfo = Utils.getRandomImageName();

                     String stImgFilePath = makeJobImageFile(photoUri,
                           stImgInfo, AgDataStoreResources.ATT_TYPE_IMAGE);
                     
                     if (stImgFilePath != null) {
                     
                     photoUri = Uri.parse(stImgFilePath);

//                     updateImgAttributes(photoUri.toString(), feature.getId(),
//                           AgDataStoreResources.ATT_TYPE_IMAGE);
                     }else{
                         if(! Utils.isSDCardMount()){
                             Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER," makeJobImageFile photo not access" );
                             return false;
                         }else{
                        stImgInfo = Constants.ST_EMPTY;
                         }
                     }
                  }

                  photo.stImgName = stImgInfo;
               }

            }
            recValues = photo.getArrObj();
            break;

         case AgDataStoreResources.ATT_TYPE_NDVI:

            NDVI ndvi = new NDVI();

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "getFeatureRecord - NDVI");

            attribEntity = dbMangr.getAttributesByFeatureId(feature.getId());
            if (attribEntity != null && attribEntity.size() > 0) {

               for (AttributeEntity attributeEntity : attribEntity) {

                  attribInfo = attributeEntity.getAttributeInfoEntity();
                  ndvi.stFlagType = attribInfo.getTemplateType().getFdtTag();

                  if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.FLAGNAME)) {
                     ndvi.stFlagName = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.CROP)) {
                     ndvi.stCrop = getAttributeValue(attributeEntity);
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.CROP_NPER)) {
                     ndvi.stCropNper = Utils.getFormatData(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.CONVFACT)) {
                     ndvi.stConvFact = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.NDVI_REF)) {
                     ndvi.stNDVIRef = Utils.getFormatData(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.NDVI_NON_REF)) {
                     ndvi.stNDVIFp = Utils.getFormatData(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.NDVI_SOIL)) {
                     ndvi.stNDVISoil = Utils.getFormatData(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.NDVI_NUE)) {
                     ndvi.stNUEPer = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.NDVI_MAX_YIELD)) {
                     ndvi.stMaxYield = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.NDVI_RI)) {
                     ndvi.stResponseIndex = Utils.getFormatData(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.NDVI_NRATE)) {
                     ndvi.stNRate = Utils.getFormatData(attributeEntity.getValue());
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.NRATEUNIT)) {
                     ndvi.stNRateUnit = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.YIELDUNIT)) {
                     ndvi.stYieldUnit = attributeEntity.getValue();
                  }
                  else if (attribInfo.getFdtTag().equals(
                        AgDataStoreResources.__IMAGE)) {

                     Uri photoUri = Uri.parse(attributeEntity.getValue());

                     String stImgInfo = photoUri.getPath();

                     if (stImgInfo != null
                           && !stImgInfo.equals(Constants.ST_EMPTY)) {

                        stImgInfo = Utils.getRandomImageName();

                        String stImgFilePath = makeJobImageFile(photoUri,
                              stImgInfo, AgDataStoreResources.ATT_TYPE_NDVI);
                        if (stImgFilePath != null) {

                           photoUri = Uri.parse(stImgFilePath);

// updateImgAttributes(photoUri.toString(),
// feature.getId(),
// AgDataStoreResources.ATT_TYPE_OTHERS);
                        } else {
                           if (!Utils.isSDCardMount()) {
                              Log.printLog(isLogger, Log.LOG_TYPE_INFO,
                                    Constants.TAG_JOB_ENCODER,
                                    " makeJobImageFile photo not access");
                              return false;
                           } else {
                              stImgInfo = Constants.ST_EMPTY;
                           }
                        }
                     }

                     ndvi.stPhoto = stImgInfo;

                  }
               }
            }

            recValues = ndvi.getArrObj();
            break;
            
         case AgDataStoreResources.ATT_TYPE_NDVI_REF:

            NDVIRef ndviRef = new NDVIRef();

            Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
                  "getFeatureRecord - NDVIRef");

            attribEntity = dbMangr.getAttributesByFeatureId(feature.getId());

            if (attribEntity != null && attribEntity.size() > 0) {

               for (AttributeEntity attributeEntity : attribEntity) {

                  attribInfo = attributeEntity.getAttributeInfoEntity();
                  ndviRef.stFlagType = attribInfo.getTemplateType().getFdtTag();

                  if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.FLAGNAME)) {
                     ndviRef.stFlagName = attributeEntity.getValue();
                  } else if (attribInfo.getFdtTag().equals(
                         AgDataStoreResources.NDVI_REF_FLAG_TYPE)) {
                     ndviRef.stFlatNRef = Utils.getFormatData(attributeEntity.getValue());
                  } 
               }
            }

            recValues = ndviRef.getArrObj();

            break;

         default:
            break;
      }

      // Fill the vector data with dummy entries from istartindex to
      if (iStartIndex == iCurrentIndex) {
         vecRecValues.add(iCurrentIndex, recValues);
      } else {
         for (int k = iStartIndex; k <= iCurrentIndex - 1; k++) {
            Object[] recObj = getEmptyRecord(recValues.length);
            vecRecValues.add(k, recObj);
         }
         vecRecValues.add(iCurrentIndex, recValues);
      }

      bRet = true;
      
      //deleteFlagInfo(feature.getId());

      return bRet;

   }

   private Object[] getEmptyRecord(int iCount) {

      Object[] recValue = new Object[iCount];

      for (int i = 0; i < iCount; i++) {
         recValue[i] = "";
      }

      return recValue;
   }

   // Image - move create jobfile structure
   private String makeJobImageFile(Uri imgUri, String stImgName,
         int iTemplateType) {
      String stDestPath = null;
      try {
         stDestPath = Utils.getImageFileLoc(agJob.getJobId(),
               iTemplateType, getJobTime(), stProjName);
         if(stDestPath==null){
            return null;
         }
         
      } catch (FileNotFoundException e1) {
         e1.printStackTrace();
         if(e1.getMessage().equals(Utils.UNABLE_TO_CREATE)){
            Log.printLog(isLogger, Log.LOG_TYPE_INFO,Constants.TAG_JOB_ENCODER,"No memory space in sdcard - makeJobImageFile" );
            return null;
         }
      }

      String stImgFilePath = stDestPath + stImgName;

      try {
         Utils.saveUriImage(imgUri, context, stImgFilePath);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         File file = new File(stDestPath.toString());
         if(file.isDirectory())
         {
            if(file.list() != null && file.list().length == 0)
            {
            Utils.deleteDirectory(stDestPath);
            }
         }
         return null;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }catch (OutOfMemoryError e) {
         e.printStackTrace();
         return null;
      }

      return stImgFilePath;
   }

   // Update Image new Image URI in DB
   /*private void updateImgAttributes(String stImgUri, long lFeatureId,
         int iTemplateId) {
      List<AttributeInfoEntity> mList = dbMangr
            .getAttributeInfolistByTTId(AgDataStoreResources.ATT_TYPE_IMAGE);
      ArrayList<AttributeEntity> attList = new ArrayList<AttributeEntity>(
            mList.size());

      for (int i = 0; i < mList.size(); i++) {
         if (mList.get(i).getDataType() == AgDataStoreResources.ATTRIBUTEINFO_DATATYPE_IMAGE) {
            AttributeEntity mObject = new AttributeEntity();
            mObject.setFeatureId(lFeatureId);
            mObject.setAttributeInfoId(mList.get(i).getId());
            mObject.setValue(stImgUri);
            attList.add(mObject);
         }
      }

      Log.printLog(isLogger, Log.LOG_TYPE_INFO, TAG,
            "updateImgAttributes feature Id  - " + lFeatureId);
      if(Utils.isSDCardMount()){
      dbMangr.updateAttrByFeatureId(lFeatureId, agJob.getJobId(), iTemplateId,
            attList);
      }
   }*/
   
   // Delete Flag info details in sdcard   
   private void deleteFlagInfo(long lFeatureId){
      
      File file = new File(Constants.getFlagStoreDir()+String.valueOf(lFeatureId));
      if(file.exists()){
         file.delete();
      }else{
         Log.printLog(true, Log.LOG_TYPE_INFO, Constants.TAG_JOB_ENCODER, "Flag not found - not deleted");         
      }      
   }

   // Update the boundary revision and modified value
   private void updateFiledBndryChanges(Field field, int iRevision,
         boolean isModified) {
      if (isModified) {
         if (field != null) {

            field.setBoundaryRevision(iRevision);

            field.setBoundaryModified(Constants.BOUNDARY_MODIFIED_VAL);

            dbMangr.updateField(field);
         }
      }
   }

   private void updateFiledArea(Field field) {

      if (field == null) {
         return;
      }

      List<Feature> features = dbMangr.getFeaturesByFieldId(field.getId(),
            AgDataStoreResources.FEATURE_TYPE_BOUNDARY);

      long lAreaVal = 0;

//      if (null != field.getArea()) {
//         lAreaVal = Long.valueOf(field.getArea());
//      }

      for (Feature feature : features) {
         if (null != feature.getArea()) {
            lAreaVal = lAreaVal + feature.getArea();
         }
      }

      field.setArea(String.valueOf(lAreaVal));

      dbMangr.updateField(field);
   }

   // update Field Bounding box value in DB
	private void updateFiledBoundingBox(Field field) {
		
		if (field == null) {
			return;
		}
		BoundingBox mFieldBB = null;
		if (null == field.getBottomRightX()) {
			mFieldBB = new BoundingBox(Integer.MAX_VALUE, Integer.MIN_VALUE,
					Integer.MIN_VALUE, Integer.MAX_VALUE);
		} else {
			mFieldBB = new BoundingBox(field.getTopLeftX(),
					field.getBottomRightY(), field.getBottomRightX(),
					field.getTopLeftY());
		}

		List<Feature> features = dbMangr.getFeaturesByFieldId(field.getId(),
				AgDataStoreResources.FEATURE_TYPE_BOUNDARY);

		if (features == null) {
			throw new NoSuchElementException(
					"Feature is empty for the boundry job");
		}

		for (Feature feature : features) {
		    if(feature == null){
		        continue;
		    }
			BoundingBox mFeatBB = new BoundingBox(feature.getTopLeftX(),
					feature.getBottomRightY(), feature.getBottomRightX(),
					feature.getTopLeftY());

			mFieldBB.stretch(mFeatBB);
		}

		field.setBottomRightX(mFieldBB.right);
		field.setTopLeftY(mFieldBB.top);
		field.setTopLeftX(mFieldBB.left);
		field.setBottomRightY(mFieldBB.bottom);

		dbMangr.updateField(field);

   }

   /*
    * 
    */
   public Map<Integer, String> getTemplatePath() {
      return mapPath;
   }

   /**
    * Clear the template path
    */
   public void setClearTemplatePath() {
      if (mapPath != null) {
         mapPath.clear();
      }
   }

   public void setJobTime() {

      Date mDate = new Date();
      SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd'_'HHmmss");

      stJobTime = ft.format(mDate);
   }

   public String getJobTime() {
      return stJobTime;
   }
   
  

}
