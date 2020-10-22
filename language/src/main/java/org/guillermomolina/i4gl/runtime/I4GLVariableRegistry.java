package org.guillermomolina.i4gl.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.interop.TruffleObject;

import org.guillermomolina.i4gl.runtime.values.I4GLNull;

/**
 * Manages the mapping from variable names to {@link Object variable objects}.
 */
public final class I4GLVariableRegistry {
    private final VariablesObject variablesObject = new VariablesObject();

    public Object lookup(String name, boolean createIfNotPresent) {
        Object result = variablesObject.variables.get(name);
        if (result == null && createIfNotPresent) {
            variablesObject.variables.put(name, I4GLNull.SINGLETON);
        }
        return result;
    }

    public void register(String name, Object value) {
        variablesObject.variables.put(name, value);
    }

    public void register(Map<String, Object> newVariables) {
        for (Map.Entry<String, Object> entry : newVariables.entrySet()) {
            register(entry.getKey(), entry.getValue());
        }
    }

    public Object getObject(String name) {
        return variablesObject.variables.get(name);
    }

    /**
     * Returns the sorted list of all variables, for printing purposes only.
     */
    public List<Object> getVariables() {
        List<Object> result = new ArrayList<>(variablesObject.variables.values());
        Collections.sort(result, new Comparator<Object>() {
            public int compare(Object f1, Object f2) {
                return f1.toString().compareTo(f2.toString());
            }
        });
        return result;
    }

    public TruffleObject getVariablesObject() {
        return variablesObject;
    }

}
