package org.xhtmlrenderer.js.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Taras Maslov
 * 5/31/2018
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class G2DState implements Cloneable {

    AffineTransform transform = new AffineTransform();
    Color fillColor;
    Color strokeColor;
    double lineWidth = 1;
    int fontSize = 12;

    void apply(Graphics2D graphics2D) {
        graphics2D.setTransform(transform);
        graphics2D.setStroke(new BasicStroke((float) lineWidth));
        graphics2D.setFont(new Font("sans-serif", Font.PLAIN, fontSize)); // todo cache
    }
    
    void apply(Graphics2D graphics2D, boolean fill) {
        apply(graphics2D);
        graphics2D.setColor(fill ? fillColor : strokeColor);
    }

    public void scale(double x, double y) {
        transform.scale(x, y);
    }

    void rotate(double angle) {
        transform.rotate(angle);
    }

    void translate(double x, double y) {
        transform.translate(x, y);
    }

    void transform(double a, double b, double c, double d, double e, double f) {
        transform.concatenate(new AffineTransform(a, b, c, d, e, f));
    }

    void setTransform(double a, double b, double c, double d, double e, double f) {
        transform.setTransform(new AffineTransform(a, b, c, d, e, f));
    }
    
    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public void setLineWidth(double value) {
        this.lineWidth = value;
    }

    public G2DState setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    @Override
    public G2DState clone()  {
        try {
            val res = (G2DState) super.clone();
            res.transform = transform != null ? (AffineTransform) transform.clone() : null;
            res.fillColor = fillColor;
            res.lineWidth = lineWidth;
            return res;
        } catch (CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
    }
    
    
}