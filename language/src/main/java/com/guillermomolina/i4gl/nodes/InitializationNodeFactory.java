package com.guillermomolina.i4gl.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import com.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;

/**
 * Node factory for initialization nodes. Each variable has to be initialized before it is read. This factory creates
 * initialization node for given variable based on its type.
 */
public class InitializationNodeFactory {

    private InitializationNodeFactory() {
    }

    @SuppressWarnings("deprecation")
	public static I4GLStatementNode create(FrameSlot frameSlot, Object value, VirtualFrame frame) {
	    // 
        switch (frameSlot.getKind()) {
            case Int:
                int intValue;
                if(value instanceof Integer) {
                    intValue = (int) value;
                } else if (value instanceof Short) {
                    intValue = (short) value;
                } else {
                    throw new ClassCastException();
                }
                return (frame == null)?
                    new IntInitializationNode(frameSlot, intValue) : new IntInitializationWithFrameNode(frameSlot, intValue, frame);
            case Long: return (frame == null)?
                    new LongInitializationNode(frameSlot, (long) value) : new LongInitializationWithFrameNode(frameSlot, (long) value, frame);
            case Double: return (frame == null)?
                    new DoubleInitializationNode(frameSlot, (double) value) : new DoubleInitializationWithFrameNode(frameSlot, (double) value, frame);
            case Float: return (frame == null)?
                    new FloatInitializationNode(frameSlot, (float) value) : new FloatInitializationWithFrameNode(frameSlot, (float) value, frame);
            default: return (frame == null)?
                    new ObjectInitializationNode(frameSlot, value) : new ObjectInitializationWithFrameNode(frameSlot, value, frame);
        }
	}
}

/**
 * Base class for every initialization node.
 */
abstract class InitializationNode extends I4GLStatementNode {
	
	protected final FrameSlot slot;

	InitializationNode(FrameSlot slot) {
		this.slot = slot;
	}
    
    public FrameSlot getSlot() {
        return slot;
    }

	@Override
	public abstract void executeVoid(VirtualFrame frame);
}

/**
 * Initialization node for integer type variables. It looks for the variable in current frame.
 */
class IntInitializationNode extends InitializationNode {

    protected final int value;

    IntInitializationNode(FrameSlot slot, int value) {
        super(slot);
        this.value = value;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        frame.setInt(slot, value);
    }

}

/**
 * Initialization node for integer type variables. It looks for the variable the specified frame. It is used to
 * initialize variables inside a unit.
 */
class IntInitializationWithFrameNode extends IntInitializationNode {

    private final VirtualFrame frame;

    IntInitializationWithFrameNode(FrameSlot slot, int value, VirtualFrame frame) {
        super(slot, value);
        this.frame = frame;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        this.frame.setInt(slot, value);
    }

}

/**
 * Initialization node for long type variables. It looks for the variable the current frame.
 */
class LongInitializationNode extends InitializationNode {

	protected final long value;
	
	LongInitializationNode(FrameSlot slot, long value) {
		super(slot);
		this.value = value;
	}
	
	@Override
	public void executeVoid(VirtualFrame frame) {
        frame.setLong(slot, value);
	}

}

/**
 * Initialization node for long type variables. It looks for the variable the specified frame. It is used to
 * initialize variables inside a unit.
 */
class LongInitializationWithFrameNode extends LongInitializationNode {

    private final VirtualFrame frame;

    LongInitializationWithFrameNode(FrameSlot slot, long value, VirtualFrame frame) {
        super(slot, value);
        this.frame = frame;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        this.frame.setLong(slot, value);
    }

}

/**
 * Initialization node for float type variables. It looks for the variable the current frame.
 */
class FloatInitializationNode extends InitializationNode {

	protected final float value;
	
	FloatInitializationNode(FrameSlot slot, float value) {
		super(slot);
		this.value = value;
	}

    @Override
    public void executeVoid(VirtualFrame frame) {
        frame.setFloat(slot, value);
    }

}

/**
 * Initialization node for float type variables. It looks for the variable the specified frame. It is used to
 * initialize variables inside a unit.
 */
class FloatInitializationWithFrameNode extends FloatInitializationNode {

    private final VirtualFrame frame;

    FloatInitializationWithFrameNode(FrameSlot slot, float value, VirtualFrame frame) {
        super(slot, value);
        this.frame = frame;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        this.frame.setFloat(slot, value);
    }

}

/**
 * Initialization node for real type variables. It looks for the variable the current frame.
 */
class DoubleInitializationNode extends InitializationNode {

	protected final double value;
	
	DoubleInitializationNode(FrameSlot slot, double value) {
		super(slot);
		this.value = value;
	}

    @Override
    public void executeVoid(VirtualFrame frame) {
        frame.setDouble(slot, value);
    }

}

/**
 * Initialization node for real type variables. It looks for the variable the specified frame. It is used to
 * initialize variables inside a unit.
 */
class DoubleInitializationWithFrameNode extends DoubleInitializationNode {

    private final VirtualFrame frame;

    DoubleInitializationWithFrameNode(FrameSlot slot, double value, VirtualFrame frame) {
        super(slot, value);
        this.frame = frame;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        this.frame.setDouble(slot, value);
    }

}


/**
 * Initialization node for generic type variables. It looks for the variable the current frame.
 */
class ObjectInitializationNode extends InitializationNode {

	protected final Object value;
	
	ObjectInitializationNode(FrameSlot slot, Object value) {
		super(slot);
		this.value = value;
	}

    @Override
    public void executeVoid(VirtualFrame frame) {
        frame.setObject(slot, value);
    }

}

/**
 * Initialization node for generic type variables. It looks for the variable the specified frame. It is used to
 * initialize variables inside a unit.
 */
class ObjectInitializationWithFrameNode extends ObjectInitializationNode {

    private final VirtualFrame frame;

    ObjectInitializationWithFrameNode(FrameSlot slot, Object value, VirtualFrame frame) {
        super(slot, value);
        this.frame = frame;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        this.frame.setObject(slot, value);
    }

}