package com.example.hf_lipomonitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;



public class MainActivity extends Activity {
	private EditText et;
	private TextView tv;
	private Context context;
	private BluetoothSPP bt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et = (EditText) findViewById(R.id.editText1);
		
		
		//temp code fragment using for tests
		context = getApplicationContext();
		bt = new BluetoothSPP(context);		
		initBT();
		
		
	}

	private void initBT() {
		if(!bt.isBluetoothEnabled()){
			setBluetooth(true);
			makeToast("Turning on Bluetooth...");
		}
	}
	
	@Override	
	protected void onDestroy(){
		super.onDestroy();
		setBluetooth(false);
		makeToast("TurnedOFF Bluetooth");
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
		
		switch (item.getItemId()){
		case R.id.btSearch:
			
			Toast toast = Toast.makeText(context, "DUAP", 2);
			toast.show();			
			return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
	private static boolean setBluetooth(boolean enable) {
	    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    boolean isEnabled = bluetoothAdapter.isEnabled();
	    if (enable && !isEnabled) {
	        return bluetoothAdapter.enable(); 
	    }
	    else if(!enable && isEnabled) {
	        return bluetoothAdapter.disable();
	    }
	    // No need to change bluetooth state
	    return true;
	}
	
	private void findAndSelectBTDevice(){
		bt.startService(BluetoothState.DEVICE_OTHER);
		
	}
	
	//function mostly for debugging
	public void makeToast(String text){
		Context context = getApplicationContext();		
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
