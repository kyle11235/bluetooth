package com.example.bluetooth.ui.server;

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
import com.example.bluetooth.service.bt.BtBase;
import com.example.bluetooth.service.bt.BtServer;

public class ServerFragment extends Fragment {

    private ServerViewModel serverViewModel;
    private Button btn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        serverViewModel = ViewModelProviders.of(this).get(ServerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_server, container, false);
        final TextView textView = root.findViewById(R.id.text);
        serverViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    // only after main activity is created, you can find view
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // create server
        btn = getActivity().findViewById(R.id.btnStart);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtServer btServer = new BtServer(new BtBase.Listener() {
                    @Override
                    public void onBlueEvent(String type, Object object) {
                        APP.log("onBlueEvent, type=" + type + ", object=" + object.toString());
                    }
                });
                btServer.serve();
            }
        });

    }


}