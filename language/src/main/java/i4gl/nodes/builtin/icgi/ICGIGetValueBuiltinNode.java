package i4gl.nodes.builtin.icgi;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.I4GLBuiltinNode;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLTextType;

@NodeInfo(shortName = "icgi_getvalue")
public abstract class ICGIGetValueBuiltinNode extends I4GLBuiltinNode {

    @Specialization
    String icgiGetValue(Object... arguments) {
        return (arguments.length == 1)? getValue((String) arguments[0]) : getValue((String) arguments[0], (int) arguments[1]);
    }

    public String getValue(String name) {
        return "value";
    }

    public String getValue(String name, int n) {
        return "value";
    }

    @Override
    public I4GLType getType() {
        return I4GLTextType.SINGLETON;
    }

}


