package org.osmdroid.tileprovider.tilesource.mapbox;

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
import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.IStyledTileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MapboxTileSource extends XYTileSource implements
      IStyledTileSource<Integer> {

   private static String         mapboxMapId             = null;
   private static String         secretKey               = null;
   private static final String   MANIFEST_MAP_ID_KEY     = "MapBoxMapId";
   private static final String   MANIFEST_MAP_SECRET_KEY = "MapBoxSecretKey";

   private static final Logger   logger                  = LoggerFactory
                                                               .getLogger(MapboxTileSource.class);
   private static String         m_baseUrl;

   private static final String   FILENAME_ENDING         = ".png";

   public static final String    MAP_NAME                = "MapBox";

   public static final int       SATELLITE               = 1;

   private static final String[] M_IMAGERYSET            = { "satellite" };

   private static final int      MIN_ZOOM                = 1;

   private static final int      MAX_ZOOM                = 20;

   private static final int      TILE_SIZE               = 256;

   // baseURl used for OnlineTileSourceBase override

   private Integer               m_style                 = SATELLITE;

   public MapboxTileSource(String aName, string aResourceId, int aZoomMinLevel,
         int aZoomMaxLevel, int aTileSizePixels, String aImageFilenameEnding,
         String[] aBaseUrl, String mapId, final Context appContext) {
      super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
            aImageFilenameEnding, aBaseUrl);

      getKeys(appContext);
   }

   public MapboxTileSource(String[] aBaseUrl, Context context, final int iStyle) {

      super(MAP_NAME, iStyle == SATELLITE ? ResourceProxy.string.mapbox
            : ResourceProxy.string.alkaerial, MIN_ZOOM, MAX_ZOOM, TILE_SIZE,
            FILENAME_ENDING, aBaseUrl);

      getKeys(context);
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
      // m_baseUrl =
// "http://pcmiler.alk.com/APIs/REST/v1.0/Service.svc/maptile";
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
      if (m_style == null || m_style < SATELLITE
            || m_style > M_IMAGERYSET.length) {
         return "";
      } else {
         return M_IMAGERYSET[m_style - 1];
      }
   }

   @Override
   public Integer getStyle() {
      return m_style;

   }

   public static void getKeys(final Context aContext) {
      if (aContext == null) {
         return;
      }
      final PackageManager pm = aContext.getPackageManager();
      try {
         final ApplicationInfo info = pm.getApplicationInfo(
               aContext.getPackageName(), PackageManager.GET_META_DATA);
         if (info.metaData == null) {
            logger.info("ApplicationInfo metadata not found");
         } else {

            String key = info.metaData.getString(MANIFEST_MAP_ID_KEY);
            if (key == null) {
               logger.info(MANIFEST_MAP_ID_KEY + "key not found in manifest");
            } else {
               if (DEBUGMODE) {
                  logger.debug("Map ID: " + key);
               }
               mapboxMapId = key.trim();
            }

            String seckey = info.metaData.getString(MANIFEST_MAP_SECRET_KEY);

            if (seckey == null) {
               logger.info(MANIFEST_MAP_SECRET_KEY
                     + " key not found in manifest");
            } else {
               if (DEBUGMODE) {
                  logger.debug("Map Secret Key: " + seckey);
               }
               secretKey = seckey.trim();
            }
         }
      } catch (final PackageManager.NameNotFoundException e) {
         logger.info(MANIFEST_MAP_SECRET_KEY + "/" + MANIFEST_MAP_ID_KEY
               + " key not found in manifest");
      }
   }

   @Override
   public String getTileURLString(final MapTile pTile) {
      String test = "";
      if (m_baseUrl == null) {
         try {
            initMetaData();

         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      test = String.format(m_baseUrl == null ? "" : m_baseUrl,
            pTile.getZoomLevel(), pTile.getX(), pTile.getY());

      return test;
   }

   public synchronized void initMetaData() throws Exception {
      logger.trace("initMetaData");

      // Roads mode
      final HttpClient client = new DefaultHttpClient();
      final String stURL = String.format(getBaseUrl(), mapboxMapId,mapboxMapId, secretKey);
      HttpUriRequest head = new HttpGet(stURL);
      Log.i("test", "stURL:"+stURL);
      logger.debug("make request " + head.getURI().toString());
      HttpResponse response = client.execute(head);

      HttpEntity entity = response.getEntity();

      if (entity == null) {
         throw new Exception("Cannot get response for url "
               + head.getURI().toString());
      }

      InputStream in = entity.getContent();
      ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
      BufferedOutputStream out = new BufferedOutputStream(dataStream,
            StreamUtils.IO_BUFFER_SIZE);
      StreamUtils.copy(in, out);
      out.flush();
      Log.i("test", "dataStream.toString():" + dataStream.toString());
      final JSONObject jsonObject = new JSONObject(dataStream.toString());
      final JSONArray tiles = jsonObject.getJSONArray("tiles");
      m_baseUrl = tiles.getString(0);

      m_baseUrl = m_baseUrl.replace("{z}", "%s");
      m_baseUrl = m_baseUrl.replace("{y}", "%s");
      m_baseUrl = m_baseUrl.replace("{x}", "%s");
      Log.i("test", "mBaseURL:" + m_baseUrl);
      client.getConnectionManager().shutdown();
      logger.trace("end initMetaData");
   }
}
