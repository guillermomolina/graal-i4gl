package i4gl.runtime.types;

import java.sql.JDBCType;
import java.sql.Types;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;
import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.context.Context;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.types.compound.VarcharType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.DecimalType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * The isInstance type checks are declared using an functional interface and are
 * expressed using the interoperability libraries. The advantage of this is type
 * checks automatically work for foreign values or primitive values like byte or
 * short.
 * <p>
 * The class implements the interop contracts for
 * {@link InteropLibrary#isMetaObject(Object)} and
 * {@link InteropLibrary#isMetaInstance(Object, Object)}. The latter allows
 * other languages and tools to perform type checks using types of i4gl.
 * <p>
 * In order to assign types to guest language values, I4GL values implement
 * {@link InteropLibrary#getMetaObject(Object)}. The interop contracts for
 * primitive values cannot be overriden, so in order to assign meta-objects to
 * primitive values, the primitive values are assigned using language views. See
 * {@link I4GLLanguage#getLanguageView}.
 */
@ExportLibrary(InteropLibrary.class)
public abstract class BaseType implements TruffleObject {

    private static final TruffleLogger LOGGER = I4GLLanguage.getLogger(BaseType.class);

    public static BaseType fromTableColumInfo(final TableColumnInfo info) {
        switch (info.getDataType()) {
            case Types.CHAR:
                return new CharType(info.getColumnSize());
            case Types.VARCHAR:
                return new VarcharType(info.getColumnSize());
            case Types.SMALLINT:
                return SmallIntType.SINGLETON;
            case Types.INTEGER:
                return IntType.SINGLETON;
            case Types.BIGINT:
                return BigIntType.SINGLETON;
            case Types.REAL:
                return SmallFloatType.SINGLETON;
            case Types.FLOAT:
                return FloatType.SINGLETON;
            case Types.DECIMAL:
                return new DecimalType(info.getColumnSize(), info.getDecimalDigits());
            case Types.DATE:
                return DateType.SINGLETON;
            default:
                LOGGER.warning("Unknown SQL type: " + JDBCType.valueOf(info.getDataType()).getName());
                throw new NotImplementedException();
        }
    }

    /**
     * Checks whether this type is of a certain instance. If used on fast-paths it
     * is required to cast {@link BaseType} to a constant.
     */
    public abstract boolean isInstance(Object value, InteropLibrary interop);

    /**
     * Gets the {@link FrameSlotKind} of the value that is represented by this
     * descriptor.
     */
    public abstract FrameSlotKind getSlotKind();

    /**
     * Gets the default value of this type. It is used mainly for initialization of
     * variables.
     */
    public abstract Object getDefaultValue();


    public abstract String getNullString();


    /**
     * Checks whether this type is convertible to the selected type.
     */
    public abstract boolean convertibleTo(BaseType type);

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<Context>> getLanguage() {
        return I4GLLanguage.class;
    }

    /*
     * All I4GLTypeSystem are declared as interop meta-objects. Other example for
     * meta-objects are Java classes, or JavaScript prototypes.
     */
    @ExportMessage
    boolean isMetaObject() {
        return true;
    }

    /*
     * I4GL does not have the notion of a qualified or simple name, so we return the
     * same type name for both.
     */
    @ExportMessage(name = "getMetaQualifiedName")
    @ExportMessage(name = "getMetaSimpleName")
    public Object getName() {
        return toString();
    }

    @ExportMessage(name = "toDisplayString")
    Object toDisplayString(boolean allowSideEffects) {
        return toString();
    }

    /*
     * The interop message isMetaInstance might be used from other languages or by
     * the {@link I4GLIsInstanceBuiltin isInstance} builtin. It checks whether a
     * given value, which might be a primitive, foreign or I4GL value is of a given
     * I4GL type. This allows other languages to make their instanceOf interopable
     * with foreign values.
     */
    @ExportMessage
    static class IsMetaInstance {

        protected IsMetaInstance() {
        }

        /*
         * We assume that the same type is checked at a source location. Therefore we
         * use an inline cache to specialize for observed types to be constant. The
         * limit of "3" specifies that we specialize for 3 different types until we
         * rewrite to the doGeneric case. The limit in this example is somewhat
         * arbitrary and should be determined using careful tuning with real world
         * benchmarks.
         */
        @Specialization(guards = "type == cachedType", limit = "3")
        static boolean doCached(BaseType type, Object value, @Cached("type") BaseType cachedType,
                @CachedLibrary("value") InteropLibrary library) {
            return cachedType.isInstance(value, library);
        }

        @TruffleBoundary
        @Specialization(replaces = "doCached")
        static boolean doGeneric(BaseType type, Object value) {
            return type.isInstance(value, InteropLibrary.getFactory().getUncached());
        }
    }
}
