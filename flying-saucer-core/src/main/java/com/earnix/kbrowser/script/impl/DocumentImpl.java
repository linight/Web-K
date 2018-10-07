package com.earnix.kbrowser.script.impl;

import com.earnix.kbrowser.dom.nodes.TextNode;
import com.earnix.kbrowser.script.Binder;
import com.earnix.kbrowser.script.web_idl.DOMString;
import com.earnix.kbrowser.script.web_idl.USVString;
import com.earnix.kbrowser.script.whatwg_dom.Attr;
import com.earnix.kbrowser.script.whatwg_dom.CDATASection;
import com.earnix.kbrowser.script.whatwg_dom.Comment;
import com.earnix.kbrowser.script.whatwg_dom.DOMImplementation;
import com.earnix.kbrowser.script.whatwg_dom.Document;
import com.earnix.kbrowser.script.whatwg_dom.DocumentFragment;
import com.earnix.kbrowser.script.whatwg_dom.DocumentType;
import com.earnix.kbrowser.script.whatwg_dom.Element;
import com.earnix.kbrowser.script.whatwg_dom.Event;
import com.earnix.kbrowser.script.whatwg_dom.HTMLCollection;
import com.earnix.kbrowser.script.whatwg_dom.Node;
import com.earnix.kbrowser.script.whatwg_dom.NodeFilter;
import com.earnix.kbrowser.script.whatwg_dom.NodeIterator;
import com.earnix.kbrowser.script.whatwg_dom.NodeList;
import com.earnix.kbrowser.script.whatwg_dom.ProcessingInstruction;
import com.earnix.kbrowser.script.whatwg_dom.Range;
import com.earnix.kbrowser.script.whatwg_dom.Text;
import com.earnix.kbrowser.script.whatwg_dom.TreeWalker;
import com.earnix.kbrowser.script.whatwg_dom.impl.HTMLCollectionImpl;
import com.earnix.kbrowser.script.whatwg_dom.impl.TextImpl;
import com.earnix.kbrowser.swing.BasicPanel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Taras Maslov
 * 6/21/2018
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentImpl implements Document {

    com.earnix.kbrowser.dom.nodes.Document document;
    DOMImplementation implementation = new DOMImplementationImpl();

    protected BasicPanel panel;

    public DocumentImpl(BasicPanel panel) {
        this.document = panel.getDocument();
        this.panel = panel;
    }

    @Override
    public DOMImplementation implementation() {
        return implementation;
    }

    @Override
    public @USVString
    String URL() {
        return panel.getURL().toString();
    }

    @Override
    public @USVString
    String documentURI() {
        return panel.getURL().toString();
    }

    @Override
    public @USVString
    String origin() {
        return null;
    }

    @Override
    public @DOMString String compatMode() {
        return "CSS1Compat";
    }

    @Override
    public @DOMString String characterSet() {
        return "UTF-8";
    }

    @Override
    public @DOMString String charset() {
        return "UTF8";
    }

    @Override
    public @DOMString String inputEncoding() {
        return null;
    }

    @Override
    public @DOMString String contentType() {
        return null;
    }

    @Override
    public DocumentType doctype() {
        return null;
    }

    @Override
    public Element documentElement() {
        return (Element) Binder.get(document, panel);
    }

    @Override
    public HTMLCollection getElementsByTagName(String qualifiedName) {
        val elements = document.getElementsByTag(qualifiedName.toString());
        return new HTMLCollectionImpl(elements, panel);
    }

    @Override
    public HTMLCollection getElementsByTagNameNS(String namespace, String localName) {
        return getElementsByTagName(localName);
    }

    @Override
    public HTMLCollection getElementsByClassName(String classNames) {
        val modelElements = document.getElementsByClass(classNames.toString());
        return new HTMLCollectionImpl(modelElements, panel);
    }

    @Override
    public Element createElement(String localName, Object options) {
        return Binder.getElement(new com.earnix.kbrowser.dom.nodes.Element(localName), panel);
    }

    @Override
    public Element createElementNS(String namespace, String qualifiedName, Object options) {
        return createElement(qualifiedName, options);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return null;
    }

    @Override
    public Text createTextNode(String data) {
        TextNode textNode = new TextNode(data.toString());
        val result = new TextImpl(textNode, panel);
        Binder.put(textNode, result);
        return result;
    }

    @Override
    public CDATASection createCDATASection(String data) {
        return null;
    }

    @Override
    public Comment createComment(String data) {
        return null;
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return null;
    }

    @Override
    public Node importNode(Node node, boolean deep) {
        return null;
    }

    @Override
    public Node adoptNode(Node node) {
        return null;
    }

    @Override
    public Attr createAttribute(String localName) {
        return null;
    }

    @Override
    public Attr createAttributeNS(String namespace, String qualifiedName) {
        return null;
    }

    @Override
    public Event createEvent(String inter_face) {
        return null;
    }

    @Override
    public Range createRange() {
        return null;
    }

    @Override
    public NodeIterator createNodeIterator(Node root, long whatToShow, NodeFilter filter) {
        return null;
    }

    @Override
    public TreeWalker createTreeWalker(Node root, long whatToShow, NodeFilter filter) {
        return null;
    }

    @Override
    public Element getElementById(String elementId) {
        val jsoupEl = document.getElementById(elementId.toString());
        return (Element) Binder.get(jsoupEl, panel);
    }

    @Override
    public HTMLCollection children() {
        return null;
    }

    @Override
    public Element firstElementChild() {
        return null;
    }

    @Override
    public Element lastElementChild() {
        return null;
    }

    @Override
    public Integer childElementCount() {
        return document.children().size();
    }

    @Override
    public void prepend(Object... nodes) {

    }

    @Override
    public void append(Object... nodes) {

    }

    @Override
    public Element querySelector(String selectors) {
        val selected = document.select(selectors);
        if (selected.size() > 0) {
            Element bound = (Element) Binder.get(selected.first(), panel);
            if (bound == null) {
                bound = new ElementImpl(selected.first(), panel);
                Binder.put(selected.first(), bound);
            }

            return bound;
        }
        return null;
    }

    @Override
    public NodeList querySelectorAll(String selectors) {
        val selected = document.select(selectors);
        val nodes = new ArrayList<com.earnix.kbrowser.dom.nodes.Node>(selected);
        return new NodeListImpl(nodes, panel);
    }
}