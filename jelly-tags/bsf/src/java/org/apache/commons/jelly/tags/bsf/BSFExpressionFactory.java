/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/bsf/src/java/org/apache/commons/jelly/tags/bsf/BSFExpressionFactory.java,v 1.4 2003/10/09 21:21:16 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/09 21:21:16 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * $Id: BSFExpressionFactory.java,v 1.4 2003/10/09 21:21:16 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.bsf;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

/** Represents a factory of BSF expressions
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.4 $
  */
public class BSFExpressionFactory implements ExpressionFactory {

    /** The logger of messages */
    private Log log = LogFactory.getLog( getClass() );
    
    private String language = "javascript";
    private BSFManager manager;
    private BSFEngine engine;
    private JellyContextRegistry registry = new JellyContextRegistry();
    
    public BSFExpressionFactory() {
    }
    
    // Properties
    //------------------------------------------------------------------------- 

    /** @return the BSF language to be used */
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    /** @return the BSF Engine to be used by this expression factory */
    public BSFEngine getBSFEngine() throws BSFException {
        if ( engine == null ) {
            engine = createBSFEngine();
        }
        return engine;
    }
    
    public void setBSFEngine(BSFEngine engine) {
        this.engine = engine;
    }
    
    public BSFManager getBSFManager() {
        if ( manager == null ) {
            manager = createBSFManager();
            manager.setObjectRegistry( registry );
        }
        return manager;
    }
    
    public void setBSFManager(BSFManager manager) {
        this.manager = manager;
        manager.setObjectRegistry( registry );
    }
    
    // ExpressionFactory interface
    //------------------------------------------------------------------------- 
    public Expression createExpression(String text) throws JellyException {
        try {
            return new BSFExpression( text, getBSFEngine(), getBSFManager(), registry );
        } catch (BSFException e) {
            throw new JellyException("Could not obtain BSF engine",e);
        }
    }
    
    // Implementation methods
    //------------------------------------------------------------------------- 
    
    /** Factory method */
    protected BSFEngine createBSFEngine() throws BSFException {        
        return getBSFManager().loadScriptingEngine( getLanguage() );
    }
    
    /** Factory method */
    protected BSFManager createBSFManager() {
        BSFManager answer = new BSFManager();
        return answer;
    }
}
