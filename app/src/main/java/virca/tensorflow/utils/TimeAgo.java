package virca.tensorflow.utils;

import android.content.Context;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import virca.tensorflow.R;
import virca.tensorflow.TensorFlowApplication;


public class TimeAgo {

    private static LinkedHashMap<String, Long> times = new LinkedHashMap<>();
    private Context mContext;
    private Date mDate;
    private String weekdays[] = null;
    String weekday_now = "", result = "", dateTimeFormat = "dd.mm.yyyy HH:mm"; // "yyyy-MM-dd HH:mm:ss";
    Calendar c = Calendar.getInstance();
    Integer year;

    static {
/*
        times.put("year", TimeUnit.DAYS.toMillis(365));
        times.put("month", TimeUnit.DAYS.toMillis(30));
        times.put("week", TimeUnit.DAYS.toMillis(7));
        times.put("day", TimeUnit.DAYS.toMillis(1));
        times.put("hour", TimeUnit.HOURS.toMillis(1));
        times.put("minute", TimeUnit.MINUTES.toMillis(1));
        times.put("second", TimeUnit.SECONDS.toMillis(1));

        times.put( mContext.getString(R.string.period_year), TimeUnit.DAYS.toMillis(365));
        times.put( getString(R.string.period_month), TimeUnit.DAYS.toMillis(30));
        times.put( getString(R.string.period_week), TimeUnit.DAYS.toMillis(7));
        times.put( getString(R.string.period_day), TimeUnit.DAYS.toMillis(1));
        times.put( getString(R.string.period_hour), TimeUnit.HOURS.toMillis(1));
        times.put( getString(R.string.period_minute), TimeUnit.MINUTES.toMillis(1));
        times.put( getString(R.string.period_second), TimeUnit.SECONDS.toMillis(1));
*/
    }

    public TimeAgo() {
        mContext = TensorFlowApplication.get();
        Locale current = Locale.getDefault();
        weekdays = new DateFormatSymbols(current).getWeekdays(); //Locale.ENGLISH

        times.put(mContext.getString(R.string.period_year), TimeUnit.DAYS.toMillis(365));
        times.put(mContext.getString(R.string.period_month), TimeUnit.DAYS.toMillis(30));
        times.put(mContext.getString(R.string.period_week), TimeUnit.DAYS.toMillis(7));
        times.put(mContext.getString(R.string.period_day), TimeUnit.DAYS.toMillis(1));
        times.put(mContext.getString(R.string.period_hour), TimeUnit.HOURS.toMillis(1));
        times.put(mContext.getString(R.string.period_minute), TimeUnit.MINUTES.toMillis(1));
        times.put(mContext.getString(R.string.period_second), TimeUnit.SECONDS.toMillis(1));

        Date now = new Date();
        //Calendar c = Calendar.getInstance();
        c.setTime(now);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        weekday_now = weekdays[dayOfWeek];
    }

    //http://stackoverflow.com/questions/4212320/get-the-current-language-in-device
    private String toRelative(long duration, int maxLevel) {
        //Calendar c = Calendar.getInstance();
        c.setTime(mDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String weekday = weekdays[dayOfWeek];

        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<String, Long> time : times.entrySet()) {
            long timeDelta = duration / time.getValue();
            if (timeDelta > 0) {
                res.append(timeDelta)
                        .append(" ")
                        .append(time.getKey());
                if (Locale.getDefault().getLanguage().equals("en"))
                    res.append(timeDelta > 1 ? "s" : "");
                res.append(", ");
                duration -= time.getValue() * timeDelta;
                level++;
            }
            if (level == maxLevel || level == 1) {
                break;
            }
        }
        if ("".equals(res.toString())) {

            //SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
            //Date date;
            //date = formatter.parse(stringDate);
            Calendar cl = Calendar.getInstance();
            cl.setTime(mDate);
            //dayOfWeek = c.get(Calendar.DAY_OF_WEEK)-1;
            //year = c.get(Calendar.YEAR);
            //return "0 seconds ago"; recently
            return mContext.getString(R.string.period_now) + ", " + c.get(Calendar.DATE) + "." + (mDate.getMonth() + 1) + "." + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        } else {
            res.setLength(res.length() - 2);
            res.append(" " + mContext.getString(R.string.period_ago));
            result = "";
            if (!weekday_now.equals(weekday)) result = weekday + ", ";
            result += res.toString();
            return result;
        }
    }

    private String toRelative(long duration) {
        return toRelative(duration, times.size());
    }

    public String toRelative(Date start, Date end) {
        mDate = start;
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime());
    }

    public String toRelative(Date start, Date end, int level) {
        mDate = start;
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime(), level);
    }
}
