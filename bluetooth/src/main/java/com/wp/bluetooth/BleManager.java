package com.wp.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.wp.permission.OnCheckPermissionHandler;
import com.wp.permission.PermissionManager;
import com.wp.permission.WpPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_CONNECTING;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_DISCONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_DISCONNECTING;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_UNKNOWN;
import static com.wp.bluetooth.BleRequest.FACTORY_SIGN;
import static com.wp.bluetooth.BleRequest.READ_ACTIVE_TIME;
import static com.wp.bluetooth.BleRequest.READ_DEVICE_NUMBER;
import static com.wp.bluetooth.BleRequest.READ_RFID_OLD;
import static com.wp.bluetooth.BleRequest.SET_ACTIVE_TIME;
import static com.wp.bluetooth.BleRequest.SET_DATE;
import static com.wp.bluetooth.BleRequest.SET_DEVICE_NUMBER;
import static com.wp.bluetooth.BleRequest.SET_DEVICE_TYPE;
import static com.wp.bluetooth.BleRequest.UPDATE_OLD;
import static com.wp.bluetooth.BleRequest.VERSION;

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 9/18/21
 * 看了我的代码，感动了吗
 */
public class BleManager {
    BlueBaseHandler handler;
    BluetoothClient mClient;
    public static int REQUEST_CODE_LOCATION_SETTINGS = 10000;

    public static int ONE_BYTE_MAX_SIZE = 256;

    public static int ONE_TAG_SIZE = 12;


    private static BleManager instance;

    private AppCompatActivity activity;

    private Map<String, BleGattProfile> connectedBluetoothMap;

    private Map<String, SearchResult> foundDeviceMap;

    private Map<String, SearchResult> allDeviceMap;

    //起始
    public static byte start = (byte) 0xab;
    //结尾
    public static byte end = 0x7e;
    //版本开头
    public static byte versionStart = 0x03;
    //版本结尾
    public static byte versionEnd = 0x15;
    //校验开头
    public static byte checkStart = 0x55;
    //校验结尾
    public static byte checkEnd = 0x55;
    //序号
    public static byte serialNumber = 0x01;

    //Generic Access Profile
    public static String SERVICE_UUID_GENERIC_ACCESS_PROFILE = "00001800-0000-1000-8000-00805f9b34fb";
    //设备名称
    public static String CHARACTER_UUID_GENERIC_ACCESS_PROFILE_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    //Appearance
    public static String CHARACTER_UUID_GENERIC_ACCESS_PROFILE_APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    //Peripheral preferred connection parameters
    public static String CHARACTER_UUID_GENERIC_ACCESS_PROFILE_PARAMS = "00002a04-0000-1000-8000-00805f9b34fb";
    //仅可解析的私有地址
    public static String CHARACTER_UUID_GENERIC_ACCESS_PROFILE = "00002ac9-0000-1000-8000-00805f9b34fb";

    //Generic Attribute Profile
    public static UUID SERVICE_UUID_GENERIC_ATTRIBUTE_PROFILE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    //Service Changed
    public static UUID CHARACTER_UUID_GENERIC_ATTRIBUTE_SERVICE_CHANGED = UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb");


    public static UUID[] SERVICE_UUID_ARR = new UUID[]{
            UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00010203-0405-0607-0809-0a0b0c0d1910"),
    };
    //notify指令的char uuid
    public static UUID[] NOTIFY_CHARACTER_UUID_ARR = new UUID[]{
            UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00010203-0405-0607-0809-0a0b0c0d2b10"),
    };
    //写入指令的char uuid
    public static UUID[] WRITE_CHARACTER_UUID_ARR = new UUID[]{
            UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00010203-0405-0607-0809-0a0b0c0d2b11"),
    };

    private List<Byte> unCompleteList = new ArrayList();


    private BleConnectStatusListener bleConnectStatusListener;
    private BluetoothStateListener mBluetoothStateListener;

    private static List<BleReceiveHandler> receiveHandlers = new ArrayList<>();

    private BleManager() {
        connectedBluetoothMap = new HashMap<>();
        foundDeviceMap = new HashMap<>();
        allDeviceMap = new HashMap<>();
    }

    public static BleManager getInstance(AppCompatActivity activity) {
        if (instance == null) {
            instance = new BleManager();
        }
        instance.activity = activity;
        return instance;
    }

    public void init(final BlueBaseHandler handler) {
        this.handler = handler;
        //如果已经完成了初始化，则不进行重新初始化了,只替换handler
        if (mClient != null) {
            return;
        }
        PermissionManager.INSTANCE.requestPermission(activity, new OnCheckPermissionHandler() {
            @Override
            public void onAllGranted() {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                //说明此设备不支持蓝牙操作
                if (mBluetoothAdapter == null) {
                    handler.onInitFail(ErrorCode.UN_SUPPORT_DEVICE);
                    return;
                }
                //没有开启蓝牙
                if (!mBluetoothAdapter.isEnabled()) {
                    handler.onInitFail(ErrorCode.BLUETOOTH_UN_OPEN);
                    return;
                }
                //如果没打开gps，提示先打开
                if (!checkGps(activity)) {
                    handler.onInitFail(ErrorCode.LOCATION_BUTTON_NOT_OPEN);
                    return;
                }
                //先解除之前的蓝牙状态回调，如果有的话
                if (mBluetoothStateListener != null) {
                    mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
                    mBluetoothStateListener = null;
                }
                mBluetoothStateListener = new
                        BluetoothStateListener() {
                            @Override
                            public void onBluetoothStateChanged(boolean openOrClosed) {
                                if (openOrClosed) {
                                    handler.onBlueToothOpened();
                                } else {
                                    handler.onBlueToothClosed();
                                }
                            }
                        };

                mClient = new BluetoothClient(activity);
                mClient.registerBluetoothStateListener(mBluetoothStateListener);
                handler.onInitSuccess();
            }

            @Override
            public void onAtLeastOneDeniedCanRemind() {
                handler.onInitFail(ErrorCode.LOCATION_PERMISSION_DENIED);
            }

            @Override
            public void onAtLeastOneDeniedNotRemind() {
                handler.onInitFail(ErrorCode.LOCATION_PERMISSION_DENIED);
            }

            @Override
            public void onFinish() {

            }
        }, WpPermission.ACCESS_COARSE_LOCATION);
    }


    public ErrorCode check(AppCompatActivity activity) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //说明此设备不支持蓝牙操作
        if (mBluetoothAdapter == null) {
            return ErrorCode.UN_SUPPORT_DEVICE;
        }
        //没有开启蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            return ErrorCode.BLUETOOTH_UN_OPEN;
        }
        if (!checkGps(activity)) {
            return ErrorCode.LOCATION_BUTTON_NOT_OPEN;
        }
        if (mClient == null) {
            return ErrorCode.NOT_INIT;
        }
        return ErrorCode.OK;
    }

    @SuppressLint("MissingPermission")
    public void startScan(final String filter, final boolean allowDuplicatesKey, int interval, final BlueScanHandler blueSearchHandler) {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(interval)
                .build();
        if (mClient == null) {
            return;
        }
        mClient.stopSearch();
        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                blueSearchHandler.onSearchStart();
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                if (filter != null && !device.getName().contains(filter)) {
                    return;
                }
                String mac = device.getAddress();

                if (allDeviceMap.get(mac) == null) {
                    allDeviceMap.put(mac, device);
                }
                if (allowDuplicatesKey) {
                    blueSearchHandler.onSearchResult(device);
                } else {
                    if (foundDeviceMap.get(mac) == null) {
                        foundDeviceMap.put(mac, device);
                        blueSearchHandler.onSearchResult(device);
                    }
                }
            }

            @Override
            public void onSearchStopped() {
                blueSearchHandler.onSearchStop();
                clearFoundDeviceList();
            }

            @Override
            public void onSearchCanceled() {
                blueSearchHandler.onSearchCanceled();
                clearFoundDeviceList();
            }
        });

    }

    public void connect(final String mac) {
        switch (mClient.getConnectStatus(mac)) {
            //状态为断开连接时才去连接设备
            case STATUS_DEVICE_DISCONNECTED:
                handler.onBluetoothConnectStatusChanged(mac, false);
            case STATUS_UNKNOWN:
                break;
            case STATUS_DEVICE_CONNECTED:
                //如果当前是连接状态 就直接返回，且返回连接成功的status
                handler.onBluetoothConnectStatusChanged(mac, true);
                return;
            case STATUS_DEVICE_CONNECTING:
            case STATUS_DEVICE_DISCONNECTING:
                return;
        }

        if (bleConnectStatusListener != null) {
            mClient.unregisterConnectStatusListener(mac, bleConnectStatusListener);
        }

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3) // 连接如果失败重试3次
                .setConnectTimeout(30000) // 连接超时30s
                .setServiceDiscoverRetry(3) // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000) // 发现服务超时20s
                .build();

        bleConnectStatusListener = new BleConnectStatusListener() {
            @Override
            public void onConnectStatusChanged(String s, int status) {
                switch (status) {
                    case STATUS_CONNECTED:
                        handler.onBluetoothConnectStatusChanged(s, true);
                        Date now = new Date();
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
                        String str = sf.format(now);
                        sendInstruct(mac, BleRequest.SET_DATE, HexUtil.decodeHex(str), null);
                        break;
                    case STATUS_DISCONNECTED:
                        handler.onBluetoothConnectStatusChanged(s, false);
                        connectedBluetoothMap.remove(s);
                        break;
                }
            }
        };

        mClient.registerConnectStatusListener(mac, bleConnectStatusListener);

        mClient.connect(mac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                if (code == REQUEST_SUCCESS) {
                    if (!connectedBluetoothMap.containsKey(mac)) {
                        connectedBluetoothMap.put(mac, profile);
                    }

                    BleNotifyResponse response = new BleNotifyResponse() {
                        @Override
                        public void onNotify(UUID service, UUID characteristic, byte[] bytes) {

                            handler.onReceive(mac, service, characteristic, bytes);
                            for (int i = 0; i < receiveHandlers.size(); i++) {
                                BleReceiveHandler receiveHandler = receiveHandlers.get(i);
                                receiveHandler.onReceive(mac, service, characteristic, bytes);
                            }
                            unCompleteList.addAll(toList(bytes));

                            byte start = unCompleteList.get(0);

                            byte end = unCompleteList.get(unCompleteList.size() - 1);

                            //如果开头和结尾不对，就直接不处理

                            if (start == (byte) 0xbb && end == 0x7e) {


                                BleResult result = new BleResult();

                                result.setMac(mac);
                                result.setData(HexUtil.encodeHexStr(toByteArray(unCompleteList)));


                                byte two = unCompleteList.get(1);

                                byte three = unCompleteList.get(2);

                                byte four = unCompleteList.get(3);

                                byte five = unCompleteList.get(4);

                                if (three == READ_RFID_OLD.getRequest()) {
                                    if (two == 0x01 && four * ONE_BYTE_MAX_SIZE + five == unCompleteList.subList(5, unCompleteList.size() - 1).size()) {
                                        //老式的按rfid键读取
                                        Log.e("老式rfid01", "老式01");
                                        int size = four * ONE_BYTE_MAX_SIZE + five;
                                        String tags = "";
                                        Log.e("size", size + "");
                                        if (size == ONE_TAG_SIZE) {
                                            tags = HexUtil.encodeHexStr(toByteArray(unCompleteList.subList(5, unCompleteList.size() - 1)));
                                        } else {
                                            String tag1 = HexUtil.encodeHexStr(toByteArray(unCompleteList.subList(5, 5 + ONE_TAG_SIZE)));
                                            String tag2 = HexUtil.encodeHexStr(toByteArray(unCompleteList.subList(5 + ONE_TAG_SIZE, 5 + 2 * ONE_TAG_SIZE)));
                                            Log.e("tag1", tag1 + "");
                                            Log.e("tag2", tag2 + "");
                                            if (tag1.equals(tag2)) {
                                                tags = tag1;
                                            } else {
                                                tags = tag1 + tag2;
                                            }
                                        }
                                        result.setData(tags);
                                        result.setRequestCode(READ_RFID_OLD.getCode());
                                    }
                                    if (two == 0x02 && four == unCompleteList.subList(4, unCompleteList.size() - 1).size()) {
                                        Log.e("老式rfid02", "老式02");
                                        result.setRequestCode(READ_RFID_OLD.getCode());
                                    }
                                } else if (three == FACTORY_SIGN.getRequest() && four == unCompleteList.subList(4, unCompleteList.size() - 1).size()) {
                                    //获取版本标识 老版本
                                    result.setRequestCode(FACTORY_SIGN.getCode());

                                    byte[] identification = new byte[four];

                                    System.arraycopy(bytes, 4, identification, 0, 4 + four - 4);

                                    boolean isTrueFactorySign = checkFactorySign(identification);

                                    result.setData(isTrueFactorySign + "");

                                } else if (three == VERSION.getRequest() && unCompleteList.size() == 5) {
                                    //BB02AA007E
                                    result.setRequestCode(VERSION.getCode());
                                    String versionStr = HexUtil.BytesToHexString(new byte[]{two});
                                    result.setData(versionStr);
                                } else if (two * ONE_BYTE_MAX_SIZE + three == unCompleteList.size()) {
                                    byte[] waitDecodeData = toByteArray(unCompleteList.subList(3, unCompleteList.size() - 1));


                                    byte key = getEncodeKEey(mac);

                                    List<Byte> finalData = toList(Sign.vBOOTMODE_API_DeCode(waitDecodeData, key));


                                    //指令
                                    byte instruction = finalData.get(0);

                                    result.setRequestCode(BleRequest.get(instruction).getCode());


                                    //版本号
                                    byte versionStart = finalData.get(1);
                                    byte versionEnd = finalData.get(2);
                                    //序号
                                    int serialNumber = finalData.get(finalData.size() - 1);
                                    //校验结束
                                    int checkEnd = finalData.get(finalData.size() - 2);
                                    //校验起始
                                    int checkStart = finalData.get(finalData.size() - 3);
                                    //状态
                                    int status = finalData.get(finalData.size() - 4);
                                    //数据
                                    List<Byte> dataArea = finalData.subList(3, finalData.size() - 4);

                                    if (instruction == BleRequest.READ_FACTORY_SIGN.getRequest()) {
                                        boolean isTrueFactorySign = checkFactorySign(toByteArray(dataArea));
                                        result.setData(isTrueFactorySign + "");
                                    } else if (instruction == BleRequest.READ_AGREEMENT_VERSION.getRequest()) {
                                        byte[] vs = new byte[2];
                                        vs[0] = versionStart;
                                        vs[1] = versionEnd;
                                        String version = HexUtil.encodeHexStr(vs);
                                        result.setData(version);
                                    } else if (instruction == BleRequest.READ_RFID.getRequest()) {
                                        result.setData(HexUtil.encodeHexStr(toByteArray(dataArea)));
                                    }
                                }
                                handler.onReceiveData(mac, result);
                                for (int i = 0; i < receiveHandlers.size(); i++) {
                                    BleReceiveHandler receiveHandler = receiveHandlers.get(i);
                                    receiveHandler.onReceiveData(mac,result);
                                }
                                unCompleteList.clear();
                            }

                        }

                        @Override
                        public void onResponse(int i) {

                        }

                    };
                    List<BleGattService> services = profile.getServices();
                    for (int i = 0; i < services.size(); i++) {
                        BleGattService service = services.get(i);
                        List<BleGattCharacter> characters = service.getCharacters();
                        for (int j = 0; j < characters.size(); j++) {
                            BleGattCharacter character = characters.get(j);
                            //可notify
                            for (int k = 0; k < NOTIFY_CHARACTER_UUID_ARR.length; k++) {
                                UUID notifyUuid = NOTIFY_CHARACTER_UUID_ARR[k];
                                if (notifyUuid.toString().equals(character.getUuid().toString())
                                ) {
                                    if ((character.getProperty() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                                        mClient.notify(mac, service.getUUID(), character.getUuid(), response);
                                    }
//                                    if ((character.getProperty() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
//                                        mClient.indicate(mac, service.getUUID(), character.getUuid(), response);
//                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }


    public byte getEncodeKEey(String mac) {
        SearchResult device = allDeviceMap.get(mac);

        byte key = 0;

        if (device != null) {
            char[] nameChar = device.getName().toCharArray();
            if (nameChar.length >= 2) {
                String hexStr = nameChar[nameChar.length - 2] + "" + nameChar[nameChar.length - 1] + "";
                byte[] hex = HexUtil.decodeHex(hexStr);
                key = hex[0];
            }
        }
        return key;
    }


    public void open() {
        BluetoothUtils.openBluetooth();
    }

    public boolean isInit() {
        return mClient != null;
    }

    public void close() {
        stopScan();
        mClient.closeBluetooth();
    }

    public ErrorCode disconnect(String mac) {
        ErrorCode code = check(activity);
        mClient.disconnect(mac);
        return code;
    }

    private boolean checkGps(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isOpen = GpsUtil.isOpen(activity); //判断GPS是否打开
            return isOpen;
        }
        return true;
    }


    public void goGpsSetting() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
    }

    public void sendInstruct(final String mac, byte[] bytes, final BlueSendHandler handler) {
        Log.e("BleManager", HexUtil.BytesToHexString(bytes));

        for (int i = 0; i < WRITE_CHARACTER_UUID_ARR.length; i++) {
            mClient.write(mac, SERVICE_UUID_ARR[i], WRITE_CHARACTER_UUID_ARR[i], bytes, new BleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    Log.e("onResponse", code + "");
                    if (code == REQUEST_SUCCESS) {
                        if (handler != null) {
                            handler.onSendSuccess(mac, code);
                        }
                    } else {
                        if (handler != null) {
                            handler.onSendFail(mac, code);
                        }
                    }
                }
            });
        }

    }

    public void sendInstruct(final String mac, String requestCode, final BlueSendHandler handler) {
        BleRequest request = BleRequest.get(requestCode);
        sendInstruct(mac, request, new byte[0], handler);
    }

    public void sendInstruct(final String mac, BleRequest request, byte[] data, final BlueSendHandler handler) {
        byte[] bytes;
        if (request == FACTORY_SIGN || request == VERSION || request == SET_DEVICE_TYPE
                ||
                request == SET_DEVICE_NUMBER || request == READ_DEVICE_NUMBER
                || request == SET_ACTIVE_TIME || request == READ_ACTIVE_TIME
                || request == UPDATE_OLD
                || request == SET_DATE) {
            bytes = new byte[3 + data.length];
            bytes[0] = (byte) 0xab;
            bytes[1] = request.getRequest();
            System.arraycopy(data, 0, bytes, 2, data.length);
            bytes[bytes.length - 1] = (byte) 0xfe;

        } else {
            bytes = getEncryptRequest(mac, request, data);
        }
        sendInstruct(mac, bytes, handler);
    }

    private byte[] getEncryptRequest(String mac, BleRequest request, byte[] data) {


        //总长
        int total = (10 + data.length);

        byte totalStart;
        byte totalEnd;
        if (total > ONE_BYTE_MAX_SIZE) {
            totalStart = (byte) (total / ONE_BYTE_MAX_SIZE);
            totalEnd = (byte) (total % ONE_BYTE_MAX_SIZE);
        } else {
            totalStart = 0x00;
            totalEnd = (byte) total;
        }

        //以下加密
        //指令
        byte instruction = request.getRequest();
        //数据 data

        //以上加密


        List<Byte> waitEncodeBytes = new ArrayList();
        waitEncodeBytes.add(instruction);
        waitEncodeBytes.add(versionStart);
        waitEncodeBytes.add(versionEnd);
        for (byte datum : data) {
            waitEncodeBytes.add(datum);
        }
        waitEncodeBytes.add(checkStart);
        waitEncodeBytes.add(checkEnd);
        waitEncodeBytes.add(serialNumber);

        byte key = getEncodeKEey(mac);

        byte[] encodeByte = Sign.vBOOTMODE_API_EnCode(toByteArray(waitEncodeBytes), key);

        List<Byte> allBytes = new ArrayList<Byte>();

        allBytes.add(start);
        allBytes.add(totalStart);
        allBytes.add(totalEnd);
        allBytes.addAll(toList(encodeByte));
        allBytes.add(end);
        return toByteArray(allBytes);
    }


    public void stopScan() {
        mClient.stopSearch();
    }

    public boolean isEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isEnabled();
    }

    public BluetoothClient getClient() {
        return mClient;
    }

    public boolean isConnected(String mac) {
        int status = mClient.getConnectStatus(mac);
        return status == BluetoothProfile.STATE_CONNECTED;
    }

    public boolean isDiscovering() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isDiscovering();
    }

    public Set<String> getConnectedDevices() {
        return connectedBluetoothMap.keySet();
    }

    public BleGattProfile getBleProfile(String mac) {
        return connectedBluetoothMap.get(mac);
    }

    public BleGattCharacter getBleGattCharacter(String mac, String serviceId, String characterId) {
        BleGattProfile profile = getBleProfile(mac);
        if (profile == null) {
            return null;
        }
        BleGattService service = profile.getService(UUID.fromString(serviceId));
        for (BleGattCharacter character : service.getCharacters()) {
            if (character.getUuid().equals(UUID.fromString(characterId))) {
                return character;
            }
        }
        return null;
    }

    public void clearFoundDeviceList() {
        foundDeviceMap.clear();
    }


    private List<Byte> toList(byte[] bytes) {
        List<Byte> list = new ArrayList();
        for (int i = 0; i < bytes.length; i++) {
            list.add(bytes[i]);
        }
        return list;
    }

    private byte[] toByteArray(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    private boolean checkFactorySign(byte[] identification) {

        byte[] correctManufacturerIdentification = new byte[]{
                0x08, 0x00, 0x03, 0x07, 0x02, 0x05, 0x02, 0x06, 0x01, 0x05
        };
        if (identification.length != correctManufacturerIdentification.length) {
            return false;
        } else {
            for (int i = 0; i < correctManufacturerIdentification.length; i++) {
                if (correctManufacturerIdentification[i] != identification[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void addReceiveHandler(BleReceiveHandler receiveHandler){
        receiveHandlers.add(receiveHandler);
    }

    public static void removeReceiveHandler(BleReceiveHandler receiveHandler){
        receiveHandlers.remove(receiveHandler);
    }

}
