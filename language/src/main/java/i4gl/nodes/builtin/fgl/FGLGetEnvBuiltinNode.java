package i4gl.nodes.builtin.fgl;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.I4GLBuiltinNode;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLTextType;

@NodeInfo(shortName = "fgl_getenv")
public abstract class FGLGetEnvBuiltinNode extends I4GLBuiltinNode {

    @Specialization
    protected String getEnv(String name) {
        return System.getenv(name);
    }

    @Override
    public I4GLType getType() {
        return I4GLTextType.SINGLETON;
    }

}


