package net.sourceforge.opencamera.Util;

import android.app.Activity;
import android.widget.Toast;

import net.sourceforge.opencamera.R;

import java.util.ArrayList;

/**
 * Created by KIMSEONHO on 2015-12-07.
 */
public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private final long DELAY = 2000;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {       // when exit
        if (System.currentTimeMillis() > backKeyPressedTime + DELAY) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + DELAY) {
            activity.finish();
            toast.cancel();
        }
    }

    public boolean onBackPressed(boolean started) {      // when stop action
        if (System.currentTimeMillis() > backKeyPressedTime + DELAY) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide(started);
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + DELAY) {
            toast.cancel();
            return false;       // stop action
        }

        return true;
    }

    private void showGuide() {
        toast = Toast.makeText(activity, R.string.exit_app, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showGuide(final boolean historyName) {
        toast = Toast.makeText(activity, R.string.stop_action + ":" + historyName, Toast.LENGTH_SHORT);
        toast.show();
    }
}
