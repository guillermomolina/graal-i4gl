package i4gl.runtime.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import i4gl.I4GLLanguage;
import i4gl.parser.I4GLFullParser;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.source.Source;

/**
 * Manages the mapping from function names to {@link I4GLFunction function objects}.
 */
public final class I4GLFunctionRegistry {

    private final I4GLLanguage language;
    private final I4GLFunctions functionsObject = new I4GLFunctions();
    private final Map<Map<String, RootCallTarget>, Void> registeredFunctions = new IdentityHashMap<>();

    public I4GLFunctionRegistry(I4GLLanguage language) {
        this.language = language;
    }

    /**
     * Returns the canonical {@link I4GLFunction} object for the given name. If it does not exist yet,
     * it is created.
     */
    @TruffleBoundary
    public I4GLFunction lookup(String name, boolean createIfNotPresent) {
        I4GLFunction result = functionsObject.functions.get(name);
        if (result == null && createIfNotPresent) {
            result = new I4GLFunction(language, name);
            functionsObject.functions.put(name, result);
        }
        return result;
    }

    /**
     * Associates the {@link I4GLFunction} with the given name with the given implementation root
     * node. If the function did not exist before, it defines the function. If the function existed
     * before, it redefines the function and the old implementation is discarded.
     */
    I4GLFunction register(String name, RootCallTarget callTarget) {
        I4GLFunction result = functionsObject.functions.get(name);
        if (result == null) {
            result = new I4GLFunction(callTarget);
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
        final I4GLFullParser parser = new I4GLFullParser(language, newFunctions);
        register(parser.getAllFunctions());
    }

    public I4GLFunction getFunction(String name) {
        return functionsObject.functions.get(name);
    }

    /**
     * Returns the sorted list of all functions, for printing purposes only.
     */
    public List<I4GLFunction> getFunctions() {
        List<I4GLFunction> result = new ArrayList<>(functionsObject.functions.values());
        Collections.sort(result, new Comparator<I4GLFunction>() {
            public int compare(I4GLFunction f1, I4GLFunction f2) {
                return f1.toString().compareTo(f2.toString());
            }
        });
        return result;
    }

    public TruffleObject getFunctionsObject() {
        return functionsObject;
    }

}