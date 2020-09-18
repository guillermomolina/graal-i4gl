package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

@NodeInfo(shortName = "invoke")
public final class InvokeNode extends ExpressionNode {

    private final I4GLLanguage language;
    private final String functionIdentifier;
    @Children private final ExpressionNode[] argumentNodes;
    @CompilerDirectives.CompilationFinal private CallTarget function;
    @Child private InteropLibrary library;

	public InvokeNode(I4GLLanguage language, String identifier, ExpressionNode[] argumentNodes) {
        this.language = language;
        this.functionIdentifier = identifier;
        this.argumentNodes = argumentNodes;
        this.library = InteropLibrary.getFactory().createDispatched(3);
	}

    @Override
    public TypeDescriptor getType() {
	    return null;
    }

    private CallTarget getFunction() {
        return language.getFunction(this.functionIdentifier);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
	    if (function == null) {
	        CompilerDirectives.transferToInterpreterAndInvalidate();
	        function = getFunction();
        }
        Object[] argumentValues = this.evaluateArguments(frame);

        return function.call(argumentValues);
	}

    @ExplodeLoop
    private Object[] evaluateArguments(VirtualFrame frame) {
        //CompilerAsserts.compilationConstant(argumentNodes.length);
        
        Object[] argumentValues = new Object[argumentNodes.length + 1];
        argumentValues[0] = frame;
        for (int i = 0; i < argumentNodes.length; i++) {
            argumentValues[i+1] = argumentNodes[i].executeGeneric(frame);
        }

        return argumentValues;
    }
    
}
