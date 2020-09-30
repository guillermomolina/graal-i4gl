package org.guillermomolina.i4gl.parser.types.compound;

public class Char1Descriptor extends CharDescriptor {
    private Char1Descriptor() {
        super(1);
    }

    private static Char1Descriptor instance = new Char1Descriptor();

    public static Char1Descriptor getInstance() {
        return instance;
    }  
}
