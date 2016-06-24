package com.trimble.reporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.trimble.agent.R;
import com.trimble.reporter.looper.LooperThread;
import com.trimble.reporter.utils.Utils;

public class IncidentResolveActivity extends BaseActivity {

	public static final int RESLOVE_INCIDENT = 101;

	public static final String INCIDENT_ID = "incidentid";

	private String stIncidentID = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.resloveincident);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			stIncidentID = bundle.getString(INCIDENT_ID);
		}
	}

	public void onClick(View onViewClicked) {
		switch (onViewClicked.getId()) {
		case R.id.cancel:
			setResult(RESULT_CANCELED);
		
			finish();

			break;
		case R.id.send:
			
			EditText editText = (EditText) findViewById(R.id.txt_notes);
			String stComments = editText.getText().toString();
			try {
				stComments = URLEncoder.encode(stComments, "UTF-8");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			final int iDeviceID = AgentLocActivity.getAgentID(this);
			
			sendResolveReport(iDeviceID, stComments);
			
			break;

		default:
			break;
		}
	}

	private void sendResolveReport(final int iDeviceID, final String stComments) {
		Thread thread = new Thread() {

			@Override
			public void run() {
			
				String stURL = String.format(LooperThread.RESOLVEINCIDENT,
						iDeviceID, stIncidentID, stComments);

				stURL += "ticket=" + LooperThread.getInstance().getTicket();
				Log.i(LooperThread.LOG, "URL =" + stURL);
				try {
					byte bData[] = connectHttp(stURL, null, null, false, null,
							true, null);
					Log.i("track", new String(bData));
				
				} catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
				
			}
		};

		if (!Utils.isInternetAvailable(this)) {

			showToast(getString(R.string.connection_error));
			handler.sendEmptyMessage(1);
		}else{
			int id=0;
			handler.sendEmptyMessage(id);
			thread.start();
			
		}
		
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Intent intent = new  Intent();
				intent.putExtra(INCIDENT_ID, stIncidentID);
				setResult(RESULT_OK,intent);
				finish();
				break;
			case 1 :
				setResult(RESULT_CANCELED);
				finish();
				break;

			default:
				break;
			}
			
		};
	};

	public byte[] connectHttp(String url, int[] iExRespCode, String stData,
			boolean isPost, Vector<Header[]> vecheader,
			boolean bIsContentTypePlainText, byte[] bData)
			throws ClientProtocolException, IOException {

		HttpClient httpURLConnection = null;
		byte[] data = null;
		// HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
		// 10000); //Timeout Limit
		HttpResponse response = null;

		HttpEntity entity = null;

		try {
			httpURLConnection = new DefaultHttpClient();
			//
			// Log.i("ACDC", "Data:"+stData);
			if (isPost) {
				HttpPost httpPost = new HttpPost(url);
				if (stData != null) {
					StringEntity se = new StringEntity(stData);

					if (!bIsContentTypePlainText) {
						se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
								JSON_CONTENT));
					} else {

						se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
								PLAIN_TEXT_CONTENT));
					}

					httpPost.setEntity(se);
				} else {
					ByteArrayEntity isEntity = new ByteArrayEntity(bData);
					isEntity.setContentType("binary/octet-stream");
					// isEntity.setChunked(true);
					httpPost.setEntity(isEntity);
				}

				HttpParams myHttpParams = getHttpParams();
				httpPost.setParams(myHttpParams);

				response = httpURLConnection.execute(httpPost);
			} else {

				/*
				 * if (!url.endsWith("?")) url += "?"; if (stData!=null) { url =
				 * url + stData; }
				 */
				HttpGet httpget = new HttpGet(url);

				HttpParams myHttpParams = getHttpParams();
				httpget.setParams(myHttpParams);

				response = httpURLConnection.execute(httpget);
			}
			if (response != null && response.getStatusLine() != null) {
				if (iExRespCode != null) {
					int iResCode = response.getStatusLine().getStatusCode();
					iExRespCode[0] = iResCode;
				}

				if (vecheader != null)
					vecheader.add(response.getAllHeaders());
				entity = response.getEntity();

				if (entity != null) {

					InputStream is = entity.getContent();
					data = readDataChunked(is);
					is.close();
				}
			}
		} finally {
			if (entity != null) {
				entity.consumeContent();
			}

			if (httpURLConnection != null) {
				httpURLConnection.getConnectionManager().shutdown();
			}
		}

		return data;
	}

	private byte[] readDataChunked(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte[] buffer = new byte[CHUNK_SIZE];
		int bytesRead = 0;

		while ((bytesRead = is.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}

		byte[] output = out.toByteArray();

		return output;
	}

	private static final String USER_AGENT = "Android/tcc";

	private static final String JSON_CONTENT = "application/json";

	private static final String PLAIN_TEXT_CONTENT = "text/plain";

	private final static int CHUNK_SIZE = 4096;

	private final static int CONNECTION_TIMEOUT = 80000;

	private HttpParams getHttpParams() {
		HttpParams myHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myHttpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(myHttpParams, CONNECTION_TIMEOUT);

		String stUserAgent = USER_AGENT;

		myHttpParams.setParameter(CoreProtocolPNames.USER_AGENT, stUserAgent);
		return myHttpParams;
	}
}
