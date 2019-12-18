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

import org.w3c.dom.Text;

public class ServerFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    // only after main activity is created, you can find view
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextView txt = getActivity().findViewById(R.id.txtServer);

        // create server
        Button btn = getActivity().findViewById(R.id.btnStart);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt.setText(txt.getText() + "\n" + "server is started");
                APP.log("server is started");

                BtServer btServer = new BtServer(new BtBase.Listener() {
                    @Override
                    public void onBlueEvent(String type, Object object) {
                        txt.setText(txt.getText() + "\n" + "onBlueEvent, type=" + type + ", object=" + object.toString());
                        APP.log("onBlueEvent, type=" + type + ", object=" + object.toString());
                    }
                });
                btServer.serve();

            }
        });

    }


}