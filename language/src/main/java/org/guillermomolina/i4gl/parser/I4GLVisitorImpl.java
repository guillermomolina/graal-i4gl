package org.guillermomolina.i4gl.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.ParserRuleContext;
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
import org.guillermomolina.i4gl.nodes.call.InvokeNodeGen;
import org.guillermomolina.i4gl.nodes.call.ReadArgumentNode;
import org.guillermomolina.i4gl.nodes.call.ReturnNode;
import org.guillermomolina.i4gl.nodes.control.CaseNode;
import org.guillermomolina.i4gl.nodes.control.ForNode;
import org.guillermomolina.i4gl.nodes.control.IfNode;
import org.guillermomolina.i4gl.nodes.control.WhileNode;
import org.guillermomolina.i4gl.nodes.function.FunctionBodyNode;
import org.guillermomolina.i4gl.nodes.literals.DoubleLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.IntLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.LogicLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.LongLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.literals.StringLiteralNodeGen;
import org.guillermomolina.i4gl.nodes.logic.AndNodeGen;
import org.guillermomolina.i4gl.nodes.logic.EqualsNodeGen;
import org.guillermomolina.i4gl.nodes.logic.LessThanNodeGen;
import org.guillermomolina.i4gl.nodes.logic.LessThanOrEqualNodeGen;
import org.guillermomolina.i4gl.nodes.logic.NotNodeGen;
import org.guillermomolina.i4gl.nodes.logic.OrNodeGen;
import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.nodes.statement.BlockNode;
import org.guillermomolina.i4gl.nodes.statement.DisplayNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.nodes.variables.read.ReadConstantNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.ReadGlobalVariableNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.ReadLocalVariableNodeGen;
import org.guillermomolina.i4gl.nodes.variables.read.ReadReferenceVariableNodeGen;
import org.guillermomolina.i4gl.nodes.variables.write.SimpleAssignmentNode;
import org.guillermomolina.i4gl.nodes.variables.write.SimpleAssignmentNodeGen;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.exceptions.UnknownIdentifierException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.ReferenceDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.ConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.BooleanDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.function.FunctionDescriptor;
import org.guillermomolina.i4gl.parser.utils.FormalParameter;

public class I4GLVisitorImpl extends I4GLBaseVisitor<Node> {

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

    /* State while parsing a source unit. */
    // private final Source source;
    private RootNode mainRootNode;

    /* State while parsing a block. */
    private final I4GLLanguage language;
    private LexicalScope currentLexicalScope;

    private final List<String> errorList;

    public I4GLVisitorImpl(final I4GLLanguage language, final Source source) {
        this.language = language;
        // this.source = source;
        this.currentLexicalScope = new LexicalScope(null, "GLOBAL");
        this.errorList = new ArrayList<>();
    }

    private void reportError(/* ParserRuleContext context, */final String error) {
        errorList.add(error);
    }

    public RootNode getRootNode() {
        return mainRootNode;
    }

    public List<String> getErrorList() {
        return errorList;
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
     * @param withReturnType flag whether to also lookup for functions' return
     *                       variable which are write-only
     * @param <T>            type of the returned object
     * @return the value return from the
     *         {@link GlobalObjectLookup#onFound(LexicalScope, String)} function
     */
    private <T> T doLookup(final String identifier, final GlobalObjectLookup<T> lookupFunction,
            final boolean withReturnType) throws UnknownIdentifierException {
        try {
            T result = lookupToParentScope(this.currentLexicalScope, identifier, lookupFunction, withReturnType, false);
            if (result == null) {
                throw new UnknownIdentifierException(identifier);
            }
            return result;
        } catch (final LexicalException e) {
            reportError(e.getMessage());
            return null;
        }
    }

    /**
     * Overload of
     * {@link NodeFactory#doLookup(String, GlobalObjectLookup, boolean)}.
     */
    private <T> T doLookup(final String identifier, final GlobalObjectLookup<T> lookupFunction)
            throws UnknownIdentifierException {
        return this.doLookup(identifier, lookupFunction, false);
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
     */
    private <T> T lookupToParentScope(LexicalScope scope, final String identifier,
            final GlobalObjectLookup<T> lookupFunction, boolean withReturnType, final boolean onlyPublic)
            throws LexicalException {
        while (scope != null) {
            if ((onlyPublic) ? scope.containsPublicIdentifier(identifier) : scope.containsLocalIdentifier(identifier)) {
                return lookupFunction.onFound(scope, identifier);
            } else {
                scope = scope.getOuterScope();
            }
            withReturnType = false; // NOTE: return variable may be only in current scope
        }

        return null;
    }

    public TypeDescriptor getTypeDescriptor(final String identifier) throws UnknownIdentifierException {
        return this.doLookup(identifier, LexicalScope::getTypeDescriptor);
    }

    @Override
    public Node visitMainBlock(final I4GLParser.MainBlockContext ctx) {
        mainRootNode = createFunction("MAIN", null, ctx.typeDeclarations(), ctx.mainStatements());
        return mainRootNode;
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
        return new BlockNode(statementNodes.toArray(new StatementNode[statementNodes.size()]));
    }

    @Override
    public Node visitFunctionDefinition(final I4GLParser.FunctionDefinitionContext ctx) {
        final String functionIdentifier = ctx.functionIdentifier().getText();
        List<String> parameteridentifiers = null;
        if (ctx.parameterList() != null) {
            parameteridentifiers = new ArrayList<>(ctx.parameterList().parameterGroup().size());
            for (final I4GLParser.ParameterGroupContext parameterCtx : ctx.parameterList().parameterGroup()) {
                parameteridentifiers.add(parameterCtx.getText());
            }
        }
        return createFunction(functionIdentifier, parameteridentifiers, ctx.typeDeclarations(), ctx.codeBlock());
    }

    public I4GLRootNode createFunction(final String functionIdentifier, List<String> parameteridentifiers,
            I4GLParser.TypeDeclarationsContext typeDeclarationsContext, ParserRuleContext codeBlockContext) {
        try {
            final FunctionDescriptor functionDescriptor = currentLexicalScope.registerFunction(functionIdentifier);

            currentLexicalScope = new LexicalScope(currentLexicalScope, functionIdentifier);
            if (parameteridentifiers != null) {
                for (final String parameteridentifier : parameteridentifiers) {
                    currentLexicalScope.addArgument(parameteridentifier);
                }
            }

            List<StatementNode> blockNodes = new ArrayList<>();
            if (typeDeclarationsContext != null) {
                BlockNode initializationNode = (BlockNode) visit(typeDeclarationsContext);
                if (initializationNode != null) {
                    blockNodes.addAll(initializationNode.getStatements());
                }
            }

            final List<FormalParameter> formalParameters = new ArrayList<>();
            for (String parameterIdentifier : currentLexicalScope.arguments) {
                TypeDescriptor typeDescriptor = currentLexicalScope.getIdentifierDescriptor(parameterIdentifier);
                formalParameters.add(new FormalParameter(parameterIdentifier, typeDescriptor, functionDescriptor));
            }
            functionDescriptor.setFormalParameters(formalParameters);

            if (codeBlockContext != null) {
                BlockNode bodyNode = (BlockNode) visit(codeBlockContext);
                if (bodyNode != null) {
                    blockNodes.addAll(bodyNode.getStatements());
                }
            }

            BlockNode blockNode = new BlockNode(blockNodes.toArray(new StatementNode[blockNodes.size()]));
            final FunctionBodyNode functionBodyNode = new FunctionBodyNode(blockNode);
            final I4GLRootNode rootNode = new I4GLRootNode(language, currentLexicalScope.getFrameDescriptor(),
                    functionBodyNode);
            currentLexicalScope = currentLexicalScope.getOuterScope();
            currentLexicalScope.setFunctionRootNode(language, functionIdentifier, rootNode);
            return rootNode;
        } catch (final LexicalException | UnknownIdentifierException e) {
            reportError(e.getMessage());
            return null;
        }
    }

    /**
     * Creates {@link SimpleAssignmentNode} for the specified variable and value.
     * 
     * @param targetIdentifier the variable's identifier
     * @param valueNode        assigning value's node
     * @return the newly created node
     */
    private SimpleAssignmentNode createAssignmentNode(final String targetIdentifier, final ExpressionNode valueNode) {
        try {
            final TypeDescriptor targetType = doLookup(targetIdentifier, LexicalScope::getIdentifierDescriptor);
            this.checkTypesAreCompatible(valueNode.getType(), targetType);
            final FrameSlot targetSlot = doLookup(targetIdentifier, LexicalScope::getLocalSlot);

            return SimpleAssignmentNodeGen.create(valueNode, targetSlot);
        } catch (final UnknownIdentifierException e) {
            reportError(e.getMessage());
            return null;
        }
    }

    /**
     * Checks whether the two types are compatible. The operation is not symmetric.
     */
    private boolean checkTypesAreCompatible(final TypeDescriptor leftType, final TypeDescriptor rightType) {
        if ((leftType != rightType) && (!leftType.convertibleTo(rightType))
                && (rightType instanceof FunctionDescriptor)) {
            reportError("Type mismatch");
            return false;
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
        return new BlockNode(statementNodes.toArray(new StatementNode[statementNodes.size()]));
    }

    @Override
    public Node visitProcedureStatement(final I4GLParser.ProcedureStatementContext ctx) {
        final String identifier = ctx.procedureIdentifier().getText();

        final List<I4GLParser.ActualParameterContext> attributeListCtx = ctx.actualParameter();
        final List<ExpressionNode> parameterNodes = new ArrayList<>(attributeListCtx.size());
        for (final I4GLParser.ActualParameterContext attributeCtx : attributeListCtx) {
            parameterNodes.add((ExpressionNode) visit(attributeCtx));
        }
        try {
            final ExpressionNode callNode = createFunctionCall(identifier, parameterNodes);
            if (!ctx.variable().isEmpty()) {
                String resultIdentifier = ctx.variable(0).getText();
                return createAssignmentNode(resultIdentifier, callNode);
            }
            return callNode;
        } catch (final LexicalException e) {
            reportError(e.getMessage());
            return null;
        }
    }

    /**
     * Creates {@link InvokeNode} for call statement with specified parameters.
     * 
     * @param identifierToken identifier of the called function
     * @param params          argument expression nodes
     * @return the newly created node
     */
    public ExpressionNode createFunctionCall(final String identifier, final List<ExpressionNode> params)
            throws LexicalException {
        try {
            return doLookup(identifier, (final LexicalScope foundInScope, final String foundIdentifier) -> {
                if (foundInScope.isFunction(foundIdentifier)) {
                    final FunctionDescriptor functionDescriptor = foundInScope.getFunctionDescriptor(foundIdentifier,
                            params);
                    functionDescriptor.verifyArguments(params);
                    return this.createInvokeNode(foundIdentifier, functionDescriptor, foundInScope, params);
                } else {
                    throw new LexicalException(foundIdentifier + " is not a function.");
                }
            });
        } catch (final UnknownIdentifierException e) {
            // Forward declaration of function in local unit
            LexicalScope scope = currentLexicalScope;
            while (scope.getOuterScope() != null) {
                scope = scope.getOuterScope();
            }
            final FunctionDescriptor functionDescriptor = scope.registerFunction(identifier);

            return this.createInvokeNode(identifier, functionDescriptor, scope, params);
        }
    }

    private ExpressionNode createInvokeNode(final String identifier, final FunctionDescriptor descriptor,
            final LexicalScope functionScope, final List<ExpressionNode> argumentNodes) {
        final ExpressionNode[] arguments = argumentNodes.toArray(new ExpressionNode[argumentNodes.size()]);
        try {
            final TypeDescriptor returnType = getTypeDescriptor("INTEGER");
            return InvokeNodeGen.create(language, identifier, arguments, returnType);
        } catch (UnknownIdentifierException e) {
            reportError(e.getMessage());
            return null;
        }
    }

    @Override
    public Node visitReturnStatement(final I4GLParser.ReturnStatementContext ctx) {
        final List<ExpressionNode> parameterNodes = new ArrayList<>();
        if (ctx.variableOrConstantList() != null) {
            final List<I4GLParser.ExpressionContext> parameterListCtx = ctx.variableOrConstantList().expression();
            for (final I4GLParser.ExpressionContext parameterCtx : parameterListCtx) {
                parameterNodes.add((ExpressionNode) visit(parameterCtx));
            }
        } else {
            parameterNodes.add(IntLiteralNodeGen.create(0));
        }
        return new ReturnNode(parameterNodes.get(0));
    }

    @Override
    public Node visitWhileStatement(final I4GLParser.WhileStatementContext ctx) {
        ExpressionNode condition = (ExpressionNode) visit(ctx.ifCondition());
        StatementNode loopBody = (StatementNode) visit(ctx.codeBlock());
        return new WhileNode(condition, loopBody);
    }

    @Override
    public Node visitForStatement(final I4GLParser.ForStatementContext ctx) {
        try {
            String iteratingIdentifier = ctx.controlVariable().getText();
            ExpressionNode startValue = (ExpressionNode) visit(ctx.initialValue());
            ExpressionNode finalValue = (ExpressionNode) visit(ctx.finalValue());
            ExpressionNode stepValue = null;
            if (ctx.numericConstant() != null) {
                stepValue = (ExpressionNode) visit(ctx.numericConstant());
            } else {
                stepValue = IntLiteralNodeGen.create(1);
            }
            StatementNode loopBody = (StatementNode) visit(ctx.codeBlock());
            FrameSlot controlSlot = this.doLookup(iteratingIdentifier, LexicalScope::getLocalSlot);
            if (startValue.getType() != finalValue.getType()
                    && !startValue.getType().convertibleTo(finalValue.getType())) {
                reportError("Type mismatch in beginning and last value of for loop.");
            }
            SimpleAssignmentNode initialAssignment = createAssignmentNode(iteratingIdentifier, startValue);
            ExpressionNode readControlVariableNode = createReadVariableNode(iteratingIdentifier);
            SimpleAssignmentNode stepNode = createAssignmentNode(iteratingIdentifier,
                    AddNodeGen.create(readControlVariableNode, stepValue));
            return new ForNode(initialAssignment, controlSlot, startValue, finalValue, stepNode,
                    readControlVariableNode, loopBody);
        } catch (final UnknownIdentifierException e) {
            reportError(e.getMessage());
            return null;
        }
    }

    @Override
    public Node visitIfStatement(final I4GLParser.IfStatementContext ctx) {
        ExpressionNode condition = (ExpressionNode) visit(ctx.ifCondition());
        StatementNode thenNode = (StatementNode) visit(ctx.codeBlock(0));
        StatementNode elseNode = null;
        if (ctx.codeBlock().size() == 2) {
            elseNode = (StatementNode) visit(ctx.codeBlock(1));
        }
        return new IfNode(condition, thenNode, elseNode);
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
        ExpressionNode[] indexes = indexNodes.toArray(new ExpressionNode[indexNodes.size()]);
        StatementNode[] statements = statementNodes.toArray(new StatementNode[statementNodes.size()]);
        StatementNode otherwiseNode = null;
        if (ctx.OTHERWISE() != null) {
            otherwiseNode = (StatementNode) visit(ctx.codeBlock(codeBlockIndex));
        }
        return new CaseNode(caseExpression, indexes, statements, otherwiseNode);
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
        return elseNode;
    }

    @Override
    public Node visitIfCondition(final I4GLParser.IfConditionContext ctx) {
        if (ctx.TRUE() != null) {
            return LogicLiteralNodeGen.create(true);
        }
        if (ctx.FALSE() != null) {
            return LogicLiteralNodeGen.create(false);
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
                leftNode = DivideIntegerNodeGen.create(leftNode, rightNode);
            } else /* operatorCtx.MATCHES() != null */ {
                throw new NotImplementedException();
                /*
                 * leftNode = MatchesNodeGen.create(leftNode, rightNode); if(operatorCtx.NOT()
                 * != null) { leftNode = NotNodeGen.create(leftNode); }
                 */
            }
        }
        if (leftNode.getType() != BooleanDescriptor.getInstance()) {
            leftNode = NotNodeGen.create(EqualsNodeGen.create(leftNode, IntLiteralNodeGen.create(0)));
        }
        return leftNode;
    }

    @Override
    public Node visitIfCondition2(final I4GLParser.IfCondition2Context ctx) {
        ExpressionNode leftNode = null;
        for (final I4GLParser.IfLogicalTermContext context : ctx.ifLogicalTerm()) {
            final ExpressionNode rightNode = (ExpressionNode) visit(context);
            leftNode = leftNode == null ? rightNode : OrNodeGen.create(leftNode, rightNode);
        }
        return leftNode;
    }

    @Override
    public Node visitIfLogicalTerm(final I4GLParser.IfLogicalTermContext ctx) {
        ExpressionNode leftNode = null;
        for (final I4GLParser.IfLogicalFactorContext context : ctx.ifLogicalFactor()) {
            final ExpressionNode rightNode = (ExpressionNode) visit(context);
            leftNode = leftNode == null ? rightNode : AndNodeGen.create(leftNode, rightNode);
        }
        return leftNode;
    }

    @Override
    public Node visitIfLogicalFactor(final I4GLParser.IfLogicalFactorContext ctx) {
        if (ctx.LPAREN() != null) {
            return visit(ctx.ifCondition());
        }
        if (ctx.NOT() != null) {
            return NotNodeGen.create((ExpressionNode) visit(ctx.ifCondition()));
        }
        return visit(ctx.simpleExpression());
    }

    @Override
    public Node visitSimpleExpression(final I4GLParser.SimpleExpressionContext ctx) {
        ExpressionNode leftNode = null;
        int index = 0;
        for (final I4GLParser.TermContext termCtx : ctx.term()) {
            final ExpressionNode rightNode = (ExpressionNode) visit(termCtx);
            if (leftNode == null) {
                leftNode = rightNode;
                /*
                 * if(ctx.MINUS() != null) { leftNode =
                 * SubtractNodeGen.create(IntLiteralNodeGen.create(0), rightNode); }
                 */
            } else {
                final I4GLParser.AddingOperatorContext operatorCtx = ctx.addingOperator(index++);
                if (operatorCtx.PLUS() != null) {
                    leftNode = AddNodeGen.create(leftNode, rightNode);
                } else /* operatorCtx.MINUS() != null */ {
                    leftNode = SubtractNodeGen.create(leftNode, rightNode);
                }
            }
        }
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
        return leftNode;
    }

    @Override
    public Node visitFactor(final I4GLParser.FactorContext ctx) {
        ExpressionNode factor = null;
        if (ctx.variable() != null) {
            factor = (ExpressionNode) visit(ctx.variable());
        } else if (ctx.constant() != null) {
            factor = (ExpressionNode) visit(ctx.constant());
        } else if (ctx.expression() != null) {
            factor = (ExpressionNode) visit(ctx.expression());
        } else /* ctx.factor() != null */ {
            factor = NotNodeGen.create((ExpressionNode) visit(ctx.factor()));
        }
        if (ctx.unitType() != null) {
            throw new NotImplementedException();
        }
        return factor;
    }

    @Override
    public Node visitDisplayStatement(final I4GLParser.DisplayStatementContext ctx) {
        final List<I4GLParser.ExpressionContext> attributeListCtx = ctx.expression();
        final List<ExpressionNode> parameterNodes = new ArrayList<>(attributeListCtx.size());
        for (final I4GLParser.ExpressionContext attributeCtx : attributeListCtx) {
            parameterNodes.add((ExpressionNode) visit(attributeCtx));
        }
        return new DisplayNode(parameterNodes.toArray(new ExpressionNode[parameterNodes.size()]));
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
        return mergeBlocks(ctx.typeDeclaration());
    }

    @Override
    public Node visitTypeDeclaration(final I4GLParser.TypeDeclarationContext ctx) {
        return mergeBlocks(ctx.variableDeclaration());
    }

    @Override
    public Node visitVariableDeclaration(final I4GLParser.VariableDeclarationContext ctx) {
        final String type = ctx.type().getText();
        final List<I4GLParser.ConstantIdentifierContext> identifierCtxList = ctx.constantIdentifier();
        final List<String> identifierList = new ArrayList<>(identifierCtxList.size());
        for (final I4GLParser.ConstantIdentifierContext identifierCtx : identifierCtxList) {
            identifierList.add(identifierCtx.getText());
        }
        List<StatementNode> initializationNodes = new ArrayList<>();
        try {
            final TypeDescriptor typeDescriptor = getTypeDescriptor(type);
            for (final String identifier : identifierList) {
                currentLexicalScope.registerLocalVariable(identifier, typeDescriptor);
                StatementNode initializationNode;
                final int argumentIndex = currentLexicalScope.arguments.indexOf(identifier);
                if (argumentIndex != -1) {
                    final ExpressionNode readNode = new ReadArgumentNode(argumentIndex, typeDescriptor);
                    initializationNode = createAssignmentNode(identifier, readNode);
                } else {
                    Object defaultValue = typeDescriptor.getDefaultValue();
                    if (defaultValue == null) {
                        throw new LexicalException("Undefined default value for type " + typeDescriptor.toString());
                    }
                    FrameSlot frameSlot = currentLexicalScope.localIdentifiers.getFrameSlot(identifier);
                    initializationNode = InitializationNodeFactory.create(frameSlot, defaultValue, null);

                }
                initializationNodes.add(initializationNode);
            }
        } catch (final UnknownIdentifierException | LexicalException e) {
            reportError(e.getMessage());
        }

        return new BlockNode(initializationNodes.toArray(new StatementNode[initializationNodes.size()]));
    }

    @Override
    public Node visitAssignmentStatement(final I4GLParser.AssignmentStatementContext ctx) {
        final String variableIdentifier = ctx.variable().getText();
        final ExpressionNode valueNode = (ExpressionNode) visit(ctx.expression(0));
        return createAssignmentNode(variableIdentifier, valueNode);
    }

    @Override
    public Node visitVariable(final I4GLParser.VariableContext ctx) {
        return createExpressionFromSingleIdentifier(ctx.getText());
    }

    /**
     * Creates an {@link ExpressionNode} from an identifier (can be variable read or
     * parameterless function call)
     * 
     * @param identifierToken the identifier
     * @return the newly created node
     */
    public ExpressionNode createExpressionFromSingleIdentifier(final String identifier) {
        try {
            return doLookup(identifier, (final LexicalScope foundInLexicalScope, final String foundIdentifier) -> {
                if (foundInLexicalScope.isParameterlessFunction(foundIdentifier)) {
                    final FunctionDescriptor descriptor = (FunctionDescriptor) foundInLexicalScope
                            .getIdentifierDescriptor(foundIdentifier);
                    return this.createInvokeNode(foundIdentifier, descriptor, foundInLexicalScope,
                            Collections.emptyList());
                } else {
                    return createReadVariableFromScope(foundIdentifier, foundInLexicalScope);
                }
            });
        } catch (final UnknownIdentifierException e) {
            reportError(e.getMessage());
            return null;
        }
    }

    /**
     * Creates node that read value from specified variable.
     * 
     * @param identifierToken identifier token of the variable
     * @return the newly created node
     */
    public ExpressionNode createReadVariableNode(final String identifier) {
        try {
            return this.doLookup(identifier, (final LexicalScope foundInScope,
                    final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier, foundInScope));
        } catch (final UnknownIdentifierException e) {
            reportError(e.getMessage());
            return null;
        }
    }

    private ExpressionNode createReadVariableFromScope(final String identifier, final LexicalScope scope) {
        final FrameSlot variableSlot = scope.getLocalSlot(identifier);
        final TypeDescriptor type = scope.getIdentifierDescriptor(identifier);
        final boolean isLocal = scope == currentLexicalScope;
        final boolean isReference = type instanceof ReferenceDescriptor;
        final boolean isConstant = type instanceof ConstantDescriptor;

        if (isConstant) {
            final ConstantDescriptor constantType = (ConstantDescriptor) type;
            return ReadConstantNodeGen.create(constantType.getValue(), constantType);
        } else if (isLocal) {
            // TODO: check if it is a variable
            if (isReference) {
                return ReadReferenceVariableNodeGen.create(variableSlot, type);
            } else {
                return ReadLocalVariableNodeGen.create(variableSlot, type);
            }
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

        return StringLiteralNodeGen.create(literal.intern());
    }

    @Override
    public Node visitInteger(final I4GLParser.IntegerContext ctx) {
        final String literal = ctx.getText();
        try {
            return IntLiteralNodeGen.create(Integer.parseInt(literal));
        } catch (final NumberFormatException e) {
            return LongLiteralNodeGen.create(Long.parseLong(literal));
        }
    }

    @Override
    public Node visitReal(final I4GLParser.RealContext ctx) {
        final String literal = ctx.getText();
        return DoubleLiteralNodeGen.create(Double.parseDouble(literal));
    }
}