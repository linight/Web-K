package com.earnix.webk.runtime.dom;

import com.earnix.webk.runtime.web_idl.Callback;
import com.earnix.webk.runtime.web_idl.impl.WebIDLAdapter;

/**
 * @author Taras Maslov
 * 6/20/2018
 */
@Callback
public interface EventHandler {
    void call(WebIDLAdapter<Event> event);
}
