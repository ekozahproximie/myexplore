package com.trimble.agmantra.filecodec.fop;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.entity.Equipments;
import com.trimble.agmantra.entity.People;
import com.trimble.agmantra.utils.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FOPEncoder {

   private long         lFopID     = 0;
   private float        iVersion   = 0;
   private String       stFilePath = "";
   private OpListValues opListVal  = null;
   private FarmWorksContentProvider mContentProvider=null;

   /**
    * 
    * @param stFilePath
    * @param lFopID
    * @param iVersion
    * @param opListVal
    */
   public FOPEncoder(final String stFilePath, final long lFopID,
         final float iVersion, OpListValues opListVal,FarmWorksContentProvider mContentProvider) {
      this.stFilePath = stFilePath;
      this.lFopID = lFopID;
      this.iVersion = iVersion;
      this.opListVal = opListVal;
      this.mContentProvider=mContentProvider;
   }

   public boolean createFOPFile(String[] stStatus) {

      boolean isSuccess = false;

      String stFOPFile = makeFOP(Utils.getHexaStringFromLong(lFopID),
            String.valueOf(iVersion));

      File file = new File(stFilePath);

      FileOutputStream bufWrite = null;
      try {
        
         bufWrite = new FileOutputStream(file);
         
         // This is not supported in 2.1 sdk - need for xml node verification
         
         /*String stData[] = new String[1];
         isSuccess = Utils.getFormatedXMLString(stFOPFile, stData);

         if(isSuccess && null != stData[0]){
            bufWrite.write(stData[0].getBytes());         
         }*/ 
         
         bufWrite.write(stFOPFile.getBytes()); 
         
         isSuccess = true;
      } catch (FileNotFoundException e) {
         String stTemp = Environment.getExternalStorageState();
         if (!stTemp.equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         } else {

               stStatus[0] = Constants.SDCARD_NO_SPACE;
 
            e.printStackTrace();
         }
         return false;
      } catch (IOException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         } else {
         
               stStatus[0] = Constants.SDCARD_NO_SPACE;
        
            e.printStackTrace();
         }
         return false;
      } finally {
         if (bufWrite != null) {
            try {
               bufWrite.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
            bufWrite = null;
         }
      }

      return isSuccess;
   }

   /**
    * 
    * @param stFopId
    * @param stVersion
    * @return
    */
   /**
    * @param stFopId
    * @param stVersion
    * @return
    */
   private String makeFOP(String stFopId, String stVersion) {

      StringBuilder buffer = new StringBuilder();

      String stFOPID = String.format(FOPAttribsTag.TAG_FOP_OPEN, stFopId);
      buffer.append(stFOPID);
      buffer.append(FOPAttribsTag.ST_SPACE);

      String stversion = String.format(FOPAttribsTag.TAG_VERSION, stVersion);
      buffer.append(stversion);

      FlsSchema flsSchema = new FlsSchema(opListVal,mContentProvider);

      buffer.append(flsSchema.makeFLS());

      buffer.append(makeOpList());
      buffer.append(makeComplete(FOPAttribsTag.JOB_COMPLETE));
      buffer.append(String.format(FOPAttribsTag.TAG_FOP_END));

      return buffer.toString();
   }

   /**
    * 
    * @param stFieldId
    * @return
    */
   private String makeOPField(String stFieldId) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_FIELD_START);

      if (stFieldId != null) {
         buffer.append(stFieldId);
      }
      buffer.append(FOPAttribsTag.TAG_FIELD_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stCropId
    * @return
    */
   private String makeOPCrop(String stCropId) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_CROP_START);

      if (stCropId != null) {
         buffer.append(stCropId);
      }
      buffer.append(FOPAttribsTag.TAG_CROP_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stType
    * @return
    */
   private String makeOPType(String stType) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_TYPE_START);

		if (stType != null) {
			String optypeId = Constants.SCOUTING_OPTYPE_ID;
			// buffer.append(Utils.getHexaStringFromLong(opListVal.iTemplateType));
			buffer.append(optypeId);
		}
		buffer.append(FOPAttribsTag.TAG_TYPE_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stUTCval
    * @param stDate
    * @return
    */
   private String makeOpDate(String stUTCval, String stDate) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String.format(FOPAttribsTag.TAG_DATE_OPEN, stUTCval));

      if (stDate != null) {
         buffer.append(stDate);
      }
      buffer.append(FOPAttribsTag.TAG_DATE_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stWidth
    * @return
    */
   private String makeOpWidth(String stWidth) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String.format(FOPAttribsTag.TAG_OP_WIDTH_START));

      if (stWidth != null) {
         buffer.append(stWidth);
      }
      buffer.append(FOPAttribsTag.TAG_OP_WIDTH_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stSetStatus
    * @param stSoiltype
    * @param stSoilcond
    * @param stAppmethod
    * @param stGrowthstage
    * @return
    */

   private String makeOpFieldCondition(String stSetStatus, String stSoiltype,
         String stSoilcond, String stAppmethod, String stGrowthstage) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String
            .format(FOPAttribsTag.TAG_FLD_CONTN_OPEN, stSetStatus));

      if (stSoiltype != null) {
         buffer.append(FOPAttribsTag.TAG_SOIL_TYPE_START);
         buffer.append(stSoiltype);
         buffer.append(FOPAttribsTag.TAG_SOIL_TYPE_END);

      }

      if (stSoilcond != null) {
         buffer.append(FOPAttribsTag.TAG_SOIL_CONTN_START);
         buffer.append(stSoilcond);
         buffer.append(FOPAttribsTag.TAG_SOIL_CONTN_END);

      }
      if (stAppmethod != null) {
         buffer.append(FOPAttribsTag.TAG_APP_METHOD_START);
         buffer.append(stSoilcond);
         buffer.append(FOPAttribsTag.TAG_APP_METHOD_END);

      }

      if (stGrowthstage != null) {
         buffer.append(FOPAttribsTag.TAG_GRTH_STAGE_START);
         buffer.append(stGrowthstage);
         buffer.append(FOPAttribsTag.TAG_GRTH_STAGE_END);

      }

      buffer.append(FOPAttribsTag.TAG_FLD_CONTN_END);

      return buffer.toString();
   }

   /**
    * 
    * @param stSetStatus
    * @param stTemparature
    * @param stHumidity
    * @param stWindspeed
    * @param stWindgust
    * @param stWinddir
    * @param stSkycond
    * @return
    */
   private String makeOpWeatherCondition(String stSetStatus,
         String stTemparature, String stHumidity, String stWindspeed,
         String stWindgust, String stWinddir, String stSkycond) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String.format(FOPAttribsTag.TAG_WEATHER_CONTN_OPEN,
            stSetStatus));

      if (stTemparature != null) {
         buffer.append(FOPAttribsTag.TAG_TEMP_START);
         buffer.append(stTemparature);
         buffer.append(FOPAttribsTag.TAG_TEMP_END);

      }

      if (stHumidity != null) {
         buffer.append(FOPAttribsTag.TAG_HUMID_START);
         buffer.append(stHumidity);
         buffer.append(FOPAttribsTag.TAG_HUMID_END);

      }
      if (stWindspeed != null) {
         buffer.append(FOPAttribsTag.TAG_WIND_SPEED_START);
         buffer.append(stWindspeed);
         buffer.append(FOPAttribsTag.TAG_WIND_SPEED_END);

      }

      if (stWindgust != null) {
         buffer.append(FOPAttribsTag.TAG_WIND_GUST_START);
         buffer.append(stWindgust);
         buffer.append(FOPAttribsTag.TAG_WIND_GUST_END);

      }

      if (stWinddir != null) {
         buffer.append(FOPAttribsTag.TAG_WIND_DIR_START);
         buffer.append(stWinddir);
         buffer.append(FOPAttribsTag.TAG_WIND_DIR_END);

      }

      if (stSkycond != null) {
         buffer.append(FOPAttribsTag.TAG_SKY_CONTN_START);
         buffer.append(stSkycond);
         buffer.append(FOPAttribsTag.TAG_SKY_CONTN_END);

      }

      buffer.append(FOPAttribsTag.TAG_WEATHER_CONTN_END);

      return buffer.toString();
   }

   /**
    * 
    * @return
    */
   private String makeOpList() {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_OP_LST_START);

      buffer.append(makeOP());

      buffer.append(FOPAttribsTag.TAG_OP_LST_END);

      return buffer.toString();
   }

   /**
    * 
    * @return
    */

   private String makeOP() {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_OP_START);

      // Job implementation
      buffer.append(makeOPField(Utils.getHexaStringFromLong(opListVal.field
            .getId())));

		if (opListVal.crop != null) {
			boolean isCropNamePresent = opListVal.crop.getDesc()
					.equalsIgnoreCase(FarmWorksContentProvider.UNKNOWN) ? false
					: true;
			if (isCropNamePresent) {
				buffer.append(makeOPCrop(Utils
						.getHexaStringFromLong(opListVal.crop.getId())));
			} else {
				buffer.append(makeOPCrop(" "));
			}
		} else {
			buffer.append(FOPAttribsTag.TAG_CROP_START_END);
		}

      buffer.append(makeOPType(opListVal.agJob.getJobType().getName()));

      long iMillSec = 0;

      Date dateValue = opListVal.job.getStarttime();

      String stDate = "";

      if (dateValue != null) {

         iMillSec =   dateValue.getTime();     //(int) Utils.getTimeInMilleSecs(dateValue);

         stDate = getFOPDateFormat(dateValue);
      }
      buffer.append(makeOpDate(String.valueOf(iMillSec), stDate));
       iMillSec = 0;
       dateValue = opListVal.job.getStarttime();
       stDate = "";
      if (dateValue != null) {
         iMillSec =   dateValue.getTime();     //(int) Utils.getTimeInMilleSecs(dateValue);
         stDate = getFOPTimeFormat(dateValue);

      }

      buffer.append(makeStartTime(String.valueOf(iMillSec), stDate));

      iMillSec = 0;

      dateValue = opListVal.job.getEndtime();

      stDate = "";
      
      if (dateValue != null) {

         iMillSec =     dateValue.getTime();    //(int) Utils.getTimeInMilleSecs(dateValue);

         stDate = getFOPTimeFormat(dateValue);

      }

      buffer.append(makeStopTime(String.valueOf(iMillSec), stDate));

      

      // TODO - start area and end area - next phase
      buffer.append(makeStartArea(String.valueOf(Unit.UNIT_TYPE_ACRE),
            FOPAttribsTag.ST_ZERO, Unit.UNIT_ACRE_SHORT));

      buffer.append(makeStopArea(String.valueOf(Unit.UNIT_TYPE_ACRE),
            FOPAttribsTag.ST_ZERO, Unit.UNIT_ACRE_SHORT));

      // TODO - This is polygon or line width which from settings
      buffer.append(makeOpWidth(String.valueOf(opListVal.dOpWidth)));

      buffer.append(makeOpFieldCondition(FOPAttribsTag.ST_ZERO,
            FOPAttribsTag.ST_SPACE, FOPAttribsTag.ST_SPACE,
            FOPAttribsTag.ST_SPACE, FOPAttribsTag.ST_SPACE));

      buffer.append(makeOpWeatherCondition(FOPAttribsTag.ST_ZERO,
            FOPAttribsTag.ST_ZERO, FOPAttribsTag.ST_ZERO,
            FOPAttribsTag.ST_ZERO, FOPAttribsTag.ST_ZERO,
            FOPAttribsTag.ST_ZERO, FOPAttribsTag.ST_ZERO));

      buffer.append(makeJobType("harvest"));

      // TODO - Region ID value have to fill here
      buffer.append(makeRegionList(Constants.DEFAULT_REGION_ID
            + Constants.ST_EMPTY));

      buffer.append(makeSupplyTable(false));

      buffer.append(FOPAttribsTag.TAG_OP_END);

      return buffer.toString();

   }

   /**
    * 
    * @param stJobType
    * @return
    */
   private String makeJobType(String stJobType) {
      String stType = "";

      stType = "<" + stJobType + "/>";

      return stType;

   }

   private String makeRegionList(String stRegionID) {
      StringBuilder buffer = new StringBuilder();
      if (stRegionID == null) {
         buffer.append(FOPAttribsTag.TAG_REG_START_END);
         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_REG_LST_START);

      buffer.append(String.format(FOPAttribsTag.TAG_REG_OPEN, stRegionID));

      // TODO - start area implementation - GET VALUE FROM DB
      buffer.append(makeStartArea(String.valueOf(Unit.UNIT_TYPE_ACRE),
            FOPAttribsTag.ST_ZERO, Unit.UNIT_ACRE_SHORT));

      buffer.append(makeStopArea(String.valueOf(Unit.UNIT_TYPE_ACRE),
            FOPAttribsTag.ST_ZERO, Unit.UNIT_ACRE_SHORT));

      long iStartMillSec = 0;
      long iStopMillSec = 0;

      Date dateValue = opListVal.job.getStarttime();

      String stDate = "";

      if (dateValue != null) {

         iStartMillSec =  dateValue.getTime() ;// (int) Utils.getTimeInMilleSecs(dateValue);

         stDate = getFOPTimeFormat(dateValue);

      }

      buffer.append(makeStartTime(String.valueOf(iStartMillSec), stDate));
      
      stDate = "";
      dateValue = null;

      dateValue = opListVal.job.getEndtime();
      
      if (dateValue != null) {

         iStopMillSec =   dateValue.getTime();      //(int) Utils.getTimeInMilleSecs(dateValue);

         stDate = getFOPTimeFormat(dateValue);
      }

      buffer.append(makeStopTime(String.valueOf(iStopMillSec), stDate));

      String stElapseTime = String.valueOf(iStopMillSec-iStartMillSec);
           
      buffer.append(makeElapsedTime(stElapseTime));

      buffer.append(makeOpPeopleList(null));

      buffer.append(makeOpEquipList(null));

      // TODO - notes type details  -  Hardcoded value  - 0x0040
      buffer.append(makeOpNotes("0x0040", ""));

      buffer.append(FOPAttribsTag.TAG_REG_END);

      buffer.append(FOPAttribsTag.TAG_REG_LST_END);

      return buffer.toString();
   }

   private String makeStartTime(String stUTCval, String stStartTime) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String.format(FOPAttribsTag.TAG_START_TIME_OPEN, stUTCval));

      if (stStartTime != null) {
         buffer.append(stStartTime);
      }
      buffer.append(FOPAttribsTag.TAG_START_TIME_END);

      return buffer.toString();
   }

   private String makeStopTime(String stUTCval, String stStopTime) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String.format(FOPAttribsTag.TAG_STOP_TIME_OPEN, stUTCval));

      if (stStopTime != null) {
         buffer.append(stStopTime);
      }
      buffer.append(FOPAttribsTag.TAG_STOP_TIME_END);

      return buffer.toString();
   }

   private String makeStartArea(String stUnitType, String stAreaVal,
         String stUnitDescVal) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String
            .format(FOPAttribsTag.TAG_START_AREA_OPEN, stUnitType));

      if (stAreaVal != null) {
         buffer.append(stAreaVal);
      }

      if (stUnitDescVal != null) {
         buffer.append(FOPAttribsTag.TAG_UNIT_DESC_START);
         buffer.append(stUnitDescVal);
         buffer.append(FOPAttribsTag.TAG_UNIT_DESC_END);

      }
      buffer.append(FOPAttribsTag.TAG_START_AREA_END);

      return buffer.toString();
   }

   private String makeStopArea(String stUnitType, String stAreaVal,
         String stUnitDescVal) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String.format(FOPAttribsTag.TAG_STOP_AREA_OPEN, stUnitType));

      if (stAreaVal != null) {
         buffer.append(stAreaVal);
      }

      if (stUnitDescVal != null) {
         buffer.append(FOPAttribsTag.TAG_UNIT_DESC_START);
         buffer.append(stUnitDescVal);
         buffer.append(FOPAttribsTag.TAG_UNIT_DESC_END);

      }
      buffer.append(FOPAttribsTag.TAG_STOP_AREA_END);

      return buffer.toString();
   }

   private String makeElapsedTime(String stTimeVal) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(String.format(FOPAttribsTag.TAG_ELAP_TIME_START, stTimeVal));

      if (stTimeVal != null) {
         buffer.append(stTimeVal);
      }

      buffer.append(FOPAttribsTag.TAG_ELAP_TIME_END);

      return buffer.toString();
   }

   private String makeOpPeopleList(ArrayList<People> peoples) {

      StringBuilder buffer = new StringBuilder();

      if (peoples == null || peoples.size() <= 0) {
         buffer.append(FOPAttribsTag.TAG_PEOPLE_LST_START_END);
         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_PEOPLE_LST_START);

      if (peoples != null) {
         for (People people : peoples) {
            if (people != null) {
               buffer.append(makeOpPeople(opListVal.people.getId().toString(),
                     opListVal.people.getEpaNum().toString()));
            }
         }
      }
      buffer.append(FOPAttribsTag.TAG_CLIENT_LIST_END);

      return buffer.toString();
   }

   private String makeOpPeople(String stXID, String stQuantityVal) {

      StringBuilder buffer = new StringBuilder();

      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_PEOPLE_START_END);
         return buffer.toString();
      }

      String stPeopleOpen = String.format(FOPAttribsTag.TAG_PEOPLE_OPEN, stXID);

      buffer.append(stPeopleOpen);

      if (stQuantityVal != null) {
         buffer.append(FOPAttribsTag.TAG_QUANTITY_START);
         buffer.append(stQuantityVal);
         buffer.append(FOPAttribsTag.TAG_QUANTITY_END);
      }
      buffer.append(FOPAttribsTag.TAG_PEOPLE_START);

      return buffer.toString();
   }

   private String makeOpEquipList(ArrayList<Equipments> equipments) {

      StringBuilder buffer = new StringBuilder();

      if (equipments == null || equipments.size() <= 0) {
         buffer.append(FOPAttribsTag.TAG_EQP_LST_START_END);
         return buffer.toString();
      }

      buffer.append(FOPAttribsTag.TAG_EQP_LST_START);

      if (equipments != null) {
         for (Equipments equipment : equipments) {
            if (equipment != null) {
               // TODO - Values for the Equipment list
               buffer.append(makeOpEquipment(opListVal.equipments.getId()
                     .toString(), "", "", ""));
            }
         }
      }
      buffer.append(FOPAttribsTag.TAG_EQP_LST_END);

      return buffer.toString();
   }

   private String makeOpEquipment(String stXID, String stQuantity,
         String stStartQuantity, String stEndQuantity) {

      StringBuilder buffer = new StringBuilder();

      if (stXID == null) {
         buffer.append(FOPAttribsTag.TAG_EQP_START_END);
         return buffer.toString();
      }

      String stPeopleOpen = String.format(FOPAttribsTag.TAG_EQP_LST_START,
            stXID);

      buffer.append(stPeopleOpen);

      if (stQuantity != null) {
         buffer.append(FOPAttribsTag.TAG_QUANTITY_START);
         buffer.append(stQuantity);
         buffer.append(FOPAttribsTag.TAG_QUANTITY_END);

      }

      if (stStartQuantity != null) {
         buffer.append(FOPAttribsTag.TAG_START_QUANTITY_START);
         buffer.append(stQuantity);
         buffer.append(FOPAttribsTag.TAG_START_QUANTITY_END);
      }

      if (stEndQuantity != null) {
         buffer.append(FOPAttribsTag.TAG_STOP_QUANTITY_START);
         buffer.append(stQuantity);
         buffer.append(FOPAttribsTag.TAG_STOP_QUANTITY_END);

      }

      buffer.append(FOPAttribsTag.TAG_EQP_END);

      return buffer.toString();
   }

   private String makeOpNotes(String stNotesType, String stNotes) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_NOTES_START);

      if (stNotesType != null) {
         buffer.append(FOPAttribsTag.TAG_NOTE_TYPE_START);
         buffer.append(stNotesType);
         buffer.append(FOPAttribsTag.TAG_NOTE_TYPE_END);
      }
      if (stNotes != null) {
         buffer.append(FOPAttribsTag.TAG_NOTE_START);
         buffer.append(stNotes);
         buffer.append(FOPAttribsTag.TAG_NOTE_END);
      }
      buffer.append(FOPAttribsTag.TAG_NOTES_END);

      return buffer.toString();
   }

   private String makeSupplyTable(boolean isAvail) {

      StringBuilder buffer = new StringBuilder();

      if (!isAvail) {
         buffer.append(FOPAttribsTag.TAG_SUPPLY_OP_TABLE_START_END);
         return buffer.toString();
      }
      return buffer.toString();
   }

   private String makeComplete(String stStatus) {

      StringBuilder buffer = new StringBuilder();

      buffer.append(FOPAttribsTag.TAG_COMPLETED_START);

      if (stStatus != null) {
         buffer.append(stStatus);
      }
      buffer.append(FOPAttribsTag.TAG_COMPLETED_END);

      return buffer.toString();
   }

   private String getFOPTimeFormat(Date date) {

      SimpleDateFormat sdfDate = new SimpleDateFormat("hh:mm a");

      return sdfDate.format(date);
   }
   private String getFOPDateFormat(Date date) {

      SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

      return sdfDate.format(date);
   }

}
