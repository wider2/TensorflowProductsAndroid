package virca.tensorflow;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.Locale;

import virca.tensorflow.utils.Usage;
import virca.tensorflow.utils.Utilities;


/*
import com.productlayer.android.common.util.LocaleUtil;
import com.productlayer.android.sdk.PLYAndroid;
import com.productlayer.rest.client.config.PLYRestClientConfig;
*/

//import com.github.yuweiguocn.library.greendao.MigrationHelper;


public class TensorFlowApplication extends Application {

    //private static PLYAndroid clientPL;
    /*
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_RECOVERABLE = 9002;
    public final static int GOOGLE_ACCOUNT_PICK = 1000;
    public static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    private static final String KEY_ACCOUNT = "key_account";
    //private static String TWITTER_KEY = "M59aRCGob1T1CZy10TfeYSOi2";
    //private static String TWITTER_SECRET = "Gc2RX5Qa97stk7KUEqSfFVpaVVPp1lLogE9oJVyVxM6HlXAes5";
*/
    //private GoogleApiClient mGoogleApiClient;
    //private Account mAccount;
    //CallbackManager callbackManager;
    //SQLiteDatabase db;

    public static Thread.UncaughtExceptionHandler androidDefaultUEH;
    //private static DaoSession mDaoSession;
    //DaoMaster.OpenHelper mInstance;
    //private RefWatcher refWatcher;
    @NonNull
    private static TensorFlowApplication instance;

    public TensorFlowApplication() {
        instance = this;
    }

    @NonNull
    public static TensorFlowApplication get() {
        return instance;
    }

   /*
    public DaoSession initializeDB() {

        mInstance = new MyOpenHelper(getApplicationContext(), GlobalConstants.SQL_DB_NAME, null);
        if (db == null) {
            db = mInstance.getWritableDatabase();
            System.out.println("initializeDB->: db==null");
        } else {
            if (!db.isOpen()) {
                db = mInstance.getWritableDatabase();
                System.out.println("initializeDB->: db!=null && !db.isOpen()");
            } else {
                System.out.println("initializeDB->: db!=null && db.isOpen()");
            }
        }

        //clearDatabase(getBaseContext());
        checkDemoDb();

        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        //userDao = mDaoSession.getUserDao();
        return mDaoSession;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public DaoSession getOrCreateDaoSession() {
        DaoSession mDaoSession = VircaApplication.get().getDaoSession();
        if (mDaoSession == null) mDaoSession = VircaApplication.get().initializeDB();
        return mDaoSession;
    }


    public static void clearDatabase(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(
                context.getApplicationContext(), GlobalConstants.SQL_DB_NAME, null);
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        devOpenHelper.onUpgrade(db, 0, 0);
        devOpenHelper.close();
    }


    public void closeDatabase() {
        if (db != null) db.close();
        mDaoSession = null;
    }

    @Override
    protected void finalize() throws Throwable {
        Log.v("VircaApplication", "finalize is call");
        if (db != null) db.close();
        super.finalize();
    }

    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(base);
        String lang = prefs.getString("locale_override", "");
        if (lang.matches("")) lang = "et";

        super.attachBaseContext(LocaleHelper.onAttach(base, lang)); // "en"));
    }
*/
    @Override
    public void onCreate() {
        super.onCreate();
        //refWatcher = LeakCanary.install(this);
        //https://convertcase.net/
        try {
            /*
            SQLiteDatabase db = openOrCreateDatabase(GlobalConstants.SQL_DB_NAME, Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS PRODUCT_SHOPPING(_id INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP INTEGER, LOGIN_ID VARCHAR, EAN_CODE VARCHAR, EAN_FORMAT VARCHAR, TAGFIELD VARCHAR, NAME VARCHAR, ORDER_ID VARCHAR, PRICE VARCHAR, QUANTITY INTEGER, PRICE_CUR VARCHAR, CAT_ID VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS PRODUCTS(_id INTEGER PRIMARY KEY AUTOINCREMENT, CREATED_TIMESTAMP INTEGER, UPDATED_TIMESTAMP INTEGER, NAME VARCHAR, GTIN VARCHAR, DESCRIPTION VARCHAR, SHORTLY VARCHAR, LANGUAGE VARCHAR, CATEGORY VARCHAR, BRAND VARCHAR, BRAND_OWNER VARCHAR, IMAGE_URL VARCHAR, URL VARCHAR, COLOR_HEX VARCHAR, WEIGHT VARCHAR, COUNTRY_ORIGIN VARCHAR, NUTRITION VARCHAR, WARNINGS VARCHAR, USER_GUIDE VARCHAR, CAT_ID VARCHAR, DEPARTMENT_ID VARCHAR);");

            db.execSQL("CREATE TABLE IF NOT EXISTS DEPARTMENTS(_id INTEGER PRIMARY KEY AUTOINCREMENT, SHOP_ID VARCHAR, DEPARTMENT_ID VARCHAR, DEPARTMENT_ENG VARCHAR, DEPARTMENT_EST VARCHAR, DEPARTMENT_RUS VARCHAR);");

            db.execSQL("CREATE TABLE IF NOT EXISTS SHOP_DATA(_id INTEGER PRIMARY KEY AUTOINCREMENT, BODY VARCHAR, ERROR VARCHAR, SHOP_ID VARCHAR, S_NAME VARCHAR, S_ADDRESS VARCHAR, S_PHONE VARCHAR, S_EMAIL VARCHAR, S_URL VARCHAR, S_INFO VARCHAR, S_IMG VARCHAR, S_PERSON VARCHAR, S_SKYPE VARCHAR, CART_ID VARCHAR);");
            //db.execSQL("CREATE TABLE IF NOT EXISTS QUERYPRODUCTLAYER(_id INTEGER PRIMARY KEY AUTOINCREMENT, PL-CREATED-TIME VARCHAR, PL-UPD-TIME VARCHAR, PL-PROD-GTIN VARCHAR, PL-PROD-NAME VARCHAR, PL-LNG VARCHAR, PL-BRAND-NAME VARCHAR, PL-BRAND-OWN-NAME VARCHAR);");

            //db.execSQL("CREATE TABLE IF NOT EXISTS PRODUCT_STONE(_id INTEGER PRIMARY KEY AUTOINCREMENT, CERT VARCHAR, SHAPE VARCHAR, CARAT VARCHAR, COLOR VARCHAR, CLARITY VARCHAR, CUT VARCHAR, POLISH VARCHAR, SYMMETRY VARCHAR, MEASUREMENTS VARCHAR, DEPTH VARCHAR, TBL VARCHAR, RATIO VARCHAR, FL VARCHAR, BID VARCHAR, PRICE VARCHAR, DEF VARCHAR, APR VARCHAR, LAB VARCHAR);");

            db.execSQL("CREATE TABLE IF NOT EXISTS DISCOUNTS(_id INTEGER PRIMARY KEY AUTOINCREMENT, TS_VALID_UNTIL INTEGER, EAN_CODE VARCHAR, DISCOUNT VARCHAR, TYPE VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS PRICES(_id INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP INTEGER, EAN_CODE VARCHAR, PRICE VARCHAR, PRICE_CUR VARCHAR);");

            db.execSQL("CREATE TABLE IF NOT EXISTS CATALOGUE(_id INTEGER PRIMARY KEY AUTOINCREMENT, GROUP_ID VARCHAR, CAT_ID VARCHAR, GROUP_EST VARCHAR, CAT_EST VARCHAR, TAGFIELD VARCHAR, GROUP_ENG VARCHAR, CAT_ENG VARCHAR, COUNT INTEGER);");

            db.execSQL("CREATE TABLE IF NOT EXISTS ORDERS(_id INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP INTEGER, QUANTITY INTEGER, ORDER_ID VARCHAR, CART_ID VARCHAR, ACCOUNT_ID VARCHAR, PRICE VARCHAR, PRICE0 VARCHAR, PRICE_CUR VARCHAR, TAX VARCHAR, TAGFIELD VARCHAR );");
*/
        } catch (Exception e) {
            e.printStackTrace();
        }


/*
        if (instance == null) instance = this;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(instance);
        String lang = prefs.getString("locale_override", "");
        if (lang.matches("")) lang = "en_US";
        LanguageUtil.changeLanguageType(instance, new Locale(lang));
*/
        /*
        PLYRestClientConfig config = new PLYRestClientConfig();
        // get your own API key from https://developer.productlayer.com and set it here
        config.apiKey = getString(R.string.product_layer_api_key);

        // create PLYAndroid client
        clientPL = new PLYAndroid(config);
        //clientPL.setLanguage(LocaleUtil.getDefaultLanguage());
*/


        //ApplicationContext.getInstance().init(getApplicationContext());
        //setupDatabase();
        //FacebookSdk.sdkInitialize(getApplicationContext());

        //Fabric.with(this, new Crashlytics());
        //setupFabric();

        //https://github.com/shelajev/Retrofit2SampleApp/blob/master/app/src/main/java/zeroturnaround/org/jrebel4androidgettingstarted/ContributorsApplication.java
/*
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
*/


        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
        } else {
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                String report = "";
                StackTraceElement[] arr = paramThrowable.getStackTrace();
                report = paramThrowable.toString() + "\r\n";
                report += "--------- Stack trace ---------\r\n" + paramThread.toString();
                for (int i = 0; i < arr.length; i++) {
                    report += "    " + arr[i].toString() + "\r\n";
                }

                // If the exception was thrown in a background thread inside
                // AsyncTask, then the actual exception can be found with getCause
                Throwable cause = paramThrowable.getCause();
                if (cause != null) {
                    report += "\n------------ Cause ------------\r\n";
                    report += cause.toString() + "\r\n";
                    arr = cause.getStackTrace();
                    for (int i = 0; i < arr.length; i++) {
                        report += "    " + arr[i].toString() + "\r\n";
                    }
                }

                String rep = "";
                rep += "Time: " + Utilities.GetTimeNow() + "\r\n";
                rep += "Device: " + Usage.getDeviceType() + "\r\n";
                rep += "OS version: " + android.os.Build.VERSION.RELEASE + "\r\n";
                rep += "OS API Level: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.VERSION.SDK_INT + ")\r\n";
                rep += "RAM " + Usage.getUsedMemorySize() + "%\r\n";
                rep += "Total Memory: " + Usage.getTotalMemory() + " B\r\n";
                int allowed = Usage.AllowedMemory(getBaseContext());
                rep += "Allowed memory for appl: " + String.valueOf(allowed) + " MB\r\n";
                rep += "SD Card memory free: " + Usage.sdCardMemoryFree() + "\r\n";
/*
                rep += "Battery life left: " + getBatteryLevel() + "%\r\n";
                rep += "Battery capacity: " + getBatteryCapacity() + " mAh\r\n";
                rep += "Battery voltage: " + BatVoltage + " V\r\n";
                rep += "Battery temperature: " + BatTemp + " C\r\n";
                rep += "CPU temperature: " + CpuTempReader.getTempSimple().toString() + " C\r\n";
*/
                rep += "" + Usage.getBatteryStatus(getBaseContext()) + "\r\n";
                rep += "Screen size: " + Usage.getDeviceScreenResNew(getApplicationContext()) + "\r\n";
                rep += "Rooted: " + Usage.isRooted() + "\r\n";
                rep += "Locale language: " + Locale.getDefault().toString() + "\r\n";
                rep += "Is charging? " + Usage.isCharging(getBaseContext()) + "\r\n";
                rep += "How charging? " + Usage.howCharging(getBaseContext()) + "\r\n";
                rep += "Network access: " + Usage.getNetworkAccess(getBaseContext()) + "\r\n";
                rep += "Is device currently running low on memory? " + Usage.isLowMemory(getBaseContext()) + "\r\n";
                rep += "Is activity on top? " + Usage.isTopActivity(getBaseContext(), "MainActivity") + "\r\n";
                rep += "\r\n";
                rep += "Thread: " + Usage.CollectCrashedThread(paramThread) + "\r\n";
                //rep += "Thread: id=" + paramThread.getId() + "; name=" + paramThread.getName() + "; priority=" + paramThread.getPriority() + "; group=" + paramThread.getThreadGroup().toString() + "\r\n"; //sometimes no data
                rep += "Message: " + paramThrowable.getMessage() + "\r\n";
                //Toast.makeText(this, rep, Toast.LENGTH_SHORT).show();
                Utilities.writeFile("CrashReport.txt", rep + report + "\r\n", false, getApplicationContext(), "");
/*
                SharedStatesMap mSharedStates = SharedStatesMap.getInstance();
                String accountId = mSharedStates.getKey("account_id");
                String playerId = mSharedStates.getKey("player_id");

                //http://cp.commercedisplay.com/drop.ashx?Sender=add_tck&cpt=Exception on device&ercode=D0&player_id=" + player_id + "&username=" + macAddress + "&add_info=" + rep + report;
                MainPresenter mmm = new MainPresenter(null, getBaseContext());
                mmm.sendEmail(playerId, rep + report);
*/
                androidDefaultUEH.uncaughtException(paramThread, paramThrowable);
            }
        });
    }

    private void setupFabric() {
        /*
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret));

        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                //  .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .core(new CrashlyticsCore.Builder().disabled(false).build())
                .build();

        Fabric.with(getApplicationContext(), crashlyticsKit, new Twitter(authConfig));
        Crashlytics.setString("BuildType", BuildConfig.BUILD_TYPE);
        */
    }


}
