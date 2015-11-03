package net.sourceforge.opencamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


final class GridViewAdapter extends BaseAdapter {
    private static final String TAG = "GalleryAdapter";
    private final Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ArrayList<String> urls = new ArrayList<String>();
    private DisplayImageOptions option;

    public GridViewAdapter(Context context, ArrayList<String> url) {
        super();
        if( MyDebug.LOG ) {
            Log.d(TAG, "GridViewAdapter");
        }
        this.context = context;
        this.urls = url;

        option = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();

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

        // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
        //  which implements ImageAware interface)
        imageLoader.displayImage("file://" + urls.get(position), view, option);


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