package net.sourceforge.opencamera.BluetoothController;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

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
                    Log.d(TAG, "Received Message: " + message);
                } else {
                    Log.d(TAG, "Received ERROR - unknown command : " + message);
                }
            }
        });




    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Bluetooth Device Connect");
                bt.connect(data);
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
//            Toast.makeText(mActivity
//                    , "what is this"
//                    , Toast.LENGTH_SHORT).show();
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

    //블루투스 서비스 중지
    public void stopService(){
        bt.stopService();
    }

    @Override
    public void receive() {

    }

    @Override
    public void send() {
        SBGCProtocol.action.OutgoingAction(SBGCProtocol.CMD_BOARD_INFO);

        Log.i(TAG,"zzzzzzxzxxzx");
//        byte []sendData = new byte[1];
//        sendData[0]=SBGCProtocol.CMD_BOARD_INFO;
//
//        bt.send(sendData,true);

    }

    @Override
    public void encodePacket() {

    }

    @Override
    public void decodePacket() {

    }


}
