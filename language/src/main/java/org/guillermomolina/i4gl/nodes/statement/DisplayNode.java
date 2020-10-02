/*
 * Copyright (c) 2012, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.guillermomolina.i4gl.nodes.statement;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.NullValue;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

@NodeInfo(shortName = "DISPLAY", description = "The node implementing the DISPLAY statement")
public final class DisplayNode extends I4GLStatementNode {

    @Children
    private final ExpressionNode[] argumentNodes;
    @Child
    private InteropLibrary interop;

    public DisplayNode(ExpressionNode[] argumentNodes) {
        this.argumentNodes = argumentNodes;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        for (int i = 0; i < argumentNodes.length; i++) {
            Object value = argumentNodes[i].executeGeneric(frame);
            if (value instanceof Integer) {
                System.out.printf("%11d", (int)value);
            } else if (value instanceof Long) {
                System.out.printf("%20d", (long)value);
            } else if (value instanceof Float) {
                System.out.printf("%14.2f", (float)value);
            } else if (value instanceof Double) {
                System.out.printf("%14.2f", (double)value);
            } else if (value == NullValue.SINGLETON) {
                TypeDescriptor type = argumentNodes[i].getType();
                if (type instanceof TextDescriptor) {
                    System.out.print(type.getDefaultValue());
                } else {
                    throw new I4GLRuntimeException("Variable is NULL and is not a String");
                }
            } else {       
                System.out.print(value.toString());
            }
        }
        System.out.printf("%n");
    }
}


