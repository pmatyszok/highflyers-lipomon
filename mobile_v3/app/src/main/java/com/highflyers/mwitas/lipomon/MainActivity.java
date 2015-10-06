package com.highflyers.mwitas.lipomon;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.highflyers.mwitas.lipomon.exception.ViewNotFoundException;
import com.highflyers.mwitas.lipomon.mock.CellDataMock;
import com.highflyers.mwitas.lipomon.model.Helper;
import com.highflyers.mwitas.lipomon.model.ICellDataSource;
import com.highflyers.mwitas.lipomon.model.adapters.ListAdapter;

public class MainActivity extends AppCompatActivity {
    static ICellDataSource cellDataSource = null;
    static ListAdapter cellListAdapter = null;

    private static ListView lvCells = null;
    protected static String TAG = "LipoMonMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            lvCells = (ListView) Helper.getView(this, R.id.lvCells, "lvCells");
        } catch (ViewNotFoundException e) {
            handleError(e.getMessage());
            return;
        }

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

    private void updateList() {
        if (cellListAdapter != null)
            cellListAdapter.updateListAdapter(cellDataSource);
            //cellListAdapter.notifyDataSetChanged();
    }

    public static void handleError(String errorMsg) {
        Log.e(TAG, errorMsg);
        safeFinish();
    }

    private static void safeFinish() {
        if (cellDataSource != null) {
            cellDataSource.close();
            cellDataSource = null;
        }
    }
}
