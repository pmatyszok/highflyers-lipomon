package com.highflyers.mwitas.lipomon;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.highflyers.mwitas.lipomon.exception.InputStreamException;
import com.highflyers.mwitas.lipomon.exception.ViewNotFoundException;
import com.highflyers.mwitas.lipomon.model.CellDataSource;
import com.highflyers.mwitas.lipomon.model.Helper;
import com.highflyers.mwitas.lipomon.model.ICellDataSource;
import com.highflyers.mwitas.lipomon.model.adapters.ListAdapter;
import com.highflyers.mwitas.lipomon.model.bt.BtManager;
import com.highflyers.mwitas.lipomon.model.bt.DeviceData;
import com.highflyers.mwitas.lipomon.model.bt.DevicesListDataSource;
import com.highflyers.mwitas.lipomon.model.bt.IDevicesListDataSource;
import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtInvalidStateException;
import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtNotSupportedException;
import com.highflyers.mwitas.lipomon.model.constants.MessageHandlerConts;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static ICellDataSource cellDataSource = null;
    static ListAdapter cellListAdapter = null;
    static IDevicesListDataSource deviceDataSource = null;

    private static BtManager btManager = null;
    private static MainActivity activity = null;
    protected static String TAG = "LipoMonMainActivity";

    private static ListView lvCells = null;
    private static Spinner spDevices = null;
    private static Button btnConnect = null;


    private ActivityStateFlags activityStateFlags = new ActivityStateFlags();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        activityStateFlags.setJustStarted();

        getViews();

        initializeBt();

        setDevicesSpinner();
        setConnectionButton();
    }

    private void setConnectionButton() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeviceSelected((DeviceData) spDevices.getSelectedItem());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!activityStateFlags.blockEnableIntent()) {
            try {
                if (!btManager.isEnabled())
                    enableBt();
            } catch (BtInvalidStateException e) {
                handleInvalidStateException(e);
            }
        }

        if (activityStateFlags.refreshSpDevices()) {
            setDevicesSpinner();
        }

        spDevices.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BtManager.REQUEST_ENABLE_BT) {
            try {
                if (!btManager.isEnabled()) {
                    handleError(this, getString(R.string.txt_cannot_run_with_bt_disabled));
                }
            } catch (BtInvalidStateException e) {
                handleInvalidStateException(e);
            }

            activityStateFlags.setReturnFromActivity();
            activityStateFlags.setRefreshSpDevices();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        safeFinish();
    }

    private void initializeBt() {
        try {
            btManager = new BtManager();
        } catch (BtNotSupportedException e) {
            handleError(this, getString(R.string.txt_bt_not_supported));
        }
    }

    private void enableBt() {
        try {
            if (!btManager.isEnabled()) {
                btManager.requestEnable(this);
            }
        } catch (BtInvalidStateException e) {
            handleInvalidStateException(e);
        }
    }

    private void setCellsListView() {
        //cellDataSource = new CellDataMock(3, new NewMessageHandler());
        try {
            cellDataSource = new CellDataSource(btManager, new NewMessageHandler());
        } catch (InputStreamException e) {
            handleError(this, getString(R.string.txt_unable_to_get_inputstream));
        }

        cellListAdapter = new ListAdapter(this, cellDataSource);
        lvCells.setAdapter(cellListAdapter);
    }

    private void setDevicesSpinner() {
        deviceDataSource = new DevicesListDataSource(btManager);
        spDevices.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,
                deviceDataSource.getList()));

        /*spDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onDeviceSelected((DeviceData) spDevices.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                onDeviceSelected(DeviceData.NO_PAIRED_DEVICES);
            }
        });*/
    }

    private void onDeviceSelected(DeviceData selectedItem) {
        btManager.closeConnection();

        try {
            if (!selectedItem.getAddress().equals(DeviceData.ZERO_ADDRESS)) {
                if (!btManager.connectToDevice(selectedItem.getAddress())) {
                    Toast.makeText(this, R.string.txt_unable_to_connect_to_device, Toast.LENGTH_LONG).show();
                    spDevices.performClick();
                    return;
                }
            } else{
                Toast.makeText(this, R.string.txt_unable_to_connect_to_device, Toast.LENGTH_LONG).show();
            }
        } catch (BtInvalidStateException e) {
            handleInvalidStateException(e);
        } catch (IOException e) {
            handleError(this, e.getMessage());
        }

        setCellsListView();
    }

    private void getViews() {
        try {
            lvCells = (ListView) Helper.getView(this, R.id.lvCells, "lvCells");
            spDevices = (Spinner) Helper.getView(this, R.id.spDevices, "spDevices");
            btnConnect = (Button) Helper.getView(this, R.id.btnConnect, "btnConnect");
        } catch (ViewNotFoundException e) {
            handleError(this, e.getMessage());
        }
    }

    private static void updateList() {
        if (cellListAdapter != null)
            cellListAdapter.refreshList();
    }

    public static void handleError(final Context context, String errorMsg) {
        Log.e(TAG, errorMsg);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.lbl_error_occurred)
                .setMessage(errorMsg)
                .setPositiveButton(R.string.btn_handle_error, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        MainActivity.safeFinish();
                    }
                })
                .show();
    }

    private void handleInvalidStateException(BtInvalidStateException ex) {
        handleError(this, getString(R.string.txt_invalid_bt_state_title) + ": " + ex.state.text);
    }

    private static void safeFinish() {
        if (cellDataSource != null) {
            cellDataSource.close();
            cellDataSource = null;
        }

        if (btManager != null) {
            btManager.close();
            btManager = null;
        }

        activity.finish();
    }

    class ActivityStateFlags {
        private boolean btEnable_returnFromActivity = false;
        private boolean spDevices_refresh = false;
        private boolean app_justStarted = false;

        public boolean blockEnableIntent() {
            boolean actualState = (btEnable_returnFromActivity);
            btEnable_returnFromActivity = false;
            return actualState;
        }

        public void setReturnFromActivity() {
            btEnable_returnFromActivity = true;
        }

        public void setRefreshSpDevices() {
            spDevices_refresh = true;
        }

        public boolean refreshSpDevices() {
            boolean actualState = spDevices_refresh;
            spDevices_refresh = false;
            return actualState;
        }

        public void setJustStarted() {
            app_justStarted = true;
        }

        public boolean justStarted() {
            return app_justStarted;
        }

        public void resetJustStarted() {
            app_justStarted = false;
        }
    }

    private static class NewMessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MessageHandlerConts.LIST_UPDATED) {
                MainActivity.updateList();
            }
        }
    }
}
