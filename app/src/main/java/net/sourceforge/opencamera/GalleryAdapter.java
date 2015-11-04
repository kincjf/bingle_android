package net.sourceforge.opencamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;


final class GridViewAdapter extends ArrayAdapter {
    private static final String TAG = "GalleryAdapter";
    private final Context context;
    private ArrayList<String> urls = new ArrayList<String>();
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, ArrayList<String> url) {
        super(context,R.layout.gallery_layout, url);
        if( MyDebug.LOG ) {
            Log.d(TAG, "GridViewAdapter");
        }
        this.context = context;
        this.urls = url;

        inflater = LayoutInflater.from(context);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView view;
        if (convertView == null) {
            if( MyDebug.LOG ) {
                Log.d(TAG, "converView :" + convertView);
            }
            view = new ImageView(context);
            //view = (ImageView) inflater.inflate(R.layout.gallery_layout,parent,false);

        }else {
            view = (ImageView) convertView;
        }

        Glide.with(context)
                .load("file://"+urls.get(position))
                .override(300, 200)
                .thumbnail(0.1f)
                .into(view);


        // Trigger the download of the URL asynchronously into the image view.
        //Picasso.with(context) //
        //        .load("file://"+urls.get(position)) //
        //        .resize(300, 200)
        //        .into(view);

        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public Object getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}