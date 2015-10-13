package net.sourceforge.opencamera.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ZoomControls;

import net.sourceforge.opencamera.CameraController.CameraController;
import net.sourceforge.opencamera.LocationSupplier;
import net.sourceforge.opencamera.MainActivity;
import net.sourceforge.opencamera.PreferenceKeys;
import net.sourceforge.opencamera.Preview.Preview;
import net.sourceforge.opencamera.UI.FolderChooserDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private static final String TAG = "MainActivityTest";
	private MainActivity mActivity = null;
	private Preview mPreview = null;

	@SuppressWarnings("deprecation")
	public MainActivityTest() {
		super("net.sourceforge.opencamera", MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		Log.d(TAG, "setUp");
		super.setUp();

	    setActivityInitialTouchMode(false);

	    Intent intent = new Intent();
	    intent.putExtra("test_project", true);
	    setActivityIntent(intent);
	    mActivity = getActivity();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		//editor.putBoolean(PreferenceKeys.getUseCamera2PreferenceKey(), true); // uncomment to test Camera2 API
		editor.apply();
		
		// now need to restart, in case any of the preferences are used when starting up
		restart(); // need to restart for this preference to take effect

		//Camera camera = mPreview.getCamera();
	    /*mSpinner = (Spinner) mActivity.findViewById(
	        com.android.example.spinner.R.id.Spinner01
	      );*/

	    //mPlanetData = mSpinner.getAdapter();
	}

	@Override
	protected void tearDown() throws Exception {
		Log.d(TAG, "tearDown");

		assertTrue( mPreview.getCameraController() == null || mPreview.getCameraController().count_camera_parameters_exception == 0 );

		// reset back to defaults
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.apply();

		super.tearDown();
	}

    public void testPreConditions() {
		assertTrue(mPreview != null);
		//assertTrue(mPreview.getCamera() != null);
		//assertTrue(mCamera != null);
		//assertTrue(mSpinner.getOnItemSelectedListener() != null);
		//assertTrue(mPlanetData != null);
		//assertEquals(mPlanetData.getCount(),ADAPTER_COUNT);
	}

	private void restart() {
		Log.d(TAG, "restart");
	    mActivity.finish();
	    setActivity(null);
		Log.d(TAG, "now starting");
	    mActivity = getActivity();
	    mPreview = mActivity.getPreview();
		Log.d(TAG, "restart done");
	}
	
	private void pauseAndResume() {
		Log.d(TAG, "pauseAndResume");
	    // onResume has code that must run on UI thread
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				Log.d(TAG, "pause...");
				getInstrumentation().callActivityOnPause(mActivity);
				Log.d(TAG, "resume...");
				getInstrumentation().callActivityOnResume(mActivity);
			}
		});
		// need to wait for UI code to finish before leaving
		this.getInstrumentation().waitForIdleSync();
	}

	private void updateForSettings() {
		Log.d(TAG, "updateForSettings");
	    // updateForSettings has code that must run on UI thread
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mActivity.updateForSettings();
			}
		});
		// need to wait for UI code to finish before leaving
		this.getInstrumentation().waitForIdleSync();
	}

	private void clickView(final View view) {
		// TouchUtils.clickView doesn't work properly if phone held in portrait mode!
	    //TouchUtils.clickView(MainActivityTest.this, view);
		assertTrue(view.getVisibility() == View.VISIBLE);
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				assertTrue(view.performClick());
			}
		});
		// need to wait for UI code to finish before leaving
		this.getInstrumentation().waitForIdleSync();
	}

	private void switchToFlashValue(String required_flash_value) {
		if( mPreview.supportsFlash() ) {
		    String flash_value = mPreview.getCurrentFlashValue();
			Log.d(TAG, "start flash_value: "+ flash_value);
			if( !flash_value.equals(required_flash_value) ) {
				assertFalse( mActivity.popupIsOpen() );
			    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
			    clickView(popupButton);
			    while( !mActivity.popupIsOpen() ) {
			    }
			    View flashButton = mActivity.getPopupButton("TEST_FLASH_" + required_flash_value);
			    assertTrue(flashButton != null);
			    clickView(flashButton);
			    flash_value = mPreview.getCurrentFlashValue();
				Log.d(TAG, "changed flash_value to: "+ flash_value);
			}
		    assertTrue(flash_value.equals(required_flash_value));
		    assertTrue(flash_value.equals( mPreview.getCameraController().getFlashValue() ));
		}
	}

	private void switchToFocusValue(String required_focus_value) {
		Log.d(TAG, "switchToFocusValue: "+ required_focus_value);
	    if( mPreview.supportsFocus() ) {
		    String focus_value = mPreview.getCurrentFocusValue();
			Log.d(TAG, "start focus_value: "+ focus_value);
			if( !focus_value.equals(required_focus_value) ) {
				assertFalse( mActivity.popupIsOpen() );
			    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
			    clickView(popupButton);
			    while( !mActivity.popupIsOpen() ) {
			    }
			    View focusButton = mActivity.getPopupButton("TEST_FOCUS_" + required_focus_value);
			    assertTrue(focusButton != null);
			    clickView(focusButton);
			    focus_value = mPreview.getCurrentFocusValue();
				Log.d(TAG, "changed focus_value to: "+ focus_value);
			}
		    assertTrue(focus_value.equals(required_focus_value));
		    String actual_focus_value = mPreview.getCameraController().getFocusValue();
			Log.d(TAG, "actual_focus_value: "+ actual_focus_value);
			String compare_focus_value = focus_value;
			if( compare_focus_value.equals("focus_mode_locked") )
				compare_focus_value = "focus_mode_auto";
			else if( compare_focus_value.equals("focus_mode_infinity") && mPreview.usingCamera2API() )
				compare_focus_value = "focus_mode_manual2";
		    assertTrue(compare_focus_value.equals(actual_focus_value));
	    }
	}
	
	private void switchToISO(int required_iso) {
		Log.d(TAG, "switchToISO: "+ required_iso);
	    if( mPreview.supportsFocus() ) {
		    int iso = mPreview.getCameraController().getISO();
			Log.d(TAG, "start iso: "+ iso);
			if( iso != required_iso ) {
				assertFalse( mActivity.popupIsOpen() );
			    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
			    clickView(popupButton);
			    while( !mActivity.popupIsOpen() ) {
			    }
			    View isoButton = mActivity.getPopupButton("TEST_ISO_" + required_iso);
			    assertTrue(isoButton != null);
			    clickView(isoButton);
			    iso = mPreview.getCameraController().getISO();
				Log.d(TAG, "changed iso to: "+ iso);
			}
		    assertTrue(iso == required_iso);
	    }
	}
	
	/* Sets the camera up to a predictable state:
	 * - Front camera
	 * - Photo mode
	 * - Flash off (if flash supported)
	 * - Focus mode auto (if focus modes supported)
	 * As a side-effect, the camera and/or camera parameters values may become invalid.
	 */
	private void setToDefault() {
		if( mPreview.isVideo() ) {
			Log.d(TAG, "turn off video mode");
		    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
		    clickView(switchVideoButton);
		}
		assertTrue(!mPreview.isVideo());

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			int cameraId = mPreview.getCameraId();
			Log.d(TAG, "start cameraId: "+ cameraId);
			while( cameraId != 0 ) {
			    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
			    clickView(switchCameraButton);
			    // camera becomes invalid when switching cameras
				cameraId = mPreview.getCameraId();
				Log.d(TAG, "changed cameraId to: "+ cameraId);
			}
		}

		switchToFlashValue("flash_off");
		switchToFocusValue("focus_mode_auto");
	}

	/* Ensures that we only start the camera preview once when starting up.
	 */
	public void testStartCameraPreviewCount() {
		Log.d(TAG, "testStartCameraPreviewCount");
		/*Log.d(TAG, "1 count_cameraStartPreview: " + mPreview.count_cameraStartPreview);
		int init_count_cameraStartPreview = mPreview.count_cameraStartPreview;
	    mActivity.finish();
	    setActivity(null);
	    mActivity = this.getActivity();
	    mPreview = mActivity.getPreview();
		Log.d(TAG, "2 count_cameraStartPreview: " + mPreview.count_cameraStartPreview);
		assertTrue(mPreview.count_cameraStartPreview == init_count_cameraStartPreview);
		this.getInstrumentation().callActivityOnPause(mActivity);
		Log.d(TAG, "3 count_cameraStartPreview: " + mPreview.count_cameraStartPreview);
		assertTrue(mPreview.count_cameraStartPreview == init_count_cameraStartPreview);
		this.getInstrumentation().callActivityOnResume(mActivity);
		Log.d(TAG, "4 count_cameraStartPreview: " + mPreview.count_cameraStartPreview);
		assertTrue(mPreview.count_cameraStartPreview == init_count_cameraStartPreview+1);*/
		setToDefault();

		restart();
	    // onResume has code that must run on UI thread
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				Log.d(TAG, "1 count_cameraStartPreview: " + mPreview.count_cameraStartPreview);
				assertTrue(mPreview.count_cameraStartPreview == 1);
				getInstrumentation().callActivityOnPause(mActivity);
				Log.d(TAG, "2 count_cameraStartPreview: " + mPreview.count_cameraStartPreview);
				assertTrue(mPreview.count_cameraStartPreview == 1);
				getInstrumentation().callActivityOnResume(mActivity);
				Log.d(TAG, "3 count_cameraStartPreview: " + mPreview.count_cameraStartPreview);
				assertTrue(mPreview.count_cameraStartPreview == 2);
			}
		});
		// need to wait for UI code to finish before leaving
		this.getInstrumentation().waitForIdleSync();
	}

	/* Ensures that we save the video mode.
	 */
	public void testSaveVideoMode() {
		Log.d(TAG, "testSaveVideoMode");
		setToDefault();

	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    clickView(switchVideoButton);
	    assertTrue(mPreview.isVideo());

		restart();
	    assertTrue(mPreview.isVideo());

	    pauseAndResume();
	    assertTrue(mPreview.isVideo());
	}

	/* Ensures that we save the flash mode torch when quitting and restarting.
	 */
	public void testSaveFlashTorchQuit() throws InterruptedException {
		Log.d(TAG, "testSaveFlashTorchQuit");

		if( !mPreview.supportsFlash() ) {
			return;
		}

		setToDefault();
		
		switchToFlashValue("flash_torch");

		restart();
		Thread.sleep(4000); // needs to be long enough for the autofocus to complete
	    String controller_flash_value = mPreview.getCameraController().getFlashValue();
		Log.d(TAG, "controller_flash_value: " + controller_flash_value);
	    assertTrue(controller_flash_value.equals("flash_torch"));
	    String flash_value = mPreview.getCurrentFlashValue();
		Log.d(TAG, "flash_value: " + flash_value);
	    assertTrue(flash_value.equals("flash_torch"));
	}

	/* Ensures that we save the flash mode torch when switching to front camera and then to back
	 * Note that this sometimes fail on Galaxy Nexus, because flash turns off after autofocus (and other camera apps do this too), but this only seems to happen some of the time!
	 * And Nexus 7 has no flash anyway.
	 * So commented out test for now.
	 */
	/*public void testSaveFlashTorchSwitchCamera() {
		Log.d(TAG, "testSaveFlashTorchSwitchCamera");

		if( !mPreview.supportsFlash() ) {
			return;
		}
		else if( Camera.getNumberOfCameras() == 0 ) {
			return;
		}

		setToDefault();
		
		switchToFlashValue("flash_torch");

		int cameraId = mPreview.getCameraId();
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    clickView(switchCameraButton);
		int new_cameraId = mPreview.getCameraId();
		assertTrue(cameraId != new_cameraId);

	    clickView(switchCameraButton);
		new_cameraId = mPreview.getCameraId();
		assertTrue(cameraId == new_cameraId);

		Camera camera = mPreview.getCamera();
	    Camera.Parameters parameters = camera.getParameters();
		Log.d(TAG, "parameters flash mode: " + parameters.getFlashMode());
	    assertTrue(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH));
	    String flash_value = mPreview.getCurrentFlashValue();
		Log.d(TAG, "flash_value: " + flash_value);
	    assertTrue(flash_value.equals("flash_torch"));
	}*/
	
	public void testFlashStartup() throws InterruptedException {
		Log.d(TAG, "testFlashStartup");
		setToDefault();

		if( !mPreview.supportsFlash() ) {
			return;
		}

		Log.d(TAG, "# switch to flash on");
		switchToFlashValue("flash_on");
		Log.d(TAG, "# restart");
		restart();

		Log.d(TAG, "# switch flash mode");
		// now switch to torch - the idea is that this is done while the camera is starting up
	    // though note that sometimes we might not be quick enough here!
		// don't use switchToFlashValue here, it'll get confused due to the autofocus changing the parameters flash mode
		// update: now okay to use it, now we have the popup UI
	    //View flashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.flash);
	    //clickView(flashButton);
		switchToFlashValue("flash_torch");

	    //Camera camera = mPreview.getCamera();
	    //Camera.Parameters parameters = camera.getParameters();
	    //String flash_mode = mPreview.getCurrentFlashMode();
	    String flash_value = mPreview.getCurrentFlashValue();
		Log.d(TAG, "# flash value is now: " + flash_value);
		Log.d(TAG, "# sleep");
		Thread.sleep(4000); // needs to be long enough for the autofocus to complete
	    /*parameters = camera.getParameters();
		Log.d(TAG, "# parameters flash mode: " + parameters.getFlashMode());
	    assertTrue(parameters.getFlashMode().equals(flash_mode));*/
		String camera_flash_value = mPreview.getCameraController().getFlashValue();
		Log.d(TAG, "# camera flash value: " + camera_flash_value);
	    assertTrue(camera_flash_value.equals(flash_value));
	}
	
	private void checkOptimalPreviewSize() {
		Log.d(TAG, "preview size: " + mPreview.getCameraController().getPreviewSize().width + ", " + mPreview.getCameraController().getPreviewSize().height);
        List<CameraController.Size> sizes = mPreview.getSupportedPreviewSizes();
    	CameraController.Size best_size = mPreview.getOptimalPreviewSize(sizes);
		Log.d(TAG, "best size: " + best_size.width + ", " + best_size.height);
    	assertTrue( best_size.width == mPreview.getCameraController().getPreviewSize().width );
    	assertTrue( best_size.height == mPreview.getCameraController().getPreviewSize().height );
	}

	private void checkOptimalVideoPictureSize(double targetRatio) {
        // even the picture resolution should have same aspect ratio for video - otherwise have problems on Nexus 7 with Android 4.4.3
		Log.d(TAG, "video picture size: " + mPreview.getCameraController().getPictureSize().width + ", " + mPreview.getCameraController().getPictureSize().height);
        List<CameraController.Size> sizes = mPreview.getSupportedPictureSizes();
    	CameraController.Size best_size = mPreview.getOptimalVideoPictureSize(sizes, targetRatio);
		Log.d(TAG, "best size: " + best_size.width + ", " + best_size.height);
    	assertTrue( best_size.width == mPreview.getCameraController().getPictureSize().width );
    	assertTrue( best_size.height == mPreview.getCameraController().getPictureSize().height );
	}

	private void checkSquareAspectRatio() {
		Log.d(TAG, "preview size: " + mPreview.getCameraController().getPreviewSize().width + ", " + mPreview.getCameraController().getPreviewSize().height);
		Log.d(TAG, "frame size: " + mPreview.getView().getWidth() + ", " + mPreview.getView().getHeight());
		double frame_aspect_ratio = ((double)mPreview.getView().getWidth()) / (double)mPreview.getView().getHeight();
		double preview_aspect_ratio = ((double)mPreview.getCameraController().getPreviewSize().width) / (double)mPreview.getCameraController().getPreviewSize().height;
		Log.d(TAG, "frame_aspect_ratio: " + frame_aspect_ratio);
		Log.d(TAG, "preview_aspect_ratio: " + preview_aspect_ratio);
		// we calculate etol like this, due to errors from rounding
		//double etol = 1.0f / Math.min((double)mPreview.getWidth(), (double)mPreview.getHeight()) + 1.0e-5;
		double etol = (double)mPreview.getView().getWidth() / (double)(mPreview.getView().getHeight() * (mPreview.getView().getHeight()-1) ) + 1.0e-5;
		assertTrue( Math.abs(frame_aspect_ratio - preview_aspect_ratio) <= etol );
	}
	
	/* Ensures that preview resolution is set as expected in non-WYSIWYG mode
	 */
	public void testPreviewSize() {
		Log.d(TAG, "testPreviewSize");

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getPreviewSizePreferenceKey(), "preference_preview_size_display");
		editor.apply();
		updateForSettings();

        Point display_size = new Point();
        {
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            display.getSize(display_size);
			Log.d(TAG, "display_size: " + display_size.x + " x " + display_size.y);
        }
        //double targetRatio = mPreview.getTargetRatioForPreview(display_size);
        double targetRatio = mPreview.getTargetRatio();
        double expTargetRatio = ((double)display_size.x) / (double)display_size.y;
        assertTrue( Math.abs(targetRatio - expTargetRatio) <= 1.0e-5 );
        checkOptimalPreviewSize();
		checkSquareAspectRatio();

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			Log.d(TAG, "switch camera");
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);

	        //targetRatio = mPreview.getTargetRatioForPreview(display_size);
	        targetRatio = mPreview.getTargetRatio();
	        assertTrue( Math.abs(targetRatio - expTargetRatio) <= 1.0e-5 );
	        checkOptimalPreviewSize();
			checkSquareAspectRatio();
		}
	}

	/* Ensures that preview resolution is set as expected in WYSIWYG mode
	 */
	public void testPreviewSizeWYSIWYG() {
		Log.d(TAG, "testPreviewSizeWYSIWYG");

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getPreviewSizePreferenceKey(), "preference_preview_size_wysiwyg");
		editor.apply();
		updateForSettings();

        Point display_size = new Point();
        {
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            display.getSize(display_size);
			Log.d(TAG, "display_size: " + display_size.x + " x " + display_size.y);
        }
        CameraController.Size picture_size = mPreview.getCameraController().getPictureSize();
        CameraController.Size preview_size = mPreview.getCameraController().getPreviewSize();
        //double targetRatio = mPreview.getTargetRatioForPreview(display_size);
        double targetRatio = mPreview.getTargetRatio();
        double expTargetRatio = ((double)picture_size.width) / (double)picture_size.height;
        double previewRatio = ((double)preview_size.width) / (double)preview_size.height;
        assertTrue( Math.abs(targetRatio - expTargetRatio) <= 1.0e-5 );
        assertTrue( Math.abs(previewRatio - expTargetRatio) <= 1.0e-5 );
        checkOptimalPreviewSize();
		checkSquareAspectRatio();

		Log.d(TAG, "switch to video");
	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    clickView(switchVideoButton);
	    assertTrue(mPreview.isVideo());
    	CamcorderProfile profile = mPreview.getCamcorderProfile();
        CameraController.Size video_preview_size = mPreview.getCameraController().getPreviewSize();
        //targetRatio = mPreview.getTargetRatioForPreview(display_size);
        targetRatio = mPreview.getTargetRatio();
        expTargetRatio = ((double)profile.videoFrameWidth) / (double)profile.videoFrameHeight;
        previewRatio = ((double)video_preview_size.width) / (double)video_preview_size.height;
        assertTrue( Math.abs(targetRatio - expTargetRatio) <= 1.0e-5 );
        assertTrue( Math.abs(previewRatio - expTargetRatio) <= 1.0e-5 );
        checkOptimalPreviewSize();
		checkSquareAspectRatio();
        checkOptimalVideoPictureSize(expTargetRatio);

	    clickView(switchVideoButton);
	    assertTrue(!mPreview.isVideo());
        CameraController.Size new_picture_size = mPreview.getCameraController().getPictureSize();
        CameraController.Size new_preview_size = mPreview.getCameraController().getPreviewSize();
	    Log.d(TAG, "picture_size: " + picture_size.width + " x " + picture_size.height);
	    Log.d(TAG, "new_picture_size: " + new_picture_size.width + " x " + new_picture_size.height);
	    Log.d(TAG, "preview_size: " + preview_size.width + " x " + preview_size.height);
	    Log.d(TAG, "new_preview_size: " + new_preview_size.width + " x " + new_preview_size.height);
	    assertTrue(new_picture_size.equals(picture_size));
	    assertTrue(new_preview_size.equals(preview_size));

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			Log.d(TAG, "switch camera");
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);

	        picture_size = mPreview.getCameraController().getPictureSize();
	        preview_size = mPreview.getCameraController().getPreviewSize();
	        //targetRatio = mPreview.getTargetRatioForPreview(display_size);
	        targetRatio = mPreview.getTargetRatio();
	        expTargetRatio = ((double)picture_size.width) / (double)picture_size.height;
	        previewRatio = ((double)preview_size.width) / (double)preview_size.height;
	        assertTrue( Math.abs(targetRatio - expTargetRatio) <= 1.0e-5 );
	        assertTrue( Math.abs(previewRatio - expTargetRatio) <= 1.0e-5 );
	        checkOptimalPreviewSize();
			checkSquareAspectRatio();
			
			Log.d(TAG, "switch to video again");
		    clickView(switchVideoButton);
		    assertTrue(mPreview.isVideo());
	    	profile = mPreview.getCamcorderProfile();
	        video_preview_size = mPreview.getCameraController().getPreviewSize();
		    //targetRatio = mPreview.getTargetRatioForPreview(display_size);
		    targetRatio = mPreview.getTargetRatio();
	        expTargetRatio = ((double)profile.videoFrameWidth) / (double)profile.videoFrameHeight;
	        previewRatio = ((double)video_preview_size.width) / (double)video_preview_size.height;
	        assertTrue( Math.abs(targetRatio - expTargetRatio) <= 1.0e-5 );
	        assertTrue( Math.abs(previewRatio - expTargetRatio) <= 1.0e-5 );
	        checkOptimalPreviewSize();
			checkSquareAspectRatio();
	        checkOptimalVideoPictureSize(expTargetRatio);

		    clickView(switchVideoButton);
		    assertTrue(!mPreview.isVideo());
	        new_picture_size = mPreview.getCameraController().getPictureSize();
	        new_preview_size = mPreview.getCameraController().getPreviewSize();
		    assertTrue(new_picture_size.equals(picture_size));
		    assertTrue(new_preview_size.equals(preview_size));
		}
	}

	/* Various tests for auto-focus.
	 */
	public void testAutoFocus() throws InterruptedException {
		Log.d(TAG, "testAutoFocus");
	    if( !mPreview.supportsFocus() ) {
	    	return;
	    }
		setToDefault();

		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

		Thread.sleep(1000); // wait until autofocus startup
		int saved_count = mPreview.count_cameraAutoFocus;
	    Log.d(TAG, "1 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count);
		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

		// touch to auto-focus with focus area
	    saved_count = mPreview.count_cameraAutoFocus;
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		Log.d(TAG, "2 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);

	    saved_count = mPreview.count_cameraAutoFocus;
	    // test selecting same mode doesn't set off an autofocus or reset the focus area
		switchToFocusValue("focus_mode_auto");
		Log.d(TAG, "3 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count);
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);

	    saved_count = mPreview.count_cameraAutoFocus;
	    // test switching mode sets off an autofocus, and resets the focus area
		switchToFocusValue("focus_mode_macro");
		Log.d(TAG, "4 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

	    saved_count = mPreview.count_cameraAutoFocus;
	    // switching to focus locked shouldn't set off an autofocus
		switchToFocusValue("focus_mode_locked");
		Log.d(TAG, "5 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count);

		saved_count = mPreview.count_cameraAutoFocus;
		// touch to focus should autofocus
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		Log.d(TAG, "6 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);

		saved_count = mPreview.count_cameraAutoFocus;
	    // switching to focus continuous shouldn't set off an autofocus
		switchToFocusValue("focus_mode_continuous_video");
		Log.d(TAG, "7 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(!mPreview.isFocusWaiting());
		assertTrue(mPreview.count_cameraAutoFocus == saved_count);

		// nor should touch to focus
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		Log.d(TAG, "8 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(!mPreview.isFocusWaiting());
		assertTrue(mPreview.count_cameraAutoFocus == saved_count);
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
	    
		switchToFocusValue("focus_mode_locked"); // change to a mode that isn't auto (so that the first iteration of the next loop will set of an autofocus, due to changing the focus mode)
		List<String> supported_focus_values = mPreview.getSupportedFocusValues();
		assertTrue( supported_focus_values != null );
		assertTrue( supported_focus_values.size() > 1 );
		for(String supported_focus_value : supported_focus_values) {
			Log.d(TAG, "supported_focus_value: " + supported_focus_value);
		    saved_count = mPreview.count_cameraAutoFocus;
			Log.d(TAG, "saved autofocus count: " + saved_count);
		    //View focusModeButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_mode);
		    //clickView(focusModeButton);
		    switchToFocusValue(supported_focus_value);
		    // test that switching focus mode resets the focus area
			assertTrue(!mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);
		    // test that switching focus mode sets off an autofocus in focus auto or macro mode
		    String focus_value = mPreview.getCameraController().getFocusValue();
			Log.d(TAG, "changed focus_value to: "+ focus_value);
			Log.d(TAG, "count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
			if( focus_value.equals("focus_mode_auto") || focus_value.equals("focus_mode_macro") ) {
				assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
			}
			else {
				assertTrue(!mPreview.isFocusWaiting());
				assertTrue(mPreview.count_cameraAutoFocus == saved_count);
			}

		    // test that touch to auto-focus region only works in focus auto, macro or continuous mode, and that we set off an autofocus for focus auto and macro
			// test that touch to set metering area works in any focus mode
		    saved_count = mPreview.count_cameraAutoFocus;
			TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
			Log.d(TAG, "count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
			if( focus_value.equals("focus_mode_auto") || focus_value.equals("focus_mode_macro") || focus_value.equals("focus_mode_continuous_video") ) {
				if( focus_value.equals("focus_mode_continuous_video") ) {
					assertTrue(!mPreview.isFocusWaiting());
					assertTrue(mPreview.count_cameraAutoFocus == saved_count);
				}
				else {
					assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
				}
				assertTrue(mPreview.hasFocusArea());
			    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
			    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
			    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
			}
			else {
				assertTrue(mPreview.count_cameraAutoFocus == saved_count);
				assertTrue(!mPreview.hasFocusArea());
			    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
			}
			// also check that focus mode is unchanged
		    assertTrue(mPreview.getCameraController().getFocusValue().equals(focus_value));
			if( focus_value.equals("focus_mode_auto") ) {
				break;
			}
	    }
	}

	/* Test doing touch to auto-focus region by swiping to all four corners works okay.
	 */
	public void testAutoFocusCorners() {
		Log.d(TAG, "testAutoFocusCorners");
	    if( !mPreview.supportsFocus() ) {
	    	return;
	    }
		setToDefault();
		int [] gui_location = new int[2];
		mPreview.getView().getLocationOnScreen(gui_location);
		final int step_dist_c = 2;
		final float scale = mActivity.getResources().getDisplayMetrics().density;
		final int large_step_dist_c = (int) (60 * scale + 0.5f); // convert dps to pixels
		final int step_count_c = 10;
		int width = mPreview.getView().getWidth();
		int height = mPreview.getView().getHeight();
		Log.d(TAG, "preview size: " + width + " x " + height);

		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

		Log.d(TAG, "top-left");
	    TouchUtils.drag(MainActivityTest.this, gui_location[0] + step_dist_c, gui_location[0], gui_location[1] + step_dist_c, gui_location[1], step_count_c);
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);

	    mPreview.clearFocusAreas();
		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);
	    
		// do larger step at top-right, due to conflicting with Settings button
		// but we now ignore swipes - so we now test for that instead
		Log.d(TAG, "top-right");
	    TouchUtils.drag(MainActivityTest.this, gui_location[0]+width-1-large_step_dist_c, gui_location[0]+width-1, gui_location[1]+large_step_dist_c, gui_location[1], step_count_c);
		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

		Log.d(TAG, "bottom-left");
	    TouchUtils.drag(MainActivityTest.this, gui_location[0]+step_dist_c, gui_location[0], gui_location[1]+height-1-step_dist_c, gui_location[1]+height-1, step_count_c);
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);

	    mPreview.clearFocusAreas();
		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

	    // skip bottom right, conflicts with zoom on various devices
	}

	/* Test face detection, and that we don't get the focus/metering areas set.
	 */
	public void testFaceDetection() throws InterruptedException {
		Log.d(TAG, "testFaceDetection");
	    if( !mPreview.supportsFaceDetection() ) {
			Log.d(TAG, "face detection not supported");
	    	return;
	    }
		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getFaceDetectionPreferenceKey(), true);
		editor.apply();
		updateForSettings();

		int saved_count = mPreview.count_cameraAutoFocus;
		Log.d(TAG, "0 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		// autofocus shouldn't be immediately, but after a delay
		Thread.sleep(1000);
		Log.d(TAG, "1 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);
	    boolean face_detection_started = false;
	    if( !mPreview.getCameraController().startFaceDetection() ) {
	    	// should throw RuntimeException if face detection already started
	    	face_detection_started = true;
		}
	    assertTrue(face_detection_started);

		// touch to auto-focus with focus area
	    saved_count = mPreview.count_cameraAutoFocus;
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		Log.d(TAG, "2 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);
		    face_detection_started = false;
		    if( !mPreview.getCameraController().startFaceDetection() ) {
		    	// should throw RuntimeException if face detection already started
		    	face_detection_started = true;
			}
		    assertTrue(face_detection_started);
		}
	}

	private void subTestPopupButtonAvailability(String test_key, String option, List<String> options) {
	    View button = mActivity.getPopupButton(test_key + "_" + option);
	    if( options != null && options.contains(option) ) {
	    	assertTrue(button != null);
	    }
	    else {
			Log.d(TAG, "option? "+ option);
			Log.d(TAG, "button? "+ button);
	    	assertTrue(button == null);
	    }
	}
	
	private void subTestPopupButtonAvailability(String option, boolean expected) {
	    View button = mActivity.getPopupButton(option);
	    if( expected ) {
	    	assertTrue(button != null);
	    }
	    else {
	    	assertTrue(button == null);
	    }
	}
	
	private void subTestPopupButtonAvailability() {
		List<String> supported_flash_values = mPreview.getSupportedFlashValues();
		subTestPopupButtonAvailability("TEST_FLASH", "flash_off", supported_flash_values);
		subTestPopupButtonAvailability("TEST_FLASH", "flash_auto", supported_flash_values);
		subTestPopupButtonAvailability("TEST_FLASH", "flash_on", supported_flash_values);
		subTestPopupButtonAvailability("TEST_FLASH", "flash_torch", supported_flash_values);
		subTestPopupButtonAvailability("TEST_FLASH", "flash_red_eye", supported_flash_values);
		List<String> supported_focus_values = mPreview.getSupportedFocusValues();
		subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_auto", supported_focus_values);
		subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_locked", supported_focus_values);
		subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_infinity", supported_focus_values);
		subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_macro", supported_focus_values);
		subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_fixed", supported_focus_values);
		subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_edof", supported_focus_values);
		subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_continuous_video", supported_focus_values);
		List<String> supported_iso_values = mPreview.getSupportedISOs();
		subTestPopupButtonAvailability("TEST_ISO", "auto", supported_iso_values);
		subTestPopupButtonAvailability("TEST_ISO", "100", supported_iso_values);
		subTestPopupButtonAvailability("TEST_ISO", "200", supported_iso_values);
		subTestPopupButtonAvailability("TEST_ISO", "400", supported_iso_values);
		subTestPopupButtonAvailability("TEST_ISO", "800", supported_iso_values);
		subTestPopupButtonAvailability("TEST_ISO", "1600", supported_iso_values);
		subTestPopupButtonAvailability("TEST_WHITE_BALANCE", mPreview.getSupportedWhiteBalances() != null);
		subTestPopupButtonAvailability("TEST_SCENE_MODE", mPreview.getSupportedSceneModes() != null);
		subTestPopupButtonAvailability("TEST_COLOR_EFFECT", mPreview.getSupportedColorEffects() != null);
	}
	
	private void subTestFocusFlashAvailability() {
	    //View focusModeButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_mode);
	    //View flashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.flash);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    /*boolean focus_visible = focusModeButton.getVisibility() == View.VISIBLE;
		Log.d(TAG, "focus_visible? "+ focus_visible);
	    boolean flash_visible = flashButton.getVisibility() == View.VISIBLE;
		Log.d(TAG, "flash_visible? "+ flash_visible);*/
	    boolean exposure_visible = exposureButton.getVisibility() == View.VISIBLE;
		Log.d(TAG, "exposure_visible? "+ exposure_visible);
	    boolean exposure_lock_visible = exposureLockButton.getVisibility() == View.VISIBLE;
		Log.d(TAG, "exposure_lock_visible? "+ exposure_lock_visible);
	    boolean popup_visible = popupButton.getVisibility() == View.VISIBLE;
		Log.d(TAG, "popup_visible? "+ popup_visible);
		boolean has_focus = mPreview.supportsFocus();
		Log.d(TAG, "has_focus? "+ has_focus);
		boolean has_flash = mPreview.supportsFlash();
		Log.d(TAG, "has_flash? "+ has_flash);
		boolean has_exposure = mPreview.supportsExposures();
		Log.d(TAG, "has_exposure? "+ has_exposure);
		boolean has_exposure_lock = mPreview.supportsExposureLock();
		Log.d(TAG, "has_exposure_lock? "+ has_exposure_lock);
		//assertTrue(has_focus == focus_visible);
		//assertTrue(has_flash == flash_visible);
		assertTrue(has_exposure == exposure_visible);
		assertTrue(has_exposure_lock == exposure_lock_visible);
		assertTrue(popup_visible);
		
	    clickView(popupButton);
	    while( !mActivity.popupIsOpen() ) {
	    }
	    subTestPopupButtonAvailability();
	}

	/*
	 * For each camera, test that visibility of flash and focus etc buttons matches the availability of those camera parameters.
	 * Added to guard against a bug where on Nexus 7, the flash and focus buttons were made visible by showGUI, even though they aren't supported by Nexus 7 front camera.
	 */
	public void testFocusFlashAvailability() {
		Log.d(TAG, "testFocusFlashAvailability");
		setToDefault();

		subTestFocusFlashAvailability();

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			int cameraId = mPreview.getCameraId();
			Log.d(TAG, "cameraId? "+ cameraId);
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    //mActivity.clickedSwitchCamera(switchCameraButton);
		    clickView(switchCameraButton);
			int new_cameraId = mPreview.getCameraId();
			Log.d(TAG, "new_cameraId? "+ new_cameraId);
			assertTrue(cameraId != new_cameraId);

		    subTestFocusFlashAvailability();
		}
	}

	/* Tests switching to/from video mode, for front and back cameras, and tests the focus mode changes as expected.
	 */
	public void testSwitchVideo() throws InterruptedException {
		Log.d(TAG, "testSwitchVideo");

		setToDefault();

	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    clickView(switchVideoButton);
	    String focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "video focus_value: "+ focus_value);
	    if( mPreview.supportsFocus() ) {
	    	assertTrue(focus_value.equals("focus_mode_continuous_video"));
	    }

	    int saved_count = mPreview.count_cameraAutoFocus;
	    Log.d(TAG, "0 count_cameraAutoFocus: " + saved_count);
	    clickView(switchVideoButton);
	    focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "picture focus_value: "+ focus_value);
	    if( mPreview.supportsFocus() ) {
	    	assertTrue(focus_value.equals("focus_mode_auto"));
	    	// check that this doesn't cause an autofocus
	    	assertTrue(!mPreview.isFocusWaiting());
		    Log.d(TAG, "1 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
			assertTrue(mPreview.count_cameraAutoFocus == saved_count);
	    }

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			int cameraId = mPreview.getCameraId();
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);
			int new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId != new_cameraId);
		    focus_value = mPreview.getCameraController().getFocusValue();
			Log.d(TAG, "front picture focus_value: "+ focus_value);
		    if( mPreview.supportsFocus() ) {
		    	assertTrue(focus_value.equals("focus_mode_auto"));
		    }

		    clickView(switchVideoButton);
		    focus_value = mPreview.getCameraController().getFocusValue();
			Log.d(TAG, "front video focus_value: "+ focus_value);
		    if( mPreview.supportsFocus() ) {
		    	assertTrue(focus_value.equals("focus_mode_continuous_video"));
		    }

		    clickView(switchVideoButton);
		    focus_value = mPreview.getCameraController().getFocusValue();
			Log.d(TAG, "front picture focus_value: "+ focus_value);
		    if( mPreview.supportsFocus() ) {
		    	assertTrue(focus_value.equals("focus_mode_auto"));
		    }
	    }
	}

	/* Start in photo mode with auto focus:
	 * - go to video mode
	 * - then switch to front camera
	 * - then stop video
	 * - then go to back camera
	 * Check focus mode has returned to auto.
	 * This test is important when front camera doesn't support focus modes, but back camera does - we won't be able to reset to auto focus for the front camera, but need to do so when returning to back camera
	 */
	public void testFocusSwitchVideoSwitchCameras() {
		Log.d(TAG, "testFocusSwitchVideoSwitchCameras");

		if( mPreview.getCameraControllerManager().getNumberOfCameras() == 0 ) {
			return;
		}

	    if( !mPreview.supportsFocus() ) {
	    	return;
	    }

		setToDefault();

	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    clickView(switchVideoButton);
	    String focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "video focus_value: "+ focus_value);
	    assertTrue(focus_value.equals("focus_mode_continuous_video"));

	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    clickView(switchCameraButton);
	    // camera becomes invalid when switching cameras
	    focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "front video focus_value: "+ focus_value);
		// don't care when focus mode is for front camera (focus may not be supported for front camera)

	    clickView(switchVideoButton);
	    focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "front focus_value: "+ focus_value);
		// don't care when focus mode is for front camera (focus may not be supported for front camera)

	    clickView(switchCameraButton);
	    // camera becomes invalid when switching cameras
	    focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "end focus_value: "+ focus_value);
	    assertTrue(focus_value.equals("focus_mode_auto"));
	}

	/* Start in photo mode with focus macro:
	 * - switch to front camera
	 * - switch to back camera
	 * Check focus mode is still macro.
	 * This test is important when front camera doesn't support focus modes, but back camera does - need to remain in macro mode for the back camera.
	 */
	public void testFocusRemainMacroSwitchCamera() {
		Log.d(TAG, "testFocusRemainMacroSwitchCamera");

		if( mPreview.getCameraControllerManager().getNumberOfCameras() == 0 ) {
			return;
		}

	    if( !mPreview.supportsFocus() ) {
	    	return;
	    }

		setToDefault();
		switchToFocusValue("focus_mode_macro");

	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    // n.b., call twice, to switch to front then to back
	    clickView(switchCameraButton);
	    clickView(switchCameraButton);

	    String focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "focus_value: "+ focus_value);
	    assertTrue(focus_value.equals("focus_mode_macro"));
	}

	/* Start in photo mode with focus auto:
	 * - switch to video mode
	 * - switch to focus macro
	 * - switch to picture mode
	 * Check focus mode is now auto.
	 * As of 1.26, we now remember the focus mode for photos.
	 */
	public void testFocusRemainMacroSwitchPhoto() {
		Log.d(TAG, "testFocusRemainMacroSwitchPhoto");

	    if( !mPreview.supportsFocus() ) {
	    	return;
	    }

		setToDefault();

	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    clickView(switchVideoButton);
	    String focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "focus_value after switching to video mode: "+ focus_value);
	    assertTrue(focus_value.equals("focus_mode_continuous_video"));

		switchToFocusValue("focus_mode_macro");

	    clickView(switchVideoButton);

	    focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "focus_value after switching to picture mode: " + focus_value);
	    assertTrue(focus_value.equals("focus_mode_auto"));
	}

	/* Start in photo mode with focus auto:
	 * - switch to focus macro
	 * - switch to video mode
	 * - switch to picture mode
	 * Check focus mode is still macro.
	 * As of 1.26, we now remember the focus mode for photos.
	 */
	public void testFocusSaveMacroSwitchPhoto() {
		Log.d(TAG, "testFocusSaveMacroSwitchPhoto");

	    if( !mPreview.supportsFocus() ) {
	    	return;
	    }

		setToDefault();

		switchToFocusValue("focus_mode_macro");

	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    clickView(switchVideoButton);
	    String focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "focus_value after switching to video mode: "+ focus_value);
	    assertTrue(focus_value.equals("focus_mode_continuous_video"));

	    clickView(switchVideoButton);

	    focus_value = mPreview.getCameraController().getFocusValue();
		Log.d(TAG, "focus_value after switching to picture mode: " + focus_value);
	    assertTrue(focus_value.equals("focus_mode_macro"));
	}

	/* Start in photo mode with auto focus:
	 * - go to video mode
	 * - check in continuous focus mode
	 * - switch to auto focus mode
	 * - then pause and resume
	 * - then check still in video mode, but has reset to continuous mode
	 * - then repeat with restarting instead
	 * This test is important, as there is some reported corruption if we start up in a mode other than continuous (e.g., Galaxy S5 with UHD recording); also if the app quits altogether, we reset to continuous mode for video anyway
	 */
	public void testFocusSwitchVideoResetContinuous() {
		Log.d(TAG, "testFocusSwitchVideoResetContinuous");

	    if( !mPreview.supportsFocus() ) {
	    	return;
	    }

		setToDefault();

	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    clickView(switchVideoButton);
	    String focus_value = mPreview.getCameraController().getFocusValue();
	    assertTrue(focus_value.equals("focus_mode_continuous_video"));

		switchToFocusValue("focus_mode_auto");
	    focus_value = mPreview.getCameraController().getFocusValue();
	    assertTrue(focus_value.equals("focus_mode_auto"));

	    this.pauseAndResume();
	    assertTrue(mPreview.isVideo());
	    
	    focus_value = mPreview.getCameraController().getFocusValue();
	    assertTrue(focus_value.equals("focus_mode_continuous_video"));

	    // now with restart

		switchToFocusValue("focus_mode_auto");
	    focus_value = mPreview.getCameraController().getFocusValue();
	    assertTrue(focus_value.equals("focus_mode_auto"));

	    restart();
	    assertTrue(mPreview.isVideo());

	    focus_value = mPreview.getCameraController().getFocusValue();
	    assertTrue(focus_value.equals("focus_mode_continuous_video"));
	}

	public void testTakePhotoExposureCompensation() throws InterruptedException {
		Log.d(TAG, "testTakePhotoExposureCompensation");
		setToDefault();
		
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    SeekBar seekBar = (SeekBar) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_seekbar);
	    ZoomControls seekBarZoom = (ZoomControls) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_seekbar_zoom);
	    assertTrue(exposureButton.getVisibility() == (mPreview.supportsExposures() ? View.VISIBLE : View.GONE));
	    assertTrue(seekBar.getVisibility() == View.GONE);
	    assertTrue(seekBarZoom.getVisibility() == View.GONE);
	    
	    if( !mPreview.supportsExposures() ) {
	    	return;
	    }

	    clickView(exposureButton);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(seekBar.getVisibility() == View.VISIBLE);
	    assertTrue(seekBarZoom.getVisibility() == View.VISIBLE);

	    assertTrue( mPreview.getMaximumExposure() - mPreview.getMinimumExposure() == seekBar.getMax() );
	    assertTrue( mPreview.getCurrentExposure() - mPreview.getMinimumExposure() == seekBar.getProgress() );
		Log.d(TAG, "change exposure to 1");
	    mActivity.changeExposure(1);
		this.getInstrumentation().waitForIdleSync();
	    assertTrue( mPreview.getCurrentExposure() == 1 );
	    assertTrue( mPreview.getCurrentExposure() - mPreview.getMinimumExposure() == seekBar.getProgress() );
		Log.d(TAG, "set exposure to min");
	    seekBar.setProgress(0);
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "actual exposure is now " + mPreview.getCurrentExposure());
		Log.d(TAG, "expected exposure to be " + mPreview.getMinimumExposure());
	    assertTrue( mPreview.getCurrentExposure() == mPreview.getMinimumExposure() );
	    assertTrue( mPreview.getCurrentExposure() - mPreview.getMinimumExposure() == seekBar.getProgress() );

	    // test the exposure button clears and reopens without changing exposure level
	    clickView(exposureButton);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(seekBar.getVisibility() == View.GONE);
	    assertTrue(seekBarZoom.getVisibility() == View.GONE);
	    clickView(exposureButton);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(seekBar.getVisibility() == View.VISIBLE);
	    assertTrue(seekBarZoom.getVisibility() == View.VISIBLE);
	    assertTrue( mPreview.getCurrentExposure() == mPreview.getMinimumExposure() );
	    assertTrue( mPreview.getCurrentExposure() - mPreview.getMinimumExposure() == seekBar.getProgress() );

	    // test touch to focus clears the exposure controls
		int [] gui_location = new int[2];
		mPreview.getView().getLocationOnScreen(gui_location);
		final int step_dist_c = 2;
		final int step_count_c = 10;
	    TouchUtils.drag(MainActivityTest.this, gui_location[0]+step_dist_c, gui_location[0], gui_location[1]+step_dist_c, gui_location[1], step_count_c);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(seekBar.getVisibility() == View.GONE);
	    assertTrue(seekBarZoom.getVisibility() == View.GONE);
	    clickView(exposureButton);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(seekBar.getVisibility() == View.VISIBLE);
	    assertTrue(seekBarZoom.getVisibility() == View.VISIBLE);
	    assertTrue( mPreview.getCurrentExposure() == mPreview.getMinimumExposure() );
	    assertTrue( mPreview.getCurrentExposure() - mPreview.getMinimumExposure() == seekBar.getProgress() );

		Log.d(TAG, "set exposure to -1");
	    seekBar.setProgress(-1 - mPreview.getMinimumExposure());
		this.getInstrumentation().waitForIdleSync();
	    assertTrue( mPreview.getCurrentExposure() == -1 );
	    assertTrue( mPreview.getCurrentExposure() - mPreview.getMinimumExposure() == seekBar.getProgress() );

	    // clear again so as to not interfere with take photo routine
	    TouchUtils.drag(MainActivityTest.this, gui_location[0]+step_dist_c, gui_location[0], gui_location[1]+step_dist_c, gui_location[1], step_count_c);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(seekBar.getVisibility() == View.GONE);
	    assertTrue(seekBarZoom.getVisibility() == View.GONE);

	    subTestTakePhoto(false, false, true, true);

	    if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			Log.d(TAG, "switch camera");
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);

		    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
		    assertTrue(seekBar.getVisibility() == View.GONE);
		    assertTrue(seekBarZoom.getVisibility() == View.GONE);
		    assertTrue( mPreview.getCurrentExposure() == -1 );
		    assertTrue( mPreview.getCurrentExposure() - mPreview.getMinimumExposure() == seekBar.getProgress() );

		    clickView(exposureButton);
		    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
		    assertTrue(seekBar.getVisibility() == View.VISIBLE);
		    assertTrue(seekBarZoom.getVisibility() == View.VISIBLE);
		    assertTrue( mPreview.getCurrentExposure() == -1 );
		    assertTrue( mPreview.getCurrentExposure() - mPreview.getMinimumExposure() == seekBar.getProgress() );
		}
	}

	public void testExposureLockNotSaved() {
		Log.d(TAG, "testExposureLockNotSaved");

	    if( !mPreview.supportsExposureLock() ) {
	    	return;
	    }

		setToDefault();

	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    clickView(exposureLockButton);
	    assertTrue(mPreview.getCameraController().getAutoExposureLock());

	    this.pauseAndResume();
	    assertTrue(!mPreview.getCameraController().getAutoExposureLock());

	    // now with restart

	    clickView(exposureLockButton);
	    assertTrue(mPreview.getCameraController().getAutoExposureLock());

	    restart();
	    assertTrue(!mPreview.getCameraController().getAutoExposureLock());
	}

	public void testTakePhotoManualISOExposure() throws InterruptedException {
		Log.d(TAG, "testTakePhotoManualISOExposure");
		if( !mPreview.usingCamera2API() ) {
			return;
		}
		else if( !mPreview.supportsISORange() ) {
			return;
		}
		setToDefault();

		switchToISO(100);

		View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    SeekBar isoSeekBar = (SeekBar) mActivity.findViewById(net.sourceforge.opencamera.R.id.iso_seekbar);
	    SeekBar exposureTimeSeekBar = (SeekBar) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_time_seekbar);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(isoSeekBar.getVisibility() == View.GONE);
	    assertTrue(exposureTimeSeekBar.getVisibility() == View.GONE);
	    
	    clickView(exposureButton);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(isoSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(exposureTimeSeekBar.getVisibility() == (mPreview.supportsExposureTime() ? View.VISIBLE : View.GONE));

	    assertTrue( isoSeekBar.getMax() == 100 );
	    if( mPreview.supportsExposureTime() )
		    assertTrue( exposureTimeSeekBar.getMax() == 100 );

		Log.d(TAG, "change ISO to min");
	    isoSeekBar.setProgress(0);
		this.getInstrumentation().waitForIdleSync();
		assertTrue( mPreview.getCameraController().getISO() == mPreview.getMinimumISO() );

	    if( mPreview.supportsExposureTime() ) {
			Log.d(TAG, "change exposure time to min");
		    exposureTimeSeekBar.setProgress(0);
			this.getInstrumentation().waitForIdleSync();
			assertTrue( mPreview.getCameraController().getISO() == mPreview.getMinimumISO() );
			assertTrue( mPreview.getCameraController().getExposureTime() == mPreview.getMinimumExposureTime() );
	    }

		Log.d(TAG, "change ISO to max");
	    isoSeekBar.setProgress(100);
		this.getInstrumentation().waitForIdleSync();
		assertTrue( mPreview.getCameraController().getISO() == mPreview.getMaximumISO() );

	    if( mPreview.supportsExposureTime() ) {
			Log.d(TAG, "change exposure time to max");
		    exposureTimeSeekBar.setProgress(100);
			this.getInstrumentation().waitForIdleSync();
			assertTrue( mPreview.getCameraController().getISO() == mPreview.getMaximumISO() );
			assertTrue( mPreview.getCameraController().getExposureTime() == mPreview.getMaximumExposureTime() );
	    }

	    // test the exposure button clears and reopens without changing exposure level
	    clickView(exposureButton);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(isoSeekBar.getVisibility() == View.GONE);
	    assertTrue(exposureTimeSeekBar.getVisibility() == View.GONE);
	    clickView(exposureButton);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(isoSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(exposureTimeSeekBar.getVisibility() == (mPreview.supportsExposureTime() ? View.VISIBLE : View.GONE));
		assertTrue( mPreview.getCameraController().getISO() == mPreview.getMaximumISO() );
	    if( mPreview.supportsExposureTime() )
			assertTrue( mPreview.getCameraController().getExposureTime() == mPreview.getMaximumExposureTime() );

	    // test touch to focus clears the exposure controls
		int [] gui_location = new int[2];
		mPreview.getView().getLocationOnScreen(gui_location);
		final int step_dist_c = 2;
		final int step_count_c = 10;
	    TouchUtils.drag(MainActivityTest.this, gui_location[0]+step_dist_c, gui_location[0], gui_location[1]+step_dist_c, gui_location[1], step_count_c);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(isoSeekBar.getVisibility() == View.GONE);
	    assertTrue(exposureTimeSeekBar.getVisibility() == View.GONE);
	    clickView(exposureButton);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(isoSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(exposureTimeSeekBar.getVisibility() == (mPreview.supportsExposureTime() ? View.VISIBLE : View.GONE));
		assertTrue( mPreview.getCameraController().getISO() == mPreview.getMaximumISO() );
	    if( mPreview.supportsExposureTime() )
			assertTrue( mPreview.getCameraController().getExposureTime() == mPreview.getMaximumExposureTime() );

	    // clear again so as to not interfere with take photo routine
	    TouchUtils.drag(MainActivityTest.this, gui_location[0]+step_dist_c, gui_location[0], gui_location[1]+step_dist_c, gui_location[1], step_count_c);
	    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
	    assertTrue(isoSeekBar.getVisibility() == View.GONE);
	    assertTrue(exposureTimeSeekBar.getVisibility() == View.GONE);

	    subTestTakePhoto(false, false, true, true);

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			Log.d(TAG, "switch camera");
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);

		    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
		    assertTrue(isoSeekBar.getVisibility() == View.GONE);
		    assertTrue(exposureTimeSeekBar.getVisibility() == View.GONE);
			assertTrue( mPreview.getCameraController().getISO() == mPreview.getMaximumISO() );
		    if( mPreview.supportsExposureTime() )
				assertTrue( mPreview.getCameraController().getExposureTime() == mPreview.getMaximumExposureTime() );

		    clickView(exposureButton);
		    assertTrue(exposureButton.getVisibility() == View.VISIBLE);
		    assertTrue(isoSeekBar.getVisibility() == View.VISIBLE);
		    assertTrue(exposureTimeSeekBar.getVisibility() == (mPreview.supportsExposureTime() ? View.VISIBLE : View.GONE));
			assertTrue( mPreview.getCameraController().getISO() == mPreview.getMaximumISO() );
		    if( mPreview.supportsExposureTime() )
				assertTrue( mPreview.getCameraController().getExposureTime() == mPreview.getMaximumExposureTime() );
		}
	}

	private void subTestTakePhoto(boolean locked_focus, boolean immersive_mode, boolean touch_to_focus, boolean wait_after_focus) throws InterruptedException {
		assertTrue(mPreview.isPreviewStarted());

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		boolean has_thumbnail_anim = sharedPreferences.getBoolean(PreferenceKeys.getThumbnailAnimationPreferenceKey(), true);
		
		// count initial files in folder
		File folder = mActivity.getImageFolder();
		Log.d(TAG, "folder: " + folder);
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);
		
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    //View flashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.flash);
	    //View focusButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_mode);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    View trashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.trash);
	    View shareButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.share);
	    assertTrue(switchCameraButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(switchVideoButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    int exposureVisibility = exposureButton.getVisibility();
	    int exposureLockVisibility = exposureLockButton.getVisibility();
	    assertTrue(popupButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

		String focus_value = mPreview.getCameraController().getFocusValue();
		boolean can_auto_focus = false;
		boolean can_focus_area = false;
        if( focus_value.equals("focus_mode_auto") || focus_value.equals("focus_mode_macro") ) {
        	can_auto_focus = true;
        }
        if( mPreview.getMaxNumFocusAreas() != 0 && ( focus_value.equals("focus_mode_auto") || focus_value.equals("focus_mode_macro") || focus_value.equals("focus_mode_continuous_picture") || focus_value.equals("focus_mode_continuous_video") || focus_value.equals("focus_mode_manual2") ) ) {
        	can_focus_area = true;
        }
		Log.d(TAG, "can_auto_focus? " + can_auto_focus);
		Log.d(TAG, "can_focus_area? " + can_focus_area);
	    int saved_count = mPreview.count_cameraAutoFocus;
	    if( touch_to_focus ) {
			// touch to auto-focus with focus area (will also exit immersive mode)
			// autofocus shouldn't be immediately, but after a delay
			Thread.sleep(1000);
		    saved_count = mPreview.count_cameraAutoFocus;
			Log.d(TAG, "saved count_cameraAutoFocus: " + saved_count);
			Log.d(TAG, "about to click preview for autofocus");
			TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
			Log.d(TAG, "1 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
			assertTrue(mPreview.count_cameraAutoFocus == (can_auto_focus ? saved_count+1 : saved_count));
			assertTrue(mPreview.hasFocusArea() == can_focus_area);
			if( can_focus_area ) {
			    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
			    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
			    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
			}
			else {
			    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
			    // we still set metering areas
			    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
			}
			if( wait_after_focus ) {
				Thread.sleep(3000);
			}
	    }
		Log.d(TAG, "saved count_cameraAutoFocus: " + saved_count);

		View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		assertFalse( mActivity.hasThumbnailAnimation() );
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
		    assertTrue(!mPreview.isTakingPhoto() || switchCameraButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || switchVideoButton.getVisibility() == View.GONE);
		    //assertTrue(!mPreview.isTakingPhoto() || flashButton.getVisibility() == View.GONE);
		    //assertTrue(!mPreview.isTakingPhoto() || focusButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || exposureButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || exposureLockButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || popupButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || trashButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || shareButton.getVisibility() == View.GONE);
	    }
		Log.d(TAG, "done taking photo");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue(mPreview.count_cameraTakePicture==1);
		assertTrue(mActivity.hasThumbnailAnimation() == has_thumbnail_anim );

		assertTrue( folder.exists() );
		int n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);
		Thread.sleep(1000); // wait until we've scanned
		assertFalse(mActivity.getStorageUtils().failed_to_scan);

		// in locked focus mode, taking photo should never redo an auto-focus
		// if photo mode, we may do a refocus if the previous auto-focus failed, but not if it succeeded
		Log.d(TAG, "2 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		if( locked_focus ) {
			assertTrue(mPreview.count_cameraAutoFocus == (can_auto_focus ? saved_count+1 : saved_count));
		}
		if( touch_to_focus ) {
			assertTrue(mPreview.hasFocusArea() == can_focus_area);
			if( can_focus_area ) {
			    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
			    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
			    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
			}
			else {
			    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
			    // we still set metering areas
			    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
			}
		}
		else {
			assertFalse(mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);
		}

		// trash/share only shown when preview is paused after taking a photo

		assertTrue(mPreview.isPreviewStarted()); // check preview restarted
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    if( !immersive_mode ) {
	    	assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    	assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    }
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	}

	public void testTakePhoto() throws InterruptedException {
		Log.d(TAG, "testTakePhoto");
		setToDefault();
		subTestTakePhoto(false, false, true, true);
	}

	public void testTakePhotoNoAutofocus() throws InterruptedException {
		Log.d(TAG, "testTakePhotoNoAutofocus");
		setToDefault();
		subTestTakePhoto(false, false, false, false);
	}

	public void testTakePhotoNoThumbnail() throws InterruptedException {
		Log.d(TAG, "testTakePhotoNoThumbnail");
		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getThumbnailAnimationPreferenceKey(), false);
		editor.apply();
		subTestTakePhoto(false, false, true, true);
	}

	/* Tests bug fixed by take_photo_after_autofocus in Preview, where the app would hang due to taking a photo after touching to focus. */
	public void testTakePhotoFlashBug() throws InterruptedException {
		Log.d(TAG, "testTakePhotoFlashBug");
		setToDefault();
		switchToFlashValue("flash_on");
		subTestTakePhoto(false, false, true, false);
	}

	public void testTakePhotoFrontCamera() throws InterruptedException {
		Log.d(TAG, "testTakePhotoFrontCamera");
		if( mPreview.getCameraControllerManager().getNumberOfCameras() == 0 ) {
			return;
		}
		setToDefault();
		int cameraId = mPreview.getCameraId();
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    clickView(switchCameraButton);
		int new_cameraId = mPreview.getCameraId();
		assertTrue(cameraId != new_cameraId);
		subTestTakePhoto(false, false, true, true);
	}

	public void testTakePhotoLockedFocus() throws InterruptedException {
		Log.d(TAG, "testTakePhotoLockedFocus");
		setToDefault();
		switchToFocusValue("focus_mode_locked");
		subTestTakePhoto(true, false, true, true);
	}

	public void testTakePhotoManualFocus() throws InterruptedException {
		Log.d(TAG, "testTakePhotoManualFocus");
		if( !mPreview.usingCamera2API() ) {
			return;
		}
		setToDefault();
	    SeekBar seekBar = (SeekBar) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_seekbar);
	    assertTrue(seekBar.getVisibility() == View.INVISIBLE);
		switchToFocusValue("focus_mode_manual2");
	    assertTrue(seekBar.getVisibility() == View.VISIBLE);
		seekBar.setProgress( (int)(0.25*(seekBar.getMax()-1)) );
		subTestTakePhoto(false, false, true, true);
	}

	public void testTakePhotoLockedLandscape() throws InterruptedException {
		Log.d(TAG, "testTakePhotoLockedLandscape");
		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getLockOrientationPreferenceKey(), "landscape");
		editor.apply();
		updateForSettings();
		subTestTakePhoto(false, false, true, true);
	}

	public void testTakePhotoLockedPortrait() throws InterruptedException {
		Log.d(TAG, "testTakePhotoLockedPortrait");
		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getLockOrientationPreferenceKey(), "portrait");
		editor.apply();
		updateForSettings();
		subTestTakePhoto(false, false, true, true);
	}

	// If this test fails, make sure we've manually selected that folder (as permission can't be given through the test framework)
	public void testTakePhotoSAF() throws InterruptedException {
		Log.d(TAG, "testTakePhotoSAF");

		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
			Log.d(TAG, "SAF requires Android Lollipop or better");
			return;
		}

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), true);
		editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "content://com.android.externalstorage.documents/tree/primary%3ADCIM%2FOpenCamera");
		editor.apply();
		updateForSettings();

		subTestTakePhoto(false, false, true, true);
	}

	// If this fails with a SecurityException about needing INJECT_EVENTS permission, this seems to be due to the "help popup" that Android shows - can be fixed by clearing that manually, then rerunning the test.
	public void testImmersiveMode() throws InterruptedException {
		Log.d(TAG, "testImmersiveMode");

		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ) {
			Log.d(TAG, "immersive mode requires Android Kitkat or better");
			return;
		}

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getImmersiveModePreferenceKey(), "immersive_mode_gui");
		editor.apply();
		updateForSettings();

	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    View trashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.trash);
	    View shareButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.share);
	    View zoomSeekBar = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.zoom_seekbar);
	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    int exposureVisibility = exposureButton.getVisibility();
	    int exposureLockVisibility = exposureLockButton.getVisibility();
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);

	    // now wait for immersive mode to kick in
	    Thread.sleep(6000);
	    assertTrue(switchCameraButton.getVisibility() == View.GONE);
	    assertTrue(switchVideoButton.getVisibility() == View.GONE);
	    assertTrue(exposureButton.getVisibility() == View.GONE);
	    assertTrue(exposureLockButton.getVisibility() == View.GONE);
	    assertTrue(popupButton.getVisibility() == View.GONE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.GONE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);
	    
	    subTestTakePhoto(false, true, true, true);
	    
	    // test now exited immersive mode
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);

	    // wait for immersive mode to kick in again
	    Thread.sleep(6000);
	    assertTrue(switchCameraButton.getVisibility() == View.GONE);
	    assertTrue(switchVideoButton.getVisibility() == View.GONE);
	    assertTrue(exposureButton.getVisibility() == View.GONE);
	    assertTrue(exposureLockButton.getVisibility() == View.GONE);
	    assertTrue(popupButton.getVisibility() == View.GONE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.GONE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);

	    subTestTakePhotoPreviewPaused(true);

	    // test now exited immersive mode
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);

	    // need to switch video before going back to immersive mode
		if( !mPreview.isVideo() ) {
			clickView(switchVideoButton);
		}
	    // test now exited immersive mode
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);
	    
	    // wait for immersive mode to kick in again
	    Thread.sleep(6000);
	    assertTrue(switchCameraButton.getVisibility() == View.GONE);
	    assertTrue(switchVideoButton.getVisibility() == View.GONE);
	    assertTrue(exposureButton.getVisibility() == View.GONE);
	    assertTrue(exposureLockButton.getVisibility() == View.GONE);
	    assertTrue(popupButton.getVisibility() == View.GONE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.GONE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);
	    
	    subTestTakeVideo(false, false, false, true, false);

	    // test touch exits immersive mode
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);

	    // switch back to photo mode
		if( mPreview.isVideo() ) {
			clickView(switchVideoButton);
		}

		if( mPreview.usingCamera2API() && mPreview.supportsISORange() ) {
		    // now test exposure button disappears when in manual ISO mode
			switchToISO(100);

			// wait for immersive mode to kick in again
		    Thread.sleep(6000);
		    assertTrue(switchCameraButton.getVisibility() == View.GONE);
		    assertTrue(switchVideoButton.getVisibility() == View.GONE);
		    assertTrue(exposureButton.getVisibility() == View.GONE);
		    assertTrue(exposureLockButton.getVisibility() == View.GONE);
		    assertTrue(popupButton.getVisibility() == View.GONE);
		    assertTrue(trashButton.getVisibility() == View.GONE);
		    assertTrue(shareButton.getVisibility() == View.GONE);
		    assertTrue(zoomSeekBar.getVisibility() == View.GONE);
		    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);
		}
	}

	// See note under testImmersiveMode() if this fails with a SecurityException about needing INJECT_EVENTS permission.
	public void testImmersiveModeEverything() throws InterruptedException {
		Log.d(TAG, "testImmersiveModeEverything");

		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ) {
			Log.d(TAG, "immersive mode requires Android Kitkat or better");
			return;
		}

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getImmersiveModePreferenceKey(), "immersive_mode_everything");
		editor.apply();
		updateForSettings();

	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    View trashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.trash);
	    View shareButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.share);
	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
	    View zoomSeekBar = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.zoom_seekbar);
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    int exposureVisibility = exposureButton.getVisibility();
	    int exposureLockVisibility = exposureLockButton.getVisibility();
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);

	    // now wait for immersive mode to kick in
	    Thread.sleep(6000);
	    assertTrue(switchCameraButton.getVisibility() == View.GONE);
	    assertTrue(switchVideoButton.getVisibility() == View.GONE);
	    assertTrue(exposureButton.getVisibility() == View.GONE);
	    assertTrue(exposureLockButton.getVisibility() == View.GONE);
	    assertTrue(popupButton.getVisibility() == View.GONE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.GONE);
	    assertTrue(takePhotoButton.getVisibility() == View.GONE);
	    
	    // now touch to exit immersive mode
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		Thread.sleep(500);

		// test now exited immersive mode
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);

	    // test touch exits immersive mode
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	    assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    assertTrue(takePhotoButton.getVisibility() == View.VISIBLE);
	}
	
	private void subTestTakePhotoPreviewPaused(boolean immersive_mode) throws InterruptedException {
		mPreview.count_cameraTakePicture = 0;

		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getPausePreviewPreferenceKey(), true);
		editor.apply();

		Log.d(TAG, "check if preview is started");
		assertTrue(mPreview.isPreviewStarted());
		
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    //View flashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.flash);
	    //View focusButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_mode);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    View trashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.trash);
	    View shareButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.share);
	    assertTrue(switchCameraButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(switchVideoButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    // store status to compare with later
	    int exposureVisibility = exposureButton.getVisibility();
	    int exposureLockVisibility = exposureLockButton.getVisibility();
	    assertTrue(popupButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
		    assertTrue(!mPreview.isTakingPhoto() || switchCameraButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || switchVideoButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || popupButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || exposureButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || exposureLockButton.getVisibility() == View.GONE);
		    // trash/share not yet shown, as still taking the photo
		    assertTrue(!mPreview.isTakingPhoto() || trashButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || shareButton.getVisibility() == View.GONE);
	    }
		Log.d(TAG, "done taking photo");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue(mPreview.count_cameraTakePicture==1);
		
		Bitmap thumbnail = mActivity.gallery_bitmap;
		assertTrue(thumbnail != null);

		int n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);

		// now preview should be paused
		assertTrue(!mPreview.isPreviewStarted()); // check preview paused
	    assertTrue(switchCameraButton.getVisibility() == View.GONE);
	    assertTrue(switchVideoButton.getVisibility() == View.GONE);
	    assertTrue(exposureButton.getVisibility() == View.GONE);
	    assertTrue(exposureLockButton.getVisibility() == View.GONE);
	    assertTrue(popupButton.getVisibility() == View.GONE);
	    assertTrue(trashButton.getVisibility() == View.VISIBLE);
	    assertTrue(shareButton.getVisibility() == View.VISIBLE);

		Log.d(TAG, "about to click preview");
	    TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		Log.d(TAG, "done click preview");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync 3");

		// check photo not deleted
		n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);

	    assertTrue(mPreview.isPreviewStarted()); // check preview restarted
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    //assertTrue(flashButton.getVisibility() == flashVisibility);
	    //assertTrue(focusButton.getVisibility() == focusVisibility);
	    if( !immersive_mode ) {
		    assertTrue(exposureButton.getVisibility() == exposureVisibility);
		    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    }
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

	    // check still same icon even after a delay
		assertTrue(mActivity.gallery_bitmap == thumbnail);
	    Thread.sleep(1000);
		assertTrue(mActivity.gallery_bitmap == thumbnail);
	}

	public void testTakePhotoPreviewPaused() throws InterruptedException {
		Log.d(TAG, "testTakePhotoPreviewPaused");
		setToDefault();
		subTestTakePhotoPreviewPaused(false);
	}
	
	public void testTakePhotoPreviewPausedSAF() throws InterruptedException {
		Log.d(TAG, "testTakePhotoPreviewPausedSAF");

		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
			Log.d(TAG, "SAF requires Android Lollipop or better");
			return;
		}

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), true);
		editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "content://com.android.externalstorage.documents/tree/primary%3ADCIM%2FOpenCamera");
		editor.apply();
		updateForSettings();

		subTestTakePhotoPreviewPaused(false);
	}
	
	private void subTestTakePhotoPreviewPausedTrash() throws InterruptedException {
		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getPausePreviewPreferenceKey(), true);
		editor.apply();

		assertTrue(mPreview.isPreviewStarted());
		
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    //View flashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.flash);
	    //View focusButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_mode);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    View trashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.trash);
	    View shareButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.share);
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    // flash and focus etc default visibility tested in another test
	    // but store status to compare with later
	    //int flashVisibility = flashButton.getVisibility();
	    //int focusVisibility = focusButton.getVisibility();
	    int exposureVisibility = exposureButton.getVisibility();
	    int exposureLockVisibility = exposureLockButton.getVisibility();
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
		    assertTrue(!mPreview.isTakingPhoto() || switchCameraButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || switchVideoButton.getVisibility() == View.GONE);
		    //assertTrue(!mPreview.isTakingPhoto() || flashButton.getVisibility() == View.GONE);
		    //assertTrue(!mPreview.isTakingPhoto() || focusButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || exposureButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || exposureLockButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || popupButton.getVisibility() == View.GONE);
		    // trash/share not yet shown, as still taking the photo
		    assertTrue(!mPreview.isTakingPhoto() || trashButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || shareButton.getVisibility() == View.GONE);
	    }
		Log.d(TAG, "done taking photo");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue(mPreview.count_cameraTakePicture==1);
		
		Bitmap thumbnail = mActivity.gallery_bitmap;
		assertTrue(thumbnail != null);
		
		int n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);

		// now preview should be paused
		assertTrue(!mPreview.isPreviewStarted()); // check preview restarted
	    assertTrue(switchCameraButton.getVisibility() == View.GONE);
	    assertTrue(switchVideoButton.getVisibility() == View.GONE);
	    //assertTrue(flashButton.getVisibility() == View.GONE);
	    //assertTrue(focusButton.getVisibility() == View.GONE);
	    assertTrue(exposureButton.getVisibility() == View.GONE);
	    assertTrue(exposureLockButton.getVisibility() == View.GONE);
	    assertTrue(popupButton.getVisibility() == View.GONE);
	    assertTrue(trashButton.getVisibility() == View.VISIBLE);
	    assertTrue(shareButton.getVisibility() == View.VISIBLE);

		Log.d(TAG, "about to click trash");
		clickView(trashButton);
		Log.d(TAG, "done click trash");

		// check photo deleted
		n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 0);

		assertTrue(mPreview.isPreviewStarted()); // check preview restarted
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    //assertTrue(flashButton.getVisibility() == flashVisibility);
	    //assertTrue(focusButton.getVisibility() == focusVisibility);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

	    // icon may be null, or have been set to another image - only changed after a delay
	    Thread.sleep(1000);
		assertTrue(mActivity.gallery_bitmap != thumbnail);
	}

	public void testTakePhotoPreviewPausedTrash() throws InterruptedException {
		Log.d(TAG, "testTakePhotoPreviewPausedTrash");
		setToDefault();
		subTestTakePhotoPreviewPausedTrash();
	}

	public void testTakePhotoPreviewPausedTrashSAF() throws InterruptedException {
		Log.d(TAG, "testTakePhotoPreviewPausedTrashSAF");

		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
			Log.d(TAG, "SAF requires Android Lollipop or better");
			return;
		}

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), true);
		editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "content://com.android.externalstorage.documents/tree/primary%3ADCIM%2FOpenCamera");
		editor.apply();
		updateForSettings();

		subTestTakePhotoPreviewPausedTrash();
	}

	/* Tests that we don't do an extra autofocus when taking a photo, if recently touch-focused.
	 */
	public void testTakePhotoQuickFocus() throws InterruptedException {
		Log.d(TAG, "testTakePhotoQuickFocus");
		setToDefault();
		
		assertTrue(mPreview.isPreviewStarted());
		
		// touch to auto-focus with focus area
		// autofocus shouldn't be immediately, but after a delay
		Thread.sleep(1000);
	    int saved_count = mPreview.count_cameraAutoFocus;
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		Log.d(TAG, "1 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);

	    // wait 3s for auto-focus to complete
		Thread.sleep(3000);

		View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		Log.d(TAG, "done taking photo");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue(mPreview.count_cameraTakePicture==1);

		// taking photo shouldn't have done an auto-focus, and still have focus areas
		// except on Camera2, where currently we do do an autofocus
		Log.d(TAG, "2 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == (mPreview.usingCamera2API() ? saved_count+2 : saved_count+1));
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
	}

	public void takePhotoRepeatFocus(boolean locked) throws InterruptedException {
		Log.d(TAG, "takePhotoRepeatFocus");
		setToDefault();
		if( locked ) {
			switchToFocusValue("focus_mode_locked");
		}

		assertTrue(mPreview.isPreviewStarted());
		
		// touch to auto-focus with focus area
		// autofocus shouldn't be immediately, but after a delay
		Thread.sleep(1000);
	    int saved_count = mPreview.count_cameraAutoFocus;
		TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		Log.d(TAG, "1 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);

	    // wait 3s for auto-focus to complete, and 5s to require additional auto-focus when taking a photo
		Thread.sleep(8000);

		View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		Log.d(TAG, "done taking photo");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue(mPreview.count_cameraTakePicture==1);

		// taking photo should have done an auto-focus iff in automatic mode, and still have focus areas
		Log.d(TAG, "2 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == (locked ? saved_count+1 : saved_count+2));
		assertTrue(mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
	    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
	}

	/* Tests that we do an extra autofocus when taking a photo, if too long since last touch-focused.
	 */
	public void testTakePhotoRepeatFocus() throws InterruptedException {
		Log.d(TAG, "testTakePhotoRepeatFocus");
		takePhotoRepeatFocus(false);
	}

	/* Tests that we don't do an extra autofocus when taking a photo, if too long since last touch-focused, when in locked focus mode.
	 */
	public void testTakePhotoRepeatFocusLocked() throws InterruptedException {
		Log.d(TAG, "testTakePhotoRepeatFocusLocked");
		takePhotoRepeatFocus(true);
	}

	/* Tests taking a photo with animation and shutter disabled, and not setting focus areas
	 */
	public void testTakePhotoAlt() throws InterruptedException {
		Log.d(TAG, "testTakePhotoAlt");
		setToDefault();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getThumbnailAnimationPreferenceKey(), false);
		editor.putBoolean(PreferenceKeys.getShutterSoundPreferenceKey(), false);
		editor.apply();

		assertTrue(mPreview.isPreviewStarted());
		
		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);
		
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    //View flashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.flash);
	    //View focusButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_mode);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    View trashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.trash);
	    View shareButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.share);
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    // flash and focus etc default visibility tested in another test
	    // but store status to compare with later
	    //int flashVisibility = flashButton.getVisibility();
	    //int focusVisibility = focusButton.getVisibility();
	    int exposureVisibility = exposureButton.getVisibility();
	    int exposureLockVisibility = exposureLockButton.getVisibility();
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

		// autofocus shouldn't be immediately, but after a delay
		Thread.sleep(2000);
	    int saved_count = mPreview.count_cameraAutoFocus;

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
		    assertTrue(!mPreview.isTakingPhoto() || switchCameraButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || switchVideoButton.getVisibility() == View.GONE);
		    //assertTrue(!mPreview.isTakingPhoto() || flashButton.getVisibility() == View.GONE);
		    //assertTrue(!mPreview.isTakingPhoto() || focusButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || exposureButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || exposureLockButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || popupButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || trashButton.getVisibility() == View.GONE);
		    assertTrue(!mPreview.isTakingPhoto() || shareButton.getVisibility() == View.GONE);
	    }
		Log.d(TAG, "done taking photo");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue(mPreview.count_cameraTakePicture==1);

		int n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);

		// taking photo should have done an auto-focus, and no focus areas
		Log.d(TAG, "2 count_cameraAutoFocus: " + mPreview.count_cameraAutoFocus);
		assertTrue(mPreview.count_cameraAutoFocus == saved_count+1);
		assertTrue(!mPreview.hasFocusArea());
	    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
	    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

		// trash/share only shown when preview is paused after taking a photo

		assertTrue(mPreview.isPreviewStarted()); // check preview restarted
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    //assertTrue(flashButton.getVisibility() == flashVisibility);
	    //assertTrue(focusButton.getVisibility() == focusVisibility);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	}

	public void takePhotoLoop(int count) {
		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);

		int start_count = mPreview.count_cameraTakePicture;
		while( mPreview.count_cameraTakePicture - start_count < count ) {
		    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
			Log.d(TAG, "about to click take photo");
		    clickView(takePhotoButton);
			Log.d(TAG, "wait until finished taking photo");
		    while( mPreview.isTakingPhoto() ) {
		    }
			Log.d(TAG, "done taking photo");
			this.getInstrumentation().waitForIdleSync();

			int n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == mPreview.count_cameraTakePicture - start_count);
		}
	}

	/* Tests taking photos repeatedly with auto-stabilise enabled.
	 * Tests with front and back; and then tests again with test_low_memory set.
	 */
	public void testTakePhotoAutoLevel() {
		Log.d(TAG, "testTakePhotoAutoLevel");
		setToDefault();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), true);
		editor.apply();

		assertTrue(mPreview.isPreviewStarted());
		final int n_photos_c = 5;

		takePhotoLoop(n_photos_c);
		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			int cameraId = mPreview.getCameraId();
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    while( switchCameraButton.getVisibility() != View.VISIBLE ) {
		    	// wait until photo is taken and button is visible again
		    }
		    clickView(switchCameraButton);
			int new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId != new_cameraId);
			takePhotoLoop(n_photos_c);
		    while( switchCameraButton.getVisibility() != View.VISIBLE ) {
		    	// wait until photo is taken and button is visible again
		    }
		    clickView(switchCameraButton);
			new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId == new_cameraId);
		}

		mActivity.test_low_memory = true;

		takePhotoLoop(n_photos_c);
		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			int cameraId = mPreview.getCameraId();
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    while( switchCameraButton.getVisibility() != View.VISIBLE ) {
		    	// wait until photo is taken and button is visible again
		    }
		    clickView(switchCameraButton);
			int new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId != new_cameraId);
			takePhotoLoop(n_photos_c);
		    while( switchCameraButton.getVisibility() != View.VISIBLE ) {
		    	// wait until photo is taken and button is visible again
		    }
		    clickView(switchCameraButton);
			new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId == new_cameraId);
		}
	}

	public void takePhotoLoopAngles(int [] angles) {
		// count initial files in folder
		mActivity.test_have_angle = true;
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);

		int start_count = mPreview.count_cameraTakePicture;
		while( mPreview.count_cameraTakePicture - start_count < angles.length ) {
			mActivity.test_angle = angles[mPreview.count_cameraTakePicture - start_count];
		    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
			Log.d(TAG, "about to click take photo");
		    clickView(takePhotoButton);
			Log.d(TAG, "wait until finished taking photo");
		    while( mPreview.isTakingPhoto() ) {
		    }
			Log.d(TAG, "done taking photo");
			this.getInstrumentation().waitForIdleSync();

			int n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == mPreview.count_cameraTakePicture - start_count);
		}

		mActivity.test_have_angle = false;
	}

	/* Tests taking photos repeatedly with auto-stabilise enabled, at various angles.
	 * Tests with front and back; and then tests again with test_low_memory set.
	 */
	public void testTakePhotoAutoLevelAngles() {
		Log.d(TAG, "testTakePhotoAutoLevel");
		setToDefault();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), true);
		editor.apply();

		assertTrue(mPreview.isPreviewStarted());
		final int [] angles = new int[]{0, -129, 30, -44, 61, -89, 179};

		takePhotoLoopAngles(angles);
		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			int cameraId = mPreview.getCameraId();
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    while( switchCameraButton.getVisibility() != View.VISIBLE ) {
		    	// wait until photo is taken and button is visible again
		    }
		    clickView(switchCameraButton);
			int new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId != new_cameraId);
			takePhotoLoopAngles(angles);
		    while( switchCameraButton.getVisibility() != View.VISIBLE ) {
		    	// wait until photo is taken and button is visible again
		    }
		    clickView(switchCameraButton);
			new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId == new_cameraId);
		}

		mActivity.test_low_memory = true;

		takePhotoLoopAngles(angles);
		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			int cameraId = mPreview.getCameraId();
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    while( switchCameraButton.getVisibility() != View.VISIBLE ) {
		    	// wait until photo is taken and button is visible again
		    }
		    clickView(switchCameraButton);
			int new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId != new_cameraId);
			takePhotoLoopAngles(angles);
		    while( switchCameraButton.getVisibility() != View.VISIBLE ) {
		    	// wait until photo is taken and button is visible again
		    }
		    clickView(switchCameraButton);
			new_cameraId = mPreview.getCameraId();
			assertTrue(cameraId == new_cameraId);
		}
	}

	private void subTestTakeVideo(boolean test_exposure_lock, boolean test_focus_area, boolean allow_failure, boolean immersive_mode, boolean quick) throws InterruptedException {
		assertTrue(mPreview.isPreviewStarted());

		if( test_exposure_lock && !mPreview.supportsExposureLock() ) {
			return;
		}

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		if( mPreview.isVideo() ) {
			assertTrue( (Integer)takePhotoButton.getTag() == net.sourceforge.opencamera.R.drawable.take_video_selector );
		}
		else {
			assertTrue( (Integer)takePhotoButton.getTag() == net.sourceforge.opencamera.R.drawable.take_photo_selector );
		}

	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
		if( !mPreview.isVideo() ) {
			clickView(switchVideoButton);
		}
	    assertTrue(mPreview.isVideo());
		assertTrue(mPreview.isPreviewStarted());
		
		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);
		
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    //View flashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.flash);
	    //View focusButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_mode);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    View trashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.trash);
	    View shareButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.share);
	    assertTrue(switchCameraButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(switchVideoButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    // but store status to compare with later
	    int exposureVisibility = exposureButton.getVisibility();
	    int exposureLockVisibility = exposureLockButton.getVisibility();
	    assertTrue(popupButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

	    assertTrue( (Integer)takePhotoButton.getTag() == net.sourceforge.opencamera.R.drawable.take_video_selector );
		Log.d(TAG, "about to click take video");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take video");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");

	    if( mPreview.isTakingPhoto() ) {
		    assertTrue( (Integer)takePhotoButton.getTag() == net.sourceforge.opencamera.R.drawable.take_video_recording );
		    assertTrue(switchCameraButton.getVisibility() == View.GONE);
		    assertTrue(switchVideoButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
		    assertTrue(popupButton.getVisibility() == (!immersive_mode && mPreview.supportsFlash() ? View.VISIBLE : View.GONE)); // popup button only visible when recording video if flash supported
		    assertTrue(exposureButton.getVisibility() == exposureVisibility);
		    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
		    assertTrue(trashButton.getVisibility() == View.GONE);
		    assertTrue(shareButton.getVisibility() == View.GONE);

		    if( !immersive_mode && !quick ) {
			    // test turning torch on/off (if in immersive mode, popup button will be hidden)
				switchToFlashValue("flash_torch");
			    Thread.sleep(500);
				switchToFlashValue("flash_off");
		    }
		    
		    if( quick ) {
		    	// still need a short delay (at least 500ms, otherwise Open Camera will ignore the repeated stop)
		    	Thread.sleep(500);
		    }
		    else {
		    	Thread.sleep(5000);
		    }
		    assertTrue( (Integer)takePhotoButton.getTag() == net.sourceforge.opencamera.R.drawable.take_video_recording );

			assertTrue(!mPreview.hasFocusArea());
			if( !allow_failure ) {
			    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);
			}

		    if( test_focus_area ) {
				// touch to auto-focus with focus area
				Log.d(TAG, "touch to focus");
				TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
			    Thread.sleep(1000); // wait for autofocus
				assertTrue(mPreview.hasFocusArea());
			    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
			    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
			    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
			    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
				Log.d(TAG, "done touch to focus");

				// this time, don't wait
				Log.d(TAG, "touch again to focus");
				TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
		    }
		    
		    if( test_exposure_lock ) {
				Log.d(TAG, "test exposure lock");
			    assertTrue( !mPreview.getCameraController().getAutoExposureLock() );
			    clickView(exposureLockButton);
				this.getInstrumentation().waitForIdleSync();
				Log.d(TAG, "after idle sync");
			    assertTrue( mPreview.getCameraController().getAutoExposureLock() );
			    Thread.sleep(2000);
		    }
	
		    assertTrue( (Integer)takePhotoButton.getTag() == net.sourceforge.opencamera.R.drawable.take_video_recording );
			Log.d(TAG, "about to click stop video");
		    clickView(takePhotoButton);
			Log.d(TAG, "done clicking stop video");
			this.getInstrumentation().waitForIdleSync();
			Log.d(TAG, "after idle sync");
	    }
	    else {
			Log.d(TAG, "didn't start video");
			assertTrue(allow_failure);
	    }

		assertTrue( folder.exists() );
		int n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		if( quick ) {
			// if quick, should have deleted corrupt video - but may be device dependent, sometimes we manage to record a video anyway!
			assertTrue(n_new_files == 0 || n_new_files == 1);
		}
		else {
			assertTrue(n_new_files == 1);
		}

		// trash/share only shown when preview is paused after taking a photo

		assertTrue(mPreview.isPreviewStarted()); // check preview restarted
	    assertTrue(switchCameraButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(switchVideoButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == (immersive_mode ? View.GONE : View.VISIBLE));
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

	    assertTrue( (Integer)takePhotoButton.getTag() == net.sourceforge.opencamera.R.drawable.take_video_selector );
	}

	public void testTakeVideo() throws InterruptedException {
		Log.d(TAG, "testTakeVideo");

		setToDefault();

		subTestTakeVideo(false, false, false, false, false);
	}

	public void testTakeVideoSAF() throws InterruptedException {
		Log.d(TAG, "testTakeVideoSAF");

		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
			Log.d(TAG, "SAF requires Android Lollipop or better");
			return;
		}

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), true);
		editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "content://com.android.externalstorage.documents/tree/primary%3ADCIM%2FOpenCamera");
		editor.apply();
		updateForSettings();

		subTestTakeVideo(false, false, false, false, false);
	}
	
	public void testTakeVideoStabilization() throws InterruptedException {
		Log.d(TAG, "testTakeVideoStabilization");

	    if( !mPreview.supportsVideoStabilization() ) {
			Log.d(TAG, "video stabilization not supported");
	    	return;
	    }
	    assertFalse(mPreview.getCameraController().getVideoStabilization());

	    setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getVideoStabilizationPreferenceKey(), true);
		editor.apply();
		updateForSettings();
	    assertTrue(mPreview.getCameraController().getVideoStabilization());

		subTestTakeVideo(false, false, false, false, false);

	    assertTrue(mPreview.getCameraController().getVideoStabilization());
	}

	public void testTakeVideoExposureLock() throws InterruptedException {
		Log.d(TAG, "testTakeVideoExposureLock");

		setToDefault();

		subTestTakeVideo(true, false, false, false, false);
	}

	public void testTakeVideoFocusArea() throws InterruptedException {
		Log.d(TAG, "testTakeVideoFocusArea");

		setToDefault();

		subTestTakeVideo(false, true, false, false, false);
	}

	public void testTakeVideoQuick() throws InterruptedException {
		Log.d(TAG, "testTakeVideoQuick");

		setToDefault();

		subTestTakeVideo(false, false, false, false, true);
	}

	public void testTakeVideoQuickSAF() throws InterruptedException {
		Log.d(TAG, "testTakeVideoQuickSAF");

		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
			Log.d(TAG, "SAF requires Android Lollipop or better");
			return;
		}

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), true);
		editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "content://com.android.externalstorage.documents/tree/primary%3ADCIM%2FOpenCamera");
		editor.apply();
		updateForSettings();

		subTestTakeVideo(false, false, false, false, true);
	}

	public void testTakeVideoForceFailure() throws InterruptedException {
		Log.d(TAG, "testTakeVideoForceFailure");

		setToDefault();

		mActivity.getPreview().test_video_failure = true;
		subTestTakeVideo(false, false, true, false, false);
	}

	public void testTakeVideo4K() throws InterruptedException {
		Log.d(TAG, "testTakeVideo4K");
		
		if( !mActivity.supportsForceVideo4K() ) {
			return;
		}

		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getForceVideo4KPreferenceKey(), true);
		editor.apply();
		updateForSettings();

		subTestTakeVideo(false, false, true, false, false);
	}

	public void testTakeVideoFPS() throws InterruptedException {
		Log.d(TAG, "testTakeVideoFPS");
		
		setToDefault();
		final String [] fps_values = new String[]{"15", "24", "25", "30", "60"};
		for(int i=0;i<fps_values.length;i++) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getVideoFPSPreferenceKey(), fps_values[i]);
			editor.apply();
			restart(); // should restart to emulate what happens in real app

			Log.d(TAG, "test video with fps: " + fps_values[i]);
			boolean allow_failure = fps_values[i].equals("24") || fps_values[i].equals("25") || fps_values[i].equals("60");
			subTestTakeVideo(false, false, allow_failure, false, false);
		}
	}

	public void testTakeVideoBitrate() throws InterruptedException {
		Log.d(TAG, "testTakeVideoBitrate");
		
		setToDefault();
		final String [] bitrate_values = new String[]{"1000000", "10000000", "20000000", "50000000"};
		//final String [] bitrate_values = new String[]{"1000000", "10000000", "20000000", "30000000"};
		for(int i=0;i<bitrate_values.length;i++) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getVideoBitratePreferenceKey(), bitrate_values[i]);
			editor.apply();
			restart(); // should restart to emulate what happens in real app

			Log.d(TAG, "test video with bitrate: " + bitrate_values[i]);
			boolean allow_failure = bitrate_values[i].equals("30000000") || bitrate_values[i].equals("50000000");
			subTestTakeVideo(false, false, allow_failure, false, false);
		}
	}

	private void subTestTakeVideoMaxDuration(boolean restart, boolean interrupt) throws InterruptedException {
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getVideoMaxDurationPreferenceKey(), "15");
			if( restart ) {
				editor.putString(PreferenceKeys.getVideoRestartPreferenceKey(), "1");
			}
			editor.apply();
		}

		assertTrue(mPreview.isPreviewStarted());

		View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
		if( !mPreview.isVideo() ) {
			clickView(switchVideoButton);
		}
	    assertTrue(mPreview.isVideo());
		assertTrue(mPreview.isPreviewStarted());
		
		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);
		
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    //View flashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.flash);
	    //View focusButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.focus_mode);
	    View exposureButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure);
	    View exposureLockButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.exposure_lock);
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
	    View trashButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.trash);
	    View shareButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.share);
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    // flash and focus etc default visibility tested in another test
	    // but store status to compare with later
	    //int flashVisibility = flashButton.getVisibility();
	    //int focusVisibility = focusButton.getVisibility();
	    int exposureVisibility = exposureButton.getVisibility();
	    int exposureLockVisibility = exposureLockButton.getVisibility();
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take video");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take video");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");

		assertTrue( mPreview.isTakingPhoto() );

		assertTrue(switchCameraButton.getVisibility() == View.GONE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    //assertTrue(flashButton.getVisibility() == flashVisibility);
	    //assertTrue(focusButton.getVisibility() == View.GONE);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == (mPreview.supportsFlash() ? View.VISIBLE : View.GONE)); // popup button only visible when recording video if flash supported
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);

	    Thread.sleep(10000);
		Log.d(TAG, "check still taking video");
		assertTrue( mPreview.isTakingPhoto() );

		int n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);

		if( restart ) {
			if( interrupt ) {
			    Thread.sleep(5100);
			    restart();
				Log.d(TAG, "done restart");
				// now wait, and check we don't crash
			    Thread.sleep(5000);
			    return;
			}
			else {
			    Thread.sleep(10000);
				Log.d(TAG, "check restarted video");
				assertTrue( mPreview.isTakingPhoto() );
				assertTrue( folder.exists() );
				n_new_files = folder.listFiles().length - n_files;
				Log.d(TAG, "n_new_files: " + n_new_files);
				assertTrue(n_new_files == 2);

				Thread.sleep(15000);
			}
		}
		else {
		    Thread.sleep(8000);
		}
		Log.d(TAG, "check stopped taking video");
		assertTrue( !mPreview.isTakingPhoto() );
		
		assertTrue( folder.exists() );
		n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == (restart ? 2 : 1));

		// trash/share only shown when preview is paused after taking a photo

		assertTrue(mPreview.isPreviewStarted()); // check preview restarted
	    assertTrue(switchCameraButton.getVisibility() == View.VISIBLE);
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);
	    //assertTrue(flashButton.getVisibility() == flashVisibility);
	    //assertTrue(focusButton.getVisibility() == focusVisibility);
	    assertTrue(exposureButton.getVisibility() == exposureVisibility);
	    assertTrue(exposureLockButton.getVisibility() == exposureLockVisibility);
	    assertTrue(popupButton.getVisibility() == View.VISIBLE);
	    assertTrue(trashButton.getVisibility() == View.GONE);
	    assertTrue(shareButton.getVisibility() == View.GONE);
	}

	public void testTakeVideoMaxDuration() throws InterruptedException {
		Log.d(TAG, "testTakeVideoMaxDuration");
		
		setToDefault();
		
		subTestTakeVideoMaxDuration(false, false);
	}

	public void testTakeVideoMaxDurationRestart() throws InterruptedException {
		Log.d(TAG, "testTakeVideoMaxDurationRestart");
		
		setToDefault();
		
		subTestTakeVideoMaxDuration(true, false);
	}

	public void testTakeVideoMaxDurationRestartInterrupt() throws InterruptedException {
		Log.d(TAG, "testTakeVideoMaxDurationRestartInterrupt");
		
		setToDefault();
		
		subTestTakeVideoMaxDuration(true, true);
	}

	public void testTakeVideoSettings() throws InterruptedException {
		Log.d(TAG, "testTakeVideoSettings");
		
		setToDefault();
		
		assertTrue(mPreview.isPreviewStarted());

		View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
		if( !mPreview.isVideo() ) {
			clickView(switchVideoButton);
		}
	    assertTrue(mPreview.isVideo());
		assertTrue(mPreview.isPreviewStarted());
		
		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);
		
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take video");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take video");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");

		assertTrue( mPreview.isTakingPhoto() );

	    Thread.sleep(2000);
		Log.d(TAG, "check still taking video");
		assertTrue( mPreview.isTakingPhoto() );

		int n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);

		// now go to settings
	    View settingsButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.settings);
		Log.d(TAG, "about to click settings");
	    clickView(settingsButton);
		Log.d(TAG, "done clicking settings");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue( !mPreview.isTakingPhoto() );

		assertTrue( folder.exists() );
		n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);

		Thread.sleep(500);
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				Log.d(TAG, "on back pressed...");
			    mActivity.onBackPressed();
			}
		});
		// need to wait for UI code to finish before leaving
		this.getInstrumentation().waitForIdleSync();
	    Thread.sleep(500);
		assertTrue( !mPreview.isTakingPhoto() );
		
		Log.d(TAG, "about to click take video");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take video");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");

		assertTrue( mPreview.isTakingPhoto() );

		assertTrue( folder.exists() );
		n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 2);

	}

	/** Switch to macro focus, go to settings, check switched to continuous mode, leave settings, check back in macro mode, then test recording.
	 */
	public void testTakeVideoMacro() throws InterruptedException {
		Log.d(TAG, "testTakeVideoMacro");
	    if( !mPreview.supportsFocus() ) {
	    	return;
	    }
		
		setToDefault();
		
		assertTrue(mPreview.isPreviewStarted());

		View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
		if( !mPreview.isVideo() ) {
			clickView(switchVideoButton);
		}
	    assertTrue(mPreview.isVideo());
		assertTrue(mPreview.isPreviewStarted());

		switchToFocusValue("focus_mode_macro");

		// now go to settings
	    View settingsButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.settings);
		Log.d(TAG, "about to click settings");
	    clickView(settingsButton);
		Log.d(TAG, "done clicking settings");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue( !mPreview.isTakingPhoto() );

		Thread.sleep(500);

		assertTrue(mPreview.getCurrentFocusValue().equals("focus_mode_macro"));

		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				Log.d(TAG, "on back pressed...");
			    mActivity.onBackPressed();
			}
		});
		// need to wait for UI code to finish before leaving
		this.getInstrumentation().waitForIdleSync();
	    Thread.sleep(500);

		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);
		
	    assertTrue(switchVideoButton.getVisibility() == View.VISIBLE);

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take video");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take video");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");

		assertTrue( mPreview.isTakingPhoto() );

	    Thread.sleep(2000);
		Log.d(TAG, "check still taking video");
		assertTrue( mPreview.isTakingPhoto() );

		int n_new_files = folder.listFiles().length - n_files;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == 1);

	}

	public void testTakeVideoFlashVideo() throws InterruptedException {
		Log.d(TAG, "testTakeVideoFlashVideo");

		if( !mPreview.supportsFlash() ) {
			return;
		}
		
		setToDefault();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getVideoFlashPreferenceKey(), true);
		editor.apply();
		updateForSettings();
		
		assertTrue(mPreview.isPreviewStarted());

		View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
		if( !mPreview.isVideo() ) {
			clickView(switchVideoButton);
		}
	    assertTrue(mPreview.isVideo());
		assertTrue(mPreview.isPreviewStarted());
		
	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take video");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take video");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");

		assertTrue( mPreview.isTakingPhoto() );

	    Thread.sleep(1500);
		Log.d(TAG, "check still taking video");
		assertTrue( mPreview.isTakingPhoto() );

		// wait until flash off
		long time_s = System.currentTimeMillis();
		for(;;) {
		    if( !mPreview.getCameraController().getFlashValue().equals("flash_torch") ) {
		    	break;
		    }
		    assertTrue( System.currentTimeMillis() - time_s <= 200 );
		}
		
		// wait until flash on
		time_s = System.currentTimeMillis();
		for(;;) {
		    if( mPreview.getCameraController().getFlashValue().equals("flash_torch") ) {
		    	break;
		    }
		    assertTrue( System.currentTimeMillis() - time_s <= 1100 );
		}

		// wait until flash off
		time_s = System.currentTimeMillis();
		for(;;) {
		    if( !mPreview.getCameraController().getFlashValue().equals("flash_torch") ) {
		    	break;
		    }
		    assertTrue( System.currentTimeMillis() - time_s <= 200 );
		}

		// wait until flash on
		time_s = System.currentTimeMillis();
		for(;;) {
		    if( mPreview.getCameraController().getFlashValue().equals("flash_torch") ) {
		    	break;
		    }
		    assertTrue( System.currentTimeMillis() - time_s <= 1100 );
		}

		Log.d(TAG, "about to click stop video");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking stop video");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		
		// test flash now off
	    assertTrue( !mPreview.getCameraController().getFlashValue().equals("flash_torch") );
	}

	// type: 0 - go to background; 1 - go to settings; 2 - go to popup
	private void subTestTimer(int type) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getTimerPreferenceKey(), "10");
		editor.putBoolean(PreferenceKeys.getTimerBeepPreferenceKey(), false);
		editor.apply();

		assertTrue(!mPreview.isOnTimer());

		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);

		View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");
		assertTrue(mPreview.isOnTimer());
		assertTrue(mPreview.count_cameraTakePicture==0);
		
		try {
			// wait 2s, and check we are still on timer, and not yet taken a photo
			Thread.sleep(2000);
			assertTrue(mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==0);
			// quit and resume
			if( type == 0 )
				restart();
			else if( type == 1 ) {
			    View settingsButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.settings);
				Log.d(TAG, "about to click settings");
			    clickView(settingsButton);
				Log.d(TAG, "done clicking settings");
				this.getInstrumentation().waitForIdleSync();
				Log.d(TAG, "after idle sync");

				mActivity.runOnUiThread(new Runnable() {
					public void run() {
						Log.d(TAG, "on back pressed...");
					    mActivity.onBackPressed();
					}
				});
				// need to wait for UI code to finish before leaving
				this.getInstrumentation().waitForIdleSync();
			    Thread.sleep(500);
			}
			else {
			    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
			    clickView(popupButton);
			    while( !mActivity.popupIsOpen() ) {
			    }
			}
			takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		    // check timer cancelled, and not yet taken a photo
			assertTrue(!mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==0);
			int n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 0);

			// start timer again
			Log.d(TAG, "about to click take photo");
			assertTrue(mPreview.getCameraController() != null);
		    clickView(takePhotoButton);
			assertTrue(mPreview.getCameraController() != null);
			Log.d(TAG, "done clicking take photo");
			assertTrue(mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==0);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 0);
			
			// wait 15s, and ensure we took a photo
			Thread.sleep(15000);
			Log.d(TAG, "waited, count now " + mPreview.count_cameraTakePicture);
			assertTrue(!mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==1);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 1);
			
			// now set timer to 5s, and turn on pause_preview
			editor.putString(PreferenceKeys.getTimerPreferenceKey(), "5");
			editor.putBoolean(PreferenceKeys.getPausePreviewPreferenceKey(), true);
			editor.apply();

			Log.d(TAG, "about to click take photo");
			assertTrue(mPreview.getCameraController() != null);
		    clickView(takePhotoButton);
			assertTrue(mPreview.getCameraController() != null);
			Log.d(TAG, "done clicking take photo");
			assertTrue(mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==1);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 1);

			// wait 10s, and ensure we took a photo
			Thread.sleep(10000);
			Log.d(TAG, "waited, count now " + mPreview.count_cameraTakePicture);
			assertTrue(!mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==2);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 2);
			
			// now test cancelling
			Log.d(TAG, "about to click take photo");
			assertTrue(mPreview.getCameraController() != null);
		    clickView(takePhotoButton);
			assertTrue(mPreview.getCameraController() != null);
			Log.d(TAG, "done clicking take photo");
			assertTrue(mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==2);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 2);

			// wait 2s, and cancel
			Thread.sleep(2000);
			Log.d(TAG, "about to click take photo to cance");
			assertTrue(mPreview.getCameraController() != null);
		    clickView(takePhotoButton);
			assertTrue(mPreview.getCameraController() != null);
			Log.d(TAG, "done clicking take photo to cancel");
			assertTrue(!mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==2);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 2);

			// wait 8s, and ensure we didn't take a photo
			Thread.sleep(8000);
			Log.d(TAG, "waited, count now " + mPreview.count_cameraTakePicture);
			assertTrue(!mPreview.isOnTimer());
			assertTrue(mPreview.count_cameraTakePicture==2);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 2);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/* Test with 10s timer, start a photo, go to background, then back, then take another photo. We should only take 1 photo - the original countdown should not be active (nor should we crash)!
	 */
	public void testTimerBackground() {
		Log.d(TAG, "testTimerBackground");
		setToDefault();
		
		subTestTimer(0);
	}
	
	/* Test and going to settings.
	 */
	public void testTimerSettings() {
		Log.d(TAG, "testTimerSettings");
		setToDefault();
		
		subTestTimer(1);
	}
	
	/* Test and going to popup.
	 */
	public void testTimerPopup() {
		Log.d(TAG, "testTimerPopup");
		setToDefault();
		
		subTestTimer(2);
	}
	
	/* Takes video on a timer, but interrupts with restart.
	 */
	public void testVideoTimerInterrupt() {
		Log.d(TAG, "testVideoTimerInterrupt");
		setToDefault();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getTimerPreferenceKey(), "5");
		editor.putBoolean(PreferenceKeys.getTimerBeepPreferenceKey(), false);
		editor.apply();

		assertTrue(!mPreview.isOnTimer());

		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);

	    View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
	    clickView(switchVideoButton);
	    assertTrue(mPreview.isVideo());

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");
		assertTrue(mPreview.isOnTimer());
		assertTrue(mPreview.count_cameraTakePicture==0);
		
		try {
			// wait a moment after 5s, then restart
			Thread.sleep(5100);
			assertTrue(mPreview.count_cameraTakePicture==0);
			// quit and resume
			restart();
			Log.d(TAG, "done restart");

		    // check timer cancelled; may or may not have managed to take a photo
			assertTrue(!mPreview.isOnTimer());
		}
		catch(InterruptedException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	/* Tests to do with video and popup menu.
	 */
	private void subTestVideoPopup(boolean on_timer) {
		Log.d(TAG, "subTestVideoPopup");

		assertTrue(!mPreview.isOnTimer());
		assertTrue(!mActivity.popupIsOpen());
	    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);

	    if( !mPreview.isVideo() ) {
			View switchVideoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_video);
		    clickView(switchVideoButton);
		    assertTrue(mPreview.isVideo());
	    }

	    if( !on_timer ) {
	    	// open popup now
		    clickView(popupButton);
		    while( !mActivity.popupIsOpen() ) {
		    }
	    }
	    
	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");
		if( on_timer ) {
			assertTrue(mPreview.isOnTimer());
		}
		
		try {
			if( on_timer ) {
				Thread.sleep(2000);
	
				// now open popup
			    clickView(popupButton);
			    while( !mActivity.popupIsOpen() ) {
			    }

			    // check timer is cancelled
				assertTrue( !mPreview.isOnTimer() );

				// wait for timer (if it was still going)
				Thread.sleep(4000);

				// now check we still aren't recording, and that popup is still open
				assertTrue( mPreview.isVideo() );
				assertTrue( !mPreview.isTakingPhoto() );
				assertTrue( !mPreview.isOnTimer() );
				assertTrue( mActivity.popupIsOpen() );
			}
			else {
				Thread.sleep(1000);

				// now check we are recording video, and that popup is closed
				assertTrue( mPreview.isVideo() );
				assertTrue( mPreview.isTakingPhoto() );
				assertTrue( !mActivity.popupIsOpen() );
			}

			if( !on_timer ) {
				// (if on timer, the video will have stopped)
				List<String> supported_flash_values = mPreview.getSupportedFlashValues();
				if( supported_flash_values == null ) {
					// button shouldn't show at all
					assertTrue( popupButton.getVisibility() == View.GONE );
				}
				else {
					// now open popup again
				    clickView(popupButton);
				    while( !mActivity.popupIsOpen() ) {
				    }
					subTestPopupButtonAvailability("TEST_FLASH", "flash_off", supported_flash_values);
					subTestPopupButtonAvailability("TEST_FLASH", "flash_auto", supported_flash_values);
					subTestPopupButtonAvailability("TEST_FLASH", "flash_on", supported_flash_values);
					subTestPopupButtonAvailability("TEST_FLASH", "flash_torch", supported_flash_values);
					subTestPopupButtonAvailability("TEST_FLASH", "flash_red_eye", supported_flash_values);
					// only flash should be available
					subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_auto", null);
					subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_locked", null);
					subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_infinity", null);
					subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_macro", null);
					subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_fixed", null);
					subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_edof", null);
					subTestPopupButtonAvailability("TEST_FOCUS", "focus_mode_continuous_video", null);
					subTestPopupButtonAvailability("TEST_ISO", "auto", null);
					subTestPopupButtonAvailability("TEST_ISO", "100", null);
					subTestPopupButtonAvailability("TEST_ISO", "200", null);
					subTestPopupButtonAvailability("TEST_ISO", "400", null);
					subTestPopupButtonAvailability("TEST_ISO", "800", null);
					subTestPopupButtonAvailability("TEST_ISO", "1600", null);
					subTestPopupButtonAvailability("TEST_WHITE_BALANCE", false);
					subTestPopupButtonAvailability("TEST_SCENE_MODE", false);
					subTestPopupButtonAvailability("TEST_COLOR_EFFECT", false);
				}
			}

			Log.d(TAG, "now stop video");
		    clickView(takePhotoButton);
			Log.d(TAG, "done clicking stop video");
			this.getInstrumentation().waitForIdleSync();
			Log.d(TAG, "after idle sync");
			assertTrue( !mPreview.isTakingPhoto() );
			assertTrue( !mActivity.popupIsOpen() );

		}
		catch(InterruptedException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		// now open popup again
	    clickView(popupButton);
	    while( !mActivity.popupIsOpen() ) {
	    }
	    subTestPopupButtonAvailability();
	}
	
	/* Tests that popup menu closes when we record video; then tests behaviour of popup.
	 */
	public void testVideoPopup() {
		Log.d(TAG, "testVideoPopup");
		setToDefault();

		subTestVideoPopup(false);

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			Log.d(TAG, "switch camera");
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);
			subTestVideoPopup(false);
	    }
	}

	/* Takes video on a timer, but checks that the popup menu stops video timer; then tests behaviour of popup.
	 */
	public void testVideoTimerPopup() {
		Log.d(TAG, "testVideoTimerPopup");
		setToDefault();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getTimerPreferenceKey(), "5");
		editor.putBoolean(PreferenceKeys.getTimerBeepPreferenceKey(), false);
		editor.apply();
		
		subTestVideoPopup(true);

		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			Log.d(TAG, "switch camera");
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);
			subTestVideoPopup(true);
	    }
	}
	
	/* Tests taking photos repeatedly with auto-stabilise enabled.
	 */
	public void testTakePhotoBurst() {
		Log.d(TAG, "testTakePhotoBurst");
		setToDefault();

		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getBurstModePreferenceKey(), "3");
			editor.apply();
		}

		// count initial files in folder
		File folder = mActivity.getImageFolder();
		int n_files = folder.listFiles().length;
		Log.d(TAG, "n_files at start: " + n_files);

		assertTrue(mPreview.count_cameraTakePicture==0);

		View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");
		assertTrue(!mPreview.isOnTimer());

		try {
			// wait 6s, and test that we've taken the photos by then
			Thread.sleep(6000);
		    assertTrue(mPreview.isPreviewStarted()); // check preview restarted
			Log.d(TAG, "count_cameraTakePicture: " + mPreview.count_cameraTakePicture);
			assertTrue(mPreview.count_cameraTakePicture==3);
			int n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 3);

			// now test pausing and resuming
		    clickView(takePhotoButton);
		    pauseAndResume();
			// wait 5s, and test that we haven't taken any photos
			Thread.sleep(5000);
		    assertTrue(mPreview.isPreviewStarted()); // check preview restarted
			assertTrue(mPreview.count_cameraTakePicture==3);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 3);

			// test with preview paused
			{
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(PreferenceKeys.getPausePreviewPreferenceKey(), true);
				editor.apply();
			}
		    clickView(takePhotoButton);
			Thread.sleep(6000);
			assertTrue(mPreview.count_cameraTakePicture==6);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 6);
			assertTrue(!mPreview.isPreviewStarted()); // check preview paused

		    TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
			this.getInstrumentation().waitForIdleSync();
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 6);
		    assertTrue(mPreview.isPreviewStarted()); // check preview restarted
			{
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(PreferenceKeys.getPausePreviewPreferenceKey(), false);
				editor.apply();
			}

			// now test burst interval
			{
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(PreferenceKeys.getBurstModePreferenceKey(), "2");
				editor.putString(PreferenceKeys.getBurstIntervalPreferenceKey(), "3");
				editor.putBoolean(PreferenceKeys.getTimerBeepPreferenceKey(), false);
				editor.apply();
			}
		    clickView(takePhotoButton);
		    while( mPreview.isTakingPhoto() ) {
		    }
			Log.d(TAG, "done taking 1st photo");
			this.getInstrumentation().waitForIdleSync();
			assertTrue(mPreview.count_cameraTakePicture==7);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 7);
			// wait 2s, should still not have taken another photo
			Thread.sleep(2000);
			assertTrue(mPreview.count_cameraTakePicture==7);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 7);
			// wait another 5s, should have taken another photo (need to allow time for the extra auto-focus)
			Thread.sleep(5000);
			assertTrue(mPreview.count_cameraTakePicture==8);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 8);
			// wait 4s, should not have taken any more photos
			Thread.sleep(4000);
			assertTrue(mPreview.count_cameraTakePicture==8);
			n_new_files = folder.listFiles().length - n_files;
			Log.d(TAG, "n_new_files: " + n_new_files);
			assertTrue(n_new_files == 8);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	/* Tests that saving quality (i.e., resolution) settings can be done per-camera. Also checks that the supported picture sizes is as expected.
	 */
	public void testSaveQuality() {
		Log.d(TAG, "testSaveQuality");

		if( mPreview.getCameraControllerManager().getNumberOfCameras() == 0 ) {
			return;
		}
		setToDefault();

	    List<CameraController.Size> preview_sizes = mPreview.getSupportedPictureSizes();

	    // change back camera to the last size
		CameraController.Size size = preview_sizes.get(preview_sizes.size()-1);
	    {
		    Log.d(TAG, "set size to " + size.width + " x " + size.height);
		    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getResolutionPreferenceKey(mPreview.getCameraId()), size.width + " " + size.height);
			editor.apply();
	    }
		
		// need to resume activity for it to take effect (for camera to be reopened)
	    pauseAndResume();
	    CameraController.Size new_size = mPreview.getCameraController().getPictureSize();
	    Log.d(TAG, "size is now " + new_size.width + " x " + new_size.height);
	    assertTrue(size.equals(new_size));

	    // switch camera to front
		int cameraId = mPreview.getCameraId();
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    clickView(switchCameraButton);
		int new_cameraId = mPreview.getCameraId();
		assertTrue(cameraId != new_cameraId);

	    List<CameraController.Size> front_preview_sizes = mPreview.getSupportedPictureSizes();

	    // change front camera to the last size
		CameraController.Size front_size = front_preview_sizes.get(front_preview_sizes.size()-1);
	    {
		    Log.d(TAG, "set front_size to " + front_size.width + " x " + front_size.height);
		    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getResolutionPreferenceKey(mPreview.getCameraId()), front_size.width + " " + front_size.height);
			editor.apply();
	    }
		
		// need to resume activity for it to take effect (for camera to be reopened)
	    pauseAndResume();
	    // check still on front camera
	    Log.d(TAG, "camera id " + mPreview.getCameraId());
		assertTrue(mPreview.getCameraId() == new_cameraId);
	    CameraController.Size front_new_size = mPreview.getCameraController().getPictureSize();
	    Log.d(TAG, "front size is now " + front_new_size.width + " x " + front_new_size.height);
	    assertTrue(front_size.equals(front_new_size));

	    // change front camera to the first size
		front_size = front_preview_sizes.get(0);
	    {
		    Log.d(TAG, "set front_size to " + front_size.width + " x " + front_size.height);
		    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getResolutionPreferenceKey(mPreview.getCameraId()), front_size.width + " " + front_size.height);
			editor.apply();
	    }
		
		// need to resume activity for it to take effect (for camera to be reopened)
	    pauseAndResume();
	    front_new_size = mPreview.getCameraController().getPictureSize();
	    Log.d(TAG, "front size is now " + front_new_size.width + " x " + front_new_size.height);
	    assertTrue(front_size.equals(front_new_size));

	    // switch camera to back
	    clickView(switchCameraButton);
		new_cameraId = mPreview.getCameraId();
		assertTrue(cameraId == new_cameraId);
		
		// now back camera size should still be what it was
	    {
		    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			String settings_size = settings.getString(PreferenceKeys.getResolutionPreferenceKey(mPreview.getCameraId()), "");
		    Log.d(TAG, "settings key is " + PreferenceKeys.getResolutionPreferenceKey(mPreview.getCameraId()));
		    Log.d(TAG, "settings size is " + settings_size);
	    }
	    new_size = mPreview.getCameraController().getPictureSize();
	    Log.d(TAG, "size is now " + new_size.width + " x " + new_size.height);
	    assertTrue(size.equals(new_size));
	}

	private void testExif(String file, boolean expect_gps) throws IOException {
		//final String TAG_GPS_IMG_DIRECTION = "GPSImgDirection";
		//final String TAG_GPS_IMG_DIRECTION_REF = "GPSImgDirectionRef";
		ExifInterface exif = new ExifInterface(file);
		assertTrue(exif.getAttribute(ExifInterface.TAG_ORIENTATION) != null);
		assertTrue(exif.getAttribute(ExifInterface.TAG_MAKE) != null);
		assertTrue(exif.getAttribute(ExifInterface.TAG_MODEL) != null);
		if( expect_gps ) {
			assertTrue(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null);
			assertTrue(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) != null);
			assertTrue(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null);
			assertTrue(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) != null);
			// can't read custom tags, even though we can write them?!
			//assertTrue(exif.getAttribute(TAG_GPS_IMG_DIRECTION) != null);
			//assertTrue(exif.getAttribute(TAG_GPS_IMG_DIRECTION_REF) != null);
		}
		else {
			assertTrue(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) == null);
			assertTrue(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) == null);
			assertTrue(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) == null);
			assertTrue(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF) == null);
			// can't read custom tags, even though we can write them?!
			//assertTrue(exif.getAttribute(TAG_GPS_IMG_DIRECTION) == null);
			//assertTrue(exif.getAttribute(TAG_GPS_IMG_DIRECTION_REF) == null);
		}
	}

	private void subTestLocationOn(boolean gps_direction) throws IOException {
		setToDefault();

		assertTrue(!mActivity.getLocationSupplier().hasLocationListeners());
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PreferenceKeys.getLocationPreferenceKey(), true);
			if( gps_direction ) {
				editor.putBoolean(PreferenceKeys.getGPSDirectionPreferenceKey(), true);
			}
			editor.apply();
			updateForSettings();
		}

		assertTrue(mActivity.getLocationSupplier().hasLocationListeners());
		Log.d(TAG, "wait until received location");
		while( !mActivity.getLocationSupplier().testHasReceivedLocation() ) {
		}
		Log.d(TAG, "have received location");
		this.getInstrumentation().waitForIdleSync();
	    assertTrue(mActivity.getLocationSupplier().getLocation() != null);
	    assertTrue(mPreview.count_cameraTakePicture==0);

		View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		mActivity.test_last_saved_image = null;
	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		assertTrue(mPreview.count_cameraTakePicture==1);
		assertTrue(mActivity.test_last_saved_image != null);
		testExif(mActivity.test_last_saved_image, true);

		// now test with auto-stabilise
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), true);
			editor.apply();
		}
		mActivity.test_last_saved_image = null;
	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		assertTrue(mPreview.count_cameraTakePicture==2);
		assertTrue(mActivity.test_last_saved_image != null);
		testExif(mActivity.test_last_saved_image, true);

		// switch to front camera
		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);
			assertTrue(mActivity.getLocationSupplier().hasLocationListeners());
			// shouldn't need to wait for test_has_received_location to be true, as should remember from before switching camera
		    assertTrue(mActivity.getLocationSupplier().getLocation() != null);
		}
	}

	/* Tests we save location data; also tests that we save other exif data.
	 */
	public void testLocationOn() throws IOException {
		Log.d(TAG, "testLocationOn");
		subTestLocationOn(false);
	}

	/* Tests we save location and gps direction.
	 */
	public void testLocationDirectionOn() throws IOException {
		Log.d(TAG, "testLocationDirectionOn");
		subTestLocationOn(true);
	}

	/* Tests we don't save location data; also tests that we save other exif data.
	 */
	private void subTestLocationOff(boolean gps_direction) throws IOException {
		setToDefault();

		if( gps_direction ) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PreferenceKeys.getGPSDirectionPreferenceKey(), true);
			editor.apply();
			updateForSettings();
		}
		this.getInstrumentation().waitForIdleSync();
		assertTrue(!mActivity.getLocationSupplier().hasLocationListeners());
	    assertTrue(mActivity.getLocationSupplier().getLocation() == null);
	    assertTrue(mPreview.count_cameraTakePicture==0);

		View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		mActivity.test_last_saved_image = null;
	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		assertTrue(mPreview.count_cameraTakePicture==1);
		assertTrue(mActivity.test_last_saved_image != null);
		testExif(mActivity.test_last_saved_image, false);

		// now test with auto-stabilise
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), true);
			editor.apply();
		}
		mActivity.test_last_saved_image = null;
	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		assertTrue(mPreview.count_cameraTakePicture==2);
		assertTrue(mActivity.test_last_saved_image != null);
		testExif(mActivity.test_last_saved_image, false);

		// switch to front camera
		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);
			this.getInstrumentation().waitForIdleSync();
		    assertTrue(mActivity.getLocationSupplier().getLocation() == null);

		    clickView(switchCameraButton);
			this.getInstrumentation().waitForIdleSync();
		    assertTrue(mActivity.getLocationSupplier().getLocation() == null);
		}

		// now switch location back on
		Log.d(TAG, "now switch location back on");
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PreferenceKeys.getLocationPreferenceKey(), true);
			editor.apply();
			restart(); // need to restart for this preference to take effect
		}

		while( !mActivity.getLocationSupplier().testHasReceivedLocation() ) {
		}
		this.getInstrumentation().waitForIdleSync();
	    assertTrue(mActivity.getLocationSupplier().getLocation() != null);

		// switch to front camera
		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);
			// shouldn't need to wait for test_has_received_location to be true, as should remember from before switching camera
		    assertTrue(mActivity.getLocationSupplier().getLocation() != null);
		}
	}

	/* Tests we don't save location data; also tests that we save other exif data.
	 */
	public void testLocationOff() throws IOException {
		Log.d(TAG, "testLocationOff");
		subTestLocationOff(false);
	}

	/* Tests we save gps direction.
	 */
	public void testDirectionOn() throws IOException {
		Log.d(TAG, "testDirectionOn");
		subTestLocationOff(false);
	}

	/* Tests we can stamp date/time and location to photo.
	 */
	public void testPhotoStamp() throws IOException {
		Log.d(TAG, "testPhotoStamp");

		setToDefault();

		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getStampPreferenceKey(), "preference_stamp_yes");
			editor.apply();
			updateForSettings();
		}

	    assertTrue(mPreview.count_cameraTakePicture==0);

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "photo count: " + mPreview.count_cameraTakePicture);
		assertTrue(mPreview.count_cameraTakePicture==1);

		// now again with location
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PreferenceKeys.getLocationPreferenceKey(), true);
			editor.apply();
			updateForSettings();
		}

		assertTrue( mActivity.getLocationSupplier().hasLocationListeners() );
		while( !mActivity.getLocationSupplier().testHasReceivedLocation() ) {
		}
		this.getInstrumentation().waitForIdleSync();
	    assertTrue(mActivity.getLocationSupplier().getLocation() != null);

	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "photo count: " + mPreview.count_cameraTakePicture);
		assertTrue(mPreview.count_cameraTakePicture==2);

		// now again with custom text
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getTextStampPreferenceKey(), "Test stamp!�$");
			editor.apply();
			updateForSettings();
		}

		assertTrue( mActivity.getLocationSupplier().hasLocationListeners() );
		while( !mActivity.getLocationSupplier().testHasReceivedLocation() ) {
		}
		this.getInstrumentation().waitForIdleSync();
	    assertTrue(mActivity.getLocationSupplier().getLocation() != null);

	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "photo count: " + mPreview.count_cameraTakePicture);
		assertTrue(mPreview.count_cameraTakePicture==3);

		// now test with auto-stabilise
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), true);
			editor.apply();
		}

	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "photo count: " + mPreview.count_cameraTakePicture);
		assertTrue(mPreview.count_cameraTakePicture==4);

	}

	/* Tests we can stamp custom text to photo.
	 */
	public void testCustomTextStamp() throws IOException {
		Log.d(TAG, "testCustomTextStamp");

		setToDefault();

		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getTextStampPreferenceKey(), "Test stamp!�$");
			editor.apply();
			updateForSettings();
		}

	    assertTrue(mPreview.count_cameraTakePicture==0);

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "photo count: " + mPreview.count_cameraTakePicture);
		assertTrue(mPreview.count_cameraTakePicture==1);

		// now test with auto-stabilise
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), true);
			editor.apply();
		}

	    clickView(takePhotoButton);

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "photo count: " + mPreview.count_cameraTakePicture);
		assertTrue(mPreview.count_cameraTakePicture==2);

	}

	/* Tests zoom.
	 */
	public void testZoom() {
		Log.d(TAG, "testZoom");
		setToDefault();

	    if( !mPreview.supportsZoom() ) {
			Log.d(TAG, "zoom not supported");
	    	return;
	    }

	    final ZoomControls zoomControls = (ZoomControls) mActivity.findViewById(net.sourceforge.opencamera.R.id.zoom);
		assertTrue(zoomControls.getVisibility() == View.INVISIBLE);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PreferenceKeys.getShowZoomControlsPreferenceKey(), true);
		editor.apply();
		updateForSettings();

		assertTrue(zoomControls.getVisibility() == View.VISIBLE);
	    final SeekBar zoomSeekBar = (SeekBar) mActivity.findViewById(net.sourceforge.opencamera.R.id.zoom_seekbar);
		assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
		int max_zoom = mPreview.getMaxZoom();
		assertTrue(zoomSeekBar.getMax() == max_zoom);
		Log.d(TAG, "zoomSeekBar progress = " + zoomSeekBar.getProgress());
		Log.d(TAG, "actual zoom = " + mPreview.getCameraController().getZoom());
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());

	    if( mPreview.supportsFocus() ) {
			assertTrue(!mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

			// touch to auto-focus with focus area
			TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
			assertTrue(mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
		    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
	    }

	    int zoom = mPreview.getCameraController().getZoom();

	    // use buttons to zoom
		Log.d(TAG, "zoom in");
	    mActivity.zoomIn();
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to zoom " + zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == zoom+1);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());
	    if( mPreview.supportsFocus() ) {
	    	// check that focus areas cleared
			assertTrue(!mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

			// touch to auto-focus with focus area
			TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
			assertTrue(mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
		    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
	    }

		Log.d(TAG, "zoom out");
		mActivity.zoomOut();
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to zoom " + zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());
	    if( mPreview.supportsFocus() ) {
	    	// check that focus areas cleared
			assertTrue(!mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);

			// touch to auto-focus with focus area
			TouchUtils.clickView(MainActivityTest.this, mPreview.getView());
			assertTrue(mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() != null);
		    assertTrue(mPreview.getCameraController().getFocusAreas().size() == 1);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() != null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas().size() == 1);
	    }

	    // now test multitouch zoom
	    mPreview.scaleZoom(2.0f);
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to zoom " + zoom);
	    assertTrue(mPreview.getCameraController().getZoom() > zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());

	    mPreview.scaleZoom(0.5f);
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to zoom " + zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());

		// test to max/min
	    mPreview.scaleZoom(10000.0f);
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to max_zoom " + max_zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == max_zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());
		
	    mPreview.scaleZoom(1.0f/10000.0f);
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to zero");
	    assertTrue(mPreview.getCameraController().getZoom() == 0);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());

		// use seekbar to zoom
		Log.d(TAG, "zoom to max");
		Log.d(TAG, "progress was: " + zoomSeekBar.getProgress());
	    zoomSeekBar.setProgress(0);
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to max_zoom " + max_zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == max_zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());
	    if( mPreview.supportsFocus() ) {
	    	// check that focus areas cleared
			assertTrue(!mPreview.hasFocusArea());
		    assertTrue(mPreview.getCameraController().getFocusAreas() == null);
		    assertTrue(mPreview.getCameraController().getMeteringAreas() == null);
	    }
	}

	public void testZoomIdle() {
		Log.d(TAG, "testZoomIdle");
		setToDefault();

	    if( !mPreview.supportsZoom() ) {
			Log.d(TAG, "zoom not supported");
	    	return;
	    }

	    final SeekBar zoomSeekBar = (SeekBar) mActivity.findViewById(net.sourceforge.opencamera.R.id.zoom_seekbar);
		assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    int max_zoom = mPreview.getMaxZoom();
	    zoomSeekBar.setProgress(0);
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to zoom " + max_zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == max_zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());

		pauseAndResume();
	    Log.d(TAG, "after pause and resume: compare actual zoom " + mPreview.getCameraController().getZoom() + " to zoom " + max_zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == max_zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());
	}

	public void testZoomSwitchCamera() {
		Log.d(TAG, "testZoomSwitchCamera");
		setToDefault();

	    if( !mPreview.supportsZoom() ) {
			Log.d(TAG, "zoom not supported");
	    	return;
	    }
	    else if( mPreview.getCameraControllerManager().getNumberOfCameras() == 0 ) {
			return;
		}

	    final SeekBar zoomSeekBar = (SeekBar) mActivity.findViewById(net.sourceforge.opencamera.R.id.zoom_seekbar);
		assertTrue(zoomSeekBar.getVisibility() == View.VISIBLE);
	    int max_zoom = mPreview.getMaxZoom();
	    zoomSeekBar.setProgress(0);
		this.getInstrumentation().waitForIdleSync();
	    Log.d(TAG, "compare actual zoom " + mPreview.getCameraController().getZoom() + " to zoom " + max_zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == max_zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());

	    int cameraId = mPreview.getCameraId();
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    clickView(switchCameraButton);
		int new_cameraId = mPreview.getCameraId();
		assertTrue(cameraId != new_cameraId);

	    max_zoom = mPreview.getMaxZoom();
	    Log.d(TAG, "after pause and resume: compare actual zoom " + mPreview.getCameraController().getZoom() + " to zoom " + max_zoom);
	    assertTrue(mPreview.getCameraController().getZoom() == max_zoom);
		assertTrue(max_zoom-zoomSeekBar.getProgress() == mPreview.getCameraController().getZoom());
	}

	public void testSwitchCameraIdle() {
		Log.d(TAG, "testSwitchCameraIdle");
		setToDefault();

		if( mPreview.getCameraControllerManager().getNumberOfCameras() == 0 ) {
			return;
		}

	    int cameraId = mPreview.getCameraId();
	    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
	    clickView(switchCameraButton);
		int new_cameraId = mPreview.getCameraId();
		assertTrue(cameraId != new_cameraId);

		pauseAndResume();

	    int new2_cameraId = mPreview.getCameraId();
		assertTrue(new2_cameraId == new_cameraId);

	}

	/* Tests going to gallery.
	 */
	public void testGallery() {
		Log.d(TAG, "testGallery");
		setToDefault();

	    View galleryButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.gallery);
	    clickView(galleryButton);
	    
	}

	/* Tests going to settings.
	 */
	public void testSettings() {
		Log.d(TAG, "testSettings");
		setToDefault();

	    View settingsButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.settings);
	    clickView(settingsButton);
	    
	}

	private void subTestCreateSaveFolder(boolean use_saf, String save_folder, boolean delete_folder) {
		setToDefault();

		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			if( use_saf ) {
				editor.putBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), true);
				editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), save_folder);
			}
			else {
				editor.putString(PreferenceKeys.getSaveLocationPreferenceKey(), save_folder);
			}
			editor.apply();
			updateForSettings();
		}
		if( !use_saf ) {
			ArrayList<String> save_location_history = mActivity.getSaveLocationHistory();
			assertTrue(save_location_history.size() > 0);
			assertTrue(save_location_history.contains(save_folder));
			assertTrue(save_location_history.get( save_location_history.size()-1 ).equals(save_folder));
		}

		File folder = mActivity.getImageFolder();
		if( folder.exists() && delete_folder ) {
			assertTrue(folder.isDirectory());
			// delete folder - need to delete contents first
			if( folder.isDirectory() ) {
		        String [] children = folder.list();
		        for (int i = 0; i < children.length; i++) {
		            File file = new File(folder, children[i]);
		            file.delete();
		        	MediaScannerConnection.scanFile(mActivity, new String[] { file.getAbsolutePath() }, null, null);
		        }
			}
			folder.delete();
		}
		int n_old_files = 0;
		if( folder.exists() ) {
			n_old_files = folder.listFiles().length;
		}
		Log.d(TAG, "n_old_files: " + n_old_files);

	    View takePhotoButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.take_photo);
		Log.d(TAG, "about to click take photo");
	    clickView(takePhotoButton);
		Log.d(TAG, "done clicking take photo");

		Log.d(TAG, "wait until finished taking photo");
	    while( mPreview.isTakingPhoto() ) {
	    }
		Log.d(TAG, "done taking photo");
		this.getInstrumentation().waitForIdleSync();
		Log.d(TAG, "after idle sync");
		assertTrue(mPreview.count_cameraTakePicture==1);

		assertTrue( folder.exists() );
		int n_new_files = folder.listFiles().length;
		Log.d(TAG, "n_new_files: " + n_new_files);
		assertTrue(n_new_files == n_old_files+1);

		// change back to default, so as to not be annoying
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			if( use_saf ) {
				editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "content://com.android.externalstorage.documents/tree/primary%3ADCIM%2FOpenCamera");
			}
			else {
				editor.putString(PreferenceKeys.getSaveLocationPreferenceKey(), "OpenCamera");
			}
			editor.apply();
		}
	}

	/** Tests taking a photo with a new save folder.
	 */
	public void testCreateSaveFolder1() {
		Log.d(TAG, "testCreateSaveFolder1");
		subTestCreateSaveFolder(false, "OpenCameraTest", true);
	}

	/** Tests taking a photo with a new save folder.
	 */
	public void testCreateSaveFolder2() {
		Log.d(TAG, "testCreateSaveFolder2");
		subTestCreateSaveFolder(false, "OpenCameraTest/", true);
	}

	/** Tests taking a photo with a new save folder.
	 */
	public void testCreateSaveFolder3() {
		Log.d(TAG, "testCreateSaveFolder3");
		subTestCreateSaveFolder(false, "OpenCameraTest_a/OpenCameraTest_b", true);
	}

	/** Tests taking a photo with a new save folder.
	 */
	@SuppressLint("SdCardPath")
	public void testCreateSaveFolder4() {
		Log.d(TAG, "testCreateSaveFolder4");
		subTestCreateSaveFolder(false, "/sdcard/Pictures/OpenCameraTest", true);
	}

	/** Tests taking a photo with a new save folder.
	 */
	public void testCreateSaveFolderUnicode() {
		Log.d(TAG, "testCreateSaveFolderUnicode");
		subTestCreateSaveFolder(false, "�����!�$%^&()", true);
	}

	/** Tests taking a photo with a new save folder.
	 */
	public void testCreateSaveFolderEmpty() {
		Log.d(TAG, "testCreateSaveFolderEmpty");
		subTestCreateSaveFolder(false, "", false);
	}

	/** Tests taking a photo with a new save folder.
	 */
	public void testCreateSaveFolderSAF() {
		Log.d(TAG, "testCreateSaveFolderSAF");

		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
			Log.d(TAG, "SAF requires Android Lollipop or better");
			return;
		}

		subTestCreateSaveFolder(true, "content://com.android.externalstorage.documents/tree/primary%3ADCIM", true);
	}

	/** Tests launching the folder chooser on a new folder.
	 */
	public void testFolderChooserNew() throws InterruptedException {
		setToDefault();

		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getSaveLocationPreferenceKey(), "OpenCameraTest");
			editor.apply();
			updateForSettings();
		}

		File folder = mActivity.getImageFolder();
		if( folder.exists() ) {
			assertTrue(folder.isDirectory());
			// delete folder - need to delete contents first
			if( folder.isDirectory() ) {
		        String [] children = folder.list();
		        for (int i = 0; i < children.length; i++) {
		            File file = new File(folder, children[i]);
		            file.delete();
		        	MediaScannerConnection.scanFile(mActivity, new String[] { file.getAbsolutePath() }, null, null);
		        }
			}
			folder.delete();
		}

		FolderChooserDialog fragment = new FolderChooserDialog();
		fragment.show(mActivity.getFragmentManager(), "FOLDER_FRAGMENT");
		Thread.sleep(1000); // wait until folderchooser started up
		Log.d(TAG, "started folderchooser");
		assertTrue(fragment.getCurrentFolder() != null);
		assertTrue(fragment.getCurrentFolder().equals(folder));
		assertTrue(folder.exists());
	}

	/** Tests launching the folder chooser on a folder we don't have access to.
	 * (Shouldn't be possible to get into this state, but just in case.)
	 */
	public void testFolderChooserInvalid() throws InterruptedException {
		setToDefault();

		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getSaveLocationPreferenceKey(), "/OpenCameraTest");
			editor.apply();
			updateForSettings();
		}

		FolderChooserDialog fragment = new FolderChooserDialog();
		fragment.show(mActivity.getFragmentManager(), "FOLDER_FRAGMENT");
		Thread.sleep(1000); // wait until folderchooser started up
		Log.d(TAG, "started folderchooser");
		assertTrue(fragment.getCurrentFolder() != null);
		assertTrue(fragment.getCurrentFolder().exists());
	}

	public void testSaveFolderHistory() {
		setToDefault();

		// clearFolderHistory has code that must be run on UI thread
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				Log.d(TAG, "clearFolderHistory");
    			mActivity.clearFolderHistory();
			}
		});
		// need to wait for UI code to finish before leaving
		this.getInstrumentation().waitForIdleSync();
		ArrayList<String> save_location_history = mActivity.getSaveLocationHistory();
		Log.d(TAG, "save_location_history size: " + save_location_history.size());
		assertTrue(save_location_history.size() == 1);
		String current_folder = null;
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			current_folder = settings.getString(PreferenceKeys.getSaveLocationPreferenceKey(), "OpenCamera");
			Log.d(TAG, "current_folder: " + current_folder);
			Log.d(TAG, "save_location_history entry: " + save_location_history.get(0));
			assertTrue(save_location_history.get(0).equals(current_folder));
		}
		
		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getSaveLocationPreferenceKey(), "new_folder_history_entry");
			editor.apply();
			updateForSettings();
		}
		save_location_history = mActivity.getSaveLocationHistory();
		Log.d(TAG, "save_location_history size: " + save_location_history.size());
		for(int i=0;i<save_location_history.size();i++) {
			Log.d(TAG, save_location_history.get(i));
		}
		assertTrue(save_location_history.size() == 2);
		assertTrue(save_location_history.get(0).equals(current_folder));
		assertTrue(save_location_history.get(1).equals("new_folder_history_entry"));
		
		restart();

		save_location_history = mActivity.getSaveLocationHistory();
		assertTrue(save_location_history.size() == 2);
		assertTrue(save_location_history.get(0).equals(current_folder));
		assertTrue(save_location_history.get(1).equals("new_folder_history_entry"));

		{
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PreferenceKeys.getSaveLocationPreferenceKey(), current_folder);
			editor.apply();
			// now call testUsedFolderPicker() instead of updateForSettings(), to simulate using the recent folder picker
			// clearFolderHistory has code that must be run on UI thread
			mActivity.runOnUiThread(new Runnable() {
				public void run() {
	    			mActivity.usedFolderPicker();
				}
			});
			// need to wait for UI code to finish before leaving
			this.getInstrumentation().waitForIdleSync();
		}
		save_location_history = mActivity.getSaveLocationHistory();
		assertTrue(save_location_history.size() == 2);
		assertTrue(save_location_history.get(0).equals("new_folder_history_entry"));
		assertTrue(save_location_history.get(1).equals(current_folder));

		// clearFolderHistory has code that must be run on UI thread
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
    			mActivity.clearFolderHistory();
			}
		});
		// need to wait for UI code to finish before leaving
		this.getInstrumentation().waitForIdleSync();
		save_location_history = mActivity.getSaveLocationHistory();
		assertTrue(save_location_history.size() == 1);
		assertTrue(save_location_history.get(0).equals(current_folder));
	}

	private void compareVideoQuality(List<String> video_quality, List<String> exp_video_quality) {
		for(int i=0;i<video_quality.size();i++) {
        	Log.d(TAG, "supported video quality: " + video_quality.get(i));
		}
		for(int i=0;i<exp_video_quality.size();i++) {
        	Log.d(TAG, "expected video quality: " + exp_video_quality.get(i));
		}
		assertTrue( video_quality.size() == exp_video_quality.size() );
		for(int i=0;i<video_quality.size();i++) {
			String quality = video_quality.get(i);
			String exp_quality = exp_video_quality.get(i);
			assertTrue(quality.equals(exp_quality));
		}
	}
	
	/* Tests for setting correct video resolutions and profiles.
	 */
	public void testVideoResolutions1() {
		Vector<CameraController.Size> video_sizes = new Vector<CameraController.Size>();
		video_sizes.add(new CameraController.Size(1920, 1080));
		video_sizes.add(new CameraController.Size(1280, 720));
		video_sizes.add(new CameraController.Size(1600, 900));
		mPreview.setVideoSizes(video_sizes);

		SparseArray<Pair<Integer, Integer>> profiles = new SparseArray<Pair<Integer, Integer>>();
		profiles.put(CamcorderProfile.QUALITY_HIGH, new Pair<Integer, Integer>(1920, 1080));
		profiles.put(CamcorderProfile.QUALITY_1080P, new Pair<Integer, Integer>(1920, 1080));
		profiles.put(CamcorderProfile.QUALITY_720P, new Pair<Integer, Integer>(1280, 720));
		profiles.put(CamcorderProfile.QUALITY_LOW, new Pair<Integer, Integer>(1280, 720));
		mPreview.initialiseVideoQualityFromProfiles(profiles);

		List<String> video_quality = mPreview.getSupportedVideoQuality();
		Vector<String> exp_video_quality = new Vector<String>();
		exp_video_quality.add("" + CamcorderProfile.QUALITY_HIGH);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_720P + "_r1600x900");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_720P);
		compareVideoQuality(video_quality, exp_video_quality);
	}

	public void testVideoResolutions2() {
		Vector<CameraController.Size> video_sizes = new Vector<CameraController.Size>();
		video_sizes.add(new CameraController.Size(1920, 1080));
		video_sizes.add(new CameraController.Size(1280, 720));
		video_sizes.add(new CameraController.Size(1600, 900));
		mPreview.setVideoSizes(video_sizes);

		SparseArray<Pair<Integer, Integer>> profiles = new SparseArray<Pair<Integer, Integer>>();
		profiles.put(CamcorderProfile.QUALITY_HIGH, new Pair<Integer, Integer>(1920, 1080));
		profiles.put(CamcorderProfile.QUALITY_720P, new Pair<Integer, Integer>(1280, 720));
		profiles.put(CamcorderProfile.QUALITY_LOW, new Pair<Integer, Integer>(1280, 720));
		mPreview.initialiseVideoQualityFromProfiles(profiles);

		List<String> video_quality = mPreview.getSupportedVideoQuality();
		Vector<String> exp_video_quality = new Vector<String>();
		exp_video_quality.add("" + CamcorderProfile.QUALITY_HIGH);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_720P + "_r1600x900");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_720P);
		compareVideoQuality(video_quality, exp_video_quality);
	}

	public void testVideoResolutions3() {
		Vector<CameraController.Size> video_sizes = new Vector<CameraController.Size>();
		video_sizes.add(new CameraController.Size(1920, 1080));
		video_sizes.add(new CameraController.Size(1280, 720));
		video_sizes.add(new CameraController.Size(960, 720));
		video_sizes.add(new CameraController.Size(800, 480));
		video_sizes.add(new CameraController.Size(720, 576));
		video_sizes.add(new CameraController.Size(720, 480));
		video_sizes.add(new CameraController.Size(768, 576));
		video_sizes.add(new CameraController.Size(640, 480));
		video_sizes.add(new CameraController.Size(320, 240));
		video_sizes.add(new CameraController.Size(352, 288));
		video_sizes.add(new CameraController.Size(240, 160));
		video_sizes.add(new CameraController.Size(176, 144));
		video_sizes.add(new CameraController.Size(128, 96));
		mPreview.setVideoSizes(video_sizes);

		SparseArray<Pair<Integer, Integer>> profiles = new SparseArray<Pair<Integer, Integer>>();
		profiles.put(CamcorderProfile.QUALITY_HIGH, new Pair<Integer, Integer>(1920, 1080));
		profiles.put(CamcorderProfile.QUALITY_1080P, new Pair<Integer, Integer>(1920, 1080));
		profiles.put(CamcorderProfile.QUALITY_720P, new Pair<Integer, Integer>(1280, 720));
		profiles.put(CamcorderProfile.QUALITY_480P, new Pair<Integer, Integer>(720, 480));
		profiles.put(CamcorderProfile.QUALITY_CIF, new Pair<Integer, Integer>(352, 288));
		profiles.put(CamcorderProfile.QUALITY_QVGA, new Pair<Integer, Integer>(320, 240));
		profiles.put(CamcorderProfile.QUALITY_LOW, new Pair<Integer, Integer>(320, 240));
		mPreview.initialiseVideoQualityFromProfiles(profiles);

		List<String> video_quality = mPreview.getSupportedVideoQuality();
		Vector<String> exp_video_quality = new Vector<String>();
		exp_video_quality.add("" + CamcorderProfile.QUALITY_HIGH);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_720P);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_480P + "_r960x720");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_480P + "_r768x576");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_480P + "_r720x576");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_480P + "_r800x480");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_480P);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_CIF + "_r640x480");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_CIF);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_QVGA);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_LOW + "_r240x160");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_LOW + "_r176x144");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_LOW + "_r128x96");
		compareVideoQuality(video_quality, exp_video_quality);
	}

	// case from https://sourceforge.net/p/opencamera/discussion/general/thread/b95bfb83/?limit=25#14ac
	public void testVideoResolutions4() {
		// Video quality: 4_r864x480, 4, 2
		// Video resolutions: 176x144, 480x320, 640x480, 864x480, 1280x720, 1920x1080
		Vector<CameraController.Size> video_sizes = new Vector<CameraController.Size>();
		video_sizes.add(new CameraController.Size(176, 144));
		video_sizes.add(new CameraController.Size(480, 320));
		video_sizes.add(new CameraController.Size(640, 480));
		video_sizes.add(new CameraController.Size(864, 480));
		video_sizes.add(new CameraController.Size(1280, 720));
		video_sizes.add(new CameraController.Size(1920, 1080));
		mPreview.setVideoSizes(video_sizes);

		SparseArray<Pair<Integer, Integer>> profiles = new SparseArray<Pair<Integer, Integer>>();
		profiles.put(CamcorderProfile.QUALITY_HIGH, new Pair<Integer, Integer>(1920, 1080));
		profiles.put(CamcorderProfile.QUALITY_480P, new Pair<Integer, Integer>(640, 480));
		profiles.put(CamcorderProfile.QUALITY_QCIF, new Pair<Integer, Integer>(176, 144));
		mPreview.initialiseVideoQualityFromProfiles(profiles);

		List<String> video_quality = mPreview.getSupportedVideoQuality();
		Vector<String> exp_video_quality = new Vector<String>();
		exp_video_quality.add("" + CamcorderProfile.QUALITY_HIGH);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_480P + "_r1280x720");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_480P + "_r864x480");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_480P);
		exp_video_quality.add("" + CamcorderProfile.QUALITY_QCIF + "_r480x320");
		exp_video_quality.add("" + CamcorderProfile.QUALITY_QCIF);
		compareVideoQuality(video_quality, exp_video_quality);
	}

	public void testPreviewRotation() {
		Log.d(TAG, "testPreviewRotation");

		setToDefault();
		
		int display_orientation = mPreview.getDisplayRotation();
		Log.d(TAG, "display_orientation = " + display_orientation);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getRotatePreviewPreferenceKey(), "180");
		editor.apply();
		updateForSettings();

		int new_display_orientation = mPreview.getDisplayRotation();
		Log.d(TAG, "new_display_orientation = " + new_display_orientation);
		assertTrue( new_display_orientation == ((display_orientation + 2) % 4) );
	}

	public void testSceneMode() {
		Log.d(TAG, "testSceneMode");

		setToDefault();
		
	    List<String> scene_modes = mPreview.getSupportedSceneModes();
	    if( scene_modes == null ) {
	    	return;
	    }
		Log.d(TAG, "scene mode: " + mPreview.getCameraController().getSceneMode());
	    assertTrue( mPreview.getCameraController().getSceneMode() == null || mPreview.getCameraController().getSceneMode().equals(mPreview.getCameraController().getDefaultSceneMode()) );

	    String scene_mode = null;
	    // find a scene mode that isn't default
	    for(String this_scene_mode : scene_modes) {
	    	if( !this_scene_mode.equals(mPreview.getCameraController().getDefaultSceneMode()) ) {
	    		scene_mode = this_scene_mode;
	    		break;
	    	}
	    }
	    if( scene_mode == null ) {
	    	return;
	    }
		Log.d(TAG, "change to scene_mode: " + scene_mode);
	    
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getSceneModePreferenceKey(), scene_mode);
		editor.apply();
		updateForSettings();

		String new_scene_mode = mPreview.getCameraController().getSceneMode();
		Log.d(TAG, "scene_mode is now: " + new_scene_mode);
	    assertTrue( new_scene_mode.equals(scene_mode) );
	}

	public void testColorEffect() {
		Log.d(TAG, "testColorEffect");

		setToDefault();
		
	    List<String> color_effects = mPreview.getSupportedColorEffects();
	    if( color_effects == null ) {
	    	return;
	    }
		Log.d(TAG, "color effect: " + mPreview.getCameraController().getColorEffect());
	    assertTrue( mPreview.getCameraController().getColorEffect() == null || mPreview.getCameraController().getColorEffect().equals(mPreview.getCameraController().getDefaultColorEffect()) );

	    String color_effect = null;
	    // find a color effect that isn't default
	    for(String this_color_effect : color_effects) {
	    	if( !this_color_effect.equals(mPreview.getCameraController().getDefaultColorEffect()) ) {
	    		color_effect = this_color_effect;
	    		break;
	    	}
	    }
	    if( color_effect == null ) {
	    	return;
	    }
		Log.d(TAG, "change to color_effect: " + color_effect);
	    
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getColorEffectPreferenceKey(), color_effect);
		editor.apply();
		updateForSettings();

		String new_color_effect = mPreview.getCameraController().getColorEffect();
		Log.d(TAG, "color_effect is now: " + new_color_effect);
	    assertTrue( new_color_effect.equals(color_effect) );
	}

	public void testWhiteBalance() {
		Log.d(TAG, "testWhiteBalance");

		setToDefault();
		
	    List<String> white_balances = mPreview.getSupportedWhiteBalances();
	    if( white_balances == null ) {
	    	return;
	    }
		Log.d(TAG, "white balance: " + mPreview.getCameraController().getWhiteBalance());
	    assertTrue( mPreview.getCameraController().getWhiteBalance() == null || mPreview.getCameraController().getWhiteBalance().equals(mPreview.getCameraController().getDefaultWhiteBalance()) );

	    String white_balance = null;
	    // find a white balance that isn't default
	    for(String this_white_balances : white_balances) {
	    	if( !this_white_balances.equals(mPreview.getCameraController().getDefaultWhiteBalance()) ) {
	    		white_balance = this_white_balances;
	    		break;
	    	}
	    }
	    if( white_balance == null ) {
	    	return;
	    }
		Log.d(TAG, "change to white_balance: " + white_balance);
	    
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getWhiteBalancePreferenceKey(), white_balance);
		editor.apply();
		updateForSettings();

		String new_white_balance = mPreview.getCameraController().getWhiteBalance();
		Log.d(TAG, "white_balance is now: " + new_white_balance);
	    assertTrue( new_white_balance.equals(white_balance) );
	}

	public void testImageQuality() {
		Log.d(TAG, "testImageQuality");

		setToDefault();
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getQualityPreferenceKey(), "100");
		editor.apply();
		updateForSettings();

		int quality = mPreview.getCameraController().getJpegQuality();
		Log.d(TAG, "quality is: " + quality);
	    assertTrue( quality == 100 );
	}

	/* Test for failing to open camera.
	 */
	public void testFailOpenCamera() throws InterruptedException {
		Log.d(TAG, "testFailOpenCamera");

		setToDefault();

		assertTrue(mPreview.getCameraControllerManager() != null);
		assertTrue(mPreview.getCameraController() != null);
		mPreview.test_fail_open_camera = true;

		// can't test on startup, as camera is created when we create activity, so instead test by switching camera
		if( mPreview.getCameraControllerManager().getNumberOfCameras() > 0 ) {
			Log.d(TAG, "switch camera");
		    View switchCameraButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.switch_camera);
		    clickView(switchCameraButton);
			assertTrue(mPreview.getCameraControllerManager() != null);
			assertTrue(mPreview.getCameraController() == null);
			this.getInstrumentation().waitForIdleSync();
		
			assertFalse( mActivity.popupIsOpen() );
		    View popupButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.popup);
			Log.d(TAG, "about to click popup");
		    clickView(popupButton);
			Log.d(TAG, "done clicking popup");
			Thread.sleep(500);
			// if camera isn't opened, popup shouldn't open
			assertFalse( mActivity.popupIsOpen() );

		    View settingsButton = (View) mActivity.findViewById(net.sourceforge.opencamera.R.id.settings);
			Log.d(TAG, "about to click settings");
		    clickView(settingsButton);
			Log.d(TAG, "done clicking settings");
			this.getInstrumentation().waitForIdleSync();
			Log.d(TAG, "after idle sync");
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PreferenceKeys.getVolumeKeysPreferenceKey(), "volume_exposure");
		editor.apply();
		this.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP);
	}
	
	public void testBestPreviewFps() {
		Log.d(TAG, "testBestPreviewFps");

		setToDefault();
		
		List<int []> list0 = new ArrayList<int []>();
		list0.add(new int[]{15000, 15000});
		list0.add(new int[]{15000, 30000});
		list0.add(new int[]{7000, 30000});
		list0.add(new int[]{30000, 30000});
		int [] best_fps0 = mPreview.chooseBestPreviewFps(list0);
		assertTrue(best_fps0[0] == 7000 && best_fps0[1] == 30000);

		List<int []> list1 = new ArrayList<int []>();
		list1.add(new int[]{15000, 15000});
		list1.add(new int[]{7000, 60000});
		list1.add(new int[]{15000, 30000});
		list1.add(new int[]{7000, 30000});
		list1.add(new int[]{30000, 30000});
		int [] best_fps1 = mPreview.chooseBestPreviewFps(list1);
		assertTrue(best_fps1[0] == 7000 && best_fps1[1] == 60000);

		List<int []> list2 = new ArrayList<int []>();
		list2.add(new int[]{15000, 15000});
		list2.add(new int[]{7000, 15000});
		list2.add(new int[]{7000, 10000});
		list2.add(new int[]{8000, 19000});
		int [] best_fps2 = mPreview.chooseBestPreviewFps(list2);
		assertTrue(best_fps2[0] == 8000 && best_fps2[1] == 19000);
	}

	public void testMatchPreviewFpsToVideo() {
		Log.d(TAG, "matchPreviewFpsToVideo");

		setToDefault();
		
		List<int []> list0 = new ArrayList<int []>();
		list0.add(new int[]{15000, 15000});
		list0.add(new int[]{15000, 30000});
		list0.add(new int[]{7000, 30000});
		list0.add(new int[]{30000, 30000});
		int [] best_fps0 = mPreview.matchPreviewFpsToVideo(list0, 30000);
		assertTrue(best_fps0[0] == 30000 && best_fps0[1] == 30000);

		List<int []> list1 = new ArrayList<int []>();
		list1.add(new int[]{15000, 15000});
		list1.add(new int[]{7000, 60000});
		list1.add(new int[]{15000, 30000});
		list1.add(new int[]{7000, 30000});
		list1.add(new int[]{30000, 30000});
		int [] best_fps1 = mPreview.matchPreviewFpsToVideo(list1, 15000);
		assertTrue(best_fps1[0] == 15000 && best_fps1[1] == 15000);

		List<int []> list2 = new ArrayList<int []>();
		list2.add(new int[]{15000, 15000});
		list2.add(new int[]{7000, 15000});
		list2.add(new int[]{7000, 10000});
		list2.add(new int[]{8000, 19000});
		int [] best_fps2 = mPreview.matchPreviewFpsToVideo(list2, 7000);
		assertTrue(best_fps2[0] == 7000 && best_fps2[1] == 10000);
	}

	public void testLocationToDMS() {
		Log.d(TAG, "testLocationToDMS");

		setToDefault();
		
		String location_string = LocationSupplier.locationToDMS(0.0);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("0�0'0\""));

		location_string = LocationSupplier.locationToDMS(0.0000306);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("0�0'0\""));

		location_string = LocationSupplier.locationToDMS(0.000306);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("0�0'1\""));

		location_string = LocationSupplier.locationToDMS(0.00306);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("0�0'11\""));

		location_string = LocationSupplier.locationToDMS(0.9999);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("0�59'59\""));

		location_string = LocationSupplier.locationToDMS(1.7438);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("1�44'37\""));

		location_string = LocationSupplier.locationToDMS(53.000137);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("53�0'0\""));

		location_string = LocationSupplier.locationToDMS(147.00938);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("147�0'33\""));

		location_string = LocationSupplier.locationToDMS(-0.0);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("0�0'0\""));

		location_string = LocationSupplier.locationToDMS(-0.0000306);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("0�0'0\""));

		location_string = LocationSupplier.locationToDMS(-0.000306);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("-0�0'1\""));

		location_string = LocationSupplier.locationToDMS(-0.00306);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("-0�0'11\""));

		location_string = LocationSupplier.locationToDMS(-0.9999);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("-0�59'59\""));

		location_string = LocationSupplier.locationToDMS(-1.7438);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("-1�44'37\""));

		location_string = LocationSupplier.locationToDMS(-53.000137);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("-53�0'0\""));

		location_string = LocationSupplier.locationToDMS(-147.00938);
		Log.d(TAG, "location_string: " + location_string);
		assertTrue(location_string.equals("-147�0'33\""));
	}
		
}
