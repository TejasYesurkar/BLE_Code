package com.manager.servicesble_code;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private Handler handler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private int mState = UART_PROFILE_DISCONNECTED;

    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;
    private static ArrayAdapter<String> listAdapter;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();


        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT); //The value of REQUEST_ENABLE_BT =2
            //start bluetooth if it not enable
        }
        CheckPermission();
    }

    private void CheckPermission()
    {
//Check Permssion is Allow or not
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getApplicationContext(),"Persion not Granted",Toast.LENGTH_SHORT).show();

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION))
            {
                Toast.makeText(getApplicationContext(),"You required this permission to run this app",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,new String[] {ACCESS_FINE_LOCATION},1);
            }
            else
            {
                // ActivityCompat.requestPermissions(this,new String[] {ACCESS_FINE_LOCATION},1);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Persion Granted",Toast.LENGTH_SHORT).show();
        }


    }
    public void startSer(View view) {

handler = new Handler(  );
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBtAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);// SCANNING period 10 sec

            mBtAdapter.startLeScan(leScanCallback);

    }

    public void stopSer(View view) {
        mBtAdapter.stopLeScan(leScanCallback);
    }

    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {

                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {

                            //Active Device Information 
                            Log.d( "Device Discovered", "<<<<<<<<<<<<<<<<<" );
                            if (device.getName() != null) {
                                Log.d( "Device Name", device.getName() );
                                Log.d( "Device Address", device.getAddress() );
                                Log.d( ">>>>>>>>>>>>>>>>>", "<<<<<<<<<<<<<<<<<<<" );
                                addDevice( device, rssi );

                            }
                        }
                    } );
                }
            };


    private void addDevice(BluetoothDevice device, int rssi) {

    }
}
