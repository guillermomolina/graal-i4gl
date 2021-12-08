package i4gl.nodes.builtin.icgi;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.I4GLBuiltinNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;

@NodeInfo(shortName = "icgi_encode")
public abstract class ICGIEncodeBuiltinNode extends I4GLBuiltinNode {

    @Specialization
    public String icgiEncode(String string) {
        return string;
    }

    @Override
    public BaseType getType() {
        return TextType.SINGLETON;
    }

}