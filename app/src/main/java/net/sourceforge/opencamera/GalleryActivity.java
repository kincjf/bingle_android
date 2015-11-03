package net.sourceforge.opencamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;


/**
 * Created by WG on 2015-10-22.
 */
public class GalleryActivity extends Activity {
    private static final String TAG = "GalleryActivity";
    private GridView gridView;
    private ArrayList<String> urls;
    private GridViewAdapter gridViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if( MyDebug.LOG ) {
            Log.d(TAG, "onCreate");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .writeDebugLogs() // Remove for release app
                    .build();
            ImageLoader.getInstance().init(config);

            if( MyDebug.LOG ) {
                Log.d(TAG, "ImageLoader init");
            }
        }

        Intent intent = getIntent();
        urls = intent.getExtras().getStringArrayList("imageList");
        gridView = (GridView)findViewById(R.id.iv_grid);
        gridViewAdapter = new GridViewAdapter(GalleryActivity.this, urls);
        try{
            gridView.setAdapter(gridViewAdapter);
        }catch (OutOfMemoryError E) {
            E.printStackTrace();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String rutaDeLaImagen = gridViewAdapter.getItem(position).toString();

                Intent intent = new Intent(GalleryActivity.this, GalleryPanoViewer.class);
                intent.putExtra("url", rutaDeLaImagen);
                startActivity(intent);

            }
        });
    }
}
