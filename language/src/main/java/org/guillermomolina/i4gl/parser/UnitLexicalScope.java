package org.guillermomolina.i4gl.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.statement.BlockNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Specialized {@link LexicalScope} for units. It differentiates between public and private identifiers.
 */
public class UnitLexicalScope extends LexicalScope {

    private final Set<String> publicIdentifiers;

    UnitLexicalScope(LexicalScope outer, String name, boolean usingTPExtension) {
        super(outer, name, usingTPExtension);
        this.publicIdentifiers = new HashSet<>();
    }

    @Override
    BlockNode createInitializationBlock() {
        VirtualFrame unitFrame = I4GLLanguage.INSTANCE.createUnitFrame(this.getName(), this.getFrameDescriptor());
        List<StatementNode> initializationNodes = this.generateInitializationNodes(unitFrame);
        initializationNodes.addAll(this.scopeInitializationNodes);

        return new BlockNode(initializationNodes.toArray(new StatementNode[initializationNodes.size()]));
    }

    @Override
    boolean containsPublicIdentifier(String identifier) {
        return this.publicIdentifiers.contains(identifier);
    }

    public void markAllIdentifiersPublic() {
        Map<String, TypeDescriptor> allIdentifiers = this.localIdentifiers.getAllIdentifiers();
        for (Map.Entry<String, TypeDescriptor> entry : allIdentifiers.entrySet()) {
            String currentIdentifier = entry.getKey();
            this.publicIdentifiers.add(currentIdentifier);
        }
    }

}
