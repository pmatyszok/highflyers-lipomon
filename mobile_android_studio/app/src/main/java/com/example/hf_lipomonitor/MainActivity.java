package com.example.hf_lipomonitor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends Activity {
	private EditText et;
	private TextView tv;
	private Button sendButton;
	private Context context;
	public BluetoothSPP bt;
	private String LOG = "Bluetooth Application: ";

    private final List<TextView> tvCellValue = new ArrayList<TextView>(3);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		et = (EditText) findViewById(R.id.editText1);
		sendButton = (Button) findViewById(R.id.sendButton);
		tv = (TextView) findViewById(R.id.textView1);

        tvCellValue.add(0, (TextView) findViewById(R.id.tvCell1Value));
        tvCellValue.add(1, (TextView) findViewById(R.id.tvCell2Value));
        tvCellValue.add(2, (TextView) findViewById(R.id.tvCell3Value));

        for(int i = 0; i < tvCellValue.size(); ++i) {
            if (tvCellValue.get(i) == null) {
                Log.e(LOG, "TextView at position " + i + " not found");
                return;
            }
        }

		context = getApplicationContext();
		bt = new BluetoothSPP(context);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!bt.isBluetoothEnabled()) {
			bt.enable();
			Log.e(LOG, "bt.enable() method works");

		} else {
			if (!bt.isServiceAvailable()) {
				bt.setupService();
				bt.startService(BluetoothState.DEVICE_OTHER);
				Log.e(LOG, "!bt.isServiceAvailable condition works");
			}
		}
		bluetoothConnectionStatus();
		bt.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(byte[] data, String message) {
				
				/*tv.setText("");
				tv.setText(message);
				Log.i(LOG, message);
				Log.i(LOG, data.toString());*/
                Log.d(LOG, "Message received");

                try {
                    Integer cell = Integer.parseInt(message.substring(1, 2));
                    tvCellValue.get(cell).setText(message);
                } catch(NumberFormatException | IndexOutOfBoundsException ex) {
                    Log.e(LOG, ex.getMessage());
                }
            }
		});
		
		sendButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String etString = et.getText().toString();
				//TODO investigate sending LF and CR (second send method param)
				bt.send(etString, true);
                Log.i(LOG, "Message sent");
			}
		});
	}
	

	private void bluetoothConnectionStatus() {
		bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
			public void onDeviceConnected(String name, String address) {
				// Do something when successfully connected
				makeToast(getString(R.string.devices_connected));
			}

			public void onDeviceDisconnected() {
				// Do something when connection was disconnected
				makeToast(getString(R.string.devices_disconnected));
			}

			public void onDeviceConnectionFailed() {
				// Do something when connection failed
				makeToast(getString(R.string.connection_failed));
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		
		bt.stopService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
		case R.id.btSearch:
			findAndSelectBTDevice();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void findAndSelectBTDevice() {
		Intent intent = new Intent(this, DeviceList.class);
		startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
			if (resultCode == Activity.RESULT_OK)

				bt.connect(data);
		} else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				bt.setupService();
			} else {
				makeToast(getString(R.string.bluetooth_not_enabled));
				finish();
			}
		}

	}

	public void makeToast(String text) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
