package i4gl.nodes.variables.write;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.types.compound.VarcharType;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Varchar;

/**
 * Node representing assignment to a variable of primitive type.
 *
 * This node uses specializations which means that it is not used directly but
 * completed node is generated by Truffle. {@link SimpleAssignmentNodeGen}
 */
@NodeChild(value = "indexNode", type = ExpressionNode.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "type", type = BaseType.class)
public abstract class AssignToTextNode extends StatementNode {

    protected abstract FrameSlot getSlot();

    protected abstract BaseType getType();

    @CompilerDirectives.CompilationFinal
    private int jumps = -1;
   
    protected boolean isChar() {
        return getType() instanceof CharType;
    }
 
    @Specialization(guards = "isChar()")
    void assignChar(VirtualFrame frame, final int index, final String value) {

        Object targetObject = frame.getValue(getSlot());
        if (!(targetObject instanceof Char)) {
            targetObject = getType().getDefaultValue();
            frame.setObject(getSlot(), targetObject);
        }

        final Char target = (Char) targetObject;
        target.setCharAt(index - 1, value.charAt(0));
    }
   
    protected boolean isVarchar() {
        return getType() instanceof VarcharType;
    }
   
    @Specialization(guards = "isVarchar()")
    void assignVarchar(VirtualFrame frame, final int index, final String value) {

        Object targetObject = frame.getValue(getSlot());
        if (!(targetObject instanceof Varchar)) {
            targetObject = getType().getDefaultValue();
            frame.setObject(getSlot(), targetObject);
        }

        final Varchar target = (Varchar) targetObject;
        target.setCharAt(index - 1, value.charAt(0));
    }

    protected boolean isText() {
        return getType() instanceof TextType;
    }

    @Specialization(guards = "isText()")
    void assignText(VirtualFrame frame, final int index, final String value) {

        Object targetObject = frame.getValue(getSlot());
        if (!(targetObject instanceof String)) {
            targetObject = "";
        }
        StringBuilder builder = new StringBuilder((String)targetObject);
        builder.setCharAt(index, value.charAt(0));
        frame.setObject(getSlot(), builder.toString());
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }
}
