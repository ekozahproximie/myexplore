/*Copyright (c) <2012>, Intel Corporation

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

\- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
\- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
\- Neither the name of Intel Corporation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
\*/

package android.intel.sdp.CameraVideo;

import java.io.File;

import com.example.test.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraVideoActivity extends Activity {
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean m_inPreview = false;

	private final String TAG = "SDP_MEDIARECORDER";
	private final String FLAG_SUC = "SDP_MEDIARECORDER_SUCCESSFULLY";
	private final String FLAG_FAIL = "SDP_MEDIARECORDER_FAILED";

	private boolean mFailed = true;
	private boolean mStop = false;

	private MediaRecorder mediaRecorder;
	private CamcorderProfile profile;

	private final String OUTPUT_FILE = Environment.getExternalStorageDirectory()+"/cameraVideo.3gp";
	private int mRecDuration = 5;// record 5 seconds
	private String mEncoder;

	private static final String KEY_REC_DURATION = "rec_duration";
	private static final String KEY_FRAME_SIZE = "frame_size";// e.g.:1027*768
	private static final String KEY_FRAME_RATE = "frame_rate";
	private static final String KEY_VIDEO_ENCODER = "encoder";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_video);
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, cameraInfo);

			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				camera = Camera.open(i);
			}
		}
		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

		Intent intentThis = getIntent();

		if (intentThis.getStringExtra(KEY_REC_DURATION) != null) {
			mRecDuration = Integer.parseInt(intentThis
					.getStringExtra(KEY_REC_DURATION));

			Log.d(TAG,
					"Set rec duration to"
							+ intentThis.getStringExtra(KEY_REC_DURATION));
		}
		if (intentThis.getStringExtra(KEY_FRAME_SIZE) != null) {
			String frameSize[] = intentThis.getStringExtra(KEY_FRAME_SIZE)
					.split("\\*");
			if (frameSize.length > 2) {
				profile.videoFrameWidth = Integer.parseInt(frameSize[0]);
				profile.videoFrameHeight = Integer.parseInt(frameSize[1]);

				Log.d(TAG,
						"Set frame size to"
								+ intentThis.getStringExtra(KEY_FRAME_SIZE));
			}
		}
		if (intentThis.getStringExtra(KEY_FRAME_RATE) != null) {
			profile.videoFrameRate = Integer.parseInt(intentThis
					.getStringExtra(KEY_FRAME_RATE));
			Log.d(TAG, "Set frame rate to" + profile.videoFrameRate);
		}
		if (intentThis.getStringExtra(KEY_VIDEO_ENCODER) != null) {
			mEncoder = intentThis.getStringExtra(KEY_VIDEO_ENCODER)
					.toUpperCase();

			if (mEncoder.equals("H263")) {
				profile.videoCodec = MediaRecorder.VideoEncoder.H263;
			} else if (mEncoder.equals("H264") || mEncoder.equals("AVC")) {
				profile.videoCodec = MediaRecorder.VideoEncoder.H264;
			} else if (mEncoder.equals("MPEG4")) {
				profile.videoCodec = MediaRecorder.VideoEncoder.MPEG_4_SP;
			}
			Log.d(TAG, "Set video encoder to" + mEncoder);
		}

	}

	@Override
	public void onResume() {
		if(camera == null){
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, cameraInfo);

			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				camera = Camera.open(i);
			}
		}
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		if (m_inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		m_inPreview = false;

		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.camera_option_menu, menu);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.camera) {
			if (m_inPreview) {
				try {
					recordVideo();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// inPreview=false;
			}
		}

		return (super.onOptionsItemSelected(item));
	}

	private void recordVideo() throws Exception {
		File file = new File(OUTPUT_FILE);

		// Clear old file if it exists before recording.
		if (file.exists()) {
			Log.d(TAG, "Before recording, output file exists, delete it.");
			file.delete();
		}

		camera.unlock();

		mediaRecorder = new MediaRecorder();

		mediaRecorder.setCamera(camera);
		//mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setPreviewDisplay(previewHolder.getSurface());
		// mediaRecorder.setOnErrorListener(this);
		mediaRecorder.setProfile(profile);
		Log.d(TAG, "Set output file: " + OUTPUT_FILE);
		mediaRecorder.setOutputFile(OUTPUT_FILE);

		Log.d(TAG, "Prepare to record.");
		mediaRecorder.prepare();

		Log.d(TAG, "Start to record.");
		mediaRecorder.start(); // Recording is now started

		Thread.sleep(mRecDuration * 1000);

		mediaRecorder.stop();
		mediaRecorder.release();

	}

	private Camera.Size getCameraPictureSize(Camera.Parameters parameters) {
		Camera.Size pictureSize = null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (pictureSize == null) {
				pictureSize = size;
			} else {
				int pictureArea = pictureSize.width * pictureSize.height;
				int area = size.width * size.height;

				if (area < pictureArea) {
					pictureSize = size;
				}
			}
		}

		return (pictureSize);
	}

	private Camera.Size getCameraPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size previewSize = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (previewSize == null) {
					previewSize = size;
				} else {
					int previewArea = previewSize.width * previewSize.height;
					int area = size.width * size.height;

					if (area > previewArea) {
						previewSize = size;
					}
				}
			}
		}

		return (previewSize);
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				if(camera != null)
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e(TAG, "error in creating preview)", t);
				Toast.makeText(CameraVideoActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if(camera != null){
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getCameraPreviewSize(height, width, parameters);
			Camera.Size videoSize = getCameraPictureSize(parameters);

			if (size != null && videoSize != null) {
				parameters.setPreviewSize(size.width, size.height);
				parameters.setPictureSize(videoSize.width, videoSize.height);
				parameters.setPictureFormat(ImageFormat.JPEG);
				camera.setParameters(parameters);
				camera.startPreview();
				m_inPreview = true;
			}
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {

		}
	};

}