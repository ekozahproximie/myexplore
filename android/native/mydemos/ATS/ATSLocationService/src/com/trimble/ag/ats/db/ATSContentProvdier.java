/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name: atsLocationService
 *
 * Module Name: com.trimble.ag.ats.db
 *
 * File name: atsContentProvdier.java
 *
 * Author: sprabhu
 *
 * Created On: 27-Oct-2015 11:25:06 pm
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
package com.trimble.ag.ats.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.trimble.ag.ats.dao.DaoSession;
import com.trimble.ag.ats.dao.LocationDao;
import com.trimble.ag.ats.dao.OrganizationDao;
import com.trimble.ag.ats.dao.SettingsDao;
import com.trimble.ag.ats.dao.ATSDaoMaster;
import com.trimble.ag.ats.dao.UserDao;
import com.trimble.ag.ats.dao.User_OrganizationDao;
import com.trimble.ag.ats.entity.Location;
import com.trimble.ag.ats.entity.Organization;
import com.trimble.ag.ats.entity.Settings;
import com.trimble.ag.ats.entity.User;
import com.trimble.ag.ats.entity.User_Organization;

import de.greenrobot.dao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author sprabhu
 *
 */
public final class ATSContentProvdier {

   private static ATSContentProvdier       contentProvdier      = null;

   private static final String            TAG                  = ATSContentProvdier.class
                                                                     .getSimpleName();
   private transient Context              context              = null;

   private transient ATSDB_OpenHelper      devDbOpenHelper      = null;
   private transient SQLiteDatabase       db                   = null;
   private transient DaoSession           m_DAOSession         = null;
   private transient ATSDaoMaster          m_DAOMaster          = null;
   private transient DaoWrapper           daoWrapper           = null;

   private transient LocationDao          mLocationDao         = null;
   private transient User_OrganizationDao mUserOrganizationDao = null;
   private transient UserDao              mUserDao             = null;
   private transient OrganizationDao      mOrganizationDao     = null;

   private transient SettingsDao          mSettingsDao         = null;

   private transient boolean              isDbInit             = false;

   public static interface DbInitListener {

      public static final int SUCCESS = 1;
      public static final int FAIL    = 0;

      public void onDbInitCallback(int iStatus);
   }

   /**
    * 
    */
   private ATSContentProvdier(final Context context) {
      if (contentProvdier != null) {
         throw new IllegalArgumentException("Use getInsance !");
      }
      this.context = context;
      Log.i("test", " atsContentProvdier() block");
      initAll();
   }

   public static synchronized ATSContentProvdier getInstance(
         final Context context) {
      if (contentProvdier == null) {
         contentProvdier = new ATSContentProvdier(context);
      }

      return contentProvdier;
   }

   public void onDbInit(final DbInitListener dbInitListener) {
      final boolean isSuccess = initAll();
      if (dbInitListener != null) {
         dbInitListener.onDbInitCallback(isSuccess ? DbInitListener.SUCCESS
               : DbInitListener.FAIL);
      }
   }

   private boolean initAll() {

      if (ATSDB_OpenHelper.IS_STORE_ON_SD) {
         deleteOldDB(ATSDB_OpenHelper.DB_FULL_PATH);
      }
      devDbOpenHelper = new ATSDB_OpenHelper(context);
      db = devDbOpenHelper.getWritableDatabase();

      m_DAOMaster = new ATSDaoMaster(db);
      m_DAOSession = m_DAOMaster.newSession();
      daoWrapper = new DaoWrapper();
      mLocationDao = m_DAOSession.getLocationDao();
      mSettingsDao = m_DAOSession.getSettingsDao();
      mUserDao = m_DAOSession.getUserDao();
      mOrganizationDao = m_DAOSession.getOrganizationDao();
      mUserOrganizationDao = m_DAOSession.getUser_OrganizationDao();

      isDbInit = true;
      return isDbInit;
   }

   {
      Log.i("test", "atsContentProvdier instance block");
   }

   /**
    * @return the devDbOpenHelper
    */
   public ATSDB_OpenHelper getDevDbOpenHelper() {
      return devDbOpenHelper;
   }

   @Override
   protected void finalize() throws Throwable {
      Log.i("test", "atsContentProvdier finalize block");
      super.finalize();
   }

   /**
    * @return the isDbInit
    */
   public boolean isDbInit() {
      return isDbInit;
   }

   public void insertLocations(final List<Location> locationList) {
      daoWrapper.insertLocations(locationList, mLocationDao);
   }

   public void insertLocation(final Location location) {
      mLocationDao.insert(location);
   }

   public void updateLocations(final List<Location> locationList) {
      daoWrapper.updateLocations(locationList, mLocationDao);
   }

   public void deleteSyncedLocations() {
      daoWrapper.deleteSyncedLocations(mLocationDao);
   }

   public List<Location> getNonSyncedLocations(String orgId) {
      return daoWrapper.getNonSyncedLocations(mLocationDao,orgId);
   }

   public void insertSetings(final List<Settings> settingsList) {
      daoWrapper.insertSetings(settingsList, mSettingsDao);
   }

   public void updateSetings(final List<Settings> settingsList) {
      daoWrapper.updateSetings(settingsList, mSettingsDao);
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

   public User insertUserName(final String stUserName,
         final String stOrganzationName, String stOrgnazantionID,
         final int iServerMode, final long lPrimaryOrgId) {
 User user = null;
 if (stUserName == null) {
         Log.i(TAG, "insert user name is NULL");
         return user;
 }
 List<User> users = mUserDao.queryBuilder()
                 .where(UserDao.Properties.UserName.eq(stUserName)).list();

 if (users == null || users.size() == 0) {
         user = new User();
         user.setUserName(stUserName);
         user.setOrgId(stOrgnazantionID);
         mUserDao.insert(user);
         Log.i(TAG, " new user insert");
 } else {
         Log.i(TAG, "get an old user");
         user = users.get(0);
 }

 Organization organization = null;
 User_Organization user_Organization = null;

 if (stOrgnazantionID == null || stOrgnazantionID.trim().length() == 0) {

         List<User_Organization> users_Organization = mUserOrganizationDao
                         .queryBuilder()
                         .where(User_OrganizationDao.Properties.UserId.eq(user
                                         .getUserId()),
                                         User_OrganizationDao.Properties.ServerMode
                                                         .eq(iServerMode)).list();

         if (users_Organization == null || users_Organization.size() == 0) {
                 Random random = new Random();
                 do {
                         stOrgnazantionID = String.valueOf(random
                                         .nextInt(Short.MAX_VALUE));
                 } while (mOrganizationDao.load(stOrgnazantionID) != null);

                 organization = new Organization(stOrgnazantionID,
                                 stOrganzationName, true);
                 mOrganizationDao.insert(organization);

                 user_Organization = new User_Organization();
                 user_Organization.setOrgId(stOrgnazantionID);
                 user_Organization.setUserId(user.getUserId());
                 user_Organization.setServerMode(iServerMode);
                 mUserOrganizationDao.insert(user_Organization);
         }
 } else {
         organization = mOrganizationDao.load(stOrgnazantionID);
         final boolean isPrimaryOrg = lPrimaryOrgId == Long
                         .parseLong(stOrgnazantionID);
         if (organization == null) {
                 organization = new Organization(stOrgnazantionID,
                                 stOrganzationName, isPrimaryOrg);
                 mOrganizationDao.insert(organization);
         }else{
            organization.setIsPrimaryOrg(isPrimaryOrg);
            organization.setName(stOrganzationName);
            mOrganizationDao.update(organization);
         }

         List<User_Organization> user_Organizations = mUserOrganizationDao
                         .queryBuilder()
                         .where(User_OrganizationDao.Properties.UserId.eq(user
                                         .getUserId()),
                                         User_OrganizationDao.Properties.OrgId
                                                         .eq(stOrgnazantionID),
                                         User_OrganizationDao.Properties.ServerMode
                                                         .eq(iServerMode)).list();

         if (user_Organizations == null || user_Organizations.size() == 0) {
                 user_Organization = new User_Organization();
                 user_Organization.setOrgId(stOrgnazantionID);
                 user_Organization.setUserId(user.getUserId());
                 user_Organization.setServerMode(iServerMode);
                 mUserOrganizationDao.insert(user_Organization);
         }

 }

 return user;
}

      public List<Organization> getOrganizationsForUser(final User user, final int iServerMode) {
      List<Organization> orgs = null;
      if (user == null) {
         Log.i(TAG, "  getOrganizationsForUser:user is NULL");
         return null;
      }
      orgs = daoWrapper
            .getOrganizationForUser(mUserDao, mOrganizationDao, user, iServerMode);
      if (orgs.size() == 0) {
         Log.i(TAG, "getOrganizationsForUser- list has no result ");
      } else {
         
         Log.i(TAG,
               "getOrganizationsForUser-list has result size = " + orgs.size());
      }
      return orgs;

   }

   public String getUserOrgId(final User user, final int iServerMode) {

      String stUserOrgId = null;

      List<Organization> organizationList = getOrganizationsForUser(user, iServerMode);
      Organization org = null;
      if (organizationList != null && organizationList.size() > 0) {
         org = organizationList.get(0);
         if (org != null) {
            stUserOrgId = org.getOrgId();
         }
      }

      return stUserOrgId;
   }

   public List<User> getUserInfo(final String stUserName) {
      if (stUserName == null) {
         Log.i(TAG, "getUserInfo user name is NULLL");
         return null;
      }
      return mUserDao.queryBuilder()
            .where(UserDao.Properties.UserName.eq(stUserName)).list();
   }

   public User getUserInfo(final String stUserName,
         final String stOrganizationID) {
      User user = null;
      if (stUserName == null || stOrganizationID == null) {
         Log.i(TAG, "getUserInfo user name or OrganizationID is NULL");
         return null;
      }

      List<User> users = mUserDao.queryRaw(", "
            + User_OrganizationDao.TABLENAME + " U ON T."
            + UserDao.Properties.UserId.columnName + " = U."
            + User_OrganizationDao.Properties.UserId.columnName + " WHERE U."
            + User_OrganizationDao.Properties.OrgId.columnName + " = ? "
            + "AND T." + UserDao.Properties.UserName.columnName + " = ? ",
            stOrganizationID, stUserName);

      if (users != null && users.size() > 0) {
         user = users.get(0);
      } else {
         Log.i(TAG, "users null in getUserInfo");
      }

      return user;
   }

   public void deleteUserOrgMappingNotInList(
         List<Organization> organizationList, final String stUserName,
         final int iServerMode) {
      List<User> users = mUserDao.queryBuilder()
            .where(UserDao.Properties.UserName.eq(stUserName)).list();
      User user = null;
      if (users != null && users.size() != 0) {
         user = users.get(0);
      }

      if (user == null) {
         return;
      }
      final List<String> orgIds = new ArrayList<String>(organizationList.size());
      for (Organization org : organizationList) {
         orgIds.add(org.getOrgId());
      }
      QueryBuilder<User_Organization> queryBuilder = mUserOrganizationDao
            .queryBuilder().where(
                  User_OrganizationDao.Properties.OrgId.notIn(orgIds),
                  User_OrganizationDao.Properties.UserId.eq(user.getUserId()),
                  User_OrganizationDao.Properties.ServerMode.eq(iServerMode));
      Log.i(TAG, "deleteUserOrgMappingNotInList size = "
            + queryBuilder.list().size() + "," + queryBuilder.list().toString());
      mUserOrganizationDao.deleteInTx(queryBuilder.list());

   }

}
