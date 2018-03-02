package com.example.intagpc.bluetoothdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button btnBluetoothOn, btnBluetoothOff, btnFindPairedDevices, btnFindNearbyDevices;
    private int bluetoothConstant = 715;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> nearbyDevices;
    private ListView listViewDevices;
    private ArrayList arrayList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initWidgets();
        buttonListeners();
    }

    public void initWidgets() {

        btnBluetoothOn = (Button) findViewById(R.id.btnBtOn);
        btnBluetoothOff = (Button) findViewById(R.id.btnOff);
        btnFindPairedDevices = (Button) findViewById(R.id.btnPairedDevices);
        btnFindNearbyDevices = (Button) findViewById(R.id.btnFindDevices);
        listViewDevices = (ListView) findViewById(R.id.listViewDevices);

    }


    public void buttonListeners() {

        btnFindNearbyDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanNearbyBluetoothDevices();
            }
        });
        btnFindPairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findPairedDevices();
            }
        });

        btnBluetoothOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                turnOffBluetooth();

                Toast.makeText(MainActivity.this, "bluetooth is disabled", Toast.LENGTH_SHORT).show();

            }
        });

        btnBluetoothOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (MainActivity.this, Manifest.permission.BLUETOOTH)) {


                    } else {

                        ActivityCompat.requestPermissions
                                (MainActivity.this,
                                        new String[]{Manifest.permission.BLUETOOTH}, bluetoothConstant);

                    }

                } else {


                    onBluetooth();
                }
            }
        });

    }

    public void onBluetooth() {

        if (bluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Device does not support bluetooth", Toast.LENGTH_SHORT).show();

        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 0);
            } else {
                Toast.makeText(MainActivity.this, "bluetooth is enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void turnOffBluetooth() {

        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        } else {
            Toast.makeText(MainActivity.this, "bluetooth is already disabled", Toast.LENGTH_SHORT).show();

        }

    }

    public void findPairedDevices() {


        nearbyDevices = bluetoothAdapter.getBondedDevices();
        ArrayList arrayList = new ArrayList();
        for (BluetoothDevice bt : nearbyDevices) {

            arrayList.add(bt.getName());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listViewDevices.setAdapter(arrayAdapter);
    }

    public void scanNearbyBluetoothDevices() {

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    public final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                arrayList.add(bluetoothDevice.getName());

            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
            listViewDevices.setAdapter(arrayAdapter);
        }


    };
}