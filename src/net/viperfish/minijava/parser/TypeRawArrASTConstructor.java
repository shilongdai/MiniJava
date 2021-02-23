package net.viperfish.minijava.parser;

import net.viperfish.minijava.ast.*;
import net.viperfish.minijava.ebnf.Symbol;

import java.util.List;

public class TypeRawArrASTConstructor implements ASTConstructor {

    @Override
    public TypeDenoter buildTree(Symbol current, List<AST> parsed) {
       if(!current.getName().equals("IntRelatedType") && !current.getName().equals("UserRelatedType")) {
           throw new IllegalArgumentException("Expected IntRelatedType or UserRelatedType, got: " + current.getName());
       }

       if(parsed.size() == 1) {
           AST raw = parsed.get(0);
           return getRawType(raw);
       } else if(parsed.size() == 2) {
           TypeDenoter raw = getRawType(parsed.get(0));
           DefaultAST arr = (DefaultAST) parsed.get(1);
           if(arr.getSymbol().getName().equals("sqBrackets")) {
               return new ArrayType(raw, raw.posn);
           } else {
               throw new IllegalArgumentException("Expected sqBrackets, got: " + arr.getSymbol().getName());
           }
       } else {
           throw new IllegalArgumentException("Expected raw type or array, got: " + parsed);
       }
    }

    private TypeDenoter getRawType(AST raw) {
        if(raw instanceof Identifier) {
            return new ClassType((Identifier) raw, raw.posn);
        } else if(raw instanceof BaseType) {
            return (TypeDenoter) raw;
        } else {
            throw new IllegalArgumentException("Expected raw type, got: " + raw);
        }
    }

}
