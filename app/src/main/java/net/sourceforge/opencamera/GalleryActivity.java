package net.sourceforge.opencamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.panorama.Panorama;
import com.google.android.gms.panorama.PanoramaApi;

import java.util.ArrayList;


/**
 * Created by WG on 2015-10-22.
 */
public class GalleryActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GalleryActivity";
    private GridView gridView;
    private ArrayList<String> urls;
    private GalleryAdapter galleryAdapter;

    private GoogleApiClient mClient;

    String clickImageUrl;
    int requestStatus = 0;      // Panorama Viewer 정상 종료

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if( MyDebug.LOG ) {
            Log.d(TAG, "onCreate");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        Intent intent = getIntent();
        urls = intent.getExtras().getStringArrayList("imageList");
        gridView = (GridView)findViewById(R.id.iv_grid);
        galleryAdapter = new GalleryAdapter(GalleryActivity.this, urls);
        gridView.setSelector(new StateListDrawable()); // image 선택시 생기는 여백 제거

        try{
            gridView.setAdapter(galleryAdapter);
        }catch (OutOfMemoryError E) {
            E.printStackTrace();
        }

        mClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Panorama.API)
                .build();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                clickImageUrl = galleryAdapter.getItem(position).toString();


                mClient.connect();
/*
                Intent intent = new Intent(GalleryActivity.this, GalleryPanoViewer.class);
                intent.putExtra("clickImageUrl", clickImageUrl);
                startActivity(intent);
*/
                /*  그리드 뷰 이미지 클릭시 view pager로 연동
                String clickImageUrl = gridViewAdapter.getItem(position).toString();

                Intent intent = new Intent(GalleryActivity.this, GalleryViewPagerActivity.class);
                intent.putExtra("urls", urls);
                intent.putExtra("clickImageUrl", position);
                startActivity(intent);
                */

            }
        });
    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "filepath : " + clickImageUrl);

        Panorama.PanoramaApi.loadPanoramaInfo(mClient, Uri.parse("file://" + clickImageUrl)).setResultCallback(
                new ResultCallback<PanoramaApi.PanoramaResult>() {
                    @Override
                    public void onResult(PanoramaApi.PanoramaResult result) {
                        if (result.getStatus().isSuccess()) {
                            Intent viewerIntent = result.getViewerIntent();
                            Log.i(TAG, "found viewerIntent: " + viewerIntent);
                            if (viewerIntent != null) {     // 정상적인 Panorama 사진을 경우(Metadata)
                                startActivityForResult(viewerIntent, requestStatus);
                            } else {
                                Toast.makeText(GalleryActivity.this, "VR 파노라마 사진이 아닙니다.", Toast.LENGTH_SHORT).show();
                                mClient.disconnect();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
             case 0 :       // 정상 종료
                 mClient.disconnect();
                 break;
             default:
                 Log.d(TAG, "Error when Panorama Viewer exited");
        }
        Log.i(TAG, "resultCode : " + resultCode);
    }


}
