package com.spime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileMap extends Activity {
    /** Called when the activity is first created. */
	public static String stMapPath="/sdcard/Maps/";
	private Builder builder;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {

			boolean mExternalStorageAvailable = false;
			boolean mExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can read and write the media
				mExternalStorageAvailable = mExternalStorageWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can only read the media
				mExternalStorageAvailable = true;
				mExternalStorageWriteable = false;

			} else {
				// Something else is wrong. It may be one of many other
				// states, but all we need
				// to know is we can neither read nor write
				mExternalStorageAvailable = mExternalStorageWriteable = false;
			}
			File root = Environment.getExternalStorageDirectory();
			if (mExternalStorageAvailable &&mExternalStorageWriteable
					&& root.canWrite()) {
				File gpxfile = new File(root, "MapManini.txt");
				
				
				    
			        // Create a read-only memory-mapped file
			        FileChannel roChannel = 
			          new RandomAccessFile(gpxfile, "r").getChannel();
			          
			        ByteBuffer readonlybuffer = 
			          roChannel.map(FileChannel.MapMode.READ_ONLY, 
			    0, (int)roChannel.size());
			        System.out.println(readonlybuffer.capacity());
			       
			     // Retrieve bytes between the position and limit
			     // (see Putting Bytes into a ByteBuffer)
			    byte[] bytes = new byte[readonlybuffer.capacity()];
			    readonlybuffer.get(bytes, 0, bytes.length);
			  
			    showDialog(new String(bytes));
			      
				gpxfile.createNewFile();
				FileWriter gpxwriter = new FileWriter(gpxfile, true);
				BufferedWriter out = new BufferedWriter(gpxwriter);
				out.write("SPIME123");
				out.close();
			}else{
				
			}
		}catch (FileNotFoundException  e) {
			// TODO: handle exception
			Log.e("Error", "Could not write file " + e.getMessage());
		} catch (java.lang.UnsupportedOperationException e) {
			// TODO: handle exception
			Log.e("Error", "Could not write file " + e.getMessage());
		} 
        catch (IOException e) {
			Log.e("Error", "Could not write file " + e.getMessage());
		}catch (Throwable  e) {
			// TODO: handle exception
			Log.e("Error", "Could not write file " + e.getMessage());
		}
    }
    private void showDialog(String stMessage){
    	  builder = new AlertDialog.Builder(this);
    	 
         builder.setTitle("Spime MapMan");

         builder.setIcon(R.drawable.beer);

         builder.setMessage(stMessage);

         builder.setPositiveButton("Ok", null);

         builder.setNegativeButton("Cancel", null);

         builder.show();

    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    
    	
    }
}