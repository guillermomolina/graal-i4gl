package i4gl.nodes.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.runtime.exceptions.GotoException;

/**
 * Block node implementation with extended support of goto statements.
 */
@NodeInfo(shortName = "EXTENDED BLOCK")
public class ExtendedBlockNode extends StatementNode {

    @Children
    private final StatementNode[] bodyNodes;

    private final Map<String, Integer> labelToBlockIndex;

    public ExtendedBlockNode(StatementNode[] bodyNodes) {
        this.labelToBlockIndex = new HashMap<>();
        List<StatementNode> newBodyNodes = this.createNewBodyNodes(bodyNodes);
        this.bodyNodes = newBodyNodes.toArray(new StatementNode[newBodyNodes.size()]);
    }

    private List<StatementNode> createNewBodyNodes(StatementNode[] bodyNodes) {
        if (bodyNodes.length == 0) {
            return Collections.emptyList();
        }

        List<StatementNode> newBodyNodes = new ArrayList<>();
        List<StatementNode> currentBlockNodes;

        currentBlockNodes = startNewBlockWithNode(bodyNodes[0], 0);
        for (int i = 1; i < bodyNodes.length; ++i) {
            StatementNode currentStatement = bodyNodes[i];
            if (!(currentStatement instanceof LabeledStatement)) {
                currentBlockNodes.add(currentStatement);
            } else {
                newBodyNodes.add(createBlockNode(currentBlockNodes));
                currentBlockNodes = startNewBlockWithNode(currentStatement, newBodyNodes.size());
            }
        }
        newBodyNodes.add(createBlockNode(currentBlockNodes));

        return newBodyNodes;
    }

    private List<StatementNode> startNewBlockWithNode(StatementNode newNode, int index) {
        List<StatementNode> blockNodes = new ArrayList<>();
        blockNodes.add(newNode);

        if (newNode instanceof LabeledStatement) {
            String label = ((LabeledStatement) newNode).getLabel();
            this.labelToBlockIndex.put(label, index);
        }

        return blockNodes;
    }

    private BlockNode createBlockNode(List<StatementNode> statementNodes) {
        return new BlockNode(statementNodes.toArray(new StatementNode[statementNodes.size()]));
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        int beginningIndex = 0;
        boolean done = false;
        while (!done) {
            try {
                for (int i = beginningIndex; i < this.bodyNodes.length; ++i) {
                    this.bodyNodes[i].executeVoid(frame);
                }
                done = true;
            } catch (GotoException e) {
                if (this.labelToBlockIndex.containsKey(e.getLabelIdentifier())) {
                    beginningIndex = this.labelToBlockIndex.get(e.getLabelIdentifier());
                } else {
                    throw e;
                }
            }
        }
    }
}
