package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.Collections;
import java.util.List;

public class StandardReferenceRefConstructor implements ASTConstructor {
    @Override
    public AST buildTree(Symbol current, List<AST> parsed) {
        if (current.getName().equals("Reference")) {
            return parseStandardReference(parsed);
        }
        throw new UnsupportedOperationException("Expected Reference Symbol, got: " + current.getName());
    }

    private Reference parseStandardReference(List<AST> parsed) {
        if (parsed.size() == 1) {
            DefaultAST decision = (DefaultAST) parsed.get(0);
            if (decision.getSymbol().getName().equals("thisOrId")) {
                return generateBaseRef(decision.getChildASTs().get(0));
            } else {
                throw new IllegalArgumentException("Expected Symbol thisOrId, got: " + decision.getSymbol().getName());
            }
        } else if (parsed.size() == 2) {
            AST first = parsed.get(0);
            if (!(first instanceof DefaultAST)) {
                first = new DefaultAST(first.posn, null, Collections.singletonList(first));
            }
            DefaultAST chainedIDRefs = (DefaultAST) parsed.get(1);
            if (!chainedIDRefs.getSymbol().getName().equals("dotIdChain")) {
                throw new IllegalArgumentException("Expected dotIdChain, got: " + chainedIDRefs.getSymbol().getName());
            }
            List<AST> childs = chainedIDRefs.getChildASTs();
            childs.add(0, first);
            return generateQualRef(childs);
        } else {
            throw new IllegalArgumentException("Expected Single Ref or Chained Ref, got: " + parsed);
        }
    }

    private Reference generateQualRef(List<AST> chainedIDs) {
        if (chainedIDs.size() == 1) {
            DefaultAST baseRef = (DefaultAST) chainedIDs.get(0);
            return generateBaseRef(baseRef.getChildASTs().get(0));
        }
        DefaultAST last = (DefaultAST) chainedIDs.get(chainedIDs.size() - 1);
        Identifier id = (Identifier) last.getChildASTs().get(0);
        return new QualRef(generateQualRef(chainedIDs.subList(1, chainedIDs.size())), id, id.posn);
    }

    private BaseRef generateBaseRef(AST thisOrId) {
        if (thisOrId instanceof Identifier) {
            return new IdRef((Identifier) thisOrId, thisOrId.posn);
        } else if (thisOrId instanceof ThisRef) {
            return (BaseRef) thisOrId;
        } else {
            throw new IllegalArgumentException("Expected This or Id, got: " + thisOrId.getClass());
        }
    }
}
