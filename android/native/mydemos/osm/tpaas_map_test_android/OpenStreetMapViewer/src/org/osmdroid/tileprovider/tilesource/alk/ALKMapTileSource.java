package org.osmdroid.tileprovider.tilesource.alk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.IStyledTileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.util.Mercator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ALKMapTileSource extends XYTileSource implements
      IStyledTileSource<Integer> {

// ALK Map REST services return png images
   private static final String   FILENAME_ENDING      = ".png";

   public static final String    MAP_NAME             = "ALKMap";

   private static final String   MANIFEST_ALK_MAP_KEY = "ALK_KEY";

   private static final Logger   logger               = LoggerFactory
                                                            .getLogger(ALKMapTileSource.class);

   public static final int       ROAD                 = 1;
   
   public static final int       AERIAL                 = 2;

   private static String         sALKMap              = null;

   private static final String   URL_FORMAT           = "%s?x=%s&y=%s&z=%s&style=%s&srs=EPSG:900913&region=%s&dataset=%s&AuthToken=%s";

   private static final String[] M_IMAGERYSET         = { "default",
         "satellite", "AerialWithLabels"             };

   private static final int      MIN_ZOOM             = 1;

   private static final int      MAX_ZOOM             = 20;

   private static final int      TILE_SIZE            = 256;

   public static final double    NA_Latitude_MIN      = 15;

   public static final double    NA_Latitude_MAX      = 75;

   public static final double    NA_Longitude_MAX     = -53;

   public static final double    NA_Longitude_MIN     = -171;

   public static final double    SA_Latitude_MIN      = -58;

   public static final double    SA_Latitude_MAX      = 17;

   public static final double    SA_Longitude_MAX     = -32;

   public static final double    SA_Longitude_MIN     = -95;

   public static final double    ME_Latitude_MIN      = 12;

   public static final double    ME_Latitude_MAX      = 35;

   public static final double    ME_Longitude_MAX     = 60;

   public static final double    ME_Longitude_MIN     = 34;

   public static final double    EU_Latitude_MIN      = 34;

   public static final double    EU_Latitude_MAX      = 72;

   public static final double    EU_Longitude_MAX     = 70;

   public static final double    EU_Longitude_MIN     = -30;

   public static final double    AF_Latitude_MIN      = -40;

   public static final double    AF_Latitude_MAX      = 37;

   public static final double    AF_Longitude_MAX     = 56;

   public static final double    AF_Longitude_MIN     = -30;

   public static final double    OC_Latitude_MIN      = -52;

   public static final double    OC_Latitude_MAX      = -9;

   public static final double    OC_Longitude_MIN     = 106;

   public static final double    OC_Longitude_MAX     = 179.99;

   public static final double    AS_Latitude_MIN      = -14;

   public static final double    AS_Latitude_MAX      = 54;

   public static final double    AS_Longitude_MIN     = 60;

   public static final double    AS_Longitude_MAX     = 148;

// baseURl used for OnlineTileSourceBase override
   private String                m_baseUrl;

   private Integer               m_style              = ROAD;

   public ALKMapTileSource(String aName, ResourceProxy.string aResourceId,
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

   public ALKMapTileSource(String[] aBaseUrl, Context context,final int iStyle) {

      super(MAP_NAME,iStyle == ROAD ? ResourceProxy.string.alkroad:
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
      if (m_style == null || m_style < ROAD || m_style > M_IMAGERYSET.length) {
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
      final String region = update_region(pTile);
      final StringBuilder region_dataset = new StringBuilder("PCM_")
            .append(region);

      String test = String.format(URL_FORMAT, getBaseUrl(), pTile.getX(),
            pTile.getY(), pTile.getZoomLevel(), M_IMAGERYSET[getStyle() - 1],
            region, region_dataset, sALKMap);
      return test;
   }

   public static String update_region(final MapTile pTile) {
      String region = "NA";
      if (pTile.getZoomLevel() < MIN_ZOOM) {
         return region;
      } else {
         double latitude = Mercator
               .tile2lat(pTile.getY(), pTile.getZoomLevel());
         double longitude = Mercator.tile2lon(pTile.getX(),
               pTile.getZoomLevel());

         if ((latitude > NA_Latitude_MIN) && (latitude < NA_Latitude_MAX)
               && (longitude < NA_Longitude_MAX)
               && (longitude > NA_Longitude_MIN)) {
            region = "NA";
         } else if ((latitude > SA_Latitude_MIN)
               && (latitude < SA_Latitude_MAX)
               && (longitude < SA_Longitude_MAX)
               && (longitude > SA_Longitude_MIN)) {
            region = "SA";

         } else if ((latitude > ME_Latitude_MIN)
               && (latitude < ME_Latitude_MAX)
               && (longitude < ME_Longitude_MAX)
               && (longitude > ME_Longitude_MIN)) {
            region = "ME";

         } else if ((latitude > EU_Latitude_MIN)
               && (latitude < EU_Latitude_MAX)
               && (longitude < EU_Longitude_MAX)
               && (longitude > EU_Longitude_MIN)) {
            region = "EU";

         } else if ((latitude > AF_Latitude_MIN)
               && (latitude < AF_Latitude_MAX)
               && (longitude < AF_Longitude_MAX)
               && (longitude > AF_Longitude_MIN)) {
            region = "AF";

         } else if ((latitude > OC_Latitude_MIN)
               && (latitude < OC_Latitude_MAX)
               && (longitude < OC_Longitude_MAX)
               && (longitude > OC_Longitude_MIN)) {
            region = "OC";

         } else if ((latitude > AS_Latitude_MIN)
               && (latitude < AS_Latitude_MAX)
               && (longitude < AS_Longitude_MAX)
               && (longitude > AS_Longitude_MIN)) {
            region = "AS";

         }
      }
      return region;
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
