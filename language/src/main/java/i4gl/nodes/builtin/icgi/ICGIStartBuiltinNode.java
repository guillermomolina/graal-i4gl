package i4gl.nodes.builtin.icgi;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.I4GLBuiltinNode;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.primitive.I4GLIntType;

@NodeInfo(shortName = "icgi_start")
public abstract class ICGIStartBuiltinNode extends I4GLBuiltinNode {

    @Specialization
    public int icgiStart() {
        return 0;
    }

    @Override
    public I4GLType getType() {
        return I4GLIntType.SINGLETON;
    }
}