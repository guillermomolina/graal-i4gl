package i4gl.nodes.statement;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.I4GLTypeSystem;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.context.Context;

@TypeSystemReference(I4GLTypeSystem.class)
@NodeInfo(shortName = "DISPLAY", description = "The node implementing the DISPLAY statement")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class DisplayNode extends StatementNode {

    @Specialization
    public void display(String argument) {
        Context.get(this).getOutput().println(argument);
    }

    @Specialization
    public void display(Object argument) {
        Context.get(this).getOutput().println(argument.toString());
    }
/*
    @Specialization
    @TruffleBoundary
    public void println(Object value,
            @CachedLibrary(limit = "3") InteropLibrary interop) {
        I4GLContext.get(this).getOutput().println(interop.toDisplayString(I4GLLanguageView.forValue(value)));
    }*/
}
