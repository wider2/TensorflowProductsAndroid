package virca.tensorflow.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//import shopping.virca.utils.LocaleHelper;


//https://github.com/Blankj/AndroidUtilCode
public class Usage {

    public float readCpuUsage() {
        try {
            int lReturn = 0;
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
                e.printStackTrace();
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            //return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
            //return lReturn;

            float fCPU = (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
            //fCPU = Math.round(fCPU);
            //BigDecimal result = round(fCPU,2);
            fCPU = round2(fCPU, 2);
            return fCPU;
            //return fCPU;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //return new BigDecimal(0.0);
        return 0;
    }

    /*
        public static BigDecimal round(float d, int decimalPlace) {
            BigDecimal bd = new BigDecimal(Float.toString(d));
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bd;
        }
    */
    //http://stackoverflow.com/questions/8911356/whats-the-best-practice-to-round-a-float-to-2-decimals
    public static float round2(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }


    public static int AllowedMemory(Context context) {
        int memorySize = 0;
        try {
            memorySize = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        } catch (Exception e) {
            e.getMessage();
        }
        return memorySize;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getDeviceType() {
        try {
            String manufacturer = "", model = "", device = "";
            if (android.os.Build.MANUFACTURER != null)
                manufacturer = android.os.Build.MANUFACTURER;
            if (android.os.Build.MODEL != null) model = android.os.Build.MODEL;
            if (model.startsWith(manufacturer)) {
                device = capitalize(model);
                //device = URLEncoder.encode(device, "UTF-8");
                return device;
            } else {
                device = capitalize(manufacturer) + " " + model;
                //device = URLEncoder.encode(device, "UTF-8");
                return device;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public static String GetCurrentDate() {
        Time now = new Time();
        now.setToNow();

        Integer x = now.monthDay;        //1-31
        String tnow = x.toString();
        x = now.month + 1;                //0-11
        tnow += "/" + x.toString();
        x = now.year;
        tnow += "/" + x.toString();

        return tnow;
    }

    public static String GetCurrentTime() {
        Time now = new Time();
        now.setToNow();

        Integer x = now.monthDay;        //1-31
        String tnow = x.toString();
        x = now.hour;                    //0-23
        tnow += " " + x.toString() + ":";
        x = now.minute;
        if (x < 10) tnow += "0";
        tnow += x.toString() + ":";
        x = now.second;
        if (x < 10) tnow += "0";
        tnow += x.toString();

        return tnow;
    }

    public static String GetTimeNow() {
        Time now = new Time();
        now.setToNow();

        Integer x = now.monthDay;        //1-31
        String tnow = x.toString();
        x = now.month + 1;                //0-11
        tnow += "/" + x.toString();
        x = now.year;
        tnow += "/" + x.toString();

        x = now.hour;                    //0-23
        tnow += " " + x.toString() + ":";
        x = now.minute;
        if (x < 10) tnow += "0";
        tnow += x.toString() + ":";
        x = now.second;
        if (x < 10) tnow += "0";
        tnow += x.toString();

        return tnow;
    }

    public static String getUsedMemorySize() {
        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = -1L;
        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
            totalSize = info.totalMemory();
            info = null;
            usedSize = totalSize - freeSize;
            usedSize = (usedSize * 100) / totalSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(usedSize);
    }

    public static long getTotalMemory() {
        /*
        long totalSize = 0L;
        try {
            Runtime info = Runtime.getRuntime();
            totalSize = info.totalMemory();
            info=null;
        } catch (Exception e) {
        }
        return String.valueOf(totalSize);
        */
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long blockSize;
        final long totalBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        } else {
            //noinspection deprecation
            blockSize = stat.getBlockSize();
            //noinspection deprecation
            totalBlocks = stat.getBlockCount();
        }
        return totalBlocks * blockSize;
    }

    /* Calculates the free memory of the device. This is based on an inspection of the filesystem, which in android
    * devices is stored in RAM.
    *
            * @return Number of bytes available.
            */
    /*
    private static long getAvailableInternalMemorySize() {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long blockSize;
        final long availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            //noinspection deprecation
            blockSize = stat.getBlockSize();
            //noinspection deprecation
            availableBlocks = stat.getAvailableBlocks();
        }
        return availableBlocks * blockSize;
    }
    */
    /*
    public static long sdCardMemoryFree() {
        long free_memory = 0;
        try {
            File path = Environment.getExternalStorageDirectory();
            android.os.StatFs stat = new android.os.StatFs(path.getPath());
            long availBlocks = stat.getAvailableBlocksLong();
            long blockSize = stat.getBlockSizeLong();
            stat = null;
            free_memory = (long) availBlocks * (long) blockSize;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return free_memory;
    }
*/
    public static long sdCardMemoryFree() {
        //long y = File.getUsableSpace();
        //File file = new File(();
        long freeBytesExternal = Environment.getExternalStorageDirectory().getFreeSpace();
        return freeBytesExternal;
    }

    public static String getDeviceScreenResNew(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display1 = wm.getDefaultDisplay();
        Point size1 = new Point();
        int width = 0, height = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display1.getSize(size1);
            width = size1.x;
            height = size1.y;
        } else {
            width = display1.getWidth();
            height = display1.getHeight();
        }
        //global_width = width;
        //global_height = height;
        return width + "x" + height;
    }

    public static Integer getDeviceScreenDpPart(Context context, String param) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display1 = wm.getDefaultDisplay();
        Point size1 = new Point();
        int width = 0, height = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display1.getSize(size1);
            width = size1.x;
            height = size1.y;
        } else {
            width = display1.getWidth();
            height = display1.getHeight();
        }

        if (param.equals("0")) {
            return width;
        } else {
            return height;
        }
    }


    //https://github.com/ShakeJ/Android-Caffeine-library/blob/master/Caffeine/caffeine/utils/DisplayUtil.java
    public static double getDeviceScreenCmPart(Context context, String param) {
        double widthInch = 0, heightInch = 0;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display1 = wm.getDefaultDisplay();
        Point size1 = new Point();
        int width = 0, height = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display1.getSize(size1);
            width = size1.x;
            height = size1.y;
        } else {
            width = display1.getWidth();
            height = display1.getHeight();
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        widthInch = width / dm.xdpi;
        heightInch = height / dm.ydpi;
        widthInch = Math.pow(widthInch, 2);
        heightInch = Math.pow(heightInch, 2);

        if (param.equals("0")) {
            return widthInch;
        } else {
            return heightInch;
        }
    }

    public static String getDeviceScreenSize(Context context, String param) {
        double widthInch = 0, heightInch = 0, diagonal = 0;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display1 = wm.getDefaultDisplay();
        Point size1 = new Point();
        int width = 0, height = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display1.getSize(size1);
            width = size1.x;
            height = size1.y;
        } else {
            width = display1.getWidth();
            height = display1.getHeight();
        }
        //global_width = width;
        //global_height = height;

        //DisplayMetrics dm = new DisplayMetrics();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(dm);
        //dm.densityDpi; //gives me much more accurate DPIs than x and ydpi. Thanks!
        widthInch = width / dm.xdpi;
        heightInch = height / dm.ydpi;
        //widthInch = (double)Math.round(widthInch * 100) / 100;
        //heightInch = (double)Math.round(heightInch * 100) / 100;
        widthInch = Math.pow(widthInch, 2);
        heightInch = Math.pow(heightInch, 2);
        diagonal = Math.sqrt(widthInch + heightInch);
        diagonal = Math.pow(diagonal, 2);

        if (param.equals("0")) {
            String w = String.valueOf(widthInch);
            if (w.length() > 6) w = w.substring(0, 6);
            String h = String.valueOf(heightInch);
            if (h.length() > 6) h = h.substring(0, 6);
            return w + "x" + h;
            //return widthInch + "x" + heightInch;
        } else {
            return String.valueOf(diagonal);
        }
    }


    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
/*
    public static String getMACAddressAll(Context context) {
        String macAddress = "";
        try {
            if (macAddress.matches(""))
                macAddress = getMACAddress("wlan0"); //using wifi available
            if (macAddress.matches(""))
                macAddress = getMACAddress("eth0"); //using ethernet connection availale
            if (macAddress.matches("")) {
                WifiManager manager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                WifiInfo wifiInfo = manager.getConnectionInfo();
                macAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
                wifiInfo = null;
                manager = null;
            }
            macAddress = macAddress.toUpperCase();
            //Toast.makeText(lcontext, "macAddress: " + macAddress, Toast.LENGTH_SHORT).show(); //12+5
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macAddress;
    }
*/
    public Double getBatteryCapacity(Context context) {
        Object powerProfile = null;
        Double batteryCapacity = 0.0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            powerProfile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            batteryCapacity = (Double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(powerProfile, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        powerProfile = null;
        return batteryCapacity;
    }

    public static String getBatteryStatus(Context appContext) {
        String result = "";
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = appContext.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        if (status == BatteryManager.BATTERY_HEALTH_COLD) {
            result = "Battery health = Cold";
        } else if (status == BatteryManager.BATTERY_HEALTH_DEAD) {
            result = "Battery health = Dead";
        } else if (status == BatteryManager.BATTERY_HEALTH_GOOD) {
            result = "Battery health = Good";
        } else if (status == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
            result = "Battery health = Over heat";
        } else if (status == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
            result = "Battery health = Over voltage";
        } else if (status == BatteryManager.BATTERY_HEALTH_UNKNOWN) {
            result = "Battery health = Unknown";
        } else if (status == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
            result = "Battery health =  Unspecified failure";
        }
        return result;
    }

    /*
    //level %
    public int getBatteryLevel(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getBaseContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int voltage = batteryStatus.getIntExtra("voltage", 0);
        BatVoltage = String.valueOf(voltage);
        int itemperature = batteryStatus.getIntExtra("temperature", 0);
        double temps = (double) itemperature / 10;
        BatTemp = String.valueOf(temps);

        batteryStatus=null; ifilter=null;
        int batteryPct = (int) ((level/(float) scale)* 100.0f);
        return batteryPct;
    }
*/
    public static Boolean isRooted() {
        String[] ROOT_INDICATORS = new String[]{
                // Common binaries
                "/system/xbin/su",
                "/system/bin/su",
                // < Android 5.0
                "/system/app/Superuser.apk",
                "/system/app/SuperSU.apk",
                // >= Android 5.0
                "/system/app/Superuser",
                "/system/app/SuperSU",
                // Fallback
                "/system/xbin/daemonsu"
        };
        if (android.os.Build.TAGS != null && android.os.Build.TAGS.contains("test-keys"))
            return true;

        try {
            for (String candidate : ROOT_INDICATORS) {
                if (new File(candidate).exists())
                    return true;
            }
        } catch (Exception ignore) {
            return null;
        }
        return false;
    }

    public static Boolean isCharging(Context appContext) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = appContext.registerReceiver(null, ifilter);

            int status = batteryStatus.getIntExtra("status", -1);
            return (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL);
        } catch (Exception e) {
            //Logger.warn("Could not get charging status");
        }
        return null;
    }

    public static String howCharging(Context appContext) {
        String result = "";
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = appContext.registerReceiver(null, ifilter);

        //int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        //boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        boolean wireCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS;

        if (usbCharge) {
            result = "Device is charging on USB.";
        } else if (acCharge) {
            result = "Device is charging on AC.";
        } else if (wireCharge) {
            result = "Device is charging on wireless.";
        } else {
            result = "Device charging method unknown.";
        }
        return result;
    }

    public static String getNetworkAccess(Context appContext) {
        try {
            ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                if (activeNetwork.getType() == 1) {
                    return "wifi";
                } else if (activeNetwork.getType() == 9) {
                    return "ethernet";
                } else {
                    // We default to cellular as the other enums are all cellular in some
                    // form or another
                    return "cellular";
                }
            } else {
                return "none";
            }
        } catch (Exception e) {
            //Logger.warn("Could not get network access information, we recommend granting the 'android.permission.ACCESS_NETWORK_STATE' permission");
        }
        return null;
    }

    public static Boolean isLowMemory(Context appContext) {
        try {
            ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memInfo);
            return memInfo.lowMemory;
        } catch (Exception e) {
            //return "Could not check lowMemory status. " + e.getMessage();
        }
        return null;
    }

    public static boolean isHasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static boolean isLowEndHardware(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //VersionUtils.hasKitkat()) {
            return am.isLowRamDevice(); //api 19 kitkat
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { //(VersionUtils.hasJellyBean()) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            return mi.totalMem < (512 * 1024 * 1024);
        } else {
            return Runtime.getRuntime().availableProcessors() == 1;
        }
    }

    public static boolean checkPermission(final Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isTopActivity(Context appContext, String name) {
        Boolean result = false;
        if (checkPermission(appContext, Manifest.permission.GET_TASKS)) {
            ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(1);
            ComponentName cn = list.get(0).topActivity;
            result = cn.getClassName().equals(name);
        }
        return result;
    }

    public static String CollectCrashedThread(Thread paramThread) {
        Thread t = paramThread; //.getUncaughtExceptionHandler();//.getUncaughtExceptionThread();
        final StringBuilder result = new StringBuilder();
        if (t != null) {
            result.append("id=").append(t.getId()).append('\n');
            result.append("name=").append(t.getName()).append('\n');
            result.append("priority=").append(t.getPriority()).append('\n');
            if (t.getThreadGroup() != null) {
                result.append("groupName=").append(t.getThreadGroup().getName()).append('\n');
            }
        } else {
            result.append("No broken thread, this might be a silent exception.");
        }
        return result.toString();
    }

    public static String getTimeZone_London() {
        Date currentLocalTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault()).getTime();
        String tm2 = new SimpleDateFormat("Z").format(currentLocalTime);
        tm2 = tm2.replace("+", "");//remove +

        String tm21, tm22;
        if (tm2.substring(0, 1).matches("-")) {
            tm21 = tm2.substring(0, 3);//hr
            tm22 = tm2.substring(3);//min
        } else {
            tm21 = tm2.substring(0, 2);//hr
            tm22 = tm2.substring(2);//min
        }
        Integer hr = Integer.parseInt(tm21);
        //hr--;
        if (hr < -12) hr += 24;

        return hr + "." + tm22;
    }

    public static int getTimeZoneLondonMinutes() {
        Date currentLocalTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault()).getTime();
        String tm2 = new SimpleDateFormat("Z").format(currentLocalTime);
        tm2 = tm2.replace("+", "");//remove +

        String tm21, tm22;
        if (tm2.substring(0, 1).matches("-")) {
            tm21 = tm2.substring(0, 3);//hr
            tm22 = tm2.substring(3);//min
        } else {
            tm21 = tm2.substring(0, 2);//hr
            tm22 = tm2.substring(2);//min
        }
        Integer hr = Integer.parseInt(tm21);
        Integer mn = Integer.parseInt(tm22);
        //hr--;
        if (hr < -12) hr += 24;

        return (hr * 60) + mn;
    }

    public static boolean twoTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static boolean isTablet(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= 11) { // honeycomb
            // test screen size, use reflection because isLayoutSizeAtLeast is only available since 11
            Configuration con = ctx.getResources().getConfiguration();
            try {
                Method mIsLayoutSizeAtLeast = con.getClass().getMethod("isLayoutSizeAtLeast");
                Boolean r = (Boolean) mIsLayoutSizeAtLeast.invoke(con, 0x00000004); // Configuration.SCREENLAYOUT_SIZE_XLARGE
                return r;
            } catch (Exception x) {
                return false;
            }
        }
        return false;
    }

    public static String hasTablet(Context ctx) {
        String tablet = "2"; //smartphone
        boolean tbl = isTablet(ctx);
        if (tbl) tablet = "1";
        if (tablet.equals("") || !tbl) {
            tbl = twoTablet(ctx);
            if (tbl) tablet = "1";
        }
        return tablet;
    }

    public static String getLocalIpAddress() {
        final StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (!first) {
                            result.append('\n');
                        }
                        result.append(inetAddress.getHostAddress());
                        first = false;
                        //String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        //Log.i(TAG, "***** IP="+ ip);
                        //return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e(TAG, ex.toString());
        }
        //return null;
        return result.toString();
    }

    public static boolean isScreenLock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

/*
    public static Resources getLangResources(Context ctx) {
        Resources resources = null;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String languageCode = prefs.getString("locale_override", "");
            if (languageCode.matches("")) languageCode = "en";

            Context context = LocaleHelper.setLocale(ctx, languageCode);
            resources = context.getResources();
            //tvLabelId.setText(resources.getString(R.string.personal_id) + "\n" + resources.getString(R.string.personal_scan));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resources;
    }
*/

    public static void runCmd(String cmd, Context context) {
        DataOutputStream os;
        try {
            //String p = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Virca/";
            File file = context.getApplicationContext().getFilesDir();
            //File file = new File(p);
            String[] fpath = new String[]{file.getAbsolutePath()};

            Process process = Runtime.getRuntime().exec("su", fpath, file);
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runCmd(String[] cmd) {
        DataOutputStream os;
        try {
            Process process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runCmdSimple(String[] cmd) {
        DataOutputStream os;
        try {
            //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //String[] f = new String[] { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() };
            String p = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Virca/";
            File file = new File(p);
            String[] fpath = new String[]{p};
            Process process = Runtime.getRuntime().exec(cmd, fpath, file);
            os = new DataOutputStream(process.getOutputStream());
            //os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Long convertDateToTimestamp(String str_date) {
        Long result = 0L;
        try {
            if (!str_date.equals("")) {
                //String str_date = "13-09-2011";
                str_date = str_date.replace("-", ".");
                DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                Date date = (Date) formatter.parse(str_date);
                result = date.getTime();
                //System.out.println("Today is " + date.getTime());
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return result;
    }


    public static String timestampToDate(long timestamp) {
        Date date = new Date (timestamp);
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    public static String timestampToDateTime(long timestamp) {
        Date date = new Date (timestamp);
        return new SimpleDateFormat("dd.MM.yyyy hh:mm").format(date);
    }

/*
    public static Date convertTimestampToDate(long curTimeStamp) { //shows year incorrectly
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();

        //curTimeStamp *= 1000;
        calendar.setTimeInMillis(curTimeStamp);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date dateCurrentTimeZone = (Date) calendar.getTime();

        return dateCurrentTimeZone;
    }
*/

}