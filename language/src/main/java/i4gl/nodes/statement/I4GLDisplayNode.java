package i4gl.nodes.statement;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.I4GLTypeSystem;
import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.runtime.context.I4GLContext;

@TypeSystemReference(I4GLTypeSystem.class)
@NodeInfo(shortName = "DISPLAY", description = "The node implementing the DISPLAY statement")
@NodeChild(value = "argument", type = I4GLExpressionNode.class)
public abstract class I4GLDisplayNode extends I4GLStatementNode {

    @Specialization
    public void display(String argument) {
        I4GLContext.get(this).getOutput().println(argument);
    }

    @Specialization
    public void display(Object argument) {
        I4GLContext.get(this).getOutput().println(argument.toString());
    }
/*
    @Specialization
    @TruffleBoundary
    public void println(Object value,
            @CachedLibrary(limit = "3") InteropLibrary interop) {
        I4GLContext.get(this).getOutput().println(interop.toDisplayString(I4GLLanguageView.forValue(value)));
    }*/
}
