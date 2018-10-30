package com.earnix.webk.dom.parser;

import com.earnix.webk.dom.helper.Validate;
import com.earnix.webk.dom.nodes.Attributes;
import com.earnix.webk.dom.nodes.DocumentModel;
import com.earnix.webk.dom.nodes.ElementModel;
import com.earnix.webk.dom.nodes.NodeModel;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Hedley
 */
abstract class TreeBuilder {
    protected Parser parser;
    CharacterReader reader;
    Tokeniser tokeniser;
    protected DocumentModel doc; // current doc we are building into
    protected ArrayList<ElementModel> stack; // the stack of open elements
    protected String baseUri; // current base uri, for creating new elements
    protected Token currentToken; // currentToken is used only for error tracking.
    protected ParseSettings settings;

    private Token.StartTag start = new Token.StartTag(); // start tag to process
    private Token.EndTag end = new Token.EndTag();

    abstract ParseSettings defaultSettings();

    protected void initialiseParse(Reader input, String baseUri, Parser parser) {
        Validate.notNull(input, "String input must not be null");
        Validate.notNull(baseUri, "BaseURI must not be null");

        doc = new DocumentModel(baseUri);
        doc.parser(parser);
        this.parser = parser;
        settings = parser.settings();
        reader = new CharacterReader(input);
        currentToken = null;
        tokeniser = new Tokeniser(reader, parser.getErrors());
        stack = new ArrayList<>(32);
        this.baseUri = baseUri;
    }

    DocumentModel parse(Reader input, String baseUri, Parser parser) {
        initialiseParse(input, baseUri, parser);
        runParser();
        return doc;
    }

    abstract List<NodeModel> parseFragment(String inputFragment, ElementModel context, String baseUri, Parser parser);

    protected void runParser() {
        while (true) {
            Token token = tokeniser.read();
            process(token);
            token.reset();

            if (token.type == Token.TokenType.EOF)
                break;
        }
    }

    protected abstract boolean process(Token token);

    protected boolean processStartTag(String name) {
        if (currentToken == start) { // don't recycle an in-use token
            return process(new Token.StartTag().name(name));
        }
        return process(start.reset().name(name));
    }

    public boolean processStartTag(String name, Attributes attrs) {
        if (currentToken == start) { // don't recycle an in-use token
            return process(new Token.StartTag().nameAttr(name, attrs));
        }
        start.reset();
        start.nameAttr(name, attrs);
        return process(start);
    }

    protected boolean processEndTag(String name) {
        if (currentToken == end) { // don't recycle an in-use token
            return process(new Token.EndTag().name(name));
        }
        return process(end.reset().name(name));
    }


    protected ElementModel currentElement() {
        int size = stack.size();
        return size > 0 ? stack.get(size - 1) : null;
    }
}