package com.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import com.guillermomolina.i4gl.I4GLLanguage;
import com.guillermomolina.i4gl.exceptions.NotImplementedException;
import com.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import com.guillermomolina.i4gl.runtime.context.I4GLFunction;
import com.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;
import com.guillermomolina.i4gl.runtime.types.I4GLType;

@NodeInfo(shortName = "INVOKE")
public final class I4GLInvokeNode extends I4GLExpressionNode {

    private final String functionIdentifier;
    @Children private final I4GLExpressionNode[] argumentNodes;
    @CompilationFinal private I4GLFunction cachedFunction;
    @Child private InteropLibrary library;

	public I4GLInvokeNode(String identifier, I4GLExpressionNode[] argumentNodes) {
        this.functionIdentifier = identifier;
        this.argumentNodes = argumentNodes;
        this.library = InteropLibrary.getFactory().createDispatched(3);
	}

    @Override
    public I4GLType getType() {
        throw new NotImplementedException();
    }

    private I4GLFunction getFunction() {
        return lookupContextReference(I4GLLanguage.class).get().getFunctionRegistry().lookup(functionIdentifier, true);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object returnValue = invoke(frame);
        if(returnValue instanceof Object[]) {
            final Object[] returnValues = (Object[]) returnValue;
            throw new IncorrectNumberOfReturnValuesException(1, returnValues.length);
        }
        return returnValue;
    }
    
    public Object invoke(VirtualFrame frame) {
	    if (cachedFunction == null) {
	        CompilerDirectives.transferToInterpreterAndInvalidate();
	        cachedFunction = getFunction();
        }
        Object[] argumentValues = this.evaluateArguments(frame);
        CallTarget function = cachedFunction.getCallTarget();
        return function.call(argumentValues);
	}
    
    @ExplodeLoop
    private Object[] evaluateArguments(VirtualFrame frame) {
        CompilerAsserts.compilationConstant(argumentNodes.length);
        
        Object[] argumentValues = new Object[argumentNodes.length];
        for (int i = 0; i < argumentNodes.length; i++) {
            argumentValues[i] = argumentNodes[i].executeGeneric(frame);
        }

        return argumentValues;
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.CallTag.class) {
            return true;
        }
        return super.hasTag(tag);
    }    
}
