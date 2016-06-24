
package com.trimble.agmantra.filecodec.xml;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.utils.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FieldInfo {

    private FieldInfoValues fieldInfoValues = null;

    private String stFilePath = null;

    /*
     * Constructor
     */
    public FieldInfo(String stFilePath, FieldInfoValues fieldInfoVal) {
        this.fieldInfoValues = fieldInfoVal;
        this.stFilePath = stFilePath;
    }

    /*
     * This method constructs the Field Info XML, based on the current instance
     * values and places it in the path, passed as an argument
     */
    public boolean ConstructXML(String[] stStatus) {
        boolean bRet = false;

        // now create a new xml file and write sXML to it
        // File sdcard = Environment.getExternalStorageDirectory();
        File fieldInfoFile = new File(stFilePath);
        FileOutputStream fOut = null;
        OutputStreamWriter myOutWriter = null;

        try {
            fieldInfoFile.createNewFile();
            fOut = new FileOutputStream(fieldInfoFile);
            myOutWriter = new OutputStreamWriter(fOut);

            String stFiledInfoXml = getXmlString();
            
            // This is not supported in 2.1 sdk - need for xml node verification
             /*String[] stXMLformat = new String[1];
             bRet = Utils.getFormatedXMLString(stFiledInfoXml, stXMLformat);

            if (bRet && null != stXMLformat[0]) {
                myOutWriter.write(stXMLformat[0]);
            }*/          
            myOutWriter.write(stFiledInfoXml);
            
            bRet = true;
            
        } catch (FileNotFoundException e) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
            } else {

                stStatus[0] = Constants.SDCARD_NO_SPACE;

                e.printStackTrace();
            }
            return false;
        } catch (IOException e) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
            } else {

                stStatus[0] = Constants.SDCARD_NO_SPACE;

                e.printStackTrace();
            }
            return false;
        } finally {

            if (myOutWriter != null) {
                try {
                    myOutWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myOutWriter = null;
            }
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fOut = null;
            }

        }

        return bRet;
    }

    private String getXmlString() {

        StringBuilder stXmlNode = new StringBuilder();
        stXmlNode.append("<field_info>");

        // project id population
        stXmlNode.append("<prjid>");
        stXmlNode.append(fieldInfoValues.sPrjID);
        stXmlNode.append("</prjid>");

        // field population
        stXmlNode.append("<field id=\"");
        stXmlNode.append(fieldInfoValues.sFieldID);
        stXmlNode.append("\">");

        stXmlNode.append("<desc>");
        stXmlNode.append(Utils.getEscapedXMLString(fieldInfoValues.sFieldDesc));
        stXmlNode.append("</desc>");

        stXmlNode.append("<area unit=\"");
        stXmlNode.append(fieldInfoValues.sFieldAreaUnitID);
        stXmlNode.append("\">");
        stXmlNode.append(Float.toString(fieldInfoValues.fFieldArea));
        stXmlNode.append("<unitdesc>");
        stXmlNode.append(fieldInfoValues.sFieldAreaUnitDesc);
        stXmlNode.append("</unitdesc></area>");

        // Include the client only if the client ID is not 0x80000000
        if (!fieldInfoValues.sClientID.equals(Utils
                .getHexaStringFromLong(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID))) {
            stXmlNode.append("<clientid>");
            stXmlNode.append(fieldInfoValues.sClientID);
            stXmlNode.append("</clientid>");
        }

        // Include the farm only if the farm ID is not 0x80000000
        if (!fieldInfoValues.sFarmID.equals(Utils
                .getHexaStringFromLong(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID))) {
            stXmlNode.append("<farmid>");
            stXmlNode.append(fieldInfoValues.sFarmID);
            stXmlNode.append("</farmid>");
        }
        stXmlNode.append("<boundary>");
        stXmlNode.append("<revision>");
        stXmlNode.append(Integer.toString(fieldInfoValues.nFieldBdryRevision));
        stXmlNode.append("</revision>");
        stXmlNode.append("<modified>");
        stXmlNode.append(Integer.toString(fieldInfoValues.nFieldBdryModified));
        stXmlNode.append("</modified>");
        stXmlNode.append("</boundary></field>");

        // Include the farm only if the farm ID is not 0x80000000
        if (!fieldInfoValues.sFarmID.equals(Utils
                .getHexaStringFromLong(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID))) {
            // farm population
            stXmlNode.append("<farm id=\"");
            stXmlNode.append(fieldInfoValues.sFarmID);
            stXmlNode.append("\">");
            stXmlNode.append("<desc>");
            stXmlNode.append(Utils.getEscapedXMLString(fieldInfoValues.sFarmDesc));
            stXmlNode.append("</desc>");
            stXmlNode.append("<area unit=\"");
            stXmlNode.append(fieldInfoValues.sFarmAreaUnitID);
            stXmlNode.append("\">");
            stXmlNode.append(Float.toString(fieldInfoValues.fFarmArea));
            stXmlNode.append("<unitdesc>");
            stXmlNode.append(fieldInfoValues.sFarmAreaUnitDesc);
            stXmlNode.append("</unitdesc></area>");

            // Include the clientid only if the clientID is not 0x80000000
            if (!fieldInfoValues.sClientID.equals(Utils
                    .getHexaStringFromLong(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID))) {
                stXmlNode.append("<clientid>");
                stXmlNode.append(fieldInfoValues.sClientID);
                stXmlNode.append("</clientid>");
            }

            stXmlNode.append("</farm>");
        }

        // Include the client only if the client ID is not 0x80000000
        if (!fieldInfoValues.sClientID.equals(Utils
                .getHexaStringFromLong(FarmWorksContentProvider.D_UNKNOWN_CFFE_ID))) {
            // client population
            stXmlNode.append("<client id=\"");
            stXmlNode.append(fieldInfoValues.sClientID);
            stXmlNode.append("\">");
            stXmlNode.append("<desc>");
            stXmlNode.append(Utils.getEscapedXMLString(fieldInfoValues.sClientDesc));
            stXmlNode.append("</desc>");
            stXmlNode.append("</client>");
        }
        // end xml
        stXmlNode.append("</field_info>");

        return stXmlNode.toString();
    }
}
