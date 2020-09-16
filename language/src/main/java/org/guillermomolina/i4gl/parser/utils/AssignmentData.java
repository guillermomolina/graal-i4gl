package org.guillermomolina.i4gl.parser.utils;
/*
import org.guillermomolina.i4gl.nodes.ExpressionNode;

import org.antlr.v4.runtime.Token;

import java.util.Collections;
import java.util.List;
*/
/**
 * Structure holding information about currently parsed assignment statement. It holds its type (whether it is assignment
 * to a variable, array, record, etc.), node that represents the target expression ({@link org.guillermomolina.i4gl.nodes.variables.read.ReadLocalVariableNode},
 * {@link org.guillermomolina.i4gl.nodes.variables.read.ReadFromArrayNode}, {@link org.guillermomolina.i4gl.nodes.variables.read.ReadFromRecordNode},
 * etc.) and the previous target node (we have to actually use this node to create the assignment node because of the
 * definition of the assignment nodes).
 */
public class AssignmentData {
/*
    public enum Type {
        Simple, Array, Dereference, Record
    }

    public Type type;

    public ExpressionNode targetNode;

    private ExpressionNode nextTargetNode;

    public Token targetIdentifier;

    public ExpressionNode arrayIndexNode;

    public void setSimple(NodeFactory factory, Token identifierToken) {
        this.targetIdentifier = identifierToken;
        this.type = AssignmentData.Type.Simple;

        this.nextTargetNode = factory.createReadVariableNode(identifierToken);
    }

    public void setArray(NodeFactory factory, List<ExpressionNode> indexNodes) {
        this.targetNode = this.nextTargetNode;
        this.type = Type.Array;
        this.arrayIndexNode = indexNodes.get(indexNodes.size() - 1);

        if (indexNodes.size() > 1) {
            this.targetNode = factory.createReadFromArrayNode(this.targetNode, indexNodes.subList(0, indexNodes.size() - 1));
        }
        this.nextTargetNode = factory.createReadFromArrayNode(this.targetNode, Collections.singletonList(this.arrayIndexNode));
    }

    public void setRecord(NodeFactory factory, Token identifierToken) {
        this.targetNode = this.nextTargetNode;
        this.type = Type.Record;
        this.targetIdentifier = identifierToken;

        this.nextTargetNode = factory.createReadFromRecordNode(this.targetNode, identifierToken);
    }

    public void setDereference(NodeFactory factory) {
        this.targetNode = this.nextTargetNode;
        this.type = AssignmentData.Type.Dereference;
        this.nextTargetNode = factory.createReadDereferenceNode(this.targetNode);
    }
*/
}
