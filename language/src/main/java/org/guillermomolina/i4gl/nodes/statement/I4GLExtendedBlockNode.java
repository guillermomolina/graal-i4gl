package org.guillermomolina.i4gl.nodes.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.runtime.exceptions.GotoException;

/**
 * Block node implementation with extended support of goto statements.
 */
@NodeInfo(shortName = "EXTENDED BLOCK")
public class I4GLExtendedBlockNode extends I4GLStatementNode {

    @Children
    private final I4GLStatementNode[] bodyNodes;

    private final Map<String, Integer> labelToBlockIndex;

    public I4GLExtendedBlockNode(I4GLStatementNode[] bodyNodes) {
        this.labelToBlockIndex = new HashMap<>();
        List<I4GLStatementNode> newBodyNodes = this.createNewBodyNodes(bodyNodes);
        this.bodyNodes = newBodyNodes.toArray(new I4GLStatementNode[newBodyNodes.size()]);
    }

    private List<I4GLStatementNode> createNewBodyNodes(I4GLStatementNode[] bodyNodes) {
        if (bodyNodes.length == 0) {
            return Collections.emptyList();
        }

        List<I4GLStatementNode> newBodyNodes = new ArrayList<>();
        List<I4GLStatementNode> currentBlockNodes;

        currentBlockNodes = startNewBlockWithNode(bodyNodes[0], 0);
        for (int i = 1; i < bodyNodes.length; ++i) {
            I4GLStatementNode currentStatement = bodyNodes[i];
            if (!(currentStatement instanceof I4GLLabeledStatement)) {
                currentBlockNodes.add(currentStatement);
            } else {
                newBodyNodes.add(createBlockNode(currentBlockNodes));
                currentBlockNodes = startNewBlockWithNode(currentStatement, newBodyNodes.size());
            }
        }
        newBodyNodes.add(createBlockNode(currentBlockNodes));

        return newBodyNodes;
    }

    private List<I4GLStatementNode> startNewBlockWithNode(I4GLStatementNode newNode, int index) {
        List<I4GLStatementNode> blockNodes = new ArrayList<>();
        blockNodes.add(newNode);

        if (newNode instanceof I4GLLabeledStatement) {
            String label = ((I4GLLabeledStatement) newNode).getLabel();
            this.labelToBlockIndex.put(label, index);
        }

        return blockNodes;
    }

    private I4GLBlockNode createBlockNode(List<I4GLStatementNode> statementNodes) {
        return new I4GLBlockNode(statementNodes.toArray(new I4GLStatementNode[statementNodes.size()]));
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
