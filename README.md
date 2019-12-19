# bluetooth

- scenario

        https://stackoverflow.com/questions/17502818/android-ios-peer-to-peer-architecture

- bluethooth classic

        https://developer.android.com/reference/android/bluetooth/package-summary

- BLE / bluethooth low energy

        - BLE protocal
        https://learn.adafruit.com/introduction-to-bluetooth-low-energy/gatt

        - BLE specification
        https://www.bluetooth.com/specifications/bluetooth-core-specification/

        - android ble API
        https://developer.android.com/guide/topics/connectivity/bluetooth-le.html

        - packet size
        https://stackoverflow.com/questions/38913743/maximum-packet-length-for-bluetooth-le
        
                - The MTU is negotiated during connection process. 
                iOS will automatically use the largest value supported by both devices. iOS supports up to 158 bytes

                - android max size is 517 bytes

                - send longer size
                https://github.com/NordicSemiconductor/Android-BLE-Library/issues/3

        - speed of BLE (transfer big file over BLE?)
        https://stackoverflow.com/questions/22919090/ble-file-transfer-from-a-device-to-a-smartphone

- file transfer

        IOS refuses classic bluetooth (Serial Port Profile) from android
        IOS can only play BLE central role (scan others), but BLE is not for large file -_-!
        android as central role has bug - https://issuetracker.google.com/issues/36977196

        - BLE + wifi
        android phone / ios iphone == BLE central role
        android hardware device == some android play peripheral role
        android device use wifi to upgrade map data / large file from internet directly

- dev BLE Peripheral role
        
        - test if ble is supported / advertise + scan demo
        https://code.tutsplus.com/tutorials/how-to-advertise-android-as-a-bluetooth-le-peripheral--cms-25426
        https://github.com/tutsplus/Android-BluetoohLEAdvertising

        - The BLE Peripheral Simulator
        is an Android app that allows developers to try out new features of Web Bluetooth without the need for a BLE Peripheral Device. 
        https://github.com/WebBluetoothCG/ble-test-peripheral-android

        - test with web BLE
        https://googlechrome.github.io/samples/web-bluetooth/

        - test with ios app - LightBLE / 蓝牙助手

        - uuidgen (run from mac command line)
        C8F72DBE-CAFA-41F5-B252-27E00A9519F6

- dev BLE central role

        - google document

        - test with web BLE
        https://googlechrome.github.io/samples/web-bluetooth/

- todo

        - Certral as GATT server?
        can android Central role who scan other devices to play a GATT server role?
        if yes how can the client get a bluetooth device and then connect it to GATT server?

        - identify a BLE device? filter by service uuid?
        ZTE phone, APP mac address=34:69:87:2c:fa:00

        - test with another andriod device
        now LightBLE can connect and display service nearby including my service
        but my app cannot connect and display
        see if my app can discoer my service?
        
        

