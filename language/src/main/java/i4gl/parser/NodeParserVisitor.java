package i4gl.parser;

import java.sql.SQLException;
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
import i4gl.nodes.arithmetic.AddNodeGen;
import i4gl.nodes.arithmetic.DivideIntegerNodeGen;
import i4gl.nodes.arithmetic.DivideNodeGen;
import i4gl.nodes.arithmetic.ModuloNodeGen;
import i4gl.nodes.arithmetic.MultiplyNodeGen;
import i4gl.nodes.arithmetic.SubtractNodeGen;
import i4gl.nodes.call.CallNode;
import i4gl.nodes.call.InvokeNode;
import i4gl.nodes.call.ReturnNode;
import i4gl.nodes.cast.CastToBigIntNodeGen;
import i4gl.nodes.cast.CastToChar1NodeGen;
import i4gl.nodes.cast.CastToCharNodeGen;
import i4gl.nodes.cast.CastToDateNodeGen;
import i4gl.nodes.cast.CastToFloatNodeGen;
import i4gl.nodes.cast.CastToIntNodeGen;
import i4gl.nodes.cast.CastToSmallFloatNodeGen;
import i4gl.nodes.cast.CastToSmallIntNodeGen;
import i4gl.nodes.cast.CastToVarcharNodeGen;
import i4gl.nodes.control.CaseNode;
import i4gl.nodes.control.DebuggerNode;
import i4gl.nodes.control.ForNode;
import i4gl.nodes.control.IfNode;
import i4gl.nodes.control.WhileNode;
import i4gl.nodes.expression.ConcatenationNodeGen;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.literals.BigIntLiteralNodeGen;
import i4gl.nodes.literals.IntLiteralNodeGen;
import i4gl.nodes.literals.NullLiteralNode;
import i4gl.nodes.literals.SmallFloatLiteralNode;
import i4gl.nodes.literals.SmallFloatLiteralNodeGen;
import i4gl.nodes.literals.TextLiteralNode;
import i4gl.nodes.logic.AndNodeGen;
import i4gl.nodes.logic.EqualsNodeGen;
import i4gl.nodes.logic.IsNullNodeGen;
import i4gl.nodes.logic.LessThanNodeGen;
import i4gl.nodes.logic.LessThanOrEqualNodeGen;
import i4gl.nodes.logic.LikeNodeGen;
import i4gl.nodes.logic.MatchesNodeGen;
import i4gl.nodes.logic.NotNodeGen;
import i4gl.nodes.logic.OrNodeGen;
import i4gl.nodes.operators.AsciiNodeGen;
import i4gl.nodes.operators.ClippedNodeGen;
import i4gl.nodes.operators.MdyNodeGen;
import i4gl.nodes.operators.UsingNodeGen;
import i4gl.nodes.root.BaseRootNode;
import i4gl.nodes.root.MainRootNode;
import i4gl.nodes.sql.CursorNode;
import i4gl.nodes.sql.ForEachNode;
import i4gl.nodes.sql.InitializeNode;
import i4gl.nodes.sql.InitializeToNullNode;
import i4gl.nodes.sql.SelectNode;
import i4gl.nodes.sql.SqlNode;
import i4gl.nodes.statement.BlockNode;
import i4gl.nodes.statement.DisplayNode;
import i4gl.nodes.statement.DisplayNodeGen;
import i4gl.nodes.statement.StatementNode;
import i4gl.nodes.variables.read.ReadArgumentNode;
import i4gl.nodes.variables.read.ReadArrayElementNodeGen;
import i4gl.nodes.variables.read.ReadLocalVariableNodeGen;
import i4gl.nodes.variables.read.ReadNonLocalVariableNodeGen;
import i4gl.nodes.variables.read.ReadRecordFieldNode;
import i4gl.nodes.variables.read.ReadRecordFieldNodeGen;
import i4gl.nodes.variables.read.ReadResultsNode;
import i4gl.nodes.variables.write.WriteArrayElementNodeGen;
import i4gl.nodes.variables.write.WriteLocalVariableNodeGen;
import i4gl.nodes.variables.write.WriteNonLocalVariableNodeGen;
import i4gl.nodes.variables.write.WriteRecordFieldNodeGen;
import i4gl.nodes.variables.write.WriteResultsNode;
import i4gl.parser.I4GLParser.NotIndexedVariableContext;
import i4gl.parser.exceptions.LexicalException;
import i4gl.parser.exceptions.ParseException;
import i4gl.parser.exceptions.TypeMismatchException;
import i4gl.parser.exceptions.UnknownIdentifierException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.complex.CursorType;
import i4gl.runtime.types.complex.DatabaseType;
import i4gl.runtime.types.compound.ArrayType;
import i4gl.runtime.types.compound.Char1Type;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.types.compound.RecordField;
import i4gl.runtime.types.compound.RecordType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.types.compound.VarcharType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;
import i4gl.runtime.values.Database;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class NodeParserVisitor extends I4GLParserBaseVisitor<Node> {
    private static final String ARRAY_STRING = "ARRAY";
    private static final String RECORD_STRING = "RECORD";
    private static final String DIMENSIONS_STRING = "Dimensions can not be ";

    private static final TruffleLogger LOGGER = I4GLLanguage.getLogger(NodeParserVisitor.class);

    /* State while parsing a source unit. */
    private Database currentDatabase;
    private final Source source;
    private final I4GLLanguage language;
    private ParseScope currentParseScope;
    private FrameDescriptor globalsFrameDescriptor;
    private FrameDescriptor moduleFrameDescriptor;
    private final Map<String, RootCallTarget> allFunctions;
    private final String moduleName;

    public NodeParserVisitor(final I4GLLanguage language, final Source source) {
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

    public Database getDatabase(final ParserRuleContext ctx) {
        if (currentDatabase == null) {
            try {
                DatabaseType databaseType = (DatabaseType) lookupVariableType(
                        ParseScope.DATABASE_IDENTIFIER);
                currentDatabase = (Database) (databaseType.getDefaultValue());
                currentDatabase.connect();
            } catch (LexicalException e) {
                throw new ParseException(source, ctx, "No Database declared");
            }
        }
        return currentDatabase;
    }

    /**
     * Lambda interface used in
     * {@link NodeParserVisitor#doLookup(String, GlobalObjectLookup, boolean)}
     * function. The function looks up specified identifier and calls
     * {@link GlobalObjectLookup#onFound(ParseScope, String)} function with the
     * found identifier and lexical sccope in which it was found.
     * 
     * @param <T>
     */
    private interface GlobalObjectLookup<T> {
        T onFound(ParseScope foundInScope, String foundIdentifier) throws LexicalException;
    }

    /**
     * Looks up the specified identifier (in current scope and its parent scope up
     * to the topmost scope. If the identifier is found it calls the
     * {@link GlobalObjectLookup#onFound(ParseScope, String)} function with the
     * found identifier and lexical scope in which it was found as arguments. It
     * firstly looks ups to the topmost lexical scope and if the identifier is not
     * found then it looks up in the unit scopes.
     * 
     * @param identifier     identifier to be looked up
     * @param lookupFunction the function to be called when the identifier is found
     * @param <T>            type of the returned object
     * @return the value return from the
     *         {@link GlobalObjectLookup#onFound(ParseScope, String)} function
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
     * {@link NodeParserVisitor#doLookup(String, GlobalObjectLookup, boolean)}. Does
     * the looking up to the topmost lexical scope.
     * 
     * @param scope          scope to begin the lookup in
     * @param identifier     the identifier to lookup
     * @param lookupFunction function that is called when the identifier is found
     * @param <T>            type of the returned object
     * @return the value return from the
     *         {@link GlobalObjectLookup#onFound(ParseScope, String)} function
     * @throws LexicalException
     */
    private <T> T lookupToParentScope(ParseScope scope, final String identifier,
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

    private BaseType lookupVariableType(final String identifier) throws LexicalException {
        return doLookup(identifier, ParseScope::getIdentifierType);
    }

    // private FrameSlot lookupVariableSlot(final String identifier) throws
    // LexicalException {
    // return doLookup(identifier, ParseScope::getLocalSlot);
    // }

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

    public ParseScope pushNewScope(final String type, final String name) {
        final String currentScopeName = currentParseScope == null ? "<unset>" : currentParseScope.toString();
        currentParseScope = new ParseScope(currentParseScope, type, name);
        trace(MessageFormat.format("Push scope {0}->{1}", currentScopeName, currentParseScope));
        return currentParseScope;
    }

    public ParseScope popScope() {
        final ParseScope outerScope = currentParseScope.getOuterScope();
        final String outerScopeName = outerScope == null ? "<unset>" : outerScope.toString();
        trace(MessageFormat.format("Pop scope {0}<-{1}", outerScopeName, currentParseScope));
        currentParseScope = outerScope;
        return currentParseScope;
    }

    private void setSourceFromContext(StatementNode node, ParserRuleContext ctx) {
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
        globalsFrameDescriptor = pushNewScope(ParseScope.GLOBAL_TYPE, null).getFrameDescriptor();
        createSqlcaGlobalVariable(ctx);
        if (ctx.databaseDeclaration() != null) {
            visit(ctx.databaseDeclaration());
        }
        if (ctx.globalsDeclaration() != null) {
            visit(ctx.globalsDeclaration());
        }
        moduleFrameDescriptor = pushNewScope(ParseScope.MODULE_TYPE, moduleName).getFrameDescriptor();
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

    private void createSqlcaGlobalVariable(final I4GLParser.ModuleContext ctx) {
        try {
            currentParseScope.addSqlcaVariable();
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
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
            pushNewScope(ParseScope.FUNCTION_TYPE, functionIdentifier);
            Interval sourceInterval = srcFromContext(ctx);
            final SourceSection sourceSection = source.createSection(sourceInterval.a, sourceInterval.length());
            BlockNode bodyNode = createFunctionBody(sourceSection, null, ctx.typeDeclarations(),
                    ctx.mainStatements());
            MainRootNode rootNode = new MainRootNode(language, currentParseScope.getFrameDescriptor(), bodyNode,
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
        final List<StatementNode> statementNodes = new ArrayList<>(ctx.children.size());
        for (final ParseTree child : ctx.children) {
            if (!(child instanceof TerminalNodeImpl)) {
                StatementNode statementNode = (StatementNode) visit(child);
                if (statementNode != null) {
                    statementNodes.add(statementNode);
                } else if (!(child instanceof I4GLParser.DatabaseDeclarationContext)) {
                    throw new ParseException(source, ctx, "Visited an unimplemented Node in MAIN");
                }

            }
        }
        BlockNode node = new BlockNode(statementNodes.toArray(new StatementNode[0]));
        setSourceFromContext(node, ctx);
        return node;
    }

    @Override
    public Node visitFunctionDefinition(final I4GLParser.FunctionDefinitionContext ctx) {
        try {
            final String functionIdentifier = ctx.identifier().getText();
            List<String> parameterIdentifiers = null;
            if (ctx.parameterList() != null) {
                var parameterList = ctx.parameterList();
                if (parameterList.parameterGroup() != null) {
                    var parameterGroup = parameterList.parameterGroup();
                    parameterIdentifiers = new ArrayList<>(parameterGroup.identifier().size());
                    for (final I4GLParser.IdentifierContext identifierCtx : parameterGroup.identifier()) {
                        parameterIdentifiers.add(identifierCtx.getText());
                    }
                }
            }
            Interval sourceInterval = srcFromContext(ctx);
            final SourceSection sourceSection = source.createSection(sourceInterval.a, sourceInterval.length());
            pushNewScope(ParseScope.FUNCTION_TYPE, functionIdentifier);
            BlockNode bodyNode = createFunctionBody(sourceSection, parameterIdentifiers, ctx.typeDeclarations(),
                    ctx.codeBlock());
            final BaseRootNode rootNode = new BaseRootNode(language, currentParseScope.getFrameDescriptor(), bodyNode,
                    sourceSection, functionIdentifier);
            allFunctions.put(functionIdentifier, Truffle.getRuntime().createCallTarget(rootNode));
            popScope();
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
            visit(typeDeclarationsContext);
        }

        if (parameteridentifiers != null) {
            int argumentIndex = 0;
            for (final String parameteridentifier : parameteridentifiers) {
                final BaseType type = currentParseScope.getIdentifierType(parameteridentifier);
                if (type == null) {
                    throw new LexicalException("Function parameter " + parameteridentifier + " must be declared");
                }
                final ExpressionNode readNode = new ReadArgumentNode(argumentIndex++);
                blockNodes.add(createAssignToVariable(parameteridentifier, readNode));
            }
        }

        if (codeBlockContext != null) {
            BlockNode blockNode = (BlockNode) visit(codeBlockContext);
            if (blockNode != null) {
                blockNodes.addAll(blockNode.getStatements());
            }
        }

        final BlockNode bodyNode = new BlockNode(blockNodes.toArray(new StatementNode[0]));
        bodyNode.setSourceSection(sourceSection.getCharIndex(), sourceSection.getCharLength());
        bodyNode.addRootTag();
        return bodyNode;
    }

    private StatementNode createAssignToVariable(final String identifier, final ExpressionNode valueNode)
            throws LexicalException {
        return doLookup(identifier,
                (final ParseScope foundInScope, final String foundIdentifier) -> createAssignToVariableFromScope(
                        foundInScope, foundIdentifier, valueNode));
    }

    private StatementNode createAssignToVariableFromScope(final ParseScope scope, final String identifier,
            final ExpressionNode valueNode) throws TypeMismatchException {
        final FrameSlot variableSlot = scope.getLocalSlot(identifier);
        final BaseType targetType = scope.getIdentifierType(identifier);
        final boolean isLocal = scope == currentParseScope;

        final ExpressionNode castNode = createCastNode(valueNode, targetType);
        if (isLocal) {
            return WriteLocalVariableNodeGen.create(castNode, variableSlot, targetType);
        }
        return WriteNonLocalVariableNodeGen.create(castNode, scope.getName(), variableSlot, targetType);
    }

    @Override
    public Node visitCodeBlock(final I4GLParser.CodeBlockContext ctx) {
        final List<StatementNode> statementNodes = new ArrayList<>(ctx.children.size());
        for (final ParseTree child : ctx.children) {
            StatementNode statementNode = (StatementNode) visit(child);
            if (statementNode != null) {
                statementNodes.add(statementNode);
            } else if (!(child instanceof I4GLParser.DatabaseDeclarationContext)) {
                throw new ParseException(source, ctx, "Visited an unimplemented Node in FUNCTION");
            }
        }
        BlockNode node = new BlockNode(statementNodes.toArray(new StatementNode[0]));
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitCallStatement(final I4GLParser.CallStatementContext ctx) {
        final InvokeNode invokeNode = (InvokeNode) visit(ctx.function());
        WriteResultsNode assignResultsNode = null;
        if (ctx.variableOrComponentList() != null) {
            assignResultsNode = (WriteResultsNode) visit(ctx.variableOrComponentList());
        }
        CallNode node = new CallNode(invokeNode, assignResultsNode);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitFunction(final I4GLParser.FunctionContext ctx) {
        final String functionIdentifier = ctx.identifier().getText();
        List<ExpressionNode> argumentNodeList = new ArrayList<>();
        if (ctx.expressionOrComponentVariableList() != null) {
            argumentNodeList = createExpressionOrComponentVariableList(ctx.expressionOrComponentVariableList());
        }
        final ExpressionNode[] argumentNodes = argumentNodeList
                .toArray(new ExpressionNode[0]);
        InvokeNode node = new InvokeNode(functionIdentifier, argumentNodes);
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitReturnStatement(final I4GLParser.ReturnStatementContext ctx) {
        List<ExpressionNode> resultNodeList = new ArrayList<>();
        if (ctx.expressionOrComponentVariableList() != null) {
            resultNodeList = createExpressionOrComponentVariableList(ctx.expressionOrComponentVariableList());
        }
        final ExpressionNode[] resultNodes = resultNodeList.toArray(new ExpressionNode[0]);
        ReturnNode node = new ReturnNode(resultNodes);
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitWhileStatement(final I4GLParser.WhileStatementContext ctx) {
        try {
            currentParseScope.increaseLoopDepth();
            ExpressionNode condition = (ExpressionNode) visit(ctx.ifCondition());
            StatementNode loopBody = (StatementNode) visit(ctx.codeBlock());
            WhileNode node = new WhileNode(condition, loopBody);
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
            ExpressionNode startValue = (ExpressionNode) visit(ctx.initialValue());
            ExpressionNode finalValue = (ExpressionNode) visit(ctx.finalValue());
            ExpressionNode stepValue = null;
            if (ctx.numericConstant() != null) {
                stepValue = (ExpressionNode) visit(ctx.numericConstant());
            } else {
                stepValue = IntLiteralNodeGen.create(1);
            }

            StatementNode loopBody = ctx.codeBlock() == null ? new BlockNode(null)
                    : (StatementNode) visit(ctx.codeBlock());
            if (startValue.getReturnType() != finalValue.getReturnType()
                    && !startValue.getReturnType().convertibleTo(finalValue.getReturnType())) {
                throw new ParseException(source, ctx, "Type mismatch in beginning and last value of for loop.");
            }
            StatementNode initialAssignment = createAssignToVariable(iteratingIdentifier, startValue);
            ExpressionNode readControlVariableNode = createReadVariableNode(iteratingIdentifier);
            StatementNode stepNode = createAssignToVariable(iteratingIdentifier,
                    AddNodeGen.create(readControlVariableNode, stepValue));
            ForNode node = new ForNode(initialAssignment, startValue, finalValue, stepNode, readControlVariableNode,
                    loopBody);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            currentParseScope.decreaseLoopDepth();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private Map<ReadResultsNode, StatementNode> createAssignmentsToComponentVariable(
            final I4GLParser.ComponentVariableContext ctx) {
        if (ctx.starComponentVariable() != null) {
            return createAssignmentsToStarComponentVariable(ctx.starComponentVariable());
        } else if (ctx.thruComponentVariable() != null) {
            throw new NotImplementedException();
        }
        throw new ParseException(source, ctx, "Malformed component variable");
    }

    private Map<ReadResultsNode, StatementNode> createAssignmentsToStarComponentVariable(
            final I4GLParser.StarComponentVariableContext ctx) {
        try {
            ExpressionNode variableNode = (ExpressionNode) visit(ctx.simpleVariable());
            int index = 0;
            while (index < ctx.identifier().size()) {
                String identifier = ctx.identifier(index++).getText();
                variableNode = createRecordFieldNode(variableNode, identifier);
            }
            BaseType expressionType = variableNode.getReturnType();
            if (expressionType instanceof RecordType) {
                RecordType recordType = (RecordType) expressionType;
                final Map<ReadResultsNode, StatementNode> pairs = new LinkedHashMap<>();
                for (RecordField field : recordType.getFields()) {
                    final String identifier = field.getId();
                    final BaseType fieldType = field.getType();
                    final ReadResultsNode readResultNode = new ReadResultsNode();
                    final ExpressionNode castNode = createCastNode(readResultNode, fieldType);
                    final StatementNode assignResultNode = WriteRecordFieldNodeGen.create(variableNode, castNode,
                            identifier, fieldType);
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
        final Map<ReadResultsNode, StatementNode> pairs = new LinkedHashMap<>(size);
        for (final I4GLParser.VariableOrComponentContext variableOrComponentCtx : ctx.variableOrComponent()) {
            if (variableOrComponentCtx.componentVariable() != null) {
                pairs.putAll(createAssignmentsToComponentVariable(variableOrComponentCtx.componentVariable()));
            } else { // variableOrComponentCtx.variable() != null
                final ReadResultsNode readResultNode = new ReadResultsNode();
                final StatementNode assignResultNode = createAssignmentNode(variableOrComponentCtx.variable(),
                        readResultNode);
                pairs.put(readResultNode, assignResultNode);
            }
        }
        WriteResultsNode node = new WriteResultsNode(pairs);
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
            WriteResultsNode assignResultsNode = null;
            if (ctx.intoVariableList() != null) {
                assignResultsNode = (WriteResultsNode) visit(ctx.intoVariableList());
            }
            ExpressionNode cursorVariableNode = createReadVariableNode(cursorName);
            StatementNode loopBody = ctx.codeBlock() == null ? new BlockNode(null)
                    : (StatementNode) visit(ctx.codeBlock());
            ForEachNode node = new ForEachNode(cursorVariableNode, assignResultsNode, loopBody);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
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
        List<ExpressionNode> indexNodes = new ArrayList<>();
        List<StatementNode> statementNodes = new ArrayList<>();
        for (int whenIndex = 0; whenIndex < whenCount; whenIndex++) {
            indexNodes.add((ExpressionNode) visit(ctx.expression(expressionIndex++)));
            statementNodes.add((StatementNode) visit(ctx.codeBlock(codeBlockIndex++)));
        }
        ExpressionNode[] indexNodesArray = indexNodes.toArray(new ExpressionNode[0]);
        StatementNode[] statementNodesArray = statementNodes.toArray(new StatementNode[0]);
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
        List<ExpressionNode> conditionNodes = new ArrayList<>();
        List<StatementNode> statementNodes = new ArrayList<>();
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
        if (ctx.booleanConstant() != null) {
            ExpressionNode booleanConstantNode = (ExpressionNode) visit(ctx.booleanConstant());
            setSourceFromContext(booleanConstantNode, ctx);
            booleanConstantNode.addExpressionTag();
            return booleanConstantNode;
        }
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
        ExpressionNode node = null;
        if (ctx.expression() != null && ctx.IS() != null && ctx.NULL() != null) {
            node = (ExpressionNode) visit(ctx.expression());
            node = IsNullNodeGen.create(node);
            if (ctx.NOT() != null) {
                node = NotNodeGen.create(node);
            }
        } else if (ctx.ifCondition() != null) {
            node = (ExpressionNode) visit(ctx.ifCondition());
            if (ctx.NOT() != null) {
                node = NotNodeGen.create(node);
            }
        } else if (ctx.ifLogicalRelation() != null) {
            node = (ExpressionNode) visit(ctx.ifLogicalRelation());
        }
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitIfLogicalRelation(final I4GLParser.IfLogicalRelationContext ctx) {
        ExpressionNode leftNode = (ExpressionNode) visit(ctx.expression(0));
        final I4GLParser.RelationalOperatorContext operatorCtx = ctx.relationalOperator();
        if (operatorCtx != null) {
            ExpressionNode rightNode = (ExpressionNode) visit(ctx.expression(1));
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
                setSourceFromContext(leftNode, ctx);
                leftNode.addExpressionTag();
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
                setSourceFromContext(leftNode, ctx);
                leftNode.addExpressionTag();
            }
        }
        return leftNode;
    }

    @Override
    public Node visitFactor(final I4GLParser.FactorContext ctx) {
        ExpressionNode node = (ExpressionNode) visit(ctx.factorTypes());
        if (ctx.CLIPPED() != null) {
            node = ClippedNodeGen.create(node);
            setSourceFromContext(node, ctx);
            node.addExpressionTag();
        }
        if (ctx.UNITS() != null) {
            throw new NotImplementedException();
        }
        if (ctx.USING() != null) {
            ExpressionNode format = (ExpressionNode) visit(ctx.string());
            node = UsingNodeGen.create(node, format);
            setSourceFromContext(node, ctx);
            node.addExpressionTag();
        }
        return node;
    }

    @Override
    public Node visitFactorTypes(final I4GLParser.FactorTypesContext ctx) {
        if (ctx.constant() != null) {
            return visit(ctx.constant());
        }
        if (ctx.variable() != null) {
            return visit(ctx.variable());
        }
        ExpressionNode node = null;
        if (ctx.function() != null) {
            node = (ExpressionNode) visit(ctx.function());
            ;
            if (ctx.GROUP() != null) {
                throw new NotImplementedException();
            }
        } else if (ctx.expression() != null) {
            node = (ExpressionNode) visit(ctx.expression());
            ;
        } else if (ctx.NOT() != null && ctx.factor() != null) {
            node = (ExpressionNode) visit(ctx.factor());
            ;
            node = NotNodeGen.create(node);
        }
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    private List<ExpressionNode> createExpressionOrComponentVariableList(
            final I4GLParser.ExpressionOrComponentVariableListContext ctx) {
        List<ExpressionNode> nodeList = new ArrayList<>();
        for (final I4GLParser.ExpressionOrComponentVariableContext exprOrCompCtx : ctx
                .expressionOrComponentVariable()) {
            if (exprOrCompCtx.expression() != null) {
                nodeList.add((ExpressionNode) visit(exprOrCompCtx.expression()));
            } else if (exprOrCompCtx.componentVariable() != null) {
                nodeList.addAll(createReadComponentVariable(exprOrCompCtx.componentVariable()));
            } else {
                throw new ParseException(source, ctx, "Can not be here");
            }
        }
        return nodeList;
    }

    private List<ExpressionNode> createReadComponentVariable(final I4GLParser.ComponentVariableContext ctx) {
        if (ctx.starComponentVariable() != null) {
            return createReadStarComponentVariable(ctx.starComponentVariable());
        } else if (ctx.thruComponentVariable() != null) {
            throw new NotImplementedException();
        }
        throw new ParseException(source, ctx, "Malformed component variable");
    }

    private List<ExpressionNode> createReadStarComponentVariable(
            final I4GLParser.StarComponentVariableContext ctx) {
        try {
            ExpressionNode variableNode = (ExpressionNode) visit(ctx.simpleVariable());
            int index = 0;
            while (index < ctx.identifier().size()) {
                String identifier = ctx.identifier(index++).getText();
                variableNode = createRecordFieldNode(variableNode, identifier);
            }
            BaseType expressionType = variableNode.getReturnType();
            if (expressionType instanceof RecordType) {
                RecordType recordType = (RecordType) expressionType;
                final List<ExpressionNode> readNodes = new ArrayList<>();
                for (RecordField field : recordType.getFields()) {
                    final String identifier = field.getId();
                    final ExpressionNode readNode = createRecordFieldNode(variableNode, identifier);
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
        if (ctx.attributeList() != null) {
            throw new NotImplementedException();
        }
        if (ctx.concatExpression() != null) {
            ExpressionNode displayValueListNode = (ExpressionNode) visit(ctx.concatExpression());
            if (ctx.TO() != null) {
                throw new NotImplementedException();
            }
            if (ctx.AT() != null) {
                throw new NotImplementedException();
            }
            DisplayNode node = DisplayNodeGen.create(displayValueListNode);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } else {
            throw new NotImplementedException();
        }
    }

    @Override
    public Node visitVariableDeclaration(final I4GLParser.VariableDeclarationContext ctx) {
        try {
            final TypeParserVisitor typeFactory = new TypeParserVisitor(this);
            final BaseType type = typeFactory.visit(ctx.type());
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
        List<ExpressionNode> argumentNodeList = new ArrayList<>();
        if (ctx.expressionOrComponentVariableList() != null) {
            argumentNodeList = createExpressionOrComponentVariableList(ctx.expressionOrComponentVariableList());
        }
        ExpressionNode leftNode = null;
        for (final ExpressionNode rightNode : argumentNodeList) {
            if (leftNode == null) {
                leftNode = rightNode;
            } else {
                leftNode = ConcatenationNodeGen.create(leftNode, rightNode);
                setSourceFromContext(leftNode, ctx);
                leftNode.addExpressionTag();
            }
        }
        return leftNode;
    }

    @Override
    public Node visitAssignmentValue(final I4GLParser.AssignmentValueContext ctx) {
        if (ctx.concatExpression() != null) {
            return visit(ctx.concatExpression());
        }
        if (ctx.function() != null) {
            return visit(ctx.function());
        }
        if (ctx.NULL() != null) {
            NullLiteralNode node = new NullLiteralNode();
            setSourceFromContext(node, ctx);
            node.addExpressionTag();
            return node;
        }
        throw new ParseException(source, ctx, "Parse error");
    }

    @Override
    public Node visitAssignmentStatement(final I4GLParser.AssignmentStatementContext ctx) {
        StatementNode node;
        if (ctx.simpleAssignmentStatement() != null) {
            node = (StatementNode) visit(ctx.simpleAssignmentStatement());
        } else {
            node = (StatementNode) visit(ctx.multipleAssignmentStatement());
        }
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
    }

    @Override
    public Node visitOtherStorageStatement(I4GLParser.OtherStorageStatementContext ctx) {
        if (ctx.initializeStatement() != null) {
            return visit(ctx.initializeStatement());
        }
        throw new NotImplementedException("visitOtherI4GLStatement");
    }

    @Override
    public Node visitSimpleAssignmentStatement(final I4GLParser.SimpleAssignmentStatementContext ctx) {
        ExpressionNode valueNode = (ExpressionNode) visit(ctx.assignmentValue());
        return createAssignmentNode(ctx.variable(), valueNode);
    }

    @Override
    public Node visitMultipleAssignmentStatement(final I4GLParser.MultipleAssignmentStatementContext ctx) {
        throw new NotImplementedException();
    }

    private StatementNode createAssignmentToArray(ExpressionNode arrayExpression,
            List<ExpressionNode> indexNodes, final ExpressionNode valueNode) throws LexicalException {
        ExpressionNode readIndexedNode = arrayExpression;
        StatementNode result = null;
        final int lastIndex = indexNodes.size() - 1;
        for (int index = 0; index < indexNodes.size(); index++) {
            assert readIndexedNode != null;
            BaseType arrayType = readIndexedNode.getReturnType();
            /*
             * if (!(actualType instanceof ArrayType)) {
             * throw new TypeMismatchException(arrayType.toString(), ARRAY_STRING);
             * }
             */
            ExpressionNode indexNode = indexNodes.get(index);
            BaseType elementType = ((ArrayType) arrayType).getElementsType();
            if (index == lastIndex) {
                final ExpressionNode castNode = createCastNode(valueNode, elementType);
                result = WriteArrayElementNodeGen.create(readIndexedNode, indexNode, castNode, elementType);
            } else {
                readIndexedNode = ReadArrayElementNodeGen.create(readIndexedNode, indexNode, elementType);
            }
        }
        assert result != null;
        return result;
    }

    @Override
    public Node visitSimpleVariable(final I4GLParser.SimpleVariableContext ctx) {
        try {
            final String identifier = ctx.identifier().getText();
            ExpressionNode variableNode = createReadVariableNode(identifier);
            variableNode.addExpressionTag();
            return variableNode;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    @Override
    public Node visitRecordVariable(final I4GLParser.RecordVariableContext ctx) {
        try {
            ExpressionNode variableNode = (ExpressionNode) visit(ctx.simpleVariable());
            for (int index = 0; index < ctx.identifier().size(); index++) {
                final String identifier = ctx.identifier(index).getText();
                variableNode = createRecordFieldNode(variableNode, identifier);
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
            ExpressionNode variableNode = (ExpressionNode) visit(ctx.notIndexedVariable());
            final List<I4GLParser.ExpressionContext> indexList = ctx.variableIndex().expressionList().expression();
            if (indexList.size() > 3) {
                throw new ParseException(source, ctx, DIMENSIONS_STRING + indexList.size());
            }
            List<ExpressionNode> indexNodes = new ArrayList<>(indexList.size());
            for (I4GLParser.ExpressionContext indexCtx : indexList) {
                indexNodes.add((ExpressionNode) visit(indexCtx));
            }
            variableNode = createReadArrayElementNode(variableNode, indexNodes);
            for (int index = 0; index < ctx.identifier().size(); index++) {
                final String identifier = ctx.identifier(index).getText();
                variableNode = createRecordFieldNode(variableNode, identifier);
            }
            setSourceFromContext(variableNode, ctx);
            variableNode.addExpressionTag();
            return variableNode;
        } catch (LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private ReadRecordFieldNode createRecordFieldNode(final ExpressionNode recordExpression,
            final String identifier) throws LexicalException {
        BaseType fieldType = recordExpression.getReturnType();
        BaseType returnType = null;

        if (!(fieldType instanceof RecordType)) {
            throw new TypeMismatchException(fieldType.toString(), RECORD_STRING);
        }
        RecordType recordType = (RecordType) fieldType;
        if (!recordType.containsIdentifier(identifier)) {
            throw new UnknownIdentifierException("The record does not contain this identifier");
        }

        returnType = recordType.getFieldType(identifier);
        return ReadRecordFieldNodeGen.create(recordExpression, identifier, returnType);
    }

    private ExpressionNode createReadArrayElementNode(ExpressionNode arrayExpression,
            List<ExpressionNode> indexNodes) throws LexicalException {
        ExpressionNode readIndexedNode = arrayExpression;
        for (ExpressionNode indexNode : indexNodes) {
            BaseType actualType = readIndexedNode.getReturnType();
            if (actualType instanceof ArrayType) {
                actualType = ((ArrayType) actualType).getElementsType();
            } else if (!(actualType instanceof TextType)) {
                throw new TypeMismatchException(actualType.toString(), ARRAY_STRING);
            }
            readIndexedNode = ReadArrayElementNodeGen.create(readIndexedNode, indexNode, actualType);
        }
        return readIndexedNode;
    }

    private ExpressionNode createReadVariableFromScope(final String identifier, final ParseScope scope) {
        final FrameSlot variableSlot = scope.getLocalSlot(identifier);
        final BaseType type = scope.getIdentifierType(identifier);
        final boolean isLocal = scope == currentParseScope;

        if (isLocal) {
            return ReadLocalVariableNodeGen.create(variableSlot, type);
        } else {
            return ReadNonLocalVariableNodeGen.create(scope.getName(), variableSlot, type);
        }
    }

    private ExpressionNode createReadVariableNode(final String identifier) throws LexicalException {
        return doLookup(identifier, (final ParseScope foundInScope,
                final String foundIdentifier) -> createReadVariableFromScope(foundIdentifier, foundInScope));
    }

    private ExpressionNode createReadDatabaseVariableNode() throws LexicalException {
        return createReadVariableNode(ParseScope.DATABASE_IDENTIFIER);
    }

    @Override
    public Node visitString(final I4GLParser.StringContext ctx) {
        String literal = ctx.getText();

        // Remove the trailing and ending "
        assert literal.length() >= 2 && ((literal.startsWith("\"") && literal.endsWith("\""))
                || (literal.startsWith("'") && literal.endsWith("'")));
        literal = literal.substring(1, literal.length() - 1);

        TextLiteralNode node = new TextLiteralNode(literal.intern());
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitAsciiConstant(final I4GLParser.AsciiConstantContext ctx) {
        if (ctx.expression() != null) {
            ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expression());
            ExpressionNode node = AsciiNodeGen.create(expressionNode);
            setSourceFromContext(node, ctx);
            node.addExpressionTag();
            return node;
        }
        final String asciiCodeText = ctx.UNSIGNED_INTEGER().getText();
        ExpressionNode node;
        try {
            final String charValue = Character.toString(Integer.parseUnsignedInt(asciiCodeText));
            node = new TextLiteralNode(charValue.intern());
        } catch (final NumberFormatException e) {
            throw new ParseException(source, ctx, "Invalid ASCII constant");
        }
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitBooleanConstant(final I4GLParser.BooleanConstantContext ctx) {
        if (ctx.TRUE() != null) {
            return IntLiteralNodeGen.create(0);
        }
        if (ctx.FALSE() != null) {
            return IntLiteralNodeGen.create(1);
        }
        throw new ParseException(source, ctx, "Invalid boolean constant");
    }

    @Override
    public Node visitDateConstant(final I4GLParser.DateConstantContext ctx) {
        ExpressionNode monthExpressionNode = (ExpressionNode) visit(ctx.expression(0));
        ExpressionNode dayExpressionNode = (ExpressionNode) visit(ctx.expression(1));
        ExpressionNode yearExpressionNode = (ExpressionNode) visit(ctx.expression(2));
        ExpressionNode node = MdyNodeGen.create(monthExpressionNode, dayExpressionNode, yearExpressionNode);
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitInteger(final I4GLParser.IntegerContext ctx) {
        final String literal = ctx.getText();
        ExpressionNode node;
        // try {
        // node = SmallIntLiteralNodeGen.create(Short.parseShort(literal));
        // } catch (final NumberFormatException e1) {
        try {
            node = IntLiteralNodeGen.create(Integer.parseInt(literal));
        } catch (final NumberFormatException e2) {
            node = BigIntLiteralNodeGen.create(Long.parseLong(literal));
        }
        // }
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitReal(final I4GLParser.RealContext ctx) {
        final String literal = ctx.getText();
        final SmallFloatLiteralNode node = SmallFloatLiteralNodeGen.create(Float.parseFloat(literal));
        setSourceFromContext(node, ctx);
        node.addExpressionTag();
        return node;
    }

    @Override
    public Node visitBreakpointStatement(final I4GLParser.BreakpointStatementContext ctx) {
        final DebuggerNode node = new DebuggerNode();
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

    private StatementNode createAssignmentToSimpleVariable(final I4GLParser.SimpleVariableContext variableCtx,
            final ExpressionNode valueNode) {
        try {
            return createAssignToVariable(variableCtx.identifier().getText(), valueNode);
        } catch (final LexicalException e) {
            throw new ParseException(source, variableCtx, e.getMessage());
        }
    }

    private StatementNode createAssignmentToRecordVariable(final I4GLParser.RecordVariableContext variableCtx,
            final ExpressionNode valueNode) {
        try {
            ExpressionNode variableNode = (ExpressionNode) visit(variableCtx.simpleVariable());
            int index = 0;
            while (index < variableCtx.identifier().size() - 1) {
                String identifier = variableCtx.identifier(index++).getText();
                variableNode = createRecordFieldNode(variableNode, identifier);
            }
            BaseType expressionType = variableNode.getReturnType();

            if (!(expressionType instanceof RecordType)) {
                throw new TypeMismatchException(expressionType.toString(), RECORD_STRING);
            }
            String identifier = variableCtx.identifier(index).getText();
            RecordType recordType = (RecordType) expressionType;
            BaseType fieldType = recordType.getFieldType(identifier);
            ExpressionNode castNode = createCastNode(valueNode, fieldType);
            StatementNode node = WriteRecordFieldNodeGen.create(variableNode, castNode, identifier, fieldType);
            node.addStatementTag();
            setSourceFromContext(node, variableCtx);
            return node;
        } catch (LexicalException e) {
            throw new ParseException(source, variableCtx, e.getMessage());
        }
    }

    private ExpressionNode createCastNode(final ExpressionNode valueNode, final BaseType targetType)
            throws TypeMismatchException {
        final BaseType sourceType = valueNode.getReturnType();

        if (sourceType == targetType) {
            return valueNode;
        }

        if (sourceType != null && (!sourceType.convertibleTo(targetType))) {
            throw new TypeMismatchException(targetType.toString(), sourceType.toString());
        }

        if (targetType == SmallIntType.SINGLETON) {
            return CastToSmallIntNodeGen.create(valueNode);
        }
        if (targetType == IntType.SINGLETON) {
            return CastToIntNodeGen.create(valueNode);
        }
        if (targetType == BigIntType.SINGLETON) {
            return CastToBigIntNodeGen.create(valueNode);
        }
        if (targetType == SmallFloatType.SINGLETON) {
            return CastToSmallFloatNodeGen.create(valueNode);
        }
        if (targetType == FloatType.SINGLETON) {
            return CastToFloatNodeGen.create(valueNode);
        }
        if (targetType == DateType.SINGLETON) {
            return CastToDateNodeGen.create(valueNode);
        }
        if (targetType == Char1Type.SINGLETON) {
            return CastToChar1NodeGen.create(valueNode);
        }
        if (targetType instanceof CharType) {
            return CastToCharNodeGen.create(valueNode, targetType);
        }
        if (targetType instanceof VarcharType) {
            return CastToVarcharNodeGen.create(valueNode, targetType);
        }
        throw new TypeMismatchException(targetType.toString(), sourceType.toString());
    }

    private StatementNode createAssignmentToNotIndexedVariable(final I4GLParser.NotIndexedVariableContext variableCtx,
            final ExpressionNode valueNode) {
        if (variableCtx.simpleVariable() != null) {
            return createAssignmentToSimpleVariable(variableCtx.simpleVariable(), valueNode);
        }
        return createAssignmentToRecordVariable(variableCtx.recordVariable(), valueNode);
    }

    private StatementNode createAssignmentToIndexedSimpleVariable(final I4GLParser.SimpleVariableContext variableCtx,
            final I4GLParser.VariableIndexContext variableIndexCtx, final ExpressionNode valueNode) {
        try {
            final String identifier = variableCtx.identifier().getText();
            final BaseType targetType = lookupVariableType(identifier);
            // final FrameSlot targetSlot = lookupVariableSlot(identifier);
            final List<I4GLParser.ExpressionContext> indexList = variableIndexCtx.expressionList().expression();
            List<ExpressionNode> indexNodes = new ArrayList<>(indexList.size());
            for (I4GLParser.ExpressionContext indexCtx : indexList) {
                indexNodes.add((ExpressionNode) visit(indexCtx));
            }
            if (targetType instanceof ArrayType) {
                if (indexNodes.size() > 3) {
                    throw new ParseException(source, variableCtx, DIMENSIONS_STRING + indexList.size());
                }
            } else if (targetType instanceof TextType) {
                if (indexNodes.size() != 1) {
                    throw new ParseException(source, variableCtx, DIMENSIONS_STRING + indexList.size());
                }
            } else {
                throw new ParseException(source, variableCtx,
                        "Variable " + identifier + " must be indexed (string or array) to use []");
            }
            ExpressionNode variableNode = createReadVariableNode(identifier);
            StatementNode node = createAssignmentToArray(variableNode, indexNodes, valueNode);
            setSourceFromContext(node, variableCtx);
            node.addStatementTag();
            return node;
        } catch (LexicalException e) {
            throw new ParseException(source, variableCtx, e.getMessage());
        }
    }

    private StatementNode createAssignmentToIndexedSimpleVariableWithRecord(
            final I4GLParser.IndexedVariableContext variableCtx, final ExpressionNode valueNode) {
        try {
            ExpressionNode variableNode = (ExpressionNode) visit(variableCtx.notIndexedVariable());
            final List<I4GLParser.ExpressionContext> indexList = variableCtx.variableIndex().expressionList()
                    .expression();
            if (indexList.size() > 3) {
                throw new ParseException(source, variableCtx, DIMENSIONS_STRING + indexList.size());
            }
            List<ExpressionNode> indexNodes = new ArrayList<>(indexList.size());
            for (I4GLParser.ExpressionContext indexCtx : indexList) {
                indexNodes.add((ExpressionNode) visit(indexCtx));
            }
            variableNode = createReadArrayElementNode(variableNode, indexNodes);
            int index = 0;
            RecordType recordType = (RecordType) variableNode.getReturnType();
            while (index < variableCtx.identifier().size() - 1) {
                if (!(recordType instanceof RecordType)) {
                    throw new TypeMismatchException(recordType.toString(), RECORD_STRING);
                }
                String identifier = variableCtx.identifier(index++).getText();
                variableNode = createRecordFieldNode(variableNode, identifier);
                recordType = (RecordType) variableNode.getReturnType();
            }
            String identifier = variableCtx.identifier(index).getText();
            BaseType fieldType = recordType.getFieldType(identifier);
            ExpressionNode castNode = createCastNode(valueNode, fieldType);
            StatementNode node = WriteRecordFieldNodeGen.create(variableNode, castNode, identifier, fieldType);
            node.addStatementTag();
            setSourceFromContext(node, variableCtx);
            return node;
        } catch (LexicalException e) {
            throw new ParseException(source, variableCtx, e.getMessage());
        }
    }

    private StatementNode createAssignIndexedRecordVariable(final String identifier,
            final ExpressionNode variableNode, final I4GLParser.VariableIndexContext variableIndexCtx,
            final ExpressionNode valueNode) throws LexicalException {
        RecordType recordType = (RecordType) variableNode.getReturnType();
        if (!recordType.containsIdentifier(identifier)) {
            throw new UnknownIdentifierException("The record does not contain this identifier");
        }
        final List<I4GLParser.ExpressionContext> indexList = variableIndexCtx.expressionList().expression();
        List<ExpressionNode> indexNodes = new ArrayList<>(indexList.size());
        for (I4GLParser.ExpressionContext indexCtx : indexList) {
            indexNodes.add((ExpressionNode) visit(indexCtx));
        }
        BaseType fieldType = recordType.getFieldType(identifier);
        if (fieldType instanceof ArrayType) {
            if (indexList.size() > 3) {
                throw new LexicalException(DIMENSIONS_STRING + indexList.size());
            }
        } else if (fieldType instanceof TextType) {
            if (indexList.size() != 1) {
                throw new LexicalException(DIMENSIONS_STRING + indexList.size());
            }
        } else {
            throw new LexicalException("Variable must be indexed (string or array) to assign to []");
        }
        ReadRecordFieldNode readNode = createRecordFieldNode(variableNode, identifier);
        return createAssignmentToArray(readNode, indexNodes, valueNode);
    }

    private StatementNode createAssignmentToIndexedRecordVariable(final I4GLParser.RecordVariableContext variableCtx,
            final I4GLParser.VariableIndexContext variableIndexCtx, final ExpressionNode valueNode) {
        try {
            ExpressionNode variableNode = (ExpressionNode) visit(variableCtx.simpleVariable());
            int index = 0;
            while (index < variableCtx.identifier().size() - 1) {
                String identifier = variableCtx.identifier(index++).getText();
                variableNode = createRecordFieldNode(variableNode, identifier);
            }
            BaseType expressionType = variableNode.getReturnType();
            if (!(expressionType instanceof RecordType)) {
                throw new TypeMismatchException(expressionType.toString(), RECORD_STRING);
            }
            String identifier = variableCtx.identifier(index).getText();
            StatementNode node = createAssignIndexedRecordVariable(identifier, variableNode, variableIndexCtx,
                    valueNode);
            node.addStatementTag();
            setSourceFromContext(node, variableCtx);
            return node;
        } catch (LexicalException e) {
            throw new ParseException(source, variableCtx, e.getMessage());
        }
    }

    private StatementNode createAssignmentToIndexedVariable(final I4GLParser.IndexedVariableContext variableCtx,
            final ExpressionNode valueNode) {
        final NotIndexedVariableContext notIndexedVariableContext = variableCtx.notIndexedVariable();
        if (notIndexedVariableContext.simpleVariable() != null) {
            if (variableCtx.identifier() != null && !variableCtx.identifier().isEmpty()) {
                return createAssignmentToIndexedSimpleVariableWithRecord(variableCtx, valueNode);
            }
            return createAssignmentToIndexedSimpleVariable(notIndexedVariableContext.simpleVariable(),
                    variableCtx.variableIndex(), valueNode);
        }
        return createAssignmentToIndexedRecordVariable(notIndexedVariableContext.recordVariable(),
                variableCtx.variableIndex(), valueNode);
    }

    private StatementNode createAssignmentNode(final I4GLParser.VariableContext variableCtx,
            final ExpressionNode valueNode) {
        if (variableCtx.notIndexedVariable() != null) {
            return createAssignmentToNotIndexedVariable(variableCtx.notIndexedVariable(), valueNode);
        }
        return createAssignmentToIndexedVariable(variableCtx.indexedVariable(), valueNode);
    }

    @Override
    public Node visitSqlStatement410(final I4GLParser.SqlStatement410Context ctx) {
        StatementNode node = (StatementNode) visit(ctx.sqlStatement());
        setSourceFromContext(node, ctx);
        return node;
    }

    @Override
    public Node visitSqlStatement(final I4GLParser.SqlStatementContext ctx) {
        StatementNode node = (StatementNode) super.visitSqlStatement(ctx);
        if (node == null) {
            try {
                final ExpressionNode databaseVariableNode = createReadDatabaseVariableNode();
                final int start = ctx.start.getStartIndex();
                final int end = ctx.stop.getStopIndex();
                final String sql = ctx.start.getInputStream().getText(new Interval(start, end));
                node = new SqlNode(databaseVariableNode, sql);
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

            WriteResultsNode assignResultsNode = null;
            if (ctx.intoVariableList() != null) {
                end = ctx.intoVariableList().start.getStartIndex() - 1;
                interval = new Interval(start, end);
                sql = ctx.start.getInputStream().getText(interval);
                start = ctx.intoVariableList().stop.getStopIndex() + 1;
                assignResultsNode = (WriteResultsNode) visit(ctx.intoVariableList());
            }

            end = ctx.stop.getStopIndex();
            interval = new Interval(start, end);
            sql += ctx.start.getInputStream().getText(interval);
            ExpressionNode databaseVariableNode = createReadDatabaseVariableNode();
            SelectNode node = new SelectNode(databaseVariableNode, sql, assignResultsNode);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }

    private List<TableColumnInfo> getTableColumnDefaults(final I4GLParser.ColumnsListContext ctx) {
        // TODO: Should this be done at runtime ?
        List<TableColumnInfo> columnDefaultList = new ArrayList<>();
        Database database = getDatabase(ctx);
        SQLDatabaseMetaData dmd = database.getSession().getSQLConnection().getSQLMetaData();
        for (var columnTableId : ctx.columnsTableId()) {
            if (columnTableId.tableIdentifier().tableQualifier() != null) {
                throw new NotImplementedException();
            }
            final String tableName = columnTableId.tableIdentifier().identifier().getText();
            String columnName = null;
            if (columnTableId.identifier() != null) {
                columnName = columnTableId.identifier().getText();
            } else if (columnTableId.STAR() == null) {
                throw new ParseException(source, ctx, "Invalid column definition");
            }
            try {
                TableColumnInfo[] tableColumnInfoArray = dmd.getColumnInfo(null, null, tableName);
                for (TableColumnInfo tableColumnInfo : tableColumnInfoArray) {
                    if (columnName == null || tableColumnInfo.getColumnName().equals(columnName)) {
                        columnDefaultList.add(tableColumnInfo);
                    }
                }
            } catch (SQLException e) {
                throw new ParseException(source, ctx, e.getMessage());
            }
        }
        return columnDefaultList;
    }

    @Override
    public Node visitInitializeStatement(final I4GLParser.InitializeStatementContext ctx) {
        WriteResultsNode assignResultsNode = (WriteResultsNode) visit(ctx.variableOrComponentList());
        StatementNode node = null;
        if (ctx.LIKE() != null) {
            var columDefaultInfoList = getTableColumnDefaults(ctx.columnsList());
            if (assignResultsNode.getReadAssignMap().size() != columDefaultInfoList.size()) {
                throw new ParseException(source, ctx, "Type or number of parameters mismmatch");
            }
            node = (StatementNode) new InitializeNode(assignResultsNode,
                    columDefaultInfoList.toArray(new TableColumnInfo[0]));
        }
        if (ctx.TO() != null || ctx.NULL() != null) {
            node = (StatementNode) new InitializeToNullNode(assignResultsNode);
        }
        setSourceFromContext(node, ctx);
        node.addStatementTag();
        return node;
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
            final FrameSlot cursorSlot = currentParseScope.addVariable(cursorName, CursorType.SINGLETON);
            ExpressionNode databaseVariableNode = createReadDatabaseVariableNode();
            StatementNode node = new CursorNode(databaseVariableNode, sql, cursorSlot);
            setSourceFromContext(node, ctx);
            node.addStatementTag();
            return node;
        } catch (final LexicalException e) {
            throw new ParseException(source, ctx, e.getMessage());
        }
    }
}