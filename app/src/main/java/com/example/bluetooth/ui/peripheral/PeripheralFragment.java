package com.example.bluetooth.ui.peripheral;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bluetooth.APP;
import com.example.bluetooth.R;
import com.example.bluetooth.service.ble.BleBase;
import com.example.bluetooth.service.ble.BlePeripheral;
import com.example.bluetooth.service.ble.BleServer;
import com.example.bluetooth.service.ble.service.BatteryService;

public class PeripheralFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_peripheral, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextView txt = getActivity().findViewById(R.id.txtPeripheral);

        // advertise
        Button btn = getActivity().findViewById(R.id.btnAdvertise);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open GATT server
                BleServer server = new BleServer(getContext(), new BleBase.Listener() {
                    @Override
                    public void onBlueEvent(String type, Object object) {
                        APP.log("onBlueEvent, type=" + type + ", object=" + object.toString());
                        txt.setText(txt.getText() + "\n" + object.toString());
                    }
                });
                server.addService(new BatteryService());
                txt.setText(txt.getText() + "\n" + "add service");
                APP.log("add service");

                // advertise
                BlePeripheral peripheral = new BlePeripheral(getContext(), null);
                peripheral.advertise();
                txt.setText(txt.getText() + "\n" + "advertise");
                APP.log("advertise");

            }
        });

    }
}