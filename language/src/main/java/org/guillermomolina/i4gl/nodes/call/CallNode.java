package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.ReturnValue;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;

@NodeInfo(shortName = "call")
public final class CallNode extends StatementNode {

    private final I4GLLanguage language;
    private final String functionIdentifier;
    @Children private final ExpressionNode[] argumentNodes;
    @Children private final StatementNode[] resultNodes;
    @CompilerDirectives.CompilationFinal private CallTarget function;
    @Child private InteropLibrary library;

	public CallNode(I4GLLanguage language, String identifier, ExpressionNode[] argumentNodes, StatementNode[] resultNodes) {
        this.language = language;
        this.functionIdentifier = identifier;
        this.argumentNodes = argumentNodes;
        this.resultNodes = resultNodes;
        this.library = InteropLibrary.getFactory().createDispatched(3);
	}

    private CallTarget getFunction() {
        return language.getFunction(this.functionIdentifier);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
	    if (function == null) {
	        CompilerDirectives.transferToInterpreterAndInvalidate();
	        function = getFunction();
        }
        Object[] argumentValues = this.evaluateArguments(frame);
        ReturnValue returnValue = (ReturnValue) function.call(argumentValues);
        if(returnValue.getSize() != resultNodes.length) {
            throw new IncorrectNumberOfReturnValuesException(resultNodes.length, returnValue.getSize());
        }
        throw new NotImplementedException();
	}

    @ExplodeLoop
    private Object[] evaluateArguments(VirtualFrame frame) {
        CompilerAsserts.compilationConstant(argumentNodes.length);
        
        Object[] argumentValues = new Object[argumentNodes.length + 1];
        argumentValues[0] = frame;
        for (int i = 0; i < argumentNodes.length; i++) {
            argumentValues[i+1] = argumentNodes[i].executeGeneric(frame);
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
