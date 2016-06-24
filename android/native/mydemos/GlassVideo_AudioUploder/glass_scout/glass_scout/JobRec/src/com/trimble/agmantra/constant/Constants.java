package com.trimble.agmantra.constant;

import android.os.Environment;

import java.io.File;

public class Constants {

   public static int          VRA_MAX_CH                    = 2;

   // Used in FGP header file random number
   public static final int    BLANKLONG                     = 0x80000000;

   // FGP File version
   public static final int    FGP_VERSION                   = 4;

   // FOP File version - TODO - verify the version
   public static final int    FOP_VERSION                   = 2;
   public static final String SCOUTING_OPTYPE_ID			= "0x1C4";

   public static final int    COLOR_BLACK                   = 0x0000004;
   public static final double FEET_PER_METER                = 3.280839895;
   public static final double MY_PI                         = 3.141592653589793238;

   public static final double EET_PER_METER                 = 3.280839895;
   public static final double FEET_PER_MILE                 = 5280.0;

   // File extension
   public static final String GPE_FILE_EXTENS               = ".gpe";
   public static final String lEGEND_FILE_EXTENS            = ".lgd";
   public static final String SHP_FILE_EXTENS               = ".shp";
   public static final String SHX_FILE_EXTENS               = ".shx";
   public static final String FOP_FILE_EXTENS               = ".fop";
   public static final String FGP_FILE_EXTENS               = ".fgp";
   public static final String FDT_FILE_EXTENS               = ".fdt";
   public static final String DBF_FILE_EXTENS               = ".dbf";
   public static final String ZIP_FILE_EXTENS               = ".zip";
   public static final String XML_FILE_EXTENS               = ".xml";
   public static final String PROJECT_FILE_EXTENS           = ".prj";
   public static final String IMAGE_FILE_EXTENS                 = ".jpg";

   // Default Values
   public static final String DEFAULT_DEVICE_NAME           = "AGFW0001";
   public static final String DEFAULT_VAL_LEGEND            = "LEGEND0001";

   public static final double DEFAULT_VAL_JOB_BOOM_OFFSET   = 0;
   public static final double DEFAULT_VAL_JOB_BOOM          = 0;
   public static final int    DEFAULT_VAL_JOB_TYPE          = 0;
   public static final double DEFAULT_VAL_SHP_DATUM         = 8.00000000;
   public static final double DEFAULT_VAL_SHP_SYSTEM        = 1.00000000;
   public static final double DEFAULT_VAL_SHP_ZONE          = 1.00000000;

   public static final int    DEFAULT_REGION_ID                 = 1;

   // Max Dbf field length

   public static final int    MAX_DBF_STRING_FIELD_LENGTH   = 250;

   public static final int    MAX_DBF_LONG_STRING_FIELD_LENGTH  = 250;
   public static final int    MAX_DBF_SHORT_STRING_FIELD_LENGTH = 40;

   public static final int    MAX_DBF_NUMBER_FIELD_LENGTH   = 20;
   public static final int    MAX_DBF_DATE_FIELD_LENGTH     = 20;
   public static final int    MAX_DBF_BOOLEAN_FIELD_LENGTH  = 1;

   // 256
   public static final int    DBF_FLOAT_DECIMAL_COUNT       = 5;
   public static final int    DBF_OTHERS_DECIMAL_COUNT      = 0;

   // SHP stuff
   public static final String SHP_DBF_HEADER                = "__ID42";

   // String Constans
   public static final String ST_SPACE                      = " ";
   public static final String ST_EMPTY                      = "";
   public static final String ST_FORWORD_SLASH              = "/";
   public static final String ST_BACKWORD_SLASH             = "\\";
   public static final String ST_COMMA                      = ", ";
   public static final String ST_COLON                      = ":";
   public static final String ST_UNDERSCORE                 = "_";
   public static final String ST_HYPHEN                     = "-";
   public static final String ST_NEWLINE                    = String
                                                                  .format("%n");
   public static final String ST_DOT                        = ".";

   // List of Chars - DBF format
   public static final char   FLD_TYPE_CHARACTER            = 'C';
   public static final char   FLD_TYPE_BOOLEAN              = 'L';
   public static final char   FLD_TYPE_NUMBER               = 'N';
   public static final char   FLD_TYPE_FLOAT                = 'F';
   public static final char   FLD_TYPE_DATE                 = 'D';
   public static final char   FLD_TYPE_DATE_TIME            = '@';

   public static final int    BOUNDARY_MODIFIED_VAL         = 1;
   public static final int    DEFAULT_BOUNDARY_MODIFIED_VAL = 1;
   public static final int    DEFAULT_BOUNDARY_REVISION_VAL = 0;

   public static final String PROJECT_NAME                      = "0x286f4958";
   public static final String DEFAULT_IMG_NAME                  = "agImg";

   public final static String AG_FOLDERNAME                     = "AgMantra";
   public static final File   EXT_STORAGE_PATH                  = Environment
                                                                      .getExternalStorageDirectory();
   public static final String APP_STORAGE_PATH                  = EXT_STORAGE_PATH
                                                                      + File.separator
                                                                      + AG_FOLDERNAME;
   
   public static final String JOB_ID             = "jobid";
   public static final String JOB_FILE_PATH      = "job_file_path";

   public static final String TAG_JOB_SYNC_SERVICE              = "JOBSYNCSERVICE";
   public static final String TAG_JOB_ENCODER                   = "jobEncoder";
   public static final String TAG_JOB_UPLOADER                  = "JOBUPLOADER";
   public static final String TAG_JOB_DOWNLOADER                = "jobdwonloader";
   public static final String TAG_JOB_IMPORTER                  = "jobimporter";
   public static final String TAG_JOB_RECORDER                  = "jobRecorder";
  
   /*
    * SQFT_PER_ACRE 43560.0 #define SQM_PER_HA 10000.0 #define KM_PER_MILE
    * 1.609344 #define #define FEET_PER_MILE 5280.0 #define LBS_PER_KG
    * 2.204622622 #define ACRE_PER_HA 2.4710538 #define LITER_PER_GAL 3.7854118
    * #define KG_PER_LITER 1.0000000
    */
   
   
 public static final long MIN_SPACE_SDCARD_IN_MB                  = 5;
 public static final String SDCARD_NO_SPACE                 = "SDcard UnMounted or No Space in Sdcard";
 
 public static final long DEFAULT_ID                 = 2147483648l;
 
 public static  boolean    IS_DEV_BUILD = false;  

 public static final boolean IS_MOVE_UPLOADED_FILE             = true;
 
 public static String getStoreRoot(){
     

     File SDCARD_PATH = Environment.getExternalStorageDirectory();

     String AG_FLAG_STORAGE = SDCARD_PATH.getAbsolutePath() + File.separator
             + AG_FOLDERNAME ;
     
     return AG_FLAG_STORAGE;
 }

   
   public static String getFlagStoreDir(){
        String FLAG_FOLDERNAME = "Flag";

        File SDCARD_PATH = Environment.getExternalStorageDirectory();

       // public static final String SDCARD_PATH =
       // Environment.getExternalStorageDirectory();
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(SDCARD_PATH.getAbsolutePath());
        buffer.append(File.separator);
        buffer.append(AG_FOLDERNAME);
        buffer.append(File.separator);
        buffer.append(FLAG_FOLDERNAME);
        buffer.append(File.separator);
       
        String AG_FLAG_STORAGE = buffer.toString();
           
        return AG_FLAG_STORAGE;
   }
   
   public static String getFlagStoreDir_Job(long lJobID){
          StringBuffer buffer = new StringBuffer();
           
           buffer.append(getFlagStoreDir());
           buffer.append(String.valueOf(lJobID));
           buffer.append(File.separator);
       String AG_FLAG_STORAGE = buffer.toString();
       
       return AG_FLAG_STORAGE;
  }
   public static String getFlagStoreDir_Job_Feat(long lJobID,long lFeatureID){
     
       String AG_FLAG_STORAGE = getFlagStoreDir_Job(lJobID)+  lFeatureID;
       
       return AG_FLAG_STORAGE;
  }
   public static String getFlagStoreDir_Job_Feat(long lJobID,String stFeatureID){
       
       String AG_FLAG_STORAGE = getFlagStoreDir_Job(lJobID)+  stFeatureID;
       return AG_FLAG_STORAGE;
  }
}
