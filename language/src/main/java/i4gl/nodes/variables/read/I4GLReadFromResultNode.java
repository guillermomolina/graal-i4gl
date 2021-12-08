package i4gl.nodes.variables.read;

import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.exceptions.NotImplementedException;
import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.runtime.exceptions.UnexpectedRuntimeException;
import i4gl.runtime.types.BaseType;

public class I4GLReadFromResultNode extends I4GLExpressionNode {
    private Object result;

    public void setResult(final Object result) {
        this.result = result;
    }

    @Override
    public BaseType getType() {
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
