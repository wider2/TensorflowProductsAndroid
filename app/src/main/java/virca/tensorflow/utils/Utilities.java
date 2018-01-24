package virca.tensorflow.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.Time;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import virca.tensorflow.R;

public class Utilities {

    public static String readFromFile(String sFileName) {
        int endresult = 0;
        String ret = "er", cur_url = "";
        try {
            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if (isSDPresent) {
                String uri = Environment.getExternalStorageDirectory().toString();
                //uri += mContext.getString(R.string.app_name) + "/BtcDiamondDual/" + sFileName;

                File file = new File(uri);
                if (!file.exists()) return "er";

                FileInputStream inputStream = new FileInputStream(file);
                ArrayList<String> bandWidth = new ArrayList<String>();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    bandWidth.add(receiveString);
                    //if (bandWidth.size() == 10)bandWidth.remove(0); - draw last 10 lines
                }
                bufferedReader.close();

                for (String str : bandWidth)
                    stringBuilder.append(str + "\n");

                ret = stringBuilder.toString();
                inputStream.close();

                endresult = 1;
                //LogLastAction2("Read file OK "+sFileName+" "+endresult+"\t"+ret);
            } else {
                /*
                File file2 = getFileStreamPath(sFileName);
                if (file2.exists()) {
                    cur_url = file2.getAbsolutePath();
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(cur_url));
                    synchronized (buf) {
                    }
                    ret = convertStreamToString(buf);
                    buf.close();
                }
                */
            }
        } catch (FileNotFoundException e) {
            endresult = 2;
            //ret="File not found: " + e.toString();
            ret = "er";
            //LogLastAction2("Read file fail 1 "+sFileName+" "+e.getMessage()+" "+e.toString());
        } catch (IOException e) {
            endresult = 3;
            ret = "er";
            //LogLastAction2("Read file fail 2 "+sFileName+" "+e.getMessage()+" "+e.toString());
        }
        return ret;
    }

    public static String convertStreamToString(InputStream is)
            throws IOException {
            /*
             * To convert the InputStream to String we use the
             * Reader.read(char[] buffer) method. We iterate until the
             * Reader return -1 which means there's no more data to
             * read. We use the StringWriter class to produce the string.
             */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                reader.close();
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public static void writeFile(String filename, String output, boolean b, Context mContext, String catalog) {
        filename = filename.replace(" ", "-");
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        //File root = new File(Environment.getExternalStorageDirectory(), mContext.getString(R.string.app_name));
        if (!root.exists()) root.mkdirs();

        try {
            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if (isSDPresent) {
                if (!catalog.matches("")) catalog = "/" + catalog;
                root = new File(Environment.getExternalStorageDirectory(), mContext.getString(R.string.app_name) + "" + catalog);//Add folder
                if (!root.exists()) root.mkdirs();
                if (root.canWrite()) {
                    File gpxfile = new File(root, filename);//File
                    FileWriter writer = new FileWriter(gpxfile, b);//,true);//file exists - append to it
                    writer.append(output);
                    writer.flush();
                    writer.close();
                    //Toast.makeText(this, sFileName+" saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                //FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
                FileOutputStream fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write(output);
                osw.flush();

                fos.flush();
                fos.getFD().sync();
                osw.close();
                fos.close();
            }
        } catch (IOException e) {
            //Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //http://stackoverflow.com/questions/25785609/android-downloading-a-file-and-saving-on-sd-card
    public static void writeFileInputStream(String filename, String catalog, InputStream inputStream, int totalSize) {

        filename = filename.replace(" ", "-");
        File root = new File(Environment.getExternalStorageDirectory(), "BtcDiamondDual");
        if (!root.exists()) root.mkdirs();

        try {
            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if (isSDPresent) {
                if (!catalog.matches("")) catalog = "/" + catalog;
                root = new File(Environment.getExternalStorageDirectory(), "BtcDiamondDual" + catalog);//Add folder
                if (!root.exists()) root.mkdirs();
                File file = new File(root, filename);

                if (root.getFreeSpace() > totalSize) {
                    if (root.canWrite()) {
                        //if (file.exists())
                        FileOutputStream fileOutput = new FileOutputStream(file);
                        //this is the total size of the file
                        //int totalSize = urlConnection.getContentLength();
                        //variable to store total downloaded bytes
                        int downloadedSize = 0;

                        //create a buffer...
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0; //used to store a temporary size of the buffer

                        //now, read through the input buffer and write the contents to the file
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            //add the data in the buffer to the file in the file output stream (the file on the sd card
                            fileOutput.write(buffer, 0, bufferLength);
                            //add up the size so we know how much is downloaded
                            downloadedSize += bufferLength;
                        }
                        //close the output stream when done
                        fileOutput.close();
                    }
                }
            }
        } catch (IOException e) {
            //Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

    public static int getSpinnerIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    //saving Bitmap to SD card
    public static void writeFileBitmap(String filename, String catalog, Bitmap finalBitmap, int totalSize) {
        filename = filename.replace(" ", "-");
        File root = new File(Environment.getExternalStorageDirectory(), "BtcDiamondDual");
        if (!root.exists()) root.mkdirs();
        try {
            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if (isSDPresent) {
                if (!catalog.matches("")) catalog = "/" + catalog;
                root = new File(Environment.getExternalStorageDirectory(), "BtcDiamondDual" + catalog);//Add folder
                if (!root.exists()) root.mkdirs();
                File file = new File(root, filename);

                if (root.getFreeSpace() > totalSize) {
                    if (root.canWrite()) {
                        FileOutputStream out = new FileOutputStream(file);
                        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        //finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out); //so it is impossible to compare with original filename
                        out.flush();
                        out.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Bitmap getBitmapFromAsset(String strName, Context context) {
        Bitmap bitmap = null;
        AssetManager assetManager = context.getAssets();

        InputStream istr = null;
        try {
            //List<String> mapList = Arrays.asList(assetManager.list(""));
            if (Arrays.asList(context.getAssets().list("")).contains(strName)) {
                //if (Arrays.asList(context.getAssets().list("")).contains(‌​strName)) {
                istr = assetManager.open(strName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (istr != null) bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }


    //https://www.mkyong.com/java/display-a-list-of-countries-in-java/
    public static String getCountryName(String code) {
        String result = "";
        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {
            if (countryCode.equals(code)) {
                Locale obj = new Locale("", countryCode);
                result = obj.getDisplayCountry();
                //System.out.println("Country Code = " + obj.getCountry() + ", Country Name = " + obj.getDisplayCountry());
            }
        }
        return result;
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    public static Integer getZodiak(String dateBirth) {
        Integer zodiakId = 0, sel_seq = 0, iselDay = 0, iselMonth = 0;
        String selDay = "0", selMonth = "0", seq = "";
        //int[,] ZodiakSeq = new int[13, 2];
        Integer listZodiak[][] = new Integer[13][2];

        listZodiak[1][0] = 321;
        listZodiak[1][1] = 419;

        listZodiak[2][0] = 420;
        listZodiak[2][1] = 520;

        listZodiak[3][0] = 521;
        listZodiak[3][1] = 620;

        listZodiak[4][0] = 621;
        listZodiak[4][1] = 722;

        listZodiak[5][0] = 723;
        listZodiak[5][1] = 822;

        listZodiak[6][0] = 823;
        listZodiak[6][1] = 922;

        listZodiak[7][0] = 923;
        listZodiak[7][1] = 1022;

        listZodiak[8][0] = 1023;
        listZodiak[8][1] = 1121;

        listZodiak[9][0] = 1122;
        listZodiak[9][1] = 1221;

        listZodiak[10][0] = 1222;
        listZodiak[10][1] = 119;

        listZodiak[11][0] = 120;
        listZodiak[11][1] = 218;

        listZodiak[12][0] = 219;
        listZodiak[12][1] = 320;


        String[] titems = dateBirth.split("[\\.]");
        for (Integer i = 0; i < titems.length; i++) {
            if (i == 0) selDay = titems[i];
            if (i == 1) selMonth = titems[i];
        }
        iselDay = Integer.parseInt(selDay);
        iselMonth = Integer.parseInt(selMonth);

        if (iselDay < 10) selDay = "0" + iselDay;
        seq = iselMonth + selDay;
        sel_seq = Integer.parseInt(seq);

        if (sel_seq <= 119) zodiakId = 10;
        for (int i = 1; i <= 12; i++) {
            //if (sel_seq <= 119 && sel_seq <= listZodiak[i][1]) zodiakId = i;
            if (listZodiak[i][0] <= sel_seq && sel_seq <= listZodiak[i][1]) {
                zodiakId = i;
            }
            //if ((listZodiak[i][0] <= sel_seq && sel_seq <= listZodiak[i][1]) || (listZodiak[i][0] >= sel_seq && sel_seq <= listZodiak[i][1])) zodiakId = i;
        }
        return zodiakId;
    }

}
