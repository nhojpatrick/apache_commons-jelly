/*
 * /home/cvs/jakarta-commons-sandbox/jelly/jelly-tags/fmt/src/java/org/apache/commons/jelly/tags/fmt/BundleTag.java,v 1.1 2003/01/16 16:21:46 jstrachan Exp
 * 1.1
 * 2003/01/16 16:21:46
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
 * BundleTag.java,v 1.1 2003/01/16 16:21:46 jstrachan Exp
 */
package org.apache.commons.jelly.tags.fmt;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.expression.Expression;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

/**
 * Support for tag handlers for &lt;bundle&gt;, the resource bundle loading
 * tag in JSTL.
 *
 * @author <a href="mailto:willievu@yahoo.com">Willie Vu</a>
 * @version 1.1
 *
 * @todo decide how to implement setResponseLocale
 */
public class BundleTag extends TagSupport {
	
	
	//*********************************************************************
	// Private constants
	
	private static final Locale EMPTY_LOCALE = new Locale("", "");
	
	
	//*********************************************************************
	// Protected state
	
	private Expression basename;                  // 'basename' attribute
	private Expression prefix;                    // 'prefix' attribute
	/** evaluated basename */
	private String ebasename;
	/** evaluated prefix */
	private String eprefix;
	
	
	//*********************************************************************
	// Private state
	
	private LocalizationContext locCtxt;
	
	
	//*********************************************************************
	// Constructor and initialization
	
	public BundleTag() {
	}
	
	//*********************************************************************
	// Collaboration with subtags
	
	public LocalizationContext getLocalizationContext() {
		return locCtxt;
	}
	
	public String getPrefixAsString() {
		return eprefix;
	}
	
	
	//*********************************************************************
	// Tag logic
	
	/**
	 * Evaluates this tag after all the tags properties have been initialized.
	 *
	 */
	public void doTag(XMLOutput output) throws JellyTagException {
		Object basenameInput = null;
		if (this.basename != null) {
			basenameInput = this.basename.evaluate(context);
		}
		if (basenameInput != null) {
			ebasename = basenameInput.toString();
		}
		
		Object prefixInput = null;
		if (this.prefix != null) {
			prefixInput = this.prefix.evaluate(context);
		}
		if (prefixInput != null) {
			eprefix = prefixInput.toString();
		}
		
		this.locCtxt = this.getLocalizationContext(context, ebasename);
		invokeBody(output);
	}
	
	
	//*********************************************************************
	// Public utility methods
	
	/**
	 * Gets the default I18N localization context.
	 *
	 * @param jc Page in which to look up the default I18N localization context
	 */
	public static LocalizationContext getLocalizationContext(JellyContext jc) {
		LocalizationContext locCtxt = null;
		
		Object obj = jc.getVariable(Config.FMT_LOCALIZATION_CONTEXT);
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof LocalizationContext) {
			locCtxt = (LocalizationContext) obj;
		} else {
			// localization context is a bundle basename
			locCtxt = getLocalizationContext(jc, (String) obj);
		}
		
		return locCtxt;
	}
	
	/**
	 * Gets the resource bundle with the given base name, whose locale is
	 * determined as follows:
	 *
	 * Check if a match exists between the ordered set of preferred
	 * locales and the available locales, for the given base name.
	 * The set of preferred locales consists of a single locale
	 * (if the <tt>org.apache.commons.jelly.tags.fmt.locale</tt> configuration
	 * setting is present).
	 *
	 * <p> If no match was found in the previous step, check if a match
	 * exists between the fallback locale (given by the
	 * <tt>org.apache.commons.jelly.tags.fmt.fallbackLocale</tt> configuration
	 * setting) and the available locales, for the given base name.
	 *
	 * @param pageContext Page in which the resource bundle with the
	 * given base name is requested
	 * @param basename Resource bundle base name
	 *
	 * @return Localization context containing the resource bundle with the
	 * given base name and the locale that led to the resource bundle match,
	 * or the empty localization context if no resource bundle match was found
	 */
	public static LocalizationContext getLocalizationContext(JellyContext jc,
	String basename) {
		LocalizationContext locCtxt = null;
		ResourceBundle bundle = null;
		
		if ((basename == null) || basename.equals("")) {
			return new LocalizationContext();
		}
		
		
		// Try preferred locales
		Locale pref = null; {
			Object tmp = jc.getVariable(Config.FMT_LOCALE);
			if (tmp != null && tmp instanceof Locale) {
				pref = (Locale) tmp;
			}
		}
		if (pref != null) {
			// Preferred locale is application-based
			bundle = findMatch(basename, pref, jc.getClassLoader());
			if (bundle != null) {
				locCtxt = new LocalizationContext(bundle, pref);
			}
		}
		
		if (locCtxt == null) {
			// No match found with preferred locales, try using fallback locale
			{
				Object tmp = jc.getVariable(Config.FMT_FALLBACK_LOCALE);
				if (tmp != null && tmp instanceof Locale) {
					pref = (Locale) tmp;
				}
			}
			if (pref != null) {
				bundle = findMatch(basename, pref, jc.getClassLoader());
				if (bundle != null) {
					locCtxt = new LocalizationContext(bundle, pref);
				}
			}
		}
		
		if (locCtxt == null) {
			// try using the root resource bundle with the given basename
			try {
				bundle = ResourceBundle.getBundle(basename, EMPTY_LOCALE, 
				jc.getClassLoader());
				if (bundle != null) {
					locCtxt = new LocalizationContext(bundle, null);
				}
			} catch (MissingResourceException mre) {
				// do nothing
			}
		}
		
		if (locCtxt != null) {
			// set response locale
			if (locCtxt.getLocale() != null) {
				// TODO
				// SetLocaleSupport.setResponseLocale(jc, locCtxt.getLocale());
			}
		} else {
			// create empty localization context
			locCtxt = new LocalizationContext();
		}
		
		return locCtxt;
	}
	
	
	
	/*
	 * Gets the resource bundle with the given base name and preferred locale.
	 *
	 * This method calls java.util.ResourceBundle.getBundle(), but ignores
	 * its return value unless its locale represents an exact or language match
	 * with the given preferred locale.
	 *
	 * @param basename the resource bundle base name
	 * @param pref the preferred locale
	 * @param cl   classloader used to find resource bundle
	 *
	 * @return the requested resource bundle, or <tt>null</tt> if no resource
	 * bundle with the given base name exists or if there is no exact- or
	 * language-match between the preferred locale and the locale of
	 * the bundle returned by java.util.ResourceBundle.getBundle().
	 */
	private static ResourceBundle findMatch(String basename, Locale pref, ClassLoader cl) {
		ResourceBundle match = null;
		
		try {
			ResourceBundle bundle =
			ResourceBundle.getBundle(basename, pref, cl);
			Locale avail = bundle.getLocale();
			if (pref.equals(avail)) {
				// Exact match
				match = bundle;
			} else {
				if (pref.getLanguage().equals(avail.getLanguage())
				&& ("".equals(avail.getCountry()))) {
					
					// Language match.
					// By making sure the available locale does not have a
					// country and matches the preferred locale's language, we
					// rule out "matches" based on the container's default
					// locale. For example, if the preferred locale is
					// "en-US", the container's default locale is "en-UK", and
					// there is a resource bundle (with the requested base
					// name) available for "en-UK", ResourceBundle.getBundle()
					// will return it, but even though its language matches
					// that of the preferred locale, we must ignore it,
					// because matches based on the container's default locale
					// are not portable across different containers with
					// different default locales.
					
					match = bundle;
				}
			}
		} catch (MissingResourceException mre) {
		}
		
		return match;
	}
	
	/** Setter for property basename.
	 * @param basename New value of property basename.
	 *
	 */
	public void setBasename(Expression basename) {
		this.basename = basename;
	}
	
	/** Setter for property prefix.
	 * @param prefix New value of property prefix.
	 *
	 */
	public void setPrefix(Expression prefix) {
		this.prefix = prefix;
	}
	
}
