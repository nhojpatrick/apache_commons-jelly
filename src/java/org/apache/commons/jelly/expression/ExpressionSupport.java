/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/expression/ExpressionSupport.java,v 1.10 2002/11/13 16:55:27 jstrachan Exp $
 * $Revision: 1.10 $
 * $Date: 2002/11/13 16:55:27 $
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
 * $Id: ExpressionSupport.java,v 1.10 2002/11/13 16:55:27 jstrachan Exp $
 */
package org.apache.commons.jelly.expression;

import java.util.Collections;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.EnumerationIterator;
import org.apache.commons.collections.iterators.SingletonIterator;

import org.apache.commons.jelly.JellyContext;

/** <p><code>ExpressionSupport</code>
  * an abstract base class for Expression implementations
  * which provides default implementations of some of the
  * typesafe evaluation methods.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.10 $
  */
public abstract class ExpressionSupport implements Expression {

    protected static final Iterator EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();

    // inherit javadoc from interface
    public String evaluateAsString(JellyContext context) {
        Object value = evaluateRecurse(context);
        // sometimes when Jelly is used inside Maven the value
        // of an expression can actually be an expression.
        // e.g. ${foo.bar} can lookup "foo.bar" in a Maven context
        // which could actually be an expression
      
        if ( value != null ) {
            return value.toString();
        }
        return null;
    }


    // inherit javadoc from interface
    public Object evaluateRecurse(JellyContext context) {
        Object value = evaluate(context);
        if (value instanceof Expression) {
            Expression expression = (Expression) value;
            return expression.evaluateRecurse(context);
        }
        return value;
    }
    
    // inherit javadoc from interface
    public boolean evaluateAsBoolean(JellyContext context) {
        Object value = evaluateRecurse(context);
        if ( value instanceof Boolean ) {
            Boolean b = (Boolean) value;
            return b.booleanValue();
        }
        else if ( value instanceof String ) {
            // return Boolean.getBoolean( (String) value );
            String str = (String) value;

            if ( str.equals( "on" )
                 ||
                 str.equals( "yes" )
                 ||
                 str.equals( "1" )
                 ||
                 str.equals( "true" ) )
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        return false;
    }

    // inherit javadoc from interface
    public Iterator evaluateAsIterator(JellyContext context) {
        Object value = evaluateRecurse(context);
        if ( value == null ) {
            return EMPTY_ITERATOR;
        }
        else
        if ( value instanceof Iterator ) {
            return (Iterator) value;
        }
        else if ( value instanceof List ) {
            List list = (List) value;
            return list.iterator();
        }
        else if ( value instanceof Map ) {
            Map map = (Map) value;
            return map.entrySet().iterator();
        }
        else if ( value.getClass().isArray() ) {
            return new ArrayIterator( value );
        }
        else if ( value instanceof Enumeration ) {
            return new EnumerationIterator((Enumeration ) value);
        }
        else if ( value instanceof Collection ) {
          Collection collection = (Collection) value;
          return collection.iterator();
        }
        else {
            // XXX: should we return single iterator?
            return new SingletonIterator( value );
        }
    }
}
