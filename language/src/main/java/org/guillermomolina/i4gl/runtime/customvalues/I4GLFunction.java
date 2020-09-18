package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;

/**
 * Representation of a function-type variable. In I4GL we may have variables which represent functions or procedures.
 * Our implementation of this type of variable is a slight wrapper to the function's {@link RootCallTarget}.
 */
@CompilerDirectives.ValueType
public class I4GLFunction {

	private RootCallTarget callTarget;

	public I4GLFunction(RootCallTarget rootCallTarget) {
		this.callTarget = rootCallTarget;
	}

	public I4GLFunction() {
		this(null);
	}

	public RootCallTarget getCallTarget() {
		return callTarget;
	}
	
}
