package org.firstinspires.ftc.teamcode.util;

public class TeamUtils {

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static <T> T getOrDefault(T value, T ifNull) {
        if (value == null) {
            return ifNull;
        } else {
            return value;
        }
    }

}
