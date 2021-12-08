package i4gl.nodes.builtin.icgi;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.BuiltinNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.IntType;

@NodeInfo(shortName = "icgi_start")
public abstract class ICGIStartBuiltinNode extends BuiltinNode {

    @Specialization
    public int icgiStart() {
        return 0;
    }

    @Override
    public BaseType getType() {
        return IntType.SINGLETON;
    }
}