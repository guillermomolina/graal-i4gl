package com.guillermomolina.i4gl.nodes.statement;

import com.guillermomolina.i4gl.nodes.I4GLTypeSystem;
import com.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import com.guillermomolina.i4gl.runtime.context.I4GLContext;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.nodes.NodeInfo;

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
        if(argument != null) {
            I4GLContext.get(this).getOutput().println(argument.toString());
        }
    }
}
