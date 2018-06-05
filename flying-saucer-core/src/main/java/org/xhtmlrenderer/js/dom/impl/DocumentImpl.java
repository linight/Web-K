package org.xhtmlrenderer.js.dom.impl;

import lombok.AllArgsConstructor;
import org.xhtmlrenderer.js.dom.*;
import org.xhtmlrenderer.js.web_idl.Attribute;

/**
 * @author Taras Maslov
 * 6/1/2018
 */
@AllArgsConstructor
public class DocumentImpl implements Document {
    
    private org.w3c.dom.Document target;

    @Override
    public Attribute<DocumentType> doctype() {
        return Attribute.<DocumentType>readOnly().give(() -> 
                new DocumentTypeImpl(target.getDoctype())
        );
    }

    @Override
    public Attribute<DOMImplementation> implementation() {
        return Attribute.<DOMImplementation>readOnly().give(() -> new DOMImplementationImpl(target.getImplementation()));
    }

    @Override
    public Attribute<Element> documentElement() {
        return Attribute.<Element>readOnly().give(() -> ElementImpl.create(target.getDocumentElement()));
    }

    @Override
    public Element createElement(DOMString tagName) throws DOMException {
        return ElementImpl.create(target.createElement("tagName"));
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return new DocumentFragmentImpl(target.createDocumentFragment());
    }

    @Override
    public Text createTextNode(DOMString data) {
        return new TextImpl(target.createTextNode(data.toString()));
    }

    @Override
    public Comment createComment(DOMString data) {
        return null;
    }

    @Override
    public CDATASection createCDATASection(DOMString data) throws DOMException {
        return null;
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(DOMString target, DOMString data) throws DOMException {
        return null;
    }

    @Override
    public Attr createAttribute(DOMString name) throws DOMException {
        return null;
    }

    @Override
    public EntityReference createEntityReference(DOMString name) throws DOMException {
        return null;
    }

    @Override
    public NodeList getElementsByTagName(DOMString tagname) {
        return new NodeListImpl(target.getElementsByTagName(tagname.toString()));
    }

    @Override
    public Node importNode(Node importedNode, boolean deep) {
        return null;
    }

    @Override
    public Element createElementNS(DOMString namespaceURI, DOMString qualifiedName) throws DOMException {
        return null;
    }

    @Override
    public Attr createAttributeNS(DOMString namespaceURI, DOMString qualifiedName) throws DOMException {
        return null;
    }

    @Override
    public NodeList getElementsByTagNameNS(DOMString namespaceURI, DOMString localName) {
        return null;
    }

    @Override
    public Element getElementById(DOMString elementId) {
        return ElementImpl.create(target.getElementById(elementId.toString()));
    }

    @Override
    public Attribute<DOMString> inputEncoding() {
        return null;
    }

    @Override
    public Attribute<DOMString> xmlEncoding() {
        return null;
    }

    @Override
    public Attribute<Boolean> xmlStandalone() {
        return null;
    }

    @Override
    public Attribute<DOMString> xmlVersion() {
        return null;
    }

    @Override
    public Attribute<Boolean> strictErrorChecking() {
        return null;
    }

    @Override
    public Attribute<DOMString> documentURI() {
        return null;
    }

    @Override
    public Node adoptNode(Node source) throws DOMException {
        return null;
    }

    @Override
    public Attribute<DOMConfiguration> domConfig() {
        return null;
    }

    @Override
    public void normalizeDocument() {

    }

    @Override
    public Node renameNode(Node n, DOMString namespaceURI, DOMString qualifiedName) throws DOMException {
        return null;
    }
}