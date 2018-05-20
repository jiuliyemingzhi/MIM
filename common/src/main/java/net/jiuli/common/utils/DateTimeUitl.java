package net.jiuli.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jiuli on 17-10-18.
 */

public class DateTimeUitl {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);

    public static String getSampleDate(Date date) {
        return FORMAT.format(date);
    }
}
