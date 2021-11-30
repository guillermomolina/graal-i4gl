package i4gl.nodes.builtin.icgi;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.I4GLBuiltinNode;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLTextType;

@NodeInfo(shortName = "icgi_decode")
public abstract class ICGIDecodeBuiltinNode extends I4GLBuiltinNode {

    @Specialization
    public String icgiDecode(String string) {
        return string;
    }

    @Override
    public I4GLType getType() {
        return I4GLTextType.SINGLETON;
    }

}