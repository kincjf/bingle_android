package net.sourceforge.opencamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import net.sourceforge.opencamera.UI.GalleryViewPager;

import java.util.ArrayList;

/**
 * Created by WG on 2015-11-06.
 */
public class GalleryViewPagerActivity extends Activity{

    private static final String TAG = "GalleryViewPagerActivity";
    private static final String ISLOCKED_ARG = "isLocked";
    private ViewPager viewPager;
    private GalleryViewPagerAdapter galleryViewPagerAdapter;
    private ArrayList<String> urls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (MyDebug.LOG) {
            Log.d(TAG, "onCreate");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_view_pager);
        viewPager = (GalleryViewPager) findViewById(R.id.view_pager);
        setContentView(viewPager);

        Intent intent = getIntent();
        urls = intent.getExtras().getStringArrayList("imageList");
        galleryViewPagerAdapter = new GalleryViewPagerAdapter(GalleryViewPagerActivity.this, urls);

        viewPager.setAdapter(galleryViewPagerAdapter);

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
        }

    }

}
