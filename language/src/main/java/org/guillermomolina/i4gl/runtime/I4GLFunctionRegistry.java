package org.guillermomolina.i4gl.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.source.Source;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.parser.I4GLFullParser;

/**
 * Manages the mapping from function names to {@link I4GLFunction function objects}.
 */
public final class I4GLFunctionRegistry {

    private final I4GLLanguage language;
    private final FunctionsObject functionsObject = new FunctionsObject();

    public I4GLFunctionRegistry(I4GLLanguage language) {
        this.language = language;
    }

    /**
     * Returns the canonical {@link I4GLFunction} object for the given name. If it does not exist yet,
     * it is created.
     */
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
    public I4GLFunction register(String name, RootCallTarget callTarget) {
        I4GLFunction function = lookup(name, true);
        function.setCallTarget(callTarget);
        return function;
    }

    public void register(Map<String, RootCallTarget> newFunctions) {
        for (Map.Entry<String, RootCallTarget> entry : newFunctions.entrySet()) {
            register(entry.getKey(), entry.getValue());
        }
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
