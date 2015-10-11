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
import java.util.regex.Pattern;

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

    private static Pattern regex = Pattern.compile("\\r?\\n");


    public CellDataSource(BtManager btManager, Handler handler) throws InputStreamException {
        try {
            inStream = btManager.getInputStream();
            msgHandler = handler;

            if (inStream != null) {
                new Thread(new BtHandler(inStream, msgHandler)).start();
            } else {
                throw new InputStreamException();
            }

            cells = new ArrayList<>(6);

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
        if (cellId > getCellsCount()) {
            return CellData.INVALID_CELL;
        } else {
            return cells.get(cellId);
        }
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

    private ICellData getCellWithAdd(int cellId) {
        for(int i=0; cellId > cells.size()-1; i++) {
            cells.add(i, new CellData(i));
        }

        return cells.get(cellId);
    }

    private StringBuilder dispatchMessage(StringBuilder message) {
        int lfPosition = message.indexOf("\n");
        if (lfPosition != -1) {
            String[] parts = regex.split(message.toString());

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

    private static final int increment = 25;  // Magic constant from Firmware project
    private static final float multiplier = 10000.0f;  // Magic constant from Firmware project

    private void parseMessage(String msg) {
        final int PART_CELL = 1;
        final int PART_VOLTAGE = 2;
        int state = 0;
        int actualCell = -1;

        StringBuilder sb = null;

        for (int i=0; i<msg.length(); i++) {  // Drop LF character
            char c = msg.charAt(i);

            if (c == 'c') {
                state = PART_CELL;
                sb = new StringBuilder(2);
                continue;
            } else if (state == PART_CELL && c == 'v') {
                if (sb != null) {
                    try {
                        actualCell = Integer.parseInt(sb.toString());
                    } catch (NumberFormatException ignored) {
                        actualCell = -1;
                    }
                }

                state = PART_VOLTAGE;
                sb = new StringBuilder(4);
                continue;
            }

            if (sb != null) {
                switch (state) {
                    case PART_CELL:
                        sb.append(c);
                        break;
                    case PART_VOLTAGE:
                        sb.append(String.format("%02x", (int)c));   // Append as hex
                        break;
                }
            }
        }

        if (sb != null && actualCell != -1) {
            try {
                Integer voltage_in = Integer.parseInt(sb.toString(), 16);
                float voltage = (float) (voltage_in * increment) / multiplier;

                ICellData cell = getCellWithAdd(actualCell);
                if (!cell.equals(CellData.INVALID_CELL)) {
                    cell.setVoltage(voltage);
                }
            } catch (NumberFormatException ignored) {}
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
