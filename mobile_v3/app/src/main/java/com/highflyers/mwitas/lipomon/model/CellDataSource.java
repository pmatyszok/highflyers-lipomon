package com.highflyers.mwitas.lipomon.model;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.highflyers.mwitas.lipomon.exception.InputStreamException;
import com.highflyers.mwitas.lipomon.model.bt.BtManager;
import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtInvalidStateException;
import com.highflyers.mwitas.lipomon.model.constants.MessageHandlerConts;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class CellDataSource implements ICellDataSource {
    private InputStream inStream = null;
    private List<ICellData> cells = null;
    private BtHandler btHandler = null;
    private Handler msgHandler = null;


    public CellDataSource(BtManager btManager, Handler handler) throws InputStreamException {
        try {
            inStream = btManager.getInputStream();
            msgHandler = handler;

            if (inStream != null) {
                new Thread(new BtHandler(inStream, msgHandler)).start();
            } else {
                throw new InputStreamException();
            }

        } catch (BtInvalidStateException e) {
            cells.add(CellData.INVALID_CELL);
            close();
        }
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
        return (cells == null) ? 0 : cells.size();
    }

    @Override
    public void close() {
        if (btHandler != null) {
            btHandler.signalStop();
            btHandler = null;
        }

        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException ignored) {}
            inStream = null;
        }
    }

    private StringBuilder dispatchMessage(StringBuilder message) {
        int lfPosition = message.indexOf("\n");
        if (lfPosition != -1) {
            String[] parts = message.toString().split("\\r?\\n", 1);

            message = (parts.length > 1) ? new StringBuilder(parts[1]) : new StringBuilder(10);

            parseMessage(parts[0]);
            triggerUiUpdate();
        }

        return message;
    }

    private void triggerUiUpdate() {
        if (msgHandler != null) {
            Message m = new Message();
            m.what = MessageHandlerConts.LIST_UPDATED;
            msgHandler.sendMessage(m);
        }
    }

    private void parseMessage(String msg) {
        if (msg.contains("set")) {
            cells = new ArrayList<>(1);
            CellData data = new CellData(1);
            data.setVoltage(1.1f);
            cells.add(data);
        }
    }

    class BtHandler implements Runnable {
        Handler handler = null;
        InputStream inStream = null;

        boolean shouldRun = true;

        public BtHandler(InputStream inStream, Handler handler) {
            this.handler = handler;
            this.inStream = inStream;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[512];
            int bytesRead;
            StringBuilder message = new StringBuilder(10);

            while (shouldRun) {
                try {
                    bytesRead = inStream.read(buffer);

                    if (bytesRead > 512)
                        continue;

                    String received = new String(buffer, 0, bytesRead);
                    message.append(received);
                    Log.i("BtMsgChr", received);
                } catch (IOException e) {
                    continue;
                }

                message = dispatchMessage(message);
            }

            closeConnection();
        }

        private void closeConnection() {
            handler = null;

            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ignored) {}

                inStream = null;
            }
        }

        public void signalStop() {
            shouldRun = false;
        }
    }


}
