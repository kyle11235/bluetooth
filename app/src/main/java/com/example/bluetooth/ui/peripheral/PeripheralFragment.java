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
import com.example.bluetooth.service.ble.BlePeripheral;

public class PeripheralFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_peripheral, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // go to scan page
        Button btn = getActivity().findViewById(R.id.btnAdvertise);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView txt = getActivity().findViewById(R.id.txtPeripheral);
                txt.setText(txt.getText() + "\n" + "advertise");
                APP.log("advertise");

                BlePeripheral peripheral = new BlePeripheral(getContext());
                peripheral.advertise();

            }
        });

    }
}