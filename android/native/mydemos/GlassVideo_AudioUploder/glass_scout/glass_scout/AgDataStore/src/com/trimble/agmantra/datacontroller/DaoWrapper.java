package com.trimble.agmantra.datacontroller;

import com.trimble.agmantra.dao.AgJobDao;
import com.trimble.agmantra.dao.AttributeEntityDao;
import com.trimble.agmantra.dao.AttributeInfoEntityDao;
import com.trimble.agmantra.dao.ClientDao;
import com.trimble.agmantra.dao.CropDao;
import com.trimble.agmantra.dao.FarmDao;
import com.trimble.agmantra.dao.FarmDao.Properties;
import com.trimble.agmantra.dao.FeatureDao;
import com.trimble.agmantra.dao.FeatureTypeDao;
import com.trimble.agmantra.dao.FieldDao;
import com.trimble.agmantra.dao.FlagCounterDao;
import com.trimble.agmantra.dao.JobDao;
import com.trimble.agmantra.dao.JobTransactionDao;
import com.trimble.agmantra.dao.JobTypeDao;
import com.trimble.agmantra.dao.JobtimingDao;
import com.trimble.agmantra.dao.LanguageDao;
import com.trimble.agmantra.dao.PickListDao;
import com.trimble.agmantra.dao.TemplateTypeDao;

import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.AgJob;
import com.trimble.agmantra.entity.AttributeEntity;
import com.trimble.agmantra.entity.AttributeInfoEntity;
import com.trimble.agmantra.entity.Client;
import com.trimble.agmantra.entity.Crop;
import com.trimble.agmantra.entity.Farm;
import com.trimble.agmantra.entity.Feature;
import com.trimble.agmantra.entity.FeatureType;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.entity.FlagCounter;
import com.trimble.agmantra.entity.Job;
import com.trimble.agmantra.entity.JobTransaction;
import com.trimble.agmantra.entity.JobType;
import com.trimble.agmantra.entity.Jobtiming;
import com.trimble.agmantra.entity.Language;
import com.trimble.agmantra.entity.PickList;
import com.trimble.agmantra.entity.TemplateType;

import de.greenrobot.dao.QueryBuilder;

import com.trimble.agmantra.dbutil.Log;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DaoWrapper {

   private static final String TAG = "FarmWorksDaoWrapper";
   
   private FarmWorksContentProvider mContentProvider=null;
   
   public DaoWrapper(FarmWorksContentProvider mContentProvider) {
       this.mContentProvider=mContentProvider;
   }

   protected QueryBuilder<Client> getClientQb(ClientDao clientDao) {
      QueryBuilder<Client> qb = clientDao.queryBuilder();
      qb.orderAsc(com.trimble.agmantra.dao.ClientDao.Properties.Desc);
      return qb;
   }

   protected QueryBuilder<Farm> getFarmQb(FarmDao farmDao, long clientid) {

      QueryBuilder<Farm> qb = null;
      if (farmDao != null) {
         qb = farmDao.queryBuilder();
         qb.where(Properties.ClientId.eq(clientid));
         qb.orderAsc(Properties.Desc);
      }
      return qb;
   }
   protected QueryBuilder<Farm> getFarmAllQb(FarmDao farmDao) {

       QueryBuilder<Farm> qb = null;
      if (farmDao != null ){
          qb = farmDao.queryBuilder();
          qb.orderAsc(Properties.Desc);
       
       }
       return qb;
    }
   protected QueryBuilder<Field> getFieldQb(FieldDao fieldDao, long farmid) {

      QueryBuilder<Field> qb = null;
         if (fieldDao != null) {
         qb = fieldDao.queryBuilder();
         qb.where(com.trimble.agmantra.dao.FieldDao.Properties.FarmId.eq(farmid));
         qb.orderAsc(com.trimble.agmantra.dao.FieldDao.Properties.Desc);
      }
      return qb;
   }

   protected QueryBuilder<Field> getFieldQb(FieldDao fieldDao) {

      QueryBuilder<Field> qb = null;
      if (fieldDao != null) {
         qb = fieldDao.queryBuilder();
         qb.orderAsc(com.trimble.agmantra.dao.FieldDao.Properties.Desc);

      }
      return qb;
   }

   // Generate random Job id

   private int getRandomJobID() {
      int id = new SecureRandom().nextInt();
      id = Math.abs(id);
      return id;
   }

   // Generate random Feature id
   private int getRandomFeatureID() {
      int id = new SecureRandom().nextInt();
      id = Math.abs(id);
      return id;
   }

   protected long insertJobid(JobDao jobDao) {
      long jobIdRetr = -1;
      Job job = new Job();
      long jobid = 0;
      do {
         jobid = (long) getRandomJobID();
         Log.i("AgDataStore", "RandomJobId generated = " + jobid);
      } while (((jobid > 0x10000000) && (jobid < 0xF0000000))
            && (jobDao.load(jobid) != null));

      job.setId(jobid);
      job.setStarttime(new Date());
      job.setStatus(AgDataStoreResources.JOB_STATUS_UNFINISHED);
      jobIdRetr = jobDao.insert(job);
      Log.i("AgDataStore", "RandomJobId inserted to table is = " + jobIdRetr);
      return jobIdRetr;
   }

   protected long updateFlagCounter(FlagCounterDao flagCounterDao, long jobId,
         long templateTypeId) {
      long lInsertCheck = -1;
      FlagCounter flgCounter = null;
      if (jobId > 0 && templateTypeId > 0) {
         Log.i("AgDataStore", "updateFlagCounter::Jobid/templid" + jobId + "/"
               + templateTypeId);
         flgCounter = getFlagCounterEntity(flagCounterDao, jobId,
               templateTypeId);
         if (null != flgCounter) {
            int iCnter = flgCounter.getCount();
            Log.i("AgDataStore",
                  "updateFlagCounter::entry for jobid/tempid already present. Count = "
                        + iCnter);
            iCnter++;
            flgCounter.setCount(iCnter);
            flagCounterDao.update(flgCounter);
            Log.i("AgDataStore", "updateFlagCounter::Jobid/templid" + jobId
                  + "/" + templateTypeId + "update to:" + iCnter);
         } else {
            // Insert new record in counter table
            flgCounter = new FlagCounter();
            flgCounter.setJobId(jobId);
            flgCounter.setTemplateTypeId(templateTypeId);
            flgCounter.setCount(1);
            lInsertCheck = flagCounterDao.insert(flgCounter);
            Log.i("AgDataStore", "updateFlagCounter::inserted Jobid/templid"
                  + jobId + "/" + templateTypeId + "counter updated to: 1"
                  + "ret status :" + lInsertCheck);
         }
      }

      return lInsertCheck;
   }

   private FlagCounter getFlagCounterEntity(FlagCounterDao flagCounterDao,
         long jobId, long templateTypeId) {
      QueryBuilder<FlagCounter> qb = flagCounterDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.FlagCounterDao.Properties.JobId
            .eq(jobId),
            com.trimble.agmantra.dao.FlagCounterDao.Properties.TemplateTypeId
                  .eq(templateTypeId));

      List<FlagCounter> tempFlag = qb.list();
      if (tempFlag != null && !tempFlag.isEmpty()) {
         Log.i("AgDataStore",
               "getFlagCounterKey::Returned a existing entry. jobid/templid:"
                     + jobId + " / " + templateTypeId);
         return tempFlag.get(0);

      }

      Log.i("AgDataStore", "getFlagCounterKey::Returning null. jobid/templid:"
            + jobId + " / " + templateTypeId);
      return null;
   }

   // Creating Feature id and insert in job Transaction table with feature id

   protected long insertFeatureinfo(FeatureDao featureDao,
         JobTransactionDao jobTransactionDao, long jobId, long featuretypeId,
         boolean isUpdateJobTxn) {

      long insertcheck = -1;

      Feature feature = new Feature();
      JobTransaction jobTransaction = new JobTransaction();
      long featureid = 0;
      do {
         featureid = (long) getRandomFeatureID();
         Log.i("AgDataStore", "RandomFeatId generated = " + featureid);
      } while (((featureid > 0x10000000) && (featureid < 0xF0000000))
            && (featureDao.load(featureid) != null));

      feature.setId(featureid);
      feature.setFieldId((long) -1); // mark as an unknown field
      feature.setFeatureTypeId(featuretypeId);

      insertcheck = featureDao.insert(feature);
      Log.i("AgDataStore", "FeatId inserted = " + insertcheck);

      if (insertcheck != -1 && isUpdateJobTxn) {
         jobTransaction.setJobId(jobId);
         jobTransaction.setFeatureId(insertcheck);
         jobTransaction.setTemplateTypeId((long) -1);
         jobTransaction.setStatus(0);
         jobTransactionDao.insert(jobTransaction);
         Log.i("AgDataStore", "Entry into jobtx table inserted for featid:"
               + insertcheck);
      }

      return featureid;
   }

   protected void updateFeatureVertex(FeatureDao featureDao, long jobid,
         long featureid, byte[] vertex) {

      byte[] currVertex = null;
      Feature tempFeature = new Feature();
      tempFeature = featureDao.load(featureid);
      if(tempFeature != null){
    	  currVertex = tempFeature.getVertex();
          byte[] updatedByte = byteArrayCopy(currVertex, vertex);
          tempFeature.setVertex(updatedByte);
          featureDao.update(tempFeature);  
      }

   }

   /**
    * 
    * Copy new byte array to another byte array
    * 
    * @param newByte
    */

   protected synchronized byte[] byteArrayCopy(byte[] existingByte,
         byte[] newByte) {
      int iDescOffset = 0;

      if (newByte == null && existingByte == null) {
         return null;
      }

      if (newByte == null) {
         return existingByte;
      }

      if (existingByte == null && newByte != null) {
         existingByte = new byte[newByte.length];
         iDescOffset = 0;
      } else {
         iDescOffset = existingByte.length;
         byte bTemp[] = existingByte;
         existingByte = new byte[newByte.length + existingByte.length];
         System.arraycopy(bTemp, 0, existingByte, 0, bTemp.length);
      }
      int iSrcStart = 0;
      System.arraycopy(newByte, iSrcStart, existingByte, iDescOffset,
            newByte.length);
      return existingByte;

   }

   // AgJob operations

   protected AgJob getAgJobbyJobId(AgJobDao agJobDao, long jobId) {
      QueryBuilder<AgJob> qb = null;

      if (jobId != -1) {

         qb = agJobDao.queryBuilder().where(
               com.trimble.agmantra.dao.AgJobDao.Properties.JobId.eq(jobId));

         List<AgJob> agJobslist = qb.list();

         if (null != agJobslist && !agJobslist.isEmpty()) {

            return agJobslist.get(0);
         }

      }
      return null;
   }

   protected List<AgJob> getAgJobbyFieldID(AgJobDao agJobDao, long fieldId) {
      QueryBuilder<AgJob> qb = null;

      if (fieldId != -1) {

         qb = agJobDao.queryBuilder().where(
               com.trimble.agmantra.dao.AgJobDao.Properties.FieldId.eq(fieldId));

         List<AgJob> agJobslist = qb.list();

         if (null != agJobslist && !agJobslist.isEmpty()) {

            return agJobslist;
         }

      }
      return null;
   }
   

   protected boolean checkUnfinishedAgJobbyFieldID (JobDao jobDao, AgJobDao agJobDao, long fieldId) {
      QueryBuilder<AgJob> qb = null;

      if (fieldId != -1) return false;
      
      List<Job> unfinJob = getUnfinshedJobs(jobDao);
      
      if (null == unfinJob || 0 == unfinJob.size()) {
         return false;
      }

      for (Job job:unfinJob) {
         qb = agJobDao.queryBuilder().where(
               com.trimble.agmantra.dao.AgJobDao.Properties.FieldId.eq(fieldId),
               com.trimble.agmantra.dao.AgJobDao.Properties.JobId.eq(job.getId()));

         List<AgJob> agJobslist = qb.list();

         if (null != agJobslist && !agJobslist.isEmpty()) {

            return true;
         }
         
      }
      
      return false;
   }
   
   protected List<JobTransaction> getFeatureTransactionByJobId(
         JobTransactionDao jobTransactionDao, long JobId) {

      QueryBuilder<JobTransaction> qb = null;
      if (JobId != -1) {

         qb = jobTransactionDao.queryBuilder();
         qb.where(com.trimble.agmantra.dao.JobTransactionDao.Properties.JobId
               .eq(JobId));
         return qb.list();
      }
      return null;
   }

   // getFeaturelistby fieldid

   protected List<Feature> getFeaturesByFieldId(FeatureDao featureDao,
         long fieldid) {

      QueryBuilder<Feature> qb = null;
      if (fieldid != -1) {

         qb = featureDao.queryBuilder();
         qb.where(com.trimble.agmantra.dao.FeatureDao.Properties.FieldId
               .eq(fieldid));
         return qb.list();
      }
      return null;
   }

   protected List<Feature> getFeaturesByFieldId(FeatureDao featureDao,
         long fieldid, long lfeaturetypeid) {

      QueryBuilder<Feature> qb = null;
      if ((fieldid != -1) && (-1 != lfeaturetypeid)) {

         qb = featureDao.queryBuilder();
         qb.where(com.trimble.agmantra.dao.FeatureDao.Properties.FieldId
               .eq(fieldid),
               com.trimble.agmantra.dao.FeatureDao.Properties.FeatureTypeId
                     .eq(lfeaturetypeid));
         return qb.list();
      }
      return null;
   }

   protected List<Feature> getAllBoundaries(FeatureDao featureDao) {

      QueryBuilder<Feature> qb = null;
         qb = featureDao.queryBuilder();
         qb.where(com.trimble.agmantra.dao.FeatureDao.Properties.FeatureTypeId
                     .eq(AgDataStoreResources.FEATURE_TYPE_BOUNDARY),
					  com.trimble.agmantra.dao.FeatureDao.
					  Properties.FieldId.notEq(AgDataStoreResources.UN_ASSOCIATED_BOUNDARY_WITH_FIELD));
         return qb.list();
   }
   
   protected Job getJobByJobId(JobDao jobDao, long jobId) {
      QueryBuilder<Job> qb = jobDao.queryBuilder();
      Job job = new Job();
      qb.where(com.trimble.agmantra.dao.JobDao.Properties.Id.eq(jobId));
      List<Job> list = qb.list();
      if (!list.isEmpty() && null != list) {
         job = list.get(0);
      }

      return job;
   }

   // unifinshed Jobs
   protected List<Job> getUnfinshedJobs(JobDao jobDao) {
      QueryBuilder<Job> qb = jobDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.JobDao.Properties.Status.eq(AgDataStoreResources.JOB_STATUS_UNFINISHED));
      return qb.list();
   }

   protected List<Job> getFinshedJobs(JobDao jobDao) {
      QueryBuilder<Job> qb = null;
      qb = jobDao.queryBuilder();
      qb.whereOr(com.trimble.agmantra.dao.JobDao.Properties.Status.eq(AgDataStoreResources.JOB_STATUS_FINISHED),com.trimble.agmantra.dao.JobDao.Properties.Status.eq(AgDataStoreResources.JOB_STATUS_ENCODING));
      return qb.list();
   }
   
   protected List<Job> getJobsToBeUploaded(JobDao jobDao) {
	      QueryBuilder<Job> qb = null;
	      qb = jobDao.queryBuilder();
	      qb.whereOr(com.trimble.agmantra.dao.JobDao.Properties.Status.eq(AgDataStoreResources.JOB_STATUS_ENCODING),(com.trimble.agmantra.dao.JobDao.Properties.Status.eq(AgDataStoreResources.JOB_STATUS_UNUPLOADED)));
	      return qb.list();
	   }

   // Templates

   protected List<JobType> getAllJobType(JobTypeDao jobTypeDao) {
      QueryBuilder<JobType> qb = jobTypeDao.queryBuilder();
      return qb.list();

   }

   protected List<PickList> getPickList(PickListDao pickListDao, long attrInfoId,boolean isOrderByAsc, boolean isNoneTobeAppended) {
      QueryBuilder<PickList> qb = pickListDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.PickListDao.Properties.AttrinfoId
            .eq(attrInfoId));
      if (isOrderByAsc) {
		
    	  qb.orderAsc(com.trimble.agmantra.dao.PickListDao.Properties.Item);
	}
      List<PickList> tempPickList=new ArrayList<PickList>();
      if (null!=tempPickList) {
    	  if (isNoneTobeAppended) {
	    	  PickList pickList=new PickList((long)-1, FarmWorksContentProvider.NONE,FarmWorksContentProvider.NONE,"0", attrInfoId);
	    	  tempPickList.add(pickList);
    	  }
    	  tempPickList.addAll(qb.list());
	}
      
      return tempPickList;
   }

   protected List<AttributeEntity> getAttributeEntityList(
         AttributeEntityDao attributeEntityDao, long attrInfoId) {
      QueryBuilder<AttributeEntity> qb = attributeEntityDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.AttributeEntityDao.Properties.AttributeInfoId
            .eq(attrInfoId));
      return qb.list();
   }

   protected List<FeatureType> getAllFeatureType(FeatureTypeDao featuretypeDao) {
      QueryBuilder<FeatureType> qb = featuretypeDao.queryBuilder();
      return qb.list();
   }

   protected List<AttributeInfoEntity> getAttrInfoByTTId(
         AttributeInfoEntityDao attributeInfoEntityDao, long id) {
      QueryBuilder<AttributeInfoEntity> qb = attributeInfoEntityDao
            .queryBuilder();
      qb.where(com.trimble.agmantra.dao.AttributeInfoEntityDao.Properties.TemplatetypeId
            .eq(id));

      return qb.list();
   }

   protected List<TemplateType> getAllTemplateList(
         TemplateTypeDao templateTypeDao,int iFeatureType,int iJobType,String stLanguage) {
      QueryBuilder<TemplateType> qb = templateTypeDao.queryBuilder();
      //To order TemplateList in Asc order
      qb.where(com.trimble.agmantra.dao.TemplateTypeDao.Properties.Featuretypeid
              .eq(iFeatureType),com.trimble.agmantra.dao.TemplateTypeDao.Properties.Jobtypeid
              .eq(iJobType),com.trimble.agmantra.dao.TemplateTypeDao.Properties.Locale
              .eq(stLanguage));
      qb.orderAsc(com.trimble.agmantra.dao.TemplateTypeDao.Properties.Name);
      return qb.list();
   }

   protected long getJobTxnKey(JobTransactionDao jobTransactionDao, long jobId,
         long featureId) {

      long jobTxnKey = -1;
      QueryBuilder<JobTransaction> qb = jobTransactionDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.JobTransactionDao.Properties.JobId
            .eq(jobId),
            com.trimble.agmantra.dao.JobTransactionDao.Properties.FeatureId
                  .eq(featureId));
      List<JobTransaction> listJobTxn = qb.list();
      if (!listJobTxn.isEmpty() && null != listJobTxn) {
         for (JobTransaction tempjobTransaction : listJobTxn) {
            if (jobId == tempjobTransaction.getJobId()
                  && featureId == tempjobTransaction.getFeatureId()) {

               jobTxnKey = tempjobTransaction.getId();
            }

         }
      }
      return jobTxnKey;
   }

   protected long getAttrKey(AttributeEntityDao attributeEntityDao, long id) {
      long attrKey = -1;
      QueryBuilder<AttributeEntity> qb = attributeEntityDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.AttributeEntityDao.Properties.FeatureId
            .eq(id));
      List<AttributeEntity> listAttributeEntities = qb.list();
      if (null != listAttributeEntities && !listAttributeEntities.isEmpty()) {
         for (AttributeEntity attributeEntity : listAttributeEntities) {
            if (id == attributeEntity.getFeatureId()) {
               attrKey = attributeEntity.getId();
            }
         }
      }
      return attrKey;

   }

   protected boolean updateJobTxn(JobTransactionDao jobTransactionDao,
         FlagCounterDao flagCounterDao, JobTransaction jobTransaction,
         boolean isTemplateTypeUpdate) {

      long jobTxnKey = -1;
      jobTxnKey = getJobTxnKey(jobTransactionDao, jobTransaction.getJobId(),
            jobTransaction.getFeatureId());

      JobTransaction tempJobTransaction = new JobTransaction();

      tempJobTransaction = jobTransactionDao.load(jobTxnKey);

      Long id = jobTransaction.getId();

      if (null != tempJobTransaction) {

         if (id != null) {
            tempJobTransaction.setId(jobTxnKey);
         }

         Integer status = jobTransaction.getStatus();
         if (status != null) {
            tempJobTransaction.setStatus(status);
         }

         Integer passid = jobTransaction.getPassid();
         if (passid != null) {
            tempJobTransaction.setPassid(passid);
         }

         Integer attrindexId = jobTransaction.getAttrindexId();
         if (attrindexId != null) {
            tempJobTransaction.setAttrindexId(attrindexId);
         }

         Long featureId = jobTransaction.getFeatureId();
         if (featureId != null) {
            tempJobTransaction.setFeatureId(featureId);
         }

         Long jobId = jobTransaction.getJobId();
         if (jobId != null) {
            tempJobTransaction.setJobId(jobId);
         }

         Long templateTypeId = jobTransaction.getTemplateTypeId();
         if (templateTypeId != null) {
            tempJobTransaction.setTemplateTypeId(templateTypeId);
         }

         jobTransactionDao.update(tempJobTransaction);

         if (isTemplateTypeUpdate) {
            updateFlagCounter(flagCounterDao, jobId, templateTypeId);
         }
      }
      return true;

   }

   protected long getAgJobKey(AgJobDao agJobDao, long jobId) {

      long agJobKey = -1;
      if (jobId > 0) {

         QueryBuilder<AgJob> qb = agJobDao.queryBuilder();
         qb.where(com.trimble.agmantra.dao.AgJobDao.Properties.JobId.eq(jobId));
         List<AgJob> listAgJob = qb.list();
         for (AgJob tempAgJob : listAgJob) {
            if (jobId == tempAgJob.getJobId())
            ;
            agJobKey = tempAgJob.getId();

         }
      }
      return agJobKey;
   }

   protected boolean updateAgJob(AgJobDao agJobDao, long jobid, AgJob agJob) {
      long agJobKey = -1;
      agJobKey = getAgJobKey(agJobDao, jobid);
      AgJob tempagjob = new AgJob();
      tempagjob = agJobDao.load(agJobKey);

      if (null != tempagjob) {

         Long id = agJob.getId();
         if (id != null) {
            tempagjob.setId(agJobKey);
         }

         Long jobId = agJob.getJobId();
         if (jobId != null) {
            tempagjob.setJobId(jobId);
         }

         Long cropId = agJob.getCropId();
         if (cropId != null) {
            tempagjob.setCropId(cropId);
         }

         Long fieldId = agJob.getFieldId();
         if (fieldId != null) {
            tempagjob.setFieldId(fieldId);
         }

         Long jobTypeId = agJob.getJobTypeId();
         if (jobTypeId != null) {
            tempagjob.setJobTypeId(jobTypeId);
         }

         agJobDao.update(tempagjob);
      }

      return true;
   }

   protected Farm getfarmDetailsById(FarmDao farmDao, String name) {

      QueryBuilder<Farm> qb = farmDao.queryBuilder();
      qb.where(Properties.Desc.eq(name));
      List<Farm> listfarm = qb.list();
      if (!listfarm.isEmpty() && listfarm != null) {
         return listfarm.get(0);
      }

      return null;
   }

   protected boolean updateResumeTime(JobtimingDao jobtimingDao, long jobId) {
      QueryBuilder<Jobtiming> qb = jobtimingDao.queryBuilder();
      qb.where(
            com.trimble.agmantra.dao.JobtimingDao.Properties.JobId.eq(jobId),
            com.trimble.agmantra.dao.JobtimingDao.Properties.Status.eq(0));
      List<Jobtiming> listtiming = qb.list();
      if (!listtiming.isEmpty() && null != listtiming) {

         for (Jobtiming tempjobtiming : listtiming) {
            if (tempjobtiming.getJobId() == jobId
                  && tempjobtiming.getStatus() == 0) {
               Jobtiming jobtiming = new Jobtiming();
               jobtiming.setId(tempjobtiming.getId());
               jobtiming.setPauseTime(tempjobtiming.getPauseTime());
               jobtiming.setResumeTime(new Date());
               jobtiming.setStatus(1);
               jobtimingDao.update(jobtiming);
            }
         }
         return true;
      }

      return false;
   }

   protected void updateFeature(FeatureDao featureDao, Feature feature) {

      Long id = feature.getId();
      Feature tempFeature = new Feature();
      tempFeature = featureDao.load(id);

      if (null != tempFeature) {
         if (id != null) {
            tempFeature.setId(id);
         }
         Long area = feature.getArea();
         if (area != null) {
            tempFeature.setArea(area);
         }

         Long color = feature.getColor();
         if (color != null) {
            tempFeature.setColor(color);
         }

         Integer thickness = feature.getThickness();
         if (thickness != null) {
            tempFeature.setThickness(thickness);
         }

         Long perimeter = feature.getPerimeter();
         if (perimeter != null) {
            tempFeature.setPerimeter(perimeter);
         }

         byte[] vertex = feature.getVertex();
         if (vertex != null) {
            tempFeature.setVertex(vertex);
         }

         Integer bottomRightX = feature.getBottomRightX();
         if (bottomRightX != null) {
            tempFeature.setBottomRightX(bottomRightX);
         }

         Integer bottomRightY = feature.getBottomRightY();
         if (bottomRightY != null) {
            tempFeature.setBottomRightY(bottomRightY);
         }

         Integer topLeftX = feature.getTopLeftX();
         if (topLeftX != null) {
            tempFeature.setTopLeftX(topLeftX);
         }

         Integer topLeftY = feature.getTopLeftY();
         if (topLeftY != null) {
            tempFeature.setTopLeftY(topLeftY);
         }

         Long featureTypeId = feature.getFeatureTypeId();
         if (featureTypeId != null) {

            tempFeature.setFeatureTypeId(featureTypeId);
         }

         Long fieldId = feature.getFieldId();
         if (fieldId != null) {
            tempFeature.setFieldId(fieldId);
         }

         featureDao.update(tempFeature);

      }

   }

   protected void updateJob(JobDao jobDao, Job job) {

      Job tempjob = new Job();

      tempjob = jobDao.load(job.getId());

      if (null != tempjob) {

         Long id = job.getId();

         if (id != null) {
            tempjob.setId(id);
         }

         java.util.Date starttime = job.getStarttime();
         if (starttime != null) {
            tempjob.setStarttime(starttime);
         }

         java.util.Date endtime = job.getEndtime();
         if (endtime != null) {
            tempjob.setEndtime(endtime);
         }

         Integer status = job.getStatus();
         if (status != null) {
            tempjob.setStatus(status);
         }

         jobDao.update(tempjob);
      }
   }

   protected void updateField(FieldDao fieldDao, Field field) {

      Field tempField = new Field();
      tempField = fieldDao.load(field.getId());

      Long id = field.getId();
      if (id != null) {
         tempField.setId(id);
      }

      String desc = field.getDesc();
      if (desc != null) {
         tempField.setDesc(desc);
      }

      Boolean isServerdata = field.getIsServerdata();
      if (isServerdata != null) {
         tempField.setIsServerdata(isServerdata);
      }

      String area = field.getArea();
      if (area != null) {
         tempField.setArea(area);
      }

      Integer boundaryModified = field.getBoundaryModified();
      if (boundaryModified != null) {
         tempField.setBoundaryModified(boundaryModified);
      }

      Integer boundaryRevision = field.getBoundaryRevision();
      if (boundaryRevision != null) {
         tempField.setBoundaryRevision(boundaryRevision);
      }

      Integer bottomRightX = field.getBottomRightX();
      if (bottomRightX != null) {
         tempField.setBottomRightX(bottomRightX);
      }

      Integer bottomRightY = field.getBottomRightY();
      if (bottomRightY != null) {
         tempField.setBottomRightY(bottomRightY);
      }

      Integer topLeftX = field.getTopLeftX();
      if (topLeftX != null) {
         tempField.setTopLeftX(topLeftX);
      }

      Integer topLeftY = field.getTopLeftY();
      if (topLeftY != null) {
         tempField.setTopLeftY(topLeftY);
      }

      Integer locked = field.getLocked();
      if (locked != null) {
         tempField.setLocked(locked);
      }

      Integer deleted = field.getDeleted();
      if (deleted != null) {
         tempField.setDeleted(deleted);
      }

      Integer status = field.getStatus();
      if (status != null) {
         tempField.setStatus(status);
      }

      Long farmId = field.getFarmId();
      if (farmId != null) {
         tempField.setFarmId(farmId);
      }

      Long unitId = field.getUnitId();
      if (unitId != null) {
         tempField.setUnitId(unitId);
      }

      if (null != tempField) {

         fieldDao.update(tempField);
      }
   }

   protected List<AttributeEntity> getAttribsByFeatureId(
         AttributeEntityDao attributeEntityDao, long id) {
      QueryBuilder<AttributeEntity> qb = attributeEntityDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.AttributeEntityDao.Properties.FeatureId
            .eq(id));

      return qb.list();
   }

   protected void deleteAttrByFeatureId(AttributeEntityDao attributeEntityDao,
         long featureId) {
      List<AttributeEntity> tempAttrDeletelist = getAttribsByFeatureId(
            attributeEntityDao, featureId);
      attributeEntityDao.deleteInTx(tempAttrDeletelist);
   }

   protected void updateAttrByFeatureId(AttributeEntityDao attributeEntityDao,
         FlagCounterDao flagCounterDao, long featureId, long jobId,
         long templateTypeId, List<AttributeEntity> tempAttrUpdatelist,
         int countVal) {
      deleteAttrByFeatureId(attributeEntityDao, featureId);
      // attributeEntityDao.updateInTx(tempAttrUpdatelist);
      insertAttribute(attributeEntityDao, flagCounterDao, tempAttrUpdatelist,
            jobId, templateTypeId, countVal);
   }

   protected List<AttributeEntity> getAttribsByFeatureIdAsasc(
         AttributeEntityDao attributeEntityDao, long id) {
      QueryBuilder<AttributeEntity> qb = attributeEntityDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.AttributeEntityDao.Properties.FeatureId
            .eq(id));
      qb.orderAsc(com.trimble.agmantra.dao.AttributeEntityDao.Properties.FeatureId);

      return qb.list();
   }

   protected void insertAttribute(AttributeEntityDao attributeEntityDao,
         FlagCounterDao flagCounterDao, List<AttributeEntity> attributeEntity,
         long jobId, long templateTypeId, int countVal) {
      attributeEntityDao.insertInTx(attributeEntity);
   }

   protected List<JobTransaction> getFeatureInfoByTTId(
         JobTransactionDao jobTransactionDao, long lJobID, long templateid) {
      QueryBuilder<JobTransaction> qb = jobTransactionDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.JobTransactionDao.Properties.JobId
            .eq(lJobID));
      qb.where(com.trimble.agmantra.dao.JobTransactionDao.Properties.TemplateTypeId
            .eq(templateid));
      qb.orderAsc(com.trimble.agmantra.dao.JobTransactionDao.Properties.AttrindexId);

      return qb.list();
   }

   protected FlagCounter getTemplateCounter(FlagCounterDao flagCounterDao,
         long jobId, long templateTypeId) {
      QueryBuilder<FlagCounter> qb = flagCounterDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.FlagCounterDao.Properties.JobId
            .eq(jobId),
            com.trimble.agmantra.dao.FlagCounterDao.Properties.TemplateTypeId
                  .eq(templateTypeId));
      List<FlagCounter> tempcounterlist = qb.list();
      FlagCounter flagCounter = new FlagCounter();
      if (null != tempcounterlist && !tempcounterlist.isEmpty()) {
         flagCounter = tempcounterlist.get(0);
      }

      return flagCounter;
   }

   protected List<FlagCounter> getFlagCounterByJobId(
         FlagCounterDao flagCounterDao, long jobId) {
      QueryBuilder<FlagCounter> qb = flagCounterDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.FlagCounterDao.Properties.JobId
            .eq(jobId));
      return qb.list();
   }

   protected JobTransaction getFeatureInfoFromTxn(
         JobTransactionDao jobTransactionDao, long jobId, long featureId) {
      JobTransaction transaction = null;
      if (jobId > 0 && featureId > 0) {
         long txnKey = getJobTxnKey(jobTransactionDao, jobId, featureId);
         if (txnKey > 0) {
            transaction = jobTransactionDao.load(txnKey);
         }
      }

      return transaction;

   }
   
   
   protected long getTemplateTypeFromTxn(
	         JobTransactionDao jobTransactionDao, long featureId) {
       long lTemplateType=-1;
	   JobTransaction transaction = null;
	
	     QueryBuilder<JobTransaction> qb=jobTransactionDao.queryBuilder();
	     qb.where(com.trimble.agmantra.dao.JobTransactionDao.Properties.FeatureId.eq(featureId));
	     List<JobTransaction> tempJobTxn=qb.list();
	     if (tempJobTxn!=null && !tempJobTxn.isEmpty()) {
	    	 transaction=tempJobTxn.get(0);
	    	 lTemplateType=transaction.getTemplateTypeId();
		}
	      
	      return lTemplateType;

	   }


   protected List<Crop> getCropList(CropDao cropDao){
	   List<Crop> cropList=null;
      QueryBuilder<Crop> qb=cropDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.CropDao.Properties.Desc.notEq(FarmWorksContentProvider.UNKNOWN));
      qb.orderAsc(com.trimble.agmantra.dao.CropDao.Properties.Desc);
    
      cropList=qb.list();
      List<Crop> tempcroplist=new ArrayList<Crop>();
      Crop NoneCrop=new Crop(mContentProvider.getUnknownCropID(), FarmWorksContentProvider.UNKNOWN, false, FarmWorksContentProvider.NONE, 0, -1L);
      tempcroplist.add(NoneCrop);
      if (cropList!=null && !cropList.isEmpty()) {
		tempcroplist.addAll(cropList);
	}
      return tempcroplist;
   }
   
   
   public boolean isClientExist(ClientDao clientDao,String clientName){
      boolean isClientAvail=false;
      QueryBuilder<Client> qb=clientDao.queryBuilder();
      //qb.where(com.trimble.agmantra.dao.ClientDao.Properties.Desc.eq(clientName));
      List<Client> clientList=qb.list();
      if (null!=clientList && !clientList.isEmpty() && clientName!= null && clientName.length() != 0) {
            for (int i = 0;  i < clientList.size();  i++) {
            if (clientName.equalsIgnoreCase(clientList.get(i).getDesc())) {
               isClientAvail=true;
            }
        }  
      }
      return isClientAvail;
   }
   
   
public boolean isFarmExist(FarmDao farmDao,long clientId,String farmName){
   boolean isFarmAvail=false; 
   QueryBuilder<Farm> qb=farmDao.queryBuilder();
   qb.where(Properties.ClientId.eq(clientId)); 
   List<Farm> farmList=qb.list();
   if (null!=farmList && !farmList.isEmpty() && farmName!=null && farmName.length() != 0) { 
      for (int i = 0;  i < farmList.size();  i++) {
         if (farmName.equalsIgnoreCase(farmList.get(i).getDesc())) {
            isFarmAvail=true;
         }
     }  
   }
   return isFarmAvail;
      
   }
   
   
public boolean isFieldExist(FieldDao fieldDao,long farmId,String fieldName){
   boolean isFieldAvail=false; 
   QueryBuilder<Field> qb=fieldDao.queryBuilder();
   qb.where(com.trimble.agmantra.dao.FieldDao.Properties.FarmId.eq(farmId));  
   List<Field> fieldList=qb.list();
   if (null!=fieldList && !fieldList.isEmpty() && fieldName!=null && fieldName.length() != 0) { 
      for (int i = 0;  i < fieldList.size();  i++) {
         if (fieldName.equalsIgnoreCase(fieldList.get(i).getDesc())) {
            isFieldAvail=true;
         }
     }  
   }
   return isFieldAvail;
      
   }
   public boolean isFeatureExist(FeatureDao featureDao,long id) {
      boolean bfeatureExist=false;
      if (-1!=id) {
         
     
      QueryBuilder<Feature> qb=featureDao.queryBuilder();
      qb.where(com.trimble.agmantra.dao.FeatureDao.Properties.Id.eq(id));
      List<Feature> featurelist=qb.list();
      if (null!=featurelist && !featurelist.isEmpty()) {
         bfeatureExist=true;
      }
      }
      return bfeatureExist;
     
   }
   
   public List<Language> getAllLanguageList(LanguageDao languageDao){
       QueryBuilder<Language> qb = languageDao.queryBuilder();
       return qb.list();
   }
   
   public boolean isLanguageExist(LanguageDao languageDao,String stLanguageName){
      boolean isLanguageExist=false;
      if(stLanguageName == null){
    	  return isLanguageExist;
      }
	   QueryBuilder<Language> qb = languageDao.queryBuilder();
       qb.where(com.trimble.agmantra.dao.LanguageDao.Properties.Language.eq(stLanguageName));
       List<Language> languages=qb.list();
       for (Language language : languages) {
    	   if(language != null){
    		   if(stLanguageName.equals(language.getLanguage())){
    			   isLanguageExist=true;
    			   break;
    		   }
    	   }
	}
       return isLanguageExist;
   }
   
   protected void clearDownloadedClient(final ClientDao mClientDao) {
     
      QueryBuilder<Client> qb = mClientDao.queryBuilder().where(ClientDao.Properties.IsServerdata.eq(true));
      mClientDao.deleteInTx(qb.list());   
   }
   
   protected void clearDownloadedFarm(final FarmDao mFarmDao) {
      
      QueryBuilder<Farm> qb = mFarmDao.queryBuilder().where(FarmDao.Properties.IsServerdata.eq(true));
      mFarmDao.deleteInTx(qb.list());   
   }
   
   protected void clearDownloadedField(final FieldDao mFieldDao) {
      
      QueryBuilder<Field> qb = mFieldDao.queryBuilder().where(FieldDao.Properties.IsServerdata.eq(true));
      mFieldDao.deleteInTx(qb.list());   
   }
   
   protected void clearDownloadedBoundary(final FeatureDao mFeatureDao, final FieldDao mFieldDao) {

      QueryBuilder<Field> qb = mFieldDao.queryBuilder().where(FieldDao.Properties.IsServerdata.eq(true));
      for(Field field : qb.list()){
         QueryBuilder<Feature> featureQB = mFeatureDao.queryBuilder().where(
               FeatureDao.Properties.FieldId.eq(field.getId()));
         mFeatureDao.deleteInTx(featureQB.list());
      }
      
   }
   
   protected void updateClientsList(List<Client> clients, ClientDao clientDao) {
      if (clientDao.count() == 0) {
         clientDao.insertInTx(clients);
      } else {
         for (Client client : clients) {
            try {
               if (clientDao.load(client.getId()) == null) {
                  clientDao.insert(client);
               } else {
                  clientDao.update(client);
               }
            } catch (android.database.sqlite.SQLiteConstraintException e) {
               Log.e(TAG, e.getMessage(), e);
            }
         }
      }
   }
   
   protected void updateFarmsList(List<Farm> farms, FarmDao farmDao) {
      if (farmDao.count() == 0) {
         farmDao.deleteAll();
      } else {
         for (Farm farm : farms) {
            try {
               if (farmDao.load(farm.getId()) == null) {
                  farmDao.insert(farm);
               } else {
                  farmDao.update(farm);
               }
            } catch (android.database.sqlite.SQLiteConstraintException e) {
               Log.e(TAG, e.getMessage(), e);
            }
         }
      }
   }
   
   protected void updateFieldsList(List<Field> fields, FieldDao fieldDao) {
      if (fieldDao.count() == 0) {
         fieldDao.insertInTx(fields);
      } else {
         for (Field field : fields) {
            try {
               if (fieldDao.load(field.getId()) == null) {
                  fieldDao.insert(field);
               } else {
                  fieldDao.update(field);
               }
            } catch (android.database.sqlite.SQLiteConstraintException e) {
               Log.e(TAG, e.getMessage(), e);
            }
         }
      }

   }
   
   protected void updateBoundaryList(final List<Feature> features,
         final FeatureDao featureDao) {
      if (featureDao != null) {
         featureDao.insertInTx(features);
      } else {
         for (Feature boundary : features) {

            try {
               if (featureDao.load(boundary.getId()) == null) {
                  featureDao.insert(boundary);
               } else {
                  featureDao.update(boundary);
               }
            } catch (android.database.sqlite.SQLiteConstraintException e) {
               Log.e(TAG, e.getMessage(), e);
            }
         }
      }

   }
   
}
