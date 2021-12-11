package i4gl.nodes.builtin.icgi;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.BuiltinNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;

@NodeInfo(shortName = "icgi_getvalue")
public abstract class ICGIGetValueBuiltinNode extends BuiltinNode {

    @Specialization
    protected String getValue(String name) {
        return "value";
    }

    @Override
    public BaseType getReturnType() {
        return TextType.SINGLETON;
    }

}


