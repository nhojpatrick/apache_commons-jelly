/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/test/org/apache/commons/jelly/test/xml/TestXMLValidation.java,v 1.7 2003/10/09 21:21:32 rdonkin Exp $
 * $Revision: 1.7 $
 * $Date: 2003/10/09 21:21:32 $
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
 * $Id: TestXMLValidation.java,v 1.7 2003/10/09 21:21:32 rdonkin Exp $
 */
package org.apache.commons.jelly.test.xml;

import java.io.StringWriter;
import java.net.URL;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/**
 * A test to confirm that invalid documents are
 * reject iff jelly.setValidateXML(true)
 * 
 * @author Morgan Delagrange
 * @version $Revision: 1.7 $
 */
public class TestXMLValidation extends TestCase {

    Jelly jelly = null;
    JellyContext context = null;
    XMLOutput xmlOutput = null;

    public TestXMLValidation(String name) {
        super(name);
    }

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestXMLValidation.class);        
    }

    public void setUp(String scriptName) throws Exception {
        context = new JellyContext();
        xmlOutput = XMLOutput.createXMLOutput(new StringWriter());

        jelly = new Jelly();
        
        String script = scriptName;
        URL url = this.getClass().getResource(script);
        if ( url == null ) {
            throw new Exception( 
                "Could not find Jelly script: " + script 
                + " in package of class: " + this.getClass().getName() 
            );
        }
        jelly.setUrl(url);
    }

    public void testInvalidXML1NoValidation() throws Exception {
        // without validation
        setUp("invalidScript1.jelly");
        Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'foo' variable to 'bar'",
                   context.getVariable("foo").equals("bar"));

        // do it again, explicitly setting the validateXML variable
        setUp("invalidScript1.jelly");
        jelly.setValidateXML(false);
        script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'foo' variable to 'bar'",
                   context.getVariable("foo").equals("bar"));
    }

    public void testInvalidXML1Validation() throws Exception {
        // with validation
        setUp("invalidScript1.jelly");
        jelly.setValidateXML(true);
        try {
            Script script = jelly.compileScript();
            fail("Invalid scripts should throw JellyException on parse");
        } catch (JellyException e) {
        }
    }

    public void testValidXML1Validation()throws Exception {
        // with validation
        setUp("validScript1.jelly");
        jelly.setValidateXML(true);
        Script script = jelly.compileScript();
        script.run(context,xmlOutput);
        assertTrue("should have set 'foo' variable to 'bar'",
                   context.getVariable("foo").equals("bar"));
    }

}
