package com.example.bluetooth.service.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetooth.APP;
import com.example.bluetooth.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class BleItemAdapter extends RecyclerView.Adapter<BleItemAdapter.ItemHolder> {


    interface ItemClickListener {
        public void onItemClick(BluetoothDevice item);
    }

    private ItemClickListener listener;
    private Activity context;
    private final List<BluetoothDevice> list = new ArrayList<>();

    public BleItemAdapter(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder itemHolder, int position) {
        final BluetoothDevice device = list.get(position);
        String name = device.getName();
        String address = device.getAddress();
        int bondState = device.getBondState();
        itemHolder.txtName.setText(name == null ? "" : name);
        itemHolder.txtAddress.setText(String.format("%s (%s)", address, bondState == 10 ? APP.getStr(R.string.blue_no_pair) : APP.getStr(R.string.blue_ok_pair)));

        itemHolder.line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(device);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(Set<BluetoothDevice> deviceSet) {
        if (deviceSet != null) {
            list.clear();
            list.addAll(deviceSet);
        }
        notifyDataSetChanged();
    }


    public void add(BluetoothDevice device) {
        if (list.contains(device.getAddress()))
            return;
        list.add(device);
        notifyDataSetChanged();
    }

    public void addItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        final LinearLayout line;
        final TextView txtName;
        final TextView txtAddress;

        ItemHolder(final View itemView) {
            super(itemView);
            line = itemView.findViewById(R.id.line);
            txtName = itemView.findViewById(R.id.txtName);
            txtAddress = itemView.findViewById(R.id.address);
        }
    }
}