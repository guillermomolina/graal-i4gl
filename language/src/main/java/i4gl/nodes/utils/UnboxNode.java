package i4gl.nodes.utils;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.I4GLTypeSystem;
import i4gl.exceptions.NotImplementedException;
import i4gl.exceptions.ShouldNotReachHereException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;

@TypeSystemReference(I4GLTypeSystem.class)
@NodeChild
public abstract class UnboxNode extends ExpressionNode {

    static final int LIMIT = 5;

    @Specialization
    protected static short fromShort(short value) {
        return value;
    }

    @Specialization
    protected static int fromInt(int value) {
        return value;
    }

    @Specialization
    protected static long fromLong(long value) {
        return value;
    }

    @Specialization
    protected static float fromFloat(float value) {
        return value;
    }

    @Specialization
    protected static double fromDouble(double value) {
        return value;
    }

    @Specialization
    protected static String fromString(String value) {
        return value;
    }

    @Specialization(limit = "LIMIT")
    public static Object fromForeign(Object value, @CachedLibrary("value") InteropLibrary interop) {
        try {
            if (interop.fitsInShort(value)) {
                return interop.asShort(value);
            } else if (interop.fitsInInt(value)) {
                return interop.asInt(value);
            } else if (interop.fitsInLong(value)) {
                return interop.asLong(value);
            } else if (interop.fitsInFloat(value)) {
                return interop.asFloat(value);
            } else if (interop.fitsInDouble(value)) {
                return interop.asDouble(value);
            } else if (interop.isString(value)) {
                return interop.asString(value);
            } else {
                return value;
            }
        } catch (UnsupportedMessageException e) {
            throw new ShouldNotReachHereException(e);
        }
    }

    public BaseType getReturnType() {
        throw new NotImplementedException();
    }

}