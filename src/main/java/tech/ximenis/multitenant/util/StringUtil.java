package tech.ximenis.multitenant.util;

import java.math.BigDecimal;
import java.util.Objects;

public class StringUtil {
    public static String objectToString(Object object){
        if(Objects.nonNull(object)) {
            return String.valueOf(object);
        }
        return "";
    }

    public static boolean isInteger(String value){
        try {
            Integer.parseInt(value);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isBoolean(String value){
        try {
            Boolean.parseBoolean(value);
        } catch(NullPointerException e) {
            return false;
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isBigDecimal(String value){
        try {
            BigDecimal.valueOf(Double.valueOf(value));
        } catch(NullPointerException e) {
            return false;
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    public static Boolean isNullOrBlank(String string) {
        return Objects.isNull(string) || string.trim().isEmpty();
    }
}
