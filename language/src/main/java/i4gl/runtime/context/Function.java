package i4gl.runtime.context;

import java.util.logging.Level;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.CyclicAssumption;
import com.oracle.truffle.api.utilities.TriState;

import i4gl.I4GLLanguage;

@ExportLibrary(InteropLibrary.class)
public final class Function implements TruffleObject {

    public static final int INLINE_CACHE_SIZE = 2;

    private static final TruffleLogger LOG = TruffleLogger.getLogger(I4GLLanguage.ID, Function.class);

    /** The name of the function. */
    private final String name;

    /** The current implementation of this function. */
    private RootCallTarget callTarget;

    /**
     * Manages the assumption that the {@link #callTarget} is stable. We use the utility class
     * {@link CyclicAssumption}, which automatically creates a new {@link Assumption} when the old
     * one gets invalidated.
     */
    private final CyclicAssumption callTargetStable;

    protected Function(I4GLLanguage language, String name) {
        this(language.getOrCreateUndefinedFunction(name));
    }

    protected Function(RootCallTarget callTarget) {
        this.name = callTarget.getRootNode().getName();
        this.callTargetStable = new CyclicAssumption(name);
        setCallTarget(callTarget);
    }

    public String getName() {
        return name;
    }

    protected void setCallTarget(RootCallTarget callTarget) {
        boolean wasNull = this.callTarget == null;
        this.callTarget = callTarget;
        /*
         * We have a new call target. Invalidate all code that speculated that the old call target
         * was stable.
         */
        LOG.log(Level.FINE, "Installed call target for: {0}", name);
        if (!wasNull) {
            callTargetStable.invalidate();
        }
    }

    public RootCallTarget getCallTarget() {
        return callTarget;
    }

    public Assumption getCallTargetStable() {
        return callTargetStable.getAssumption();
    }

    /**
     * This method is, e.g., called when using a function literal in a string concatenation. So
     * changing it has an effect on I4GL programs.
     */
    @Override
    public String toString() {
        return name;
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<?>> getLanguage() {
        return I4GLLanguage.class;
    }

    /**
     * {@link Function} instances are always visible as executable to other languages.
     */
    @ExportMessage
    @TruffleBoundary
    SourceSection getSourceLocation() {
        return getCallTarget().getRootNode().getSourceSection();
    }

    @ExportMessage
    boolean hasSourceLocation() {
        return true;
    }

    /**
     * {@link Function} instances are always visible as executable to other languages.
     */
    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return FunctionType.SINGLETON;
    }

    @ExportMessage
    static final class IsIdenticalOrUndefined {
        @Specialization
        static TriState doI4GLFunction(Function receiver, Function other) {
            /*
             * I4GLFunctions are potentially identical to other I4GLFunctions.
             */
            return receiver == other ? TriState.TRUE : TriState.FALSE;
        }

        @Fallback
        static TriState doOther(Function receiver, Object other) {
            return TriState.UNDEFINED;
        }
    }

    @ExportMessage
    @TruffleBoundary
    static int identityHashCode(Function receiver) {
        return System.identityHashCode(receiver);
    }

    @ExportMessage
    Object toDisplayString(boolean allowSideEffects) {
        return name;
    }

    /**
     * We allow languages to execute this function. We implement the interop execute message that
     * forwards to a function dispatch.
     *
     * Since invocations are potentially expensive (result in an indirect call, which is expensive
     * by itself but also limits function inlining which can hinder other optimisations) if the node
     * turns megamorphic (i.e. cache limit is exceeded) we annotate it with {@ReportPolymorphism}.
     * This ensures that the runtime is notified when this node turns polymorphic. This, in turn,
     * may, under certain conditions, cause the runtime to attempt to make node monomorphic again by
     * duplicating the entire AST containing that node and specialising it for a particular call
     * site.
     */
    @ReportPolymorphism
    @ExportMessage
    abstract static class Execute {

        /**
         * Inline cached specialization of the dispatch.
         *
         * <p>
         * Since I4GL is a quite simple language, the benefit of the inline cache seems small: after
         * checking that the actual function to be executed is the same as the cachedFuntion, we can
         * safely execute the cached call target. You can reasonably argue that caching the call
         * target is overkill, since we could just retrieve it via {@code function.getCallTarget()}.
         * However, caching the call target and using a {@link DirectCallNode} allows Truffle to
         * perform method inlining. In addition, in a more complex language the lookup of the call
         * target is usually much more complicated than in I4GL.
         * </p>
         *
         * <p>
         * {@code limit = "INLINE_CACHE_SIZE"} Specifies the limit number of inline cache
         * specialization instantiations.
         * </p>
         * <p>
         * {@code guards = "function.getCallTarget() == cachedTarget"} The inline cache check. Note
         * that cachedTarget is a final field so that the compiler can optimize the check.
         * </p>
         * <p>
         * {@code assumptions = "callTargetStable"} Support for function redefinition: When a
         * function is redefined, the call target maintained by the I4GLFunction object is changed. To
         * avoid a check for that, we use an Assumption that is invalidated by the I4GLFunction when
         * the change is performed. Since checking an assumption is a no-op in compiled code, the
         * assumption check performed by the DSL does not add any overhead during optimized
         * execution.
         * </p>
         *
         * @see Cached
         * @see Specialization
         *
         * @param function the dynamically provided function
         * @param arguments the arguments to the function
         * @param callTargetStable The assumption object assuming the function was not redefined.
         * @param cachedTarget The call target we aim to invoke
         * @param callNode the {@link DirectCallNode} specifically created for the
         *            {@link CallTarget} in cachedFunction.
         */
        @Specialization(limit = "INLINE_CACHE_SIZE", //
                        guards = "function.getCallTarget() == cachedTarget", //
                        assumptions = "callTargetStable")
        protected static Object doDirect(Function function, Object[] arguments,
                        @Cached("function.getCallTargetStable()") Assumption callTargetStable,
                        @Cached("function.getCallTarget()") RootCallTarget cachedTarget,
                        @Cached("create(cachedTarget)") DirectCallNode callNode) {

            /* Inline cache hit, we are safe to execute the cached call target. */
            Object returnValue = callNode.call(arguments);
            return returnValue;
        }

        /**
         * Slow-path code for a call, used when the polymorphic inline cache exceeded its maximum
         * size specified in <code>INLINE_CACHE_SIZE</code>. Such calls are not optimized any
         * further, e.g., no method inlining is performed.
         */
        @Specialization(replaces = "doDirect")
        protected static Object doIndirect(Function function, Object[] arguments,
                        @Cached IndirectCallNode callNode) {
            /*
             * I4GL has a quite simple call lookup: just ask the function for the current call target,
             * and call it.
             */
            return callNode.call(function.getCallTarget(), arguments);
        }
    }

}
