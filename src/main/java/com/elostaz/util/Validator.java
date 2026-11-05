package com.elostaz.util;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern PHONE = Pattern.compile("^(\\+20|0)?1[0125][0-9]{8}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validator() {
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidUsername(String value) {
        return value != null && USERNAME.matcher(value).matches();
    }

    public static boolean isValidPassword(String value) {
        return value != null && value.length() >= 6;
    }

    public static boolean isValidPhone(String value) {
        return value != null && PHONE.matcher(value.trim()).matches();
    }

    public static boolean isValidEmail(String value) {
        return value != null && EMAIL.matcher(value.trim()).matches();
    }

    public static boolean isNonNegativeNumber(String value) {
        try {
            return Double.parseDouble(value.trim()) >= 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isNonNegativeInteger(String value) {
        try {
            return Integer.parseInt(value.trim()) >= 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}
