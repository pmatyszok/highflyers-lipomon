package com.highflyers.mwitas.lipomon.model;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public interface ICellData {
    Integer getId();
    Float getVoltage();
    void setVoltage(float voltage);
    boolean equals(Object o);
}
