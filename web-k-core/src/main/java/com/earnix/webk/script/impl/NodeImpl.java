package com.earnix.webk.script.impl;

import com.earnix.webk.dom.nodes.ElementModel;
import com.earnix.webk.dom.nodes.NodeModel;
import com.earnix.webk.script.Binder;
import com.earnix.webk.script.web_idl.Attribute;
import com.earnix.webk.script.web_idl.DOMString;
import com.earnix.webk.script.whatwg_dom.Document;
import com.earnix.webk.script.whatwg_dom.Event;
import com.earnix.webk.script.whatwg_dom.EventListener;
import com.earnix.webk.script.whatwg_dom.GetRootNodeOptions;
import com.earnix.webk.script.whatwg_dom.Node;
import com.earnix.webk.script.whatwg_dom.NodeList;
import com.earnix.webk.script.whatwg_dom.impl.EventImpl;
import com.earnix.webk.swing.BasicPanel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Taras Maslov
 * 7/13/2018
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class NodeImpl implements Node {

    NodeModel target;
    BasicPanel panel;

    public NodeImpl(NodeModel target, BasicPanel panel) {
        this.target = target;
        this.panel = panel;
    }

    @Override
    public short nodeType() {
        return 0;
    }

    @Override
    public String nodeName() {
        return target.nodeName();
    }

    @Override
    public String baseURI() {
        return panel.getURL().toString();
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Document ownerDocument() {
        return (Document) Binder.get(target.ownerDocument(), panel);
    }

    @Override
    public Node getRootNode(GetRootNodeOptions options) {
        return Binder.get(target.root(), panel);
    }

    @Override
    public Node parentNode() {
        return Binder.get(target.parentNode(), panel);
    }

    @Override
    public com.earnix.webk.script.whatwg_dom.Element parentElement() {
        val modelParent = target.parent();
        if (modelParent instanceof ElementModel) {
            return Binder.getElement((ElementModel) modelParent, panel);
        }
        return null;
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public NodeList childNodes() {
        return new NodeListImpl(target.childNodes(), panel);
    }

    @Override
    public Node firstChild() {
        return null;
    }

    @Override
    public Node lastChild() {
        return null;
    }

    @Override
    public Node previousSibling() {
        return null;
    }

    @Override
    public Node nextSibling() {
        return null;
    }

    @Override
    public Attribute<String> nodeValue() {
        return null;
    }

    @Override
    public Attribute<String> textContent() {
        return null;
    }

    @Override
    public void normalize() {

    }

    @Override
    public Node cloneNode(boolean deep) {
        return null;
    }

    @Override
    public boolean isEqualNode(Node otherNode) {
        return false;
    }

    @Override
    public boolean isSameNode(Node otherNode) {
        return false;
    }

    @Override
    public short compareDocumentPosition(Node other) {
        return 0;
    }

    @Override
    public boolean contains(Node other) {
        return false;
    }

    @Override
    public @DOMString String lookupPrefix(@DOMString String namespace) {
        return null;
    }

    @Override
    public @DOMString String lookupNamespaceURI(@DOMString String prefix) {
        return null;
    }

    @Override
    public boolean isDefaultNamespace(@DOMString String namespace) {
        return false;
    }

    @Override
    public Node insertBefore(Node node, Node child) {
        return null;
    }

    @Override
    public Node appendChild(Node node) {
        NodeImpl impl = (NodeImpl) node;
        ((ElementModel) target).appendChild(impl.target);
        return node;
    }

    @Override
    public Node replaceChild(Node node, Node child) {
        return null;
    }

    @Override
    public Node removeChild(Node child) {
        return null;
    }

    // region EventTarget

    private LinkedHashMap<String, List<EventListener>> listeners = new LinkedHashMap<>();

    @Override
    public void addEventListener(String type, EventListener callback, Object options) { //callback is NashorJavaAdapter here, check if any adaptaion needed
        var typeListeners = listeners.computeIfAbsent(type, k -> new ArrayList<>());
        typeListeners.add(callback);

        // workaround for ChartJS
        if (type.equals("animationstart")) {
            dispatchEvent(new EventImpl("animationstart", null));
        }
    }

    @Override
    public void removeEventListener(@DOMString String type, EventListener callback, Object options) {
        val typeListeners = listeners.get(type);
        if (typeListeners != null) {
            typeListeners.remove(callback);
            if (typeListeners.isEmpty()) {
                listeners.remove(type);
            }
        }
    }

    @Override
    public boolean dispatchEvent(Event event) {
        val typeListeners = listeners.get(event.type());
        if (typeListeners != null) {
            typeListeners.forEach(l -> l.handleEvent(event));
            return true;
        }
        return false;
    }


    // endregion
}