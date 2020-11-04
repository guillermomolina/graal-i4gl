package org.guillermomolina.i4gl.nodes.variables.write;

import java.util.Map;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.nodes.variables.read.I4GLReadFromResultNode;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;
import org.guillermomolina.i4gl.runtime.exceptions.UnexpectedRuntimeException;

public class I4GLAssignResultsNode extends I4GLStatementNode {

    private final Map<I4GLReadFromResultNode, I4GLStatementNode> readAssignMap;
    private Object[] results;

    public I4GLAssignResultsNode(final Map<I4GLReadFromResultNode, I4GLStatementNode> readAssignMap) {
        this.readAssignMap = readAssignMap;
    }

    public void setResults(final Object[] results) {

        this.results = results;
    }

    @ExplodeLoop
    private void evaluateResults(VirtualFrame frame) {
        CompilerAsserts.compilationConstant(readAssignMap.size());

        int index = 0;
        for (Map.Entry<I4GLReadFromResultNode, I4GLStatementNode> readAssign:readAssignMap.entrySet()) {
            readAssign.getKey().setResult(results[index++]);
            readAssign.getValue().executeVoid(frame);
        }
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        if(results == null) {
            throw new UnexpectedRuntimeException();
        }
        if (results.length != readAssignMap.size()) {
            throw new IncorrectNumberOfReturnValuesException(readAssignMap.size(), results.length);
        }
        if (!readAssignMap.isEmpty()) {
            evaluateResults(frame);
        }
    }
}
