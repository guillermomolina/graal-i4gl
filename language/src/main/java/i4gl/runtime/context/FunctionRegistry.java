package i4gl.runtime.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.source.Source;

import i4gl.I4GLLanguage;
import i4gl.parser.FullParser;

/**
 * Manages the mapping from function names to {@link Function function objects}.
 */
public final class FunctionRegistry {

    private final I4GLLanguage language;
    private final Functions functionsObject = new Functions();
    private final Map<Map<String, RootCallTarget>, Void> registeredFunctions = new IdentityHashMap<>();

    public FunctionRegistry(I4GLLanguage language) {
        this.language = language;
    }

    /**
     * Returns the canonical {@link Function} object for the given name. If it does not exist yet,
     * it is created.
     */
    @TruffleBoundary
    public Function lookup(String name, boolean createIfNotPresent) {
        Function result = functionsObject.functions.get(name);
        if (result == null && createIfNotPresent) {
            result = new Function(language, name);
            functionsObject.functions.put(name, result);
        }
        return result;
    }

    /**
     * Associates the {@link Function} with the given name with the given implementation root
     * node. If the function did not exist before, it defines the function. If the function existed
     * before, it redefines the function and the old implementation is discarded.
     */
    Function register(String name, RootCallTarget callTarget) {
        Function result = functionsObject.functions.get(name);
        if (result == null) {
            result = new Function(callTarget);
            functionsObject.functions.put(name, result);
        } else {
            result.setCallTarget(callTarget);
        }
        return result;
    }

    /**
     * Registers a map of functions. The once registered map must not change in order to allow to
     * cache the registration for the entire map. If the map is changed after registration the
     * functions might not get registered.
     */
    @TruffleBoundary
    public void register(Map<String, RootCallTarget> newFunctions) {
        if (registeredFunctions.containsKey(newFunctions)) {
            return;
        }
        for (Map.Entry<String, RootCallTarget> entry : newFunctions.entrySet()) {
            register(entry.getKey(), entry.getValue());
        }
        registeredFunctions.put(newFunctions, null);
    }

    public void register(Source newFunctions) {
        final FullParser parser = new FullParser(language, newFunctions);
        register(parser.getAllFunctions());
    }

    public Function getFunction(String name) {
        return functionsObject.functions.get(name);
    }

    /**
     * Returns the sorted list of all functions, for printing purposes only.
     */
    public List<Function> getFunctions() {
        List<Function> result = new ArrayList<>(functionsObject.functions.values());
        Collections.sort(result, new Comparator<Function>() {
            public int compare(Function f1, Function f2) {
                return f1.toString().compareTo(f2.toString());
            }
        });
        return result;
    }

    public TruffleObject getFunctionsObject() {
        return functionsObject;
    }

}