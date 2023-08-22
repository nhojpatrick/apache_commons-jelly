/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.tags.junit;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.jelly.LocationAware;

import junit.framework.AssertionFailedError;

/**
 * <p><code>JellyAssertionFailedError</code> is
 * a JUnit AssertionFailedError which is LocationAware so that it can include
 * details of where in the JellyUnit test case that the failure occurred.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */

public class JellyAssertionFailedError extends AssertionFailedError implements LocationAware {

    /** the underlying cause of the exception */
    private Throwable cause;

    /** the Jelly file which caused the problem */
    private String fileName;

    /** the tag name which caused the problem */
    private String elementName;

    /** the line number in the script of the error */
    private int lineNumber = -1;

    /** the column number in the script of the error */
    private int columnNumber = -1;

    public JellyAssertionFailedError() {
    }

    public JellyAssertionFailedError(String message) {
        super(message);
    }

    public JellyAssertionFailedError(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public JellyAssertionFailedError(Throwable cause) {
        super(cause.getLocalizedMessage());
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }


    /**
     * @return the line number of the tag
     */
    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the line number of the tag
     */
    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the column number of the tag
     */
    @Override
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Sets the column number of the tag
     */
    @Override
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * @return the Jelly file which caused the problem
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the Jelly file which caused the problem
     */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    /**
     * @return the element name which caused the problem
     */
    @Override
    public String getElementName() {
        return elementName;
    }

    /**
     * Sets the element name which caused the problem
     */
    @Override
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }


    @Override
    public String getMessage() {
        return super.getMessage() + " File: " + fileName + " At tag <" + elementName + ">: line: "
            + lineNumber + " column: " + columnNumber;
    }

    public String getReason() {
        return super.getMessage();
    }

    // #### overload the printStackTrace methods...
    @Override
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            super.printStackTrace(s);
            if  (cause != null) {
                s.println("Root cause");
                cause.printStackTrace(s);
            }
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            super.printStackTrace(s);
            if  (cause != null) {
                s.println("Root cause");
                cause.printStackTrace(s);
            }
        }
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (cause != null) {
            System.out.println("Root cause");
            cause.printStackTrace();
        }
    }
}
