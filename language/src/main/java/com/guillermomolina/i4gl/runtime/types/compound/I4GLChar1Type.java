package com.guillermomolina.i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.InteropLibrary;

import com.guillermomolina.i4gl.runtime.values.I4GLChar;

public class I4GLChar1Type extends I4GLCharType {

    public static final I4GLChar1Type SINGLETON = new I4GLChar1Type();

    private I4GLChar1Type() {
        super(1);
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        if(value instanceof I4GLChar) {
            I4GLChar charValue = (I4GLChar)value;
            return charValue.getSize() == 1;
        }
        return false;
    }

}
