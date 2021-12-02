package i4gl.common;

import java.text.DecimalFormat;

import i4gl.exceptions.NotImplementedException;

public class NumberFormatter {
    public static String Format(String format, int data) {
        if (format.contains("$")) {
            return FormatDolar(format, data);
        }
        if (format.contains("#")) {
            return FormatSharp(format, data);
        }
        if (format.contains("&")) {
            return FormatZero(format, data);
        }
        if (format.contains("*")) {
            return FormatAsterisk(format, data);
        }
        if (format.contains("<")) {
            return FormatLeft(format, data);
        }
        throw new NotImplementedException("Unrecognized format " + format);
    }

    public static String Format(String format, double data) {
        Boolean isNegativeData = data < 0;
        double unsignedData = isNegativeData ? -data : data;
        int iod = format.indexOf(".");
        String modifiedFormat = "";
        Boolean isMoney = false;
        Boolean isNegativeFormat = false;
        Boolean isLeftJustified = false;
        int dolarEndPosition = -1;
        int minusEndPosition = -1;
        String blankCharacter = " ";
        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);
            if (c == '$') {
                if (!isMoney) {
                    isMoney = true;
                    isLeftJustified = true;
                }
                dolarEndPosition = i;
                c = '#';
            }
            if (c == '-') {
                if (!isNegativeFormat) {
                    isNegativeFormat = true;
                    isLeftJustified = true;
                }
                minusEndPosition = i;
                c = '#';
            }
            if (c == '&') {
                c = '0';
            }
            if (c == '<') {
                isLeftJustified = true;
                c = '#';
            }
            if(c == '*') {
                blankCharacter = "*";
                c = '#';
            }
            if (i < iod) {

            }
            if (i == iod - 1 && unsignedData >= 1) {
                c = '0';
            }
            if (i == iod) {
            }
            if (i > iod) {
                c = '0';
            }
            modifiedFormat += c;
        }
        DecimalFormat df = new DecimalFormat(modifiedFormat);
        String output = df.format(unsignedData);
        StringBuilder sb = new StringBuilder(output);
        for (int i = 0; i < output.length(); i++) {
            char c = output.charAt(i);
            if (c == ',') {
                sb.setCharAt(i, '0');
            } else if (c != '0') {
                break;
            }
        }
        output = sb.toString();
        int length = modifiedFormat.length();
        if (isMoney) {
            int spacesCount = length - (output.length() + 1);
            spacesCount -= dolarEndPosition;
            String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
            output = "$" + spaces + output;
        }
        if (isNegativeFormat && isNegativeData) {
            int spacesCount = length - (output.length() + 1);
            spacesCount -= minusEndPosition;
            String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
            output = "-" + spaces + output;
        }
        if (output.length() > length) {
            return "*".repeat(length);
        }
        if (!isLeftJustified) {
            int spacesCount = length - output.length();
            String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
            output = spaces + output;
        }
        return output;
    }

    public static String FormatSharp(String format, int data) {
        String modifiedFormat = format;
        DecimalFormat df = new DecimalFormat(modifiedFormat);
        Boolean isNegative = data < 0;
        int unsignedData = isNegative ? -data : data;
        String output = df.format(unsignedData);
        if (unsignedData == 0) {
            if (format.endsWith("#")) {
                output = "";
            }
        }
        int length = format.length();
        output = String.format("%" + length + "s", output);
        return output;
    }

    public static String FormatZero(String format, int data) {
        String modifiedFormat = format.replace("&", "0");
        DecimalFormat df = new DecimalFormat(modifiedFormat);
        Boolean isNegative = data < 0;
        int unsignedData = isNegative ? -data : data;
        String output = df.format(unsignedData);
        StringBuilder sb = new StringBuilder(output);
        for (int i = 0; i < output.length(); i++) {
            char c = output.charAt(i);
            if (c == ',') {
                sb.setCharAt(i, '0');
            } else if (c != '0') {
                break;
            }
        }
        output = sb.toString();
        return output;
    }

    public static String FormatDolar(String format, int data) {
        String modifiedFormat = format.replace("$", "#");
        DecimalFormat df = new DecimalFormat(modifiedFormat);
        String output = "$" + df.format(data);
        if (data == 0) {
            if (format.endsWith("$")) {
                output = "$";
            }
        }
        int length = modifiedFormat.length();
        if (output.length() > length) {
            return "*".repeat(length);
        }
        output = String.format("%" + length + "s", output);
        return output;
    }

    public static String FormatAsterisk(String format, int data) {
        String modifiedFormat = format.replace("*", "#");
        DecimalFormat df = new DecimalFormat(modifiedFormat);
        String output = df.format(data);
        if (data == 0) {
            if (format.endsWith("*")) {
                output = output.replace("0", " ");
            }
        }
        int length = format.length();
        output = String.format("%" + length + "s", output);
        output = output.replace(" ", "*");
        return output;
    }

    public static String FormatLeft(String format, int data) {
        String modifiedFormat = format.replace("<", "#");
        DecimalFormat df = new DecimalFormat(modifiedFormat);
        String output = df.format(data);
        if (data == 0) {
            if (format.endsWith("<")) {
                return null;
            }
        }
        return output;
    }
}
