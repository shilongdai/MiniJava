package net.viperfish.minijava.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TokenScanner {

    private static final Map<String, TokenType> KEYWORDS;

    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("class", TokenType.CLASS);
        KEYWORDS.put("void", TokenType.VOID);
        KEYWORDS.put("public", TokenType.PUBLIC);
        KEYWORDS.put("private", TokenType.PRIVATE);
        KEYWORDS.put("static", TokenType.STATIC);
        KEYWORDS.put("int", TokenType.INT);
        KEYWORDS.put("boolean", TokenType.BOOLEAN);
        KEYWORDS.put("this", TokenType.THIS);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("new", TokenType.NEW);
        KEYWORDS.put("null", TokenType.NULL);
    }

    private PushbackInputStream input;
    private int position;
    private int lineNumber;
    private boolean eotReached;

    public TokenScanner(InputStream input) {
        this.input = new PushbackInputStream(input);
        position = -1;
        lineNumber = 1;
        eotReached = false;
    }

    public boolean hasNext() throws IOException {
        return !eotReached;
    }

    public Token nextToken() throws IOException, ParsingException {
        int next = input.read();
        position += 1;

        while (next != -1) {
            if(next == '\r') {
                next = input.read();
                position += 1;
                continue;
            }
            if(next == '\n') {
                lineNumber += 1;
                next = input.read();
                position += 1;
                continue;
            }
            if (next == '\t' || next == ' ') {
                next = input.read();
                position += 1;
                continue;
            }
            if (!StandardCharsets.US_ASCII.newEncoder().canEncode((char) next)) {
                throw new ParsingException(String.format("Unrecognized symbol: %c", (char) next), new SourcePosition(position, lineNumber));
            }
            if (next == '/') {
                int lookAhead = input.read();
                position += 1;
                if (lookAhead == '/') {
                    position += skipLine(input);
                } else if (lookAhead == '*') {
                    int delta = skipBlockComment(input);
                    if (delta == -1) {
                        throw new ParsingException("Block comment does not terminate", new SourcePosition(position, lineNumber));
                    }
                    position += delta;
                } else {
                    input.unread(lookAhead);
                    position = position - 1;
                    return new Token(TokenType.OPERATOR, "/", new SourcePosition(position, lineNumber));
                }
            } else if (Character.isLetter(next)) {
                SourcePosition pos = new SourcePosition(position, lineNumber);
                String segment = readAlphaNumeric(input);
                position += segment.length();
                StringBuilder spelling = new StringBuilder();
                spelling.append((char) next).append(segment);
                return new Token(KEYWORDS.getOrDefault(spelling.toString(), TokenType.ID), spelling.toString(), pos);
            } else if (next == '{') {
                return new Token(TokenType.LEFT_LARGE_BRACKET, "{", new SourcePosition(position, lineNumber));
            } else if (next == '}') {
                return new Token(TokenType.RIGHT_LARGE_BRACKET, "}", new SourcePosition(position, lineNumber));
            } else if (next == ';') {
                return new Token(TokenType.SEMI_COLON, ";", new SourcePosition(position, lineNumber));
            } else if (next == '(') {
                return new Token(TokenType.LEFT_PARANTHESIS, "(", new SourcePosition(position, lineNumber));
            } else if (next == ')') {
                return new Token(TokenType.RIGHT_PARANTHESIS, ")", new SourcePosition(position, lineNumber));
            } else if (next == '[') {
                return new Token(TokenType.LEFT_SQ_BRACKET, "[", new SourcePosition(position, lineNumber));
            } else if (next == ']') {
                return new Token(TokenType.RIGHT_SQ_BRACKET, "]", new SourcePosition(position, lineNumber));
            } else if (next == ',') {
                return new Token(TokenType.COMMA, ",", new SourcePosition(position, lineNumber));
            } else if (next == '.') {
                return new Token(TokenType.DOT, ".", new SourcePosition(position, lineNumber));
            } else if (Character.isDigit(next)) {
                SourcePosition pos = new SourcePosition(position, lineNumber);
                String digits = readDigits(input);
                position += digits.length();
                StringBuilder spelling = new StringBuilder();
                spelling.append((char) next).append(digits);
                return new Token(TokenType.NUM, spelling.toString(), pos);
            } else if (next == '>' || next == '<' || next == '=') {
                int lookAhead = input.read();
                position += 1;
                if (lookAhead == '=') {
                    return new Token(TokenType.OPERATOR, ((char) next) + "=", new SourcePosition(position, lineNumber));
                } else {
                    input.unread(lookAhead);
                    position -= 1;
                    TokenType type = TokenType.OPERATOR;
                    if (next == '=') {
                        type = TokenType.EQ;
                    }
                    return new Token(type, String.valueOf((char) next), new SourcePosition(position, lineNumber));
                }
            } else if (next == '!') {
                int lookAhead = input.read();
                position += 1;
                if (lookAhead == '=') {
                    return new Token(TokenType.OPERATOR, "!=", new SourcePosition(position, lineNumber));
                } else {
                    input.unread(lookAhead);
                    position -= 1;
                    return new Token(TokenType.OPERATOR, "!", new SourcePosition(position, lineNumber));
                }
            } else if (next == '&' || next == '|') {
                int lookAhead = input.read();
                position += 1;
                if (lookAhead == next) {
                    return new Token(TokenType.OPERATOR, String.valueOf((char) lookAhead) + (char) lookAhead, new SourcePosition(position, lineNumber));
                } else {
                    input.unread(lookAhead);
                    position -= 1;
                    throw new ParsingException(String.format("Unrecognized symbol: %c", (char) next), new SourcePosition(position, lineNumber));
                }
            } else if (Arrays.asList('+', '-', '*').contains((char) next)) {
                return new Token(TokenType.OPERATOR, String.valueOf((char) next), new SourcePosition(position, lineNumber));
            } else {
                throw new ParsingException(String.format("Unrecognized symbol: %c", (char) next), new SourcePosition(position, lineNumber));
            }
            next = input.read();
            position += 1;
        }
        eotReached = true;

        SourcePosition position = new SourcePosition(this.position, lineNumber);
        return new Token(TokenType.EOT, "", position);
    }

    private int skipLine(InputStream inputStream) throws IOException {
        int next = inputStream.read();
        int position = 1;
        while (next != -1 && next != '\n' && next != '\r') {
            next = inputStream.read();
            position += 1;
        }
        if(next == '\n' || next == '\r') {
            lineNumber += 1;
        }
        return position;
    }

    private int skipBlockComment(InputStream inputStream) throws IOException {
        StringBuilder slider = new StringBuilder("$");
        int next = input.read();
        int position = 1;
        if (next == -1) {
            return -1;
        }
        if(next == '\n' || next == '\r') {
            lineNumber += 1;
        }
        slider.append((char) next);

        while (next != -1) {
            if (slider.toString().equals("*/")) {
                return position;
            }

            next = inputStream.read();
            slider.append((char) next);
            slider.deleteCharAt(0);
            position += 1;
            if(next == '\n' || next == '\r') {
                lineNumber += 1;
            }
        }
        return -1;
    }

    private String readAlphaNumeric(PushbackInputStream input) throws IOException {
        StringBuilder result = new StringBuilder();

        int next = input.read();
        while (next != -1) {
            if (Character.isLetterOrDigit(next) || next == '_') {
                result.append((char) next);
            } else {
                input.unread(next);
                break;
            }
            next = input.read();
        }
        return result.toString();
    }

    private String readDigits(PushbackInputStream input) throws IOException {
        StringBuilder result = new StringBuilder();

        int next = input.read();
        while (next != -1) {
            if (Character.isDigit(next)) {
                result.append((char) next);
            } else {
                input.unread(next);
                break;
            }
            next = input.read();
        }
        return result.toString();
    }

}
