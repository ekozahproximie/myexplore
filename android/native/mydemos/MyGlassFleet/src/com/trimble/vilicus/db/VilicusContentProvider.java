package com.trimble.vilicus.db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.trimble.mobile.android.util.Box;
import com.trimble.vilicus.acdc.ACDCApi;
import com.trimble.vilicus.dao.AssetDao;
import com.trimble.vilicus.dao.DaoMaster;
import com.trimble.vilicus.dao.DaoSession;
import com.trimble.vilicus.dao.OrganizationDao;
import com.trimble.vilicus.dao.UserDao;
import com.trimble.vilicus.entity.Asset;
import com.trimble.vilicus.entity.Organization;
import com.trimble.vilicus.entity.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public final class VilicusContentProvider {

	private static VilicusContentProvider vilicusContentProvider = null;
	private static final String TAG = "VilicusContentProvider";
	private transient Context context = null;

	private transient CustomDevOpenHelper devOpenHelper = null;
	private transient DaoSession m_DAOSession;
	private transient DaoMaster m_DAOMaster;
	private transient DaoWrapper daoWrapper = null;

	
	private transient AssetDao mAssetDao = null;
	
         private transient UserDao             mUserDao               = null;
         private transient OrganizationDao     mOrganizationDao       = null;
         private transient ACDCApi             mACDCApi               = null;
	
	
	private transient SQLiteDatabase db;
	private transient List<Asset> fullAssetList;
	
	
	private transient boolean isDbUpdatedOnFirstLaunch = true;
	
	private VilicusContentProvider(Context context) {
		this.context = context;
		if (CustomDevOpenHelper.IS_STORE_ON_SD) {
			IntentFilter iFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
			iFilter.addDataScheme("file");
			context.registerReceiver(mMountReceiver, iFilter);

			IntentFilter iFilter2 = new IntentFilter(Intent.ACTION_MEDIA_EJECT);
			iFilter.addDataScheme("file");
			context.registerReceiver(mUnmountReceiver, iFilter2);
			if (!Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				Log.i(TAG, "initAll NOT DONE!");
				return;
			}
		}
		initAll();

	}

	private BroadcastReceiver mMountReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
				Log.i(TAG, "mMountReceiver onReceive called!");
				initAll();
			}
		}
	};

	private BroadcastReceiver mUnmountReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
				Log.i(TAG, "unMountReceiver onReceive called!");
				if (CustomDevOpenHelper.IS_STORE_ON_SD) {

					closeDB();
				}
			}
		}
	};
   

	private void closeDB() {
		if (devOpenHelper != null) {
			devOpenHelper.close();
			Log.i(TAG, "devOpenHelper dbClosed!");
		}

	}

	private void initAll() {
		

		if ( CustomDevOpenHelper.IS_STORE_ON_SD) {
			deleteOldDB(CustomDevOpenHelper.DB_FULL_PATH);
		}
		devOpenHelper =new CustomDevOpenHelper(context);
		db = devOpenHelper.getWritableDatabase();

		m_DAOMaster 	        = new DaoMaster(db);
		m_DAOSession 	        = m_DAOMaster.newSession();
		daoWrapper 				= new DaoWrapper();
		
		mAssetDao				= m_DAOSession.getAssetDao();
		
		mUserDao 		 		= m_DAOSession.getUserDao();
		mOrganizationDao 		= m_DAOSession.getOrganizationDao();
		    
		mACDCApi                =ACDCApi.getInstance(context);
	}

	/**
	 * Gets the single instance of VilicusContentProvider.
	 * 
	 * @param context
	 *            the context
	 * @return single instance of VilicusContentProvider
	 */
	public static synchronized VilicusContentProvider getInstance(Context context) {

		if (vilicusContentProvider == null) {

			vilicusContentProvider = new VilicusContentProvider(context);
		}

		return vilicusContentProvider;

	}

	public boolean isDBUpdated(){
	 return devOpenHelper. isDBUpdated(); 
	}
	
	public void setDbUpdate(){
	   
	}
	public boolean isSecondVersionDB(){
	   return getDBVersion() == DaoMaster.SECOND_VERSION;
	}
        public int getDBVersion(){
           return DaoMaster.SCHEMA_VERSION ;
        }
	/*
	 * After update to DB check whether Client, farm, field , device and boundary table has data
	 */
	
	
	

   
   public void insert_UpdateOrganization(final Vector<Organization> vecOrganizations){
      if(vecOrganizations != null && vecOrganizations.size() > 0 ){
         Log.i(TAG, "insert_UpdateOrganization  list size=" + vecOrganizations.size());
               daoWrapper.updateOrgnizationList(vecOrganizations, mOrganizationDao);
              
 
         }
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
   public List<User> getUserInfo(final String stUserName){
      if( stUserName == null ){
         Log.i(TAG, "getUserInfo user name is NULLL");
         return null;
      }
      return mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(stUserName)).list();
   }
   public User getUserInfo(final String stUserName,final String stOrganizationID){
      User   user =null;
      if( stUserName == null || stOrganizationID == null ){
         Log.i(TAG, "getUserInfo user name or OrganizationID is NULLL");
         return null;
      }
      List<User> users=mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(stUserName),UserDao.Properties.OrgId.eq(stOrganizationID)).list();
      if(users != null && users.size() >  0){
         user=users.get(0);
      }
      
      return user;
   }
  
	
  
	public void insert_updateAssetList(List<Asset> assets,boolean isDeleteBeforeInsert,boolean isDirectUpdate){
           if(assets != null && assets.size() > 0 ){
		   Log.i(TAG, "insert_updateAssetList  list size=" + assets.size());
		   if(isDirectUpdate){
	               mAssetDao.updateInTx(assets);
	            }else{
            
                        if (isDeleteBeforeInsert) {
                           daoWrapper.deleteAllAssets(mAssetDao);
                        }
            
                        daoWrapper.updateAssetList(assets, mAssetDao);
	            }
                 updateFullAssets();
           
           }
           
      }
	
	public Asset getAssetByID(final long lAssetID){
		return mAssetDao.load(lAssetID);
		
	}

	private synchronized void updateFullAssets(){
		
			 clearAllAssetData();
		      final User user = mACDCApi.getCurrentUser();
		      if(user == null){
		         Log.i(TAG, "  updateFullAssets user name is NULL");
		         return ;
		      }
		      
		      fullAssetList = mAssetDao.queryBuilder().where(AssetDao.Properties.Userid.eq(user.getUserid())).list();
		      getAssetsType();	
		
	      Log.i(TAG, "updateFullAssets");
	}
	
	
	public synchronized List<Asset> getAllAssets(){
		updateFullAssets();
		 ArrayList<Asset> arrayList =null;
		if( fullAssetList != null ){
		   arrayList=new ArrayList<Asset>(fullAssetList);
		}
		return arrayList;
	}
	
	
	public synchronized List<Asset> getAssets(final Box bBox) {

           if (bBox == null) {
        	   Log.i(TAG, "getAssets-Box is null");
                   return null;
           }
           if (fullAssetList == null || fullAssetList.size() == 0) {
                   updateFullAssets();
           }
           List<Asset> subAssetList = new ArrayList<Asset>();
           if (fullAssetList != null && fullAssetList.size() > 0) {

                   Log.i(TAG, "getAssets-Getting assets for bounding box");

                   for (Asset asset : fullAssetList) {
                      if(asset == null ||asset.getLongitude() == null ||
                            asset.getLatitude() == null){
                         continue;
                      }
                           if (bBox.overlap((int) (asset.getLongitude() * 1E6),
                                           (int) (asset.getLatitude() * 1E6))) {
                                   subAssetList.add(asset);
                           }
                   }
           }

        if(subAssetList.size() == 0){
   			Log.i(TAG, "getAssets-Box-" + bBox.toString() + " has no result " );
   		}else{
   		
   			Log.i(TAG, "getAssets-Box-" + bBox.toString() + " has result size = " + subAssetList.size());
   		}
           return subAssetList;
   }

	private transient HashMap<String, Asset> vehicleTypeSet = null;

	public synchronized HashMap<String, Asset> getAssetsType() {

		/*
		 * if (fullAssetList == null || fullAssetList.size() == 0) {
		 * updateFullAssets(); }
		 */
		if (fullAssetList != null && fullAssetList.size() > 0) {
			if (vehicleTypeSet == null) {
				vehicleTypeSet = new HashMap<String, Asset>(
						fullAssetList.size());
				for (Asset asset : fullAssetList) {
					String stVehicleType = asset.getType();
					if (!vehicleTypeSet.containsKey(stVehicleType)) {
						vehicleTypeSet.put(stVehicleType, asset);
					}
				}
			}

		}
		
		if(vehicleTypeSet == null ||vehicleTypeSet.size() == 0){
   			Log.i(TAG, "getAssetsType-set has no result " );
   		}else{
   		
   			Log.i(TAG, "getAssetsType-set has result size = " + vehicleTypeSet.size());
   		}
		return vehicleTypeSet;
	}


   
   public void clearDataForUser(final long lUserID,boolean isDemoMode){
      
      daoWrapper.clearAssetsForUser(mAssetDao,lUserID);
      
   }
	
   
	
	


	public boolean deleteOldDB(String stPath) {
		File file = new File(stPath);
		boolean isDeleted = false;
		if (file.exists()) {
			isDeleted = file.delete();
			Log.i(TAG, "Old DB deleted");
		}

		file = null;
		return isDeleted;
	}
	
	
   
   
   
	public synchronized void clearAllAssetData(){
	   if (fullAssetList != null) {
	         fullAssetList.clear();
	         fullAssetList = null;
	         Log.i(TAG, "Assets  cache cleared");
	      }
	      if(vehicleTypeSet != null){
	         vehicleTypeSet.clear();
	         vehicleTypeSet=null;
	         Log.i(TAG, "Asset Types cache cleared");
	      }
	}
   public void clearAllCacheData() {
      
    
      clearAllAssetData();
      
      Log.i(TAG, "All cache data cleared");
   }

public void clearAllDBData() {
	
	mAssetDao.deleteAll();
	Log.i(TAG, "All DB data cleared");
	
}


	
      /**
       * @param isDbUpdatedOnFirstLaunch the isDbUpdatedOnFirstLaunch to set
       */
      public void setDbUpdatedOnFirstLaunch(boolean isDbUpdatedOnFirstLaunch) {
         this.isDbUpdatedOnFirstLaunch = isDbUpdatedOnFirstLaunch;
      } 
      
      public boolean getDbUpdatedOnFirstLaunch(){
         return this.isDbUpdatedOnFirstLaunch;
      }
}
