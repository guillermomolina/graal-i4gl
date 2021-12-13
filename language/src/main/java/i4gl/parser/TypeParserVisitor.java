package i4gl.parser;

import java.sql.SQLException;
import java.util.List;

import i4gl.exceptions.NotImplementedException;
import i4gl.parser.exceptions.LexicalException;
import i4gl.parser.exceptions.ParseException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.ArrayType;
import i4gl.runtime.types.compound.Char1Type;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.types.compound.DecimalType;
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

public class TypeParserVisitor extends I4GLParserBaseVisitor<BaseType> {
    private final NodeParserVisitor nodeFactory;

    public TypeParserVisitor(final NodeParserVisitor nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public BaseType visitCharType(final I4GLParser.CharTypeContext ctx) {
        if (ctx.varchar() != null) {
            final int size = Integer.parseInt(ctx.numericConstant(0).getText());
            return new VarcharType(size);
        } else {
            int size = 1;
            if (!ctx.numericConstant().isEmpty()) {
                size = Integer.parseInt(ctx.numericConstant(0).getText());
            }
            if(size == 1) {
                return Char1Type.SINGLETON;
            }
            return new CharType(size);
        }
    }

    @Override
    public BaseType visitDecimalType(final I4GLParser.DecimalTypeContext ctx) {
        int precision = Integer.parseInt(ctx.numericConstant(0).getText());
        int scale = 0;
        if (ctx.numericConstant().size() == 2) {
            scale = Integer.parseInt(ctx.numericConstant(1).getText());
        }
        return new DecimalType(precision, scale);
    }

    @Override
    public BaseType visitFloatType(final I4GLParser.FloatTypeContext ctx) {
        if (ctx.numericConstant() != null) {
            throw new NotImplementedException();
        }
        return FloatType.SINGLETON;
    }

    @Override
    public BaseType visitNumberType(final I4GLParser.NumberTypeContext ctx) {
        if (ctx.SMALLINT() != null) {
            return SmallIntType.SINGLETON;
        }
        if (ctx.INTEGER() != null || ctx.INT() != null) {
            return IntType.SINGLETON;
        }
        if (ctx.BIGINT() != null) {
            return BigIntType.SINGLETON;
        }
        if (ctx.SMALLFLOAT() != null || ctx.REAL() != null) {
            return SmallFloatType.SINGLETON;
        }
        if (ctx.floatType() != null) {
            return visit(ctx.floatType());
        }
        if (ctx.decimalType() != null) {
            return visit(ctx.decimalType());
        }
        throw new NotImplementedException();
    }

    @Override
    public BaseType visitDateType(final I4GLParser.DateTypeContext ctx) {
        return DateType.SINGLETON;
    }

    private String getTableName(final I4GLParser.TableIdentifierContext ctx) {
        if (ctx.tableQualifier() != null) {
            throw new NotImplementedException();
        }
        return ctx.identifier().getText();
    }

    @Override
    public BaseType visitIndirectType(final I4GLParser.IndirectTypeContext ctx) {
        final String tableName = getTableName(ctx.tableIdentifier());
        final String columnName = ctx.identifier().getText();
        Database database = nodeFactory.getDatabase(ctx);
        SQLDatabaseMetaData dmd = database.getSession().getSQLConnection().getSQLMetaData();
        try {
            TableColumnInfo[] infos = dmd.getColumnInfo(null, null, tableName);
            for (int i = 0; i < infos.length; i++) {
                TableColumnInfo info = infos[i];
                if (info.getColumnName().compareTo(columnName) == 0) {
                    return BaseType.fromTableColumInfo(info);
                }
            }
        } catch (SQLException e) {
            throw new ParseException(nodeFactory.getSource(), ctx, e.getMessage());
        }
        throw new ParseException(nodeFactory.getSource(), ctx,
                "There is no column named " + columnName + " in table " + tableName);
    }

    @Override
    public BaseType visitLargeType(final I4GLParser.LargeTypeContext ctx) {
        if (ctx.BYTE() != null) {
            throw new NotImplementedException();
        } else /* there is a ctx.TEXT() */ {
            return TextType.SINGLETON;
        }
    }

    @Override
    public BaseType visitRecordType(final I4GLParser.RecordTypeContext ctx) {
        ParseScope currentParseScope = nodeFactory.pushNewScope(ParseScope.RECORD_TYPE, null);
        for (I4GLParser.VariableDeclarationContext variableDeclarationCtx : ctx.variableDeclaration()) {
            try {
                final BaseType type = visit(variableDeclarationCtx.type());
                final List<I4GLParser.IdentifierContext> identifierCtxList = variableDeclarationCtx.identifier();
                for (final I4GLParser.IdentifierContext identifierCtx : identifierCtxList) {
                    final String identifier = identifierCtx.getText();
                    currentParseScope.registerLocalVariable(identifier, type);
                }
            } catch (final LexicalException e) {
                throw new ParseException(nodeFactory.getSource(), ctx, e.getMessage());
            }
        }

        RecordType type = currentParseScope.createRecordType();

        nodeFactory.popScope();
        return type;
    }

    @Override
    public BaseType visitRecordLikeType(final I4GLParser.RecordLikeTypeContext ctx) {
        ParseScope currentParseScope = nodeFactory.pushNewScope(ParseScope.RECORD_TYPE, null);
        final String tableName = getTableName(ctx.tableIdentifier());
        Database database = nodeFactory.getDatabase(ctx);
        SQLDatabaseMetaData dmd = database.getSession().getSQLConnection().getSQLMetaData();
        try {
            TableColumnInfo[] infos = dmd.getColumnInfo(null, null, tableName);
            for (int i = 0; i < infos.length; i++) {
                TableColumnInfo info = infos[i];
                final BaseType type = BaseType.fromTableColumInfo(info);
                final String columnName = info.getColumnName();
                currentParseScope.registerLocalVariable(columnName, type);
            }
        } catch (SQLException | LexicalException e) {
            throw new ParseException(nodeFactory.getSource(), ctx, e.getMessage());
        }

        RecordType type = currentParseScope.createRecordType();

        nodeFactory.popScope();
        return type;
    }

    @Override
    public BaseType visitArrayType(final I4GLParser.ArrayTypeContext ctx) {
        final BaseType elementsType = visit(ctx.arrayElementstype());
        ArrayType arrayType = null;
        for (int i = ctx.arrayIndexer().dimensionSize().size() - 1; i >= 0; i--) {
            int size = Integer.parseInt(ctx.arrayIndexer().dimensionSize(i).getText());
            if (arrayType == null) {
                arrayType = new ArrayType(size, elementsType);
            } else {
                arrayType = new ArrayType(size, arrayType);
            }
        }
        return arrayType;
    }

    @Override
    public BaseType visitDynArrayType(final I4GLParser.DynArrayTypeContext ctx) {
        throw new NotImplementedException();
    }
}