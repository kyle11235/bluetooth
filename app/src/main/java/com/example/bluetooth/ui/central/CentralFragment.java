package com.example.bluetooth.ui.central;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bluetooth.APP;
import com.example.bluetooth.R;
import com.example.bluetooth.service.ble.BleCentral;
import com.example.bluetooth.service.ble.BleServer;
import com.example.bluetooth.service.ble.service.BatteryService;

public class CentralFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_central, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // go to scan page
        Button btn = getActivity().findViewById(R.id.btnScan);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView txt = getActivity().findViewById(R.id.txtCentral);
                txt.setText(txt.getText() + "\n" + "scan");
                APP.log("scan");

                // scan
                BleCentral central = new BleCentral(getContext(), new BleCentral.Listener() {
                    @Override
                    public void onFoundDevice(BluetoothDevice device) {

                        txt.setText(txt.getText() + "\n" + "onFoundDevice, name=" + device.getName());
                        APP.log("onFoundDevice, name=" + device.getName());

                        // add device to a list, let user to choose
                        // or connect one directly

                    }
                });
                central.scan();

                // GATT server on  peripheral / central side
                BleServer server = new BleServer(getContext());
                server.addService(new BatteryService());
                txt.setText(txt.getText() + "\n" + "add service");
                APP.log("add service");

            }
        });

    }
}