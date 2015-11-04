package net.sourceforge.opencamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


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
