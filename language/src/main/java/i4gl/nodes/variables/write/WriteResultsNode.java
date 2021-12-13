package i4gl.nodes.variables.write;

import java.util.Arrays;
import java.util.Map;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import i4gl.exceptions.IncorrectNumberOfReturnValuesException;
import i4gl.exceptions.UnexpectedRuntimeException;
import i4gl.nodes.statement.StatementNode;
import i4gl.nodes.variables.read.ReadResultsNode;
import i4gl.runtime.values.Null;

public class WriteResultsNode extends StatementNode {

    private final Map<ReadResultsNode, StatementNode> readAssignMap;
    private Object[] results;

    public WriteResultsNode(final Map<ReadResultsNode, StatementNode> readAssignMap) {
        this.readAssignMap = readAssignMap;
    }

    public void setResults(final Object[] results) {
        this.results = results;
    }

    public void setResultsToNull() {
        results = new Object[readAssignMap.size()];
        Arrays.fill(results, Null.SINGLETON);
    }

    public Map<ReadResultsNode, StatementNode> getReadAssignMap() {
        return readAssignMap;
    }

    @ExplodeLoop
    private void evaluateResults(VirtualFrame frame) {
        CompilerAsserts.compilationConstant(readAssignMap.size());

        int index = 0;
        for (Map.Entry<ReadResultsNode, StatementNode> readAssign:readAssignMap.entrySet()) {
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
