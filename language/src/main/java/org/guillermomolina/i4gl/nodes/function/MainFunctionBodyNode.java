package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.TruffleObject;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.interop.MainFunctionObject;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.root.MainFunctionI4GLRootNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * Node representing body of the main function of input source. It contains a body node which is executed. The returning
 * value is stored in a write only variable of the same name as the program. When executed, it returns the body encapsulated
 * in a {@link MainFunctionObject} instance which provides interoperability of I4GL with other Truffle based languages.
 * This object is returned as result of the evaluation of a I4GL source and may be consecutively executed with arguments.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link MainFunctionBodyNodeGen}
 */
@NodeFields({
    @NodeField(name = "slot", type = FrameSlot.class),
})
public abstract class MainFunctionBodyNode extends ExpressionNode {

    private final I4GLLanguage language;
	private final StatementNode body;
	private final FrameDescriptor frameDescriptor;

    protected abstract FrameSlot getSlot();

	MainFunctionBodyNode(I4GLLanguage language, StatementNode body, FrameDescriptor frameDescriptor) {
        this.language = language;
        this.body = body;
        this.frameDescriptor = frameDescriptor;
    }

	@Specialization
    Object execute() {
        ProcedureBodyNode bodyNode = new ProcedureBodyNode(body);
        MainFunctionI4GLRootNode mainFunctionNode = new MainFunctionI4GLRootNode(language, frameDescriptor, bodyNode);        
        return MainFunctionObject.writeVariable(mainFunctionNode, null);
    }

    @Override
    public TypeDescriptor getType() {
        return null;
    }

}
