/*
 * $Header:  $
 * $Revision: 1.0 $
 * $Date:  $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id:  $
 */
package org.apache.commons.jelly.tags.bean;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.impl.TagScript;

import org.xml.sax.Attributes;

/** Describes the Taglib. This class could be generated by XDoclet
  *
  * @author Theo Niemeijer
  * @version $Revision: 1.14 $
  */
public class BeanTagLibrary extends TagLibrary {

    /** Synchronized map of tag names to bean classes */
    private Map beanTypes = new Hashtable();
    
    public BeanTagLibrary() {
        registerTagFactory(
            "beandef",
            new TagFactory() {
                public Tag createTag(String name, Attributes attributes) throws Exception {
                    return new BeandefTag(BeanTagLibrary.this);
                }
            }
        );
    }

    /**
     * Allows tags to register new bean types
     */
    public void registerBean(String name, Class type) {
        beanTypes.put(name, type);
    }
    
    // TagLibrary interface
    //-------------------------------------------------------------------------                    
    public TagScript createTagScript(
        final String name, final Attributes attributes
    ) throws Exception {

        // check for standard tags first                        
        TagScript answer = super.createTagScript(name, attributes);
        if (answer != null) {
            return answer;
        }
        
        // lets try a dynamic tag
        return new TagScript( createTagFactory(name, attributes) );
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    

    /** 
     * Factory method to create a TagFactory for a given tag attribute and attributes
     */
    protected TagFactory createTagFactory(String name, Attributes attributes) throws Exception {

        return new TagFactory() {
            public Tag createTag(String name, Attributes attributes) throws Exception {
                return createBeanTag(name, attributes);
            }
        };
    }

    protected Tag createBeanTag(String name, Attributes attributes) throws Exception {
        // is the name bound to a specific class
        Class beanType = getBeanType(name, attributes);
        if (beanType != null) {
            return new BeanTag(beanType, name);
        }
        
        // its a property tag
        return new BeanPropertyTag(name);
    }
    
    protected Class getBeanType(String name, Attributes attributes) {
        return (Class) beanTypes.get(name);
    }
}
