package com.mwitas.highflyers.lipomon;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.simonguest.btxfr.MessageType;
import com.simonguest.btxfr.ServerThread;

import java.util.Set;


public class MainActivity extends ActionBarActivity {
    private static String TAG = "HF/LipoMon/MainActivity";

    CircularBuffer<MonitorEntry> entries = new CircularBuffer<>(150);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CreateLayout(Definitions.NumberOfCells);

        BluetoothManager.serverHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "New message received");
                switch (msg.what) {
                    case MessageType.DATA_RECEIVED: {
                        MonitorEntry e = new MonitorEntry((String)msg.obj);
                        entries.push(e);
                        Log.d(TAG, (String) msg.obj);
                    } break;
                }
            }
        };

        Set<BluetoothDevice> pairedDevices = BluetoothManager.pairedDevices;
        if (pairedDevices != null) {
            if (BluetoothManager.serverThread == null) {
                String msg = "Starting server thread. Able to accept data.";
                Log.v(TAG, msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                BluetoothManager.serverThread = new ServerThread(BluetoothManager.adapter, BluetoothManager.serverHandler);
                BluetoothManager.serverThread.start();
            }
        } else {
            String msg = "No paired devices found";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            Log.i(TAG, msg);
        }
    }

    private void CreateLayout(Integer numberOfCells) {
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);

        for (Integer i=0; i < numberOfCells; ++i) {
            LinearLayout children = new LinearLayout(this);
            TextView lbl_title_new = new TextView(this);
            TextView lbl_value_new = new TextView(this);

            children.setOrientation(LinearLayout.HORIZONTAL);
            children.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            lbl_title_new.setText("Cell " + i.toString());
            lbl_title_new.setPadding(0, 0, 15, 0);
            lbl_title_new.setId(numberOfCells + i);

            lbl_value_new.setText(getString(R.string.lbl_val_cell_placeholder));
            lbl_value_new.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large);
            lbl_value_new.setId(i);

            children.addView(lbl_title_new);
            children.addView(lbl_value_new);

            parent.addView(children);
        }

        addContentView(parent, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
