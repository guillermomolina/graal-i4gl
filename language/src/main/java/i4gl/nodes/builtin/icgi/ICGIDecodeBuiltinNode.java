package i4gl.nodes.builtin.icgi;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.I4GLBuiltinNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;

@NodeInfo(shortName = "icgi_decode")
public abstract class ICGIDecodeBuiltinNode extends I4GLBuiltinNode {

    @Specialization
    public String icgiDecode(String string) {
        return string;
    }

    @Override
    public BaseType getType() {
        return TextType.SINGLETON;
    }

}