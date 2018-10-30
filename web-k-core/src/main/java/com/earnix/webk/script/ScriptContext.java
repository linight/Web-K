package com.earnix.webk.script;

import com.earnix.webk.dom.nodes.DocumentModel;
import com.earnix.webk.event.DocumentListener;
import com.earnix.webk.script.html.canvas.impl.CanvasGradientImpl;
import com.earnix.webk.script.html.canvas.impl.CanvasPatternImpl;
import com.earnix.webk.script.impl.ElementImpl;
import com.earnix.webk.script.web_idl.Exposed;
import com.earnix.webk.script.whatwg_dom.css_style_attribute.CSSStyleAttribute;
import com.earnix.webk.script.whatwg_dom.impl.EventManager;
import com.earnix.webk.swing.BasicPanel;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptException;
import javax.swing.JOptionPane;
import java.util.Timer;
import java.util.TimerTask;

import static javax.script.ScriptContext.ENGINE_SCOPE;

/**
 * @author Taras Maslov
 * 5/21/2018
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScriptContext implements DocumentListener {
    //    static ScriptContext instance;
    NashornScriptEngine engine;
    javax.script.ScriptContext context;

    BasicPanel panel;
    JsConsole console = new JsConsole();
    DocumentModel document;
    EventManager eventManager = new EventManager(this);

    public ScriptContext(BasicPanel panel) {
        this.panel = panel;
        
        panel.addMouseTrackingListener(new MouseEventsAdapter(eventManager));
    }

    public void onload() {
        eval("window.onload && window.onload()");
    }

    public BasicPanel getPanel() {
        return panel;
    }

    public Object getWindow() {
        try {
            return engine.eval("this");
        } catch (ScriptException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void documentStarted() {
    }

    @Override
    public void documentLoaded() {
        handleNewDocument();
    }

    @Override
    public void onLayoutException(Throwable t) {
    }

    @Override
    public void onRenderException(Throwable t) {
    }

    private void initEngine() {
        String[] options = {"--language=es6"};
        NashornScriptEngineFactory jsFactory = new NashornScriptEngineFactory();
        engine = (NashornScriptEngine) jsFactory.getScriptEngine(options);
        context = engine.getContext();
        context.setAttribute("document", WebIDLAdapter.obtain(this, new com.earnix.webk.script.html.impl.DocumentImpl(panel)), ENGINE_SCOPE);
        context.setAttribute("console", console, ENGINE_SCOPE);
        context.setAttribute("setInterval", new Function<>(this, (ctx, args) -> {
            val fn = (JSObject) args[0];
            double interval = (double) ScriptUtils.convert(args[1], Double.class);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    fn.call(ctx);
                    panel.relayout();
                }
            }, 0, (long) interval);
            return null;
        }, "setInterval"), ENGINE_SCOPE);

        context.setAttribute("setTimeout", new Function<>(this, (ctx, arg) -> {
            val fn = (JSObject) arg[0];
            double timeout = (double) ScriptUtils.convert(arg[1], Double.class);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    fn.call(ctx);
                    panel.relayout();
                }
            }, (long) timeout);
            return null;
        }, "setTimeout"), ENGINE_SCOPE);

        context.setAttribute("location", new Location(), ENGINE_SCOPE);

//        context.setAttribute("HTMLCanvasElement", new Location(), ENGINE_SCOPE);

        context.setAttribute("addEventListener", new Function<>(this, (ctx, arg) -> {
            log.trace("addEventListener");
            return null;
        }, "addEventListener"), ENGINE_SCOPE);
        
        context.setAttribute("alert", new Function<>(this, (ctx, arg) -> {
            if(arg.length == 0) {
                return null;
            }
            JOptionPane.showMessageDialog(panel, String.valueOf(arg[0]));
            return null;
        }, "alert"), ENGINE_SCOPE);


        try {
            context.setAttribute("window", engine.eval("this"), ENGINE_SCOPE);
            context.setAttribute("self", engine.eval("this"), ENGINE_SCOPE);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }

        // https://www.w3.org/TR/DOM-Level-2-Style/css.html#CSS-CSSview-getComputedStyle

        context.setAttribute("getComputedStyle", new Function<>(this, (ctx, arg) -> {
            val element = (ElementImpl) ((WebIDLAdapter) arg[0]).getTarget();
            return new CSSStyleAttribute(panel.getSharedContext().getStyle(element.getModel()).toString(), panel);
        }, "getComputedStyle"), ENGINE_SCOPE);

        expose(CanvasGradientImpl.class);
        expose(CanvasPatternImpl.class);
    }

    private void expose(Class implementationClass) {
        ClassUtils.getAllInterfaces(implementationClass).forEach(i -> {
            if (i.isAnnotationPresent(Exposed.class)) {
                context.setAttribute(i.getSimpleName(), new AbstractJSObject() {
                    @Override
                    public Object newObject(Object... args) {
                        try {

                            val target = implementationClass.newInstance();
                            val adapter = WebIDLAdapter.obtain(ScriptContext.this, target);
                            Function constructor = (Function) adapter.getMember("constructor");
                            if (constructor != null) {
                                constructor.call(this, args);
                            }
                            return adapter;
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException();
                        }
                    }

                    @Override
                    public String getClassName() {
                        return i.getSimpleName();
                    }
                }, ENGINE_SCOPE);
            }
        });
    }

    private void handleNewDocument() {
        val nextDocument = panel.getDocument();
        if (nextDocument != document) {
            initEngine();
            val scripts = panel.getDocument().getElementsByTag("script");
            log.trace("Document has {} scripts", scripts.size());
            for (int i = 0; i < scripts.size(); i++) {
                val script = scripts.get(i);
                if (StringUtils.isNotBlank(script.data())) {
                    try {
//                                log.trace("Evaluating script {} {}", System.lineSeparator(), script.data());
                        eval(script.data());
                    } catch (Exception e) {
                        log.warn("script.eval", e);
                    }
                } else {
                    val scriptUri = script.attributes().get("src");
                    if (StringUtils.isNotBlank(scriptUri)) {
                        try {
                            val scriptText = panel.getSharedContext().getUac().getScriptResource(scriptUri);
//                                    log.trace("Evaluating script {} {}", System.lineSeparator(), scriptText);
                            eval(scriptText);
                        } catch (RuntimeException e) {
                            log.debug("script.src", e);
                        }
                    }
                }

                onload();
            }
            document = nextDocument;
        }
    }

    public Object eval(String scr) {
        Object res;
        try {
            res = engine.eval(scr, context);
        } catch (ScriptException e) {
            if (e.getCause() instanceof NashornException) {
                log.error(NashornException.getScriptStackString(e.getCause()));
            }
            throw new RuntimeException(e);
        }
        panel.reset();
        return res;
    }
    // temp
}