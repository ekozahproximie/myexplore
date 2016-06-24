package com.trimble.vilicus.db;

import android.util.Log;

import com.trimble.vilicus.dao.AssetDao;
import com.trimble.vilicus.dao.OrganizationDao;
import com.trimble.vilicus.entity.Asset;
import com.trimble.vilicus.entity.Organization;

import de.greenrobot.dao.QueryBuilder;

import java.util.List;

public class DaoWrapper {

	public DaoWrapper() {
		
	}
	private static final String TAG="VilicusDaoWrapper";
	
	
	protected void updateOrgnizationList(final List<Organization> organizations,final OrganizationDao organizationDao){
           for (Organization organization : organizations) {
                   try{
                      if(organizationDao.load(organization.getOrgId()) == null){
                         organizationDao.insert(organization);
                      }else{
                         organizationDao.update(organization);   
                      }
                      
                   }catch (android.database.sqlite.SQLiteConstraintException e) {
                           Log.e(TAG, e.getMessage(), e);
                   }
           }
           
   }
	protected void updateAssetList(List<Asset> assets,AssetDao assetDao){
	   if(assetDao.count() == 0){
	      assetDao.insertInTx(assets);
	    }else{
		for (Asset asset : assets) {
			try{
			   if(assetDao.load(asset.getId()) == null){
			      assetDao.insert(asset);
			   }else{
			      assetDao.update(asset);   
			   }
			   
			}catch (android.database.sqlite.SQLiteConstraintException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	   }
		
	}
	
	protected void deleteAllAssets(final AssetDao assetDao){
	   if(assetDao != null){
	      Log.i(TAG, "deleteAll Asset  ");
	      assetDao.deleteAll();
	   }
	}
	

	
	

   protected void clearAssetsForUser(final AssetDao mAssetDao, final Long lUserID) {
      if(lUserID == null){
         Log.e(TAG," No username are stored in clearAssetsForUser");
      }    
     
      QueryBuilder<Asset> qb = mAssetDao.queryBuilder().where(AssetDao.Properties.Userid.eq(lUserID));
      
      mAssetDao.deleteInTx(qb.list());   
     
   }
   
 

  
}
