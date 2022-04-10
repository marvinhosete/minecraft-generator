package me.tuskdev.generator.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String format(long time) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        long minutes = (seconds/60);
        return String.format("%sm %ss", minutes, (seconds - (minutes*60)));
    }

}
