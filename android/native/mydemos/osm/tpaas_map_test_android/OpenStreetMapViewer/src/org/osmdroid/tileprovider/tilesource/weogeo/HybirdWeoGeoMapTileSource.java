package org.osmdroid.tileprovider.tilesource.weogeo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.IStyledTileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class HybirdWeoGeoMapTileSource extends XYTileSource implements
      IStyledTileSource<Integer> {

// ALK Map REST services return png images
   private static final String   FILENAME_ENDING      = ".png";

   public static final String    MAP_NAME             = "HWeoGeo";

   private static final String   MANIFEST_ALK_MAP_KEY = "ALK_KEY";

   private static final Logger   logger               = LoggerFactory
                                                            .getLogger(HybirdWeoGeoMapTileSource.class);

   public static final int       HYBIRD                 = 1;
   
   private static String         sALKMap              = null;

   

   private static final String[] M_IMAGERYSET         = { "hybird" };

   private static final int      MIN_ZOOM             = 1;

   private static final int      MAX_ZOOM             = 20;

   private static final int      TILE_SIZE            = 256;


// baseURl used for OnlineTileSourceBase override
   private String                m_baseUrl = null;

   private Integer               m_style              = HYBIRD;

   public HybirdWeoGeoMapTileSource(String aName, ResourceProxy.string aResourceId,
         int aZoomMinLevel, int aZoomMaxLevel, int aTileSizePixels,
         String aImageFilenameEnding, String[] aBaseUrl,
         final Context appContext) {
      super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
            aImageFilenameEnding, aBaseUrl);
      setMapKey(appContext, aBaseUrl);

   }

   private void setMapKey(final Context appContext, final String[] aBaseUrl) {

      retrieveALKKey(appContext);
// setMapKey(appContext, aBaseUrl);
   }

   public HybirdWeoGeoMapTileSource(String[] aBaseUrl, Context context,final int iStyle) {

      super(MAP_NAME,iStyle == HYBIRD ? ResourceProxy.string.hweogeo:
                                       ResourceProxy.string.alkaerial, MIN_ZOOM, MAX_ZOOM,
            TILE_SIZE, FILENAME_ENDING, aBaseUrl);
      setMapKey(context, aBaseUrl);
      setStyle(iStyle);
   }

   /**
    * Resolves url patterns to update urls with current map view mode and
    * available sub domain.<br>
    * When several subdomains are available, change current sub domain in a
    * cycle manner
    */
   protected void updateBaseUrl()

   {
// m_baseUrl = "http://pcmiler.alk.com/APIs/REST/v1.0/Service.svc/maptile";
      logger.trace("updateBaseUrl");
      logger.debug("updated url = " + m_baseUrl);
      logger.trace("end updateBaseUrl");
   }

   /**
    * get the base path used for caching purpose
    * 
    * @return a base path built on name given as constructor parameter and
    *         current style name
    */
   @Override
   public String pathBase() {
      return mName + getStyleName(m_style);
   }

   /**
    * get the base path used for caching purpose
    * 
    * @return a base path built on name given as constructor parameter and
    *         current style name
    */

   public String pathBase(int iStyle) {
      return mName + getStyleName(iStyle);
   }

   /*--------------- IStyledTileSource --------------------*/

   @Override
   public void setStyle(final Integer pStyle) {
      boolean updateBaseUrl = false;
      if (m_style.intValue() != pStyle.intValue()) {
         updateBaseUrl = true;
      }
      m_style = pStyle;
// mode has been change, url pattern resolution should be updated
      if (updateBaseUrl) {
         updateBaseUrl();
      }
   }

   @Override
   public void setStyle(final String pStyle) {
      final Integer oldStyle = m_style;
      m_style = Integer.getInteger(pStyle);
// mode has been change, url pattern resolution should be updated
      if (m_style.intValue() != oldStyle.intValue()) {
         updateBaseUrl();
      }
   }

   /**
    * get current style name
    * 
    * @return name associated to the current map view mode
    */
   private String getStyleName(Integer m_style) {
      if (m_style == null || m_style < HYBIRD || m_style > M_IMAGERYSET.length) {
         return "";
      } else {
         return M_IMAGERYSET[m_style - 1];
      }
   }

   @Override
   public Integer getStyle() {
      return m_style;

   }

   /**
    * get the url to invoke to retrieve image for input tile
    * 
    * @param pTile
    *           the input tile
    * @return the associated url
    */
   @Override
   public String getTileURLString(final MapTile pTile) {
      String test = "";
      if(m_baseUrl == null){
         try {
            initMetaData();
           
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      test = String.format(m_baseUrl == null? "":m_baseUrl , pTile.getZoomLevel(), pTile.getY(),
            pTile.getX());
      
      return test;
   }

   
   public  synchronized void initMetaData() throws Exception
   {
           logger.trace("initMetaData");

             // Roads mode
           final HttpClient client = new DefaultHttpClient();
         
           HttpUriRequest head = new HttpGet(getBaseUrl());
           head.addHeader("X-API-TOKEN" , "81ee3218-68a7-4a93-a4d2-1e34a3e0fab9");
           logger.debug("make request "+head.getURI().toString());
       HttpResponse response = client.execute(head);

   HttpEntity entity = response.getEntity();

   if (entity == null) {
                   throw new Exception("Cannot get response for url "+head.getURI().toString());
           }

   InputStream in = entity.getContent();
   ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
   BufferedOutputStream out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
           StreamUtils.copy(in, out);
           out.flush();
           
           final JSONObject jsonObject = new JSONObject(dataStream.toString());
           final JSONArray item = jsonObject.getJSONArray("items");
               out: for (int i = 0; i < item.length(); i++) {
                  if(i != 1){
                     continue;
                  }

                  final JSONObject jsonObject2 = item.getJSONObject(i);
                  final JSONArray tile_url_templates = jsonObject2
                        .getJSONArray("tile_url_templates");
                  for (int j = 0; j < tile_url_templates.length(); j++) {
                     m_baseUrl = tile_url_templates.getString(j);
                     if (m_baseUrl != null && j == 1) {
                        break out;
                     }
                  }
               }
          

           m_baseUrl=  m_baseUrl.replace("${z}", "%s");
           m_baseUrl= m_baseUrl.replace("${y}", "%s");
           m_baseUrl= m_baseUrl.replace("${x}", "%s");
           Log.i("test", "mBaseURL:"+m_baseUrl);
           client.getConnectionManager().shutdown();
           logger.trace("end initMetaData");
   }
   /**
    * Read the API key from the manifest.<br>
    * This method should be invoked before class instantiation.<br>
    */
   public static void retrieveALKKey(final Context aContext) {
      if(aContext == null){
         return;
      }
// get the key from the manifest
      final PackageManager pm = aContext.getPackageManager();
      try {
         final ApplicationInfo info = pm.getApplicationInfo(
               aContext.getPackageName(), PackageManager.GET_META_DATA);
         if (info.metaData == null) {
            logger.info("ALK key not found in manifest");
         } else {

            String key = info.metaData.getString(MANIFEST_ALK_MAP_KEY);
            if (key == null) {
               logger.info("ALK key not found in manifest");
            } else {
               if (DEBUGMODE) {
                  logger.debug("ALK key: " + key);
               }
               sALKMap = key.trim();
            }
         }
      } catch (final PackageManager.NameNotFoundException e) {
         logger.info("ALK key not found in manifest", e);
      }
   }

}
