package net.sourceforge.opencamera;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by WG on 2015-11-06.
 */
public class GalleryViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "GalleryViewPagerAdapter";
    private final Context context;
    private ArrayList<String> urls = new ArrayList<String>();
    private int clickImageUrl;
    private ViewPager viewPager;
    private Boolean check = false;
    LayoutInflater inflater;

    public GalleryViewPagerAdapter(Context context,  ArrayList<String> url, int clickImageUrl){
        if( MyDebug.LOG ) {
            Log.d(TAG, "load");
        }
        this.context = context;
        this.urls = url;
        this.clickImageUrl = clickImageUrl;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override

    public View instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());

        if(check == false) {
            position = clickImageUrl;
            Glide.with(context)
                    .load("file://" + urls.get(position))
                    .override(400, 300)
                    .into(imageView);

            check = true;
        }else {
            Glide.with(context)
                    .load("file://" + urls.get(position))
                    .override(400, 300)
                    .into(imageView);
        }


        // Now just add PhotoView to ViewPager and return it
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;

    }


}
