package com.highflyers.mwitas.lipomon.model;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class CellData implements ICellData {
    private int cellId;
    private float voltage = 0.0f;

    public static CellData INVALID_CELL = new CellData(-1);

    public CellData(int cellId) {
        this.cellId = cellId;
    }

    public Integer getId() {
        return cellId;
    }

    public Float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!this.getClass().equals(o.getClass())) {
            return false;
        } else {
            CellData obj = (CellData)o;
            if (this.cellId == obj.cellId) {
                return true;
            }
        }

        return false;
    }
}
