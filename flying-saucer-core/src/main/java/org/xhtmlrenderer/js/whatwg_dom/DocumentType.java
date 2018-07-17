package org.xhtmlrenderer.js.whatwg_dom;

import org.xhtmlrenderer.js.web_idl.DOMString;
import org.xhtmlrenderer.js.web_idl.Exposed;
import org.xhtmlrenderer.js.web_idl.ReadonlyAttribute;

/**
 * @author Taras Maslov
 * 6/21/2018
 */
@Exposed(Window.class)
public interface DocumentType extends Node {
    
    @ReadonlyAttribute
    DOMString name();

    @ReadonlyAttribute
    DOMString publicId();

    @ReadonlyAttribute
    DOMString systemId();
    
}