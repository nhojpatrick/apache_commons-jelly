/*
 * /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/swt/SwtTagLibrary.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 * 1.1
 * 2002/12/18 15:27:49
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * SwtTagLibrary.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Attributes;

/** 
 * A Jelly custom tag library that creates SWT user interfaces
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version 1.1
 */
public class SwtTagLibrary extends TagLibrary {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SwtTagLibrary.class);

    static {
        // we could create Converters from Strings to various SWT types 
    }
        
    public SwtTagLibrary() {
        // widgets
        registerWidgetTag( "button", Button.class, SWT.BORDER | SWT.PUSH | SWT.CENTER );
        registerWidgetTag( "canvas", Canvas.class );
        registerWidgetTag( "caret", Caret.class );
        registerWidgetTag( "combo", Combo.class, SWT.DROP_DOWN );
        registerWidgetTag( "composite", Composite.class );
        registerWidgetTag( "coolBar", CoolBar.class, SWT.VERTICAL );
        registerWidgetTag( "coolItem", CoolItem.class );
        registerWidgetTag( "decorations", Decorations.class );
        registerWidgetTag( "group", Group.class );
        registerWidgetTag( "label", Label.class, SWT.HORIZONTAL | SWT.SHADOW_IN ); 
        registerWidgetTag( "list", List.class );
        registerMenuTag( "menu", SWT.DEFAULT );
        registerMenuTag( "menuBar", SWT.BAR );
        registerWidgetTag( "menuSeparator", MenuItem.class, SWT.SEPARATOR );
        registerWidgetTag( "menuItem", MenuItem.class );
        registerWidgetTag( "messageBox", MessageBox.class );
        registerWidgetTag( "progressBar", ProgressBar.class, SWT.HORIZONTAL );
        registerWidgetTag( "sash", Sash.class );
        registerWidgetTag( "scale", Scale.class );
        registerWidgetTag( "shell", Shell.class, SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.TITLE );
        registerWidgetTag( "slider", Slider.class );
        registerWidgetTag( "tabFolder", TabFolder.class );
        registerWidgetTag( "tabItem", TabItem.class );
        registerWidgetTag( "table", Table.class, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION );
        registerWidgetTag( "tableColumn", TableColumn.class );
        registerWidgetTag( "tableItem", TableItem.class );
        registerWidgetTag( "text", Text.class );
        registerWidgetTag( "toolBar", ToolBar.class, SWT.VERTICAL );
        registerWidgetTag( "toolItem", ToolItem.class );
        registerWidgetTag( "tracker", Tracker.class );
        registerWidgetTag( "tree", Tree.class, SWT.MULTI );
        registerWidgetTag( "treeItem", TreeItem.class );

        // custom widgets
        registerWidgetTag( "tableTree", TableTree.class );
        registerWidgetTag( "tableTreeItem", TableTreeItem.class );

        // layouts        
        registerLayoutTag("fillLayout", FillLayout.class);
        registerLayoutTag("gridLayout", GridLayout.class);
        registerLayoutTag("rowLayout", RowLayout.class);

        // layout data objects
        registerLayoutDataTag( "gridData", GridData.class );
        registerLayoutDataTag( "rowData", RowData.class );

        // dialogs        
        //registerWidgetTag( "colorDialog", ColorDialog.class );
        //registerWidgetTag( "directoryDialog", DirectoryDialog.class );
        //registerWidgetTag( "fileDialog", FileDialog.class );
        //registerWidgetTag( "fontDialog", FontDialog.class );
        
        // events
        registerTag("onEvent", OnEventTag.class);
    }

    /**
     * Register a layout tag for the given name
     */
    protected void registerLayoutTag(String name, final Class layoutClass) {
        registerTagFactory(
            name,
            new TagFactory() {
                /**
                 * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
                 */
                public Tag createTag(String name, Attributes attributes)
                    throws JellyException {
                    return new LayoutTag(layoutClass);
                }
            }
        );
    }

    /**
     * Register a layout data tag for the given name
     */
    protected void registerLayoutDataTag(String name, final Class layoutDataClass) {
        registerTagFactory(
            name,
            new TagFactory() {
                /**
                 * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
                 */
                public Tag createTag(String name, Attributes attributes)
                    throws JellyException {
                    return new LayoutDataTag(layoutDataClass);
                }
            }
        );
    }

    /**
     * Register a widget tag for the given name
     */
    protected void registerWidgetTag(String name, Class widgetClass) {
        registerWidgetTag(name, widgetClass, SWT.DEFAULT);
    }
    
    /**
     * Register a widget tag for the given name
     */
    protected void registerWidgetTag(String name, final Class widgetClass, final int style) {
        registerTagFactory(
            name,
            new TagFactory() {
                /**
                 * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
                 */
                public Tag createTag(String name, Attributes attributes)
                    throws JellyException {
                    return new WidgetTag(widgetClass, style);
                }
            }
        );
    }

    /**
     * Register a menu tag for the given name and style
     */
    protected void registerMenuTag(String name, final int style) {
        registerTagFactory(
            name,
            new TagFactory() {
                /**
                 * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
                 */
                public Tag createTag(String name, Attributes attributes)
                    throws JellyException {
                    return new MenuTag(style);
                }
            }
        );
    }
}