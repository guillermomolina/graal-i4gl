package org.guillermomolina.i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLBigIntType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLFloatType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLIntType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLSmallFloatType;

/**
 * Type descriptor representing the string type.
 */
public class I4GLTextType extends I4GLType {

    public static final I4GLTextType SINGLETON = new I4GLTextType();

    protected I4GLTextType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return library.isString(value);
    }

    @Override
    public Object getDefaultValue() {
        return "";
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public boolean convertibleTo(I4GLType type) {
        return type instanceof I4GLTextType || type == I4GLIntType.SINGLETON || type == I4GLBigIntType.SINGLETON
                || type == I4GLSmallFloatType.SINGLETON || type == I4GLFloatType.SINGLETON;
    }

    @Override
    public String toString() {
        return "TEXT";
    }
}