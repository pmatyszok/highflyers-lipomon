package com.mwitas.highflyers.lipomon;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class MonitorEntry {
    private static Integer STEPS = 2^10;
    private static Float MAX = 2.56f;
    private static Float DIV = MAX / STEPS;

    private Integer cellNo;
    private Integer adcValue;

    private Float voltage;
    private String recvTime;
    private String raw;

    public MonitorEntry(String rawData) {
        raw = rawData;

        // Format: c1v00ff
        cellNo = Integer.parseInt(rawData.substring(1, 2));
        adcValue = Integer.parseInt(rawData.substring(3, 7), 16);   // Parse HEX to INT

        voltage = adcValue * DIV;
        recvTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    @Override
    public String toString() {
        return raw;
    }

    public Float getVoltage() {
        return voltage;
    }

    public String getRecvTime() {
        return recvTime;
    }

    public Integer getCellNo() {
        return cellNo;
    }

    public Integer getAdcValue() {
        return adcValue;
    }
}
