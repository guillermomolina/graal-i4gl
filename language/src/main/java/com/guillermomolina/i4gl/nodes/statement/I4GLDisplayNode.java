package com.guillermomolina.i4gl.nodes.statement;

import com.guillermomolina.i4gl.nodes.I4GLTypeSystem;
import com.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import com.guillermomolina.i4gl.runtime.context.I4GLContext;
import com.guillermomolina.i4gl.runtime.context.I4GLLanguageView;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

@TypeSystemReference(I4GLTypeSystem.class)
@NodeInfo(shortName = "DISPLAY", description = "The node implementing the DISPLAY statement")
@NodeChild(value = "argument", type = I4GLExpressionNode.class)
public abstract class I4GLDisplayNode extends I4GLStatementNode {

    @Specialization
    @TruffleBoundary
    public Object println(Object value,
                    @CachedLibrary(limit = "3") InteropLibrary interop) {
        I4GLContext.get(this).getOutput().println(interop.toDisplayString(I4GLLanguageView.forValue(value)));
        return value;
    }

}
