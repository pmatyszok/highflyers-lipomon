package com.highflyers.mwitas.lipomon.model;

import java.util.List;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public interface ICellDataSource {
    List<ICellData> getCells();
    ICellData getCell(int cellId);
    int getCellsCount();
    void close();
}
