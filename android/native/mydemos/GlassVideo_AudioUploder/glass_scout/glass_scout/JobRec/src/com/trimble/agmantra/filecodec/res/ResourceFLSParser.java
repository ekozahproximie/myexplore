package com.trimble.agmantra.filecodec.res;

import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.entity.Client;
import com.trimble.agmantra.entity.Farm;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ResourceFLSParser {
	static ResourceFLSParser mFlsParser = null;
	ResourceParsing mResParse = null;
	ResourceTags mFlsResTags = null;
	FarmWorksContentProvider mDataBase = null;

	public static ResourceFLSParser getInstance(FarmWorksContentProvider mDataBase) {
		if (null == mFlsParser) {
			mFlsParser = new ResourceFLSParser(mDataBase);
		}
		return mFlsParser;
	}
	private ResourceFLSParser(FarmWorksContentProvider mDataBase){
	    this.mDataBase=mDataBase;
	}
	public void flsReader(String sdcard) {

		File myFile = new File(sdcard, "resource.fls");
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(myFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		String aDataRow = "";
		String aBuffer = "";
		try {
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			myReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Toast.makeText(getBaseContext(),
		// "Done reading from SD-CARD 'Resource.fls'",
		// Toast.LENGTH_SHORT).show();

		// now append xml header and write to a new xml file
		String sXMLHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		File myWriteFile = new File(sdcard, "mysdfile.xml");
		try {
			myWriteFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(myWriteFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
		try {
			// myOutWriter.append(sXMLHeader + "\n"); //write content here
			myOutWriter.append(aBuffer); // write content here
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			myOutWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Toast.makeText(getBaseContext(),
		// "Done writing to SD-CARD 'mysdfile.xml'",
		// Toast.LENGTH_SHORT).show();
		//
		// //display to user the content of file
		// Toast.makeText(getBaseContext(),
		// sXMLHeader + "\n" + aBuffer,
		// Toast.LENGTH_LONG).show();

		// parse the newly created XML for parsing
		File myXMLFile = new File(sdcard, "mysdfile.xml");
		FileInputStream fXMLIn = null;
		try {
			fXMLIn = new FileInputStream(myXMLFile);
			try {
				try {
					mResParse = new ResourceParsing(fXMLIn);
					pushResourcesToDB();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// done parsing
	}

	private void pushResourcesToDB() {
		if (null != mResParse.resTags) {
			mFlsResTags = mResParse.resTags;
		
			pushClientList();
			pushFarmList();
			pushFieldList();
		}

	}

	private void pushFieldList() {
		ArrayList<ResAttribField> arrFieldist = mFlsResTags.getArrFieldList();
		if (null != arrFieldist) {
			for (ResAttribField resAttribField : arrFieldist) {
				Field mNewField = new Field();
				mNewField.setArea(Float.toString(resAttribField.getArea()));
				mNewField.setId(Utils.getLongFromHexaString(resAttribField
						.getID()));
				mNewField.setDesc(resAttribField.getDescription());
				mNewField.setIsServerdata(true);
				mNewField.setBoundaryModified(resAttribField.getBdryModified());
				mNewField.setBoundaryRevision(resAttribField.getBdryRevision());
				mNewField.setLocked(resAttribField.getLocked());
				mNewField.setDeleted(resAttribField.getDeleted());
				mNewField.setFarmId(Utils.getLongFromHexaString(resAttribField
						.getFarmID()));
				mNewField.setUnitId(Utils.getLongFromHexaString(resAttribField
						.getAreaUnit()));
				mDataBase.insertField_fls(mNewField);
			}
		}
	}

	private void pushFarmList() {
		ArrayList<ResAttribFarm> arrFarmList = mFlsResTags.getArrFarmList();
		if (null != arrFarmList) {
			for (ResAttribFarm resAttribFarm : arrFarmList) {
				Farm mNewFarm = new Farm();
				mNewFarm.setId(Utils.getLongFromHexaString(resAttribFarm
						.getID()));
				mNewFarm.setDesc(resAttribFarm.getDescription());
				mNewFarm.setIsServerdata(true);
				mNewFarm.setLocked(resAttribFarm.getLocked());
				mNewFarm.setDeleted(resAttribFarm.getDeleted());
				mNewFarm.setClientId(Utils.getLongFromHexaString(resAttribFarm
						.getClientID()));
				mDataBase.insertFarm_fls(mNewFarm);
			}
		}

	}

	private void pushClientList() {
		ArrayList<ComResAttribsEx> arrClientList = mFlsResTags
				.getArrClientList();
		if (null != arrClientList) {
			for (ComResAttribsEx resAttribClient : arrClientList) {
				Client mNewClient = new Client();
				mNewClient.setId(Utils.getLongFromHexaString(resAttribClient
						.getID()));
				mNewClient.setDesc(resAttribClient.getDescription());
				mNewClient.setIsServerdata(true);
				mNewClient.setLocked(resAttribClient.getLocked());
				mNewClient.setDeleted(resAttribClient.getDeleted());
				mDataBase.insertClient_fls(mNewClient);
			}
		}
	}

}