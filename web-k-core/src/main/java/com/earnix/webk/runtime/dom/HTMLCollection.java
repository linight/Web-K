package com.earnix.webk.runtime.dom;

import com.earnix.webk.runtime.web_idl.DOMString;
import com.earnix.webk.runtime.web_idl.Exposed;
import com.earnix.webk.runtime.web_idl.Getter;
import com.earnix.webk.runtime.web_idl.LegacyUnenumerableNamedProperties;
import com.earnix.webk.runtime.web_idl.Optional;
import com.earnix.webk.runtime.web_idl.ReadonlyAttribute;
import com.earnix.webk.runtime.web_idl.Unsigned;

/**
 * @author Taras Maslov
 * 6/19/2018
 */
@Exposed(Window.class)
public interface HTMLCollection extends LegacyUnenumerableNamedProperties {
    @Unsigned
    @ReadonlyAttribute
    int length();

    @Getter
    @Optional
    Element item(@Unsigned int index);

    @Getter
    @Optional
    Element namedItem(@DOMString String name);
}

