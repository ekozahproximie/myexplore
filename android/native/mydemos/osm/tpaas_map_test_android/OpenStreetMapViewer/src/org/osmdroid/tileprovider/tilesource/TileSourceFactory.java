package org.osmdroid.tileprovider.tilesource;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.alk.ALKMapTileSource;
import org.osmdroid.tileprovider.tilesource.alk.tpaas.TpaasMapTileSource;
import org.osmdroid.tileprovider.tilesource.mapbox.MapboxTileSource;
import org.osmdroid.tileprovider.tilesource.weogeo.HybirdWeoGeoMapTileSource;
import org.osmdroid.tileprovider.tilesource.weogeo.WeoGeoMapTileSource;

import java.util.ArrayList;

public class TileSourceFactory {

	// private static final Logger logger = LoggerFactory.getLogger(TileSourceFactory.class);

	/**
	 * Get the tile source with the specified name.
	 *
	 * @param aName
	 *            the tile source name
	 * @return the tile source
	 * @throws IllegalArgumentException
	 *             if tile source not found
	 */
	public static ITileSource getTileSource(final String aName) throws IllegalArgumentException {
		for (final ITileSource tileSource : mTileSources) {
			if (tileSource.name().equals(aName)) {
				return tileSource;
			}
		}
		throw new IllegalArgumentException("No such tile source: " + aName);
	}

	public static boolean containsTileSource(final String aName) {
		for (final ITileSource tileSource : mTileSources) {
			if (tileSource.name().equals(aName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the tile source at the specified position.
	 *
	 * @param aOrdinal
	 * @return the tile source
	 * @throws IllegalArgumentException
	 *             if tile source not found
	 */
	public static ITileSource getTileSource(final int aOrdinal) throws IllegalArgumentException {
		for (final ITileSource tileSource : mTileSources) {
			if (tileSource.ordinal() == aOrdinal) {
				return tileSource;
			}
		}
		throw new IllegalArgumentException("No tile source at position: " + aOrdinal);
	}

	public static ArrayList<ITileSource> getTileSources() {
		return mTileSources;
	}

	public static void addTileSource(final ITileSource mTileSource) {
		mTileSources.add(mTileSource);
	}

	public static final OnlineTileSourceBase OSMARENDER = new XYTileSource("Osmarender",
			ResourceProxy.string.osmarender, 0, 17, 256, ".png",
			"http://tah.openstreetmap.org/Tiles/tile/");

	public static final OnlineTileSourceBase MAPNIK = new XYTileSource("Mapnik",
			ResourceProxy.string.mapnik, 0, 18, 256, ".png", "http://tile.openstreetmap.org/");

	public static final OnlineTileSourceBase CYCLEMAP = new XYTileSource("CycleMap",
			ResourceProxy.string.cyclemap, 0, 17, 256, ".png",
			"http://a.tile.opencyclemap.org/cycle/",
			"http://b.tile.opencyclemap.org/cycle/",
			"http://c.tile.opencyclemap.org/cycle/");

	public static final OnlineTileSourceBase PUBLIC_TRANSPORT = new XYTileSource(
			"OSMPublicTransport", ResourceProxy.string.public_transport, 0, 17, 256, ".png",
			"http://tile.xn--pnvkarte-m4a.de/tilegen/");

	public static final OnlineTileSourceBase BASE = new XYTileSource("Base",
			ResourceProxy.string.base, 4, 17, 256, ".png", "http://topo.openstreetmap.de/base/");

	public static final OnlineTileSourceBase TOPO = new XYTileSource("Topo",
			ResourceProxy.string.topo, 4, 17, 256, ".png", "http://topo.openstreetmap.de/topo/");

	public static final OnlineTileSourceBase HILLS = new XYTileSource("Hills",
			ResourceProxy.string.hills, 8, 17, 256, ".png", "http://topo.geofabrik.de/hills/");

	public static final OnlineTileSourceBase CLOUDMADESTANDARDTILES = new CloudmadeTileSource(
			"CloudMadeStandardTiles", ResourceProxy.string.cloudmade_standard, 0, 18, 256, ".png",
			"http://a.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s",
			"http://b.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s",
			"http://c.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s");

	// FYI - This tile source has a tileSize of "6"
	public static final OnlineTileSourceBase CLOUDMADESMALLTILES = new CloudmadeTileSource(
			"CloudMadeSmallTiles", ResourceProxy.string.cloudmade_small, 0, 21, 64, ".png",
			"http://a.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s",
			"http://b.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s",
			"http://c.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s");

	public static final OnlineTileSourceBase MAPQUESTOSM =
		new XYTileSource("MapquestOSM", ResourceProxy.string.mapquest_osm, 0, 18, 256, ".png",
				"http://otile1.mqcdn.com/tiles/1.0.0/osm/",
				"http://otile2.mqcdn.com/tiles/1.0.0/osm/",
				"http://otile3.mqcdn.com/tiles/1.0.0/osm/",
				"http://otile4.mqcdn.com/tiles/1.0.0/osm/");

	public static final OnlineTileSourceBase MAPQUESTAERIAL =
		new XYTileSource("MapquestAerial", ResourceProxy.string.mapquest_aerial, 0, 11, 256, ".png",
				"http://oatile1.mqcdn.com/naip/",
				"http://oatile2.mqcdn.com/naip/",
				"http://oatile3.mqcdn.com/naip/",
				"http://oatile4.mqcdn.com/naip/");

	public static final OnlineTileSourceBase DEFAULT_TILE_SOURCE = MAPNIK;

	// The following tile sources are overlays, not standalone map views.
	// They are therefore not in mTileSources.

	public static final OnlineTileSourceBase FIETS_OVERLAY_NL = new XYTileSource("Fiets",
			ResourceProxy.string.fiets_nl, 3, 18, 256, ".png",
			"http://overlay.openstreetmap.nl/openfietskaart-overlay/");

	public static final OnlineTileSourceBase BASE_OVERLAY_NL = new XYTileSource("BaseNL",
			ResourceProxy.string.base_nl, 0, 18, 256, ".png",
			"http://overlay.openstreetmap.nl/basemap/");

	public static final OnlineTileSourceBase ROADS_OVERLAY_NL = new XYTileSource("RoadsNL",
			ResourceProxy.string.roads_nl, 0, 18, 256, ".png",
			"http://overlay.openstreetmap.nl/roads/");
	
	public static final ALKMapTileSource ALKROADMAPTILESOURCE = new ALKMapTileSource(new String[]{
              "http://pcmiler.alk.com/APIs/REST/v1.0/Service.svc/maptile"
        },null,ALKMapTileSource.ROAD);

	public static final ALKMapTileSource ALKAERIALMAPTILESOURCE = new ALKMapTileSource(new String[]{
              "http://pcmiler.alk.com/APIs/REST/v1.0/Service.svc/maptile"
        },null,ALKMapTileSource.AERIAL);
	
	public static final TpaasMapTileSource TPAAS = new TpaasMapTileSource(new String[]{
              "https://api-stg.trimble.com/t/trimble.com/trimblemaps/1.0/services.json?client_id=tpass_demo"
        },null,ALKMapTileSource.AERIAL);
	
	public static final WeoGeoMapTileSource WEOGEO = new WeoGeoMapTileSource(new String[]{
              "https://www.trimblemaps.com/services.json?client_id=DEMO"
        },null,WeoGeoMapTileSource.ROAD);
	
	
	public static final HybirdWeoGeoMapTileSource HWEOGEO = new HybirdWeoGeoMapTileSource(new String[]{
              "https://www.trimblemaps.com/services.json?client_id=DEMO"
        },null,HybirdWeoGeoMapTileSource.HYBIRD);
	
	public static final MapboxTileSource MAP_BOX = new MapboxTileSource(new String[]{
              "https://api.tiles.mapbox.com/v4/%s.json?secure%s&access_token=%s"
        },null,MapboxTileSource.SATELLITE);
	
	private static ArrayList<ITileSource> mTileSources;
	static {
		mTileSources = new ArrayList<ITileSource>();
		mTileSources.add(ALKROADMAPTILESOURCE);
		mTileSources.add(ALKAERIALMAPTILESOURCE);
		mTileSources.add(TPAAS);
		mTileSources.add(WEOGEO);
		mTileSources.add(HWEOGEO);
		mTileSources.add(MAP_BOX);
		
		
//		mTileSources.add(OSMARENDER);
//                mTileSources.add(MAPNIK);
//                mTileSources.add(CYCLEMAP);
//                mTileSources.add(PUBLIC_TRANSPORT);
//                mTileSources.add(BASE);
//                mTileSources.add(TOPO);
//                mTileSources.add(HILLS);
//                mTileSources.add(CLOUDMADESTANDARDTILES);
//                mTileSources.add(CLOUDMADESMALLTILES);
//                mTileSources.add(MAPQUESTOSM);
//                mTileSources.add(MAPQUESTAERIAL);
	}
}
