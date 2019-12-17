package com.example.bluetooth.ui.peripheral;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bluetooth.APP;
import com.example.bluetooth.R;
import com.example.bluetooth.service.ble.BlePeripheral;

public class PeripheralFragment extends Fragment {

    private PeripheralViewModel peripheralViewModel;

    private Button btn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        peripheralViewModel = ViewModelProviders.of(this).get(PeripheralViewModel.class);
        View root = inflater.inflate(R.layout.fragment_peripheral, container, false);
        final TextView textView = root.findViewById(R.id.text);
        peripheralViewModel.getText().observe(this, new Observer<String>() {
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
        btn = getActivity().findViewById(R.id.btnAdvertise);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APP.log("onClick");
                BlePeripheral peripheral = new BlePeripheral(getContext());
                peripheral.advertise();
            }
        });

    }
}