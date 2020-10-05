package org.guillermomolina.i4gl.nodes.statement;

import java.io.PrintWriter;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.NullValue;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

@NodeInfo(shortName = "DISPLAY", description = "The node implementing the DISPLAY statement")
public final class I4GLDisplayNode extends I4GLStatementNode {

    @Children
    private final I4GLExpressionNode[] argumentNodes;
    @Child
    private InteropLibrary interop;
    

    public I4GLDisplayNode(I4GLExpressionNode[] argumentNodes) {
        this.argumentNodes = argumentNodes;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final PrintWriter output = I4GLLanguage.getCurrentContext().getOutput();
        for (int i = 0; i < argumentNodes.length; i++) {
            Object value = argumentNodes[i].executeGeneric(frame);
            if (value instanceof Integer) {
                output.printf("%11d", (int) value);
            } else if (value instanceof Long) {
                output.printf("%20d", (long) value);
            } else if (value instanceof Float) {
                output.printf("%14.2f", (float) value);
            } else if (value instanceof Double) {
                output.printf("%14.2f", (double) value);
            } else if (value == NullValue.SINGLETON) {
                I4GLTypeDescriptor type = argumentNodes[i].getType();
                if (type instanceof TextDescriptor) {
                    output.print(type.getDefaultValue());
                } else {
                    throw new I4GLRuntimeException("Variable is NULL and is not a String");
                }
            } else {
                output.print(value.toString());
            }
        }
        output.printf("%n");
    }
}
