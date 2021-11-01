package com.example.bleutoothcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.OutputStream;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;


public class DeviceList extends AppCompatActivity {
    // widget
    Button listen, devices;
    ListView devicelist;
    ArrayList<String> stringArrayList=new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    SwitchCompat switchBtn;
    BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();

    // Bleutooth
    private BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> pairedDevices;
    //private Set<BluetoothDevice> pairedDevices1;
    private OutputStream outStream = null;
    public static String EXTRA_ADDRESS = "device_address";





    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();
        if (pairedDevices.size()>0 )
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
         else {
            Toast.makeText(getApplicationContext(), "No Bluetooth Devices Found in phone.", Toast.LENGTH_LONG).show(); }
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener);
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(DeviceList.this, well.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            startActivity(i);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listen = (Button)findViewById(R.id.DeviceBtn);
        devices = (Button)findViewById(R.id.DevBtn);
        devicelist =(ListView)findViewById(R.id.listview);
        switchBtn = findViewById(R.id.switchBtn);

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

               bAdapter.startDiscovery();

               Set<BluetoothDevice> bt= bAdapter.getBondedDevices();
               String[] strings= new String[bt.size()];
               int index = 0;
               if (bt.size()>0){
                   for (BluetoothDevice device : bt){
                       strings[index]=device.getName()+ "\n" + device.getAddress();

                       index++;
                   }
                   ArrayAdapter<String> array = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                   devicelist.setAdapter(array);
               }

               // pairedDevicesList(); //method that will be called

              //  arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList);
              //  devicelist.setAdapter(arrayAdapter);

                devicelist.setOnItemClickListener(myListClickListener);
            }
        });


        devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {



                pairedDevicesList(); //method that will be called

                arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList);
                devicelist.setAdapter(arrayAdapter);
                devicelist.setOnItemClickListener(myListClickListener);
            }
        });







        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver,intentFilter);
        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList);
        devicelist.setAdapter(arrayAdapter);

        bAdapter.disable();

        // On/Off Bleutooth
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchBtn.isChecked()){

                    if(bAdapter == null)
                    {
                        Toast.makeText(getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(!bAdapter.isEnabled()){
                            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1);
                            Toast.makeText(getApplicationContext(),"Bluetooth Turned ON",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                else{

                    bAdapter.disable();
                    Toast.makeText(getApplicationContext(),"Bluetooth Turned OFF", Toast.LENGTH_SHORT).show();

                }
            }
        });


        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        }
        else
        {
            if (myBluetooth.isEnabled())
            { }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }


    }

    BroadcastReceiver myReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {

                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                stringArrayList.add(device.getName()+ "\n" + device.getAddress());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

}
