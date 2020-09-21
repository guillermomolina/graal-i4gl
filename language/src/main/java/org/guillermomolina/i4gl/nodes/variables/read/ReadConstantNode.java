package org.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.ConstantDescriptor;

/**
 * Node representing reading constants.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link ReadConstantNodeGen}
 */
@NodeFields({
    @NodeField(name = "typeDescriptor", type = ConstantDescriptor.class),
})
// TODO: instead of this node we can use literal nodes
public abstract class ReadConstantNode extends ExpressionNode {

	protected abstract ConstantDescriptor getTypeDescriptor();

	@CompilerDirectives.CompilationFinal private int intValue;
    @CompilerDirectives.CompilationFinal private long longValue;
    @CompilerDirectives.CompilationFinal private double doubleValue;
    @CompilerDirectives.CompilationFinal private char charValue;
    @CompilerDirectives.CompilationFinal private Object genericValue;

	ReadConstantNode(Object value) {
	    if (value instanceof Integer) {
	        this.intValue = (int) value;
        } else if (value instanceof Long) {
            this.longValue = (long) value;
        } else if (value instanceof Double) {
            this.doubleValue = (double) value;
        } else if (value instanceof Character) {
            this.charValue= (char) value;
        }
        this.genericValue = value;
    }

    @Specialization(guards = "isInt()")
    int readInt() {
        return this.intValue;
    }

	@Specialization(guards = "isLong()")
    long readLong() {
        return this.longValue;
    }

    @Specialization(guards = "isDouble()")
    double readDouble() {
        return this.doubleValue;
    }

    @Specialization(guards = "isChar()")
    char readChar() {
        return this.charValue;
    }

    @Specialization
    Object readGeneric() {
	    return this.genericValue;
    }

	@Override
    public TypeDescriptor getType() {
	    return this.getTypeDescriptor().getType();
    }

}
