package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;

/**
 * Representation of a subroutine-type variable. In I4GL we may have variables which represent functions or procedures.
 * Our implementation of this type of variable is a slight wrapper to the subroutine's {@link RootCallTarget}.
 */
@CompilerDirectives.ValueType
public class I4GLSubroutine {

	private RootCallTarget callTarget;

	public I4GLSubroutine(RootCallTarget rootCallTarget) {
		this.callTarget = rootCallTarget;
	}

	public I4GLSubroutine() {
		this(null);
	}

	public RootCallTarget getCallTarget() {
		return callTarget;
	}
	
}
