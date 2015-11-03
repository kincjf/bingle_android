package net.sourceforge.opencamera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.panorama.Panorama;
import com.google.android.gms.panorama.PanoramaApi.PanoramaResult;

/**
 * Created by WG on 2015-10-28.
 */
public class GalleryPanoViewer extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
    public static final String TAG = "PanoViewer";
    public String filepath = null;

    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "start");
        Intent intent = getIntent();
        filepath = intent.getExtras().getString("url");

        mClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Panorama.API)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        Log.i(TAG, "Client connect");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "filepath : " + filepath);
        Panorama.PanoramaApi.loadPanoramaInfo(mClient, Uri.parse("file://"+filepath)).setResultCallback(
                new ResultCallback<PanoramaResult>() {
                    @Override
                    public void onResult(PanoramaResult result) {
                        if (result.getStatus().isSuccess()) {
                            Intent viewerIntent = result.getViewerIntent();
                            Log.i(TAG, "found viewerIntent: " + viewerIntent);
                            if (viewerIntent != null) {
                                startActivity(viewerIntent);

                            }
                        } else {
                            Log.e(TAG, "error: " + result);
                        }
                    }
                });
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "connection suspended: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult status) {
        Log.e(TAG, "connection failed: " + status);
        // TODO fill in
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "Stop");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mClient.disconnect();
        Log.i(TAG, "Client disconnect");
    }



}
