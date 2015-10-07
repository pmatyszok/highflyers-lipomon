package com.highflyers.mwitas.lipomon.model.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtInvalidStateException;
import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtNotSupportedException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class BtManager {
    private static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter btAdapter = null;
    public BtState state = BtState.NOT_INITIALIZED;

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
        // Do nothing
    }

    public static class BtState {
        public static final BtState NOT_SUPPORTED = new BtState(-1, "Bluetooth not supported");
        public static final BtState NOT_INITIALIZED = new BtState(0, "Bluetooth not initialized");
        public static final BtState READY = new BtState(1, "Bluetooth ok");

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
