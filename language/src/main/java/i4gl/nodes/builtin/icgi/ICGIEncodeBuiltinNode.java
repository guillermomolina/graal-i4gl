package i4gl.nodes.builtin.icgi;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.BuiltinNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;

@NodeInfo(shortName = "icgi_encode")
public abstract class ICGIEncodeBuiltinNode extends BuiltinNode {

    @Specialization
    public String icgiEncode(String string) {
        return string;
    }

    @Override
    public BaseType getReturnType() {
        return TextType.SINGLETON;
    }

}