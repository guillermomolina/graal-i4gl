package org.guillermomolina.i4gl.parser.types.compound;

public class Char1Descriptor extends CharDescriptor {

    public static final Char1Descriptor SINGLETON = new Char1Descriptor();

    private Char1Descriptor() {
        super(1);
    }
}
