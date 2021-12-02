package i4gl.common;

import java.text.DecimalFormat;

public class NumberFormatter {
    public static String Format(String format, int data) {
        int formatLength = format.length();
        Boolean isNegativeData = data < 0;
        int unsignedData = isNegativeData ? -data : data;
        String modifiedFormat = "";
        Boolean isMoney = false;
        Boolean isNegativeFormat = false;
        Boolean isLeftJustified = false;
        int dolarEndPosition = -1;
        int minusEndPosition = -1;
        int parenthesisEndPosition = -1;
        String blankCharacter = " ";
        for (int i = 0; i < formatLength; i++) {
            char c = format.charAt(i);
            if (c == '$') {
                if (!isMoney) {
                    isMoney = true;
                }
                dolarEndPosition = i;
                c = '#';
            }
            if (c == '-') {
                assert parenthesisEndPosition == -1;
                if (!isNegativeFormat) {
                    isNegativeFormat = true;
                    isLeftJustified = true;
                }
                minusEndPosition = i;
                c = '#';
            }
            if (c == '(') {
                assert minusEndPosition == -1;
                if (!isNegativeFormat) {
                    isNegativeFormat = true;
                    isLeftJustified = true;
                }
                parenthesisEndPosition = i;
                c = '#';
            }
            if (c == ')') {
                assert minusEndPosition == -1;
                assert parenthesisEndPosition >= 0;
                assert i == formatLength - 1;
                assert modifiedFormat.length() == formatLength - 1;
            }
            if (c == '&') {
                c = '0';
            }
            if (c == '<') {
                isLeftJustified = true;
                c = '#';
            }
            if (c == '*') {
                blankCharacter = "*";
                c = '#';
            }
            modifiedFormat += c;
        }
        DecimalFormat df = new DecimalFormat(modifiedFormat);
        String output = df.format(unsignedData);
        if (data == 0) {
            if(format.endsWith("<")) {
                return null;
            }
            if (!modifiedFormat.endsWith("0")) {
                output = "";
            }
        }
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
        if (isMoney) {
            int spacesCount = formatLength - (output.length() + 1);
            spacesCount -= dolarEndPosition;
            String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
            output = "$" + spaces + output;
        }
        if (isNegativeFormat) {
            if (isNegativeData) {
                if (minusEndPosition != -1) {
                    int spacesCount = formatLength - (output.length() + 1);
                    spacesCount -= minusEndPosition;
                    String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
                    output = "-" + spaces + output;
                }
                if (parenthesisEndPosition != -1) {
                    int spacesCount = formatLength - (output.length() + 1);
                    spacesCount -= parenthesisEndPosition;
                    String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
                    output = "(" + spaces + output;
                }
            } else {
                if (parenthesisEndPosition != -1) {
                    assert output.charAt(output.length() - 1) == ')';
                    output = output.substring(0, output.length() - 1);
                }
            }
        }
        if (output.length() > formatLength) {
            return "*".repeat(formatLength);
        }
        if (!isLeftJustified) {
            int spacesCount = formatLength - output.length();
            String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
            output = spaces + output;
        }
        return output;
    }

    public static String Format(String format, double data) {
        int formatLength = format.length();
        Boolean isNegativeData = data < 0;
        double unsignedData = isNegativeData ? -data : data;
        int iod = format.indexOf(".");
        String modifiedFormat = "";
        Boolean isMoney = false;
        Boolean isNegativeFormat = false;
        Boolean isLeftJustified = false;
        int dolarEndPosition = -1;
        int minusEndPosition = -1;
        int parenthesisEndPosition = -1;
        String blankCharacter = " ";
        for (int i = 0; i < formatLength; i++) {
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
                assert parenthesisEndPosition == -1;
                if (!isNegativeFormat) {
                    isNegativeFormat = true;
                    isLeftJustified = true;
                }
                minusEndPosition = i;
                c = '#';
            }
            if (c == '(') {
                assert minusEndPosition == -1;
                if (!isNegativeFormat) {
                    isNegativeFormat = true;
                    isLeftJustified = true;
                }
                parenthesisEndPosition = i;
                c = '#';
            }
            if (c == ')') {
                assert minusEndPosition == -1;
                assert parenthesisEndPosition >= 0;
                assert i == formatLength - 1;
                assert modifiedFormat.length() == formatLength - 1;
            }
            if (c == '&') {
                c = '0';
            }
            if (c == '<') {
                isLeftJustified = true;
                c = '#';
            }
            if (c == '*') {
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
            if (i > iod && c != ')') {
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
        if (isMoney) {
            int spacesCount = formatLength - (output.length() + 1);
            spacesCount -= dolarEndPosition;
            String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
            output = "$" + spaces + output;
        }
        if (isNegativeFormat) {
            if (isNegativeData) {
                if (minusEndPosition != -1) {
                    int spacesCount = formatLength - (output.length() + 1);
                    spacesCount -= minusEndPosition;
                    String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
                    output = "-" + spaces + output;
                }
                if (parenthesisEndPosition != -1) {
                    int spacesCount = formatLength - (output.length() + 1);
                    spacesCount -= parenthesisEndPosition;
                    String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
                    output = "(" + spaces + output;
                }
            } else {
                if (parenthesisEndPosition != -1) {
                    assert output.charAt(output.length() - 1) == ')';
                    output = output.substring(0, output.length() - 1);
                }
            }
        }
        if (output.length() > formatLength) {
            return "*".repeat(formatLength);
        }
        if (!isLeftJustified) {
            int spacesCount = formatLength - output.length();
            String spaces = spacesCount > 0 ? blankCharacter.repeat(spacesCount) : "";
            output = spaces + output;
        }
        return output;
    }
}
