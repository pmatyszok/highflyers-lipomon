package com.highflyers.mwitas.lipomon.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.highflyers.mwitas.lipomon.MainActivity;
import com.highflyers.mwitas.lipomon.R;
import com.highflyers.mwitas.lipomon.exception.ViewNotFoundException;
import com.highflyers.mwitas.lipomon.model.CellData;
import com.highflyers.mwitas.lipomon.model.Helper;
import com.highflyers.mwitas.lipomon.model.ICellData;
import com.highflyers.mwitas.lipomon.model.ICellDataSource;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class ListAdapter extends BaseAdapter {
    private Context context = null;
    private ICellDataSource dataSource = null;

    private static LayoutInflater inflater = null;

    TextView tvTitle = null;
    TextView tvValue = null;

    public ListAdapter(Context c, ICellDataSource dataSource) {
        this.context = c;
        this.dataSource = dataSource;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (dataSource == null) {
            return 0;
        } else {
            return dataSource.getCellsCount();
        }
    }

    @Override
    public Object getItem(int position) {
        if (dataSource == null || position >= dataSource.getCellsCount()) {
            return CellData.INVALID_CELL;
        } else {
            return dataSource.getCell(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (dataSource == null || position >= dataSource.getCellsCount()) {
            return CellData.INVALID_CELL.getId();
        } else {
            return dataSource.getCell(position).getId();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.cell_entry, null);

        try {
            tvTitle = (TextView) Helper.getView(vi, R.id.tvTitle, "TextView title");
            tvValue = (TextView) Helper.getView(vi, R.id.tvValue, "TextView value");
        } catch (ViewNotFoundException e) {
            MainActivity.handleError(context, e.getMessage());
        }

        ICellData item = (ICellData) getItem(position);

        /*if (item.equals(CellData.INVALID_CELL)) {
            MainActivity.handleError(context, "Invalid cell requested");
        } else {*/
            ListEntry entry = new ListEntry((ICellData) getItem(position));

            if (tvTitle != null && tvValue != null) {
                tvTitle.setText(entry.title);
                tvValue.setText(entry.value);
            }
        //}

        return vi;
    }

    public void refreshList() {
        notifyDataSetChanged();
    }

    public void updateListAdapter(ICellDataSource dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    class ListEntry {
        public String title;
        public String value;

        public ListEntry(ICellData data) {
            title = context.getString(R.string.lbl_cell_no) +  data.getId().toString();
            value = String.format("%.4f", data.getVoltage()) + context.getString(R.string.lbl_voltage_suffix);
        }
    }
}
