/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/email/src/java/org/apache/commons/jelly/tags/email/EmailTag.java,v 1.4 2003/10/09 21:21:17 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/09 21:21:17 $
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
 * $Id: EmailTag.java,v 1.4 2003/10/09 21:21:17 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.email;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.mail.Session;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Basic tag for sending an email. Supports one attachment, multiple to addresses delimited by ";",
 * multiple cc addresses, etc.
 *
 * @author  Jason Horman
 * @author  <a href="mailto:willievu@yahoo.com">Willie Vu</a>
 * @version  $Id: EmailTag.java,v 1.4 2003/10/09 21:21:17 rdonkin Exp $
 */

public class EmailTag extends TagSupport {
    private Log logger = LogFactory.getLog(EmailTag.class);

    /** smtp server */
    private Expression server       = null;

    /** who to send the message as */
    private Expression from         = null;

    /** who to send to */
    private Expression to           = null;

    /** who to cc */
    private Expression cc           = null;

    /** mail subject */
    private Expression subject      = null;

    /** mail message */
    private Expression message      = null;

    /** file attachment */
    private File attachment     = null;
    
    /** whether we should encode the XML body as text */
    private boolean encodeXML = false;

    /**
     * Set the smtp server for the message. If not set the system
     * property "mail.smtp.host" will be used.
     */
    public void setServer(Expression server) {
        this.server = server;
    }

    /**
     * Set the from address for the message
     */
    public void setFrom(Expression from) {
        this.from = from;
    }

    /**
     * ";" seperated list of people to send to
     */
    public void setTo(Expression to) {
        this.to = to;
    }

    /**
     * ";" seperated list of people to cc
     */
    public void setCC(Expression cc) {
        this.cc = cc;
    }

    /**
     * Set the email subject
     */
    public void setSubject(Expression subject) {
        this.subject = subject;
    }

    /**
     * Set the message body. This will override the Jelly tag body
     */
    public void setMessage(Expression message) {
        this.message = message;
    }

    /**
     * Set the email attachment for the message. Only 1 attachment is supported right now
     */
    public void setAttach(File attachment) throws FileNotFoundException {
        if (!attachment.exists()) {
            throw new FileNotFoundException("attachment not found");
        }

        this.attachment = attachment;
    }

    /** 
     * Sets whether we should encode the XML body as text or not. The default
     * is false so that the body will assumed to be valid XML
     */
    public void setEncodeXML(boolean encodeXML) {
        this.encodeXML = encodeXML;
    }

    /**
     * Execute the tag
     */
    public void doTag(XMLOutput xmlOutput) throws JellyTagException {
        Properties props = new Properties();
        props.putAll(context.getVariables());

        // if a server was set then configure the system property
        if (server != null) {
			Object serverInput = this.server.evaluate(context);
            props.put("mail.smtp.host", serverInput.toString());
        } 
        else {
            if (props.get("mail.smtp.host") == null) {
                throw new JellyTagException("no smtp server configured");
            }
        }

        // check if there was no to address
        if (to == null) {
            throw new JellyTagException("no to address specified");
        }

        // check if there was no from
        if (from == null) {
            throw new JellyTagException("no from address specified");
        }

		String messageBody = null;
		if (this.message != null) {
			messageBody = this.message.evaluate(context).toString();
		}
		else {
			// get message from body
			messageBody = getBodyText(encodeXML);
		}

		Object fromInput = this.from.evaluate(context);
		Object toInput = this.to.evaluate(context);

        // configure the mail session
        Session session = Session.getDefaultInstance(props, null);

        // construct the mime message
        MimeMessage msg = new MimeMessage(session);
 
        try {
            // set the from address
            msg.setFrom(new InternetAddress(fromInput.toString()));

            // parse out the to addresses
            StringTokenizer st = new StringTokenizer(toInput.toString(), ";");
            InternetAddress[] addresses = new InternetAddress[st.countTokens()];
            int addressCount = 0;
            while (st.hasMoreTokens()) {
                addresses[addressCount++] = new InternetAddress(st.nextToken().trim());
            }

            // set the recipients
            msg.setRecipients(Message.RecipientType.TO, addresses);

            // parse out the cc addresses
            if (cc != null) {
				Object ccInput = this.cc.evaluate(context);
                st = new StringTokenizer(ccInput.toString(), ";");
                InternetAddress[] ccAddresses = new InternetAddress[st.countTokens()];
                int ccAddressCount = 0;
                while (st.hasMoreTokens()) {
                    ccAddresses[ccAddressCount++] = new InternetAddress(st.nextToken().trim());
                }

                // set the cc recipients
                msg.setRecipients(Message.RecipientType.CC, ccAddresses);
            }
        } 
        catch (AddressException e) {
            throw new JellyTagException(e);
        } 
        catch (MessagingException e) {
            throw new JellyTagException(e);
        }

        try {
            // set the subject
            if (subject != null) {
				Object subjectInput = this.subject.evaluate(context);
                msg.setSubject(subjectInput.toString());
            }

            // set a timestamp
            msg.setSentDate(new Date());

            // if there was no attachment just set the text/body of the message
            if (attachment == null) {

                msg.setText(messageBody);

            } 
            else {

                // attach the multipart mime message
                // setup the body
                MimeBodyPart mbp1 = new MimeBodyPart();
                mbp1.setText(messageBody);

                // setup the attachment
                MimeBodyPart mbp2 = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(attachment);
                mbp2.setDataHandler(new DataHandler(fds));
                mbp2.setFileName(fds.getName());

                // create the multipart and add its parts
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp1);
                mp.addBodyPart(mbp2);

                msg.setContent(mp);

            }

            logger.info("sending email to " + to + " using " + server);

            // send the email
            Transport.send(msg);
        } 
        catch (MessagingException e) {
            throw new JellyTagException(e);
        }

    }
}
