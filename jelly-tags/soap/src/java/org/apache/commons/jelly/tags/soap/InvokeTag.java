/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/soap/src/java/org/apache/commons/jelly/tags/soap/InvokeTag.java,v 1.3 2003/10/09 21:21:23 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 21:21:23 $
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
 * $Id: InvokeTag.java,v 1.3 2003/10/09 21:21:23 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.soap;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Invokes a web service
 * 
 * @author <a href="mailto:jim@bnainc.net">James Birchfield</a>
 * @version $Revision: 1.3 $
 */
public class InvokeTag extends TagSupport {

    private String var;
    private String endpoint = null;
    private String namespace = null;
    private String method = null;
    private Service service;
    private Object params;
    
    public InvokeTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (endpoint == null) {
            throw new MissingAttributeException("endpoint");
        }
        if (namespace == null) {
            throw new MissingAttributeException("namespace");
        }
        if (method == null) {
            throw new MissingAttributeException("method");
        }
        
        Object[] params = getParamArray();
        if (params == null) {
            params = new Object[]{ getBodyText() };
        }
        else {
            // invoke body just in case we have nested tags
            invokeBody(output);
        }

        Service service = getService();
        if (service == null) {
            service = createService();
        }
        
        Object answer = null;
        try {
            Call call = (Call) service.createCall();

            // @todo Jelly should have native support for URL and QName
            // directly on properties
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName(new QName(namespace, method));

            answer = call.invoke(params);
        } 
        catch (MalformedURLException e) {
            throw new JellyTagException(e);
        } 
        catch (ServiceException e) {
            throw new JellyTagException(e);
        }
        catch (RemoteException e) {
            throw new JellyTagException(e);
        }
        
        if (var != null) {
            context.setVariable(var, answer);
        }
        else {
            // should turn the answer into XML events...
            throw new JellyTagException( "Not implemented yet; should stream results as XML events. Results: " + answer );
        }
    }


    // Properties
    //-------------------------------------------------------------------------
    /**
     * Sets the end point to which the invocation will occur
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Sets the namespace of the operation
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
     * Returns the service to be used by this web service invocation.
     * @return Service
     */
    public Service getService() {
        return service;
    }

    /**
     * Sets the service to be used by this invocation.
     * If none is specified then a default is used.
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Sets the name of the variable to output the results of the SOAP call to.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Sets the parameters for this SOAP call. This can be an array or collection of 
     * SOAPBodyElements or types.
     */
    public void setParams(Object params) {
        this.params = params;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Factory method to create a new default Service instance
     */
    protected Service createService() {
        return new Service();
    }

    /**
     * Performs any type coercion on the given parameters to form an Object[]
     * or returns null if no parameter has been specified
     */
    protected Object[] getParamArray() {
        if (params == null) {
            return null;
        }
        if (params instanceof Object[]) {
            return (Object[]) params;
        }
        if (params instanceof Collection) {
            Collection coll = (Collection) params;
            return coll.toArray();
        }
        // lets just wrap the current object inside an array
        return new Object[] { params };
    }
}
