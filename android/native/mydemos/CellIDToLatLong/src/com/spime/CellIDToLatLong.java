package com.spime;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CellIDToLatLong extends Activity {
	 GsmCellLocation location;
	    int cellID, lac;
	    private static final int MY_NOTIFICATION_ID = 0x100;
	     TextView tvCellID=null;
	     TextView tvLoc=null;
	     TextView tvMcc=null;
	     TextView tvMnc=null;
	     TextView tvSignalLength =null;
	     private int cid,  mcc, mnc, cellPadding;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);    
        final Button btDisplay=(Button)findViewById(R.id.displayMap);
          tvCellID=(TextView)findViewById(R.id.cellid);
          tvLoc=(TextView)findViewById(R.id.loc);
          tvMcc=(TextView)findViewById(R.id.mcc);
          tvMnc=(TextView)findViewById(R.id.mnc);
          
         TextView tvSignalLength=(TextView)findViewById(R.id.signallength);
        TelephonyManager tm  = 
            (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
        location = (GsmCellLocation) tm.getCellLocation();
        cellID = location.getCid();
        lac = location.getLac();
        tvCellID.setText(""+cellID);
        tvLoc.setText(""+lac);
        /*
         * Mcc and mnc is concatenated in the networkOperatorString. The first 3
         * chars is the mcc and the last 2 is the mnc.
         */
        String networkOperator = tm.getNetworkOperator();
        if (networkOperator != null && networkOperator.length() > 0) {
         try {
          mcc = Integer.parseInt(networkOperator.substring(0, 3));
          tvMcc.setText(""+mcc);
          mnc = Integer.parseInt(networkOperator.substring(3));
          tvMnc.setText(""+mnc);
         } catch (NumberFormatException e) {
        	 e.printStackTrace();
         }
        }

        
        btDisplay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			getInfo();	
			}
		});
    }
    public void getInfo() {

        
        try {
			displayMap(cellID, lac);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    private boolean displayMap(int cellID, int lac) throws Exception 
    {
        String urlString = "http://www.google.com/glm/mmap";            
    
        //---open a connection to Google Maps API---
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) conn;        
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true); 
        httpConn.setDoInput(true);
        httpConn.connect(); 
        
        //---write some custom data to Google Maps API---
        OutputStream outputStream = httpConn.getOutputStream();
        WriteData(outputStream, cellID, lac);       
        
        //---get the response---
        InputStream inputStream = httpConn.getInputStream();  
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        
        //---interpret the response obtained---
        dataInputStream.readShort();
        dataInputStream.readByte();
        int code = dataInputStream.readInt();
        if (code == 0) {
            double lat = (double) dataInputStream.readInt() / 1000000D;
            double lng = (double) dataInputStream.readInt() / 1000000D;
            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readUTF();
            
            //---display Google Maps---
            String uriString = "geo:" + lat
                + "," + lng;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
                Uri.parse(uriString));
            startActivity(intent);
            return true;
        }
        else
        {        	
        	return false;
        }
    }  
 

    private void WriteData(OutputStream out, int cellID, int lac) 
    throws IOException
    {    	
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cellID);  
        dataOutputStream.writeInt(lac);     

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();    	
    }
    

}