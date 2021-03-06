package com.earnix.webk.runtime.dom.impl.parser;

import com.earnix.webk.runtime.dom.impl.Jsoup;
import com.earnix.webk.runtime.dom.impl.TextUtil;
import com.earnix.webk.runtime.dom.impl.select.Elements;
import com.earnix.webk.runtime.html.impl.DocumentImpl;
import com.earnix.webk.runtime.dom.impl.CommentImpl;
import com.earnix.webk.runtime.dom.impl.ElementImpl;
import com.earnix.webk.runtime.dom.impl.TextImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TokeniserStateTest {

    final char[] whiteSpace = {'\t', '\n', '\r', '\f', ' '};
    final char[] quote = {'\'', '"'};

    @Test
    public void ensureSearchArraysAreSorted() {
        char[][] arrays = {
                TokeniserState.attributeSingleValueCharsSorted,
                TokeniserState.attributeDoubleValueCharsSorted,
                TokeniserState.attributeNameCharsSorted,
                TokeniserState.attributeValueUnquoted
        };

        for (char[] array : arrays) {
            char[] copy = Arrays.copyOf(array, array.length);
            Arrays.sort(array);
            assertArrayEquals(array, copy);
        }
    }

    @Test
    public void testCharacterReferenceInRcdata() {
        String body = "<textarea>You&I</textarea>";
        DocumentImpl doc = Jsoup.parse(body);
        Elements els = doc.select("textarea");
        assertEquals("You&I", els.text());
    }

    @Test
    public void testBeforeTagName() {
        for (char c : whiteSpace) {
            String body = String.format("<div%c>test</div>", c);
            DocumentImpl doc = Jsoup.parse(body);
            Elements els = doc.select("div");
            assertEquals("test", els.text());
        }
    }

    @Test
    public void testEndTagOpen() {
        String body;
        DocumentImpl doc;
        Elements els;

        body = "<div>hello world</";
        doc = Jsoup.parse(body);
        els = doc.select("div");
        assertEquals("hello world</", els.text());

        body = "<div>hello world</div>";
        doc = Jsoup.parse(body);
        els = doc.select("div");
        assertEquals("hello world", els.text());

        body = "<div>fake</></div>";
        doc = Jsoup.parse(body);
        els = doc.select("div");
        assertEquals("fake", els.text());

        body = "<div>fake</?</div>";
        doc = Jsoup.parse(body);
        els = doc.select("div");
        assertEquals("fake", els.text());
    }

    @Test
    public void testRcdataLessthanSign() {
        String body;
        DocumentImpl doc;
        Elements els;

        body = "<textarea><fake></textarea>";
        doc = Jsoup.parse(body);
        els = doc.select("textarea");
        assertEquals("<fake>", els.text());

        body = "<textarea><open";
        doc = Jsoup.parse(body);
        els = doc.select("textarea");
        assertEquals("", els.text());

        body = "<textarea>hello world</?fake</textarea>";
        doc = Jsoup.parse(body);
        els = doc.select("textarea");
        assertEquals("hello world</?fake", els.text());
    }

    @Test
    public void testRCDATAEndTagName() {
        for (char c : whiteSpace) {
            String body = String.format("<textarea>data</textarea%c>", c);
            DocumentImpl doc = Jsoup.parse(body);
            Elements els = doc.select("textarea");
            assertEquals("data", els.text());
        }
    }

    @Test
    public void testCommentEndCoverage() {
        String html = "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --! --- --><p>Hello</p></body></html>";
        DocumentImpl doc = Jsoup.parse(html);

        ElementImpl body = doc.getBody();
        CommentImpl comment = (CommentImpl) body.childNode(1);
        assertEquals(" <table><tr><td></table> --! --- ", comment.getData());
        ElementImpl p = body.child(1);
        TextImpl text = (TextImpl) p.childNode(0);
        assertEquals("Hello", text.getWholeText());
    }

    @Test
    public void testCommentEndBangCoverage() {
        String html = "<html><head></head><body><img src=foo><!-- <table><tr><td></table> --!---!>--><p>Hello</p></body></html>";
        DocumentImpl doc = Jsoup.parse(html);

        ElementImpl body = doc.getBody();
        CommentImpl comment = (CommentImpl) body.childNode(1);
        assertEquals(" <table><tr><td></table> --!-", comment.getData());
        ElementImpl p = body.child(1);
        TextImpl text = (TextImpl) p.childNode(0);
        assertEquals("Hello", text.getWholeText());
    }

    @Test
    public void testPublicIdentifiersWithWhitespace() {
        String expectedOutput = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0//EN\">";
        for (char q : quote) {
            for (char ws : whiteSpace) {
                String[] htmls = {
                        String.format("<!DOCTYPE html%cPUBLIC %c-//W3C//DTD HTML 4.0//EN%c>", ws, q, q),
                        String.format("<!DOCTYPE html %cPUBLIC %c-//W3C//DTD HTML 4.0//EN%c>", ws, q, q),
                        String.format("<!DOCTYPE html PUBLIC%c%c-//W3C//DTD HTML 4.0//EN%c>", ws, q, q),
                        String.format("<!DOCTYPE html PUBLIC %c%c-//W3C//DTD HTML 4.0//EN%c>", ws, q, q),
                        String.format("<!DOCTYPE html PUBLIC %c-//W3C//DTD HTML 4.0//EN%c%c>", q, q, ws),
                        String.format("<!DOCTYPE html PUBLIC%c-//W3C//DTD HTML 4.0//EN%c%c>", q, q, ws)
                };
                for (String html : htmls) {
                    DocumentImpl doc = Jsoup.parse(html);
                    assertEquals(expectedOutput, doc.childNode(0).outerHtml());
                }
            }
        }
    }

    @Test
    public void testSystemIdentifiersWithWhitespace() {
        String expectedOutput = "<!DOCTYPE html SYSTEM \"http://www.w3.org/TR/REC-html40/strict.dtd\">";
        for (char q : quote) {
            for (char ws : whiteSpace) {
                String[] htmls = {
                        String.format("<!DOCTYPE html%cSYSTEM %chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", ws, q, q),
                        String.format("<!DOCTYPE html %cSYSTEM %chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", ws, q, q),
                        String.format("<!DOCTYPE html SYSTEM%c%chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", ws, q, q),
                        String.format("<!DOCTYPE html SYSTEM %c%chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", ws, q, q),
                        String.format("<!DOCTYPE html SYSTEM %chttp://www.w3.org/TR/REC-html40/strict.dtd%c%c>", q, q, ws),
                        String.format("<!DOCTYPE html SYSTEM%chttp://www.w3.org/TR/REC-html40/strict.dtd%c%c>", q, q, ws)
                };
                for (String html : htmls) {
                    DocumentImpl doc = Jsoup.parse(html);
                    assertEquals(expectedOutput, doc.childNode(0).outerHtml());
                }
            }
        }
    }

    @Test
    public void testPublicAndSystemIdentifiersWithWhitespace() {
        String expectedOutput = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0//EN\""
                + " \"http://www.w3.org/TR/REC-html40/strict.dtd\">";
        for (char q : quote) {
            for (char ws : whiteSpace) {
                String[] htmls = {
                        String.format("<!DOCTYPE html PUBLIC %c-//W3C//DTD HTML 4.0//EN%c"
                                + "%c%chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", q, q, ws, q, q),
                        String.format("<!DOCTYPE html PUBLIC %c-//W3C//DTD HTML 4.0//EN%c"
                                + "%chttp://www.w3.org/TR/REC-html40/strict.dtd%c>", q, q, q, q)
                };
                for (String html : htmls) {
                    DocumentImpl doc = Jsoup.parse(html);
                    assertEquals(expectedOutput, doc.childNode(0).outerHtml());
                }
            }
        }
    }

    @Test
    public void handlesLessInTagThanAsNewTag() {
        // out of spec, but clear author intent
        String html = "<p\n<p<div id=one <span>Two";
        DocumentImpl doc = Jsoup.parse(html);
        Assert.assertEquals("<p></p><p></p><div id=\"one\"><span>Two</span></div>", TextUtil.stripNewlines(doc.getBody().html()));
    }
}
