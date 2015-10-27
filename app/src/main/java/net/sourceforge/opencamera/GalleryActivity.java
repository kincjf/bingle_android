package net.sourceforge.opencamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by WG on 2015-10-22.
 */
public class GalleryActivity extends Activity {
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
        Intent intent = getIntent();

        ArrayList<String> urls = intent.getExtras().getStringArrayList("imageList");
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

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + rutaDeLaImagen), "image/*");
                startActivity(intent);

            }
        });

        //imageView = (ImageView) findViewById(R.id.iv_simple);
        //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + "pastel"), "suji.jpg");
        //SimpleImage(this, file, imageView);
    }



    public void SimpleImage(Context context, File file, ImageView imageView){
        mAttacher = new PhotoViewAttacher(imageView);

        //File file = new File(image_list);
        Picasso.with(context)
                .load(file)    //.load(uri)
                .into(imageView);
    }
}
