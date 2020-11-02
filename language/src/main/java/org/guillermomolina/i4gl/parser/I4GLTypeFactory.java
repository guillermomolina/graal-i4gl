package org.guillermomolina.i4gl.parser;

import java.sql.SQLException;
import java.sql.Types;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.parser.exceptions.ParseException;
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

import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class I4GLTypeFactory extends I4GLParserBaseVisitor<I4GLType> {
    final private I4GLNodeFactory nodeFactory;

    public I4GLTypeFactory(final I4GLNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public I4GLType visitCharType(final I4GLParser.CharTypeContext ctx) {
        if (ctx.varchar() != null) {
            final int size = Integer.parseInt(ctx.numericConstant(0).getText());
            return new I4GLVarcharType(size);
        } else {
            int size = 1;
            if (!ctx.numericConstant().isEmpty()) {
                size = Integer.parseInt(ctx.numericConstant(0).getText());
            }
            return new I4GLCharType(size);
        }
    }

    @Override
    public I4GLType visitNumberType(final I4GLParser.NumberTypeContext ctx) {
        if (ctx.SMALLINT() != null) {
            return I4GLSmallIntType.SINGLETON;
        }
        if (ctx.INTEGER() != null || ctx.INT() != null) {
            return I4GLIntType.SINGLETON;
        }
        if (ctx.BIGINT() != null) {
            return I4GLBigIntType.SINGLETON;
        }
        if (ctx.SMALLFLOAT() != null || ctx.REAL() != null) {
            return I4GLSmallFloatType.SINGLETON;
        }
        if (ctx.FLOAT() != null || ctx.DOUBLE() != null) {
            return I4GLFloatType.SINGLETON;
        }
        throw new NotImplementedException();
    }

    @Override
    public I4GLType visitTimeType(final I4GLParser.TimeTypeContext ctx) {
        throw new NotImplementedException();
    }

    private String getTableName(final I4GLParser.TableIdentifierContext ctx) {
        if (ctx.tableQualifier() != null) {
            throw new NotImplementedException();
        }
        return ctx.identifier().getText();
    }

    @Override
    public I4GLType visitIndirectType(final I4GLParser.IndirectTypeContext ctx) {
        final String tableName = getTableName(ctx.tableIdentifier());
        final String columnName = ctx.identifier().getText();
        I4GLDatabase database = nodeFactory.getDatabase(ctx);
        SQLDatabaseMetaData _dmd = database.getSession().getSQLConnection().getSQLMetaData();
        try {
            TableColumnInfo[] infos = _dmd.getColumnInfo(null, null, tableName);
            for (int i = 0; i < infos.length; i++) {
                TableColumnInfo info = infos[i];
                if (info.getColumnName().compareTo(columnName) == 0) {
                    switch (info.getDataType()) {
                        case Types.VARCHAR:
                            return new I4GLVarcharType(info.getColumnSize());
                        case Types.INTEGER:
                            return I4GLIntType.SINGLETON;
                        case Types.BIGINT:
                            return I4GLBigIntType.SINGLETON;
                        case Types.REAL:
                            return I4GLSmallFloatType.SINGLETON;
                        case Types.FLOAT:
                            return I4GLFloatType.SINGLETON;
                        default:
                            throw new NotImplementedException();
                    }
                }
            }
        } catch (SQLException e) {
            throw new ParseException(nodeFactory.getSource(), ctx, e.getMessage());
        }
        throw new ParseException(nodeFactory.getSource(), ctx,
                "There is no column named " + columnName + " in table " + tableName);
    }

    @Override
    public I4GLType visitLargeType(final I4GLParser.LargeTypeContext ctx) {
        if (ctx.BYTE() != null) {
            throw new NotImplementedException();
        } else /* there is a ctx.TEXT() */ {
            return I4GLTextType.SINGLETON;
        }
    }

    @Override
    public I4GLType visitRecordType(final I4GLParser.RecordTypeContext ctx) {
        I4GLParseScope currentParseScope = nodeFactory.pushNewScope(I4GLParseScope.RECORD_TYPE, null);

        if (ctx.LIKE() != null) {
            throw new NotImplementedException();
        }
        for (I4GLParser.VariableDeclarationContext variableDeclarationCtx : ctx.variableDeclaration()) {
            nodeFactory.visit(variableDeclarationCtx);
        }

        I4GLRecordType type = currentParseScope.createRecordType();

        nodeFactory.popScope();
        return type;
    }

    @Override
    public I4GLType visitArrayType(final I4GLParser.ArrayTypeContext ctx) {
        final I4GLType type = visit(ctx.arrayTypeType());
        I4GLArrayType arrayDescriptor = null;
        for (int i = ctx.arrayIndexer().dimensionSize().size() - 1; i >= 0; i--) {
            int size = Integer.parseInt(ctx.arrayIndexer().dimensionSize(i).getText());
            if (arrayDescriptor == null) {
                arrayDescriptor = new I4GLArrayType(size, type);
            } else {
                arrayDescriptor = new I4GLArrayType(size, arrayDescriptor);
            }
        }
        return arrayDescriptor;
    }

    @Override
    public I4GLType visitDynArrayType(final I4GLParser.DynArrayTypeContext ctx) {
        throw new NotImplementedException();
    }
}