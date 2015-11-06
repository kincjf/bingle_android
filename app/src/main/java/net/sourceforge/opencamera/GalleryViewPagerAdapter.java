package net.sourceforge.opencamera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sourceforge.opencamera.UI.GalleryViewPager;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by WG on 2015-11-06.
 */
public class GalleryViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "GalleryViewPagerAdapter";
    private final Context context;
    private ArrayList<String> urls = new ArrayList<String>();
    private ViewPager viewPager;
    LayoutInflater inflater;

    public GalleryViewPagerAdapter(Context context,  ArrayList<String> url){
        if( MyDebug.LOG ) {
            Log.d(TAG, "load");
        }
        this.context = context;
        this.urls = url;

        inflater = LayoutInflater.from(context);
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override

    public View instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setImageResource(Integer.parseInt(urls.get(position)));

        // Now just add PhotoView to ViewPager and return it
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;

    }

    private void toggleViewPagerScrolling() {
        if (isViewPagerActive()) {
            ((GalleryViewPager) viewPager).toggleLock();
        }
    }

    private boolean isViewPagerActive() {
        return (viewPager != null && viewPager instanceof GalleryViewPager);
    }


}
