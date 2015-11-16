package net.sourceforge.opencamera.BluetoothController;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;


/**
 * Created by yoonsKim on 2015. 11. 11..
 */
public class BluetoothController implements BlueToothInterface{
    private static final String TAG = "BluetoothService";

    private Activity mActivity;
    private Handler mHandler;
    private BluetoothAdapter btAdapter;
    BluetoothSPP bt;
    Intent intent;

    private static final int REQUEST_ENABLE_BT = 2;

    public BluetoothController(Activity activity, Handler handle){

        mActivity = activity;
        mHandler = handle;
        // BluetoothAdapter 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bt = new BluetoothSPP(activity);



    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
//                setup();
                bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
                    public void onDeviceConnected(String name, String address) {
                        // Do something when successfully connected
                    }

                    public void onDeviceDisconnected() {
                        // Do something when connection was disconnected
                    }

                    public void onDeviceConnectionFailed() {
                        // Do something when connection failed
                    }
                });
            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }


    @Override
    public void turnClockWise() {

    }

    @Override
    public void turnCounterClockWise() {

    }

    @Override
    public void turnUp() {

    }

    @Override
    public void turnDown() {

    }

    @Override
    public void turnStartPoint() {

    }

    @Override
    public void connect() {


    }

    @Override
    public void enableBluetooth() {

        Log.i(TAG, "Check the enabled Bluetooth");


        if(btAdapter.isEnabled()) {
            // 기기의 블루투스 상태가 On인 경우
            Log.d(TAG, "Bluetooth Enable Now");

//            intent = new Intent(mActivity, DeviceList.class);
//
//            mActivity.startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            // Next Step
        } else {
            // 기기의 블루투스 상태가 Off인 경우
            Log.d(TAG, "Bluetooth Enable Request");

            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
//            enableBluetooth();

        }
    }


    @Override
    public void receive() {

    }

    @Override
    public void send() {

    }

    @Override
    public void encodePacket() {

    }

    @Override
    public void decodePacket() {

    }
}
