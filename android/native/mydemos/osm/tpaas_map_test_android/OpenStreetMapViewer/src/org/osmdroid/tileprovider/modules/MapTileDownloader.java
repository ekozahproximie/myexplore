package org.osmdroid.tileprovider.modules;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.apache.http.protocol.HTTP;
import org.osmdroid.MySSLSocketFactory.TrustEveryoneManager;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileRequestState;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase.LowMemoryException;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource;
import org.osmdroid.tileprovider.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * The {@link MapTileDownloader} loads tiles from an HTTP server. It saves downloaded tiles to an
 * IFilesystemCache if available.
 *
 * @author Marc Kurtz
 * @author Nicolas Gramlich
 * @author Manuel Stahl
 *
 */
public class MapTileDownloader extends MapTileModuleProviderBase {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory.getLogger(MapTileDownloader.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private final IFilesystemCache mFilesystemCache;

	private OnlineTileSourceBase mTileSource;

	private final INetworkAvailablityCheck mNetworkAvailablityCheck;
	

	// ===========================================================
	// Constructors
	// ===========================================================

	public MapTileDownloader(final ITileSource pTileSource) {
		this(pTileSource, null, null);
	}

	public MapTileDownloader(final ITileSource pTileSource, final IFilesystemCache pFilesystemCache) {
		this(pTileSource, pFilesystemCache, null);
	}

	public MapTileDownloader(final ITileSource pTileSource,
			final IFilesystemCache pFilesystemCache,
			final INetworkAvailablityCheck pNetworkAvailablityCheck) {
		this(pTileSource, pFilesystemCache, pNetworkAvailablityCheck,
				NUMBER_OF_TILE_DOWNLOAD_THREADS, TILE_DOWNLOAD_MAXIMUM_QUEUE_SIZE);
	}

	public MapTileDownloader(final ITileSource pTileSource,
			final IFilesystemCache pFilesystemCache,
			final INetworkAvailablityCheck pNetworkAvailablityCheck, int pThreadPoolSize,
			int pPendingQueueSize) {
		super(pThreadPoolSize, pPendingQueueSize);

		mFilesystemCache = pFilesystemCache;
		mNetworkAvailablityCheck = pNetworkAvailablityCheck;
		setTileSource(pTileSource);
		
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ITileSource getTileSource() {
		return mTileSource;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean getUsesDataConnection() {
		return true;
	}

	@Override
	protected String getName() {
		return "Online Tile Download Provider";
	}

	@Override
	protected String getThreadGroupName() {
		return "downloader";
	}

	@Override
	protected Runnable getTileLoader() {
		return new TileLoader();
	};

	@Override
	public int getMinimumZoomLevel() {
		return (mTileSource != null ? mTileSource.getMinimumZoomLevel() : MINIMUM_ZOOMLEVEL);
	}

	@Override
	public int getMaximumZoomLevel() {
		return (mTileSource != null ? mTileSource.getMaximumZoomLevel() : MAXIMUM_ZOOMLEVEL);
	}

	@Override
	public void setTileSource(final ITileSource tileSource) {
		// We are only interested in OnlineTileSourceBase tile sources
		if (tileSource instanceof OnlineTileSourceBase) {
			mTileSource = (OnlineTileSourceBase) tileSource;
		} else {
			// Otherwise shut down the tile downloader
			mTileSource = null;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class TileLoader extends MapTileModuleProviderBase.TileLoader {
	   
	   
	
	   final OkHttpClient client = new OkHttpClient();
	// Create an SSLContext that uses our TrustManager
	        private SSLContext sslContext = null;
	        
	   private Response getOkHttpClient(final String stRequestURL) throws IOException {
	      Request request = new Request.Builder()
	          .url(stRequestURL).addHeader(HTTP.CONTENT_TYPE, "text/plain")
	         
	          .build();
	      client.setReadTimeout(15, TimeUnit.SECONDS);
	      client.setConnectTimeout(15, TimeUnit.SECONDS);
	      setSocketFactory();
	      Response response = client.newCall(request).execute();
	      return response;
	    }
	   private void setSocketFactory() {
	      
	      
	      
	      if(sslContext == null){
	         try {
	            final TrustEveryoneManager tm = new TrustEveryoneManager();
	            sslContext = SSLContext.getInstance("TLS");
	            sslContext.init(null, new TrustManager[] { tm }, null);
	         } catch (KeyManagementException e) {
	           
	            e.printStackTrace();
	         } catch (NoSuchAlgorithmException e1) {
	           
	            e1.printStackTrace();
	         }
	         client.setSslSocketFactory(sslContext.getSocketFactory());
	      }
	      
	   }
		@Override
		public Drawable loadTile(final MapTileRequestState aState) throws CantContinueException {

			if (mTileSource == null) {
				return null;
			}

			InputStream in = null;
			OutputStream out = null;
			final MapTile tile = aState.getMapTile();

			try {

				if (mNetworkAvailablityCheck != null
						&& !mNetworkAvailablityCheck.getNetworkAvailable()) {
					if (DEBUGMODE) {
						logger.debug("Skipping " + getName() + " due to NetworkAvailabliltyCheck.");
					}
					return null;
				}

				final String tileURLString = mTileSource.getTileURLString(tile);
				//patch
//				int iBeforeRequestMapStyle=0;
//				if(mTileSource instanceof BingMapTileSource){
//					iBeforeRequestMapStyle= ((BingMapTileSource)mTileSource).getStyle();
//				}
				
				if (DEBUGMODE) {
					logger.debug("Downloading Maptile from url: " + tileURLString);
				}

				if (TextUtils.isEmpty(tileURLString)) {
					return null;
				}

				//final HttpClient client = HttpClientProvider.getInstance(application).getHttpClient();
				
				
			        //final HttpUriRequest head = new HttpGet(tileURLString);
                                //final HttpResponse response = client.execute(head);
                                final Response response2 =getOkHttpClient(tileURLString);
				// Check to see if we got success
				//final org.apache.http.StatusLine line = response2.code();
				if (response2.code() != 200) {
					logger.warn("Problem downloading MapTile: " + tile + " HTTP response: " + response2.code());
					return null;
				}

				final ResponseBody entity = response2.body(); 
				      
				if (entity == null) {
					logger.warn("No content downloading MapTile: " + tile);
					return null;
				}
				in = entity.byteStream();

				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
				StreamUtils.copy(in, out);
				out.flush();
				final byte[] data = dataStream.toByteArray();
				final ByteArrayInputStream byteStream = new ByteArrayInputStream(data);

				// Save the data to the filesystem cache
				if (mFilesystemCache != null) {
					int iAfterRequestMapStyle=0;
					if(mTileSource instanceof BingMapTileSource){
						BingMapTileSource bingMapTileSource =((BingMapTileSource)mTileSource);
//						iAfterRequestMapStyle= bingMapTileSource.getStyle();
//						if(iAfterRequestMapStyle != iBeforeRequestMapStyle){
//							String stMapPath=
//									bingMapTileSource.
//									getTileRelativeFilenameString(tile, iBeforeRequestMapStyle);
//							mFilesystemCache.saveFile(stMapPath,  byteStream);
//							Log.i("te","AfterRequestMapStyle ="+iAfterRequestMapStyle+","+
//							"BeforeRequestMapStyle="+ iBeforeRequestMapStyle);
//							byteStream.reset();
//							return null;
//						}else
						{
							
							mFilesystemCache.saveFile(mTileSource, tile, byteStream);
							byteStream.reset();
							}
					}else{
					
					mFilesystemCache.saveFile(mTileSource, tile, byteStream);
					byteStream.reset();
					}
				}
				final Drawable result = mTileSource.getDrawable(byteStream);

				return result;
			} catch (final UnknownHostException e) {
				// no network connection so empty the queue
				logger.warn("UnknownHostException downloading MapTile: " + tile + " : " + e);
				throw new CantContinueException(e);
			} catch (final LowMemoryException e) {
				// low memory so empty the queue
				logger.warn("LowMemoryException downloading MapTile: " + tile + " : " + e);
				throw new CantContinueException(e);
			} catch (final FileNotFoundException e) {
				logger.warn("Tile not found: " + tile + " : " + e);
			} catch (final IOException e) {
				logger.warn("IOException downloading MapTile: " + tile + " : " + e);
			} catch (final Throwable e) {
				logger.error("Error downloading MapTile: " + tile, e);
			} finally {
				StreamUtils.closeStream(in);
				StreamUtils.closeStream(out);
			}

			return null;
		}

		@Override
		protected void tileLoaded(final MapTileRequestState pState, final Drawable pDrawable) {
			removeTileFromQueues(pState.getMapTile());
			// don't return the tile because we'll wait for the fs provider to ask for it
            // this prevent flickering when a load of delayed downloads complete for tiles
            // that we might not even be interested in any more
			pState.getCallback().mapTileRequestCompleted(pState, null);
		}

	}
}
