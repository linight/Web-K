package com.earnix.webk.runtime.html;

import com.earnix.webk.runtime.future.Promise;
import com.earnix.webk.runtime.web_idl.CEReactions;
import com.earnix.webk.runtime.web_idl.DOMString;
import com.earnix.webk.runtime.web_idl.Exposed;
import com.earnix.webk.runtime.web_idl.Optional;
import com.earnix.webk.runtime.dom.Node;

/**
 * @author Taras Maslov
 * 10/31/2018
 */
@Exposed(Window.class)
public interface CustomElementRegistry {
    @CEReactions
    void define(@DOMString String name, CustomElementConstructor constructor, @Optional ElementDefinitionOptions options);

    Object get(@DOMString String name);

    Promise<Void> whenDefined(DOMString name);

    @CEReactions
    void upgrade(Node root);
}
