package org.guillermomolina.i4gl.runtime.heap;

/**
 * Represents a slot in the heap. It stores an address in {@link I4GLHeap}. The address must be stored in this wrapper,
 * because objects can change their actual address in the heap thanks to the garbage collecting process.
 */
public class HeapSlot {

    /**
     * Index to memory.
     */
    private int memoryIndex;

    HeapSlot(int memoryIndex) {
        this.memoryIndex = memoryIndex;
    }

    @Override
    public int hashCode() {
        return this.memoryIndex;
    }

    int getMemoryIndex() {
        return this.memoryIndex;
    }

    void setMemoryIndex(int memoryIndex) {
        this.memoryIndex = memoryIndex;
    }

}
