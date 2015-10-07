package com.highflyers.mwitas.lipomon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.highflyers.mwitas.lipomon.exception.ViewNotFoundException;
import com.highflyers.mwitas.lipomon.mock.CellDataMock;
import com.highflyers.mwitas.lipomon.model.Helper;
import com.highflyers.mwitas.lipomon.model.ICellDataSource;
import com.highflyers.mwitas.lipomon.model.adapters.ListAdapter;
import com.highflyers.mwitas.lipomon.model.bt.BtManager;
import com.highflyers.mwitas.lipomon.model.bt.DevicesDataSource;
import com.highflyers.mwitas.lipomon.model.bt.IDevicesDataSource;
import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtInvalidStateException;
import com.highflyers.mwitas.lipomon.model.bt.exceptions.BtNotSupportedException;

public class MainActivity extends AppCompatActivity {
    static ICellDataSource cellDataSource = null;
    static ListAdapter cellListAdapter = null;
    static IDevicesDataSource deviceDataSource = null;

    private static BtManager btManager = null;
    private static MainActivity activity = null;
    protected static String TAG = "LipoMonMainActivity";

    private static ListView lvCells = null;
    private static Spinner spDevices = null;

    private ActivityStateFlags activityStateFlags = new ActivityStateFlags();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        activityStateFlags.setStarted();

        getViews();

        initializeBt();
        enableBt();

        setDevicesSpinner();
        setCellsListView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (activityStateFlags.blockEnableIntent()) {
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
        cellDataSource = new CellDataMock(3, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    updateList();
                }
            }
        });

        cellListAdapter = new ListAdapter(this, cellDataSource);
        lvCells.setAdapter(cellListAdapter);
    }

    private void setDevicesSpinner() {
        deviceDataSource = new DevicesDataSource(btManager);
        spDevices.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,
                deviceDataSource.getList()));
    }

    private void getViews() {
        try {
            lvCells = (ListView) Helper.getView(this, R.id.lvCells, "lvCells");
            spDevices = (Spinner) Helper.getView(this, R.id.spDevices, "spDevices");
        } catch (ViewNotFoundException e) {
            handleError(this, e.getMessage());
        }
    }

    private void updateList() {
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

    class ActivityStateFlags {
        private boolean btEnable_returnFromActivity = false;
        private boolean btEnable_justStarted = false;
        private boolean spDevices_refresh = false;

        public boolean blockEnableIntent() {
            boolean actualState = (btEnable_returnFromActivity || btEnable_justStarted);
            btEnable_justStarted = false;
            btEnable_returnFromActivity = false;
            return !actualState;
        }

        public void setStarted() {
            btEnable_justStarted = true;
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
    }
}
