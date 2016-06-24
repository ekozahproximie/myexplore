/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name: atsLocationService
 *
 * Module Name: com.trimble.ag.ats.dao
 *
 * File name: DaoWrapper.java
 *
 * Author: sprabhu
 *
 * Created On: 27-Oct-2015 11:44:25 pm
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

import com.trimble.ag.ats.dao.LocationDao;
import com.trimble.ag.ats.dao.OrganizationDao;
import com.trimble.ag.ats.dao.SettingsDao;
import com.trimble.ag.ats.dao.UserDao;
import com.trimble.ag.ats.dao.User_OrganizationDao;
import com.trimble.ag.ats.entity.Location;
import com.trimble.ag.ats.entity.Organization;
import com.trimble.ag.ats.entity.Settings;
import com.trimble.ag.ats.entity.User;

import java.util.List;

/**
 * @author sprabhu
 * 
 */
public class DaoWrapper {

	/**
    * 
    */
	public DaoWrapper() {

	}

	protected void insertLocations(final List<Location> locationList,
			final LocationDao locationDao) {
		if (locationDao != null && locationList != null) {

			locationDao.insertInTx(locationList);
		}

	}

	protected void updateLocations(final List<Location> locationList,
			final LocationDao locationDao) {
		if (locationDao != null && locationList != null) {
			locationDao.updateInTx(locationList);
		}

	}

	protected void deleteSyncedLocations(final LocationDao locationDao) {
		if (locationDao != null) {
			final List<Location> locationList = locationDao.queryBuilder()
					.where(LocationDao.Properties.IsSynced.eq(true)).list();
			locationDao.deleteInTx(locationList);
		}

	}

	protected List<Location> getNonSyncedLocations(
			final LocationDao locationDao, String orgId) {
		List<Location> locationList = null;
		if (locationDao != null) {
			locationList = locationDao
					.queryBuilder()
					.where(LocationDao.Properties.IsSynced.eq(false),
							LocationDao.Properties.OrgId.eq(orgId)).list();
		}

		return locationList;

	}

	protected void insertSetings(final List<Settings> settingsList,
			final SettingsDao settingsDao) {
		if (settingsDao != null && settingsList != null) {
			settingsDao.insertInTx(settingsList);
		}

	}

	protected void updateSetings(final List<Settings> settingsList,
			final SettingsDao settingsDao) {
		if (settingsDao != null && settingsList != null) {
			settingsDao.updateInTx(settingsList);
		}

	}

	protected List<Organization> getOrganizationForUser(final UserDao userDao,
			final OrganizationDao organizationDao, final User user,
			final int iServerMode) {

		List<Organization> organizations = organizationDao.queryRaw(", "
				+ User_OrganizationDao.TABLENAME + " U WHERE U."
				+ User_OrganizationDao.Properties.UserId.columnName + " = ? "
				+ "AND U."
				+ User_OrganizationDao.Properties.ServerMode.columnName
				+ " = ? " + "AND T."
				+ OrganizationDao.Properties.OrgId.columnName + " = U."
				+ User_OrganizationDao.Properties.OrgId.columnName
				+ " ORDER BY "
				+ OrganizationDao.Properties.IsPrimaryOrg.columnName + " DESC,"
				+ OrganizationDao.Properties.Name.columnName
				+ " COLLATE NOCASE ASC", user.getUserId().toString(),
				String.valueOf(iServerMode));

		return organizations;
	}
}
