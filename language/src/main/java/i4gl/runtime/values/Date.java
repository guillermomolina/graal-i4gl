package i4gl.runtime.values;

import java.text.SimpleDateFormat;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.exceptions.I4GLRuntimeException;
import i4gl.runtime.types.compound.DateType;

@ExportLibrary(InteropLibrary.class)
public final class Date implements TruffleObject, Comparable<Date> {
    // private final String DBDATE = "MDY4/";
    private final static String DBDATE = "DMY4/";
    private final static SimpleDateFormat DATE_FORMATER = DbdateToSimpleDateFormat(DBDATE);

    private final java.sql.Date value;

    public Date(java.sql.Date value) {
        this.value = value;
    }

    public Date(long milliseconds) {
        this.value = new java.sql.Date(milliseconds);
    }

    public java.sql.Date getValue() {
        return value;
    }

    @TruffleBoundary
    public int compareTo(Date o) {
        return value.compareTo(o.getValue());
    }

    private static SimpleDateFormat DbdateToSimpleDateFormat(String dbdate) {
        String elements[] = new String[3];
        int elementIndex = 0;
        for (int i = 0; i < 3; i++) {
            char a = dbdate.charAt(elementIndex++);
            switch (a) {
                case 'D':
                case 'd':
                    elements[i] = "dd";
                    break;
                case 'M':
                case 'm':
                    elements[i] = "MM";
                    break;
                case 'Y':
                case 'y':
                    char f = dbdate.charAt(elementIndex++);
                    switch (f) {
                        case '2':
                            elements[i] = "yy";
                            break;
                        case '4':
                            elements[i] = "yyyy";
                            break;
                        default:
                            throw new I4GLRuntimeException("Invalid year format in DBDATE=" + dbdate);
                    }
                    break;
                default:
                    throw new I4GLRuntimeException("Invalid format in DBDATE=" + dbdate);
            }
        }
        String separator = "/";
        if (dbdate.length() > 4) {
            separator = dbdate.substring(4,5);
        }
        String pattern = String.join(separator, elements);
        return new SimpleDateFormat(pattern);
    }

    @Override
    @TruffleBoundary
    public String toString() {
        return DATE_FORMATER.format(value);
    }

    @Override
    @TruffleBoundary
    public boolean equals(Object obj) {
        if (obj instanceof Date) {
            return value.equals(((Date) obj).getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<I4GLContext>> getLanguage() {
        return I4GLLanguage.class;
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return DateType.SINGLETON;
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return value.toString();
    }
}
