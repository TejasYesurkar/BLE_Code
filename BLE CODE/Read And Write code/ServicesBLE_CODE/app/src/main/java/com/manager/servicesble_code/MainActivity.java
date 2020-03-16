package com.manager.servicesble_code;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.manager.servicesble_code.bluetoothDevice.ItemClickListener;
import com.manager.servicesble_code.bluetoothDevice.MyAdapter;
import com.manager.servicesble_code.bluetoothDevice.device;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<String> blue_list = new ArrayList<>();
    ArrayList<device> models = new ArrayList<>(  );
    private static final int REQUEST_ENABLE_BT = 2;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private Handler handler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private BluetoothAdapter mBtAdapter = null;

    private BLEService mServiceInt;
    public  boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        recyclerView = findViewById( R.id.recyclerview );

        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT); //The value of REQUEST_ENABLE_BT =2
            //start bluetooth if it not enable
        }
        CheckPermission();
        service_Start();


    }



    private void service_Start() {

        Intent bindIntent = new Intent(this, BLEService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            Log.d(getString( R.string.TAG ),"Serives Sorted");
            mServiceInt = ((BLEService.LocalBinder)rawBinder).getService();
            if (!mServiceInt.initialize()) {
                Log.e(getString( R.string.TAG ), "Unable to initialize Bluetooth");
                finish();
            }

        }
        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mServiceInt = null;
        }
    };

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
                        // updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
                        // updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
               // clearUI();
            } else if (BLEService. ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                        // displayGattServices(BLEService.getSupportedGattServices());
            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                        //displayData(intent.getStringExtra(BLEService.EXTRA_DATA));
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BLEService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
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


    private void addDevice(BluetoothDevice dev, int rssi) {
        if (blue_list.contains( dev.getAddress().trim() ) == false) {
            blue_list.add( dev.getAddress().trim() );


            device m = new device();


            m = new device();
            m.setName( dev.getName());
            m.setAddress(dev.getAddress());
            m.setState( String.valueOf( dev.getBondState() ) );

            models.add(m);

            recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

            myAdapter = new MyAdapter( this,models,this );
            recyclerView.setAdapter( myAdapter );
        }
    }


    @Override
    public void onGetAddress(String string) {

        // Toast.makeText( MainActivity.this, ""+blue_list.get( position ), Toast.LENGTH_SHORT ).show();
     //   BluetoothDevice blue_device = mBtAdapter.getRemoteDevice( string );

        Log.d(getString( R.string.TAG ),"Select Device Address Pass to Connect Gatt"+string);

       if( mServiceInt.connectDevice( string ))
       {
           Toast.makeText( mServiceInt, "Connected Successfully", Toast.LENGTH_SHORT ).show();
       }
    }

    public void sendStr(View view) {

        byte [] value;
        String message ="LED_1_" + "ON";
        try {

            //send data to service
            value = message.getBytes("UTF-8");
            mServiceInt.writeRXCharacteristic(value);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
