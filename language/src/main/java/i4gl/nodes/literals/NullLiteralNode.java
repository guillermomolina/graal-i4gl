package i4gl.nodes.literals;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Null;


@NodeField(name = "type", type = BaseType.class)
public abstract class NullLiteralNode extends ExpressionNode {

    @Specialization(guards = "isDate()")
	public Object nullDate(VirtualFrame frame) {
		return Null.DATE;
	}

    @Fallback
 	public Object nullOther(VirtualFrame frame) {
 		return Null.SINGLETON;
	}

}
