package net.sourceforge.opencamera;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import java.util.ArrayList;


final class GridViewAdapter extends ArrayAdapter {
    private static final String TAG = "GalleryAdapter";
    private final Context context;
    private ArrayList<String> urls = new ArrayList<String>();
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, ArrayList<String> url) {
        super(context,R.layout.gallery_layout, url);
        if( MyDebug.LOG ) {
            Log.d(TAG, "load");
        }
        this.context = context;
        this.urls = url;

        inflater = LayoutInflater.from(context);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            if( MyDebug.LOG ) {
                Log.d(TAG, "converView :" + convertView);
            }
            convertView = inflater.inflate(R.layout.gallery_girdview_image,parent,false);
            convertView.setPadding(2,0,2,0);
        }

        if(urls != null) {
            Glide.with(context)
                    .load("file://" + urls.get(position))
                    .placeholder(R.drawable.gallery)
                    .override(300, 300)
                    .centerCrop()
                    .into((ImageView) convertView);
        }else{
            if( MyDebug.LOG ) {
                Log.d(TAG, "urls : null");
            }
        }

        return convertView;
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