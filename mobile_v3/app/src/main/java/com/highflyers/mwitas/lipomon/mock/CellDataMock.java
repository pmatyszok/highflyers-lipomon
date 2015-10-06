package com.highflyers.mwitas.lipomon.mock;

import android.os.Handler;
import android.os.Message;

import com.highflyers.mwitas.lipomon.model.CellData;
import com.highflyers.mwitas.lipomon.model.ICellData;
import com.highflyers.mwitas.lipomon.model.ICellDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class CellDataMock implements ICellDataSource {
    private int noOfCells = 0;
    private List<ICellData> cells = null;
    private Handler handler;

    private DataGenerator generator = null;

    public CellDataMock(int noOfCells, Handler updateHandler) {
        this.noOfCells = noOfCells;
        this.handler = updateHandler;

        cells = new ArrayList<>(noOfCells);

        for(int i=0; i<noOfCells; i++) {
            cells.add(new CellData(i));
        }

        generator = new DataGenerator(1000, 3.0f, 3.2f);
        generator.start();
    }

    @Override
    public List<ICellData> getCells() {
        return cells;
    }

    @Override
    public ICellData getCell(int cellId) {
        return cells.get(cellId);
    }

    @Override
    public int getCellsCount() {
        if (cells == null) {
            return 0;
        } else {
            return cells.size();
        }
    }

    @Override
    public void close() {
        generator.signalStop();
        generator.interrupt();
        generator = null;
    }

    class DataGenerator extends Thread {
        private int refreshTime = 1000;
        private float minValue, maxValue;

        private Random rand = new Random();

        private boolean active = true;

        public DataGenerator(int refreshTime, float minValue, float maxValue) {
            this.refreshTime = refreshTime;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public void run() {
            while(active) {
                generateData();

                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);

                try {
                    Thread.sleep(refreshTime);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        public void signalStop() {
            active = false;
        }

        private void generateData() {
            for(int i = 0; i < noOfCells; i++) {
                float val = rand.nextFloat() * (maxValue - minValue) + minValue;
                cells.get(i).setVoltage(val);
            }
        }
    }
}
