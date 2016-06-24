package com.trimble.ag.nabu.db;

import com.trimble.ag.filemonitor.dao.FileInfoDao;
import com.trimble.ag.filemonitor.entity.FileInfo;

import de.greenrobot.dao.query.QueryBuilder;

import java.util.Date;


public class DaoWrapper {

   private static final String TAG = "FileSyncDAOWrapper";

   public DaoWrapper() {

   }
   protected FileInfo getFileByName(final String stFileFullPathName,final FileInfoDao fileInfoDao){
      FileInfo fileInfo =null;
      if(stFileFullPathName != null){
         QueryBuilder<FileInfo>  builder= fileInfoDao.queryBuilder();
         builder.where(FileInfoDao.Properties.FilePath.eq(stFileFullPathName));
         //If more then one entry in the DB
         if(builder.count() >= 1){
            fileInfo =builder.list().get(0);
         }
         
      }
      return fileInfo;
   }
   
   protected long addFile(final String stFileName,final String stAPPName,
         final FileInfoDao fileInfoDao,final String stOutPutFile){
      long id=-1;
      if(stFileName != null){
         QueryBuilder<FileInfo>  builder= fileInfoDao.queryBuilder();
         builder.where(FileInfoDao.Properties.FilePath.eq(stFileName));
         //If more then one entry in the DB
         if(builder.count() > 1){
            return id;
         }
         if(builder.count() == 0){
            final FileInfo fileInfo =new FileInfo();
            fileInfo.setFilePath(stFileName);
            fileInfo.setRegisteredTime(new Date());
            fileInfo.setLastFileSize(-1L);
            fileInfo.setDescFilePath(stOutPutFile);
            fileInfo.setStatus(FileSyncContentProvider.CREATE);
            fileInfo.setAppname(stAPPName);
            id= fileInfoDao.insert( fileInfo);
         }else{
            final FileInfo fileInfo =builder.list().get(0);
            id=fileInfo.getId();
         }
         
      }
      return id;
   }
   protected boolean deleteFile(final String stFileName,
         final FileInfoDao fileInfoDao){
      boolean isDeleted=false;
     final QueryBuilder<FileInfo> builder = fileInfoDao.queryBuilder();
     builder.where(FileInfoDao.Properties.FilePath.eq(stFileName));
     if(builder.count() > 0){
        fileInfoDao.deleteInTx(builder.list());
        isDeleted=true;
     }
     return isDeleted;
     
    }
   
   protected void updateFileInfo(final long lFileId,final long lStatus,final FileInfoDao mFileInfoDao){
      
        final FileInfo fileInfo= mFileInfoDao.load(lFileId);
        if(fileInfo != null){
           fileInfo.setStatus(lStatus);
           mFileInfoDao.update(fileInfo);
        }
      
   }
  }
