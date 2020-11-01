package org.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.runtime.types.I4GLType;

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
        assert result != null;
        return result;
    }
    
}
