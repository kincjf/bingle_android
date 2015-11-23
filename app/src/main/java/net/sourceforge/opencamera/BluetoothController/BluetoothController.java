package net.sourceforge.opencamera.BluetoothController;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;
import net.sourceforge.opencamera.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


/**
 * Created by yoonsKim on 2015. 11. 11..
 */
public class BluetoothController implements BlueToothInterface {
    private static final String TAG = "BluetoothController";

    private Activity mActivity;
    private Handler mHandler;
    private BluetoothAdapter btAdapter;

    private static BluetoothSPP bluetooth;
    Intent intent;

    private static final int REQUEST_ENABLE_BT = 2;

    public BluetoothController(Activity activity, Handler handle){

        mActivity = activity;
        mHandler = handle;
        // BluetoothAdapter 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth = new BluetoothSPP(activity);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bluetooth.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_ANDROID);
//                setup();
                bluetooth.setBluetoothConnectionListener(new BluetoothConnectionListener() {
                    public void onDeviceConnected(String name, String address) {
                        // Do something when successfully connected
                        Log.i(TAG, "Status : Connected to " + name);
                    }

                    public void onDeviceDisconnected() {
                        // Do something when connection was disconnected
                        Log.i(TAG, "Status : Not connect");
                    }

                    public void onDeviceConnectionFailed() {
                        // Do something when connection failed
                        Log.i(TAG, "Status : Connection failed");
                    }
                });

                bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                    @Override
                    public void onDataReceived(byte[] data, String message) {
                        if(SBGCProtocol.action.IncommandAction(data)) {
                            Log.d(TAG, "Received Message: " + message);
                        } else {
                            Log.d(TAG, "Received ERROR - unknown command : " + message);
                        }
                    }
                });
            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }

    public static BluetoothSPP getBluetooth() {
        return bluetooth;
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


    //블루투스 장비 페이링 확인
    public void pairedDevice(){
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        List<String> listItems = new ArrayList<String>();

        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                //페어링된 장치 이름과, MAC주소를 가져올 수 있다.
                Log.d(TAG, device.getName().toString() +" Device Is Connected!");
                Log.d(TAG, device.getAddress().toString() + " Device Is Connected!");
                //listItems.add(device : pairedDevices);

            }
            listItems.add("취소");

        }else {
            Log.d(TAG, "No PairedDevices");
        }
    }



    //블루투스 장비 선택
    public void selectDevice() {
        Log.d(TAG, "selectDevice()");

        intent = new Intent(mActivity, DeviceList.class);
        mActivity.startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    //블루투스 활성화 확인
    public boolean getIsBluetoothEanble(){
        return bluetooth.isBluetoothEnabled();
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
