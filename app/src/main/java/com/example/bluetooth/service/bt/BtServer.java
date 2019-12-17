package com.example.bluetooth.service.bt;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BtServer extends BtBase {

    private static final String SERVICE_NAME = "BtServer";
    private BluetoothServerSocket serverSocket;
    private Boolean isServing = false;

    public BtServer(Listener listener) {
        super(listener);
    }

    public void serve() {
        isServing = true;
        try {
            // serverSocket = adapter.listenUsingRfcommWithServiceRecord(TAG, SPP_UUID); //加密传输，Android强制执行配对，弹窗显示配对码
            serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(SERVICE_NAME, SPP_UUID); //明文传输(不安全)，无需配对

            super.EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        log("BtServer -> serve");
                        BluetoothSocket socket = serverSocket.accept(); // 监听连接
                        serverSocket.close(); // 关闭监听，只连接一个设备, call serve again if listener got event - DISCONNECTED
                        loopRead(socket); // 循环读取
                    } catch (Throwable e) {
                        close();
                    }
                }
            });
        } catch (Throwable e) {
            log(e.getMessage());
            close();
        }
    }

    public Boolean isServing(){
        return isServing;
    }

    @Override
    public void close() {
        super.close();
        try {
            log("BtServer -> close");
            serverSocket.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        isServing = false;
    }
}