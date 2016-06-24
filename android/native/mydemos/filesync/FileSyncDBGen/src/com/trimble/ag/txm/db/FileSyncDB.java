package com.trimble.ag.txm.db;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class FileSyncDB {

	

	
	
	public static void main(String[] args) throws Exception {
		FileSyncDBGen();
		
	}

	private static void FileSyncDBGen(){
		
		try {
			final String Package_DAO = "com.trimble.ag.filemonitor.dao";
			 final String Package_Entity = "com.trimble.ag.filemonitor.entity";
			final Schema schema = new Schema(1, Package_Entity);
			schema.enableKeepSectionsByDefault();
			//schema.enableActiveEntitiesByDefault();
			
			schema.setDefaultJavaPackageDao(Package_DAO);
			addEntity_Dealer(schema);
			DaoGenerator daoGenerator;
			daoGenerator = new DaoGenerator();
			daoGenerator.generateAll(schema, ".." + File.separator + "FileMonitor"
		               + File.separator + "src" + File.separator);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	private static Entity addEntity_Dealer(Schema schema) {
		Entity fileSync = schema.addEntity("FileInfo");
		fileSync.addIdProperty().primaryKey().autoincrement();
		fileSync.addStringProperty("filePath");
		fileSync.addStringProperty("appname");
		fileSync.addStringProperty("descFilePath");
		fileSync.addDateProperty("registeredTime");
		
		fileSync.addDateProperty("lastZipTime");
		fileSync.addLongProperty("lastFileSize");
		fileSync.addStringProperty("awsFileId");
		fileSync.addLongProperty("flag");
		fileSync.addLongProperty("status");
		
		return fileSync;
	}
}
