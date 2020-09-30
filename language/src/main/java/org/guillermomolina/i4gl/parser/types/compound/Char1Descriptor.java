package org.guillermomolina.i4gl.parser.types.compound;

public class Char1Descriptor extends CharDescriptor {
    private Char1Descriptor() {
        super(1);
    }

    public static final Char1Descriptor SINGLETON = new Char1Descriptor();
}
