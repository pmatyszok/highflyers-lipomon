package com.highflyers.mwitas.lipomon.model.bt;

import android.bluetooth.BluetoothDevice;

import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtInvalidStateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class DevicesDataSource implements IDevicesDataSource {

    private BtManager btManager = null;

    public DevicesDataSource(BtManager btManager) {
        this.btManager = btManager;
    }

    @Override
    public List<DeviceData> getList() {
        List<DeviceData> result = null;

        try {
            Set<BluetoothDevice> pairedDevices = btManager.getPairedDevices();
            if (pairedDevices.size() == 0) {
                result = new ArrayList<>(1);
                result.add(DeviceData.NO_PAIRED_DEVICES);
                return result;
            }

            result = new ArrayList<>(pairedDevices.size());

            for (BluetoothDevice d : pairedDevices) {
                result.add(new DeviceData(d.getName(), d.getAddress()));
            }

            return result;
        } catch (BtInvalidStateException e) {
            result = new ArrayList<>(1);
            result.add(DeviceData.UNKNOWN_ERROR);

            return result;
        }
    }
}
