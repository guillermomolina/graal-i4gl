package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.values.Char;

public class Char1Type extends CharType {

    public static final Char1Type SINGLETON = new Char1Type();

    private Char1Type() {
        super(1);
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        if(value instanceof Char) {
            Char charValue = (Char)value;
            return charValue.getSize() == 1;
        }
        return false;
    }

}
