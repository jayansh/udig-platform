/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common.styleattributeclasses;

import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_COLOR;
import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_OFFSET;
import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_OPACITY;
import static org.locationtech.udig.style.advanced.utils.Utilities.DEFAULT_WIDTH;
import static org.locationtech.udig.style.advanced.utils.Utilities.ff;
import static org.locationtech.udig.style.advanced.utils.Utilities.getDashString;
import static org.locationtech.udig.style.advanced.utils.Utilities.getOffset;
import static org.locationtech.udig.style.advanced.utils.Utilities.sf;

import java.awt.geom.Point2D;
import java.util.List;

import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.expression.Expression;
import org.opengis.style.GraphicalSymbol;

import org.locationtech.udig.style.advanced.utils.Utilities;

/**
 * A wrapper for a {@link LineSymbolizer} to ease interaction with gui.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class LineSymbolizerWrapper extends SymbolizerWrapper {
    protected String strokeColor;
    protected String strokeOpacity;
    protected String strokeWidth;

    protected boolean hasStroke;
    protected Stroke stroke;
    protected Graphic strokeGraphicStroke;
    protected String dash;
    protected String dashOffset;
    protected String lineCap;
    protected String lineJoin;

    public LineSymbolizerWrapper( PolygonSymbolizer polygonSymbolizer, RuleWrapper parent ) {
        super(polygonSymbolizer, parent);
    }

    public LineSymbolizerWrapper( Symbolizer symbolizer, RuleWrapper parent ) {
        super(symbolizer, parent);

        LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;

        // offset
        Point2D offset = getOffset(lineSymbolizer);
        if (offset != null) {
            xOffset = String.valueOf(offset.getX());
            yOffset = String.valueOf(offset.getY());
        } else {
            xOffset = DEFAULT_OFFSET;
            yOffset = DEFAULT_OFFSET;
        }

        stroke = lineSymbolizer.getStroke();
        if (stroke != null) {
            Expression color = stroke.getColor();
            strokeColor = expressionToString(color);
            Expression width = stroke.getWidth();
            strokeWidth = expressionToString(width);
            Expression opacity = stroke.getOpacity();
            strokeOpacity = expressionToString(opacity);

            if (strokeColor == null) {
                strokeColor = DEFAULT_COLOR;
                stroke.setColor(ff.literal(DEFAULT_COLOR));
            }
            if (strokeOpacity == null) {
                strokeOpacity = DEFAULT_OPACITY;
                stroke.setOpacity(ff.literal(DEFAULT_OPACITY));
            }
            if (strokeWidth == null) {
                strokeWidth = DEFAULT_WIDTH;
                stroke.setWidth(ff.literal(DEFAULT_WIDTH));
            }
            
            strokeGraphicStroke = stroke.getGraphicStroke();
            if (strokeGraphicStroke != null) {
                List<GraphicalSymbol> graphicalSymbolsList = strokeGraphicStroke.graphicalSymbols();
                if (graphicalSymbolsList.size() > 0) {
                    GraphicalSymbol graphicalSymbol = graphicalSymbolsList.get(0);
                    if (graphicalSymbol instanceof ExternalGraphic) {
                        strokeExternalGraphicStroke = (ExternalGraphic) graphicalSymbol;
                    }
                }
            }

            // dash
            float[] dashArray = stroke.getDashArray();
            if (dashArray != null) {
                dash = getDashString(dashArray);
            } else {
                dash = ""; //$NON-NLS-1$
            }
            // dashoffset
            dashOffset = stroke.getDashOffset().evaluate(null, String.class);
            // line cap
            lineCap = stroke.getLineCap().evaluate(null, String.class);
            // line join
            lineJoin = stroke.getLineJoin().evaluate(null, String.class);

            hasStroke = true;
        } else {
            hasStroke = false;
        }

    }

    public Graphic getStrokeGraphicStroke() {
        return strokeGraphicStroke;
    }

    public void setStrokeGraphicStroke( Graphic strokeGraphicStroke ) {
        this.strokeGraphicStroke = strokeGraphicStroke;
        checkStrokeExists();

        stroke.setGraphicStroke(strokeGraphicStroke);
    }

    // ///// GETTERS/SETTERS
    public void setHasStroke( boolean hasStroke ) {
        this.hasStroke = hasStroke;
        if (hasStroke) {
            checkStrokeExists();
        } else {
            stroke = null;
            LineSymbolizer lineSymbolizer = (LineSymbolizer) getSymbolizer();
            lineSymbolizer.setStroke(null);
        }
    }

    protected void checkStrokeExists() {
        if (stroke == null) {
            if (strokeColor == null) {
                strokeColor = DEFAULT_COLOR;
            }
            if (strokeWidth == null) {
                strokeWidth = DEFAULT_WIDTH;
            }
            stroke = sf.createStroke(ff.literal(strokeColor), ff.literal(strokeWidth));
            LineSymbolizer lineSymbolizer = (LineSymbolizer) getSymbolizer();
            lineSymbolizer.setStroke(stroke);
            strokeGraphicStroke = stroke.getGraphicStroke();
        }
    }

    public void setStrokeWidth( String strokeWidth, boolean isProperty ) {
        this.strokeWidth = strokeWidth;
        checkStrokeExists();
        if (isProperty) {
            stroke.setWidth(ff.property(strokeWidth));
        } else {
            stroke.setWidth(ff.literal(strokeWidth));
        }
    }

    public void setStrokeColor( String strokeColor, boolean isProperty ) {
        this.strokeColor = strokeColor;
        checkStrokeExists();
        if (isProperty) {
            stroke.setColor(ff.property(strokeColor));
        } else {
            if (strokeColor != null) {
                stroke.setColor(ff.literal(strokeColor));
            }
        }
    }

    public void setStrokeOpacity( String strokeOpacity, boolean isProperty ) {
        this.strokeOpacity = strokeOpacity;
        checkStrokeExists();
        if (isProperty) {
            stroke.setOpacity(ff.property(strokeOpacity));
        } else {
            stroke.setOpacity(ff.literal(strokeOpacity));
        }
    }

    public void setDash( String dash ) {
        this.dash = dash;
        checkStrokeExists();
        float[] dashArray = Utilities.getDash(dash);
        stroke.setDashArray(dashArray);
    }

    public void setDashOffset( String dashOffset ) {
        this.dashOffset = dashOffset;
        checkStrokeExists();
        if (dashOffset != null && dashOffset.length() > 0) {
            stroke.setDashOffset(ff.literal(dashOffset));
        }
    }

    public void setLineCap( String lineCap ) {
        this.lineCap = lineCap;
        checkStrokeExists();
        stroke.setLineCap(ff.literal(lineCap));
    }

    public void setLineJoin( String lineJoin ) {
        this.lineJoin = lineJoin;
        checkStrokeExists();
        stroke.setLineJoin(ff.literal(lineJoin));
    }

    // getters
    public String getStrokeColor() {
        return strokeColor;
    }

    public String getStrokeOpacity() {
        return strokeOpacity;
    }

    public String getStrokeWidth() {
        return strokeWidth;
    }

    public boolean hasStroke() {
        return hasStroke;
    }

    public String getDash() {
        return dash;
    }

    public String getDashOffset() {
        return dashOffset;
    }

    public String getLineCap() {
        return lineCap;
    }

    public String getLineJoin() {
        return lineJoin;
    }

}
