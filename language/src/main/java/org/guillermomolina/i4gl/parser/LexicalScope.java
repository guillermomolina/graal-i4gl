package org.guillermomolina.i4gl.parser;

import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.InitializationNodeFactory;
import org.guillermomolina.i4gl.nodes.statement.BlockNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * This class represents currently parsed lexical scope. It is a slight wrapper
 * of {@link IdentifiersTable} with some extended functionality. Lexical scope
 * of i4gl are scopes of subroutines.
 */
public class LexicalScope {
    private final LexicalScope outer;
    private final FrameDescriptor frameDescriptor;
    private final Map<String, FrameSlot> localIdentifiers;
    private final Map<String, TypeDescriptor> typeDescriptors;
    private final List<StatementNode> scopeInitializationNodes;

    /**
     * Default constructor.
     * 
     * @param outer instance of outer lexical scope
     * @param name  name of the current lexical scope
     */
    LexicalScope(LexicalScope outer) {
        this.outer = outer;
        this.frameDescriptor = new FrameDescriptor();
        this.scopeInitializationNodes = new ArrayList<>();
        localIdentifiers = new HashMap<>();
        typeDescriptors = new HashMap<>();
    }

    LexicalScope getOuterScope() {
        return outer;
    }

    public FrameDescriptor getFrameDescriptor() {
        return frameDescriptor;
    }

    void registerVariable(String name, TypeDescriptor typeDescriptor) {
        registerArgument(name, null, typeDescriptor);
    }

    void registerArgument(String name, Integer argumentIndex, TypeDescriptor typeDescriptor) {
        FrameSlot frameSlot = frameDescriptor.addFrameSlot(name, argumentIndex, typeDescriptor.getSlotKind());
        localIdentifiers.put(name, frameSlot);
        typeDescriptors.put(name, typeDescriptor);
    }

    /**
     * Adds initialization scope for this lexical scope. These nodes are prepended
     * to the main block's tree of the subroutine this scope represents. They are
     * required to initialize values of each local variable of the scope.
     * 
     * @param initializationNode the new initialization node
     */
    void addScopeInitializationNode(StatementNode initializationNode) {
        scopeInitializationNodes.add(initializationNode);
    }

    /**
     * Creates a {@link BlockNode} containing each initialization node of the current scope and returns it.
     */
    BlockNode createInitializationBlock() {
        List<StatementNode> initializationNodes = this.generateInitializationNodes(null);
        initializationNodes.addAll(this.scopeInitializationNodes);

        return new BlockNode(initializationNodes.toArray(new StatementNode[initializationNodes.size()]));
    }

    /**
     * Generates initialization node for each declared identifier in the current
     * scope and returns list of these nodes.
     * 
     * @param frame frame of the scope (used in scopes of units)
     */
    List<StatementNode> generateInitializationNodes(VirtualFrame frame) {
        List<StatementNode> initializationNodes = new ArrayList<>();

        for (Map.Entry<String, FrameSlot> entry : localIdentifiers.entrySet()) {
            FrameSlot frameSlot = entry.getValue();
            TypeDescriptor typeDescriptor = typeDescriptors.get(entry.getKey());
            StatementNode initializationNode = createInitializationNode(frameSlot, typeDescriptor, frame);
            if (initializationNode != null) {
                initializationNodes.add(initializationNode);
            }
        }

        return initializationNodes;
    }

    private StatementNode createInitializationNode(FrameSlot frameSlot, TypeDescriptor typeDescriptor,
            VirtualFrame frame) {
        Object defaultValue = typeDescriptor.getDefaultValue();
        if (defaultValue == null) {
            return null;
        }

        return InitializationNodeFactory.create(frameSlot, defaultValue, frame);
    }
}