package com.mwitas.highflyers.lipomon;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.simonguest.btxfr.ServerThread;

import java.util.Set;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class BluetoothManager extends Application {
    private static String TAG = "HF/LipoMon/BTmgr";

    protected static BluetoothAdapter adapter;
    protected static Set<BluetoothDevice> pairedDevices;
    protected static Handler serverHandler;
    protected static ServerThread serverThread;

    @Override
    public void onCreate() {
        super.onCreate();

        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (adapter.isEnabled()) {
                pairedDevices = adapter.getBondedDevices();
            } else {
                Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Bluetooth is not enabled");
            }
        } else {
            Toast.makeText(this, "Bluetooth adapter not found", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Bluetooth adapter not found");
        }
    }
}
