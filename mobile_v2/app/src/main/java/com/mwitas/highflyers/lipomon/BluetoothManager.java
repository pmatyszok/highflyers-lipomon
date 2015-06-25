package com.mwitas.highflyers.lipomon;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.simonguest.btxfr.ClientThread;
import com.simonguest.btxfr.ServerThread;

import java.io.IOException;
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
    protected static Handler serverHandler;
    protected static Handler clientHandler;
    protected static ServerThread serverThread;
    protected static ClientThread clientThread;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            init();
        } catch (IOException ex) {
            // Do nothing
        }
    }

    private static void init() throws IOException
    {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                throw new IOException("Bluetooth is disabled");
            }
        } else {
            throw new IOException("Bluetooth adapter not found");
        }
    }

    public void RefreshManager() throws IOException
    {
        init();
    }

    public static Set<BluetoothDevice> getPairedDevices() throws IOException {
        init();
        return adapter.getBondedDevices();
    }
}
