package org.guillermomolina.i4gl.parser.utils;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure holding information about a case statement. It contains node of the control expression. nodes defining
 * the cases (condition and statement) and fallback node.
 */
public class CaseStatementData {
    public ExpressionNode caseExpression;
    public final List<ExpressionNode> indexNodes = new ArrayList<>();
    public final List<StatementNode> statementNodes = new ArrayList<>();
    public StatementNode elseNode;
}