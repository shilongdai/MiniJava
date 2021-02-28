package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

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
            return generateBaseRef(parsed.get(0));
        } else if (parsed.size() == 2) {
            AST first = parsed.get(0);
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
            return generateBaseRef(chainedIDs.get(0));
        }
        Identifier last = (Identifier) chainedIDs.get(chainedIDs.size() - 1);
        return new QualRef(generateQualRef(chainedIDs.subList(0, chainedIDs.size() - 1)), last, last.posn);
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
