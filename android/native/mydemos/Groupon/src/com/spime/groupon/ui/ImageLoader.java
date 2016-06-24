package com.spime.groupon.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import com.spime.groupon.R;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;



public class ImageLoader {

	// the simplest in-memory cache implementation. This should be replaced with
	// something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
	private HashMap<String, Uri> cache = new HashMap<String, Uri>();
	private File cacheDir;
	PhotosQueue photosQueue = new PhotosQueue();
	PhotosLoader photoLoaderThread = new PhotosLoader();
	final int stub_id = R.drawable.icon;
	
	public ImageLoader(Context context) {
		// Make the background thead low priority. This way it will not affect
		// the
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"Android/data/"+context.getPackageName()+"/cache/");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			System.out.println(cacheDir.mkdirs());
	}

	

	public void DisplayImage(String url, Activity activity,
			ImageView imageView, int scaleSize) {
		if (cache.containsKey(url))
			imageView.setImageURI(cache.get(url));
		else {
			queuePhoto(url, activity, imageView, scaleSize);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, Activity activity, ImageView imageView,
			int scaleSize) {
		// This ImageView may be used for other images before. So there may be
		// some
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView, scaleSize);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}
		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	private Uri getUri(String url, int scaleSize) {
		if (url != "") {
			// I identify images by hashcode. Not a perfect solution, good for
			// the demo.
			// try{
			String filename = String.valueOf(url.hashCode());
			File f = new File(cacheDir, filename);
			if (f.exists()) {
				Uri b = Uri.fromFile(f);
				System.out.println(f.toString());
				return b;
			}
			// from web
			try {
				Uri bitmap = null;
				InputStream is = new URL(url).openStream();
				
				FileOutputStream os = new FileOutputStream(f);
				 final int buffer_size=1024;
			        try
			        {
			            byte[] bytes=new byte[buffer_size];
			            for(;;)
			            {
			              int count=is.read(bytes, 0, buffer_size);
			              if(count==-1)
			                  break;
			              os.write(bytes, 0, count);
			            }
			        }
			        catch(Exception ex){
			        	ex.printStackTrace();
			        }
				os.close();
				bitmap = Uri.fromFile(f);
				System.out.println(f.toString());
				return bitmap;
			} catch (Throwable ex) {
				ex.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public int scaleSize;

		public PhotoToLoad(String u, ImageView i, int ss) {
			url = u;
			imageView = i;
			scaleSize = ss;
		}
	}

	
	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	// stores list of photos to download
	class PhotosQueue {

		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size();) {
				if (photosToLoad.get(j).imageView == image)
					photosToLoad.remove(j);
				else
					++j;
			}
		}
	}
	
	class PhotosLoader extends Thread {
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Uri bmp = getUri(photoToLoad.url, photoToLoad.scaleSize);
						cache.put(photoToLoad.url, bmp);
						if (((String) photoToLoad.imageView.getTag())
								.equals(photoToLoad.url)) {
							UriDisplayer bd = new UriDisplayer(bmp,
									photoToLoad.imageView);
							Activity a = (Activity) photoToLoad.imageView
									.getContext();
							a.runOnUiThread(bd);
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	

	class UriDisplayer implements Runnable {
		public static final String SPINNER_IMAGELOADER = "SPIME";
		Uri uri;
		ImageView imageView;

		public UriDisplayer(Uri u, ImageView i) {
			uri = u;
			imageView = i;
		}

		public void run() {
			Log.d(SPINNER_IMAGELOADER,
					"rui displayer using uri path: " + uri.getPath());
			File f = new File(uri.getPath());
			Log.d(SPINNER_IMAGELOADER, "file: " + f);
			if (f.exists()) {
				//imageView.setImageURI(uri);         
				//FIX DO IT THIS WAY 
				imageView.setImageURI(Uri.parse(f.toString()));
			} else {
				imageView.setImageResource(stub_id);
			}
		}
	}

	public void clearCache() {
		// clear memory cache
		cache.clear();
		// clear SD cache
		File[] files = cacheDir.listFiles();
		for (File f : files)
			f.delete();
	}
}
