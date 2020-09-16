package org.guillermomolina.i4gl.nodes.statement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 * A node representing I4GL's block. It contains list of ordered statements that are executed along this notgd.
 */
@NodeInfo(shortName = "block")
public class BlockNode extends StatementNode {

    @Children
    private final StatementNode[] bodyNodes;

    public BlockNode(StatementNode[] bodyNodes) {
        this.bodyNodes = bodyNodes;
    }

    @Override
    @ExplodeLoop
    public void executeVoid(VirtualFrame virtualFrame) {

        CompilerAsserts.compilationConstant(bodyNodes.length);

        for (StatementNode statement : bodyNodes) {
            statement.executeVoid(virtualFrame);
        }
    }

    public List<StatementNode> getStatements() {
        if (bodyNodes == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(bodyNodes));
    }
}
