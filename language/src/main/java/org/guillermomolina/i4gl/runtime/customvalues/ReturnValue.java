package org.guillermomolina.i4gl.runtime.customvalues;

import java.util.List;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Representation of record-type variables. It contains its own frame where its content is stored and a descriptor of
 * its own type.
 */
@CompilerDirectives.ValueType
public class ReturnValue {

    private final VirtualFrame frame;
    private final FrameDescriptor frameDescriptor;

    /**
     * The default constructor.
     * @param frameDescriptor frame descriptor from which the record's frame will be created
     * @param types map of the record's identifiers and their types
     */
    // TODO: can't the frame descriptor be created from the second argument?
    public ReturnValue(final FrameDescriptor frameDescriptor, final List<TypeDescriptor> types) {
        this(frameDescriptor);
        this.initValues(frameDescriptor, types);
    }

    private ReturnValue(final FrameDescriptor frameDescriptor) {
        this.frameDescriptor = frameDescriptor;
        this.frame = Truffle.getRuntime().createVirtualFrame(new Object[frameDescriptor.getSize()], frameDescriptor);
    }

    private void initValues(final FrameDescriptor frameDescriptor, final List<TypeDescriptor> types) {
        int i = 0;
        for (final FrameSlot slot : frameDescriptor.getSlots()) {
            final TypeDescriptor slotsType = types.get(i++);
            this.initValue(slot, slotsType);
        }
    }

    private void initValue(final FrameSlot slot, final TypeDescriptor descriptor) {
        this.setValue(slot, descriptor.getDefaultValue());
    }

    @SuppressWarnings("deprecation")
    private void setValue(final FrameSlot slot, final Object value) {
        // TODO: whole initialization process is weird
        // this switch is surely a duplicity and is also somewhere else in the code
        switch (slot.getKind()) {
            case Int: frame.setInt(slot, (int) value); break;
            case Long: frame.setLong(slot, (long) value); break;
            case Float: frame.setFloat(slot, (float) value); break;
            case Double: frame.setDouble(slot, (double) value); break;
            case Byte: frame.setByte(slot, (byte) (char) value); break;
            case Object: frame.setObject(slot, value); break;
            default:
        }
    }

    @SuppressWarnings("deprecation")
    private void copySlotValue(final VirtualFrame fromFrame, final VirtualFrame toFrame, final FrameSlot slot)
            throws FrameSlotTypeException {
        // TODO: this switch is surely a duplicity and is also somewhere else in the
        // code
        switch (slot.getKind()) {
            case Int:
                toFrame.setInt(slot, fromFrame.getInt(slot));
                break;
            case Long:
                toFrame.setLong(slot, fromFrame.getLong(slot));
                break;
            case Float:
                toFrame.setFloat(slot, fromFrame.getFloat(slot));
                break;
            case Double:
                toFrame.setDouble(slot, fromFrame.getDouble(slot));
                break;
            case Byte:
                toFrame.setByte(slot, fromFrame.getByte(slot));
                break;
            case Object:
                toFrame.setObject(slot, fromFrame.getObject(slot));
                break;
            default:
        }
    }

    public FrameSlot getSlot(final String identifier) {
        return this.frameDescriptor.findFrameSlot(identifier);
    }

    public VirtualFrame getFrame() {
        return this.frame;
    }

    /**
     * Creates a deep copy of the record.
     */
    public ReturnValue getCopy() {
        final ReturnValue copy = new ReturnValue(this.frameDescriptor);
        for (final FrameSlot slot : this.frameDescriptor.getSlots()) {
            try {
                this.copySlotValue(this.frame, copy.frame, slot);
            } catch (final FrameSlotTypeException e) {
                throw new I4GLRuntimeException("Unexpected type in record copying");
            }
        }

        return copy;
    }

}
