package com.lucky.androidlearndemo.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

import com.lucky.androidlearndemo.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 手机的信息
 *
  （设备主板、设备品牌、主板引导程序、设备驱动名称、设备显示的版本包、指纹、RadioVersion、分区信息、硬件名称、串口序列号
  设备主机地址、设备版本号、制造商、设备型号、产品的名称、设备标签、设备版本类型、编译时间、设备用户名、当前开发代号、源码控制版本号
  系统版本字符串、系统版本值、系统API级别、屏幕亮度、屏幕分辨率）
 *
 * （androidId、蓝牙地址、无线网卡地址、支持的cpu架构、cpu名字、cpu温度、cpu最大频率、cpu当前频率、cpu最小频率、系统总内存、系统可用内存
 * sd卡总容量、sd卡可用容量、系统卡总容量、系统卡可用容量）
 */
public class PhoneUtil {

    @SuppressLint("StaticFieldLeak")
    static Context context = App.getContext();

    public Map<String, String> getAllInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("设备主板", BOARD());
        map.put("设备品牌", BRAND());
        map.put("主板引导程序", BOOTLOADER());
        map.put("设备驱动名称", DEVICE());
        map.put("设备显示的版本包", DISPLAY());
        map.put("指纹", FINGERPRINT());
        map.put("RadioVersion", getRadioVersion());
        map.put("分区信息", getFingerprintedPartitions());
        map.put("硬件名称", HARDWARE());
        map.put("串口序列号", Serial());
        map.put("设备主机地址", HOST());
        map.put("设备版本号", ID());
        map.put("制造商", MANUFACTURER());
        map.put("设备型号", MODEL());
        map.put("产品的名称", PRODUCT());
        map.put("设备标签", TAGS());
        map.put("设备版本类型", TYPE());
        map.put("编译时间", TIME());
        map.put("设备用户名", USER());
        map.put("baseos", BASE_OS());
        map.put("当前开发代号", CODENAME());
        map.put("源码控制版本号", INCREMENTAL());
        map.put("系统版本字符串", RELEASE());
        map.put("SECURITY_PATCH", SECURITY_PATCH());
        map.put("系统版本值", SDK());
        map.put("系统API级别", SDK_INT());
        map.put("PREVIEW_SDK_INT", PREVIEW_SDK_INT());
        map.put("屏幕亮度", screenBrightness() + "");
        map.put("屏幕分辨率", resolution());

        map.put("androidId", androidId());
        map.put("蓝牙地址", bluetoothAddress());
        map.put("无线网卡地址", macAddress());
        map.put("支持的cpu架构", CpuABI());
        map.put("cpu名字", cpuName());
        map.put("cpu温度", cpuTemp());
        map.put("cpu最大频率", maxCpuFreq());
        map.put("cpu当前频率", curCpuFreq());
        map.put("cpu最小频率", minCpuFreq());
        map.put("系统总内存", totalMemory() + "");
        map.put("系统可用内存", availableMemory() + "");
        map.put("sd卡总容量", totalSD() + "");
        map.put("sd卡可用容量", availableSDCard() + "");
        map.put("系统卡总容量", totalSys() + "");
        map.put("系统卡可用容量", availableSys() + "");
        return map;
    }

    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();

        map.put("board", BOARD());
        map.put("brand", BRAND());
        map.put("bootloader", BOOTLOADER());
        map.put("device", DEVICE());
        map.put("display", DISPLAY());
        map.put("fingerprint", FINGERPRINT());
        map.put("radioversion", getRadioVersion());
        map.put("partitions", getFingerprintedPartitions());
        map.put("hardware", HARDWARE());
        map.put("serial", Serial());
        map.put("host", HOST());
        map.put("id", ID());
        map.put("manufacturer", MANUFACTURER());
        map.put("model", MODEL());
        map.put("product", PRODUCT());
        map.put("tags", TAGS());
        map.put("type", TYPE());
        map.put("time", TIME());
        map.put("user", USER());
        map.put("base_os", BASE_OS());
        map.put("codename", CODENAME());
        map.put("incremental", INCREMENTAL());
        map.put("release", RELEASE());
        map.put("security_patch", SECURITY_PATCH());
        map.put("sdk", SDK());
        map.put("sdk_int", SDK_INT());
        map.put("preview_sdk_int", PREVIEW_SDK_INT());
        map.put("screenbinght", screenBrightness() + "");
        map.put("resolution", resolution());

        map.put("androidId", androidId());
        map.put("blueAddress", bluetoothAddress());
        map.put("macAddress", macAddress());
        map.put("cpuAbi", CpuABI());
        map.put("cpuName", cpuName());
        map.put("cpuTemp", cpuTemp());
        map.put("maxCpuFreq", maxCpuFreq());
        map.put("curCpuFreq", curCpuFreq());
        map.put("minCpuFreq", minCpuFreq());
        map.put("totalMemory", totalMemory() + "");
        map.put("availableMemory", availableMemory() + "");
        map.put("totalSD", totalSD() + "");
        map.put("availableSDCard", availableSDCard() + "");
        map.put("totalSys", totalSys() + "");
        map.put("availableSys", availableSys() + "");
        return map;
    }


    //androidID获取
    public String androidId() {
        try {
            String androidId = Settings.System.getString(context.getContentResolver(),
                    Settings.System.ANDROID_ID);
            if ("9774d56d682e549c".equals(androidId)) {
                return "";
            }
            return androidId;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    //蓝牙地址获取
    @SuppressLint("MissingPermission")
    public String bluetoothAddress() {
        try {
            String blue = Settings.Secure.getString(context.getContentResolver(),
                    "bluetooth_address");
            if (blue != null && !blue.isEmpty()) {
                return blue.toLowerCase();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    //mac地址获取
    @SuppressLint("HardwareIds")
    public String macAddress() {
        if (Build.VERSION.SDK_INT == 23) {
            String str = null;
            Process pp = null;
            InputStreamReader ir = null;
            LineNumberReader input = null;
            try {
                pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
                ir = new InputStreamReader(pp.getInputStream(), StandardCharsets.UTF_8);
                input = new LineNumberReader(ir);
                str = input.readLine();
                if (str != null && str.contains(":") && str.length() == 17) {
                    input.close();
                    ir.close();
                    pp.destroy();
                    return str;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT < 23) {
            try {
                if (context != null) {
                    String result;
                    WifiManager wifiManager =
                            (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    assert wifiManager != null;
                    @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    result = wifiInfo.getMacAddress();
                    return result.replace("=", "").replace("&", "").toLowerCase();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<NetworkInterface> all =
                        Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }
                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString().toLowerCase();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    /**
     * 支持的CPU架构
     */
    public String CpuABI() {
        StringBuilder sb = new StringBuilder("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_ABIS) {
                sb.append(abi);
            }
        } else {
            sb.append(Build.CPU_ABI).append(Build.CPU_ABI2);
        }
        return sb.toString();
    }

    /**
     * 获取CPU最大频率（单位KHZ）
     * "/system/bin/cat" 命令行
     * "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
     *
     * @return 最大功率
     */
    public String maxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq" +
                    "/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    /**
     * 获取CPU最小频率（单位KHZ）
     *
     * @return 最小功率
     */
    public String minCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq" +
                    "/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (Throwable e) {
            e.printStackTrace();
            result = "";
        }
        return result.trim();
    }

    /**
     * 实时获取CPU当前频率（单位KHZ）
     *
     * @return 当前功率
     */
    public String curCpuFreq() {
        String result = "";
        try {
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取CPU名字
     *
     * @return CPU名字
     */
    public String cpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            return array[1];
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取CPU温度
     *
     * @return CPU温度
     */
    public String cpuTemp() {
        String text4 = "";
        try {
            FileReader fr4 = new FileReader("/sys/class/thermal/thermal_zone9/subsystem/thermal_zone9/temp");
            BufferedReader br4 = new BufferedReader(fr4);
            text4 = br4.readLine();
            br4.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return text4;
    }

    //系统内存
    @RequiresApi(api = Build.VERSION_CODES.M)
    public long availableMemory() {
        try {
            ActivityManager activityManager =
                    (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(info);
            return info.availMem;

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    //可用sd卡容量
    @SuppressWarnings(value = {"deprecated"})
    public long availableSDCard() {
        try {

            String extSdcardPath = System.getenv("SECONDARY_STORAGE");
            if (extSdcardPath != null) {
                File base = new File(extSdcardPath);
                StatFs stat = new StatFs(base.getPath());
                long nAvailableCount = stat.getBlockSize() * ((long) stat.getAvailableBlocks());
                return nAvailableCount;

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    //可用系统容量
    @SuppressWarnings(value = {"deprecated"})
    public long availableSys() {
        try {
            File file = Environment.getDataDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSize();
            long availableBlocks = statFs.getAvailableBlocks();
            return availableBlocks * blockSize;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    //总内存
    public long totalMemory() {
        String str1 = "/proc/meminfo";
        long initial_memory;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            String str2 = localBufferedReader.readLine();
            String[] arrayOfString = str2.split("\\s+");
            initial_memory = Long.valueOf(arrayOfString[1]).intValue();
            localBufferedReader.close();
            return initial_memory * 1024;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    //总SD卡容量
    public long totalSD() {
        try {
            if (Build.VERSION.SDK_INT <= 23) {
                String extSdcardPath = System.getenv("SECONDARY_STORAGE");
                if (extSdcardPath != null) {
                    File base = new File(extSdcardPath);
                    StatFs stat = new StatFs(base.getPath());
                    long nAvailableCount = stat.getBlockSize() * ((long) stat.getBlockCount());
                    return nAvailableCount;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    //总系统容量
    public long totalSys() {
        try {
            File file = Environment.getDataDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            return totalBlocks * blockSize;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String BOARD() {
        return Build.BOARD;
    }

    public String BRAND() {
        return Build.BRAND;
    }

    public String BOOTLOADER() {
        return Build.BOOTLOADER;
    }

    public String DEVICE() {
        return Build.DEVICE;
    }

    public String DISPLAY() {
        return Build.DISPLAY;
    }

    public String FINGERPRINT() {
        return Build.FINGERPRINT;
    }

    public String getRadioVersion() {
        return Build.getRadioVersion();
    }

    @SuppressLint("MissingPermission")
    public String Serial() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }

    public String getFingerprintedPartitions() {
        try {
            StringBuffer sb = new StringBuffer("");
            List<Build.Partition> partitions = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                partitions = Build.getFingerprintedPartitions();
                for (Build.Partition partition : partitions) {
                    sb.append(partition.getName()).append(partition.getBuildTimeMillis()).append(partition.getFingerprint()).append(",");
                }
            } else {
                return "";
            }
            return sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public String HARDWARE() {
        return Build.HARDWARE;
    }

    public String HOST() {
        return Build.HOST;
    }

    public String ID() {
        return Build.ID;
    }

    public String MANUFACTURER() {
        return Build.MANUFACTURER;
    }

    public String MODEL() {
        return Build.MODEL;
    }

    public String PRODUCT() {
        return Build.PRODUCT;
    }

    public String TAGS() {
        return Build.TAGS;
    }

    public String TYPE() {
        return Build.TYPE;
    }

    public String TIME() {
        return Build.TIME + "";
    }

    public String USER() {
        return Build.USER;
    }

    public String BASE_OS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Build.VERSION.BASE_OS;
        }
        return "";
    }

    public String CODENAME() {
        return Build.VERSION.CODENAME;
    }

    public String INCREMENTAL() {
        return Build.VERSION.INCREMENTAL;
    }

    public String RELEASE() {
        return Build.VERSION.RELEASE;
    }

    public String SECURITY_PATCH() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Build.VERSION.SECURITY_PATCH;
        }
        return "";
    }

    public String SDK() {
        return Build.VERSION.SDK;
    }

    public String PREVIEW_SDK_INT() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Build.VERSION.PREVIEW_SDK_INT + "";
        }
        return "";
    }

    public String SDK_INT() {
        return Build.VERSION.SDK_INT + "";
    }

    /**
     * Build.VERSION_CODES下对应了所有版本的版本号
     */

    //屏幕亮度
    public int screenBrightness() {
        try {
            if (context != null) {
                ContentResolver resolver = context.getContentResolver();
                return Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    //屏幕分辨率
    public String resolution() {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return ("[" + dm.density + "," + dm.widthPixels + "," + dm.heightPixels + "," + dm.scaledDensity + "," + dm.xdpi + "," + dm.ydpi + "]");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }
}
