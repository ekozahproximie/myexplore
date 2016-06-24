package com.trimble.agmantra.filecodec.fdt;

import android.widget.FrameLayout;

import com.hexiong.jdbf.DBFWriter;
import com.hexiong.jdbf.JDBFException;
import com.hexiong.jdbf.JDBField;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.entity.Language;
import com.trimble.agmantra.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/*
 * 
 */
public class FDTWrapper {

   private String    stFullFilePath = null;

   private FDTHeader fdtHeader      = null;
   private DBFWriter fdtWriter      = null;

   // Check this varibale usage requirement - impl of job encoder
   // private int iTemplateType = 0;

   /**
    * 
    * @param stFilePath
    */
   public FDTWrapper(final String stFilePath, final FDTHeader fdtHeader) {
      this.stFullFilePath = stFilePath;
      this.fdtHeader = fdtHeader;
   }

   /**
    * Create the FDT File, write the header and Default values
    */
   public boolean createFDTFile(String[] stStatus) {

      JDBField[] jdbfFields = null;
      Object[] defVal = null;
      JDBField field = null;
      char cFieldType = 0;

      boolean isWrite = false;
      boolean isNum = false;

      int iCount = 0;

      int iNumOfRec = fdtHeader.vecHeaderVal.size();

      try {

         jdbfFields = new JDBField[iNumOfRec];
         defVal = new Object[iNumOfRec];

         int iFieldLength = 0;
         int iDecimalCount = 0;

         for (AttributesInfo attribInfo : fdtHeader.vecHeaderVal) {

            switch (attribInfo.iAttFieldType) {
               case AgDataStoreResources.DATATYPE_STRING:
               case AgDataStoreResources.DATATYPE_STRINGARRAY:
               case AgDataStoreResources.DATATYPE_IMAGE:
               case AgDataStoreResources.DATATYPE_PICKLIST:
                  cFieldType = Constants.FLD_TYPE_CHARACTER;
                  iFieldLength = attribInfo.iFieldLength;
                  iDecimalCount = 0;
                  isNum = false;
                  break;
               case AgDataStoreResources.DATATYPE_FLOAT:
                  cFieldType = Constants.FLD_TYPE_FLOAT;
                  iFieldLength = attribInfo.iFieldLength;
                  iDecimalCount = 5;
                  isNum = true;
                  break;
               case AgDataStoreResources.DATATYPE_INTEGER:
                  cFieldType = Constants.FLD_TYPE_NUMBER;
                  iFieldLength = Constants.MAX_DBF_NUMBER_FIELD_LENGTH;
                  iDecimalCount = 0;
                  isNum = true;
                  break;

               case AgDataStoreResources.DATATYPE_DATE:
                  cFieldType = Constants.FLD_TYPE_DATE;
                  iFieldLength = attribInfo.iFieldLength;
                  iDecimalCount = 0;
                  isNum = false;
                  break;
               case AgDataStoreResources.DATATYPE_BOOLEAN:
                  cFieldType = Constants.FLD_TYPE_DATE;
                  iFieldLength = attribInfo.iFieldLength;
                  iDecimalCount = 0;
                  isNum = false;
                  break;
               default:
                  break;
            }

            field = new JDBField(attribInfo.stName.toString(), cFieldType,
                  iFieldLength, iDecimalCount);

            if (isNum) {
               defVal[iCount] = attribInfo.stDefaultValue;
            } else {
               defVal[iCount] = attribInfo.stDefaultValue;
            }

            isNum = false;

            jdbfFields[iCount] = field;

            iCount++;
         }

         fdtWriter = new DBFWriter(stFullFilePath, jdbfFields,FDTWrapper.getEncodingType());
         fdtWriter.addRecord(defVal);
         isWrite = true;
      }

      catch (JDBFException e) {
         if(Utils.isSDCardMount()){
            stStatus[0] = Constants.SDCARD_NO_SPACE;
         }
         e.printStackTrace();
      }
      return isWrite;

   }

   /**
    * add a record into dbf file
    * 
    * @param fieldVal
    */
   public boolean addRecordValues(final Object[] recObject,String[] stStatus) {
      boolean isSuccess = false;
      try {
         fdtWriter.addRecord(recObject);
         isSuccess = true;
      } catch (JDBFException e) {
         if(Utils.isSDCardMount()){
            stStatus[0] = Constants.SDCARD_NO_SPACE;
         }
         e.printStackTrace();
      }
      return isSuccess;
   }

   /**
    * add list record into dbf file
    * 
    * @param vecFiledObj
    */
   public boolean addRecordValues(final Vector<Object[]> vecFiledObj,String[] stStatus) {
      boolean isSuccess = false;
      if (fdtWriter != null) {
         for (Object[] recObject : vecFiledObj) {

            try {
               fdtWriter.addRecord(recObject);
               isSuccess = true;
            } catch (JDBFException e) {
               
               if(Utils.isSDCardMount()){
                  stStatus[0] = Constants.SDCARD_NO_SPACE;
               }
               
               e.printStackTrace();
            }

         }
      }
      return isSuccess;
   }

   public void closeFDTWriter() {
      if (fdtWriter != null) {
         try {
            fdtWriter.close();
         } catch (JDBFException e) {
            e.printStackTrace();
         }
         writeCodePageHeader();
         fdtWriter = null;
      }
   }
   private void writeCodePageHeader(){
	   RandomAccessFile accessFile =null;
       try{
      	 accessFile=new RandomAccessFile(stFullFilePath, "rw");
      	 accessFile.seek(29);
      	 int val =Integer.parseInt(getCodePageHeader(true));
      	 
      	 accessFile.writeByte((byte)val);
      	 
       }catch (Exception e) {
      	 Log.e("FDTWrapper",e.getMessage(),e );
		}finally{
			if(accessFile != null){
				try {
					accessFile.close();
				} catch (IOException e) {
					 Log.e("FDTWrapper",e.getMessage(),e );
				}
			}
		}
   }
   public static String getCodePageHeader(boolean codePageEntryDBF){
	   String stReturnData=null;
	   try{
	 
	   String sysLangCode = Locale.getDefault().getLanguage();
	  

		   if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[0])){
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[5])){
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[6])){
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[2])){
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[3])){
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[9])){
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[4])){
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[7])){
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[8])){
			   if(codePageEntryDBF){
				   stReturnData="200";
			   }else{
				   stReturnData="1250";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[9])){
			   if(codePageEntryDBF){
				   stReturnData="201";
			   }else{
				   stReturnData="1251";
			   }
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[10])){
			   if(codePageEntryDBF){
				   stReturnData="201";
			   }else{
				   stReturnData="1251";
			   }
            } else if (sysLangCode
                           .equals(AgDataStoreResources.LOCALE_LANGUAGES[11])) {
                        if (codePageEntryDBF) {
                           stReturnData = "77";
                        } else {
                           stReturnData = "1251";
                        }
                     } else{
			   if(codePageEntryDBF){
				   stReturnData="03";
			   }else{
				   stReturnData="1252";
			   }
		   }
	     
	   
	   }catch (Exception e) {
		Log.e("FDTWrapper",e.getMessage(),e );
	}
	   return stReturnData;
   }
   
   public static String getEncodingType(){
	   String stReturnData=null;
	   try{
	 
	   String sysLangCode = Locale.getDefault().getLanguage();
		   if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[0])){
			   stReturnData="windows-1252";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[5])){
			   stReturnData="windows-1252";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[6])){
			   stReturnData="windows-1252";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[2])){
			   stReturnData="windows-1252";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[3])){
			   stReturnData="windows-1252";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[9])){
			   stReturnData="windows-1252";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[4])){
			   stReturnData="windows-1252";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[7])){
			   stReturnData="windows-1252";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[8])){
			   stReturnData="windows-1250";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[1])){
			   stReturnData="windows-1251";
		   }else if( sysLangCode.equals(AgDataStoreResources.LOCALE_LANGUAGES[10])){
			   stReturnData="windows-1251";
		   }else{
			   stReturnData="windows-1252";
		   }
	     
	   
	   }catch (Exception e) {
		Log.e("FDTWrapper",e.getMessage(),e );
	}
	   return stReturnData;
   }
}
