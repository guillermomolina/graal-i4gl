package org.guillermomolina.i4gl.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.InitializationNodeFactory;
import org.guillermomolina.i4gl.nodes.arithmetic.I4GLAddNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.I4GLDivideIntegerNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.I4GLDivideNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.I4GLModuloNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.I4GLMultiplyNodeGen;
import org.guillermomolina.i4gl.nodes.arithmetic.I4GLSubtractNodeGen;
import org.guillermomolina.i4gl.nodes.call.I4GLCallNode;
import org.guillermomolina.i4gl.nodes.call.I4GLInvokeNode;
import org.guillermomolina.i4gl.nodes.call.I4GLReadArgumentNode;
import org.guillermomolina.i4gl.nodes.call.I4GLReturnNode;
import org.guillermomolina.i4gl.nodes.control.I4GLCaseNode;
import org.guillermomolina.i4gl.nodes.control.I4GLDebuggerNode;
import org.guillermomolina.i4gl.nodes.control.I4GLForNode;
import org.guillermomolina.i4gl.nodes.control.I4GLIfNode;
import org.guillermomolina.i4gl.nodes.control.I4GLWhileNode;
import org.guillermomolina.i4gl.nodes.literals.I4GLBigIntLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.I4GLIntLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.I4GLSmallFloatLiteralNode;
import org.guillermomolina.i4gl.nodes.literals.I4GLSmallFloatLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.I4GLTextLiteralNode;
import org.guillermomolina.i4gl.nodes.logic.I4GLAndNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLEqualsNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLIsNullNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLLessThanNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLLessThanOrEqualNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLLikeNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLMatchesNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLNotNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLOrNodeGen;
import org.guillermomolina.i4gl.nodes.root.I4GLMainRootNode;
import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLBlockNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLDatabaseNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLDisplayNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLSelectNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.nodes.variables.read.I4GLReadFromIndexedNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.I4GLReadFromRecordNode;
import org.guillermomolina.i4gl.nodes.variables.read.I4GLReadFromRecordNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.I4GLReadGlobalVariableNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.I4GLReadLocalVariableNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLAssignToIndexedNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLAssignToLocalVariableNode;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLAssignToLocalVariableNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLAssignToRecordFieldNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLAssignToRecordTextNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLAssignToTextNodeGen;
import org.guillermomolina.i4gl.parser.I4GLParser.NotIndexedVariableContext;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.exceptions.ParseException;
import org.guillermomolina.i4gl.parser.exceptions.TypeMismatchException;
import org.guillermomolina.i4gl.parser.exceptions.UnknownIdentifierException;
import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLArrayType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLCharType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLRecordType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLTextType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLVarcharType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLBigIntType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLFloatType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLIntType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLSmallFloatType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLSmallIntType;
import org.guillermomolina.i4gl.runtime.values.I4GLDatabase;
import org.guillermomolina.i4gl.runtime.values.I4GLNull;

public class I4GLNodeFactory extends I4GLBaseVisitor<Node> {
    private static final String RECORD_STRING = "Record";
    private static final String DIMENSIONS_STRING = "Dimensions can not be ";

    /* State while parsing a source unit. */
    private final Source source;
    private final Map<String, RootCallTarget> allFunctions;

    /* State while parsing a block. */
    private final I4GLLanguage language;
    private I4GLParseScope currentLexicalScope;

    public I4GLNodeFactory(final I4GLLanguage language, final Source source) {
        this.language = language;
        this.source = source;
        this.currentLexicalScope = new I4GLParseScope(null, "GLOBAL");
        this.allFunctions = new HashMap<>();
    }

    /**
     * Lambda interface used in
     * {@link I4GLNodeFactory#doLookup(String, GlobalObjectLookup, boolean)}
     * function. The function looks up specified identifier and calls
     * {@link GlobalObjectLookup#onFound(I4GLParseScope, String)} function with the
     * found identifier and lexical sccope in which it was found.
     * 
     * @param <T>
     */
    private interface GlobalObjectLookup<T> {
        T onFound(I4GLParseScope foundInScope, String foundIdentifier) throws LexicalException;
    }

    /**
     * Looks up the specified identifier (in current scope and its parent scope up
     * to the topmost scope. If the identifier is found it calls the
     * {@link GlobalObjectLookup#onFound(I4GLParseScope, String)} function with the
     * found identifier and lexical scope in which it was found as arguments. It
     * firstly looks ups to the topmost lexical scope and if the identifier is not
     * found then it looks up in the unit scopes.
     * 
     * @param identifier     identifier to be looked up
     * @param lookupFunction the function to be called when the identifier is found
     * @param <T>            type of the returned object
     * @return the value return from the
     *         {@link GlobalObjectLookup#onFound(I4GLParseScope, String)} function
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
     * {@link I4GLNodeFactory#doLookup(String, GlobalObjectLookup, boolean)}. Does
     * the looking up to the topmost lexical scope.
     * 
     * @param scope          scope to begin the lookup in
     * @param identifier     the identifier to lookup
     * @param lookupFunction function that is called when the identifier is found
     * @param <T>            type of the returned object
     * @return the value return from the
     *         {@link GlobalObjectLookup#onFound(I4GLParseScope, String)} function
     * @throws LexicalException
     */
    private <T> T lookupToParentScope(I4GLParseScope scope, final String identifier,
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

    public Map<String, RootCallTarget> getAllFunctions() {
        return allFunctions;
    }

    private static void setSourceFromContext(I4GLStatementNode node, ParserRuleContext ctx) {
        Interval sourceInterval = srcFromContext(ctx);
        assert sourceInterval != null;
        assert node != null;
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
            currentLexicalScope = new I4GLParseScope(currentLexicalScope, functionIdentifier);
            Interval sourceInterval = srcFromContext(ctx);
            final SourceSection sourceSection = source.createSection(sourceInterval.a, sourceInterval.length());
            I4GLBlockNode bodyNode = createFunctionBody(sourceSection, null, ctx.typeDeclarations(),
                    ctx.mainStatements());
            I4GLMainRootNode rootNode = new I4GLMainRootNode(language, currentLexicalScope.getFrameDescriptor(),
                    bodyNode, sourceSection, functionIdentifier);
            allFunctions.put(functionIdentifier, Truffle.getRuntime().createCallTarget(rootNode));
            currentLexicalScope = currentLexicalScope.getOuterScope();
            return rootNode;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitMainStatements(final I4GLParser.MainStatementsContext ctx) {
        final List<I4GLStatementNode> statementNodes = new ArrayList<>(ctx.children.size());
        for (final ParseTree child : ctx.children) {
            if (!(child instanceof TerminalNodeImpl)) {
                I4GLStatementNode statementNode = (I4GLStatementNode) visit(child);
                if (statementNode == null) {
                    throw new ParseException(source, ctx, "Visited an unimplemented Node");
                }
                statementNodes.add(statementNode);
            }
        }
        I4GLBlockNode node = new I4GLBlockNode(statementNodes.toArray(new I4GLStatementNode[statementNodes.size()]));
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
            currentLexicalScope = new I4GLParseScope(currentLexicalScope, functionIdentifier);
            I4GLBlockNode bodyNode = createFunctionBody(sourceSection, parameteridentifiers, ctx.typeDeclarations(),
                    ctx.codeBlock());
            final I4GLRootNode rootNode = new I4GLRootNode(language, currentLexicalScope.getFrameDescriptor(), bodyNode,
                    sourceSection, functionIdentifier);
            allFunctions.put(functionIdentifier, Truffle.getRuntime().createCallTarget(rootNode));
            currentLexicalScope = currentLexicalScope.getOuterScope();
            return rootNode;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private I4GLBlockNode createFunctionBody(SourceSection sourceSection, List<String> parameteridentifiers,
            I4GLParser.TypeDeclarationsContext typeDeclarationsContext, ParserRuleContext codeBlockContext)
            throws LexicalException {
        List<I4GLStatementNode> blockNodes = new ArrayList<>();
        if (typeDeclarationsContext != null) {
            I4GLBlockNode initializationNode = (I4GLBlockNode) visit(typeDeclarationsContext);
            if (initializationNode != null) {
                blockNodes.addAll(initializationNode.getStatements());
            }
        }

        if (parameteridentifiers != null) {
            int argumentIndex = 0;
            for (final String parameteridentifier : parameteridentifiers) {
                final I4GLType type = currentLexicalScope.getIdentifierType(parameteridentifier);
                if (type == null) {
                    throw new LexicalException("Function parameter " + parameteridentifier + " must be declared");
                }
                final I4GLExpressionNode readNode = new I4GLReadArgumentNode(argumentIndex++, type);
                blockNodes.add(createAssignToVariableNode(parameteridentifier, readNode));
            }
        }

        if (codeBlockContext != null) {
            I4GLBlockNode blockNode = (I4GLBlockNode) visit(codeBlockContext);
            if (blockNode != null) {
                blockNodes.addAll(blockNode.getStatements());
            }
        }

        final I4GLBlockNode bodyNode = new I4GLBlockNode(blockNodes.toArray(new I4GLStatementNode[blockNodes.size()]));
        bodyNode.setSourceSection(sourceSection.getCharIndex(), sourceSection.getCharLength());
        bodyNode.addRootTag();
        return bodyNode;
    }

    private I4GLAssignToLocalVariableNode createAssignToVariableNode(final String identifier,
            final I4GLExpressionNode valueNode) throws LexicalException {
        return doLookup(identifier,
                (final I4GLParseScope foundInScope, final String foundIdentifier) -> createAssignToVariableFromScope(
                        foundIdentifier, foundInScope, valueNode));
    }

    private I4GLAssignToLocalVariableNode createAssignToVariableFromScope(final String identifier,
            final I4GLParseScope scope, final I4GLExpressionNode valueNode) {
        final FrameSlot variableSlot = scope.getLocalSlot(identifier);
        final I4GLType type = scope.getIdentifierType(identifier);
        final boolean isLocal = scope == currentLexicalScope;

        if (isLocal) {
            return I4GLAssignToLocalVariableNodeGen.create(valueNode, variableSlot, type);
        }
        // assign to global
        throw new NotImplementedException();
    }

    @Override
    public Node visitCodeBlock(final I4GLParser.CodeBlockContext ctx) {
        final List<I4GLStatementNode> statementNodes = new ArrayList<>(ctx.children.size());
        for (final ParseTree child : ctx.children) {
            I4GLStatementNode statementNode = (I4GLStatementNode) visit(child);
            if (statementNode != null) {
                statementNodes.add(statementNode);
            }
        }
        I4GLBlockNode node = new I4GLBlockNode(statementNodes.toArray(new I4GLStatementNode[statementNodes.size()]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitCallStatement(final I4GLParser.CallStatementContext ctx) {
        final String functionIdentifier = ctx.function().functionIdentifier().getText();
        final List<I4GLExpressionNode> parameterNodes = new ArrayList<>(ctx.function().actualParameter().size());
        for (final I4GLParser.ActualParameterContext parameterCtx : ctx.function().actualParameter()) {
            parameterNodes.add((I4GLExpressionNode) visit(parameterCtx));
        }
        final I4GLExpressionNode[] arguments = parameterNodes.toArray(new I4GLExpressionNode[parameterNodes.size()]);
        List<FrameSlot> returnSlots = new ArrayList<>(ctx.identifier().size());
        for (final I4GLParser.IdentifierContext identifierCtx : ctx.identifier()) {
            final String resultIdentifier = identifierCtx.getText();
            FrameSlot recordSlot = currentLexicalScope.getLocalSlot(resultIdentifier);
            returnSlots.add(recordSlot);
        }
        I4GLCallNode node = new I4GLCallNode(functionIdentifier, arguments,
                returnSlots.toArray(new FrameSlot[returnSlots.size()]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitFunction(final I4GLParser.FunctionContext ctx) {
        final String functionIdentifier = ctx.functionIdentifier().getText();
        final List<I4GLExpressionNode> parameterNodes = new ArrayList<>(ctx.actualParameter().size());
        for (final I4GLParser.ActualParameterContext parameterCtx : ctx.actualParameter()) {
            parameterNodes.add((I4GLExpressionNode) visit(parameterCtx));
        }
        final I4GLExpressionNode[] arguments = parameterNodes.toArray(new I4GLExpressionNode[parameterNodes.size()]);
        I4GLInvokeNode node = new I4GLInvokeNode(functionIdentifier, arguments);
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitReturnStatement(final I4GLParser.ReturnStatementContext ctx) {
        final List<I4GLExpressionNode> parameterNodes = new ArrayList<>();
        if (ctx.expressionList() == null) {
            parameterNodes.add(I4GLIntLiteralNodeGen.create(0));
        } else {
            for (final I4GLParser.ExpressionContext parameterCtx : ctx.expressionList().expression()) {
                parameterNodes.add((I4GLExpressionNode) visit(parameterCtx));
            }
        }
        I4GLReturnNode node = new I4GLReturnNode(parameterNodes.toArray(new I4GLExpressionNode[parameterNodes.size()]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitWhileStatement(final I4GLParser.WhileStatementContext ctx) {
        try {
            currentLexicalScope.increaseLoopDepth();
            I4GLExpressionNode condition = (I4GLExpressionNode) visit(ctx.ifCondition());
            I4GLStatementNode loopBody = (I4GLStatementNode) visit(ctx.codeBlock());
            I4GLWhileNode node = new I4GLWhileNode(condition, loopBody);
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
            I4GLExpressionNode startValue = (I4GLExpressionNode) visit(ctx.initialValue());
            I4GLExpressionNode finalValue = (I4GLExpressionNode) visit(ctx.finalValue());
            I4GLExpressionNode stepValue = null;
            if (ctx.numericConstant() != null) {
                stepValue = (I4GLExpressionNode) visit(ctx.numericConstant());
            } else {
                stepValue = I4GLIntLiteralNodeGen.create(1);
            }

            I4GLStatementNode loopBody = ctx.codeBlock() == null ? new I4GLBlockNode(null)
                    : (I4GLStatementNode) visit(ctx.codeBlock());
            FrameSlot controlSlot = doLookup(iteratingIdentifier, I4GLParseScope::getLocalSlot);
            if (startValue.getType() != finalValue.getType()
                    && !startValue.getType().convertibleTo(finalValue.getType())) {
                throw new ParseException(source, ctx, "Type mismatch in beginning and last value of for loop.");
            }
            I4GLAssignToLocalVariableNode initialAssignment = createAssignToVariableNode(iteratingIdentifier,
                    startValue);
            I4GLExpressionNode readControlVariableNode = createReadVariableNode(iteratingIdentifier);
            I4GLAssignToLocalVariableNode stepNode = createAssignToVariableNode(iteratingIdentifier,
                    I4GLAddNodeGen.create(readControlVariableNode, stepValue));
            I4GLForNode node = new I4GLForNode(initialAssignment, controlSlot, startValue, finalValue, stepNode,
                    readControlVariableNode, loopBody);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            currentLexicalScope.decreaseLoopDepth();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private I4GLExpressionNode createReadVariableNode(final String identifier) throws LexicalException {
        return doLookup(identifier, (final I4GLParseScope foundInScope,
                final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier, foundInScope));
    }

    @Override
    public Node visitIfStatement(final I4GLParser.IfStatementContext ctx) {
        I4GLExpressionNode condition = (I4GLExpressionNode) visit(ctx.ifCondition());
        I4GLStatementNode thenNode = (I4GLStatementNode) visit(ctx.codeBlock(0));
        I4GLStatementNode elseNode = null;
        if (ctx.codeBlock().size() == 2) {
            elseNode = (I4GLStatementNode) visit(ctx.codeBlock(1));
        }
        final I4GLIfNode node = new I4GLIfNode(condition, thenNode, elseNode);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitCaseStatement1(final I4GLParser.CaseStatement1Context ctx) {
        int expressionIndex = 0;
        int codeBlockIndex = 0;
        I4GLExpressionNode caseExpression = (I4GLExpressionNode) visit(ctx.expression(expressionIndex++));
        final int whenCount = ctx.WHEN().size();
        List<I4GLExpressionNode> indexNodes = new ArrayList<>(whenCount);
        List<I4GLStatementNode> statementNodes = new ArrayList<>(whenCount);
        for (int whenIndex = 0; whenIndex < whenCount; whenIndex++) {
            indexNodes.add((I4GLExpressionNode) visit(ctx.expression(expressionIndex++)));
            statementNodes.add((I4GLStatementNode) visit(ctx.codeBlock(codeBlockIndex++)));
        }
        I4GLExpressionNode[] indexNodesArray = indexNodes.toArray(new I4GLExpressionNode[indexNodes.size()]);
        I4GLStatementNode[] statementNodesArray = statementNodes.toArray(new I4GLStatementNode[statementNodes.size()]);
        I4GLStatementNode otherwiseNode = null;
        if (ctx.OTHERWISE() != null) {
            otherwiseNode = (I4GLStatementNode) visit(ctx.codeBlock(codeBlockIndex));
        }
        I4GLCaseNode node = new I4GLCaseNode(caseExpression, indexNodesArray, statementNodesArray, otherwiseNode);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitCaseStatement2(final I4GLParser.CaseStatement2Context ctx) {
        final int whenCount = ctx.WHEN().size();
        List<I4GLExpressionNode> conditionNodes = new ArrayList<>(whenCount);
        List<I4GLStatementNode> statementNodes = new ArrayList<>(whenCount);
        int codeBlockIndex = 0;
        for (I4GLParser.IfConditionContext conditionCtx : ctx.ifCondition()) {
            conditionNodes.add((I4GLExpressionNode) visit(conditionCtx));
            statementNodes.add((I4GLStatementNode) visit(ctx.codeBlock(codeBlockIndex++)));
        }
        I4GLStatementNode elseNode = null;
        if (ctx.OTHERWISE() != null) {
            elseNode = (I4GLStatementNode) visit(ctx.codeBlock(codeBlockIndex));
        }
        for (int whenIndex = whenCount - 1; whenIndex >= 0; whenIndex--) {
            I4GLExpressionNode condition = conditionNodes.get(whenIndex);
            I4GLStatementNode thenNode = statementNodes.get(whenIndex);
            elseNode = new I4GLIfNode(condition, thenNode, elseNode);
        }
        setSourceFromContext(elseNode, ctx);
        elseNode.addStatementTag();
        return elseNode;
    }

    @Override
    public Node visitIfCondition(final I4GLParser.IfConditionContext ctx) {
        if (ctx.TRUE() != null) {
            return I4GLIntLiteralNodeGen.create(0);
        }
        if (ctx.FALSE() != null) {
            return I4GLIntLiteralNodeGen.create(1);
        }
        I4GLExpressionNode leftNode = (I4GLExpressionNode) visit(ctx.ifCondition2(0));
        final I4GLParser.RelationalOperatorContext operatorCtx = ctx.relationalOperator();
        if (operatorCtx != null) {
            I4GLExpressionNode rightNode = (I4GLExpressionNode) visit(ctx.ifCondition2(1));
            if (operatorCtx.EQUAL() != null) {
                leftNode = I4GLEqualsNodeGen.create(leftNode, rightNode);
            } else if (operatorCtx.NOT_EQUAL() != null) {
                leftNode = I4GLNotNodeGen.create(I4GLEqualsNodeGen.create(leftNode, rightNode));
            } else if (operatorCtx.LT() != null) {
                leftNode = I4GLLessThanNodeGen.create(leftNode, rightNode);
            } else if (operatorCtx.LE() != null) {
                leftNode = I4GLLessThanOrEqualNodeGen.create(leftNode, rightNode);
            } else if (operatorCtx.GT() != null) {
                leftNode = I4GLNotNodeGen.create(I4GLLessThanOrEqualNodeGen.create(leftNode, rightNode));
            } else if (operatorCtx.GE() != null) {
                leftNode = I4GLNotNodeGen.create(I4GLLessThanNodeGen.create(leftNode, rightNode));
            } else if (operatorCtx.LIKE() != null) {
                leftNode = I4GLLikeNodeGen.create(leftNode, rightNode);
            } else /* operatorCtx.MATCHES() != null */ {
                leftNode = I4GLMatchesNodeGen.create(leftNode, rightNode);
                if (operatorCtx.NOT() != null) {
                    leftNode = I4GLNotNodeGen.create(leftNode);
                }
            }
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitIfCondition2(final I4GLParser.IfCondition2Context ctx) {
        I4GLExpressionNode leftNode = null;
        for (final I4GLParser.IfLogicalTermContext context : ctx.ifLogicalTerm()) {
            final I4GLExpressionNode rightNode = (I4GLExpressionNode) visit(context);
            leftNode = leftNode == null ? rightNode : I4GLOrNodeGen.create(leftNode, rightNode);
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitIfLogicalTerm(final I4GLParser.IfLogicalTermContext ctx) {
        I4GLExpressionNode leftNode = null;
        for (final I4GLParser.IfLogicalFactorContext context : ctx.ifLogicalFactor()) {
            final I4GLExpressionNode rightNode = (I4GLExpressionNode) visit(context);
            leftNode = leftNode == null ? rightNode : I4GLAndNodeGen.create(leftNode, rightNode);
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitIfLogicalFactor(final I4GLParser.IfLogicalFactorContext ctx) {
        I4GLExpressionNode node = null;
        if (ctx.ifCondition() != null) {
            node = (I4GLExpressionNode) visit(ctx.ifCondition());
        }
        if (ctx.expression() != null) {
            node = (I4GLExpressionNode) visit(ctx.expression());
        }
        assert node != null;
        if (ctx.IS() != null && ctx.NULL() != null) {
            node = I4GLIsNullNodeGen.create(node);
            setSourceFromContext(node, ctx);
            node.addExpressionTag();
        }
        if (ctx.NOT() != null) {
            node = I4GLNotNodeGen.create(node);
            setSourceFromContext(node, ctx);
            node.addExpressionTag();
        }
        return node;
    }

    @Override
    public Node visitExpression(final I4GLParser.ExpressionContext ctx) {
        I4GLExpressionNode leftNode = null;
        int index = 0;
        for (final I4GLParser.TermContext termCtx : ctx.term()) {
            final I4GLExpressionNode rightNode = (I4GLExpressionNode) visit(termCtx);
            if (leftNode == null) {
                leftNode = rightNode;
            } else {
                final I4GLParser.AddingOperatorContext operatorCtx = ctx.addingOperator(index++);
                if (operatorCtx.PLUS() != null) {
                    leftNode = I4GLAddNodeGen.create(leftNode, rightNode);
                } else /* operatorCtx.MINUS() != null */ {
                    leftNode = I4GLSubtractNodeGen.create(leftNode, rightNode);
                }
            }
        }
        setSourceFromContext(leftNode, ctx);
        leftNode.addExpressionTag();
        return leftNode;
    }

    @Override
    public Node visitTerm(final I4GLParser.TermContext ctx) {
        I4GLExpressionNode leftNode = null;
        int index = 0;
        for (final I4GLParser.FactorContext factorCtx : ctx.factor()) {
            final I4GLExpressionNode rightNode = (I4GLExpressionNode) visit(factorCtx);
            if (leftNode == null) {
                leftNode = rightNode;
            } else {
                final I4GLParser.MultiplyingOperatorContext operatorCtx = ctx.multiplyingOperator(index++);
                if (operatorCtx.STAR() != null) {
                    leftNode = I4GLMultiplyNodeGen.create(leftNode, rightNode);
                } else if (operatorCtx.SLASH() != null) {
                    leftNode = I4GLDivideNodeGen.create(leftNode, rightNode);
                } else if (operatorCtx.DIV() != null) {
                    leftNode = I4GLDivideIntegerNodeGen.create(leftNode, rightNode);
                } else /* operatorCtx.MOD() != null */ {
                    leftNode = I4GLModuloNodeGen.create(leftNode, rightNode);
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
        final List<I4GLExpressionNode> parameterNodes = new ArrayList<>(valueListCtx.size());
        for (final I4GLParser.DisplayValueContext valueCtx : valueListCtx) {
            parameterNodes.add((I4GLExpressionNode) visit(valueCtx));
        }
        I4GLDisplayNode node = new I4GLDisplayNode(
                parameterNodes.toArray(new I4GLExpressionNode[parameterNodes.size()]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    private I4GLBlockNode mergeBlocks(Iterable<? extends ParserRuleContext> contextList) {
        List<I4GLStatementNode> statementNodes = new ArrayList<>();
        for (ParseTree variableDeclarationCtx : contextList) {
            I4GLBlockNode blockNode = (I4GLBlockNode) visit(variableDeclarationCtx);
            assert blockNode != null;
            if (blockNode instanceof I4GLBlockNode) {
                statementNodes.addAll(blockNode.getStatements());
            }
        }
        return new I4GLBlockNode(statementNodes.toArray(new I4GLStatementNode[statementNodes.size()]));
    }

    @Override
    public Node visitTypeDeclarations(final I4GLParser.TypeDeclarationsContext ctx) {
        I4GLBlockNode node = mergeBlocks(ctx.typeDeclaration());
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitTypeDeclaration(final I4GLParser.TypeDeclarationContext ctx) {
        I4GLBlockNode node = mergeBlocks(ctx.variableDeclaration());
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    class TypeNode extends Node {
        public final I4GLType descriptor;

        public TypeNode(final I4GLType descriptor) {
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
            List<I4GLStatementNode> initializationNodes = new ArrayList<>();
            final I4GLType type = typeNode.descriptor;
            for (final String identifier : identifierList) {
                FrameSlot frameSlot = currentLexicalScope.registerLocalVariable(identifier, type);
                I4GLStatementNode initializationNode;
                Object defaultValue;
                if (type instanceof I4GLTextType) {
                    // This is hacky, but that is what c4gl compiler does
                    defaultValue = I4GLNull.SINGLETON;
                } else {
                    defaultValue = type.getDefaultValue();
                }
                assert defaultValue != null;
                initializationNode = InitializationNodeFactory.create(frameSlot, defaultValue, null);
                initializationNodes.add(initializationNode);
            }

            I4GLBlockNode node = new I4GLBlockNode(
                    initializationNodes.toArray(new I4GLStatementNode[initializationNodes.size()]));
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
            return new TypeNode(new I4GLVarcharType(size));
        } else {
            int size = 1;
            if (!ctx.numericConstant().isEmpty()) {
                size = Integer.parseInt(ctx.numericConstant(0).getText());
            }
            return new TypeNode(new I4GLCharType(size));
        }
    }

    @Override
    public Node visitNumberType(final I4GLParser.NumberTypeContext ctx) {
        if (ctx.SMALLINT() != null) {
            return new TypeNode(I4GLSmallIntType.SINGLETON);
        }
        if (ctx.INTEGER() != null || ctx.INT() != null) {
            return new TypeNode(I4GLIntType.SINGLETON);
        }
        if (ctx.BIGINT() != null) {
            return new TypeNode(I4GLBigIntType.SINGLETON);
        }
        if (ctx.SMALLFLOAT() != null || ctx.REAL() != null) {
            return new TypeNode(I4GLSmallFloatType.SINGLETON);
        }
        if (ctx.FLOAT() != null || ctx.DOUBLE() != null) {
            return new TypeNode(I4GLFloatType.SINGLETON);
        }
        throw new NotImplementedException();
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
        } else /* there is a ctx.TEXT() */ {
            return new TypeNode(I4GLTextType.SINGLETON);
        }
    }

    @Override
    public Node visitRecordType(final I4GLParser.RecordTypeContext ctx) {
        currentLexicalScope = new I4GLParseScope(currentLexicalScope, "_record");

        if (ctx.LIKE() != null) {
            throw new NotImplementedException();
        }
        for (I4GLParser.VariableDeclarationContext variableDeclarationCtx : ctx.variableDeclaration()) {
            visit(variableDeclarationCtx);
        }

        I4GLRecordType type = currentLexicalScope.createRecordType();

        assert currentLexicalScope.getOuterScope() != null;
        currentLexicalScope = currentLexicalScope.getOuterScope();
        return new TypeNode(type);
    }

    @Override
    public Node visitArrayType(final I4GLParser.ArrayTypeContext ctx) {
        TypeNode typeNode = (TypeNode) visit(ctx.arrayTypeType());
        I4GLArrayType arrayDescriptor = null;
        for (int i = ctx.arrayIndexer().dimensionSize().size() - 1; i >= 0; i--) {
            int size = Integer.parseInt(ctx.arrayIndexer().dimensionSize(i).getText());
            if (arrayDescriptor == null) {
                arrayDescriptor = new I4GLArrayType(size, typeNode.descriptor);
            } else {
                arrayDescriptor = new I4GLArrayType(size, arrayDescriptor);
            }
        }
        return new TypeNode(arrayDescriptor);
    }

    @Override
    public Node visitDynArrayType(final I4GLParser.DynArrayTypeContext ctx) {
        throw new NotImplementedException();
    }

    @Override
    public Node visitAssignmentValue(final I4GLParser.AssignmentValueContext ctx) {
        if (ctx.NULL() != null) {
            throw new NotImplementedException();
        }
        if (ctx.expressionList().expression().size() != 1) {
            throw new NotImplementedException();
        }
        return visit(ctx.expressionList().expression(0));
    }

    @Override
    public Node visitAssignmentStatement(final I4GLParser.AssignmentStatementContext ctx) {
        if (ctx.simpleAssignmentStatement() != null) {
            return visit(ctx.simpleAssignmentStatement());
        }
        return visit(ctx.multipleAssignmentStatement());
    }

    @Override
    public Node visitSimpleAssignmentStatement(final I4GLParser.SimpleAssignmentStatementContext ctx) {
        final I4GLExpressionNode valueNode = (I4GLExpressionNode) visit(ctx.assignmentValue());
        return createAssignmentNode(ctx.variable(), valueNode);
    }

    @Override
    public Node visitMultipleAssignmentStatement(final I4GLParser.MultipleAssignmentStatementContext ctx) {
        throw new NotImplementedException();
    }

    private I4GLStatementNode createAssignmentToArray(I4GLExpressionNode arrayExpression,
            List<I4GLExpressionNode> indexNodes, I4GLExpressionNode valueNode) throws LexicalException {
        I4GLExpressionNode readIndexedNode = arrayExpression;
        I4GLStatementNode result = null;
        final int lastIndex = indexNodes.size() - 1;
        for (int index = 0; index < indexNodes.size(); index++) {
            assert readIndexedNode != null;
            I4GLType actualType = readIndexedNode.getType();
            if (!(actualType instanceof I4GLArrayType)) {
                throw new TypeMismatchException(actualType.toString(), "Array");
            }
            I4GLExpressionNode indexNode = indexNodes.get(index);
            I4GLType returnType = ((I4GLArrayType) actualType).getValuesType();
            if (index == lastIndex) {
                result = I4GLAssignToIndexedNodeGen.create(readIndexedNode, indexNode, valueNode);
            } else {
                readIndexedNode = I4GLReadFromIndexedNodeGen.create(readIndexedNode, indexNode, returnType);
            }
        }
        assert result != null;
        return result;
    }

    @Override
    public Node visitSimpleVariable(final I4GLParser.SimpleVariableContext ctx) {
        try {
            final String identifier = ctx.identifier().getText();
            I4GLExpressionNode variableNode = doLookup(identifier, (final I4GLParseScope foundInLexicalScope,
                    final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier, foundInLexicalScope));
            setSourceFromContext(variableNode, ctx);
            variableNode.addExpressionTag();
            return variableNode;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitRecordVariable(final I4GLParser.RecordVariableContext ctx) {
        try {
            I4GLExpressionNode variableNode = (I4GLExpressionNode) visit(ctx.simpleVariable());
            for (int index = 0; index < ctx.identifier().size(); index++) {
                final String identifier = ctx.identifier(index).getText();
                variableNode = createReadFromRecordNode(variableNode, identifier);
            }
            setSourceFromContext(variableNode, ctx);
            variableNode.addExpressionTag();
            return variableNode;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitIndexedVariable(final I4GLParser.IndexedVariableContext ctx) {
        try {
            I4GLExpressionNode variableNode = (I4GLExpressionNode) visit(ctx.notIndexedVariable());
            final List<I4GLParser.ExpressionContext> indexList = ctx.variableIndex().expressionList().expression();
            if (indexList.size() > 3) {
                throw new ParseException(source, ctx, DIMENSIONS_STRING + indexList.size());
            }
            List<I4GLExpressionNode> indexNodes = new ArrayList<>(indexList.size());
            for (I4GLParser.ExpressionContext indexCtx : indexList) {
                indexNodes.add((I4GLExpressionNode) visit(indexCtx));
            }
            variableNode = createReadFromIndexedNode(variableNode, indexNodes);
            setSourceFromContext(variableNode, ctx);
            variableNode.addExpressionTag();
            return variableNode;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private I4GLReadFromRecordNode createReadFromRecordNode(final I4GLExpressionNode recordExpression,
            final String identifier) throws LexicalException {
        I4GLType descriptor = recordExpression.getType();
        I4GLType returnType = null;

        if (!(descriptor instanceof I4GLRecordType)) {
            throw new TypeMismatchException(descriptor.toString(), RECORD_STRING);
        }
        I4GLRecordType accessedRecordType = (I4GLRecordType) descriptor;
        if (!accessedRecordType.containsIdentifier(identifier)) {
            throw new UnknownIdentifierException("The record does not contain this identifier");
        }

        returnType = accessedRecordType.getVariableType(identifier);
        return I4GLReadFromRecordNodeGen.create(recordExpression, identifier, returnType);
    }

    private I4GLExpressionNode createReadFromIndexedNode(I4GLExpressionNode arrayExpression,
            List<I4GLExpressionNode> indexNodes) throws LexicalException {
        I4GLExpressionNode readIndexedNode = arrayExpression;
        for (I4GLExpressionNode indexNode : indexNodes) {
            I4GLType actualType = readIndexedNode.getType();
            if (actualType instanceof I4GLArrayType) {
                actualType = ((I4GLArrayType) actualType).getValuesType();
            } else if (!(actualType instanceof I4GLTextType)) {
                throw new TypeMismatchException(actualType.toString(), "Array");
            }
            readIndexedNode = I4GLReadFromIndexedNodeGen.create(readIndexedNode, indexNode, actualType);
        }
        return readIndexedNode;
    }

    private I4GLExpressionNode createReadVariableFromScope(final String identifier, final I4GLParseScope scope) {
        final FrameSlot variableSlot = scope.getLocalSlot(identifier);
        final I4GLType type = scope.getIdentifierType(identifier);
        final boolean isLocal = scope == currentLexicalScope;

        if (isLocal) {
            return I4GLReadLocalVariableNodeGen.create(variableSlot, type);
        } else {
            return I4GLReadGlobalVariableNodeGen.create(variableSlot, type);
        }
    }

    @Override
    public Node visitString(final I4GLParser.StringContext ctx) {
        String literal = ctx.getText();

        // Remove the trailing and ending "
        assert literal.length() >= 2 && literal.startsWith("\"") && literal.endsWith("\"");
        literal = literal.substring(1, literal.length() - 1);

        I4GLTextLiteralNode node = new I4GLTextLiteralNode(literal.intern());
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitInteger(final I4GLParser.IntegerContext ctx) {
        final String literal = ctx.getText();
        I4GLExpressionNode node;
        try {
            node = I4GLIntLiteralNodeGen.create(Integer.parseInt(literal));
        } catch (final NumberFormatException e) {
            node = I4GLBigIntLiteralNodeGen.create(Long.parseLong(literal));
        }
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitReal(final I4GLParser.RealContext ctx) {
        final String literal = ctx.getText();
        final I4GLSmallFloatLiteralNode node = I4GLSmallFloatLiteralNodeGen.create(Float.parseFloat(literal));
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitDebugStatement(final I4GLParser.DebugStatementContext ctx) {
        final I4GLDebuggerNode node = new I4GLDebuggerNode();
        node.addStatementTag();
        setSourceFromContext(node, ctx);
        return node;
    }

    @Override
    public Node visitDatabaseDeclaration(final I4GLParser.DatabaseDeclarationContext ctx) {
        try {
            String identifier = ctx.identifier(0).getText();
            final FrameSlot databaseSlot = currentLexicalScope.registerDatabase(identifier);
            final I4GLDatabase databaseValue = new I4GLDatabase(identifier);
            I4GLDatabaseNode node = new I4GLDatabaseNode(databaseSlot, databaseValue);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private Node createAssignmentToSimpleVariable(final I4GLParser.SimpleVariableContext ctx,
            final I4GLExpressionNode valueNode) {
        try {
            return createAssignToVariableNode(ctx.identifier().getText(), valueNode);
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private Node createAssignmentToRecordVariable(final I4GLParser.RecordVariableContext ctx,
            final I4GLExpressionNode valueNode) {
        try {
            I4GLExpressionNode variableNode = (I4GLExpressionNode) visit(ctx.simpleVariable());
            int index = 0;
            while (index < ctx.identifier().size() - 1) {
                String identifier = ctx.identifier(index++).getText();
                variableNode = createReadFromRecordNode(variableNode, identifier);
            }
            I4GLType expressionType = variableNode.getType();
            I4GLStatementNode node;
            if (expressionType instanceof I4GLRecordType) {
                String identifier = ctx.identifier(index).getText();
                I4GLRecordType accessedRecordType = (I4GLRecordType) expressionType;
                I4GLType recordType = accessedRecordType.getVariableType(identifier);
                node = I4GLAssignToRecordFieldNodeGen.create(identifier, recordType, variableNode, valueNode);
            } else {
                throw new TypeMismatchException(expressionType.toString(), RECORD_STRING);
            }
            node.addStatementTag();
            setSourceFromContext(node, ctx);
            return node;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private Node createAssignmentToNotIndexedVariable(final I4GLParser.NotIndexedVariableContext ctx,
            final I4GLExpressionNode valueNode) {
        if (ctx.simpleVariable() != null) {
            return createAssignmentToSimpleVariable(ctx.simpleVariable(), valueNode);
        }
        return createAssignmentToRecordVariable(ctx.recordVariable(), valueNode);
    }

    private Node createAssignmentToIndexedSimpleVariable(final I4GLParser.SimpleVariableContext ctx,
            final I4GLParser.VariableIndexContext variableIndexCtx, final I4GLExpressionNode valueNode) {
        try {
            final String identifier = ctx.identifier().getText();
            final I4GLType targetType = doLookup(identifier, I4GLParseScope::getIdentifierType);
            final FrameSlot targetSlot = doLookup(identifier, I4GLParseScope::getLocalSlot);
            final List<I4GLParser.ExpressionContext> indexList = variableIndexCtx.expressionList().expression();
            List<I4GLExpressionNode> indexNodes = new ArrayList<>(indexList.size());
            for (I4GLParser.ExpressionContext indexCtx : indexList) {
                indexNodes.add((I4GLExpressionNode) visit(indexCtx));
            }
            I4GLStatementNode node;
            if (targetType instanceof I4GLArrayType) {
                if (indexNodes.size() > 3) {
                    throw new ParseException(source, ctx, DIMENSIONS_STRING + indexList.size());
                }
                I4GLExpressionNode variableNode = doLookup(identifier,
                        (final I4GLParseScope foundInLexicalScope,
                                final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier,
                                        foundInLexicalScope));
                node = createAssignmentToArray(variableNode, indexNodes, valueNode);
            } else if (targetType instanceof I4GLTextType) {
                if (indexNodes.size() != 1) {
                    throw new ParseException(source, ctx, DIMENSIONS_STRING + indexList.size());
                }
                node = I4GLAssignToTextNodeGen.create(indexNodes.get(0), valueNode, targetSlot, targetType);
            } else {
                throw new ParseException(source, ctx,
                        "Variable " + identifier + " must be indexed (string or array) to use []");
            }
            assert node != null;
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private I4GLStatementNode createAssignIndexedRecordVariable(final String identifier, final I4GLExpressionNode variableNode,
            final I4GLParser.VariableIndexContext variableIndexCtx, final I4GLExpressionNode valueNode)
            throws LexicalException {
        I4GLStatementNode node;
        I4GLRecordType accessedRecordType = (I4GLRecordType) variableNode.getType();
        if (!accessedRecordType.containsIdentifier(identifier)) {
            throw new UnknownIdentifierException("The record does not contain this identifier");
        }
        final List<I4GLParser.ExpressionContext> indexList = variableIndexCtx.expressionList().expression();
        List<I4GLExpressionNode> indexNodes = new ArrayList<>(indexList.size());
        for (I4GLParser.ExpressionContext indexCtx : indexList) {
            indexNodes.add((I4GLExpressionNode) visit(indexCtx));
        }
        I4GLType recordType = accessedRecordType.getVariableType(identifier);
        if (recordType instanceof I4GLArrayType) {
            if (indexList.size() > 3) {
                throw new LexicalException(DIMENSIONS_STRING + indexList.size());
            }
            I4GLReadFromRecordNode readNode = createReadFromRecordNode(variableNode, identifier);
            node = createAssignmentToArray(readNode, indexNodes, valueNode);
        } else if (recordType instanceof I4GLTextType) {
            if (indexList.size() != 1) {
                throw new LexicalException(DIMENSIONS_STRING + indexList.size());
            }
            node = I4GLAssignToRecordTextNodeGen.create(identifier, recordType, variableNode, indexNodes.get(0),
                    valueNode);
        } else {
            throw new LexicalException("Variable must be indexed (string or array) to assign to []");
        }
        assert node != null;
        return node;
    }

    private Node createAssignmentToIndexedRecordVariable(final I4GLParser.RecordVariableContext ctx,
            final I4GLParser.VariableIndexContext variableIndexCtx, final I4GLExpressionNode valueNode) {
        try {
            I4GLExpressionNode variableNode = (I4GLExpressionNode) visit(ctx.simpleVariable());
            int index = 0;
            while (index < ctx.identifier().size() - 1) {
                String identifier = ctx.identifier(index++).getText();
                variableNode = createReadFromRecordNode(variableNode, identifier);
            }
            I4GLType expressionType = variableNode.getType();
            if (!(expressionType instanceof I4GLRecordType)) {
                throw new TypeMismatchException(expressionType.toString(), RECORD_STRING);
            }
            String identifier = ctx.identifier(index).getText();
            I4GLStatementNode node = createAssignIndexedRecordVariable(identifier, variableNode, variableIndexCtx, valueNode);
            node.addStatementTag();
            setSourceFromContext(node, ctx);
            return node;
    } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private Node createAssignmentToIndexedVariable(final I4GLParser.IndexedVariableContext ctx,
            final I4GLExpressionNode valueNode) {
        final NotIndexedVariableContext notIndexedVariableContext = ctx.notIndexedVariable();
        if (notIndexedVariableContext.simpleVariable() != null) {
            return createAssignmentToIndexedSimpleVariable(notIndexedVariableContext.simpleVariable(),
                    ctx.variableIndex(), valueNode);
        }
        return createAssignmentToIndexedRecordVariable(notIndexedVariableContext.recordVariable(), ctx.variableIndex(),
                valueNode);
    }

    private Node createAssignmentNode(final I4GLParser.VariableContext ctx, final I4GLExpressionNode valueNode) {
        if (ctx.notIndexedVariable() != null) {
            return createAssignmentToNotIndexedVariable(ctx.notIndexedVariable(), valueNode);
        }
        return createAssignmentToIndexedVariable(ctx.indexedVariable(), valueNode);
    }

    @Override
    public Node visitMainSelectStatement(final I4GLParser.MainSelectStatementContext ctx) {
        try {
            int start = ctx.start.getStartIndex();
            int end;
            Interval interval;
            String sql = "";
            FrameSlot[] returnSlots = new FrameSlot[0];
            if (ctx.INTO() != null) {
                end = ctx.INTO().getSymbol().getStartIndex() - 1;
                interval = new Interval(start, end);
                sql = ctx.start.getInputStream().getText(interval);
                start = ctx.identifier(ctx.identifier().size() - 1).stop.getStopIndex() + 1;

                List<FrameSlot> returnSlotList = new ArrayList<>(ctx.identifier().size());
                for (final I4GLParser.IdentifierContext identifierCtx : ctx.identifier()) {
                    final String resultIdentifier = identifierCtx.getText();
                    FrameSlot recordSlot = currentLexicalScope.getLocalSlot(resultIdentifier);
                    returnSlotList.add(recordSlot);
                }
                returnSlots = returnSlotList.toArray(new FrameSlot[returnSlotList.size()]);
            }
            end = ctx.stop.getStopIndex();
            interval = new Interval(start, end);
            sql += ctx.start.getInputStream().getText(interval);
            I4GLExpressionNode readVariableNode = createReadVariableNode("_database");
            I4GLSelectNode node = new I4GLSelectNode(readVariableNode, sql, returnSlots);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitSimpleSelectStatement(final I4GLParser.SimpleSelectStatementContext ctx) {
        throw new NotImplementedException();
    }
}