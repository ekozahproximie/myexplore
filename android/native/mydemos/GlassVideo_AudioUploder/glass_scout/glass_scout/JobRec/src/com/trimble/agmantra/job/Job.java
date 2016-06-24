package com.trimble.agmantra.job;

import com.trimble.agmantra.R;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.entity.AgJob;
import com.trimble.agmantra.entity.Feature;
import com.trimble.agmantra.entity.JobTransaction;
import com.trimble.agmantra.filecodec.fgp.FGPCodec;
import com.trimble.agmantra.layers.BoundaryLayer;
import com.trimble.agmantra.layers.BoundingBox;
import com.trimble.agmantra.layers.FGPPoint;
import com.trimble.agmantra.layers.GSOBoundary;
import com.trimble.agmantra.layers.GSOPath;
import com.trimble.agmantra.layers.GSOPoint;
import com.trimble.agmantra.layers.GSOPolygon;
import com.trimble.agmantra.layers.GSObject;
import com.trimble.agmantra.layers.GSObjectLayer;
import com.trimble.agmantra.layers.GSObjectType;
import com.trimble.agmantra.layers.GeoPoint;
import com.trimble.agmantra.layers.PathLayer;
import com.trimble.agmantra.layers.PointLayer;
import com.trimble.agmantra.layers.PolygonLayer;
import com.trimble.agmantra.utils.Mercator;
import com.trimble.agmantra.utils.Utils;
import com.trimble.mobile.android.templates.FlagInfo;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * The Class Job.
 */
public class Job implements JobListener {

	/**
	 * Enumeration Variable having the list of possible com.trimble.agmantra.job
	 * types.
	 */
	public static enum JobType {

		/** The Type_ boundary_ mapping. */
		Type_Boundary_Mapping,
		/** The Type_ map_ and_ scout. */
		Type_Map_And_Scout,
		/** The Type_ take_ picture. */
		Type_Take_Picture;
	};

	public static final String JOB_ID = "jobid";

	/**
	 * Variable to store Field Id
	 */
	protected long iFieldId = -1;

	/**
    * Variable to store Job Id
    */
   protected long                     iJobId             = -1;

  
	/**
	 * Variable to Database Manager object
	 */
	public FarmWorksContentProvider mDataBase;

	/**
	 * Variable to store the Point Layer object
	 */
	private PointLayer mPointLayer;

	/**
	 * Variable to store the Path Layer object
	 */
	private PathLayer mPathLayer;

	/**
	 * Variable to store the Polygon Layer object
	 */
	private PolygonLayer mPolygonLayer;

	/**
	 * Variable to store the Boundary Layer object
	 */
	private BoundaryLayer mBoundaryLayer;

   /**
    * Variable to store the com.trimble.agmantra.job type of current recording
    * com.trimble.agmantra.job
    */
   protected int                      iCurrRecShapeType  = GSObjectType.GSO_NONE;

	/**
	 * Variable to store the com.trimble.agmantra.job type of current recording
	 * com.trimble.agmantra.job
	 */
	private int iPointId = 1;

	/**
	 * Variable to store the com.trimble.agmantra.job type of current recording
	 * com.trimble.agmantra.job TODO: Increment this value when stop recording
	 * the feature is pressed.
	 */
	private int iPassId = 1;

	/**
	 * Variable to store the com.trimble.agmantra.job type of current recording
	 * com.trimble.agmantra.job
	 */
	private int iAttributeId = 2;

	/**
	 * TODO: Check how to generated region Id. Variable to store the
	 * com.trimble.agmantra.job type of current recording
	 * com.trimble.agmantra.job
	 */
	private int iRegionIndex = 0;

	private JobListener mJobListener = null;
	private boolean isAutoCloseEnabled = false;
	private int iAutoCloseDistance = 0;

	private Vector<GSObjectLayer> vecOverlayLayer = null;

	/**
	 * Construct for the class
	 * 
	 * @param iJobId
	 *            Parameter stating the Job Id on which the
	 *            com.trimble.agmantra.job is getting recorded
	 */
	public Job(long iJobId,FarmWorksContentProvider mDataBase) {
		this.iJobId = iJobId;
		this.mDataBase = mDataBase;
     // mDataBase = FarmWorksContentProvider.getInstance(null);
      iRegionIndex = (int) Utils.getNewID();
      AgJob mAgJob = mDataBase.getAgjobByJobId(iJobId);
      iFieldId = mAgJob.getFieldId();

	}

	public void setJobRelatedValues(JobListener mListener, boolean isAutoClose,
			int iAutoCloseDist) {
		mJobListener = mListener;
		isAutoCloseEnabled = isAutoClose;
		iAutoCloseDistance = iAutoCloseDist;
	}

	public long[] startTask() {
		boolean isFieldHasBoundary = false;
		long[] iTotalFeatureIds = null;
		long[] iBoundaryfeatureIds = null;
		long[] iNonBoundfeatureIds = null;
		//All boundaries will be displayed. So fetching boundaries based on field is not required.
		List<Feature> mFeatures = mDataBase.getFeaturesByFieldId(iFieldId,AgDataStoreResources.FEATURE_TYPE_BOUNDARY);
		if ((null != mFeatures) && (mFeatures.size() > 0)) {
			int i = 0;
			iBoundaryfeatureIds = new long[mFeatures.size()];
			for (Feature feature : mFeatures) {
				iBoundaryfeatureIds[i++] = feature.getId();
			}
			loadBoundaryLayer(mFeatures);
			isFieldHasBoundary = true;
         Log.i(Constants.TAG_JOB_RECORDER,
               "startTask() - Boundary features Loaded corresponding to  the task's field");
      } else {
         Log.i(Constants.TAG_JOB_RECORDER,
               "startTask() - No related Boundary features corresponding to the task's field");
      }
		boolean isUnfinishedJob = false;
		com.trimble.agmantra.entity.Job mJob = mDataBase
				.getJobInfoByJobId(iJobId);
		List<JobTransaction> mJobTxns = null;
		if (mJob != null
				&& mJob.getStatus() != null
				&& AgDataStoreResources.JOB_STATUS_UNFINISHED == mJob
						.getStatus()) {
			mJobTxns = mDataBase.getFeatureTxnByJobId(iJobId);
			if ((null != mJobTxns) && (mJobTxns.size() > 0)) {
				int i = 0;
				iNonBoundfeatureIds = new long[mJobTxns.size()];
				for (JobTransaction jobTransaction : mJobTxns) {
					iNonBoundfeatureIds[i++] = jobTransaction.getFeatureId();
				}
				loadNonBoundLayers(mJobTxns);
				isUnfinishedJob = true;
            Log.i(Constants.TAG_JOB_RECORDER,
                  "startTask() - Task loaded from pending task");
         }
      } else {
         Log.i(Constants.TAG_JOB_RECORDER, "startTask() - New Task Started");
      }
		if (isUnfinishedJob || isFieldHasBoundary) {
			// TODO:: send map update for all layers loading.
		   //Not loading boundaries on initAllLayers
			if (null == mJobTxns) {
				iTotalFeatureIds = new long[mFeatures.size()];
			} else {
				iTotalFeatureIds = new long[mFeatures.size() + mJobTxns.size()];
			}

			int i = 0;
			if (mFeatures.size() > 0) {
				for (i = 0; i < mFeatures.size(); i++) {
					iTotalFeatureIds[i] = iBoundaryfeatureIds[i];

				}
			}

			if ((null != mJobTxns) && (mJobTxns.size() > 0)) {
			//   iTotalFeatureIds = new long[mJobTxns.size()];
				for (int j = 0; j < mJobTxns.size(); j++) {
					iTotalFeatureIds[i++] = iNonBoundfeatureIds[j];
				}
			}
			Vector<GSObjectLayer> mAllLayers = getAllOverlayLayers();
			for (GSObjectLayer gsObjectLayer : mAllLayers) {
                           for (GSObject gsObject : gsObjectLayer.objectList) {
                              gsObject.setObjectTobeRefreshed(true);                              
                           }
                        }
			initAllLayers(mAllLayers);
			for (GSObjectLayer gsObjectLayer : mAllLayers) {
                           for (GSObject gsObject : gsObjectLayer.objectList) {
                              gsObject.setObjectTobeRefreshed(false);                              
                           }
                        }
         Log.i(Constants.TAG_JOB_RECORDER,
               "startTask() - Finished call of initAllLayers() for loading the layers on the map");
      }
		Log.i(Constants.TAG_JOB_RECORDER,
				"startTask() - Finished starting a task");
		return iTotalFeatureIds;
	}

	public void updateFieldBoundaries(long iNewFieldId) {
		if (iFieldId == iNewFieldId) {
			return;
		}
		List<Feature> mFeatures = mDataBase.getFeaturesByFieldId(iFieldId,
				AgDataStoreResources.FEATURE_TYPE_BOUNDARY);

		if ((null != mFeatures) && (mFeatures.size() > 0)) {
			for (Feature feature : mFeatures) {
				if (null != mBoundaryLayer) {
					mBoundaryLayer.removeObject(feature.getId());
               Log.i(Constants.TAG_JOB_RECORDER,
                     "updateFieldBoundaries() - removed the existing field boundaries");
            }
			}
		}
		iFieldId = iNewFieldId;
		mFeatures.clear();
		mFeatures = mDataBase.getFeaturesByFieldId(iFieldId,
				AgDataStoreResources.FEATURE_TYPE_BOUNDARY);
		
		int iTempCurrRecShapeType = GSObjectType.GSO_NONE;
      if (iCurrRecShapeType != GSObjectType.GSO_NONE) {
		   iTempCurrRecShapeType = iCurrRecShapeType;
         Log.i(Constants.TAG_JOB_RECORDER,
               "updateFieldBoundaries() - current recording shape type is not none");
      } else {
         Log.i(Constants.TAG_JOB_RECORDER,
               "updateFieldBoundaries() - current recording shape type is none");
      }
		if ((null != mFeatures) && (mFeatures.size() > 0)) {
			if ((null != mFeatures) && (mFeatures.size() > 0)) {
				loadBoundaryLayer(mFeatures);
				Vector<GSObjectLayer> mAllLayers = getAllOverlayLayers();
	                        for (GSObjectLayer gsObjectLayer : mAllLayers) {
	                           for (GSObject gsObject : gsObjectLayer.objectList) {
	                              gsObject.setObjectTobeRefreshed(true);                              
	                           }
	                        }
	                        if (iTempCurrRecShapeType != GSObjectType.GSO_NONE) {
	                           iCurrRecShapeType = iTempCurrRecShapeType;
	                           resetCurrGSObject();
	                           Log.i(Constants.TAG_JOB_RECORDER,
	                                 "updateFieldBoundaries() - reataining the currect recording GSObject for continuing recording");                
	                        }                        
	                        
	                        initAllLayers(mAllLayers);
	                        for (GSObjectLayer gsObjectLayer : mAllLayers) {
	                           for (GSObject gsObject : gsObjectLayer.objectList) {
	                              gsObject.setObjectTobeRefreshed(false);                              
	                           }
	                        }
            Log.i(Constants.TAG_JOB_RECORDER,
                  "updateFieldBoundaries() - loaded boundary features corresponding to new field");
         } else {
            Log.i(Constants.TAG_JOB_RECORDER,
                  "updateFieldBoundaries() - no boundary features present for the new field");
			}
		}
      

   }
	
   private void resetCurrGSObject() {
      switch (iCurrRecShapeType) {
         case GSObjectType.GSO_POLYLINE: {
               mPathLayer.resetCurrGSObject();
               mPathLayer.getMcurrGSObject().setObjectTobeRefreshed(false);
            }
	
            break;
         case GSObjectType.GSO_BOUNDARY: {
               mBoundaryLayer.resetCurrGSObject();
               mBoundaryLayer.getMcurrGSObject().setObjectTobeRefreshed(false);         
         }
            break;
         case GSObjectType.GSO_POLYGON: {
               mPolygonLayer.resetCurrGSObject();
               mPolygonLayer.getMcurrGSObject().setObjectTobeRefreshed(false);         
         }
         default:
            break;
      }

   }
	/**
	 * This method is called to inflate the layers from the database based on
	 * the passed JobId
	 * 
	 * @param mListJobTxns
	 * @param iJobId
	 *            Job Id which is to be loaded from Database.
	 * @return none
	 */
	public void loadBoundaryLayer(List<Feature> mFeatures) {
		int i = 0;
		for (i = 0; i < mFeatures.size(); i++) {
			Vector<FGPPoint> vecmPoints = FGPCodec.getFGPPointListFromBlob(
					Constants.FGP_VERSION, mFeatures.get(i).getVertex());
			if ((null != vecmPoints) && (vecmPoints.size() > 0)) {
				ArrayList<FGPPoint> mPoints = new ArrayList<FGPPoint>(
						vecmPoints);
				inflateLayers(mPoints, mFeatures.get(i));
				// TODO Murari if initalllayers is handled properly uncomment
				// the
				// line below
				// updateBoundingBox(mFeatures.get(i));
				setStatusOfCurrentFeatureRecording(true,true,false);
			}
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "loadBoundaryLayer() - finishing loading boudary features");
	}

	private void updateBoundingBox(Feature feature) {
		switch (iCurrRecShapeType) {
		case GSObjectType.GSO_POLYLINE: {
			mPathLayer.updateBoundingBox(feature);
		}
			break;
		case GSObjectType.GSO_BOUNDARY: {
			mBoundaryLayer.updateBoundingBox(feature);
		}
			break;
		case GSObjectType.GSO_POLYGON: {
			mPolygonLayer.updateBoundingBox(feature);
		}
		default:
			break;
		}
	}

	/**
	 * This method is called to inflate the layers from the database based on
	 * the passed JobId
	 * 
	 * @param mListJobTxns
	 * @param iJobId
	 *            Job Id which is to be loaded from Database.
	 * @return none
	 */
	private void loadNonBoundLayers(List<JobTransaction> mListJobTxns) {
		int i = 0;
      int iIncompleteFeatureIndex = -1;
		for (i = 0; i < mListJobTxns.size(); i++) {
			Vector<FGPPoint> vecmPoints = FGPCodec.getFGPPointListFromBlob(
					Constants.FGP_VERSION, mListJobTxns.get(i).getFeature()
							.getVertex());
			ArrayList<FGPPoint> mPoints = new ArrayList<FGPPoint>(vecmPoints);
			if ((null != vecmPoints) && (vecmPoints.size() > 0)) {
            boolean bCompleteFeature = (mListJobTxns.get(i).getStatus() == 1) ? true
                  : false;
            if (true == bCompleteFeature) {
                    if (mListJobTxns.get(i).getFeature().getFeatureTypeId() == AgDataStoreResources.FEATURE_TYPE_BOUNDARY) {
                        inflateLayers(i, mListJobTxns, mPoints);
                    } else {
                        if (-1 == mListJobTxns.get(i).getTemplateTypeId()) {
                            long lFetID=mListJobTxns.get(i).getFeature().getFeatureTypeId();
                            if (((lFetID == AgDataStoreResources.FEATURE_TYPE_PATH) ||
                                    (lFetID == AgDataStoreResources.FEATURE_TYPE_POLYGON))) {
                                /*
                                JobTransaction jobTrans = new JobTransaction();

                                jobTrans.setJobId(iJobId);
                                jobTrans.setTemplateTypeId((long)AgDataStoreResources.ATT_TYPE_OTHERS);
                                jobTrans.setFeatureId(mListJobTxns.get(i).getFeature().getId());

                                mDataBase.updateJobTxn(jobTrans, false);

                                List<AttributeInfoEntity> mList = mDataBase
                                        .getAttributeInfolistByTTId(AgDataStoreResources.ATT_TYPE_OTHERS);
                                ArrayList<AttributeEntity> attList = new ArrayList<AttributeEntity>(
                                        mList.size());
                                String stFlagName = null;
                                for (int j = 0; j < mList.size(); j++) {
                                    if (mList.get(j).getDataType() == AgDataStoreResources.DATATYPE_IMAGE) {
                                        AttributeEntity mObject = new AttributeEntity();
                                        mObject.setFeatureId(mListJobTxns.get(i).getFeature()
                                                .getId());
                                        mObject.setAttributeInfoId(mList.get(j).getId());
                                        mObject.setValue(com.trimble.agmantra.constant.Constants.ST_EMPTY);
                                        attList.add(mObject);
                                    } else if (mList.get(j).getDataType() == AgDataStoreResources.DATATYPE_STRINGARRAY) {
                                        AttributeEntity mObject = new AttributeEntity();
                                        mObject.setFeatureId(mListJobTxns.get(i).getFeature()
                                                .getId());
                                        mObject.setAttributeInfoId(mList.get(j).getId());
                                        mObject.setValue(com.trimble.agmantra.constant.Constants.ST_EMPTY);
                                        attList.add(mObject);
                                    } else if (mList.get(j).getDataType() == AgDataStoreResources.DATATYPE_STRING) {
                                        AttributeEntity mObject = new AttributeEntity();
                                        mObject.setFeatureId(mListJobTxns.get(i).getFeature()
                                                .getId());
                                        mObject.setAttributeInfoId(mList.get(j).getId());
                                        // get the number of others flags and
                                        // update it accordingly
                                        stFlagName = generateFlagName(iJobId,
                                                AgDataStoreResources.ATT_TYPE_OTHERS);
                                        mObject.setValue(stFlagName);
                                        attList.add(mObject);
                                    }
                                }
                                long lFeatureType = (mListJobTxns.get(i).getFeature()
                                        .getFeatureTypeId() == AgDataStoreResources.FEATURE_TYPE_POLYGON)?GSObjectType.GSO_POLYGON:GSObjectType.GSO_POLYLINE;
                                
                                long lFeatureID = mListJobTxns.get(i).getFeature().getId();
                                FlagInfo flagInfo = new FlagInfo(stFlagName, null,
                                        AgDataStoreResources.ATT_TYPE_OTHERS, lFeatureID,
                                        (int)lFeatureType);
                                mDataBase.insertAttribute(attList, iJobId,
                                        AgDataStoreResources.ATT_TYPE_OTHERS);
                                storeFlagInfo(iJobId, lFeatureID, flagInfo);*/
                                //inflateLayers(i, mListJobTxns, mPoints);
                                 mDataBase.deleteFeatureById(iJobId, mListJobTxns.get(i).getFeature().getId());
                            } else {
                                mDataBase.deleteFeatureById(iJobId, mListJobTxns.get(i)
                                        .getFeature().getId());
                            }
                        } else {
                            inflateLayers(i, mListJobTxns, mPoints);
                        }
                    }
               } else {
               iIncompleteFeatureIndex = i;
               Log.i(Constants.TAG_JOB_RECORDER,
                     "loadNonBoundLayers() - pending task has an incomplete feature");
				}
			}
      }
      if (iIncompleteFeatureIndex != -1) {
         Vector<FGPPoint> vecmPoints = FGPCodec.getFGPPointListFromBlob(
               Constants.FGP_VERSION, mListJobTxns.get(iIncompleteFeatureIndex)
                     .getFeature().getVertex());
         ArrayList<FGPPoint> mPoints = new ArrayList<FGPPoint>(vecmPoints);
         inflateLayers(mPoints, mListJobTxns.get(iIncompleteFeatureIndex)
               .getFeature());
         // updateBoundingBox(mListJobTxns.get(i).getFeature());
         setStatusOfCurrentFeatureRecording(false,true,false);
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "loadBoundaryLayer() - finishing loading non boudary features");
	}
	private void inflateLayers(int i,List<JobTransaction> mListJobTxns,ArrayList<FGPPoint> mPoints){
	    inflateLayers(mPoints, mListJobTxns.get(i).getFeature());
        // updateBoundingBox(mListJobTxns.get(i).getFeature());
        if (mListJobTxns.get(i).getFeature().getFeatureTypeId() != AgDataStoreResources.FEATURE_TYPE_POINT) {
           setStatusOfCurrentFeatureRecording(true,true,false);
        }
       
	}
	 private void storeFlagInfo(long lJobID,long lLogFeatureID,FlagInfo flagInfo) {
	        java.io.ObjectOutputStream dataOutputStream = null;
	        FileOutputStream fileOutputStream = null;
	        try {
	            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
	                return;
	            }
	            File agFlagPath = new File(Constants.getFlagStoreDir_Job(lJobID));
	            if (!agFlagPath.exists()) {
	                agFlagPath.mkdirs();
	            }
	            String stFileName =Constants.getFlagStoreDir_Job_Feat(lJobID, lLogFeatureID);
	           
	            dataOutputStream = new java.io.ObjectOutputStream(
	                    fileOutputStream = new FileOutputStream(stFileName));
	            dataOutputStream.writeObject(flagInfo);
	            dataOutputStream.flush();

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (fileOutputStream != null) {
	                try {
	                    fileOutputStream.close();
	                } catch (IOException e) {

	                    e.printStackTrace();
	                }
	            }
	            if (dataOutputStream != null) {
	                try {
	                    dataOutputStream.close();
	                } catch (IOException e) {

	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	public long getFlagCountByTemplateID(long lJobID,long lTemplateID){
        // query DB to get the current running number in attrib value
        // table
        if(mDataBase == null){
            return 1;
        }
        long lRunningNumber = mDataBase.getTemplateCounterForJob(lJobID, lTemplateID);
        if (-1 == lRunningNumber)
            lRunningNumber = 1; // as no records are currently present for this
                                // template type in DB
        else
            lRunningNumber++; // as the flag counter returned is the
                              // no.of.records currently present in DB
        
        return lRunningNumber;
    }
    public String generateFlagName(long lJobID,long lTemplateID) {
       
        long lRunningNumber =getFlagCountByTemplateID(lJobID, lTemplateID);
        Context mContext =mDataBase.getContext();
        String sFlagName = new String();
        switch ((int)lTemplateID) {
            case AgDataStoreResources.ATT_TYPE_INSECTS: // insects
                sFlagName += mContext.getString(R.string.flag_in)+"-";
                break;
            case AgDataStoreResources.ATT_TYPE_WEEDS: // weeds
                sFlagName += mContext.getString(R.string.flag_we)+"-";
                break;
            case AgDataStoreResources.ATT_TYPE_DISEASE: // disease
                sFlagName += mContext.getString(R.string.flag_ds)+"-";
                break;
            case AgDataStoreResources.ATT_TYPE_CROP_CONDITION: // crop condition
                sFlagName += mContext.getString(R.string.flag_cr)+"-";
                break;
            case AgDataStoreResources.ATT_TYPE_OTHERS: // other
                sFlagName +=mContext.getString(R.string.flag_ot)+"-";
                break;
            case AgDataStoreResources.ATT_TYPE_NDVI: // NDVI
                sFlagName += mContext.getString(R.string.flag_nd)+"-";
                break;
            case AgDataStoreResources.ATT_TYPE_NDVI_REF://NDVI ref
                sFlagName += mContext.getString(R.string.flag_nr)+"-";
                break;
            default: // unknown
                sFlagName += mContext.getString(R.string.unknown)+"-";
                break;
        }
        sFlagName += Long.toString(lRunningNumber);
        return sFlagName;
    }
	private void inflateLayers(ArrayList<FGPPoint> mPoints, Feature mFeature) {
		if (GSObjectType.GSO_POINT == mPoints.get(0).iObjectType) {
			createPointObject(mPoints.get(0), mFeature.getId());

		} else {
			startFeatureRecording(mPoints.get(0), mFeature);
		}
		for (FGPPoint fgpPoint : mPoints) {
			addFGPPointToFeature(fgpPoint, mFeature.getFeatureTypeId());
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "inflateLayers() - finished inflating layer");
	}

	public Vector<GSObjectLayer> getAllOverlayLayers() {
		if (vecOverlayLayer == null) {
			vecOverlayLayer = new Vector<GSObjectLayer>(4);
		}
		vecOverlayLayer.clear();
		if (mPointLayer != null) {
			vecOverlayLayer.add(mPointLayer);
		}
		if (mPathLayer != null) {
			vecOverlayLayer.add(mPathLayer);
		}
		if (mBoundaryLayer != null) {
			vecOverlayLayer.add(mBoundaryLayer);
		}
		if (mPolygonLayer != null) {
			vecOverlayLayer.add(mPolygonLayer);
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "getAllOverlayLayers() - inflating the overlays with the existing layers");
		return vecOverlayLayer;
	}

	public long createPointObject() {
		if (null == mPointLayer) {
			mPointLayer = new PointLayer();
		}
		long iFeatureId = mDataBase.generateFeatureId(iJobId,
				AgDataStoreResources.FEATURE_TYPE_POINT, true);
		GSObject mObject = new GSOPoint(iJobId, iFeatureId, iPassId++,
				iAttributeId++, this);
		mPointLayer.addObjectToList(false, mObject);
      Log.i(Constants.TAG_JOB_RECORDER,
            "createPointObject() - created GSOPoint object and added to list");
		return iFeatureId;
	}

	public synchronized long startFeatureRecording(int iType) {
		GSObject mObject;
		long iFeatureId = -1;
		switch (iType) {
		case GSObjectType.GSO_POLYLINE: {
			if (null == mPathLayer) {
				mPathLayer = new PathLayer();
			}
			iFeatureId = mDataBase.generateFeatureId(iJobId,
					AgDataStoreResources.FEATURE_TYPE_PATH, true);
			mObject = new GSOPath(iJobId, iFeatureId, iPassId++,
					iAttributeId++, this);
			mPathLayer.addObjectToList(false, mObject);
		}
			break;
		case GSObjectType.GSO_BOUNDARY: {
			if (null == mBoundaryLayer) {
				mBoundaryLayer = new BoundaryLayer(isAutoCloseEnabled,
						iAutoCloseDistance);
			}
			iFeatureId = mDataBase.generateFeatureId(iJobId,
					AgDataStoreResources.FEATURE_TYPE_BOUNDARY, true);
			mObject = new GSOBoundary(iJobId, iFeatureId, iPassId++,
					iAttributeId++, this);
			mBoundaryLayer.addObjectToList(false, mObject);

		}
			break;
		case GSObjectType.GSO_POLYGON: {
			if (null == mPolygonLayer) {
				mPolygonLayer = new PolygonLayer(isAutoCloseEnabled,
						iAutoCloseDistance);
			}
			iFeatureId = mDataBase.generateFeatureId(iJobId,
					AgDataStoreResources.FEATURE_TYPE_POLYGON, true);
			mObject = new GSOPolygon(iJobId, iFeatureId, iPassId++,
					iAttributeId++, this);
			mPolygonLayer.addObjectToList(false, mObject);

		}

		default:
			break;
		}
		iCurrRecShapeType = iType;
      Log.i(Constants.TAG_JOB_RECORDER,
            "startFeatureRecording() - created GSObject and added to list of type = "
                  + iCurrRecShapeType);
		return iFeatureId;
	}

	public void createPointObject(FGPPoint mPoint, long iFeatureId) {
		if (null == mPointLayer) {
			mPointLayer = new PointLayer();
		}
		GSObject mObject = new GSOPoint(iJobId, iFeatureId, mPoint.iPassID,
				mPoint.iAttrID, this);
		mPointLayer.addObjectToList(true, mObject);
      Log.i(Constants.TAG_JOB_RECORDER,
            "createPointObject() - created GSOPoint object and added to list");
	}

	public void startFeatureRecording(FGPPoint mPoint, Feature feature) {
		GSObject mObject;
		long iFeatureId = feature.getId();
		long iFeatureTypeId = feature.getFeatureTypeId();
		if (iFeatureTypeId == AgDataStoreResources.FEATURE_TYPE_PATH) {
			if (null == mPathLayer) {
				mPathLayer = new PathLayer();
			}
			mObject = new GSOPath(iJobId, iFeatureId, mPoint.iPassID,
					mPoint.iAttrID, this);
			mPathLayer.addObjectToList(true, mObject);
			iCurrRecShapeType = GSObjectType.GSO_POLYLINE;
		}

		else if (iFeatureTypeId == AgDataStoreResources.FEATURE_TYPE_BOUNDARY) {
			if (null == mBoundaryLayer) {
				mBoundaryLayer = new BoundaryLayer(isAutoCloseEnabled,
						iAutoCloseDistance);
			}
			mObject = new GSOBoundary(iJobId, iFeatureId, mPoint.iPassID,
					mPoint.iAttrID, this);
			mBoundaryLayer.addObjectToList(true, mObject);
			iCurrRecShapeType = GSObjectType.GSO_BOUNDARY;

		}
		if (iFeatureTypeId == AgDataStoreResources.FEATURE_TYPE_POLYGON) {
			if (null == mPolygonLayer) {
				mPolygonLayer = new PolygonLayer(isAutoCloseEnabled,
						iAutoCloseDistance);
			}
			mObject = new GSOPolygon(iJobId, iFeatureId, mPoint.iPassID,
					mPoint.iAttrID, this);
			mPolygonLayer.addObjectToList(true, mObject);
			iCurrRecShapeType = GSObjectType.GSO_POLYGON;
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "startFeatureRecording() - created GSObject and added to list of type = "
                  + iCurrRecShapeType);
	}

	/**
	 * This method is called for storing each point from the list to the
	 * respective layer.
	 * 
	 * @param eCurrRecShapeType2
	 * @param pointInfo
	 *            object of the class
	 * @return none
	 */

	public void addFGPPointToFeature(FGPPoint mPoint, long lfeatTypeId) {

		if (lfeatTypeId == AgDataStoreResources.FEATURE_TYPE_POINT) {

			mPointLayer.addPointToCurrObject(mPoint, true);
		} else if (lfeatTypeId == AgDataStoreResources.FEATURE_TYPE_PATH) {
			mPathLayer.addPointToCurrObject(mPoint, true);
		} else if (lfeatTypeId == AgDataStoreResources.FEATURE_TYPE_BOUNDARY) {
			mBoundaryLayer.addPointToCurrObject(mPoint, true);
		} else if (lfeatTypeId == AgDataStoreResources.FEATURE_TYPE_POLYGON) {
			mPolygonLayer.addPointToCurrObject(mPoint, true);
		}
     

	}

	/**
	 * This method is called for storing each point from the list to the
	 * respective layer.
	 * 
	 * @param eCurrRecShapeType2
	 * @param pointInfo
	 *            object of the class
	 * @return none
	 */

	public synchronized void addFGPPointToFeature(FGPPoint mPoint, int iType, boolean isFixFiltered) {
		// TODO:: check for assigning point id.
		mPoint.iPointID = iPointId++;
		mPoint.iRegionID = iRegionIndex;
		mPoint.iObjectType = iType;
		switch (iType) {
		case GSObjectType.GSO_POINT: {
		   if (isFixFiltered)
			mPointLayer.addPointToCurrObject(mPoint, isFixFiltered);
		}
			break;
		case GSObjectType.GSO_POLYLINE: {
		   if (isFixFiltered)
			mPathLayer.addPointToCurrObject(mPoint, isFixFiltered);
		}
			break;
		case GSObjectType.GSO_BOUNDARY: {
			mBoundaryLayer.addPointToCurrObject(mPoint, isFixFiltered);
			// TODO:: Check whether gso_boundary can be sent for boundary type.
		}
			break;
		case GSObjectType.GSO_POLYGON: {
			mPolygonLayer.addPointToCurrObject(mPoint, isFixFiltered);
		}
			break;

		default:
			break;
		}
      
	}

	/**
	 * This method is called for removing the logged point.
	 * 
	 * @param iFeatureId
	 *            Id stating to which feature it belongs to
	 * @return none
	 */
	public void removeFeature(long iFeatureId) {
		JobTransaction mTxn = mDataBase.getFeatureInfoFromTxn(iJobId,
				iFeatureId);
		long id = 0;
		if (mTxn != null) {
			id = mTxn.getFeature().getFeatureTypeId();
			int iId = (int) id;
			switch (iId) {
			case AgDataStoreResources.FEATURE_TYPE_BOUNDARY:
				mBoundaryLayer.removeObject(iFeatureId);
				break;
			case AgDataStoreResources.FEATURE_TYPE_PATH:
				mPathLayer.removeObject(iFeatureId);
				break;
			case AgDataStoreResources.FEATURE_TYPE_POLYGON:
				mPolygonLayer.removeObject(iFeatureId);
				break;
			case AgDataStoreResources.FEATURE_TYPE_POINT:
				mPointLayer.removeObject(iFeatureId);
				break;
			}
		}
		mDataBase.deleteFeatureById(iJobId, iFeatureId);
      Log.i(Constants.TAG_JOB_RECORDER,
            "removeFeature() - remove GSObject of type = " + id);
	}
   public boolean isTaskValid() {
		boolean isValidTask = false;
		List<JobTransaction> mJobTxns = mDataBase.getFeatureTxnByJobId(iJobId);
      if (mJobTxns.size() > 0) {
         for (JobTransaction jobTransaction : mJobTxns) {
            if (jobTransaction.getFeature() != null) {
               if (jobTransaction.getFeature().getVertex() == null) {
                  mDataBase.deleteFeatureById(iJobId,
                        jobTransaction.getFeatureId());
                  continue;
               } else {
                  isValidTask = true;
                  break;
               }
            } else {
               isValidTask = true;
               break;
            }
         }
      }
      Log.i(Constants.TAG_JOB_RECORDER,
            "isTaskValid() - is current task valid =  " + isValidTask);
      return isValidTask;
	}

	/**
	 * This method is called for finishing the recorded
	 * com.trimble.agmantra.job.
	 * 
	 * @param none
	 * @return none
	 */
	public boolean completeTask() {
      boolean isValidTask = false;
		if (iCurrRecShapeType != GSObjectType.GSO_NONE) {
			stopFeatureRecording(true,true);
         Log.i(Constants.TAG_JOB_RECORDER,
               "completeTask() - stopped the current recording");
		}
		flushAllLayers();
		AgJob mAgJob = mDataBase.getAgjobByJobId(iJobId);
		iFieldId = mAgJob.getFieldId();
		List<JobTransaction> mJobTxns = mDataBase.getFeatureTxnByJobId(iJobId);
		if ((null!= mJobTxns) && (mJobTxns.size() > 0)) {
			for (JobTransaction jobTransaction : mJobTxns) {
				if (jobTransaction.getFeature().getVertex() == null) {
					mDataBase.deleteFeatureById(iJobId,
							jobTransaction.getFeatureId());
					continue;
				}
            else
            {
				Feature mObj = new Feature();
				mObj.setId(jobTransaction.getFeatureId());
				mObj.setFieldId(iFieldId);
				mDataBase.updateFeature(mObj);
               isValidTask = true;
			}
         }
			// Updating the stop time of job.
			com.trimble.agmantra.entity.Job mJob = mDataBase
					.getJobInfoByJobId(iJobId);
			mJob.setEndtime(new Date());
			mJob.setStatus(AgDataStoreResources.JOB_STATUS_ENCODING);
			mDataBase.updateJob(mJob);

		} else {
			mDataBase.deleteJobById(iJobId);
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "completeTask() - task succesfully completed and update to DB");

		/*
		 * ShpFileEncoder mShpCodec = new ShpFileEncoder();
		 * mShpCodec.createBoundaryShapeFile(iJobId,
		 * "/mnt/storage/test_shp.shp"); ShapeFileLoader mShpLoader = null;
		 * mShpLoader = ShapeFileLoader.getInstance();
		 * mShpLoader.loadShpFile("/mnt/storage/test_shp.shp");
		 */
		return isValidTask;
	}

	/**
	 * This method is called for saving the recorded job.
	 * 
	 * @param none
	 * 
	 * @return none
	 */

	public void saveTask() {
		if (iCurrRecShapeType != GSObjectType.GSO_NONE) {
			setStatusOfCurrentFeatureRecording(false,false,false);
		}
		flushAllLayers();
      Log.i(Constants.TAG_JOB_RECORDER,
            "saveTask() - task succesfully saved and update to DB");
	}

	/**
	 * This method is called for deleting the recorded com.trimble.agmantra.job.
	 * 
	 * @param none
	 * @return none
	 */

	public void removeTask(boolean isCancel) {
		List<JobTransaction> mJobTxns = mDataBase.getFeatureTxnByJobId(iJobId);
		boolean isTaskValid = false;
		if (mJobTxns.size() > 0) {
			isTaskValid = true;
		}
		if (isTaskValid && isCancel) {
			saveTask();
			return;
		}
		mDataBase.deleteJobById(iJobId);
		flushAllLayers();
      Log.i(Constants.TAG_JOB_RECORDER,
            "removeTask() - task succesfully deleted and update to DB");
	}

	public void stopFeatureRecording(boolean isFinished,boolean isFeatureCorrectionReq) {
      Log.i(Constants.TAG_JOB_RECORDER,
            "stopFeatureRecording() - current running task status = "
                  + isFinished);
		setStatusOfCurrentFeatureRecording(isFinished,false,isFeatureCorrectionReq);
	}

	public void setStatusOfCurrentFeatureRecording(boolean isFinished,boolean isObjectLoaded,boolean isFeatureCorrectionReq) {
		switch (iCurrRecShapeType) {
		case GSObjectType.GSO_POLYLINE: {
			mPathLayer.setStatusOfRecordingObject(isFinished,isFeatureCorrectionReq);
			if((false == isObjectLoaded) && (true == isFinished) && (true == mPathLayer.getMcurrGSObject().isObjectTobeRefreshed()))
                        {
                           initAllLayers(getAllOverlayLayers());
                           mPathLayer.getMcurrGSObject().setObjectTobeRefreshed(false);
                        }
			if(true == isFinished)
			{
                           mPathLayer.setCurrGSObjectNull();
			}
		}
			break;
		case GSObjectType.GSO_BOUNDARY: {
			mBoundaryLayer.setStatusOfRecordingObject(isFinished,isFeatureCorrectionReq);
			if((false == isObjectLoaded) && (true == isFinished) && (true == mBoundaryLayer.getMcurrGSObject().isObjectTobeRefreshed()))
                        {
			   initAllLayers(getAllOverlayLayers());
			   mBoundaryLayer.getMcurrGSObject().setObjectTobeRefreshed(false);
                        }
                        if(true == isFinished)
                        {
                           mBoundaryLayer.setCurrGSObjectNull();
                        }
		}
			break;
		case GSObjectType.GSO_POLYGON: {
			mPolygonLayer.setStatusOfRecordingObject(isFinished,isFeatureCorrectionReq);
			if((false == isObjectLoaded) && (true == isFinished) && (true == mPolygonLayer.getMcurrGSObject().isObjectTobeRefreshed()))
                        {
                           initAllLayers(getAllOverlayLayers());
                           mPolygonLayer.getMcurrGSObject().setObjectTobeRefreshed(false);
                        }
                        if(true == isFinished)
                        {
                           mPolygonLayer.setCurrGSObjectNull();
                        }
		}
			break;
		default:
			break;
		}
		if (true == isFinished) {
			iCurrRecShapeType = GSObjectType.GSO_NONE;
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "setStatusOfCurrentFeatureRecording() - current running feature of type = "
                  + iCurrRecShapeType + "is updated with status = "
                  + isFinished);
	}

	/**
	 * This method is called for flushing the recorded layers when
	 * com.trimble.agmantra.job is finished.
	 * 
	 * @param none
	 * @return none
	 */
	private void flushAllLayers() {
		if (null != mPathLayer) {
			mPathLayer.flushObjects();
		}
		if (null != mBoundaryLayer) {
			mBoundaryLayer.flushObjects();
		}
		if (null != mPointLayer) {
			mPointLayer.flushObjects();
		}
		if (null != mPolygonLayer) {
			mPolygonLayer.flushObjects();
		}
		// Resetting the values.
		iPassId = 1;
		iAttributeId = 2;
		iPointId = 1;
		iRegionIndex = 0;
      Log.i(Constants.TAG_JOB_RECORDER,
            "flushAllLayers() - flushed all layers and resetted the values");
	}

	@Override
	public void autoCloseFeature() {
		if (mJobListener != null) {
         Log.i(Constants.TAG_JOB_RECORDER, "autoCloseFeature() - call back");
         mJobListener.autoCloseFeature();
		}

	}

	@Override
	public void featureIncompletePreviously(long iFeatureId) {
		if (mJobListener != null) {
         Log.i(Constants.TAG_JOB_RECORDER,
               "featureIncompletePreviously() - call back");
			mJobListener.featureIncompletePreviously(iFeatureId);
		}
	}

	@Override
   public void sendMapUpdate(long iFeatureId, int eObjectType, GeoPoint mPoint,
         boolean isFinished,boolean isOffsetPresent) {
		if (mJobListener != null) {
         Log.i(Constants.TAG_JOB_RECORDER, "sendMapUpdate() - call back");
         mJobListener
               .sendMapUpdate(iFeatureId, eObjectType, mPoint, isFinished,isOffsetPresent);
      }
   }

	@Override
	public void removeFeature(long iFeatureId, int eObjectType) {
		if (mJobListener != null) {
         Log.i(Constants.TAG_JOB_RECORDER, "removeFeature() - call back");
			mJobListener.removeFeature(iFeatureId, eObjectType);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trimble.agmantra.job.JobListener#initAllLayers(java.util.Vector)
	 */
	@Override
	public void initAllLayers(Vector<GSObjectLayer> vecOverlayLayers) {
		if (mJobListener != null && vecOverlayLayer != null
				&& vecOverlayLayer.size() > 0) {
         Log.i(Constants.TAG_JOB_RECORDER, "initAllLayers() - call back");
			mJobListener.initAllLayers(vecOverlayLayers);
		}

   }

	public void suppressAutoCloseCheck() {
		switch (iCurrRecShapeType) {

		case GSObjectType.GSO_POLYGON: {
			if (null != mPolygonLayer) {
				mPolygonLayer.suppressAutoCloseCheck();
			}
		}
			break;
		case GSObjectType.GSO_BOUNDARY: {
			if (null != mBoundaryLayer) {
				mBoundaryLayer.suppressAutoCloseCheck();
			}
		}
			break;
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "suppressAutoCloseCheck() - auto close check suppressed for feature type ="
                  + iCurrRecShapeType);
	}

	public boolean isFeatureValid() {
		boolean isFeatureValid = false;
		switch (iCurrRecShapeType) {
		case GSObjectType.GSO_POLYGON: {
			if (null != mPolygonLayer) {
				isFeatureValid = mPolygonLayer.isCurrObjectValid();
			}
		}
			break;
		case GSObjectType.GSO_BOUNDARY: {
			if (null != mBoundaryLayer) {
				isFeatureValid = mBoundaryLayer.isCurrObjectValid();
			}
		}
		break;
		case GSObjectType.GSO_POLYLINE: {
			if (null != mPathLayer) {
				isFeatureValid = mPathLayer.isCurrObjectValid();
			}
		}
		break;
		}
      Log.i(Constants.TAG_JOB_RECORDER,
            "isFeatureValid() - check for feature validity for feature type ="
                  + iCurrRecShapeType + "isFeatureValid = " + isFeatureValid);
		return isFeatureValid;
	}

   public boolean isFeatureCorrectionReq() {
      boolean isFeatureCorrectionReq = false;
      switch (iCurrRecShapeType) {
      case GSObjectType.GSO_POLYGON: {
              if (null != mPolygonLayer) {
                 isFeatureCorrectionReq = mPolygonLayer.getMcurrGSObject().isbInCorrectOffset();
              }
      }
              break;
      case GSObjectType.GSO_BOUNDARY: {
              if (null != mBoundaryLayer) {
                 isFeatureCorrectionReq = mBoundaryLayer.getMcurrGSObject().isbInCorrectOffset();
              }
      }
      break;
      case GSObjectType.GSO_POLYLINE: {
              if (null != mPathLayer) {
                 isFeatureCorrectionReq = mPathLayer.getMcurrGSObject().isbInCorrectOffset();
              }
      }
      break;
      }
Log.i(Constants.TAG_JOB_RECORDER,
  "isFeatureValid() - check for feature correction for feature type ="
        + iCurrRecShapeType + "isFeatureValid = " + isFeatureCorrectionReq);
      return isFeatureCorrectionReq;      
   }
   
	public BoundingBox calculateBBoxForJob() {
		final List<JobTransaction> jobTxns = mDataBase.getFeatureTxnByJobId(iJobId);
		BoundingBox jobBox =null;
		if (jobTxns == null || jobTxns.size() == 0) {
			Log.i(Constants.TAG_JOB_RECORDER, "Job transactions list null");
			return null;
		}

		Log.i("log", "calculateBBoxForJob in job");
		for (JobTransaction jobTx : jobTxns) {
			if (jobTx == null) {
				continue;
			}
			Feature feature = jobTx.getFeature();
			if (feature != null) {

				byte[] blob = feature.getVertex();
				if (blob == null) {
					continue;
				}
				Vector<FGPPoint> fgpPointsList = null;
			
					fgpPointsList = FGPCodec.getFGPPointListFromBlob(
							Constants.FGP_VERSION, blob);
				

				if (feature.getFeatureTypeId() == AgDataStoreResources.FEATURE_TYPE_POINT) {

					double dLat = -1, dLon = -1;
					for (FGPPoint fgpPoint : fgpPointsList) {
						dLon = Mercator.xToLon(fgpPoint.iX);
						dLat = Mercator.yToLat(fgpPoint.iY);
					}
					if(jobBox == null){
						jobBox = new BoundingBox((int) (dLon * 1E6), (int) (dLat * 1E6),(int) (dLon * 1E6), (int) (dLat * 1E6));
					}else  {
						jobBox.stretch((int) (dLon * 1E6), (int) (dLat * 1E6));
					}
				} else {
					
					if (jobTx.getStatus() == AgDataStoreResources.JOB_STATUS_FINISHED) {
						BoundingBox boundaryBox = new BoundingBox(
								(int)(Mercator.xToLon(feature.getTopLeftX())*1E6), (int)(Mercator.yToLat(feature.getTopLeftY())*1E6),
								(int)(Mercator.xToLon(feature.getBottomRightX())*1E6),
								(int)(Mercator.yToLat(feature.getBottomRightY())*1E6));
						if(jobBox == null){
							jobBox = boundaryBox;
						}else{
							jobBox.stretch(boundaryBox);
						}
							
						
					} else {
						double dLat = -1, dLon = -1;
						for (FGPPoint fgpPoint : fgpPointsList) {
							dLon = Mercator.xToLon(fgpPoint.iX);
							dLat = Mercator.yToLat(fgpPoint.iY);

							if(jobBox == null){
								jobBox = new BoundingBox((int) (dLon * 1E6), (int) (dLat * 1E6),(int) (dLon * 1E6), (int) (dLat * 1E6));
							}else  {							
								jobBox.stretch((int) (dLon * 1E6),
										(int) (dLat * 1E6));
							}
							
						}
					}
				}
			}

		}
		Log.i(Constants.TAG_JOB_RECORDER, "calculateBBoxForJob - box = " + jobBox.toString());
		return jobBox;
	}
}
