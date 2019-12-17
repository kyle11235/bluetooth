package com.example.bluetooth.service.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.util.Log;

import com.example.bluetooth.APP;
import com.example.bluetooth.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class BtBase {

    public static final String TAG = "HOOK";

    // well-known SPP UUID for connecting to a Bluetooth serial board
    static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluetooth/";

    public interface Listener {

        String NO_ADAPTER = "NO_ADAPTER";

        String CONNECTING = "CONNECTING";
        String CONNECTED = "CONNECTED";
        String DISCONNECTED = "DISCONNECTED";

        String RECEIVE_MESSAGE = "RECEIVE_MESSAGE";
        String SEND_MESSAGE = "SEND_MESSAGE";

        String RECEIVE_FILE = "RECEIVE_FILE";
        String RECEIVE_FILE_FINISHED = "RECEIVE_FILE_FINISHED";

        String HEADSET_CONNECTED = "HEADSET_CONNECTED";
        String HEADSET_DISCONNECTED = "HEADSET_DISCONNECTED";

        String A2DP_CONNECTED = "A2DP_CONNECTED";
        String A2DP_DISCONNECTED = "A2DP_DISCONNECTED";

        void onBlueEvent(String type, Object object);
    }

    protected Listener listener;

    // self defined protocal flag
    private ReentrantLock lock = new ReentrantLock();
    private static final int FLAG_MESSAGE = 0;
    private static final int FLAG_FILE = 1;

    // adapter
    protected static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    protected static final Executor EXECUTOR = Executors.newCachedThreadPool();
    private BluetoothSocket socket;
    private DataOutputStream output;


    public BtBase(Listener listener) {
        this.listener = listener;
        File dirs = new File(FILE_PATH);
        if (!dirs.exists()){
            dirs.mkdirs();
        }
    }

    public static boolean enableAdapter() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        if (!adapter.isEnabled()) {
            return adapter.enable();
        }
        return true;
    }

    /**
     * 循环读取对方数据(若没有数据，则阻塞等待)
     */
    public void loopRead(BluetoothSocket socket) {
        this.socket = socket;
        try {
            if (!this.socket.isConnected()) {
                this.socket.connect();
                listener.onBlueEvent(Listener.CONNECTED, this.socket.getRemoteDevice());
            }

            output = new DataOutputStream(this.socket.getOutputStream());
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            while (true) {

                // read flag
                switch (in.readInt()) {
                    case FLAG_MESSAGE:

                        // read unicode string
                        String message = in.readUTF();
                        listener.onBlueEvent(Listener.RECEIVE_MESSAGE, message);

                        break;
                    case FLAG_FILE: //读取文件

                        // read unicode string
                        String fileName = in.readUTF(); //文件名

                        // read file length
                        long fileLen = in.readLong(); //文件长度

                        // 读取文件内容
                        long len = 0;
                        int r;
                        byte[] b = new byte[4 * 1024];
                        FileOutputStream out = new FileOutputStream(FILE_PATH + fileName);

                        listener.onBlueEvent(Listener.RECEIVE_FILE, APP.getStr(R.string.blue_receiving_file) + fileName);

                        while ((r = in.read(b)) != -1) {
                            out.write(b, 0, r);
                            len += r;
                            if (len >= fileLen)
                                break;
                        }

                        listener.onBlueEvent(Listener.RECEIVE_FILE_FINISHED, APP.getStr(R.string.blue_received_file) + fileName);

                        break;
                    default:
                        break;
                }
            }
        } catch (Throwable e) {
            close();
        }
    }

    /**
     * 发送短消息
     */
    public void sendMessage(String message) {
        lock.lock();
        try {

            output.writeInt(FLAG_MESSAGE); //消息标记
            output.writeUTF(message);

            output.flush();
            listener.onBlueEvent(Listener.SEND_MESSAGE, message);

        } catch (Throwable e) {
            close();
        }
        lock.unlock();
    }

    /**
     * 发送文件
     */
    public void sendFile(final String filePath) {
        lock.lock();
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream in = new FileInputStream(filePath);
                    File file = new File(filePath);

                    output.writeInt(FLAG_FILE); //文件标记
                    output.writeUTF(file.getName()); //文件名
                    output.writeLong(file.length()); //文件长度

                    int r;
                    byte[] b = new byte[4 * 1024];

                    listener.onBlueEvent(Listener.RECEIVE_MESSAGE, APP.getStr(R.string.blue_sending_file) + filePath);

                    while ((r = in.read(b)) != -1)
                        output.write(b, 0, r);
                    output.flush();

                    listener.onBlueEvent(Listener.RECEIVE_FILE_FINISHED, APP.getStr(R.string.blue_sent_file));
                } catch (Throwable e) {
                    close();
                }
                lock.unlock();
            }
        });
    }

    /**
     * 释放监听引用(例如释放对Activity引用，避免内存泄漏)
     */
    public void destroy() {
        listener = null;
    }

    /**
     * 关闭Socket连接
     */
    public void close() {
        try {
            socket.close();
            listener.onBlueEvent(Listener.DISCONNECTED, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前设备与指定设备是否连接
     */
    public boolean isConnected() {
        return (socket != null && socket.isConnected());
    }

    public static void log(String str) {
        Log.e(TAG, str);
    }
}
