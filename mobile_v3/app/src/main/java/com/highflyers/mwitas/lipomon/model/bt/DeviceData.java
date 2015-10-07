package com.highflyers.mwitas.lipomon.model.bt;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class DeviceData {
    private String spinnerText;
    private String address;

    public static final String ZERO_ADDRESS = "00-00-00-00-00-00";

    public static final DeviceData NO_PAIRED_DEVICES = new DeviceData("No paired devices found",
            DeviceData.ZERO_ADDRESS);

    public static final DeviceData UNKNOWN_ERROR = new DeviceData("Unknown error occurred",
            DeviceData.ZERO_ADDRESS);



    public DeviceData(String spinnerText, String address) {
        this.spinnerText = spinnerText;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String toString() {
        return spinnerText;
    }
}
