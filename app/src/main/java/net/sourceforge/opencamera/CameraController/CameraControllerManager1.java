package net.sourceforge.opencamera.CameraController;

import android.hardware.Camera;
import android.util.Log;

import net.sourceforge.opencamera.MyDebug;

@SuppressWarnings("deprecation")
public class CameraControllerManager1 extends CameraControllerManager implements ICameraAction {
	private static final String TAG = "CameraControllerManager1";
	public int getNumberOfCameras() {
		return Camera.getNumberOfCameras();
	}

	public boolean isFrontFacing(int cameraId) {
	    try {
		    Camera.CameraInfo camera_info = new Camera.CameraInfo();
			Camera.getCameraInfo(cameraId, camera_info);
			return (camera_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
	    }
	    catch(RuntimeException e) {
	    	// Had a report of this crashing on Galaxy Nexus - may be device specific issue, see http://stackoverflow.com/questions/22383708/java-lang-runtimeexception-fail-to-get-camera-info
	    	// but good to catch it anyway
    		if( MyDebug.LOG )
    			Log.d(TAG, "failed to set parameters");
	    	e.printStackTrace();
	    	return false;
	    }
	}

	@Override
	public int takeSphericalPanorama() {
		int[][] rpy = {
				{0, 0, 0},
				{0,-70,0},
				{0, -45, 0},
				{0,-45,60},
				{0, -45, 120},
				{0,-45,180},
				{0, -45, 240},
				{0,-45,300},
				{0, 0, 0},
				{0,0,-60},
				{0, 0, -120},
				{0,0,-180},
				{0,0,-240},
				{0,0,-300},
				{0,45,0},
				{0,45,60},
				{0,45,120},
				{0,45, 180},
				{0,45,240},
				{0,45,300},
				{0,60,0},
				{0,0,-360},
				{0,0,0}
		};



		return 0;
	}
}
