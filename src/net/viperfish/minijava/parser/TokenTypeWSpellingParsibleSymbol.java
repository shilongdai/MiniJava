package net.viperfish.minijava.parser;

import net.viperfish.minijava.scanner.Token;
import net.viperfish.minijava.scanner.TokenType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;


public class TokenTypeWSpellingParsibleSymbol extends TokenTypeParsibleSymbol {

    private Collection<String> acceptableSpelling;

    public TokenTypeWSpellingParsibleSymbol(TokenType type, Collection<String> acceptableSpelling) {
        super(type);
        this.acceptableSpelling = new HashSet<>(acceptableSpelling);
    }

    @Override
    public boolean isInstance(Token token) {
        return super.isInstance(token) && this.acceptableSpelling.contains(token.getSpelling());
    }

    @Override
    public String getName() {
        return super.getName() + acceptableSpelling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TokenTypeWSpellingParsibleSymbol that = (TokenTypeWSpellingParsibleSymbol) o;
        return Objects.equals(acceptableSpelling, that.acceptableSpelling);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), acceptableSpelling);
    }
}
