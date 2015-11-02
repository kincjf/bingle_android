package net.sourceforge.opencamera;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class GridViewAdapter extends BaseAdapter {
    private static final String TAG = "GalleryAdapter";
    private final Context context;
    private ArrayList<String> urls = new ArrayList<String>();

    public GridViewAdapter(Context context, ArrayList<String> url) {
        super();
        if( MyDebug.LOG ) {
            Log.d(TAG, "GridViewAdapter");
        }
        this.context = context;
        this.urls = url;

    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view;
        if (convertView == null) {
            if( MyDebug.LOG ) {
                Log.d(TAG, "converView :" + convertView);
            }
            view = new ImageView(context);
            convertView = view;
            view.setPadding(5,5,5,5);
        }else {
            view = (ImageView) convertView;
        }


        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load("file://"+urls.get(position)) //
                .resize(300, 200)
                .into(view);

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