package virca.tensorflow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import virca.tensorflow.model.ModelScanHistory;
import virca.tensorflow.model.TensorCatalogue;
import virca.tensorflow.model.TensorGroup;
import virca.tensorflow.ui.camera.CameraSource;
import virca.tensorflow.ui.camera.CameraSourcePreview;
import virca.tensorflow.ui.camera.GraphicOverlay;
import virca.tensorflow.utils.ColorNames;
import virca.tensorflow.utils.TimeAgo;

import static virca.tensorflow.utils.ColorNames.getColorNameFromHex;
import static virca.tensorflow.utils.ColorNames.getDominantColor;
import static virca.tensorflow.utils.ColorNames.getRGBArr;


public final class MainActivity extends Activity implements SensorEventListener {

    SensorManager mSensorManager;
    Boolean foundScannedBarcode = false, isScanning = false, oneShotFlag = false, foundTextFlag = false;
    TextView tvOutput, tvTop;
    Button buttonScan, buttonCapture, buttonHistory;
    ImageView edgeDrawArea, ivOutput;
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    List<TensorGroup> listTensorGroup;
    List<TensorCatalogue> listTensorCat;
    List<ModelScanHistory> listHistory;
    Dialog dAdd;
    TimeAgo timeAgo;
    Date now;


    long tStart, tEnd;
    String hexColor = "", colorName = "";


    private static final String TAG = "Barcode-reader";
    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private BarcodeDetector barcodeDetector;
    private TextRecognizer textRecognizer;
    StringBuilder strb;

    private static final int INPUT_SIZE = 224; //mobilenet
    private static final int IMAGE_MEAN = 127; //117;
    private static final float IMAGE_STD = 127f; //117f;

    /*
        private static final String INPUT_NAME = "input";
        private static final String OUTPUT_NAME = "output";
        private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
        private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    */
    //http://nilhcem.com/android/custom-tensorflow-classifier
    private static final String INPUT_NAME = "input"; //Mul, input
    private static final String OUTPUT_NAME = "final_result";
    //private static final String OUTPUT_NAME = "MobilenetV1/Conv2d_0/weights"; //final_result
    private static final String MODEL_FILE = "file:///android_asset/retrained_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/retrained_labels.txt";


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);


        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor linearAcceleration;
        linearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);

        timeAgo = new TimeAgo();


        //http://image-net.org/explore
        listHistory = new ArrayList<>();


        listTensorGroup = new ArrayList<>();
        listTensorGroup.add(new TensorGroup("0", "Material", "Material"));
        listTensorGroup.add(new TensorGroup("1", "Fruit", "Fruit"));
        listTensorGroup.add(new TensorGroup("2", "Vegetable", "Vegetable"));
        listTensorGroup.add(new TensorGroup("3", "Spice", "Spice"));
        listTensorGroup.add(new TensorGroup("4", "Bakery", "Хлебобулочные и кондитерские изделия"));
        listTensorGroup.add(new TensorGroup("5", "Dairy products, eggs and cheese", "Молочные продукты, яйца и сыр"));
        listTensorGroup.add(new TensorGroup("6", "Tank, container, reservoir, jar", "Емкость"));
        listTensorGroup.add(new TensorGroup("7", "Plant", ""));


        listTensorCat = new ArrayList<>();
        listTensorCat.add(new TensorCatalogue("0", "0", "umbrella", ""));
        listTensorCat.add(new TensorCatalogue("1", "1", "strawberry", ""));
        listTensorCat.add(new TensorCatalogue("2", "1", "mango", ""));
        listTensorCat.add(new TensorCatalogue("3", "1", "pear", ""));
        listTensorCat.add(new TensorCatalogue("4", "2", "tomatos", ""));
        listTensorCat.add(new TensorCatalogue("5", "1", "banana", ""));
        listTensorCat.add(new TensorCatalogue("6", "3", "pepper", ""));
        listTensorCat.add(new TensorCatalogue("7", "1", "persimmon", ""));
        listTensorCat.add(new TensorCatalogue("8", "1", "pineapple", ""));
        listTensorCat.add(new TensorCatalogue("9", "4", "bread", ""));
        listTensorCat.add(new TensorCatalogue("10", "1", "lemon", ""));
        listTensorCat.add(new TensorCatalogue("11", "1", "orange", ""));
        listTensorCat.add(new TensorCatalogue("12", "1", "grapefruit", ""));
        listTensorCat.add(new TensorCatalogue("13", "1", "pomegranate", ""));
        listTensorCat.add(new TensorCatalogue("14", "0", "box", ""));
        listTensorCat.add(new TensorCatalogue("15", "2", "cucumber", ""));
        listTensorCat.add(new TensorCatalogue("16", "2", "zucchini", "Цукини"));
        listTensorCat.add(new TensorCatalogue("17", "1", "peach", ""));
        listTensorCat.add(new TensorCatalogue("18", "1", "tangerine", ""));
        listTensorCat.add(new TensorCatalogue("19", "2", "corn", ""));
        listTensorCat.add(new TensorCatalogue("20", "2", "onion", ""));
        listTensorCat.add(new TensorCatalogue("21", "1", "cherry", ""));
        listTensorCat.add(new TensorCatalogue("22", "1", "grape", ""));
        listTensorCat.add(new TensorCatalogue("23", "2", "carrot", ""));
        listTensorCat.add(new TensorCatalogue("24", "5", "cheese", ""));
        listTensorCat.add(new TensorCatalogue("25", "2", "garlic", "чеснок"));
        listTensorCat.add(new TensorCatalogue("26", "2", "cabbage", "капуста"));
        listTensorCat.add(new TensorCatalogue("27", "2", "beet", "свекла"));
        listTensorCat.add(new TensorCatalogue("28", "2", "radish", "редиска"));
        listTensorCat.add(new TensorCatalogue("29", "2", "mushrooms", "грибы"));
        listTensorCat.add(new TensorCatalogue("30", "2", "peas, beans", "горох, фасоль"));
        listTensorCat.add(new TensorCatalogue("31", "2", "potato", ""));
        listTensorCat.add(new TensorCatalogue("32", "1", "avacado", ""));
        listTensorCat.add(new TensorCatalogue("33", "0", "pack", ""));
        listTensorCat.add(new TensorCatalogue("34", "2", "salate", ""));
        listTensorCat.add(new TensorCatalogue("35", "1", "watermelon", ""));
        listTensorCat.add(new TensorCatalogue("36", "5", "eggs", ""));
        listTensorCat.add(new TensorCatalogue("37", "6", "can", "банка"));
        listTensorCat.add(new TensorCatalogue("38", "6", "bottle", "бутылка"));
        listTensorCat.add(new TensorCatalogue("39", "6", "jar", "емкость"));
        listTensorCat.add(new TensorCatalogue("40", "6", "dishes", "посуда"));
        listTensorCat.add(new TensorCatalogue("41", "7", "flower", "")); //plant flower


        boolean autoFocus = true;
        boolean useFlash = false;

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        strb = new StringBuilder();
        //listFoundText = new ArrayList<>();

        edgeDrawArea = (ImageView) findViewById(R.id.edgeImageView);
        //etThreshold = (EditText) findViewById(R.id.et_threshold);
        //etHeight = (EditText) findViewById(R.id.et_height);
        tvTop = (TextView) findViewById(R.id.tv_top);
        tvOutput = (TextView) findViewById(R.id.tv_output);

        buttonCapture = (Button) findViewById(R.id.button_capture);
        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneShotFlag = true;
                strb.setLength(0);
                takePic("");
            }
        });

        buttonHistory = (Button) findViewById(R.id.bt_history);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogOutput();
            }
        });


        initTensorFlowAndLoadModel();
    }


    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        findViewById(R.id.topLayout).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }


    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        barcodeDetector = new BarcodeDetector.Builder(this).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(null, null);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());


        textRecognizer = new TextRecognizer.Builder(this).build();


        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }

        if (mCameraSource != null) mCameraSource.stop();
        barcodeDetector.release();
        textRecognizer.release();
        classifier = null;
        executor = null;
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }


    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
                //last = new Mat(mGraphicOverlay.getHeight(), mGraphicOverlay.getWidth(), CvType.CV_8UC4); //init
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private boolean onTap(float rawX, float rawY) {
        //https://stackoverflow.com/questions/36265659/how-capture-picture-while-mobile-vision-api-face-tracking
        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
            //private File imageFile;
            @Override
            public void onPictureTaken(byte[] bytes) {
                try {
                    // convert byte array into bitmap
                    Bitmap loadedImage = null;
                    Bitmap rotatedBitmap = null;
                    //last = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
                    //loadedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    boolean success = true;
                    //takePic2(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        }

        if (best != null) {
            Intent data = new Intent();
            data.putExtra(BarcodeObject, best);
            setResult(CommonStatusCodes.SUCCESS, data);
            finish();
            return true;
        }
        return false;
    }


    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            takePic("");
            return false;
            //return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private void takePic(final String param) {

        tvOutput.setText("");
        mCameraSource.takePicture(shutterCallback, new CameraSource.PictureCallback() {
            //private File imageFile;
            @Override
            public void onPictureTaken(byte[] bytes) {
                try {
                    Integer iDominantColor;
                    String group_name = "", ids = "";
                    strb.setLength(0);
                    strb.append("<u><b>Results</b></u><br />");
                    tStart = SystemClock.currentThreadTimeMillis();


                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap bitmapTreat = bitmap;

                    //https://coderwall.com/p/wzinww/resize-an-image-in-android-to-a-given-width
                    int origWidth = bitmap.getWidth();
                    int origHeight = bitmap.getHeight();
                    int destWidth = INPUT_SIZE; //default

                    if (origWidth > destWidth) {
                        // picture is wider than we want it, we calculate its target height
                        int destHeight = origHeight / (origWidth / destWidth);
                        // we create an scaled bitmap so it reduces the image, not just trim it
                        bitmapTreat = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);
                    }
                    //there is already a built-in way to accomplish this and it's 1 line of code ThumbnailUtils.extractThumbnail()
                    Bitmap croppedBitmap = ThumbnailUtils.extractThumbnail(bitmapTreat, INPUT_SIZE, INPUT_SIZE);
                    //bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
                    //imageViewResult.setImageBitmap(bitmap);
                    //Bitmap croppedBitmap = bitmap;
                    edgeDrawArea.setImageBitmap(croppedBitmap);


                    List<Classifier.Recognition> results = new ArrayList<Classifier.Recognition>();
                    //final List<Classifier.Recognition> results = classifier.recognizeImage(croppedBitmap);
                    if (croppedBitmap != null) {
                        results = classifier.recognizeImage(croppedBitmap);
                    }


                    iDominantColor = getDominantColor(croppedBitmap);
                    int[] rgb = getRGBArr((Integer) iDominantColor);
                    hexColor = "#" + Integer.toHexString(rgb[0]) + "" + Integer.toHexString(rgb[1]) + "" + Integer.toHexString(rgb[2]);
                    colorName = ColorNames.getColorNameFromRgb((int) rgb[0], (int) rgb[1], (int) rgb[2]);
                    //strb.append("<br />clr: " + iDominantColor + ", rgb: " + rgb + ", hexColor: " + hexColor + ", colorName: " + colorName);

                    strb.append("<font color='" + hexColor + "'>");
                    if (results.size() <= 0) {
                        strb.append(getString(R.string.object_not_recognized) + "<br />");
                    } else {
                        for (Integer i = 0; i < results.size(); i++) {
                            if (i <= 1) {
                                for (TensorCatalogue cat : listTensorCat) {
                                    if (cat.category_eng.equals(results.get(i).getTitle())) {
                                        for (TensorGroup group : listTensorGroup) {
                                            if (group.group_id.equals(cat.group_id))
                                                group_name = group.group_eng;
                                        }
                                        strb.append("(" + group_name + ") <b>" + results.get(i).getTitle() + "</b> (" + String.format("%.2f", (results.get(i).getConfidence() * 100)) + "%)<br />");
                                        ids += "(" + group_name + ") " + results.get(i).getTitle() + " (" + String.format("%.2f", (results.get(i).getConfidence() * 100)) + "%)<br />";
                                    }
                                }
                            }
                        }
                        //strb.append(results.toString() + "<br />");
                    }
                    strb.append("Color: " + hexColor + ", " + colorName + "<br />");

                    visionApiBarcode(bitmap, ids, hexColor);
                    isScanning = false;

                } catch (Exception e) {
                    e.printStackTrace();
                    strb.append(e.getMessage() + "<br />");
                }
            }
        });
    }

    private final CameraSource.ShutterCallback shutterCallback = new CameraSource.ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                mgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
        }
    };


    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }


    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                    //makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }


    private void visionApiBarcode(Bitmap bMap, String ids, String hexColor) {//(Mat mYuv) {
        StringBuilder bar = new StringBuilder();
        StringBuilder txt = new StringBuilder();
        //String text="";
        //Bitmap bMap = Bitmap.createBitmap(mYuv.width(), mYuv.height(), Bitmap.Config.ARGB_8888);
        //Utils.matToBitmap(mYuv, bMap);

        //BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getBaseContext()).build();
        //byte[] imageData = new byte[image.getPlanes()[0].getBuffer().remaining()];
        //Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        Frame frame = new Frame.Builder().setBitmap(bMap).build();
        SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
        if (barcodes.size() > 0) {
            foundScannedBarcode = true;
            //System.out.println(barcodes.valueAt(0).displayValue);
            for (Integer i = 0; i < barcodes.size(); i++) {
                //tvOutput.append("\nBar code found: " + barcodes.valueAt(i).rawValue);
                strb.append("Bar code found: <b>" + barcodes.valueAt(i).rawValue + "</b><br />");
                bar.append(barcodes.valueAt(i).rawValue + "<br />");
                //barcodeFound = barcodes.valueAt(i).rawValue;
                //Toast.makeText(getBaseContext(), "Text: " + barcodes.valueAt(i).displayValue, Toast.LENGTH_SHORT).show();
            }
        }
        //if (!foundScannedBarcode) processImageMat(mYuv);
        //visionOcr(bMap);

        if (!foundScannedBarcode) {
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                //strb.append("Text found: <b>" + textBlock.getValue() + "</b><br />");
                Log.i(TAG, textBlock.getValue());

                List<? extends Text> textComponents = textBlock.getComponents();
                for (Text currentText : textComponents) {
                    //strb.append("Text found: <b>" + currentText.getValue() + "</b><br />");
                    txt.append(currentText.getValue() + "<br />");
                    //listFoundText.add(currentText.getValue());
                }
            }
            if (txt.length() > 0) strb.append("Text found: <b>" + txt.toString() + "</b><br />");
        }
        //tvOutput.append(strb.toString());


        //long tEnd = System.currentTimeMillis();
        tEnd = SystemClock.currentThreadTimeMillis();
        long tResult = tEnd - tStart;

        strb.append("Time elapsed: " + (tResult) + "ms.");
        strb.append("</font>");
        tvOutput.setText(Html.fromHtml(strb.toString()), TextView.BufferType.SPANNABLE);


        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
        long longTs = currentTimestamp.getTime();

        listHistory.add(new ModelScanHistory(ids, bar.toString(), txt.toString(), hexColor, longTs));
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Float x = 0f, y = 0f, z = 0f;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        if (!isScanning) {
            if (Math.abs(x) > 0.55 || Math.abs(y) > 0.55 || Math.abs(z) > 0.55) {
                isScanning = true;
                tvTop.setText(String.format("%.2f", event.values[0]) + "; " + String.format("%.2f", event.values[1]) + "; " + String.format("%.2f", event.values[2]) + "");
                //takePic("");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private void showDialogOutput() {
        String elapsed = "", objects = "";
        strb.setLength(0);
        now = new Date();
        for (ModelScanHistory item : listHistory) {
            if (item.hexColor.length() >=6) colorName = getColorNameFromHex(item.hexColor);
            elapsed = timeAgo.toRelative(new Date(Long.parseLong(String.valueOf(item.timestamp))), now);
            objects = item.cat_id;
            if (objects.length() <= 0) {
                objects = (getString(R.string.object_not_recognized) + "<br />");
            }
            strb.append("<font color='" + item.hexColor + "'>" + elapsed + "<br /><b>" + objects + "</b>color: " + item.hexColor + ", " + colorName + "<br />" + item.barcode + "" + item.text + "</font><br />");
        }

        dAdd = new Dialog(this, R.style.DialogNoPaddingNoTitle);
        dAdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dAdd.setContentView(R.layout.dialog_output);
        dAdd.setCancelable(true);
        dAdd.setCanceledOnTouchOutside(true);

        LinearLayout dLayoutWant = (LinearLayout) dAdd.findViewById(R.id.dialog_layout_output);
        dLayoutWant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dAdd.dismiss();
            }
        });

        TextView tvOutput = (TextView) dAdd.findViewById(R.id.dialog_tv_output);
        tvOutput.setText(Html.fromHtml(strb.toString()));
        //tvOutput.setText(Html.fromHtml(listHistory.toString()), TextView.BufferType.SPANNABLE);


        Window window = dAdd.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dAdd.show();
    }


}
