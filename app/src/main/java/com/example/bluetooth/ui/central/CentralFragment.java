package com.example.bluetooth.ui.central;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
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
import com.example.bluetooth.service.ble.BleActivity;
import com.example.bluetooth.service.ble.BleBase;
import com.example.bluetooth.service.ble.BleClient;
import com.example.bluetooth.service.ble.BlePresenter;


public class CentralFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_central, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextView txt = getActivity().findViewById(R.id.txtCentral);

        // go to scan page
        Button btn = getActivity().findViewById(R.id.btnGoScan);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                APP.log("go scan");
                txt.setText(txt.getText() + "\n" + "go scan");

                startActivity(new Intent(getActivity(), BleActivity.class));

            }
        });

        // write
        Button btnWrite = getActivity().findViewById(R.id.btnWrite);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                APP.log("read");
                txt.setText(txt.getText() + "\n" + "read");

                BleClient client = new BleClient(getContext(), new BleBase.Listener() {
                    @Override
                    public void onBlueEvent(String type, Object object) {
                        APP.log("onBlueEvent, type=" + type + "object=" + object.toString());
                        txt.setText(txt.getText() + "\n" + "onBlueEvent, type=" + type + "object=" + object.toString());
                    }
                });
                BlePresenter.sendMessage("hello world");
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        APP.log("onStart");
        BlePresenter.displayDetails();
    }


}