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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bluetooth.APP;
import com.example.bluetooth.R;
import com.example.bluetooth.service.ble.BleCentral;
import com.example.bluetooth.service.ble.BlePeripheral;
import com.example.bluetooth.service.ble.service.BatteryService;

public class CentralFragment extends Fragment {

    private CentralViewModel centralViewModel;

    private Button btn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        centralViewModel = ViewModelProviders.of(this).get(CentralViewModel.class);
        View root = inflater.inflate(R.layout.fragment_central, container, false);
        final TextView textView = root.findViewById(R.id.text);
        centralViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // go to scan page
        btn = getActivity().findViewById(R.id.btnScan);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APP.log("onClick");
                BleCentral central = new BleCentral(getContext(), new BleCentral.Listener() {
                    @Override
                    public void onFoundDevice(BluetoothDevice device) {
                        APP.log("onFoundDevice, name=" + device.getName());
                    }
                });
                central.serve();
                central.addService(new BatteryService());
                central.scan();

            }
        });

    }
}