package com.mwitas.highflyers.lipomon;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.simonguest.btxfr.ClientThread;
import com.simonguest.btxfr.MessageType;
import com.simonguest.btxfr.ServerThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private static String TAG = "HF/LipoMon/MainActivity";

    private static Context context;
    private int noOfPairedDevices = 0;

    private CircularBuffer<MonitorEntry> entries = new CircularBuffer<>(150);
    private boolean serverStarted = false;

    protected Button btnConnect;
    protected Button btnRefresh;
    protected Spinner spnDevicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);

        context = this;

        try {
            initializeServerThread();
            serverStarted = true;
        } catch(IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            serverStarted = false;
        }

        setBluetoothHandlers();

        fillDeviceDataList();
        bindButtons();
        setButtons();
    }

    private void setBluetoothHandlers() {
        BluetoothManager.serverHandler = new ServerHandler();
        BluetoothManager.clientHandler = new ClientHandler();
    }

    private void setButtons() {
        boolean connectEnabled = (noOfPairedDevices != 0);
        btnConnect.setEnabled(connectEnabled);
    }

    private void bindButtons() {
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        if (btnConnect != null && btnRefresh != null) {
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO - connect button handler
                    DeviceData deviceData = (DeviceData) spnDevicesList.getSelectedItem();
                    for (BluetoothDevice device : BluetoothManager.adapter.getBondedDevices()) {
                        if (device.getAddress().contains(deviceData.getValue())) {
                            Log.v(TAG, "Starting client thread");
                            if (BluetoothManager.clientThread != null) {
                                BluetoothManager.clientThread.cancel();
                            }
                            BluetoothManager.adapter.cancelDiscovery();
                            BluetoothManager.clientThread = new ClientThread(device, BluetoothManager.clientHandler);
                            BluetoothManager.clientThread.start();
                        }
                    }
                }
            });

            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fillDeviceDataList();
                    setButtons();
                }
            });
        } else {
            Log.e(TAG, "Spinner buttons not found");
        }
    }

    private void fillDeviceDataList() {
        ArrayList<DeviceData> deviceDataList = new ArrayList<>();
        int devicesFound = 0;

        try {
            Set<BluetoothDevice> pairedDevices = BluetoothManager.getPairedDevices();
            devicesFound = pairedDevices.size();

            for (BluetoothDevice device : pairedDevices) {
                deviceDataList.add(new DeviceData(device.getName(), device.getAddress()));
            }
        } catch (IOException e) {
            deviceDataList.add(DeviceData.NO_PAIRED_DEVICES);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        spnDevicesList = (Spinner) findViewById(R.id.spn_devices_list);
        if (spnDevicesList != null) {
            ArrayAdapter<DeviceData> deviceArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, deviceDataList);
            deviceArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spnDevicesList.setAdapter(deviceArrayAdapter);
        }

        noOfPairedDevices = devicesFound;
    }

    private boolean initializeServerThread() throws IOException {
        Set<BluetoothDevice> pairedDevices = BluetoothManager.getPairedDevices();

        if (pairedDevices != null && !pairedDevices.isEmpty())
        {
            if (BluetoothManager.serverThread == null) {
                BluetoothManager.serverThread = new ServerThread(BluetoothManager.adapter, BluetoothManager.serverHandler);
                BluetoothManager.serverThread.start();
            }
        } else {
            throw new IOException(getString(R.string.bt_no_paired_Devices_found));
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class DeviceData {
        public static final DeviceData NO_PAIRED_DEVICES = new DeviceData("No paired devices found",
                "00-00-00-00-00-00");

        public DeviceData(String spinnerText, String value) {
            this.spinnerText = spinnerText;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return spinnerText;
        }

        String spinnerText;
        String value;
    }

    class ServerHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch(message.what) {
                case MessageType.DATA_RECEIVED: {
                    Toast.makeText(context, "Data received " + message.toString(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String msg = null;
            switch (message.what) {
                case MessageType.READY_FOR_DATA: {
                    msg = "Ready for data";
                    break;
                }

                case MessageType.COULD_NOT_CONNECT: {
                    msg = "Could not connect to the paired device";
                    break;
                }

                case MessageType.SENDING_DATA: {
                    msg = "Sending data";
                    break;
                }

                case MessageType.DATA_SENT_OK: {
                    msg = "Data sent ok";
                    break;
                }
            }

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
