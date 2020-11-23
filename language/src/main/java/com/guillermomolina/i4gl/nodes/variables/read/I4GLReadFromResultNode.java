package com.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.guillermomolina.i4gl.exceptions.NotImplementedException;
import com.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import com.guillermomolina.i4gl.runtime.exceptions.UnexpectedRuntimeException;
import com.guillermomolina.i4gl.runtime.types.I4GLType;

public class I4GLReadFromResultNode extends I4GLExpressionNode {
    private Object result;

    public void setResult(final Object result) {
        this.result = result;
    }

    @Override
    public I4GLType getType() {
        throw new NotImplementedException();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if(result == null) {
            throw new UnexpectedRuntimeException();
        }
        return result;
    }
    
}
