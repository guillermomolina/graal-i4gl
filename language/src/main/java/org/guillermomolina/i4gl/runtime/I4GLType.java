package org.guillermomolina.i4gl.runtime;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.I4GLContext;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.customvalues.ArrayValue;
import org.guillermomolina.i4gl.runtime.customvalues.CharValue;
import org.guillermomolina.i4gl.runtime.customvalues.RecordValue;
import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;

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
public final class I4GLType implements TruffleObject {

    /*
     * These are the sets of builtin types in simple languages. In case of i4gl the
     * types nicely match those of the types in InteropLibrary. This might not be
     * the case and more additional checks need to be performed (similar to number
     * checking for I4GLBigNumber).
     */
    public static final I4GLType INT = new I4GLType("INT", (l, v) -> l.fitsInInt(v));
    public static final I4GLType BIGINT = new I4GLType("BIGINT", (l, v) -> l.fitsInLong(v));
    public static final I4GLType SMALLFLOAT = new I4GLType("SMALLFLOAT", (l, v) -> v instanceof Float);
    public static final I4GLType DOUBLE = new I4GLType("DOUBLE", (l, v) -> v instanceof Double);
    public static final I4GLType NULL = new I4GLType("NULL", (l, v) -> l.isNull(v));
    public static final I4GLType CHAR = new I4GLType("CHAR", (l, v) -> v instanceof CharValue);
    public static final I4GLType VARCHAR = new I4GLType("VARCHAR", (l, v) -> v instanceof VarcharValue);
    public static final I4GLType TEXT = new I4GLType("TEXT", (l, v) -> l.isString(v));
    public static final I4GLType FUNCTION = new I4GLType("FUNCTION", (l, v) -> l.isExecutable(v));
    public static final I4GLType ARRAY = new I4GLType("ARRAY", (l, v) -> v instanceof ArrayValue);
    public static final I4GLType RECORD = new I4GLType("RECORD", (l, v) -> v instanceof RecordValue);
    public static final I4GLType OBJECT = new I4GLType("OBJECT", (l, v) -> l.hasMembers(v));

    /*
     * This array is used when all types need to be checked in a certain order.
     * While most interop types like number or string are exclusive, others traits
     * like members might not be. For example, an object might be a function.
     */
    @CompilationFinal(dimensions = 1)
    protected static final I4GLType[] PRECEDENCE = new I4GLType[] { NULL, INT, BIGINT, SMALLFLOAT, DOUBLE, CHAR,
            VARCHAR, TEXT, FUNCTION, ARRAY, RECORD, OBJECT };

    private final String name;
    private final TypeCheck isInstance;

    /*
     * We don't allow dynamic instances of I4GLType. Real languages might want to
     * expose this for types that are user defined.
     */
    private I4GLType(String name, TypeCheck isInstance) {
        this.name = name;
        this.isInstance = isInstance;
    }

    /**
     * Checks whether this type is of a certain instance. If used on fast-paths it
     * is required to cast {@link I4GLType} to a constant.
     */
    public boolean isInstance(Object value, InteropLibrary interop) {
        CompilerAsserts.partialEvaluationConstant(this);
        return isInstance.check(interop, value);
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<I4GLContext>> getLanguage() {
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
        return name;
    }

    @ExportMessage(name = "toDisplayString")
    Object toDisplayString(boolean allowSideEffects) {
        return name;
    }

    @Override
    public String toString() {
        return "I4GLType[" + name + "]";
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
        static boolean doCached(I4GLType type, Object value, @Cached("type") I4GLType cachedType,
                @CachedLibrary("value") InteropLibrary valueLib) {
            return cachedType.isInstance.check(valueLib, value);
        }

        @TruffleBoundary
        @Specialization(replaces = "doCached")
        static boolean doGeneric(I4GLType type, Object value) {
            return type.isInstance.check(InteropLibrary.getFactory().getUncached(), value);
        }
    }

    /*
     * A convenience interface for type checks. Alternatively this could have been
     * solved using subtypes of I4GLType.
     */
    @FunctionalInterface
    interface TypeCheck {

        boolean check(InteropLibrary lib, Object value);

    }

}
