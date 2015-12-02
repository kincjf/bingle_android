package net.sourceforge.opencamera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import net.sourceforge.opencamera.UI.GalleryViewPager;

import java.util.ArrayList;

/**
 * Created by WG on 2015-11-06.
 * 아직 완성 안됨
 */
public class GalleryViewPagerActivity extends Activity{

    private static final String TAG = "GalleryViewPager";
    private static final String ISLOCKED_ARG = "isLocked";
    private ViewPager viewPager;
    private GalleryViewPagerAdapter galleryViewPagerAdapter;
    private ArrayList<String> urls;
    private int clickImageUrl;

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
        urls = intent.getExtras().getStringArrayList("urls");
        clickImageUrl = intent.getExtras().getInt("clickImageUrl");
        galleryViewPagerAdapter = new GalleryViewPagerAdapter(GalleryViewPagerActivity.this, urls, clickImageUrl);

        viewPager.setAdapter(galleryViewPagerAdapter);

    }

}
