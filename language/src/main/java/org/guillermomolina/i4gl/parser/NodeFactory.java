package org.guillermomolina.i4gl.parser;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.InitializationNodeFactory;
import org.guillermomolina.i4gl.nodes.arithmetic.AddNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.DivideIntegerNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.DivideNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.ModuloNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.MultiplyNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.SubtractNodeGen;
import org.guillermomolina.i4gl.nodes.call.InvokeNode;
import org.guillermomolina.i4gl.nodes.call.ReadArgumentNode;
import org.guillermomolina.i4gl.nodes.call.ReadFromReturnNodeGen;
import org.guillermomolina.i4gl.nodes.call.ReturnNode;
import org.guillermomolina.i4gl.nodes.control.CaseNode;
import org.guillermomolina.i4gl.nodes.control.DebuggerNode;
import org.guillermomolina.i4gl.nodes.control.ForNode;
import org.guillermomolina.i4gl.nodes.control.IfNode;
import org.guillermomolina.i4gl.nodes.control.WhileNode;
import org.guillermomolina.i4gl.nodes.literals.DoubleLiteralNode;
import org.guillermomolina.i4gl.nodes.literals.DoubleLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.I4GLStringLiteralNode;
import org.guillermomolina.i4gl.nodes.literals.I4GLStringLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.IntLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.LongLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.logic.AndNodeGen;
import org.guillermomolina.i4gl.nodes.logic.EqualsNodeGen;
import org.guillermomolina.i4gl.nodes.logic.LessThanNodeGen;
import org.guillermomolina.i4gl.nodes.logic.LessThanOrEqualNodeGen;
import org.guillermomolina.i4gl.nodes.logic.LikeNodeGen;
import org.guillermomolina.i4gl.nodes.logic.MatchesNodeGen;
import org.guillermomolina.i4gl.nodes.logic.NotNode;
import org.guillermomolina.i4gl.nodes.logic.NotNodeGen;
import org.guillermomolina.i4gl.nodes.logic.OrNodeGen;
import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.nodes.root.MainFunctionRootNode;
import org.guillermomolina.i4gl.nodes.statement.BlockNode;
import org.guillermomolina.i4gl.nodes.statement.ConnectToDatabaseNode;
import org.guillermomolina.i4gl.nodes.statement.DisplayNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.nodes.variables.read.ReadConstantNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.ReadFromArrayNode;
import org.guillermomolina.i4gl.nodes.variables.read.ReadFromArrayNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.ReadFromRecordNode;
import org.guillermomolina.i4gl.nodes.variables.read.ReadFromRecordNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.ReadGlobalVariableNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.ReadLocalVariableNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.AssignToArrayNode;
import org.guillermomolina.i4gl.nodes.variables.write.AssignToArrayNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.AssignToRecordField;
import org.guillermomolina.i4gl.nodes.variables.write.AssignToRecordFieldNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.SimpleAssignmentNode;
import org.guillermomolina.i4gl.nodes.variables.write.SimpleAssignmentNodeGen;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.exceptions.ParseException;
import org.guillermomolina.i4gl.parser.exceptions.TypeMismatchException;
import org.guillermomolina.i4gl.parser.exceptions.UnknownIdentifierException;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.RecordDescriptor;
import org.guillermomolina.i4gl.parser.types.constant.ConstantDescriptor;

public class NodeFactory extends I4GLBaseVisitor<Node> {

    /* State while parsing a source unit. */
    private final Source source;
    private RootNode mainRootNode;

    /* State while parsing a block. */
    private final I4GLLanguage language;
    private LexicalScope currentLexicalScope;

    public NodeFactory(final I4GLLanguage language, final Source source) {
        this.language = language;
        this.source = source;
        this.currentLexicalScope = new LexicalScope(null, "GLOBAL");
    }

    /**
     * Lambda interface used in
     * {@link NodeFactory#doLookup(String, GlobalObjectLookup, boolean)} function.
     * The function looks up specified identifier and calls
     * {@link GlobalObjectLookup#onFound(LexicalScope, String)} function with the
     * found identifier and lexical sccope in which it was found.
     * 
     * @param <T>
     */
    private interface GlobalObjectLookup<T> {
        T onFound(LexicalScope foundInScope, String foundIdentifier) throws LexicalException;
    }

    /**
     * Looks up the specified identifier (in current scope and its parent scope up
     * to the topmost scope. If the identifier is found it calls the
     * {@link GlobalObjectLookup#onFound(LexicalScope, String)} function with the
     * found identifier and lexical scope in which it was found as arguments. It
     * firstly looks ups to the topmost lexical scope and if the identifier is not
     * found then it looks up in the unit scopes.
     * 
     * @param identifier     identifier to be looked up
     * @param lookupFunction the function to be called when the identifier is found
     * @param <T>            type of the returned object
     * @return the value return from the
     *         {@link GlobalObjectLookup#onFound(LexicalScope, String)} function
     */
    private <T> T doLookup(final String identifier, final GlobalObjectLookup<T> lookupFunction)
            throws LexicalException {
        T result = lookupToParentScope(currentLexicalScope, identifier, lookupFunction, false);
        if (result == null) {
            throw new UnknownIdentifierException(identifier);
        }
        return result;
    }

    /**
     * Helper function for
     * {@link NodeFactory#doLookup(String, GlobalObjectLookup, boolean)}. Does the
     * looking up to the topmost lexical scope.
     * 
     * @param scope          scope to begin the lookup in
     * @param identifier     the identifier to lookup
     * @param lookupFunction function that is called when the identifier is found
     * @param <T>            type of the returned object
     * @return the value return from the
     *         {@link GlobalObjectLookup#onFound(LexicalScope, String)} function
     * @throws LexicalException
     */
    private <T> T lookupToParentScope(LexicalScope scope, final String identifier,
            final GlobalObjectLookup<T> lookupFunction, final boolean onlyPublic) throws LexicalException {
        while (scope != null) {
            if ((onlyPublic) ? scope.containsPublicIdentifier(identifier) : scope.containsLocalIdentifier(identifier)) {
                return lookupFunction.onFound(scope, identifier);
            } else {
                scope = scope.getOuterScope();
            }
        }
        return null;
    }

    public TypeDescriptor getTypeDescriptor(final String identifier) throws LexicalException {
        return doLookup(identifier, LexicalScope::getTypeDescriptor);
    }

    public RootNode getRootNode() {
        return mainRootNode;
    }

    private static void setSourceFromContext(StatementNode node, ParserRuleContext ctx) {
        Interval sourceInterval = srcFromContext(ctx);
        node.setSourceSection(sourceInterval.a, sourceInterval.length());
    }

    private static Interval srcFromContext(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        return new Interval(a, b);
    }

    @Override
    public Node visitMainBlock(final I4GLParser.MainBlockContext ctx) {
        try {
            final String functionIdentifier = "MAIN";
            currentLexicalScope = new LexicalScope(currentLexicalScope, functionIdentifier);
            Interval sourceInterval = srcFromContext(ctx);
            final SourceSection sourceSection = source.createSection(sourceInterval.a, sourceInterval.length());
            BlockNode bodyNode = createFunctionBody(sourceSection, null, ctx.typeDeclarations(), ctx.mainStatements());
            mainRootNode = new MainFunctionRootNode(language, currentLexicalScope.getFrameDescriptor(), bodyNode,
                    sourceSection, functionIdentifier);
            currentLexicalScope = currentLexicalScope.getOuterScope();
            return mainRootNode;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitMainStatements(final I4GLParser.MainStatementsContext ctx) {
        final List<StatementNode> statementNodes = new ArrayList<>(ctx.children.size());
        for (final ParseTree child : ctx.children) {
            StatementNode statementNode = (StatementNode) visit(child);
            if (statementNode != null) {
                statementNodes.add(statementNode);
            }
        }
        BlockNode node = new BlockNode(statementNodes.toArray(new StatementNode[statementNodes.size()]));
        setSourceFromContext(node, ctx);
        return node;
    }

    @Override
    public Node visitFunctionDefinition(final I4GLParser.FunctionDefinitionContext ctx) {
        try {
            final String functionIdentifier = ctx.identifier().getText();
            List<String> parameteridentifiers = null;
            if (ctx.parameterList() != null) {
                parameteridentifiers = new ArrayList<>(ctx.parameterList().parameterGroup().size());
                for (final I4GLParser.ParameterGroupContext parameterCtx : ctx.parameterList().parameterGroup()) {
                    parameteridentifiers.add(parameterCtx.getText());
                }
            }
            Interval sourceInterval = srcFromContext(ctx);
            final SourceSection sourceSection = source.createSection(sourceInterval.a, sourceInterval.length());
            currentLexicalScope = new LexicalScope(currentLexicalScope, functionIdentifier);
            BlockNode bodyNode = createFunctionBody(sourceSection, parameteridentifiers, ctx.typeDeclarations(),
                    ctx.codeBlock());
            final I4GLRootNode rootNode = new I4GLRootNode(language, currentLexicalScope.getFrameDescriptor(), bodyNode,
                    sourceSection, functionIdentifier);
            language.addFunction(functionIdentifier, rootNode);
            currentLexicalScope = currentLexicalScope.getOuterScope();
            return rootNode;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private BlockNode createFunctionBody(SourceSection sourceSection, List<String> parameteridentifiers,
            I4GLParser.TypeDeclarationsContext typeDeclarationsContext, ParserRuleContext codeBlockContext)
            throws LexicalException {
        List<StatementNode> blockNodes = new ArrayList<>();
        if (typeDeclarationsContext != null) {
            BlockNode initializationNode = (BlockNode) visit(typeDeclarationsContext);
            if (initializationNode != null) {
                blockNodes.addAll(initializationNode.getStatements());
            }
        }

        if (parameteridentifiers != null) {
            int argumentIndex = 0;
            for (final String parameteridentifier : parameteridentifiers) {
                final TypeDescriptor typeDescriptor = currentLexicalScope.getIdentifierDescriptor(parameteridentifier);
                if (typeDescriptor == null) {
                    throw new LexicalException("Function parameter " + parameteridentifier + " must be declared");
                }
                final ExpressionNode readNode = new ReadArgumentNode(argumentIndex++, typeDescriptor);
                blockNodes.add(createAssignmentNode(parameteridentifier, readNode));
            }
        }

        if (codeBlockContext != null) {
            BlockNode blockNode = (BlockNode) visit(codeBlockContext);
            if (blockNode != null) {
                blockNodes.addAll(blockNode.getStatements());
            }
        }

        final BlockNode bodyNode = new BlockNode(blockNodes.toArray(new StatementNode[blockNodes.size()]));
        bodyNode.setSourceSection(sourceSection.getCharIndex(), sourceSection.getCharLength());
        bodyNode.addRootTag();
        return bodyNode;
    }

    /**
     * Creates {@link SimpleAssignmentNode} for the specified variable and value.
     * 
     * @param targetIdentifier the variable's identifier
     * @param valueNode        assigning value's node
     * @return the newly created node
     * @throws LexicalException
     */
    private SimpleAssignmentNode createAssignmentNode(final String targetIdentifier, final ExpressionNode valueNode)
            throws LexicalException {
        final TypeDescriptor targetType = doLookup(targetIdentifier, LexicalScope::getIdentifierDescriptor);
        checkTypesAreCompatible(valueNode.getType(), targetType);
        final FrameSlot targetSlot = doLookup(targetIdentifier, LexicalScope::getLocalSlot);

        return SimpleAssignmentNodeGen.create(valueNode, targetSlot);
    }

    /**
     * Checks whether the two types are compatible. The operation is not symmetric.
     * 
     * @throws LexicalException
     */
    private boolean checkTypesAreCompatible(final TypeDescriptor leftType, final TypeDescriptor rightType)
            throws LexicalException {
        if ((leftType != rightType) && (!leftType.convertibleTo(rightType))) {
            throw new TypeMismatchException(leftType.toString(), rightType.toString());
        }
        return true;
    }

    @Override
    public Node visitCodeBlock(final I4GLParser.CodeBlockContext ctx) {
        final List<StatementNode> statementNodes = new ArrayList<>(ctx.children.size());
        for (final ParseTree child : ctx.children) {
            StatementNode statementNode = (StatementNode) visit(child);
            if (statementNode != null) {
                statementNodes.add(statementNode);
            }
        }
        BlockNode node = new BlockNode(statementNodes.toArray(new StatementNode[statementNodes.size()]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitCallStatement(final I4GLParser.CallStatementContext ctx) {
        try {
            final String identifier = ctx.functionIdentifier().getText();
            StatementNode node;
            if (ctx.RETURNING() == null) {
                node = createInvokeNode(identifier, ctx.actualParameter());
            } else {
                node = createCallNode(identifier, ctx.actualParameter(), ctx.identifier());
            }
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private BlockNode createCallNode(final String identifier,
            final List<I4GLParser.ActualParameterContext> parameterListCtx,
            final List<I4GLParser.IdentifierContext> resultVariableListCtx) throws LexicalException {
        final List<ExpressionNode> parameterNodes = new ArrayList<>(parameterListCtx.size());
        for (final I4GLParser.ActualParameterContext parameterCtx : parameterListCtx) {
            parameterNodes.add((ExpressionNode) visit(parameterCtx));
        }
        final ExpressionNode[] arguments = parameterNodes.toArray(new ExpressionNode[parameterNodes.size()]);

        List<StatementNode> statements = new ArrayList<>(resultVariableListCtx.size() + 1);
        InvokeNode invokeNode = new InvokeNode(language, identifier, arguments);
        statements.add(invokeNode);
        int index = 0;
        for (final I4GLParser.IdentifierContext resultVariableCtx : resultVariableListCtx) {
            final String resultIdentifier = resultVariableCtx.getText();
            TypeDescriptor descriptor = currentLexicalScope.getIdentifierDescriptor(resultIdentifier);
            ExpressionNode readNode = ReadFromReturnNodeGen.create(invokeNode, index++, descriptor);
            statements.add(createAssignmentNode(resultIdentifier, readNode));
        }
        return new BlockNode(statements.toArray(new StatementNode[statements.size()]));
    }

    @Override
    public Node visitFunction(final I4GLParser.FunctionContext ctx) {
        final String identifier = ctx.functionIdentifier().getText();
        InvokeNode node = createInvokeNode(identifier, ctx.actualParameter());
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    private InvokeNode createInvokeNode(final String identifier,
            final List<I4GLParser.ActualParameterContext> parameterListCtx) {
        final List<ExpressionNode> parameterNodes = new ArrayList<>(parameterListCtx.size());
        for (final I4GLParser.ActualParameterContext parameterCtx : parameterListCtx) {
            parameterNodes.add((ExpressionNode) visit(parameterCtx));
        }
        final ExpressionNode[] arguments = parameterNodes.toArray(new ExpressionNode[parameterNodes.size()]);
        return new InvokeNode(language, identifier, arguments);
    }

    @Override
    public Node visitReturnStatement(final I4GLParser.ReturnStatementContext ctx) {
        final List<ExpressionNode> parameterNodes = new ArrayList<>();
        if (ctx.expressionList() == null) {
            parameterNodes.add(IntLiteralNodeGen.create(0));
        } else {
            for (final I4GLParser.ExpressionContext parameterCtx : ctx.expressionList().expression()) {
                parameterNodes.add((ExpressionNode) visit(parameterCtx));
            }
        }
        ReturnNode node = new ReturnNode(parameterNodes.toArray(new ExpressionNode[parameterNodes.size()]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitWhileStatement(final I4GLParser.WhileStatementContext ctx) {
        try {
            currentLexicalScope.increaseLoopDepth();
            ExpressionNode condition = (ExpressionNode) visit(ctx.ifCondition());
            StatementNode loopBody = (StatementNode) visit(ctx.codeBlock());
            WhileNode node = new WhileNode(condition, loopBody);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            currentLexicalScope.decreaseLoopDepth();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitForStatement(final I4GLParser.ForStatementContext ctx) {
        try {
            currentLexicalScope.increaseLoopDepth();
            String iteratingIdentifier = ctx.controlVariable().getText();
            ExpressionNode startValue = (ExpressionNode) visit(ctx.initialValue());
            ExpressionNode finalValue = (ExpressionNode) visit(ctx.finalValue());
            ExpressionNode stepValue = null;
            if (ctx.numericConstant() != null) {
                stepValue = (ExpressionNode) visit(ctx.numericConstant());
            } else {
                stepValue = IntLiteralNodeGen.create(1);
            }

            StatementNode loopBody = ctx.codeBlock() == null ? new BlockNode() : (StatementNode) visit(ctx.codeBlock());
            FrameSlot controlSlot = doLookup(iteratingIdentifier, LexicalScope::getLocalSlot);
            if (startValue.getType() != finalValue.getType()
                    && !startValue.getType().convertibleTo(finalValue.getType())) {
                throw new ParseException(source, ctx, "Type mismatch in beginning and last value of for loop.");
            }
            SimpleAssignmentNode initialAssignment = createAssignmentNode(iteratingIdentifier, startValue);
            ExpressionNode readControlVariableNode = createReadVariableNode(iteratingIdentifier);
            SimpleAssignmentNode stepNode = createAssignmentNode(iteratingIdentifier,
                    AddNodeGen.create(readControlVariableNode, stepValue));
            ForNode node = new ForNode(initialAssignment, controlSlot, startValue, finalValue, stepNode,
                    readControlVariableNode, loopBody);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            currentLexicalScope.decreaseLoopDepth();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    /**
     * Creates node that read value from specified variable.
     * 
     * @param identifierToken identifier token of the variable
     * @return the newly created node
     * @throws UnknownIdentifierException
     */
    private ExpressionNode createReadVariableNode(final String identifier) throws LexicalException {
        return doLookup(identifier, (final LexicalScope foundInScope,
                final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier, foundInScope));
    }

    @Override
    public Node visitIfStatement(final I4GLParser.IfStatementContext ctx) {
        ExpressionNode condition = (ExpressionNode) visit(ctx.ifCondition());
        StatementNode thenNode = (StatementNode) visit(ctx.codeBlock(0));
        StatementNode elseNode = null;
        if (ctx.codeBlock().size() == 2) {
            elseNode = (StatementNode) visit(ctx.codeBlock(1));
        }
        final IfNode node = new IfNode(condition, thenNode, elseNode);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitCaseStatement1(final I4GLParser.CaseStatement1Context ctx) {
        int expressionIndex = 0;
        int codeBlockIndex = 0;
        ExpressionNode caseExpression = (ExpressionNode) visit(ctx.expression(expressionIndex++));
        final int whenCount = ctx.WHEN().size();
        List<ExpressionNode> indexNodes = new ArrayList<>(whenCount);
        List<StatementNode> statementNodes = new ArrayList<>(whenCount);
        for (int whenIndex = 0; whenIndex < whenCount; whenIndex++) {
            indexNodes.add((ExpressionNode) visit(ctx.expression(expressionIndex++)));
            statementNodes.add((StatementNode) visit(ctx.codeBlock(codeBlockIndex++)));
        }
        ExpressionNode[] indexNodesArray = indexNodes.toArray(new ExpressionNode[indexNodes.size()]);
        StatementNode[] statementNodesArray = statementNodes.toArray(new StatementNode[statementNodes.size()]);
        StatementNode otherwiseNode = null;
        if (ctx.OTHERWISE() != null) {
            otherwiseNode = (StatementNode) visit(ctx.codeBlock(codeBlockIndex));
        }
        CaseNode node = new CaseNode(caseExpression, indexNodesArray, statementNodesArray, otherwiseNode);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitCaseStatement2(final I4GLParser.CaseStatement2Context ctx) {
        final int whenCount = ctx.WHEN().size();
        List<ExpressionNode> conditionNodes = new ArrayList<>(whenCount);
        List<StatementNode> statementNodes = new ArrayList<>(whenCount);
        int codeBlockIndex = 0;
        for (I4GLParser.IfConditionContext conditionCtx : ctx.ifCondition()) {
            conditionNodes.add((ExpressionNode) visit(conditionCtx));
            statementNodes.add((StatementNode) visit(ctx.codeBlock(codeBlockIndex++)));
        }
        StatementNode elseNode = null;
        if (ctx.OTHERWISE() != null) {
            elseNode = (StatementNode) visit(ctx.codeBlock(codeBlockIndex));
        }
        for (int whenIndex = whenCount - 1; whenIndex >= 0; whenIndex--) {
            ExpressionNode condition = conditionNodes.get(whenIndex);
            StatementNode thenNode = statementNodes.get(whenIndex);
            elseNode = new IfNode(condition, thenNode, elseNode);
        }
        setSourceFromContext(elseNode, ctx);
        elseNode.addStatementTag();
        return elseNode;
    }

    @Override
    public Node visitIfCondition(final I4GLParser.IfConditionContext ctx) {
        if (ctx.TRUE() != null) {
            return IntLiteralNodeGen.create(0);
        }
        if (ctx.FALSE() != null) {
            return IntLiteralNodeGen.create(1);
        }
        ExpressionNode leftNode = (ExpressionNode) visit(ctx.ifCondition2(0));
        final I4GLParser.RelationalOperatorContext operatorCtx = ctx.relationalOperator();
        if (operatorCtx != null) {
            ExpressionNode rightNode = (ExpressionNode) visit(ctx.ifCondition2(1));
            if (operatorCtx.EQUAL() != null) {
                leftNode = EqualsNodeGen.create(leftNode, rightNode);
            } else if (operatorCtx.NOT_EQUAL() != null) {
                leftNode = NotNodeGen.create(EqualsNodeGen.create(leftNode, rightNode));
            } else if (operatorCtx.LT() != null) {
                leftNode = LessThanNodeGen.create(leftNode, rightNode);
            } else if (operatorCtx.LE() != null) {
                leftNode = LessThanOrEqualNodeGen.create(leftNode, rightNode);
            } else if (operatorCtx.GT() != null) {
                leftNode = NotNodeGen.create(LessThanOrEqualNodeGen.create(leftNode, rightNode));
            } else if (operatorCtx.GE() != null) {
                leftNode = NotNodeGen.create(LessThanNodeGen.create(leftNode, rightNode));
            } else if (operatorCtx.LIKE() != null) {
                leftNode = LikeNodeGen.create(leftNode, rightNode);
            } else /* operatorCtx.MATCHES() != null */ {
                leftNode = MatchesNodeGen.create(leftNode, rightNode);
                if (operatorCtx.NOT() != null) {
                    leftNode = NotNodeGen.create(leftNode);
                }
            }
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitIfCondition2(final I4GLParser.IfCondition2Context ctx) {
        ExpressionNode leftNode = null;
        for (final I4GLParser.IfLogicalTermContext context : ctx.ifLogicalTerm()) {
            final ExpressionNode rightNode = (ExpressionNode) visit(context);
            leftNode = leftNode == null ? rightNode : OrNodeGen.create(leftNode, rightNode);
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitIfLogicalTerm(final I4GLParser.IfLogicalTermContext ctx) {
        ExpressionNode leftNode = null;
        for (final I4GLParser.IfLogicalFactorContext context : ctx.ifLogicalFactor()) {
            final ExpressionNode rightNode = (ExpressionNode) visit(context);
            leftNode = leftNode == null ? rightNode : AndNodeGen.create(leftNode, rightNode);
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitIfLogicalFactor(final I4GLParser.IfLogicalFactorContext ctx) {
        if (ctx.LPAREN() != null) {
            return visit(ctx.ifCondition());
        }
        if (ctx.NOT() != null) {
            NotNode node = NotNodeGen.create((ExpressionNode) visit(ctx.ifCondition()));
            setSourceFromContext(node, ctx);
            node.addExpressionTag();
        }
        return visit(ctx.expression());
    }

    @Override
    public Node visitExpression(final I4GLParser.ExpressionContext ctx) {
        ExpressionNode leftNode = null;
        int index = 0;
        for (final I4GLParser.TermContext termCtx : ctx.term()) {
            final ExpressionNode rightNode = (ExpressionNode) visit(termCtx);
            if (leftNode == null) {
                leftNode = rightNode;
            } else {
                final I4GLParser.AddingOperatorContext operatorCtx = ctx.addingOperator(index++);
                if (operatorCtx.PLUS() != null) {
                    leftNode = AddNodeGen.create(leftNode, rightNode);
                } else /* operatorCtx.MINUS() != null */ {
                    leftNode = SubtractNodeGen.create(leftNode, rightNode);
                }
            }
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitTerm(final I4GLParser.TermContext ctx) {
        ExpressionNode leftNode = null;
        int index = 0;
        for (final I4GLParser.FactorContext factorCtx : ctx.factor()) {
            final ExpressionNode rightNode = (ExpressionNode) visit(factorCtx);
            if (leftNode == null) {
                leftNode = rightNode;
            } else {
                final I4GLParser.MultiplyingOperatorContext operatorCtx = ctx.multiplyingOperator(index++);
                if (operatorCtx.STAR() != null) {
                    leftNode = MultiplyNodeGen.create(leftNode, rightNode);
                } else if (operatorCtx.SLASH() != null) {
                    leftNode = DivideNodeGen.create(leftNode, rightNode);
                } else if (operatorCtx.DIV() != null) {
                    leftNode = DivideIntegerNodeGen.create(leftNode, rightNode);
                } else /* operatorCtx.MOD() != null */ {
                    leftNode = ModuloNodeGen.create(leftNode, rightNode);
                }
            }
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitFactor(final I4GLParser.FactorContext ctx) {
        if (ctx.unitType() != null) {
            throw new NotImplementedException();
        }
        return visit(ctx.factorTypes());
    }

    @Override
    public Node visitDisplayStatement(final I4GLParser.DisplayStatementContext ctx) {
        final List<I4GLParser.DisplayValueContext> valueListCtx = ctx.displayValue();
        final List<ExpressionNode> parameterNodes = new ArrayList<>(valueListCtx.size());
        for (final I4GLParser.DisplayValueContext valueCtx : valueListCtx) {
            parameterNodes.add((ExpressionNode) visit(valueCtx));
        }
        DisplayNode node = new DisplayNode(parameterNodes.toArray(new ExpressionNode[parameterNodes.size()]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    private BlockNode mergeBlocks(Iterable<? extends ParserRuleContext> contextList) {
        List<StatementNode> statementNodes = new ArrayList<>();
        for (ParseTree variableDeclarationCtx : contextList) {
            BlockNode blockNode = (BlockNode) visit(variableDeclarationCtx);
            assert blockNode != null;
            if (blockNode instanceof BlockNode) {
                statementNodes.addAll(blockNode.getStatements());
            }
        }
        return new BlockNode(statementNodes.toArray(new StatementNode[statementNodes.size()]));
    }

    @Override
    public Node visitTypeDeclarations(final I4GLParser.TypeDeclarationsContext ctx) {
        BlockNode node = mergeBlocks(ctx.typeDeclaration());
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitTypeDeclaration(final I4GLParser.TypeDeclarationContext ctx) {
        BlockNode node = mergeBlocks(ctx.variableDeclaration());
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    class TypeNode extends Node {
        public final TypeDescriptor descriptor;

        public TypeNode(final TypeDescriptor descriptor) {
            this.descriptor = descriptor;
        }
    }

    @Override
    public Node visitVariableDeclaration(final I4GLParser.VariableDeclarationContext ctx) {
        try {
            final TypeNode typeNode = (TypeNode) visit(ctx.type());
            final List<I4GLParser.IdentifierContext> identifierCtxList = ctx.identifier();
            final List<String> identifierList = new ArrayList<>(identifierCtxList.size());
            for (final I4GLParser.IdentifierContext identifierCtx : identifierCtxList) {
                identifierList.add(identifierCtx.getText());
            }
            List<StatementNode> initializationNodes = new ArrayList<>();
            final TypeDescriptor typeDescriptor = typeNode.descriptor;
            for (final String identifier : identifierList) {
                currentLexicalScope.registerLocalVariable(identifier, typeDescriptor);
                StatementNode initializationNode;
                Object defaultValue = typeDescriptor.getDefaultValue();
                if (defaultValue == null) {
                    throw new ParseException(source, ctx,
                            "Undefined default value for type " + typeDescriptor.toString());
                }
                FrameSlot frameSlot = currentLexicalScope.localIdentifiers.getFrameSlot(identifier);
                initializationNode = InitializationNodeFactory.create(frameSlot, defaultValue, null);
                initializationNodes.add(initializationNode);
            }

            BlockNode node = new BlockNode(initializationNodes.toArray(new StatementNode[initializationNodes.size()]));
            node.addStatementTag();
            setSourceFromContext(node, ctx);
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitCharType(final I4GLParser.CharTypeContext ctx) {
        if (ctx.varchar() != null) {
            final int size = Integer.parseInt(ctx.numericConstant(0).getText());
            return new TypeNode(currentLexicalScope.createVarcharType(size));
        } else {
            int size = 1;
            if (!ctx.numericConstant().isEmpty()) {
                size = Integer.parseInt(ctx.numericConstant(0).getText());
            }
            return new TypeNode(currentLexicalScope.createNCharType(size));
        }
    }

    @Override
    public Node visitNumberType(final I4GLParser.NumberTypeContext ctx) {
        try {
            if (ctx.numericConstant().isEmpty()) {
                return new TypeNode(getTypeDescriptor(ctx.getText()));
            } else {
                throw new NotImplementedException();
            }
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitTimeType(final I4GLParser.TimeTypeContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Node visitIndirectType(final I4GLParser.IndirectTypeContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Node visitLargeType(final I4GLParser.LargeTypeContext ctx) {
        if (ctx.BYTE() != null) {
            throw new NotImplementedException();
        }
        try {
            return new TypeNode(getTypeDescriptor(ctx.getText()));
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitRecordType(final I4GLParser.RecordTypeContext ctx) {
        currentLexicalScope = new RecordLexicalScope(currentLexicalScope);

        if (ctx.LIKE() != null) {
            throw new NotImplementedException();
        }
        for (I4GLParser.VariableDeclarationContext variableDeclarationCtx : ctx.variableDeclaration()) {
            visit(variableDeclarationCtx);
        }

        RecordDescriptor descriptor = currentLexicalScope.createRecordDescriptor();

        assert currentLexicalScope.getOuterScope() != null;
        currentLexicalScope = currentLexicalScope.getOuterScope();
        return new TypeNode(descriptor);
    }

    @Override
    public Node visitArrayType(final I4GLParser.ArrayTypeContext ctx) {
        TypeNode typeNode = (TypeNode) visit(ctx.arrayTypeType());
        ArrayDescriptor arrayDescriptor = null;
        for (int i = ctx.arrayIndexer().dimensionSize().size() - 1; i >= 0; i--) {
            int size = Integer.parseInt(ctx.arrayIndexer().dimensionSize(i).getText());
            if (arrayDescriptor == null) {
                arrayDescriptor = currentLexicalScope.createArrayType(size, typeNode.descriptor);
            } else {
                arrayDescriptor = currentLexicalScope.createArrayType(size, arrayDescriptor);
            }
        }
        return new TypeNode(arrayDescriptor);
    }

    @Override
    public Node visitDynArrayType(final I4GLParser.DynArrayTypeContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Node visitAssignmentStatement(final I4GLParser.AssignmentStatementContext ctx) {
        if (!ctx.identifier().isEmpty()) {
            // RECORD.* = RECORD.*
            throw new NotImplementedException();
        }
        try {
            I4GLParser.VariableContext variableCtx = ctx.variable();
            if (variableCtx.indexingVariable() == null && variableCtx.identifier().size() == 1) {
                // Simple variable
                final String identifier = variableCtx.identifier(0).getText();
                final ExpressionNode valueNode = (ExpressionNode) visit(ctx.expressionList().expression(0));
                StatementNode node = createAssignmentNode(identifier, valueNode);
                setSourceFromContext(node, ctx);
                node.addStatementTag();
                return node;
            }
            ExpressionNode variableNode = null;
            for (int index = 0; index < variableCtx.identifier().size(); index++) {
                // Record
                final String identifier = variableCtx.identifier(index).getText();
                if (index == 0) {
                    variableNode = doLookup(identifier,
                            (final LexicalScope foundInLexicalScope,
                                    final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier,
                                            foundInLexicalScope));
                } else if (index == variableCtx.identifier().size() - 1 && variableCtx.indexingVariable() == null) {
                    final ExpressionNode valueNode = (ExpressionNode) visit(ctx.expressionList().expression(0));
                    StatementNode node = createAssignmentToRecordField(variableNode, identifier, valueNode);
                    node.addStatementTag();
                    setSourceFromContext(node, ctx);
                    return node;
                } else {
                    variableNode = createReadFromRecordNode(variableNode, identifier);
                }
            }
            // Array
            final List<I4GLParser.ExpressionContext> indexList = variableCtx.indexingVariable().expressionList()
                    .expression();
            if (indexList.size() > 3) {
                throw new ParseException(source, ctx, "Dimensions can not be " + indexList.size());
            }
            List<ExpressionNode> indexNodes = new ArrayList<>(indexList.size());
            for (I4GLParser.ExpressionContext indexCtx : indexList) {
                indexNodes.add((ExpressionNode) visit(indexCtx));
            }
            final ExpressionNode valueNode = (ExpressionNode) visit(ctx.expressionList().expression(0));
            StatementNode node = createAssignmentToArray(variableNode, indexNodes, valueNode);
            node.addStatementTag();
            setSourceFromContext(node, ctx);
            return node;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    /**
     * Creates {@link AssignToRecordField} node for assignment to variable inside a
     * record
     * 
     * @param recordExpression expression node which returns the record
     * @param identifierToken  token of the target record's variable
     * @param valueNode        assigning value's node
     * @return the newly created node
     * @throws TypeMismatchException
     */
    private StatementNode createAssignmentToRecordField(ExpressionNode recordExpression, String identifier,
            ExpressionNode valueNode) throws LexicalException {
        TypeDescriptor expressionType = recordExpression.getType();
        if (!(expressionType instanceof RecordDescriptor)) {
            throw new TypeMismatchException(expressionType.toString(), "Record");
        }
        return AssignToRecordFieldNodeGen.create(identifier, recordExpression, valueNode);
    }

    /**
     * Creates {@link AssignToArrayNode} for assignment to an array at specified
     * index
     * 
     * @param arrayExpression     expression node which returns the target array
     * @param indexExpressionNode expression node returning the index
     * @param valueNode           assigning value's node
     * @return the newly created node
     * @throws TypeMismatchException
     */
    private StatementNode createAssignmentToArray(ExpressionNode arrayExpression, List<ExpressionNode> indexNodes,
            ExpressionNode valueNode) throws LexicalException {
        ExpressionNode readArrayNode = arrayExpression;
        StatementNode result = null;
        final int lastIndex = indexNodes.size() - 1;
        for (int index = 0; index < indexNodes.size(); index++) {
            TypeDescriptor actualType = readArrayNode.getType();
            if (!(actualType instanceof ArrayDescriptor)) {
                throw new TypeMismatchException(actualType.toString(), "Array");
            }
            ExpressionNode indexNode = indexNodes.get(index);
            TypeDescriptor returnType = ((ArrayDescriptor) actualType).getValuesDescriptor();
            if (index == lastIndex) {
                result = AssignToArrayNodeGen.create(readArrayNode, indexNode, valueNode);
            } else {
                readArrayNode = ReadFromArrayNodeGen.create(readArrayNode, indexNode, returnType);
            }
        }
        assert result != null;
        return result;
    }

    @Override
    public Node visitVariable(final I4GLParser.VariableContext ctx) {
        try {
            ExpressionNode variableNode = null;
            for (int index = 0; index < ctx.identifier().size(); index++) {
                final String identifier = ctx.identifier(index).getText();
                if (index == 0) {
                    // simple variable
                    variableNode = doLookup(identifier,
                            (final LexicalScope foundInLexicalScope,
                                    final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier,
                                            foundInLexicalScope));
                } else {
                    // RECORD
                    variableNode = createReadFromRecordNode(variableNode, identifier);
                }
            }
            if (ctx.indexingVariable() != null) {
                // array
                final List<I4GLParser.ExpressionContext> indexList = ctx.indexingVariable().expressionList()
                        .expression();
                if (indexList.size() > 3) {
                    throw new ParseException(source, ctx, "Dimensions can not be " + indexList.size());
                }
                List<ExpressionNode> indexNodes = new ArrayList<>(indexList.size());
                for (I4GLParser.ExpressionContext indexCtx : indexList) {
                    indexNodes.add((ExpressionNode) visit(indexCtx));
                }
                variableNode = createReadFromArrayNode(variableNode, indexNodes);
            }
            setSourceFromContext(variableNode, ctx);
            variableNode.addExpressionTag();
            return variableNode;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private ReadFromRecordNode createReadFromRecordNode(final ExpressionNode recordExpression, final String identifier)
            throws LexicalException {
        TypeDescriptor descriptor = recordExpression.getType();
        TypeDescriptor returnType = null;

        if (!(descriptor instanceof RecordDescriptor)) {
            throw new TypeMismatchException(descriptor.toString(), "Record");
        }
        RecordDescriptor accessedRecordDescriptor = (RecordDescriptor) descriptor;
        if (!accessedRecordDescriptor.containsIdentifier(identifier)) {
            throw new UnknownIdentifierException("The record does not contain this identifier");
        }

        returnType = accessedRecordDescriptor.getLexicalScope().getIdentifierDescriptor(identifier);
        return ReadFromRecordNodeGen.create(recordExpression, identifier, returnType);
    }

    /**
     * Creates {@link ReadFromArrayNode} that reads value from specified array at
     * specified index.
     * 
     * @param arrayExpression node that returns the array
     * @param indexNodes      nodes of the indexNodes at which the array will be
     *                        read
     * @return the newly created node
     * @throws LexicalException
     */
    private ExpressionNode createReadFromArrayNode(ExpressionNode arrayExpression, List<ExpressionNode> indexNodes)
            throws LexicalException {
        ExpressionNode readArrayNode = arrayExpression;
        for (ExpressionNode indexNode : indexNodes) {
            TypeDescriptor actualType = readArrayNode.getType();
            if (!(actualType instanceof ArrayDescriptor)) {
                throw new TypeMismatchException(actualType.toString(), "Array");
            }
            TypeDescriptor returnType = ((ArrayDescriptor) actualType).getValuesDescriptor();
            readArrayNode = ReadFromArrayNodeGen.create(readArrayNode, indexNode, returnType);
        }
        return readArrayNode;
    }

    private ExpressionNode createReadVariableFromScope(final String identifier, final LexicalScope scope) {
        final FrameSlot variableSlot = scope.getLocalSlot(identifier);
        final TypeDescriptor type = scope.getIdentifierDescriptor(identifier);
        final boolean isLocal = scope == currentLexicalScope;
        final boolean isConstant = type instanceof ConstantDescriptor;

        if (isConstant) {
            final ConstantDescriptor constantType = (ConstantDescriptor) type;
            return ReadConstantNodeGen.create(constantType.getValue(), constantType);
        } else if (isLocal) {
            return ReadLocalVariableNodeGen.create(variableSlot, type);
        } else {
            return ReadGlobalVariableNodeGen.create(variableSlot, type);
        }
    }

    @Override
    public Node visitString(final I4GLParser.StringContext ctx) {
        String literal = ctx.getText();

        // Remove the trailing and ending "
        assert literal.length() >= 2 && literal.startsWith("\"") && literal.endsWith("\"");
        literal = literal.substring(1, literal.length() - 1);

        I4GLStringLiteralNode node = I4GLStringLiteralNodeGen.create(literal.intern());
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitInteger(final I4GLParser.IntegerContext ctx) {
        final String literal = ctx.getText();
        ExpressionNode node;
        try {
            node = IntLiteralNodeGen.create(Integer.parseInt(literal));
        } catch (final NumberFormatException e) {
            node = LongLiteralNodeGen.create(Long.parseLong(literal));
        }
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitReal(final I4GLParser.RealContext ctx) {
        final String literal = ctx.getText();
        final DoubleLiteralNode node = DoubleLiteralNodeGen.create(Double.parseDouble(literal));
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitDebugStatement(final I4GLParser.DebugStatementContext ctx) {
        final DebuggerNode node = new DebuggerNode();
        node.addStatementTag();
        setSourceFromContext(node, ctx);
        return node;
    }

    @Override
    public Node visitDatabaseDeclaration(final I4GLParser.DatabaseDeclarationContext ctx) {
        try {
            String identifier = ctx.identifier(0).getText();
            final FrameSlot databaseSlot = currentLexicalScope.registerDatabase(identifier);
            ConnectToDatabaseNode node = new ConnectToDatabaseNode(databaseSlot);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }
}