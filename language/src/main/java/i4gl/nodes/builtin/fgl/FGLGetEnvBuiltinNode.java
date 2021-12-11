package i4gl.nodes.builtin.fgl;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.builtin.BuiltinNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;

@NodeInfo(shortName = "fgl_getenv")
public abstract class FGLGetEnvBuiltinNode extends BuiltinNode {

    @Specialization
    protected String getEnv(String name) {
        return System.getenv(name);
    }

    @Override
    public BaseType getReturnType() {
        return TextType.SINGLETON;
    }

}


