/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.filemonitor
 *
 * File name: RecursiveFileObserver.java
 *
 * Author: sprabhu
 *
 * Created On: Sep 30, 201411:48:54 AM
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.filemonitor;

import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author sprabhu
 *
 */
public class RecursiveFileObserver extends FileObserver {

   public static int           CHANGES_ONLY = MOVE_SELF | MOVED_FROM | MOVED_TO
                                                  | MODIFY | CLOSE_WRITE;

   private static final String LOG          = RecursiveFileObserver.class
                                                  .getSimpleName();
   List<SingleFileObserver>    mObservers;
   String                      mPath;
   int                         mMask;
   private transient ZipJob zipJob =null;

   public RecursiveFileObserver(String path,final ZipJob zipJob) {
      this(path, ALL_EVENTS);
      this.zipJob=zipJob;
   }

   public RecursiveFileObserver(String path, int mask) {
      super(path, mask);
      mPath = path;
      mMask = mask;
   }

   /**
    * @return the mPath
    */
   public String getPath() {
      return mPath;
   }
   @Override
   public void startWatching() {
      if (mObservers != null) {
         return;

      }
      mObservers = new ArrayList<SingleFileObserver>();
      Stack<String> stack = new Stack<String>();
      stack.push(mPath);

      while (!stack.empty()) {
         final String parent = stack.pop();
         addFileObserver(parent, mMask);
         File path = new File(parent);
         File[] files = path.listFiles();
         if (files == null) {
            continue;
         }
         for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory() && !files[i].getName().equals(".")
                  && !files[i].getName().equals("..")) {
               stack.push(files[i].getPath());
            }
         }
      }
      for (int i = 0; i < mObservers.size(); i++)
         mObservers.get(i).startWatching();
   }

   @Override
   public void stopWatching() {
      if (mObservers == null) {
         return;

      }

      for (int i = 0; i < mObservers.size(); ++i) {
         mObservers.get(i).stopWatching();
      }

      mObservers.clear();
      mObservers = null;
   }

   @Override
   public void onEvent(int event, String path) {
      //Log.i(LOG, "onEvent:" + eventToString(event, path) + " :" + path);
      zipJob.addFileToZip(path);
   }
   @Override
   protected void finalize() {
      Log.i(LOG, "RecursiveFileObserver finalize :" + mPath );
      super.finalize();
      
   }
   private String eventToString(int event, final String stPath) {
      String stEvent = null;
      event &= FileObserver.ALL_EVENTS;
      switch (event) {
         case ACCESS:
            stEvent = "ACCESS";
            break;

         case MODIFY:
            stEvent = "MODIFY";
            break;

         case ATTRIB:
            stEvent = "ATTRIB";
            break;

         case CLOSE_WRITE:
            stEvent = "CLOSE_WRITE";
            break;

         case CLOSE_NOWRITE:
            stEvent = "CLOSE_NOWRITE";
            break;

         case OPEN:
            stEvent = "OPEN";
            break;

         case MOVED_FROM:
            stEvent = "MOVED_FROM";
            break;

         case MOVED_TO:
            stEvent = "MOVED_TO";
            break;
         case 1073742080:
         case CREATE:
            stEvent = "CREATE";
            final File file = new File(stPath);
            if (file.isDirectory()) {
               final SingleFileObserver fileObserver = addFileObserver(stPath,
                     mMask);
               if (fileObserver != null) {
                  fileObserver.startWatching();
               }
            }
            break;

         case DELETE:
            stEvent = "DELETE";
            break;
         case DELETE_SELF:
            stEvent = "DELETE_SELF";
            break;

         case MOVE_SELF:
            stEvent = "MOVE_SELF";
            break;

         case ALL_EVENTS:
            stEvent = "ALL_EVENTS";
            break;
         default:
            stEvent = String.valueOf(event);
            break;
      }
      return stEvent;
   }

   private SingleFileObserver addFileObserver(final String parent,
         final int mMask) {
      SingleFileObserver singleFileObserver = null;
      if (mObservers != null) {
         singleFileObserver = new SingleFileObserver(parent, mMask);
         mObservers.add(singleFileObserver);
      }
      return singleFileObserver;
   }

   private class SingleFileObserver extends FileObserver {

      private String mPath;

      public SingleFileObserver(String path, int mask) {
         super(path, mask);
         Log.i(LOG, "Add file monitor for " + path + " mask:" + mask);
         mPath = path;
      }

      @Override
      public void onEvent(int event, String path) {
         final StringBuilder newPath = new StringBuilder(mPath);
         newPath.append(File.separator);
         newPath.append(path);
         RecursiveFileObserver.this.onEvent(event, newPath.toString());
      }
      
      @Override
      protected void finalize() {
         Log.i(LOG, "SingleFileObserver finalize :" + mPath );
         super.finalize();
        
      }
   }

}
