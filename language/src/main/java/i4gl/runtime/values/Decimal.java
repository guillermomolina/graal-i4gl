package i4gl.runtime.values;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;
import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.context.Context;
import i4gl.runtime.types.compound.DecimalType;

@ExportLibrary(InteropLibrary.class)
public final class Decimal implements TruffleObject, Comparable<Decimal> {
    private final DecimalType decimalType;
    private BigDecimal value;

    public Decimal(final DecimalType decimalType) {
        this(decimalType, BigDecimal.ZERO);
    }

    public Decimal(final DecimalType type, final BigDecimal value) {
        this.decimalType = type;
        this.value = value.setScale(type.getScale());
    }

    public Decimal(Decimal decimal) {
        this.decimalType = decimal.decimalType;
        this.value = decimal.value;
    }

    protected BigDecimal getValue() {
        return value;
    }

    public float toSmallFloat() {
        return value.floatValue();
    }

    public double toFloat() {
        return value.doubleValue();
    }

    public void setValue(long aLong) {
        value = BigDecimal.valueOf(aLong).setScale(decimalType.getScale());
    }

    public void setValue(double aDouble) {
        value = BigDecimal.valueOf(aDouble).setScale(decimalType.getScale(), RoundingMode.HALF_DOWN);
    }

    public Object createDeepCopy() {
        return new Decimal(this);
    }

    @TruffleBoundary
    public int compareTo(Decimal o) {
        return value.compareTo(o.getValue());
    }

    @Override
    @TruffleBoundary
    public String toString() {
        int integerPart = decimalType.getPrecision() + 2;
        String format = "%" + integerPart + "." + decimalType.getScale() + "f";
        return String.format(format, value);
    }

    @Override
    @TruffleBoundary
    public boolean equals(Object obj) {
        if (obj instanceof Decimal) {
            return value.equals(((Decimal) obj).getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @ExportMessage
    boolean isNumber() {
        return fitsInLong();
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInByte() {
        throw new NotImplementedException();
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInShort() {
        throw new NotImplementedException();
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInFloat() {
        throw new NotImplementedException();
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInLong() {
        throw new NotImplementedException();
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInInt() {
        throw new NotImplementedException();
    }

    @ExportMessage
    @TruffleBoundary
    boolean fitsInDouble() {
        throw new NotImplementedException();
    }

    @ExportMessage
    @TruffleBoundary
    double asDouble() throws UnsupportedMessageException {
        if (fitsInDouble()) {
            return value.doubleValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    long asLong() throws UnsupportedMessageException {
        if (fitsInLong()) {
            return value.longValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    byte asByte() throws UnsupportedMessageException {
        if (fitsInByte()) {
            return value.byteValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    int asInt() throws UnsupportedMessageException {
        if (fitsInInt()) {
            return value.intValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    float asFloat() throws UnsupportedMessageException {
        if (fitsInFloat()) {
            return value.floatValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    @TruffleBoundary
    short asShort() throws UnsupportedMessageException {
        if (fitsInShort()) {
            return value.shortValue();
        } else {
            throw UnsupportedMessageException.create();
        }
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<Context>> getLanguage() {
        return I4GLLanguage.class;
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return new DecimalType(value.precision(), value.scale());
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return value.toString();
    }

}
