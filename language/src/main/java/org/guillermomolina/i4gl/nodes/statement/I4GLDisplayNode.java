package org.guillermomolina.i4gl.nodes.statement;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.I4GLTypeSystem;
import org.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;

@TypeSystemReference(I4GLTypeSystem.class)
@NodeInfo(shortName = "DISPLAY", description = "The node implementing the DISPLAY statement")
@NodeChild(value = "argument", type = I4GLExpressionNode.class)
public abstract class I4GLDisplayNode extends I4GLStatementNode {

    @Specialization
    public void display(String argument) {
        I4GLLanguage.getCurrentContext().getOutput().println(argument);
    }

    @Specialization
    public void display(Object argument) {
        if(argument != null) {
            I4GLLanguage.getCurrentContext().getOutput().println(argument.toString());
        }
    }

}
