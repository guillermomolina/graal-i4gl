package org.guillermomolina.i4gl.nodes.program.arguments;

import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.nodes.call.ReadArgumentNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.nodes.variables.write.SimpleAssignmentNodeGen;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.FileDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.BooleanDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.StringDescriptor;

/**
 * Factory used to create program argument assignment nodes for specific types. It uses combination of
 * {@link org.guillermomolina.i4gl.nodes.variables.write.SimpleAssignmentNode} and {@link ReadArgumentNode}
 * where possible.
 */
public class ProgramArgumentAssignmentFactory {

    public static StatementNode create(FrameSlot frameSlot, int index, TypeDescriptor type) throws LexicalException {
        if (type == IntDescriptor.getInstance() || type == LongDescriptor.getInstance() || type == RealDescriptor.getInstance() ||
                type == BooleanDescriptor.getInstance()) {
            return SimpleAssignmentNodeGen.create(new ReadArgumentNode(index, type), frameSlot);
        } else if (type == StringDescriptor.getInstance()) {
            return new StringProgramArgumentAssignmentNode(frameSlot, index);
        } else if (type instanceof FileDescriptor) {
            return new FileProgramArgumentAssignmentNode(frameSlot, index);
        } else {
            throw new LexicalException("Unsupported program argument type for argument number " + index);
        }
    }

}
