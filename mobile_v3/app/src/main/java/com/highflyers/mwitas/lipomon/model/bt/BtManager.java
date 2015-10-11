package com.highflyers.mwitas.lipomon.model.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtInvalidStateException;
import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtNotSupportedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class BtManager {
    private static final String TAG = "BtManager";
    private static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter btAdapter = null;
    private BluetoothDevice btDevice = null;
    private BluetoothSocket btSocket = null;
    public BtState state = BtState.NOT_INITIALIZED;
    private InputStream inStream = null;

    public BtManager() throws BtNotSupportedException {
        if (!getAdapter())
            throw new BtNotSupportedException();

        state = BtState.READY;
    }

    public boolean assertReady() throws BtInvalidStateException {

        if (btAdapter == null || !state.equals(BtState.READY)) {
            throw new BtInvalidStateException(state);
        }

        return true;
    }

    private boolean getAdapter() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            state = BtState.NOT_SUPPORTED;
            return false;
        }

        return true;
    }

    public boolean isEnabled() throws BtInvalidStateException {
        assertReady();
        return btAdapter.isEnabled();
    }

    public boolean requestEnable(Activity activity) throws BtInvalidStateException {
        assertReady();

        if (!isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        return isEnabled();
    }

    public Set<BluetoothDevice> getPairedDevices() throws BtInvalidStateException {
        assertReady();

        return btAdapter.getBondedDevices();
    }

    public void close() {
        closeConnection();
    }

    public void closeConnection() {
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException ignored) {}

            inStream = null;
        }
        if (btSocket != null) {
            closeSocket();
            btSocket = null;
        }

        if (btDevice != null) {
            btDevice = null;
        }
    }

    private void closeSocket() {
        if (btSocket != null) {
            if (btSocket.isConnected()) {
                try {
                    btSocket.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public boolean connectToDevice(String address) throws BtInvalidStateException, IOException {
        assertReady();

        btDevice = btAdapter.getRemoteDevice(address);
        btSocket = btDevice.createRfcommSocketToServiceRecord(SSP_UUID);

        btAdapter.cancelDiscovery();    // Connecting while discovery is impossible

        if (btSocket == null) {
            Log.e(TAG, "Failed to create socket");
            return false;
        } else {
            try {
                btSocket.connect();
                return true;
            } catch (IOException ex) {
                state = BtState.READY;
                closeSocket();
            }
        }

        return false;
    }

    public InputStream getInputStream() throws BtInvalidStateException {
        assertReady();
        if (btSocket != null)
            try {
                if (inStream != null)
                    inStream.close();

                inStream = btSocket.getInputStream();
                return inStream;
            } catch (IOException e) {
                return null;
            }

        return null;
    }

    public static class BtState {
        public static final BtState NOT_SUPPORTED = new BtState(-1, "Bluetooth not supported");
        public static final BtState NOT_INITIALIZED = new BtState(0, "Bluetooth not initialized");
        public static final BtState READY = new BtState(1, "Bluetooth ok");
        public static final BtState CONNECTED = new BtState(2, "Connected to device");

        public int value;
        public String text;

        private BtState(int value, String text) {
            this.value = value;
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass().equals(this.getClass())) {
                BtState obj = (BtState) o;
                return (obj.value == this.value);
            }

            return false;
        }
    }
}
