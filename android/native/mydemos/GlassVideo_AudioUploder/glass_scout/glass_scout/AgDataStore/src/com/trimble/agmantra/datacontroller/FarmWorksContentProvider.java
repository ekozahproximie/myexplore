package com.trimble.agmantra.datacontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.trimble.agmantra.dao.AgJobDao;
import com.trimble.agmantra.dao.AttributeEntityDao;
import com.trimble.agmantra.dao.AttributeInfoEntityDao;
import com.trimble.agmantra.dao.ClientDao;
import com.trimble.agmantra.dao.CommodityDao;
import com.trimble.agmantra.dao.CropDao;
import com.trimble.agmantra.dao.DaoMaster;
import com.trimble.agmantra.dao.DaoSession;
import com.trimble.agmantra.dao.EquipmentsDao;
import com.trimble.agmantra.dao.FarmDao;
import com.trimble.agmantra.dao.FeatureDao;
import com.trimble.agmantra.dao.FeatureTypeDao;
import com.trimble.agmantra.dao.FieldConditionDao;
import com.trimble.agmantra.dao.FieldDao;
import com.trimble.agmantra.dao.FlagCounterDao;
import com.trimble.agmantra.dao.JobDao;
import com.trimble.agmantra.dao.JobTransactionDao;
import com.trimble.agmantra.dao.JobTypeDao;
import com.trimble.agmantra.dao.JobtimingDao;
import com.trimble.agmantra.dao.LanguageDao;
import com.trimble.agmantra.dao.MappingDao;
import com.trimble.agmantra.dao.OrganizationDao;
import com.trimble.agmantra.dao.PeopleDao;
import com.trimble.agmantra.dao.PickListDao;
import com.trimble.agmantra.dao.SupplyDao;
import com.trimble.agmantra.dao.TankMixDao;
import com.trimble.agmantra.dao.TemplateTypeDao;
import com.trimble.agmantra.dao.UnitsDao;
import com.trimble.agmantra.dao.UserDao;
import com.trimble.agmantra.dao.WhetherDao;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.dbutil.CustomDevOpenHelper;
import com.trimble.agmantra.dbutil.Log;
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
import com.trimble.agmantra.entity.Mapping;
import com.trimble.agmantra.entity.Organization;
import com.trimble.agmantra.entity.PickList;
import com.trimble.agmantra.entity.TemplateType;
import com.trimble.agmantra.entity.Units;
import com.trimble.agmantra.entity.User;

import de.greenrobot.dao.QueryBuilder;

import java.io.File;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;

// TODO: Auto-generated Javadoc
/**
 * The Class FarmWorksContentProvider.
 */
public class FarmWorksContentProvider {

      private static final String TAG = FarmWorksContentProvider.class.getSimpleName();
      
	public static final long UNDEFINED_VAL = -1;

	public static final int COUNTER_VAL = 1;

	private SQLiteDatabase db;

	private DaoMaster daoMaster;

	private DaoSession daoSession;

	private ClientDao clientDao;

	private FarmDao farmDao;

	private FieldDao fieldDao;

	private JobDao jobDao;

	private JobTypeDao jobTypeDao;

	private JobTransactionDao jobTransactionDao;

	private JobtimingDao jobtimingDao;

	private AgJobDao agJobDao;

	private TemplateTypeDao templateTypeDao;

	// Attributes

	private AttributeEntityDao attributeEntityDao;

	private AttributeInfoEntityDao attributeInfoEntityDao;

	private PickListDao pickListDao;

	private FeatureDao featureDao;

	private FeatureTypeDao featureTypeDao;
	private FlagCounterDao flagCounterDao;

	private CropDao cropDao;

	private CommodityDao commodityDao;

	private UnitsDao unitsDao;

	private MappingDao mappingDao;

	private EquipmentsDao mEquipmentsDao = null;

	private SupplyDao mSupplyDao = null;

	private TankMixDao mTankMixDao = null;

	private PeopleDao mPeopleDao = null;

	private WhetherDao mWhetherDao = null;

	private UserDao mUserDao = null;
	
	private OrganizationDao mOrganizationDao = null;
	
	private FieldConditionDao mFieldConditionDao = null;

	private LanguageDao mLanguageDao = null;

	private DaoWrapper daoWrapper = null;

	private static FarmWorksContentProvider farmWorksContentProvider = null;
	private static final String tag = "AgDataStore";

	/**
	 * Instantiates a new farm works sqllite manager.
	 * 
	 * @param context
	 *            the context
	 */
	private static final String TEMPLATE_STATE = "templatestate";
	private static final String PROJECT_NAME = "projectid";
	private static final String UNKNOWN_FIELD_ID = "FIELDID";
	private static final String UNKNOWN_CROP_ID = "CROPID";
	public static final String UNKNOWN = "Unknown";
	public static final String NONE = "None";
	private Field unKnownField = null;
	private Crop unKnownCrop = null;
	private static final int D_TEMPLATE_NOT_STOREED = 0;

	private static final int D_TEMPLATE_STOREED = 1;

	public static final long D_UNKNOWN_CFFE_ID = 2147483648l;

	private Context context = null;

	private CustomDevOpenHelper devOpenHelper = null;
	public static final long DEFAULT_PROJECT_NAME = 0x10000000;

	private static final boolean D_DB_CLAER_STATE = true;
	private static final String DB_CLAER_STATE = "dbclearstate";

	private FarmWorksContentProvider(Context context) {
		this.context = context;
		if (CustomDevOpenHelper.IS_STORE_ON_SD) {

			final boolean isNotInitizlied = !isTemeplateStored(context);

			if (isNotInitizlied && CustomDevOpenHelper.IS_STORE_ON_SD) {
				deleteOldDB(CustomDevOpenHelper.DB_FULL_PATH);
			}

			IntentFilter iFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
			iFilter.addDataScheme("file");
			context.registerReceiver(mMountReceiver, iFilter);

			IntentFilter iFilter2 = new IntentFilter(Intent.ACTION_MEDIA_EJECT);
			iFilter.addDataScheme("file");
			context.registerReceiver(mUnmountReceiver, iFilter2);
			if (!Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				Log.i("FarmWorksContentProvider", "initAll NOT DONE!");
				return;
			}
		}
		initVariables();
		// initAll();

	}

	public boolean isDBupdated() {
		return devOpenHelper.isUpdateNeeded();
	}
	
	public void setDBupdated(final boolean isDbUpdated) {
		 devOpenHelper.setUpdateNeeded(isDbUpdated);
	}
	public boolean isEULADisplayNeed() {
		return devOpenHelper.isEULAUpdateNeed();
	}

	private BroadcastReceiver mMountReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
				Log.i("FarmWorksContentProvider",
						"mMountReceiver onReceive called!");
				initAll();
			}
		}
	};

	private BroadcastReceiver mUnmountReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
				Log.i("FarmWorksContentProvider",
						"unMountReceiver onReceive called!");
				if (CustomDevOpenHelper.IS_STORE_ON_SD) {

					closeDB();
				}
			}
		}
	};

	private void closeDB() {
		if (devOpenHelper != null) {
			devOpenHelper.close();
			Log.i("FarmWorksContentProvider", "devOpenHelper dbClosed!");
		}

	}

	/*
	 * private Long getUnknownFieldID() { Long id = -1L; SharedPreferences
	 * preferences = PreferenceManager .getDefaultSharedPreferences(context); id
	 * = preferences.getLong(UNKNOWN_FIELD_ID, -1); return id; }
	 * 
	 * private void setUnknownFieldID(Long lFieldID) {
	 * 
	 * SharedPreferences preferences = PreferenceManager
	 * .getDefaultSharedPreferences(context); Editor editor =
	 * preferences.edit(); editor.putLong(UNKNOWN_FIELD_ID, lFieldID);
	 * editor.commit(); }
	 */

	public Long getUnknownCropID() {
		Long id = -1L;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		id = preferences.getLong(UNKNOWN_CROP_ID, -1);
		return id;
	}

	private void setUnknownCropID(Long lCropID) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putLong(UNKNOWN_CROP_ID, lCropID);
		editor.commit();

	}

	public void close() {
		if (CustomDevOpenHelper.IS_STORE_ON_SD) {
			try {
				context.unregisterReceiver(mUnmountReceiver);
				context.unregisterReceiver(mMountReceiver);
			} catch (IllegalArgumentException e) {
				// ignore
			}
		}
		// closeDB();
		// farmWorksContentProvider = null;
	}

	public void initAll() {
	   Log.i(TAG, "initALL template store started");
		final boolean isNotInitizlied = !isTemeplateStored(context);
		Log.i(TAG, " is Temeplate Not Initizlied:"+isNotInitizlied);
		if (isNotInitizlied && CustomDevOpenHelper.IS_STORE_ON_SD) {
			deleteOldDB(CustomDevOpenHelper.DB_FULL_PATH);
			initVariables();
		}

		/*
		 * boolean isDeleted = false; if (CustomDevOpenHelper.IS_STORE_ON_SD) {
		 * File file = new File(CustomDevOpenHelper.DB_FULL_PATH); if
		 * (!file.exists()) { isDeleted = true; } }
		 */

		boolean isUpdateNeeded = devOpenHelper.isUpdateNeeded();
		Log.i(TAG, " db upated:"+isUpdateNeeded);
		if (isUpdateNeeded || isNotInitizlied /*
											 * ||
											 * (CustomDevOpenHelper.IS_STORE_ON_SD
											 * && isDeleted)
											 */) {

			/*
			 * final ProgressDialog progress = new ProgressDialog(context,
			 * R.style.DefaultProgressDialogTheme);
			 * progress.setMessage(context.getString(R.string.db_update));
			 * progress.setCancelable(false); progress.show(); new Thread() {
			 * 
			 * @Override public void run() {
			 */
			if(devOpenHelper.isUpdateInAttributeEntity()){
				devOpenHelper.updateAttributeTableID();
			}
			storeResource(isNotInitizlied);
			/*
			 * progress.dismiss();
			 * 
			 * 
			 * } }.start();
			 */
		}
		Log.i(TAG, "initALL template store finished");
	}

	private void initVariables() {
		devOpenHelper = new CustomDevOpenHelper(context);

		db = devOpenHelper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();

		clientDao = daoSession.getClientDao();
		farmDao = daoSession.getFarmDao();
		fieldDao = daoSession.getFieldDao();
		jobDao = daoSession.getJobDao();
		jobTypeDao = daoSession.getJobTypeDao();
		jobTransactionDao = daoSession.getJobTransactionDao();
		jobtimingDao = daoSession.getJobtimingDao();
		featureDao = daoSession.getFeatureDao();
		pickListDao = daoSession.getPickListDao();
		attributeEntityDao = daoSession.getAttributeEntityDao();
		attributeInfoEntityDao = daoSession.getAttributeInfoEntityDao();
		agJobDao = daoSession.getAgJobDao();
		templateTypeDao = daoSession.getTemplateTypeDao();
		featureTypeDao = daoSession.getFeatureTypeDao();
		flagCounterDao = daoSession.getFlagCounterDao();
		cropDao = daoSession.getCropDao();
		commodityDao = daoSession.getCommodityDao();
		unitsDao = daoSession.getUnitsDao();
		mappingDao = daoSession.getMappingDao();

		mEquipmentsDao = daoSession.getEquipmentsDao();
		mSupplyDao = daoSession.getSupplyDao();
		mTankMixDao = daoSession.getTankMixDao();
		mPeopleDao = daoSession.getPeopleDao();
		mWhetherDao = daoSession.getWhetherDao();
		mFieldConditionDao = daoSession.getFieldConditionDao();
		mLanguageDao = daoSession.getLanguageDao();
		mUserDao = daoSession.getUserDao();
		mOrganizationDao = daoSession.getOrganizationDao();

		daoWrapper = new DaoWrapper(this);
	}

	private void storeResource(boolean isNotInitizlied) {

		AgDataStoreResources resources = new AgDataStoreResources(this);
		android.util.Log.i(TAG, "storeResource insertAllResource started");
		resources.insertAllResource();
		android.util.Log.i(TAG, "storeResource insertAllResource finished");
		if (isNotInitizlied) {
			storeTemeplateState();
			storeProjectId();
		}
	}

	public boolean deleteOldDB(String stPath) {
		File file = new File(stPath);
		boolean isDeleted = false;
		if (file.exists()) {
			isDeleted = file.delete();
		}

		file = null;
		return isDeleted;
	}

	public boolean isTemeplateStored(Context context) {
		boolean isStroed = false;
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		int iState = preference.getInt(TEMPLATE_STATE, D_TEMPLATE_NOT_STOREED);
		isStroed = (iState == D_TEMPLATE_STOREED);
		android.util.Log.i(TAG, "isTemeplateStored "+isStroed);
		return isStroed;
	}

	private void storeTemeplateState() {
	        android.util.Log.i(TAG, "storeTemeplateState "+TEMPLATE_STATE);
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preference.edit();
		editor.putInt(TEMPLATE_STATE, D_TEMPLATE_STOREED);
		editor.commit();
	}

	public boolean isDBClearAndSaved(final Context context) {
		if (context == null) {
			return true;
		}
		final SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		final boolean isClearAndStroed = preference.getBoolean(DB_CLAER_STATE,
				D_DB_CLAER_STATE);

		return isClearAndStroed;
	}

	public void setDBClearAndSaved(final Context context, final boolean isSave) {
		if (context == null) {
			return;
		}
		final SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		final Editor editor = preference.edit();
		editor.putBoolean(DB_CLAER_STATE, isSave);
		editor.commit();
	}

	private void resetTemeplateState() {
	   android.util.Log.i(TAG, "resetTemeplateState stored");
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preference.edit();
		editor.putInt(TEMPLATE_STATE, D_TEMPLATE_NOT_STOREED);
		editor.commit();
	}

	public void clearCache() {

		resetTemeplateState();
		deleteAllTableContent();
		// DaoMaster.dropAllTables(db, true);
		// closeDb();
		// devOpenHelper.deleteDatabase(context);
		initAll();
		// setDBClearAndSaved(context, true);
	}

	private void storeProjectId() {

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preference.edit();
		editor.putLong(PROJECT_NAME, DEFAULT_PROJECT_NAME);
		editor.commit();
	}

	public long getProjectId() {
		final SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		long iprojectId = preference
				.getLong(PROJECT_NAME, DEFAULT_PROJECT_NAME);

		return iprojectId;
	}

	public void updateProjectId(long iprojectId) {

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preference.edit();
		editor.putLong(PROJECT_NAME, iprojectId);
		editor.commit();
	}

	public Cursor getQueryCursor(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		Cursor cursor = null;
		cursor = db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
		return cursor;
	}

	public Cursor getQueryCursor(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {
		Cursor cursor = null;
		cursor = db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy, limit);
		return cursor;
	}

	public Cursor getQueryCursor(boolean distinct, String table,
			String[] columns, String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy, String limit) {
		Cursor cursor = null;
		cursor = db.query(distinct, table, columns, selection, selectionArgs,
				groupBy, having, orderBy, limit);
		return cursor;
	}

	public Cursor getQueryCursorForTemplateType() {
		Cursor cursor = null;
		cursor = db.query(TemplateTypeDao.TABLENAME, null, null, null, null,
				null, null);
		return cursor;
	}

	public Cursor getQueryCursorForAttributeInfo() {
		Cursor cursor = null;
		cursor = db.query(AttributeEntityDao.TABLENAME, null, null, null, null,
				null, null);
		return cursor;
	}

	public Cursor getQueryCursorForPickList() {
		Cursor cursor = null;
		cursor = db.query(PickListDao.TABLENAME, null, null, null, null, null,
				null);
		return cursor;
	}

	/**
	 * Gets the db.
	 * 
	 * @return the db
	 */
	public SQLiteDatabase getDb() {
		return db;
	}

	/**
	 * Close db.
	 */
	private void closeDb() {
		db.close();
	}

	/**
	 * Gets the single instance of FarmWorksContentProvider.
	 * 
	 * @param context
	 *            the context
	 * @return single instance of FarmWorksContentProvider
	 */
	public static synchronized FarmWorksContentProvider getInstance(
			Context context) {

		if (farmWorksContentProvider == null) {

			farmWorksContentProvider = new FarmWorksContentProvider(context);
		}

		return farmWorksContentProvider;

	}

	/************************************************************************************************************************************
	 * CFFE OPERATIONS * *
	 *************************************************************************************************************************************/
	public List<Client> getAllClientList() {

		return daoWrapper.getClientQb(clientDao).list();
	}

	public List<Farm> getFarmListByClientId(long id) {

		QueryBuilder<Farm> farmQb = daoWrapper.getFarmQb(farmDao, id);

		if (farmQb == null) {
			return null;
		}

		return farmQb.orderAsc(FarmDao.Properties.Desc).list();
	}

	public List<Field> getFieldListByFarmId(long id) {
		return daoWrapper.getFieldQb(fieldDao, id).list();
	}

	public List<Field> getAllFieldsList() {
		return daoWrapper.getFieldQb(fieldDao).list();
	}

	public List<Farm> getAllFarmList() {
		return daoWrapper.getFarmAllQb(farmDao).list();
	}

	public long insertClient(Client client) {
		return clientDao.insert(client);
	}

	public long insertFarm(Farm farm) {
		return farmDao.insert(farm);
	}

	public long insertUnit(Units units) {
		return unitsDao.insert(units);
	}

	public AgJob getNewAgJob(String stJobType) {

		long iJobId = generateJobId();
		Long unKnownFieldId = D_UNKNOWN_CFFE_ID;
		Long unKnownCropId = getUnknownCropID();
		if (unKnownFieldId != -1) {
			unKnownField = fieldDao.load(unKnownFieldId);
		}
		if (unKnownCropId != -1) {
			unKnownCrop = cropDao.load(unKnownCropId);
		}

		AgJob agJob = new AgJob();
		if (unKnownCrop == null) {
			unKnownCrop = getUnKnownCrop();
			setUnknownCropID(unKnownCrop.getId());
		}

		Crop unknownCrop = unKnownCrop;
		if (unKnownField == null) {
			unKnownField = getUnKnownField();
			// setUnknownFieldID(unKnownField.getId());
		}
		Field field = unKnownField;
		agJob.setField(field);

		agJob.setJobId(iJobId);
		long lJobType = 0;
		List<com.trimble.agmantra.entity.JobType> mJobTypes = getAllJobType();

		for (com.trimble.agmantra.entity.JobType jobType : mJobTypes) {

			if (jobType.getName().equalsIgnoreCase(stJobType)) {
				lJobType = jobType.getId();
				break;
			} else if (jobType.getName().equalsIgnoreCase(stJobType)) {
				lJobType = jobType.getId();
				break;
			}
		}

		agJob.setJobTypeId(lJobType);
		agJob.setCropId(unknownCrop.getId());
		agJob.setCropdesc(UNKNOWN);
		long insertId = insertAgJobInfo(agJob);
		Log.i(tag, "total job count:" + insertId);

		return agJob;

	}

	public Crop getUnKnownCrop() {
		long id = 0;
		do {
			id = getRandomID();
			Log.i("AgDataStore", "cropid generated = " + id);
		} while (((id > 0x10000000) && (id < 0xF0000000))
				&& (cropDao.load(id) != null));

		Crop crop = new Crop(id);
		crop.setDesc(UNKNOWN);
		crop.setStatus(AgDataStoreResources.STATUS_AUTOGENERATED);
		cropDao.insert(crop);
		Log.i("AgDataStore", "cropid inserted = " + id);
		return crop;
	}

	private int getRandomID() {
		int id = new SecureRandom().nextInt();
		id = Math.abs(id);
		return id;
	}

	public Field getUnKnownField() {
		// long id = 0;
		/*
		 * do { id = (long) getRandomID(); Log.i(tag, "fieldid generated = " +
		 * id); } while (((id > 0x10000000) && (id < 0xF0000000)) &&
		 * (fieldDao.load(id) != null));
		 */

		Field field = new Field(D_UNKNOWN_CFFE_ID);
		field.setDesc(UNKNOWN);
		field.setStatus(AgDataStoreResources.STATUS_AUTOGENERATED);
		Client client = getUnKnownClient();
		Farm farm = getUnKnownFarm(client);
		field.setFarm(farm);
		insertField(field);
		Log.i(tag, "fieldid inserted = " + field.getId());
		return field;
	}

	private Farm getUnKnownFarm(Client client) {
		/*
		 * long id = 0; do { id = getRandomID(); Log.i(tag,
		 * "farmid generated = " + id); } while (((id > 0x10000000) && (id <
		 * 0xF0000000)) && (farmDao.load(id) != null));
		 */

		Farm farm = new Farm(D_UNKNOWN_CFFE_ID);
		farm.setDesc(UNKNOWN);
		farm.setStatus(AgDataStoreResources.STATUS_AUTOGENERATED);
		farm.setClient(client);
		insertFarm(farm);
		Log.i(tag,
				"farmid inserted = " + farm.getId() + "to Client"
						+ client.getId());
		return farm;
	}

	private Client getUnKnownClient() {
		Client client = new Client(D_UNKNOWN_CFFE_ID);
		client.setDesc(UNKNOWN);
		client.setStatus(AgDataStoreResources.STATUS_AUTOGENERATED);
		insertClient(client);
		Log.i(tag, "clientid inserted = " + client.getId());
		return client;
	}

	public long insertField(Field field) {
		return fieldDao.insert(field);
	}

	/************************************************************************************************************************************
	 * JOB OPERATIONS * *
	 *************************************************************************************************************************************/

	private long generateJobId() {
		return daoWrapper.insertJobid(jobDao);
	}

	public long generateFeatureId(long jobId, long featuretypeId,
			boolean isUpdateJobTxn) {

		long featureid = UNDEFINED_VAL;

		if (jobId != UNDEFINED_VAL && featuretypeId != UNDEFINED_VAL) {
			featureid = daoWrapper.insertFeatureinfo(featureDao,
					jobTransactionDao, jobId, featuretypeId, isUpdateJobTxn);
		}

		return featureid;
	}

	public boolean updateJobTxn(JobTransaction jobTransaction,
			boolean isTemplateTypeUpdate) {
		boolean checkupdate = false;

		if (null != jobTransaction.getJobId()
				&& null != jobTransaction.getFeatureId()
				&& jobTransaction.getJobId() != UNDEFINED_VAL
				&& jobTransaction.getFeatureId() != UNDEFINED_VAL) {

			daoWrapper.updateJobTxn(jobTransactionDao, flagCounterDao,
					jobTransaction, isTemplateTypeUpdate);
			checkupdate = true;
		}

		return checkupdate;
	}

	public void updateFeatureVertex(long jobid, long featureid, byte[] vertex) {
		daoWrapper.updateFeatureVertex(featureDao, jobid, featureid, vertex);
	}

	public AgJob getAgjobByJobId(long id) {
		return daoWrapper.getAgJobbyJobId(agJobDao, id);
	}

	public boolean checkUnfinishedAgJobbyFieldID(long fieldId) {
		return daoWrapper.checkUnfinishedAgJobbyFieldID(jobDao, agJobDao,
				fieldId);
	}

	public List<JobTransaction> getFeatureTxnByJobId(long id) {
		return daoWrapper.getFeatureTransactionByJobId(jobTransactionDao, id);
	}

	public List<Feature> getFeaturesByFieldId(long id) {
		return daoWrapper.getFeaturesByFieldId(featureDao, id);
	}

	public List<Feature> getFeaturesByFieldId(long lFieldID, long lFeatureTypeID) {
		return daoWrapper.getFeaturesByFieldId(featureDao, lFieldID,
				lFeatureTypeID);
	}

	public List<Feature> getAllBoundaryFeatures() {
           return daoWrapper.getAllBoundaries(featureDao);
   }
	
	public List<JobTransaction> getFeatureInfoByTTId(long lJobID,
			long lTemplateid) {
		return daoWrapper.getFeatureInfoByTTId(jobTransactionDao, lJobID,
				lTemplateid);
	}

	public JobTransaction getFeatureInfoFromTxn(long jobId, long featureId) {
		return daoWrapper.getFeatureInfoFromTxn(jobTransactionDao, jobId,
				featureId);
	}

	public long getTemplateTypeFromTxn(long featureId) {
		return daoWrapper.getTemplateTypeFromTxn(jobTransactionDao, featureId);

	}

	// child to parent deletion
	public boolean deleteFeatureById(long jobId, long featureId) {

		boolean isDeleted = false;

		List<JobTransaction> list = getFeatureTxnByJobId(jobId);

		for (JobTransaction jobTrans : list) {

			if (jobTrans.getFeatureId() == featureId
					&& jobTrans.getJobId() == jobId) {
				jobTransactionDao.delete(jobTrans);
				featureDao.deleteByKey(featureId);

				long attrkey = daoWrapper.getAttrKey(attributeEntityDao,
						featureId);
				attributeEntityDao.deleteByKey(attrkey);
				isDeleted = true;

			}

		}

		return isDeleted;

	}

	// child to parent deletion
	public boolean deleteJobById(long jobId) {

		boolean isDeleted = false;

		List<JobTransaction> listTxn = getFeatureTxnByJobId(jobId);

		if (null != listTxn && !listTxn.isEmpty()) {
			for (JobTransaction jobTrans : listTxn) {

				if (jobTrans.getJobId() == jobId) {
					jobTransactionDao.delete(jobTrans);
					featureDao.deleteByKey(jobTrans.getFeatureId());
					long attrkey = daoWrapper.getAttrKey(attributeEntityDao,
							jobTrans.getFeatureId());
					attributeEntityDao.deleteByKey(attrkey);
					long agJobKey = daoWrapper.getAgJobKey(agJobDao, jobId);
					agJobDao.deleteByKey(agJobKey);
					isDeleted = true;

				}

			}
		}
		jobDao.deleteByKey(jobId);
		return isDeleted;

	}

	// child to parent deletion
	protected boolean deleteAllJobById(long jobId) {

		boolean isDeleted = false;
		AgJob agJob = null;
		long lFieldID = 0;
		boolean isDeleteFieldboundary = false;
		List<JobTransaction> listTxn = getFeatureTxnByJobId(jobId);

		if (null != listTxn && !listTxn.isEmpty()) {
			for (JobTransaction jobTrans : listTxn) {

				if (jobTrans.getJobId() == jobId) {
					jobTransactionDao.delete(jobTrans);
					List<FlagCounter> templist = daoWrapper
							.getFlagCounterByJobId(flagCounterDao,
									jobTrans.getJobId());
					if (null != templist && !templist.isEmpty()) {

						flagCounterDao.deleteInTx(templist);
					}
					long attrkey = daoWrapper.getAttrKey(attributeEntityDao,
							jobTrans.getFeatureId());
					featureDao.deleteByKey(jobTrans.getFeatureId());
					attributeEntityDao.deleteByKey(attrkey);
					long agJobKey = daoWrapper.getAgJobKey(agJobDao, jobId);
					agJob = new AgJob();
					agJob = agJobDao.load(agJobKey);
					if (null != agJob) {
						// Store the field id
						lFieldID = agJob.getFieldId();
						// Query for the unfinished job from the agjob table
						// pertaining to the field id of the current deleted job
						if (!checkUnfinishedAgJobbyFieldID(lFieldID)) {
							fieldDao.deleteByKey(lFieldID);
							farmDao.deleteByKey(agJob.getField().getFarmId());
							clientDao.deleteByKey(agJob.getField().getFarm()
									.getClientId());
							isDeleteFieldboundary = true;
						}
					}

					agJobDao.deleteByKey(agJobKey);
					jobDao.deleteByKey(jobId);
					isDeleted = true;

				}

			}
		}

		// Clean the features pertaining to the fieldid from the feature table.
		if (0 != lFieldID && isDeleteFieldboundary) {
			List<Feature> featlist = getFeaturesByFieldId(lFieldID);
			if (null != featlist && 0 != featlist.size()) {
				for (Feature feature : featlist) {
					featureDao.deleteByKey(feature.getId());
				}
			}
		}

		return isDeleted;

	}

	public boolean isFeatureExist(long id) {

		return daoWrapper.isFeatureExist(featureDao, id);
	}

	public List<Job> getUnfinishedJobs() {
		if (daoWrapper == null) {
			return null;
		}
		return daoWrapper.getUnfinshedJobs(jobDao);
	}

	public List<Job> getFinishedJobs() {
		if (daoWrapper == null) {
			return null;
		}
		return daoWrapper.getFinshedJobs(jobDao);
	}

	public List<Job> getJobsToBeUploaded() {
		if (daoWrapper == null) {
			return null;
		}
		return daoWrapper.getJobsToBeUploaded(jobDao);
	}

	public Job getJobInfoByJobId(long id) {
		return daoWrapper.getJobByJobId(jobDao, id);
	}

	public void updateJob(Job job) {
		daoWrapper.updateJob(jobDao, job);
	};

	public void insertjobTypeTxn(List<JobType> jobType) {
	        Log.i(TAG, "insertjobTypeTxn"); 
		jobTypeDao.insertInTx(jobType);
		Log.i(TAG, "insertjobType completed");
	}

	public void deleteAllJobType() {
	        Log.i(TAG, "deleteAllJobType"); 
		jobTypeDao.deleteAll();
	}

	public void insertLanguageList(List<Language> languages) {
	        Log.i(TAG, "insertLanguageList");
		mLanguageDao.insertInTx(languages);
		  Log.i(TAG, "insertLanguageList completed");
	}

	public void deleteAllLanguage() {
	   Log.i(TAG, "deleteAllLanguage");
		mLanguageDao.deleteAll();
	}

	public List<Language> getAllLanguage() {
		return daoWrapper.getAllLanguageList(mLanguageDao);
	}

	public boolean isLanguageExist(String stLanguage) {
		return daoWrapper.isLanguageExist(mLanguageDao, stLanguage);
	}

	/*
	 * public void updateJobType(JobType jobType) { jobTypeDao.update(jobType);
	 * }
	 */

	public List<JobType> getAllJobType() {
		return daoWrapper.getAllJobType(jobTypeDao);
	}

	public long insertAgJobInfo(AgJob agJob) {
		return agJobDao.insert(agJob);
	}

	public boolean updateAgjobInfo(long jobId, AgJob agJob) {

		return daoWrapper.updateAgJob(agJobDao, jobId, agJob);
	}

	public void updateField(Field field) {
		daoWrapper.updateField(fieldDao, field);
	}

	public void updateClient(Client client) {
		clientDao.update(client);
	}

	public void updateFarm(Farm farm) {
		farmDao.update(farm);
	}

	public void updateFeature(Feature feature) {
		daoWrapper.updateFeature(featureDao, feature);
	}

	/**
	 * Insert attribute.
	 * 
	 * @param attributeEntity
	 *            the attribute entity
	 * @return the long
	 */
	public void insertAttribute(List<AttributeEntity> attributeEntity,
			long jobId, long templateTypeId) {

		daoWrapper.insertAttribute(attributeEntityDao, flagCounterDao,
				attributeEntity, jobId, templateTypeId, COUNTER_VAL);

	}

	/**
	 * Update attribute.
	 * 
	 * @param attributeEntity
	 *            the attribute entity
	 */
	/*
	 * public void updateAttribute(AttributeEntity attributeEntity) {
	 * attributeEntityDao.update(attributeEntity); }
	 */

	/**
	 * Delete attribute.
	 * 
	 * @param attributeEntity
	 *            the attribute entity
	 */
	/*
	 * public void deleteAttribute(AttributeEntity attributeEntity) {
	 * attributeEntityDao.deleteByKey(attributeEntity.getId()); }
	 */

	/**
	 * Insert attribute info.
	 * 
	 * @param attributeInfoEntity
	 *            the attribute info entity
	 * @return the long
	 */
	public void insertAttributeInfo(
			List<AttributeInfoEntity> attributeInfoEntity) {
	         Log.i(TAG, "insert  Attribute Info Entity ");
		attributeInfoEntityDao.insertInTx(attributeInfoEntity);
		Log.i(TAG, "insert  Attribute Info Entity completed");
		
	}

	/**
	 * Delete attribute info.
	 * 
	 * @param attributeInfoEntity
	 *            the attribute info entity
	 */
	public void deleteAttributeInfo(AttributeInfoEntity attributeInfoEntity) {
		attributeInfoEntityDao.deleteByKey(attributeInfoEntity.getId());
	}

	public void deleteAllAttributeInfo() {
	   Log.i(TAG, "delete all Attribute Info Entity type");
		attributeInfoEntityDao.deleteAll();
	}

	/**
	 * Insert template type.
	 * 
	 * @param templateType
	 *            the template type
	 * @return the long
	 */
	public void insertTemplateType(List<TemplateType> templateType) {
	        Log.i(TAG, "insertTemplateType");
		templateTypeDao.insertInTx(templateType);
		Log.i(TAG, "insertTemplateType completed");
	}

	public void deleteAllTemplateType() {
	   Log.i(TAG, "delete all template type");
		templateTypeDao.deleteAll();
	}

	public void insertFeatureType(List<FeatureType> featureType) {
	   Log.i(TAG, "insertFeatureType");
		featureTypeDao.insertInTx(featureType);
		Log.i(TAG, "insertFeatureType completed");
	}

	public void deleteAllFeatureType() {
	   Log.i(TAG, "deleteAllFeatureType");
		featureTypeDao.deleteAll();
	}

	public Farm getFieldIdByName(String name) {

		return daoWrapper.getfarmDetailsById(farmDao, name);

	}

	public List<FeatureType> getFeatureType() {
		return daoWrapper.getAllFeatureType(featureTypeDao);
	}

	public long updatePauseTime(long jobId) {
		Jobtiming jobtiming = new Jobtiming();
		jobtiming.setJobId(jobId);
		jobtiming.setPauseTime(new Date());
		jobtiming.setStatus(0);
		return jobtimingDao.insert(jobtiming);
	}

	public void updateResumeTime(long jobId) {
		daoWrapper.updateResumeTime(jobtimingDao, jobId);

	}

	/************************************************************************************************************************************
	 * TEMPLATE OPERATIONS * *
	 *************************************************************************************************************************************/
	public List<TemplateType> getTemplateList(int iFeatureType, int iJobType,
			String stLanguage) {
		return daoWrapper.getAllTemplateList(templateTypeDao, iFeatureType,
				iJobType, stLanguage);
	}

	public List<AttributeInfoEntity> getAttributeInfolistByTTId(long id) {

		return daoWrapper.getAttrInfoByTTId(attributeInfoEntityDao, id);

	}

	public void updateAttributeInfo(AttributeInfoEntity attributeInfoEntity) {
		if (attributeInfoEntity != null) {
			attributeInfoEntityDao.update(attributeInfoEntity);
		}

	}

	public List<PickList> getPickListByAttrInfoId(long attrInfoId,
			boolean isOrderByAsc, boolean isNoneTobeAppended) {
		return daoWrapper.getPickList(pickListDao, attrInfoId, isOrderByAsc,
				isNoneTobeAppended);
	}

	public List<AttributeEntity> getAttributesByAttrInfoId(long id) {

		return daoWrapper.getAttributeEntityList(attributeEntityDao, id);
	}

	public long insertPicklist(PickList pickList) {
		return pickListDao.insert(pickList);
	}

	public void insertPicklistTxn(List<PickList> picklist) {
	        Log.i(TAG, "insertPicklist "); 
		pickListDao.insertInTx(picklist);
		Log.i(TAG, "insertPicklist completed");
	}

	/*
	 * public void updatePickList(PickList pickList) {
	 * pickListDao.update(pickList); }
	 */

	public void deletePickList(PickList pickList) {
		pickListDao.deleteByKey(pickList.getId());
	}

	public void deleteAllPickList() {
   	        Log.i(TAG, "deleteAllPickList"); 
		pickListDao.deleteAll();
	}

	public void deleteAllMapping() {
	        Log.i(TAG, "deleteAllMapping"); 
		mappingDao.deleteAll();
	}

	public long getKeyByFeatureId(long id) {
		return daoWrapper.getAttrKey(attributeEntityDao, id);
	}

	// Atributes values for the feature

	public List<AttributeEntity> getAttributesByFeatureId(long id) {

		return daoWrapper.getAttribsByFeatureId(attributeEntityDao, id);

	}

	public void deleteAttrByFeatureID(long id) {
		daoWrapper.deleteAttrByFeatureId(attributeEntityDao, id);
	}

	public List<AttributeEntity> getAttribsByFeatureIdAsasc(long id) {

		return daoWrapper.getAttribsByFeatureIdAsasc(attributeEntityDao, id);

	}

	public long getTemplateCounterForJob(long jobId, long templateTypeId) {
		long counterVal = -1;
		FlagCounter counter = daoWrapper.getTemplateCounter(flagCounterDao,
				jobId, templateTypeId);

		if (null != counter && null != counter.getCount()) {
			counterVal = counter.getCount();
		}
		return counterVal;
	}

	public void updateAttrByFeatureId(long featureId, long jobId,
			long templateTypeId, List<AttributeEntity> tempAttrUpdatelist) {
		daoWrapper.updateAttrByFeatureId(attributeEntityDao, flagCounterDao,
				featureId, jobId, templateTypeId, tempAttrUpdatelist,
				COUNTER_VAL);
	}

	public Client insertNewClient(String clientDesc) {
		long id = 0;
		do {
			id = getRandomID();
			Log.i(tag, "new clientid generated = " + id);
		} while (((id > 0x10000000) && (id < 0xF0000000))
				&& (clientDao.load(id) != null));

		Client client = new Client(id);
		client.setDesc(clientDesc);
		client.setStatus(0);
		insertClient(client);
		Log.i(tag, "clientid inserted = " + client.getId());
		return client;
	}

	public Farm insertNewFarm(long clientId, String farmDesc) {
		long id = 0;
		do {
			id = getRandomID();
			Log.i(tag, " new farmid generated = " + id);
		} while (((id > 0x10000000) && (id < 0xF0000000))
				&& (farmDao.load(id) != null));

		Farm farm = new Farm(id);
		farm.setDesc(farmDesc);
		farm.setStatus(0);
		farm.setClientId(clientId);
		insertFarm(farm);
		Log.i(tag, "farmid inserted = " + farm.getId() + "to Client" + clientId);
		return farm;
	}

	public Field insertNewField(long farmId, String fieldDesc) {
		long id = 0;
		do {
			id = getRandomID();
			Log.i(tag, " new Field generated = " + id);
		} while (((id > 0x10000000) && (id < 0xF0000000))
				&& (fieldDao.load(id) != null));

		Field field = new Field(id);
		field.setDesc(fieldDesc);
		field.setStatus(0);
		field.setFarmId(farmId);
		insertField(field);
		Log.i(tag, "field inserted = " + field.getId() + "to Farm" + farmId);
		return field;
	}

	/**
	 * Gets the processing_ time.
	 * 
	 * @param startTime
	 *            the start time
	 * @return the processing_ time
	 */
	public long getProcessing_Time(long startTime) {

		long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Processing Time:" + endTime + "ms");
		return endTime;
	}

	public long insertClient_fls(Client client) {
		long insertCheck = -1;
		if (null != client) {

			Client tempclient = new Client();
			tempclient = clientDao.load(client.getId());
			if (null == tempclient) {
				Log.i(tag, "client not exist in DB insertion started = "
						+ client.getId());
				insertCheck = clientDao.insert(client);
			} else {
				Log.i(tag,
						"client  exist in DB updation started = "
								+ client.getId());
				clientDao.update(client);
				insertCheck = tempclient.getId();
			}
		}
		return insertCheck;

	}

	public long insertFarm_fls(Farm farm) {
		long insertCheck = -1;
		if (null != farm) {

			Client client = new Client();
			client = clientDao.load(farm.getClientId());
			if (null == client) {
				Log.i(tag,
						"Farm insertion not possible client id not found in clientlist = "
								+ farm.getClientId());
			} else {
				Farm tempfarm = new Farm();
				tempfarm = farmDao.load(farm.getId());

				if (null == tempfarm) {
					Log.i(tag, "Farm not exist in DB insertion started = "
							+ farm.getId());
					insertCheck = farmDao.insert(farm);
				} else {
					Log.i(tag,
							"Farm exist in DB updation started = "
									+ farm.getId());
					farmDao.update(farm);
					insertCheck = farm.getId();
				}
			}
		}
		return insertCheck;
	}

	public long insertField_fls(Field field) {
		long insertCheck = -1;
		if (null != field) {
			Farm farm = new Farm();
			farm = farmDao.load(field.getFarmId());
			if (null == farm) {
				Log.i(tag,
						"Field insertion not possible farm id not found in farmlist = "
								+ field.getFarmId());
			} else {
				Field tempField = new Field();
				tempField = fieldDao.load(field.getId());
				if (null == tempField) {
					Log.i(tag, "Field not exist in DB insertion started = "
							+ field.getId());
					insertCheck = fieldDao.insert(field);

				} else {
					Log.i(tag,
							"Field exist in DB updation started = "
									+ field.getId());
					fieldDao.update(field);
					insertCheck = field.getId();
				}

			}
		}
		return insertCheck;
	}

	public Field getFieldByFieldId(long id) {
		Field field = null;
		field = new Field();
		field = fieldDao.load(id);
		return field;
	}

	public Farm getFarmByFarmId(long id) {
		Farm farm = null;
		farm = new Farm();
		farm = farmDao.load(id);
		return farm;
	}

         public Feature getFeatureByFeatureId(long lFeatureID) {
            if (lFeatureID < 0) {
               return null;
            }
            return featureDao.load(lFeatureID);
         }
         
	public Client getClientByClientId(long id) {
		Client client = null;
		client = new Client();
		client = clientDao.load(id);
		return client;
	}

	public boolean doResourceCleaning() {
		boolean deleteStatus = false;
		// get finshed job list
		List<Job> finshedjobslist = getFinishedJobs();
		if (null != finshedjobslist && !finshedjobslist.isEmpty()) {
			for (Job job : finshedjobslist) {
				// delete client farm field
				deleteStatus = deleteAllJobById(job.getId());

			}
			return deleteStatus;
		}

		return deleteStatus;
	}

         public void insertClientList(List<Client> clientList) {
            daoWrapper.updateClientsList(clientList, clientDao);
         }
	
         public void insertFarmList(List<Farm> farmList) {
            daoWrapper.updateFarmsList(farmList, farmDao);
         }
         
         public void insertFieldList(List<Field> fieldList) {
            daoWrapper.updateFieldsList(fieldList, fieldDao);
         }
         
         public void insertFeatureList(List<Feature> boundaryList) {
            daoWrapper.updateBoundaryList(boundaryList, featureDao);
         }
         
	public void insertCropList(List<Crop> croplist) {
	        Log.i(TAG, "insertCropList");
		cropDao.insertInTx(croplist);
		Log.i(TAG, "insertCropList completed");
	}

	public List<Crop> getAllCropList() {
		return daoWrapper.getCropList(cropDao);
	}

	public void deleteAllCropList() {
	   Log.i(TAG, "deleteAllCropList");
		cropDao.deleteAll();
		
	}

	public boolean isClientExist(String clientName) {
		return daoWrapper.isClientExist(clientDao, clientName);
	}

	public boolean isFarmExist(long clientId, String farmName) {
		return daoWrapper.isFarmExist(farmDao, clientId, farmName);
	}

	public boolean isFieldExist(long farmId, String fieldName) {
		return daoWrapper.isFieldExist(fieldDao, farmId, fieldName);
	}

	public Long getDefaultCFFE_Id() {
		return D_UNKNOWN_CFFE_ID;
	}

	public long insertMapping(Mapping mapping) {
		if (mapping == null) {
			return -1;
		}
		long id = -1;
		try {
			id = mappingDao.insert(mapping);
		} catch (android.database.sqlite.SQLiteConstraintException e) {
			// ignore
			mappingDao.update(mapping);
			id = mapping.getId();
		}
		return id;
	}

	public PickList getPickListByID(Long pickListID) {

		return pickListDao.load(pickListID);
	}

	public String getDefaultValue_AttributrIE(AttributeInfoEntity attribInfo) {
		String stDefalutValue = null;

		if (attribInfo == null) {
			return stDefalutValue;
		}

		stDefalutValue = attribInfo.getDefaultValue();
		if (attribInfo.getDataType() == AgDataStoreResources.DATATYPE_PICKLIST
				&& attribInfo.getId() != AgDataStoreResources.NDVI_NPER_ID) {
			List<PickList> pickList = getPickListByName(stDefalutValue,
					attribInfo.getTemplatetypeId());
			if (pickList != null && pickList.size() > 0) {
				stDefalutValue = pickList.get(0).getItem();
			}
		}
		return stDefalutValue;
	}

	public String getLastValue_AttributrIE(AttributeInfoEntity attribInfo) {
		String stLastEnterValue = null;

		if (attribInfo == null) {
			return stLastEnterValue;
		}

		stLastEnterValue = attribInfo.getLastenter();
		if (stLastEnterValue == null || stLastEnterValue.trim().length() == 0) {
			return stLastEnterValue;
		}

		if (attribInfo.getDataType() == AgDataStoreResources.DATATYPE_PICKLIST
				&& attribInfo.getId() != AgDataStoreResources.NDVI_NPER_ID) {
			PickList pickList = null;
			try {

				pickList = getPickListByID(Long.parseLong(stLastEnterValue));
				if (pickList != null) {
					stLastEnterValue = pickList.getItem();
				}
			} catch (NumberFormatException e) {

			}

		}
		return stLastEnterValue;
	}

	public String getLastValue_ID_AttributeItem(AttributeInfoEntity attribInfo) {
		String stLastEnterValue = null;

		if (attribInfo == null) {
			return stLastEnterValue;
		}

		stLastEnterValue = attribInfo.getLastenter();
		if (stLastEnterValue == null || stLastEnterValue.trim().length() == 0) {
			return stLastEnterValue;
		}

		if (attribInfo.getDataType() == AgDataStoreResources.DATATYPE_PICKLIST
				&& attribInfo.getId() != AgDataStoreResources.NDVI_NPER_ID) {
			List<PickList> pickList = getPickListByName(stLastEnterValue,
					attribInfo.getId());
			if (pickList != null && pickList.size() > 0) {
				stLastEnterValue = String.valueOf(pickList.get(0).getId());
			}
		}
		return stLastEnterValue;
	}

	public String getLastValue_ID_AttributrIE(AttributeInfoEntity attribInfo) {
		String stLastEnterValue = null;

		if (attribInfo == null) {
			return stLastEnterValue;
		}

		stLastEnterValue = attribInfo.getLastenter();
		if (stLastEnterValue == null || stLastEnterValue.trim().length() == 0) {
			return stLastEnterValue;
		}
		if (attribInfo.getDataType() == AgDataStoreResources.DATATYPE_PICKLIST

		&& attribInfo.getId() != AgDataStoreResources.NDVI_NPER_ID) {
			try {
				PickList pickList = getPickListByID(Long
						.parseLong(stLastEnterValue));
				if (pickList != null) {
					stLastEnterValue = String.valueOf(pickList.getId());
				}
			} catch (NumberFormatException e) {
				if (attribInfo.getId() == AgDataStoreResources.GROWTH_STAGE_ID) {
					attribInfo.setId(-1L);
					String stID = getLastValue_ID_AttributeItem(attribInfo);
					attribInfo.setLastenter(stID);
					stLastEnterValue = getLastValue_ID_AttributrIE(attribInfo);
				} else {
					throw e;
				}
			}
		}
		return stLastEnterValue;
	}

	public void updatePickList(PickList pickList) {

		pickListDao.update(pickList);
	}

	public List<PickList> getPickListByName(String stItem, Long attriID) {

		QueryBuilder<PickList> qb = pickListDao.queryBuilder();
		qb.where(com.trimble.agmantra.dao.PickListDao.Properties.Item

		.eq(stItem), com.trimble.agmantra.dao.PickListDao.Properties.AttrinfoId

		.eq(attriID));

		return qb.list();
	}

	public List<PickList> getPickListByGropuID(Long groupID) {

		QueryBuilder<PickList> qb = pickListDao.queryBuilder();
		qb.where(com.trimble.agmantra.dao.PickListDao.Properties.GroupId
				.eq(groupID));

		return qb.list();
	}

	public List<Mapping> getMappingForAtrributeId(long Id) {
		QueryBuilder<Mapping> qb = mappingDao.queryBuilder();
		qb.where(com.trimble.agmantra.dao.MappingDao.Properties.AttributeId

		.eq(Id));

		return qb.list();
	}

	public List<Mapping> getMappingForAtrributeItemId(long Id) {
		QueryBuilder<Mapping> qb = mappingDao.queryBuilder();
		qb.where(com.trimble.agmantra.dao.MappingDao.Properties.AttrItemID

		.eq(Id));

		return qb.list();
	}

	public List<Mapping> getMappingForAtrItemId_MappingID(long attrId,
			long lAttriItemID, int iMappingID) {
		QueryBuilder<Mapping> qb = mappingDao.queryBuilder();
		qb.where(com.trimble.agmantra.dao.MappingDao.Properties.AttrItemID
				.eq(lAttriItemID),
				com.trimble.agmantra.dao.MappingDao.Properties.AttributeId
						.eq(attrId),
				com.trimble.agmantra.dao.MappingDao.Properties.MappingID
						.eq(iMappingID));

		return qb.list();
	}

	public List<Mapping> getMappingForAtrribute_ID_ItemId(long lLanguagePickListItemId,
			long lAttributeID) {
		QueryBuilder<Mapping> qb = mappingDao.queryBuilder();
		qb.where(com.trimble.agmantra.dao.MappingDao.Properties.AttrItemID

		.eq(lLanguagePickListItemId),
				com.trimble.agmantra.dao.MappingDao.Properties.AttributeId

				.eq(lAttributeID),
				com.trimble.agmantra.dao.MappingDao.Properties.Level

				.eq(1));

		return qb.list();
	}

	public List<Language> getLanguageByLangCode(String stLanguageCode) {
		QueryBuilder<Language> qb = mLanguageDao.queryBuilder();
		qb.where(com.trimble.agmantra.dao.LanguageDao.Properties.Language

		.eq(stLanguageCode));

		return qb.list();
	}

	private void deleteAllTableContent() {
	        Log.i(TAG, "clear cache deleteAllTableContent started");
	        deleteCFF();
		jobDao.deleteAll();
		jobTypeDao.deleteAll();
		jobTransactionDao.deleteAll();
		jobtimingDao.deleteAll();
		agJobDao.deleteAll();
		templateTypeDao.deleteAll();
		attributeEntityDao.deleteAll();
		attributeInfoEntityDao.deleteAll();
		pickListDao.deleteAll();
		featureDao.deleteAll();
		featureTypeDao.deleteAll();
		flagCounterDao.deleteAll();
		cropDao.deleteAll();
		commodityDao.deleteAll();
		unitsDao.deleteAll();
		mEquipmentsDao.deleteAll();
		mSupplyDao.deleteAll();
		mTankMixDao.deleteAll();
		mPeopleDao.deleteAll();
		mWhetherDao.deleteAll();
		mFieldConditionDao.deleteAll();
		mappingDao.deleteAll();
		mLanguageDao.deleteAll();
		mUserDao.deleteAll();
		mOrganizationDao.deleteAll();
		Log.i(TAG, "clear cache deleteAllTableContent finished");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "" + "" + "";
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}
	
         public User getUserInfo(final String stUserName,
               final String stOrganizationID) {
            User user = null;
            if (stUserName == null || stOrganizationID == null) {
               Log.i(TAG, "getUserInfo user name or OrganizationID is NULLL");
               return null;
            }
            List<User> users = mUserDao
                  .queryBuilder()
                  .where(UserDao.Properties.Name.eq(stUserName),
                        UserDao.Properties.OrgId.eq(stOrganizationID)).list();
            if (users != null && users.size() > 0) {
               user = users.get(0);
            }
      
            return user;
         }

         public User insertUserName(final String stUserName,final String stOrganzationName,
               String stOrgnazantionID){
           User user=null;
           if( stUserName == null ){
              Log.i(TAG, "insert user name is NULLL");
              return user;
           }
           List<User> users =mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(stUserName)).list();
           
           if(users != null && users.size() != 0){
              String stUserOrg=users.get(0).getOrgId();
              if(stUserOrg != null && (stOrgnazantionID == null 
                    || stOrgnazantionID.trim().length() == 0 ) ){
                 stOrgnazantionID=stUserOrg;
              }
              
           }
           Organization organization =null;
           if(stOrgnazantionID == null || stOrgnazantionID.trim().length() == 0){
              Random random = new Random() ;
              do{
                 stOrgnazantionID =String.valueOf(random.nextInt(Short.MAX_VALUE));
              }while(mOrganizationDao.load(stOrgnazantionID) != null);
                 organization = new Organization(stOrgnazantionID, stOrganzationName);
                 mOrganizationDao.insert(organization);
              
           }else{
              organization=mOrganizationDao.load(stOrgnazantionID);
               if( organization == null){
                  organization = new Organization(stOrgnazantionID, stOrganzationName);
                  mOrganizationDao.insert(organization);
               }
               
               
           }
           
           if(users == null || users.size() == 0){
               user = new User();
              user.setName(stUserName);
              user.setOrgId(organization.getOrgId());
              mUserDao.insert(user);
           }else{
              List<User> usersAssOrgan =mUserDao.queryBuilder().where(
                    UserDao.Properties.Name.eq(stUserName),UserDao.Properties.OrgId.eq(organization.getOrgId())).list();
              // existing user with new organization 
              if(usersAssOrgan == null || usersAssOrgan.size() == 0){
                 user = new User();
                 user.setName(stUserName);
                 user.setOrgId(organization.getOrgId());
                 mUserDao.insert(user);
              }else{
                 user=usersAssOrgan.get(0);
              }
              
              
           }
           return user;
        }
         
         public void clearDownloadedData(){
            
            mUserDao.deleteAll();
            mOrganizationDao.deleteAll();
            daoWrapper.clearDownloadedClient(clientDao);
            daoWrapper.clearDownloadedFarm(farmDao);
            daoWrapper.clearDownloadedBoundary(featureDao,fieldDao);
            daoWrapper.clearDownloadedField(fieldDao);
            
         }
       
         private transient List<Feature> fullBoundaryList;
         public synchronized List<Feature> updateFullBoundary() {
            
        clearAllBoundaryData();
        fullBoundaryList = getAllBoundaryFeatures();
           
            Log.i(TAG, "updateFullBoundarys");

            return fullBoundaryList;

      }
        
         public synchronized void clearAllBoundaryData(){
            
            if (fullBoundaryList != null) {
               fullBoundaryList.clear();
               fullBoundaryList = null;
                  Log.i(TAG, "Boundary cache cleared");
            }
            
      }
         
                 
         public int getDBVersion(){
            return DaoMaster.SCHEMA_VERSION ;
         }

         public boolean isThereAnyPendingJob() {
            
            if (daoWrapper == null) {
                    return false;
            }
            QueryBuilder<Job> qb = jobDao.queryBuilder();
            qb.where(com.trimble.agmantra.dao.JobDao.Properties.Status.eq(AgDataStoreResources.JOB_STATUS_UNFINISHED));
            int iUnfinishedJobCount = (int) qb.count();
            
            qb.where(com.trimble.agmantra.dao.JobDao.Properties.Status.eq(AgDataStoreResources.JOB_STATUS_ENCODING));
            int iNotUploadedJobCount = (int) qb.count();
            
            if(iUnfinishedJobCount != 0 || iNotUploadedJobCount != 0){
               return true;
            }
            
            return false;
         }
         
			public boolean isThereAnyCreatedCFF() {
				if (daoWrapper == null) {
					return false;
				}
				QueryBuilder<Field> fieldqb = fieldDao.queryBuilder();
				int iCount = (int) fieldqb.count();
				if (iCount > 1) {
					return true;
				}
				
				QueryBuilder<Farm> farmqb = farmDao.queryBuilder();
				iCount = (int) farmqb.count();
				if (iCount > 1) {
					return true;
				}
				
				QueryBuilder<Client> clientqb = clientDao.queryBuilder();
				iCount = (int) clientqb.count();
				if (iCount > 1) {
					return true;
				}
				return false;
			}
         
         public void deleteJobs(){
            jobDao.deleteAll();
            jobTransactionDao.deleteAll();
            jobtimingDao.deleteAll();
            agJobDao.deleteAll();
            featureDao.deleteAll();
         }
         
         public void deleteCFF(){
            clientDao.deleteAll();
            farmDao.deleteAll();
            fieldDao.deleteAll();
          }
}
