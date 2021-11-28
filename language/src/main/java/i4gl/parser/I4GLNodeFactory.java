package i4gl.parser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import i4gl.I4GLLanguage;
import i4gl.exceptions.NotImplementedException;
import i4gl.nodes.arithmetic.I4GLAddNodeGen;
import i4gl.nodes.arithmetic.I4GLDivideIntegerNodeGen;
import i4gl.nodes.arithmetic.I4GLDivideNodeGen;
import i4gl.nodes.arithmetic.I4GLModuloNodeGen;
import i4gl.nodes.arithmetic.I4GLMultiplyNodeGen;
import i4gl.nodes.arithmetic.I4GLSubtractNodeGen;
import i4gl.nodes.call.I4GLCallNode;
import i4gl.nodes.call.I4GLInvokeNode;
import i4gl.nodes.call.I4GLReadArgumentNode;
import i4gl.nodes.call.I4GLReturnNode;
import i4gl.nodes.control.I4GLCaseNode;
import i4gl.nodes.control.I4GLDebuggerNode;
import i4gl.nodes.control.I4GLForNode;
import i4gl.nodes.control.I4GLIfNode;
import i4gl.nodes.control.I4GLWhileNode;
import i4gl.nodes.expression.I4GLClippedNodeGen;
import i4gl.nodes.expression.I4GLConcatenationNodeGen;
import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.literals.I4GLBigIntLiteralNodeGen;
import i4gl.nodes.literals.I4GLIntLiteralNodeGen;
import i4gl.nodes.literals.I4GLSmallFloatLiteralNode;
import i4gl.nodes.literals.I4GLSmallFloatLiteralNodeGen;
import i4gl.nodes.literals.I4GLTextLiteralNode;
import i4gl.nodes.logic.I4GLAndNodeGen;
import i4gl.nodes.logic.I4GLEqualsNodeGen;
import i4gl.nodes.logic.I4GLIsNullNodeGen;
import i4gl.nodes.logic.I4GLLessThanNodeGen;
import i4gl.nodes.logic.I4GLLessThanOrEqualNodeGen;
import i4gl.nodes.logic.I4GLLikeNodeGen;
import i4gl.nodes.logic.I4GLMatchesNodeGen;
import i4gl.nodes.logic.I4GLNotNodeGen;
import i4gl.nodes.logic.I4GLOrNodeGen;
import i4gl.nodes.root.I4GLMainRootNode;
import i4gl.nodes.root.I4GLRootNode;
import i4gl.nodes.sql.I4GLCursorNode;
import i4gl.nodes.sql.I4GLForEachNode;
import i4gl.nodes.sql.I4GLSelectNode;
import i4gl.nodes.sql.I4GLSqlNode;
import i4gl.nodes.statement.I4GLBlockNode;
import i4gl.nodes.statement.I4GLDisplayNode;
import i4gl.nodes.statement.I4GLDisplayNodeGen;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.nodes.variables.read.I4GLReadFromIndexedNodeGen;
import i4gl.nodes.variables.read.I4GLReadFromRecordNode;
import i4gl.nodes.variables.read.I4GLReadFromRecordNodeGen;
import i4gl.nodes.variables.read.I4GLReadFromResultNode;
import i4gl.nodes.variables.read.I4GLReadLocalVariableNodeGen;
import i4gl.nodes.variables.read.I4GLReadNonLocalVariableNodeGen;
import i4gl.nodes.variables.write.I4GLAssignResultsNode;
import i4gl.nodes.variables.write.I4GLAssignToIndexedNodeGen;
import i4gl.nodes.variables.write.I4GLAssignToLocalVariableNodeGen;
import i4gl.nodes.variables.write.I4GLAssignToNonLocalVariableNodeGen;
import i4gl.nodes.variables.write.I4GLAssignToRecordFieldNodeGen;
import i4gl.nodes.variables.write.I4GLAssignToRecordTextNodeGen;
import i4gl.nodes.variables.write.I4GLAssignToTextNodeGen;
import i4gl.parser.I4GLParser.NotIndexedVariableContext;
import i4gl.parser.exceptions.LexicalException;
import i4gl.parser.exceptions.ParseException;
import i4gl.parser.exceptions.TypeMismatchException;
import i4gl.parser.exceptions.UnknownIdentifierException;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.complex.I4GLCursorType;
import i4gl.runtime.types.complex.I4GLDatabaseType;
import i4gl.runtime.types.compound.I4GLArrayType;
import i4gl.runtime.types.compound.I4GLRecordType;
import i4gl.runtime.types.compound.I4GLTextType;
import i4gl.runtime.values.I4GLDatabase;

public class I4GLNodeFactory extends I4GLParserBaseVisitor<Node> {
    private static final String RECORD_STRING = "Record";
    private static final String DIMENSIONS_STRING = "Dimensions can not be ";

    private static final TruffleLogger LOGGER = I4GLLanguage.getLogger(I4GLNodeFactory.class);

    /* State while parsing a source unit. */
    private I4GLDatabase currentDatabase;
    private final Source source;
    private final I4GLLanguage language;
    private I4GLParseScope currentParseScope;
    private FrameDescriptor globalsFrameDescriptor;
    private FrameDescriptor moduleFrameDescriptor;
    private final Map<String, RootCallTarget> allFunctions;
    private final String moduleName;

    public I4GLNodeFactory(final I4GLLanguage language, final Source source) {
        this.language = language;
        this.source = source;
        String localModuleName = source.getName();
        final int extensionIndex = localModuleName.lastIndexOf(".");
        if (extensionIndex != -1) {
            localModuleName = localModuleName.substring(0, extensionIndex);
        }
        this.moduleName = localModuleName;
        this.allFunctions = new HashMap<>();

    }

    public I4GLDatabase getDatabase(final ParserRuleContext ctx) {
        if (currentDatabase == null) {
            try {
                I4GLDatabaseType databaseType = (I4GLDatabaseType) lookupVariableType(
                        I4GLParseScope.DATABASE_IDENTIFIER);
                currentDatabase = (I4GLDatabase) (databaseType.getDefaultValue());
                currentDatabase.connect();
            } catch (LexicalException e) {
                throw new ParseException(source, ctx, "No Database declared");
            }
        }
        return currentDatabase;
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
        T result = lookupToParentScope(currentParseScope, identifier, lookupFunction, false);
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

    public Source getSource() {
        return source;
    }

    private I4GLType lookupVariableType(final String identifier) throws LexicalException {
        return doLookup(identifier, I4GLParseScope::getIdentifierType);
    }

    private FrameSlot lookupVariableSlot(final String identifier) throws LexicalException {
        return doLookup(identifier, I4GLParseScope::getLocalSlot);
    }

    private void trace(final String message) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine(message);
        }
    }

    public String getModuleName() {
        return moduleName;
    }

    public Map<String, RootCallTarget> getAllFunctions() {
        return allFunctions;
    }

    public FrameDescriptor getGlobalsFrameDescriptor() {
        assert globalsFrameDescriptor != null;
        return globalsFrameDescriptor;
    }

    public FrameDescriptor getModuleFrameDescriptor() {
        assert moduleFrameDescriptor != null;
        return moduleFrameDescriptor;
    }

    public I4GLParseScope pushNewScope(final String type, final String name) {
        final String currentScopeName = currentParseScope == null ? "<unset>" : currentParseScope.toString();
        currentParseScope = new I4GLParseScope(currentParseScope, type, name);
        trace(MessageFormat.format("Push scope {0}->{1}", currentScopeName, currentParseScope));
        return currentParseScope;
    }

    public I4GLParseScope popScope() {
        final I4GLParseScope outerScope = currentParseScope.getOuterScope();
        final String outerScopeName = outerScope == null ? "<unset>" : outerScope.toString();
        trace(MessageFormat.format("Pop scope {0}<-{1}", outerScopeName, currentParseScope));
        currentParseScope = outerScope;
        return currentParseScope;
    }

    private void setSourceFromContext(I4GLStatementNode node, ParserRuleContext ctx) {
        Interval sourceInterval = srcFromContext(ctx);
        assert sourceInterval != null;
        if (node == null) {
            throw new ParseException(source, ctx, "Node is null");
        }
        assert node != null;
        node.setSourceSection(sourceInterval.a, sourceInterval.length());
    }

    private static Interval srcFromContext(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        return new Interval(a, b);
    }

    @Override
    public Node visitModule(final I4GLParser.ModuleContext ctx) {
        globalsFrameDescriptor = pushNewScope(I4GLParseScope.GLOBAL_TYPE, null).getFrameDescriptor();
        if (ctx.databaseDeclaration() != null) {
            visit(ctx.databaseDeclaration());
        }
        if (ctx.globalsDeclaration() != null) {
            visit(ctx.globalsDeclaration());
        }
        moduleFrameDescriptor = pushNewScope(I4GLParseScope.MODULE_TYPE, moduleName).getFrameDescriptor();
        if (ctx.typeDeclarations() != null) {
            visit(ctx.typeDeclarations());
        }
        if (ctx.mainFunctionDefinition() != null) {
            visit(ctx.mainFunctionDefinition());
        }
        if (ctx.functionOrReportDefinitions() != null) {
            visit(ctx.functionOrReportDefinitions());
        }
        return null;
    }

    @Override
    public Node visitGlobalsDeclaration(final I4GLParser.GlobalsDeclarationContext ctx) {
        if (ctx.string() != null) {
            throw new NotImplementedException();
        } else if (ctx.typeDeclarations() != null) {
            visit(ctx.typeDeclarations());
        }
        return null;
    }

    @Override
    public Node visitMainFunctionDefinition(final I4GLParser.MainFunctionDefinitionContext ctx) {
        try {
            final String functionIdentifier = "MAIN";
            pushNewScope(I4GLParseScope.FUNCTION_TYPE, functionIdentifier);
            Interval sourceInterval = srcFromContext(ctx);
            final SourceSection sourceSection = source.createSection(sourceInterval.a, sourceInterval.length());
            I4GLBlockNode bodyNode = createFunctionBody(sourceSection, null, ctx.typeDeclarations(),
                    ctx.mainStatements());
            I4GLMainRootNode rootNode = new I4GLMainRootNode(language, currentParseScope.getFrameDescriptor(), bodyNode,
                    sourceSection, functionIdentifier);
            allFunctions.put(functionIdentifier, Truffle.getRuntime().createCallTarget(rootNode));
            popScope();
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
                if (statementNode != null) {
                    statementNodes.add(statementNode);
                } else if (!(child instanceof I4GLParser.DatabaseDeclarationContext)) {
                    throw new ParseException(source, ctx, "Visited an unimplemented Node in MAIN");
                }

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
            pushNewScope(I4GLParseScope.FUNCTION_TYPE, functionIdentifier);
            I4GLBlockNode bodyNode = createFunctionBody(sourceSection, parameteridentifiers, ctx.typeDeclarations(),
                    ctx.codeBlock());
            final I4GLRootNode rootNode = new I4GLRootNode(language, currentParseScope.getFrameDescriptor(), bodyNode,
                    sourceSection, functionIdentifier);
            allFunctions.put(functionIdentifier, Truffle.getRuntime().createCallTarget(rootNode));
            popScope();
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
            visit(typeDeclarationsContext);
        }

        if (parameteridentifiers != null) {
            int argumentIndex = 0;
            for (final String parameteridentifier : parameteridentifiers) {
                final I4GLType type = currentParseScope.getIdentifierType(parameteridentifier);
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

    private I4GLStatementNode createAssignToVariableNode(final String identifier, final I4GLExpressionNode valueNode)
            throws LexicalException {
        return doLookup(identifier,
                (final I4GLParseScope foundInScope, final String foundIdentifier) -> createAssignToVariableFromScope(
                        foundInScope, foundIdentifier, valueNode));
    }

    private I4GLStatementNode createAssignToVariableFromScope(final I4GLParseScope scope, final String identifier,
            final I4GLExpressionNode valueNode) {
        final FrameSlot variableSlot = scope.getLocalSlot(identifier);
        final I4GLType type = scope.getIdentifierType(identifier);
        final boolean isLocal = scope == currentParseScope;

        if (isLocal) {
            return I4GLAssignToLocalVariableNodeGen.create(valueNode, variableSlot, type);
        }
        return I4GLAssignToNonLocalVariableNodeGen.create(valueNode, variableSlot, type, scope.getName());
    }

    @Override
    public Node visitCodeBlock(final I4GLParser.CodeBlockContext ctx) {
        final List<I4GLStatementNode> statementNodes = new ArrayList<>(ctx.children.size());
        for (final ParseTree child : ctx.children) {
            I4GLStatementNode statementNode = (I4GLStatementNode) visit(child);
            if (statementNode != null) {
                statementNodes.add(statementNode);
            } else if (!(child instanceof I4GLParser.DatabaseDeclarationContext)) {
                throw new ParseException(source, ctx, "Visited an unimplemented Node in FUNCTION");
            }
        }
        I4GLBlockNode node = new I4GLBlockNode(statementNodes.toArray(new I4GLStatementNode[statementNodes.size()]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitCallStatement(final I4GLParser.CallStatementContext ctx) {
        final I4GLInvokeNode invokeNode = (I4GLInvokeNode) visit(ctx.function());
        I4GLAssignResultsNode assignResultsNode = null;
        if (ctx.variableOrComponentList() != null) {
            assignResultsNode = (I4GLAssignResultsNode) visit(ctx.variableOrComponentList());
        }
        I4GLCallNode node = new I4GLCallNode(invokeNode, assignResultsNode);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitFunction(final I4GLParser.FunctionContext ctx) {
        final String functionIdentifier = ctx.identifier().getText();
        List<I4GLExpressionNode> argumentNodeList = new ArrayList<>();
        if(ctx.expressionOrComponentVariableList() != null) {
            argumentNodeList = createExpressionOrComponentVariableList(ctx.expressionOrComponentVariableList());
        }
        final I4GLExpressionNode[] argumentNodes = argumentNodeList.toArray(new I4GLExpressionNode[argumentNodeList.size()]);
        I4GLInvokeNode node = new I4GLInvokeNode(functionIdentifier, argumentNodes);
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitReturnStatement(final I4GLParser.ReturnStatementContext ctx) {
        List<I4GLExpressionNode> resultNodeList = new ArrayList<>();
        if(ctx.expressionOrComponentVariableList() != null) {
            resultNodeList = createExpressionOrComponentVariableList(ctx.expressionOrComponentVariableList());
        }
        final I4GLExpressionNode[] resultNodes = resultNodeList.toArray(new I4GLExpressionNode[resultNodeList.size()]);
        I4GLReturnNode node = new I4GLReturnNode(resultNodes);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitWhileStatement(final I4GLParser.WhileStatementContext ctx) {
        try {
            currentParseScope.increaseLoopDepth();
            I4GLExpressionNode condition = (I4GLExpressionNode) visit(ctx.ifCondition());
            I4GLStatementNode loopBody = (I4GLStatementNode) visit(ctx.codeBlock());
            I4GLWhileNode node = new I4GLWhileNode(condition, loopBody);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            currentParseScope.decreaseLoopDepth();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitForStatement(final I4GLParser.ForStatementContext ctx) {
        try {
            currentParseScope.increaseLoopDepth();
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
            I4GLStatementNode initialAssignment = createAssignToVariableNode(iteratingIdentifier, startValue);
            I4GLExpressionNode readControlVariableNode = createReadVariableNode(iteratingIdentifier);
            I4GLStatementNode stepNode = createAssignToVariableNode(iteratingIdentifier,
                    I4GLAddNodeGen.create(readControlVariableNode, stepValue));
            I4GLForNode node = new I4GLForNode(initialAssignment, controlSlot, startValue, finalValue, stepNode,
                    readControlVariableNode, loopBody);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            currentParseScope.decreaseLoopDepth();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private Map<I4GLReadFromResultNode, I4GLStatementNode> createAssignmentsToComponentVariable(
            final I4GLParser.ComponentVariableContext ctx) {
        if (ctx.starComponentVariable() != null) {
            return createAssignmentsToStarComponentVariable(ctx.starComponentVariable());
        } else if (ctx.thruComponentVariable() != null) {
            throw new NotImplementedException();
        }
        throw new ParseException(source, ctx, "Malformed component variable");
    }

    private Map<I4GLReadFromResultNode, I4GLStatementNode> createAssignmentsToStarComponentVariable(
            final I4GLParser.StarComponentVariableContext ctx) {
        try {
            I4GLExpressionNode variableNode = (I4GLExpressionNode) visit(ctx.simpleVariable());
            int index = 0;
            while (index < ctx.identifier().size()) {
                String identifier = ctx.identifier(index++).getText();
                variableNode = createReadFromRecordNode(variableNode, identifier);
            }
            I4GLType expressionType = variableNode.getType();
            if (expressionType instanceof I4GLRecordType) {
                I4GLRecordType accessedRecordType = (I4GLRecordType) expressionType;
                Map<String, I4GLType> variables = accessedRecordType.getVariables();
                final Map<I4GLReadFromResultNode, I4GLStatementNode> pairs = new LinkedHashMap<>(variables.size());
                for (Map.Entry<String, I4GLType> variable : variables.entrySet()) {
                    final String identifier = variable.getKey();
                    final I4GLType recordType = variable.getValue();
                    final I4GLReadFromResultNode readResultNode = new I4GLReadFromResultNode();
                    final I4GLStatementNode assignResultNode = I4GLAssignToRecordFieldNodeGen.create(identifier,
                            recordType, variableNode, readResultNode);
                    assignResultNode.addStatementTag();
                    setSourceFromContext(assignResultNode, ctx);
                    pairs.put(readResultNode, assignResultNode);
                }
                return pairs;
            } else {
                throw new TypeMismatchException(expressionType.toString(), RECORD_STRING);
            }
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitVariableOrComponentList(final I4GLParser.VariableOrComponentListContext ctx) {
        final int size = ctx.variableOrComponent().size();
        final Map<I4GLReadFromResultNode, I4GLStatementNode> pairs = new LinkedHashMap<>(size);
        for (final I4GLParser.VariableOrComponentContext variableOrComponentCtx : ctx.variableOrComponent()) {
            if (variableOrComponentCtx.componentVariable() != null) {
                pairs.putAll(createAssignmentsToComponentVariable(variableOrComponentCtx.componentVariable()));
            } else { // variableOrComponentCtx.variable() != null
                final I4GLReadFromResultNode readResultNode = new I4GLReadFromResultNode();
                final I4GLStatementNode assignResultNode = createAssignmentNode(variableOrComponentCtx.variable(),
                        readResultNode);
                pairs.put(readResultNode, assignResultNode);
            }
        }
        I4GLAssignResultsNode node = new I4GLAssignResultsNode(pairs);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitForEachStatement(final I4GLParser.ForEachStatementContext ctx) {
        if (ctx.usingVariableList() != null || ctx.WITH() != null) {
            throw new NotImplementedException();
        }
        try {
            final String cursorName = ctx.cursorName().identifier().getText();
            I4GLAssignResultsNode assignResultsNode = null;
            if (ctx.intoVariableList() != null) {
                assignResultsNode = (I4GLAssignResultsNode) visit(ctx.intoVariableList());
            }
            I4GLExpressionNode cursorVariableNode = createReadVariableNode(cursorName);
            I4GLStatementNode loopBody = ctx.codeBlock() == null ? new I4GLBlockNode(null)
                    : (I4GLStatementNode) visit(ctx.codeBlock());
            I4GLForEachNode node = new I4GLForEachNode(cursorVariableNode, assignResultsNode, loopBody);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
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
        if (ctx.booleanConstant() != null) {
            I4GLExpressionNode booleanConstantNode = (I4GLExpressionNode) visit(ctx.booleanConstant());
            setSourceFromContext(booleanConstantNode, ctx);
            booleanConstantNode.addExpressionTag();
            return booleanConstantNode;
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
                setSourceFromContext(leftNode, ctx);
                leftNode.addExpressionTag();
            }
        }
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
                setSourceFromContext(leftNode, ctx);
                leftNode.addExpressionTag();
            }
        }
        return leftNode;
    }

    @Override
    public Node visitFactor(final I4GLParser.FactorContext ctx) {
        I4GLExpressionNode node = (I4GLExpressionNode) visit(ctx.factorTypes());
        if (ctx.CLIPPED() != null) {
            node = I4GLClippedNodeGen.create(node);
            setSourceFromContext(node, ctx);
            node.addExpressionTag();
        }
        if (ctx.UNITS() != null) {
            throw new NotImplementedException();
        }
        if (ctx.USING() != null) {
            throw new NotImplementedException();
        }
        return node;
    }

    private List<I4GLExpressionNode> createExpressionOrComponentVariableList(final I4GLParser.ExpressionOrComponentVariableListContext ctx) {
        List<I4GLExpressionNode> nodeList = new ArrayList<>();
        for(final I4GLParser.ExpressionOrComponentVariableContext exprOrCompCtx: ctx.expressionOrComponentVariable()) {
            if(exprOrCompCtx.expression() != null) {
                nodeList.add((I4GLExpressionNode)visit(exprOrCompCtx.expression()));
            }
            else if (exprOrCompCtx.componentVariable()!=null) {
                nodeList.addAll(createReadComponentVariable(exprOrCompCtx.componentVariable()));
            }
            else {
                throw new ParseException(source, ctx, "Can not be here");
            }
        }
        return nodeList;
    }

    private List<I4GLExpressionNode> createReadComponentVariable(final I4GLParser.ComponentVariableContext ctx) {
        if (ctx.starComponentVariable() != null) {
            return createReadStarComponentVariable(ctx.starComponentVariable());
        } else if (ctx.thruComponentVariable() != null) {
            throw new NotImplementedException();
        }
        throw new ParseException(source, ctx, "Malformed component variable");
    }

    private List<I4GLExpressionNode> createReadStarComponentVariable(
            final I4GLParser.StarComponentVariableContext ctx) {
        try {
            I4GLExpressionNode variableNode = (I4GLExpressionNode) visit(ctx.simpleVariable());
            int index = 0;
            while (index < ctx.identifier().size()) {
                String identifier = ctx.identifier(index++).getText();
                variableNode = createReadFromRecordNode(variableNode, identifier);
            }
            I4GLType expressionType = variableNode.getType();
            if (expressionType instanceof I4GLRecordType) {
                I4GLRecordType accessedRecordType = (I4GLRecordType) expressionType;
                Map<String, I4GLType> variables = accessedRecordType.getVariables();
                final List<I4GLExpressionNode> readNodes = new ArrayList<>(variables.size());
                for (Map.Entry<String, I4GLType> variable : variables.entrySet()) {
                    final String identifier = variable.getKey();
                    final I4GLExpressionNode readNode = createReadFromRecordNode(variableNode, identifier);
                    setSourceFromContext(readNode, ctx);
                    readNodes.add(readNode);
                }
                return readNodes;
            } else {
                throw new TypeMismatchException(expressionType.toString(), RECORD_STRING);
            }
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitDisplayStatement(final I4GLParser.DisplayStatementContext ctx) {
        if(ctx.attributeList() != null) {
            throw new NotImplementedException();
        }
        if(ctx.concatExpression() != null) {
            I4GLExpressionNode displayValueListNode = (I4GLExpressionNode) visit(ctx.concatExpression());
            if(ctx.TO() != null) {
                throw new NotImplementedException();
            }
            if(ctx.AT() != null) {
                throw new NotImplementedException();
            }
            I4GLDisplayNode node = I4GLDisplayNodeGen.create(displayValueListNode);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        }
        else {
            throw new NotImplementedException();
        }
    }

    @Override
    public Node visitVariableDeclaration(final I4GLParser.VariableDeclarationContext ctx) {
        try {
            final I4GLTypeFactory typeFactory = new I4GLTypeFactory(this);
            final I4GLType type = typeFactory.visit(ctx.type());
            final List<I4GLParser.IdentifierContext> identifierCtxList = ctx.identifier();
            for (final I4GLParser.IdentifierContext identifierCtx : identifierCtxList) {
                final String identifier = identifierCtx.getText();
                currentParseScope.registerLocalVariable(identifier, type);
            }
            return null;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitConcatExpression(final I4GLParser.ConcatExpressionContext ctx) {
        List<I4GLExpressionNode> argumentNodeList = new ArrayList<>();
        if(ctx.expressionOrComponentVariableList() != null) {
            argumentNodeList = createExpressionOrComponentVariableList(ctx.expressionOrComponentVariableList());
        }
        I4GLExpressionNode leftNode = null;
        for (final I4GLExpressionNode rightNode : argumentNodeList) {
            if (leftNode == null) {
                leftNode = rightNode;
            } else {
                leftNode = I4GLConcatenationNodeGen.create(leftNode, rightNode);
                setSourceFromContext(leftNode, ctx);
                leftNode.addExpressionTag();
            }
        }
        return leftNode;
    }

    @Override
    public Node visitAssignmentValue(final I4GLParser.AssignmentValueContext ctx) {
        if(ctx.concatExpression() != null) {
            return visit(ctx.concatExpression());
        }
        if (ctx.NULL() != null) {
            throw new NotImplementedException();
        }
        throw new ParseException(source, ctx, "Parse Error"); 
    }

    @Override
    public Node visitAssignmentStatement(final I4GLParser.AssignmentStatementContext ctx) {
        I4GLStatementNode node;
        if (ctx.simpleAssignmentStatement() != null) {
            node = (I4GLStatementNode) visit(ctx.simpleAssignmentStatement());
        } else {
            node = (I4GLStatementNode) visit(ctx.multipleAssignmentStatement());
        }
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
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
            I4GLExpressionNode variableNode = createReadVariableNode(identifier);
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
        final boolean isLocal = scope == currentParseScope;

        if (isLocal) {
            return I4GLReadLocalVariableNodeGen.create(variableSlot, type);
        } else {
            return I4GLReadNonLocalVariableNodeGen.create(scope.getName(), variableSlot, type);
        }
    }

    private I4GLExpressionNode createReadVariableNode(final String identifier) throws LexicalException {
        return doLookup(identifier, (final I4GLParseScope foundInScope,
                final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier, foundInScope));
    }

    private I4GLExpressionNode createReadDatabaseVariableNode() throws LexicalException {
        return createReadVariableNode(I4GLParseScope.DATABASE_IDENTIFIER);
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
    public Node visitAsciiConstant(final I4GLParser.AsciiConstantContext ctx) {
        final String asciiCodeText = ctx.UNSIGNED_INTEGER().getText();
        I4GLExpressionNode node;
        try {
            final String charValue = Character.toString(Integer.parseUnsignedInt(asciiCodeText));
            node = new I4GLTextLiteralNode(charValue.intern());
        } catch(final NumberFormatException e) {
            throw new ParseException(source, ctx, "Invalid ASCII constant");
        }
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;        
    }

    @Override
    public Node visitBooleanConstant(final I4GLParser.BooleanConstantContext ctx) {
        if (ctx.TRUE() != null) {
            return I4GLIntLiteralNodeGen.create(0);
        }
        if (ctx.FALSE() != null) {
            return I4GLIntLiteralNodeGen.create(1);
        }
        throw new ParseException(source, ctx, "Invalid boolean constant");
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
    public Node visitBreakpointStatement(final I4GLParser.BreakpointStatementContext ctx) {
        final I4GLDebuggerNode node = new I4GLDebuggerNode();
        node.addStatementTag();
        setSourceFromContext(node, ctx);
        return node;
    }

    @Override
    public Node visitDatabaseDeclaration(final I4GLParser.DatabaseDeclarationContext ctx) {
        try {
            String alias = ctx.identifier(0).getText();
            currentParseScope.addDatabaseVariable(alias);
            return null;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private I4GLStatementNode createAssignmentToSimpleVariable(final I4GLParser.SimpleVariableContext ctx,
            final I4GLExpressionNode valueNode) {
        try {
            return createAssignToVariableNode(ctx.identifier().getText(), valueNode);
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private I4GLStatementNode createAssignmentToRecordVariable(final I4GLParser.RecordVariableContext ctx,
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

    private I4GLStatementNode createAssignmentToNotIndexedVariable(final I4GLParser.NotIndexedVariableContext ctx,
            final I4GLExpressionNode valueNode) {
        if (ctx.simpleVariable() != null) {
            return createAssignmentToSimpleVariable(ctx.simpleVariable(), valueNode);
        }
        return createAssignmentToRecordVariable(ctx.recordVariable(), valueNode);
    }

    private I4GLStatementNode createAssignmentToIndexedSimpleVariable(final I4GLParser.SimpleVariableContext ctx,
            final I4GLParser.VariableIndexContext variableIndexCtx, final I4GLExpressionNode valueNode) {
        try {
            final String identifier = ctx.identifier().getText();
            final I4GLType targetType = lookupVariableType(identifier);
            final FrameSlot targetSlot = lookupVariableSlot(identifier);
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
                I4GLExpressionNode variableNode = createReadVariableNode(identifier);
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

    private I4GLStatementNode createAssignIndexedRecordVariable(final String identifier,
            final I4GLExpressionNode variableNode, final I4GLParser.VariableIndexContext variableIndexCtx,
            final I4GLExpressionNode valueNode) throws LexicalException {
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

    private I4GLStatementNode createAssignmentToIndexedRecordVariable(final I4GLParser.RecordVariableContext ctx,
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
            I4GLStatementNode node = createAssignIndexedRecordVariable(identifier, variableNode, variableIndexCtx,
                    valueNode);
            node.addStatementTag();
            setSourceFromContext(node, ctx);
            return node;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private I4GLStatementNode createAssignmentToIndexedVariable(final I4GLParser.IndexedVariableContext ctx,
            final I4GLExpressionNode valueNode) {
        final NotIndexedVariableContext notIndexedVariableContext = ctx.notIndexedVariable();
        if (notIndexedVariableContext.simpleVariable() != null) {
            return createAssignmentToIndexedSimpleVariable(notIndexedVariableContext.simpleVariable(),
                    ctx.variableIndex(), valueNode);
        }
        return createAssignmentToIndexedRecordVariable(notIndexedVariableContext.recordVariable(), ctx.variableIndex(),
                valueNode);
    }

    private I4GLStatementNode createAssignmentNode(final I4GLParser.VariableContext ctx,
            final I4GLExpressionNode valueNode) {
        if (ctx.notIndexedVariable() != null) {
            return createAssignmentToNotIndexedVariable(ctx.notIndexedVariable(), valueNode);
        }
        return createAssignmentToIndexedVariable(ctx.indexedVariable(), valueNode);
    }

    @Override
    public Node visitSqlStatement(final I4GLParser.SqlStatementContext ctx) {
        I4GLStatementNode node = (I4GLStatementNode) super.visitSqlStatement(ctx);
        if (node == null) {
            try {
                final I4GLExpressionNode databaseVariableNode = createReadDatabaseVariableNode();
                final int start = ctx.start.getStartIndex();
                final int end = ctx.stop.getStopIndex();
                final String sql = ctx.start.getInputStream().getText(new Interval(start, end));
                node = new I4GLSqlNode(databaseVariableNode, sql);
                setSourceFromContext(node, ctx);
                node.addStatementTag();
            } catch (LexicalException e) {
                throw new ParseException(source, ctx, e.getMessage());
            }
        }
        return node;
    }

    @Override
    public Node visitSqlSelectStatement(final I4GLParser.SqlSelectStatementContext ctx) {
        try {
            int start = ctx.start.getStartIndex();
            int end;
            Interval interval;
            String sql = "";

            I4GLAssignResultsNode assignResultsNode = null;
            if (ctx.intoVariableList() != null) {
                end = ctx.intoVariableList().start.getStartIndex() - 1;
                interval = new Interval(start, end);
                sql = ctx.start.getInputStream().getText(interval);
                start = ctx.intoVariableList().stop.getStopIndex() + 1;
                assignResultsNode = (I4GLAssignResultsNode) visit(ctx.intoVariableList());
            }

            end = ctx.stop.getStopIndex();
            interval = new Interval(start, end);
            sql += ctx.start.getInputStream().getText(interval);
            I4GLExpressionNode databaseVariableNode = createReadDatabaseVariableNode();
            I4GLSelectNode node = new I4GLSelectNode(databaseVariableNode, sql, assignResultsNode);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitDeclareCursorStatement(final I4GLParser.DeclareCursorStatementContext ctx) {
        if (ctx.SCROLL() != null) {
            throw new NotImplementedException();
        }
        try {
            String sql = "";
            if (ctx.sqlSelectStatement() != null) {
                final int start = ctx.sqlSelectStatement().start.getStartIndex();
                final int end = ctx.sqlSelectStatement().stop.getStopIndex();
                final Interval interval = new Interval(start, end);
                sql = ctx.start.getInputStream().getText(interval);
            } else if (ctx.sqlInsertStatement() != null || ctx.statementId() != null) {
                throw new NotImplementedException();
            }
            String cursorName = ctx.cursorName().identifier().getText();
            final FrameSlot cursorSlot = currentParseScope.addVariable(cursorName, I4GLCursorType.SINGLETON);
            I4GLExpressionNode databaseVariableNode = createReadDatabaseVariableNode();
            I4GLStatementNode node = new I4GLCursorNode(databaseVariableNode, sql, cursorSlot);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }
}