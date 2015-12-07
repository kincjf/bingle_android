package net.sourceforge.opencamera.BluetoothController;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;
import net.sourceforge.opencamera.MyDebug;
import net.sourceforge.opencamera.PreferenceKeys;

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
//    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

    static BluetoothSPP bt;

//    private static BluetoothSPP bluetooth;
    Intent intent;

    private static final int REQUEST_ENABLE_BT = 2;

    public BluetoothController(Activity activity, Handler handle){

        mActivity = activity;
        mHandler = handle;
        // BluetoothAdapter 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bt = new BluetoothSPP(activity);

        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(mActivity
                        , "Connected to " + name
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(mActivity
                        , "Connection lost"
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Log.i("Check", "Unable to connect");
            }
        });
        bt.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                Log.i("Check", "New Connection - " + name + " - " + address);
            }

            public void onAutoConnectionStarted() {
                Log.i("Check", "Auto menu_connection started");
            }
        });

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                if (SBGCProtocol.action.IncommandAction(data)) {
//                    Log.d(TAG, "Received Message: " + ProtocolUtil.getFirmwareVersion(data));
                    Log.d(TAG, "Received Message: " + message);
                } else {
                    Log.d(TAG, "Received ERROR - unknown command : " + message);
                }
            }
        });
        SBGCProtocol.initSBGCProtocol();




    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Bluetooth Device Connect");
                bt.connect(data);
/*
                deviceName = main_activity.getBluetoothConnectDeviceName();

                if (deviceName != null) {
                    if( MyDebug.LOG )
                        Log.d(TAG, "bluetooth connect device name : " + deviceName);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferenceKeys.getBluetoothDeviceListPreferenceKey(), deviceName);
                    editor.apply();

                }else {
                    if( MyDebug.LOG )
                        Log.d(TAG, "no bluetooth connect device name");
                }
 */
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);

            } else {
                Toast.makeText(mActivity
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchDevice(){
        Log.i(TAG, "Bluetooth Search Device");



        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        } else {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);

            Intent intent = new Intent(mActivity, DeviceList.class);
            mActivity.startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

        }

    }

    public static BluetoothSPP getBluetooth() {
        if(bt!= null){
            Log.i(TAG,"is bluetooth");

        }else{
            Log.i(TAG,"is not bluetooth");

        }
        return bt;
    }


    @Override
    public void turnClockWise(int yaw,int pitch) {

        SBGCProtocol.requestMoveGimbalTo(0,pitch,yaw);

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

        if(!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
//                setup();
            }
        }
    }

    //블루투스 활성화 확인
    public boolean getIsBluetoothEanble(){
        return bt.isBluetoothEnabled();

    }

    //연결된 블루투스 장비 이름 가져오기
    public String getBluetoothConnectDeviceName() {
        return bt.getConnectedDeviceName();
    }

    //블루투스 서비스 중지
    public void stopService(){
        bt.stopService();
    }

    @Override
    public void receive() {

    }

    boolean kakaka=true;
    int val = 90;
    @Override
    public void send() {
//        SBGCProtocol.action.OutgoingAction(SBGCProtocol.CMD_BOARD_INFO);

        Log.i(TAG,"zzzzzzxzxxzx");
        byte []sendData = new byte[1];
//        sendData[0]=SBGCProtocol.CMD_BOARD_INFO;
        sendData[0]=SBGCProtocol.CMD_MOTORS_ON;

//        bt.send(sendData,true);
//        if(kakaka){
//            SBGCProtocol.action.OutgoingAction(SBGCProtocol.CMD_MOTORS_OFF);
//            kakaka=!kakaka;
//        }else{
//            SBGCProtocol.action.OutgoingAction(SBGCProtocol.CMD_MOTORS_ON);
//            kakaka=!kakaka;
//
//        }

        SBGCProtocol.initSBGCProtocol();
val+=10;
        SBGCProtocol.requestMoveGimbalTo(0,10,val);
//        SBGCProtocol.action.OutgoingAction(SBGCProtocol.CMD_CONTROL,);
//        SBGCProtocol.requestMoveGimbalTo(0,0,100,0,0,10,1);

    }

    @Override
    public void encodePacket() {

    }

    @Override
    public void decodePacket() {

    }


}
