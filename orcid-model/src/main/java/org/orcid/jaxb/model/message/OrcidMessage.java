/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.08 at 03:13:05 PM GMT 
//

package org.orcid.jaxb.model.message;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;

import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}message-version"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-profile"/>
 *           &lt;element ref="{http://www.orcid.org/ns/orcid}orcid-search-results"/>
 *           &lt;element ref="{http://www.orcid.org/ns/orcid}error-desc"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "messageVersion", "orcidProfile", "orcidSearchResults", "errorDesc" })
@XmlRootElement(name = "orcid-message")
public class OrcidMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_VERSION = "1.0.16";

    @XmlElement(name = "message-version", required = true)
    protected String messageVersion;
    @XmlElement(name = "orcid-profile")
    protected OrcidProfile orcidProfile;
    @XmlElement(name = "orcid-search-results")
    protected OrcidSearchResults orcidSearchResults;
    @XmlElement(name = "error-desc")
    protected ErrorDesc errorDesc;

    public OrcidMessage() {
    }

    public OrcidMessage(OrcidProfile orcidProfile) {
        this.orcidProfile = orcidProfile;
        this.messageVersion = DEFAULT_VERSION;
    }

    /**
     * Gets the value of the messageVersion property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getMessageVersion() {
        return messageVersion;
    }

    /**
     * Sets the value of the messageVersion property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setMessageVersion(String value) {
        this.messageVersion = value;
    }

    /**
     * Gets the value of the orcidProfile property.
     * 
     * @return possible object is {@link OrcidProfile }
     * 
     */
    public OrcidProfile getOrcidProfile() {
        return orcidProfile;
    }

    /**
     * Sets the value of the orcidProfile property.
     * 
     * @param value
     *            allowed object is {@link OrcidProfile }
     * 
     */
    public void setOrcidProfile(OrcidProfile value) {
        this.orcidProfile = value;
    }

    /**
     * Gets the value of the orcidSearchResults property.
     * 
     * @return possible object is {@link OrcidSearchResults }
     * 
     */
    public OrcidSearchResults getOrcidSearchResults() {
        return orcidSearchResults;
    }

    /**
     * Sets the value of the orcidSearchResults property.
     * 
     * @param value
     *            allowed object is {@link OrcidSearchResults }
     * 
     */
    public void setOrcidSearchResults(OrcidSearchResults value) {
        this.orcidSearchResults = value;
    }

    /**
     * Gets the value of the errorDesc property.
     * 
     * @return possible object is {@link ErrorDesc }
     * 
     */
    public ErrorDesc getErrorDesc() {
        return errorDesc;
    }

    /**
     * Sets the value of the errorDesc property.
     * 
     * @param value
     *            allowed object is {@link ErrorDesc }
     * 
     */
    public void setErrorDesc(ErrorDesc value) {
        this.errorDesc = value;
    }

    public Source toSource() {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(getClass());
            return new JAXBSource(context, this);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal JAXB object to source", e);
        }
    }

    @Override
    public String toString() {
        return convertToString(this);
    }

    static String convertToString(Object obj) {
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            return ("Unable to unmarshal because: " + e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        OrcidMessage message = (OrcidMessage) o;

        if (errorDesc != null ? !errorDesc.equals(message.errorDesc) : message.errorDesc != null)
            return false;
        if (messageVersion != null ? !messageVersion.equals(message.messageVersion) : message.messageVersion != null)
            return false;
        if (orcidProfile != null ? !orcidProfile.equals(message.orcidProfile) : message.orcidProfile != null)
            return false;
        if (orcidSearchResults != null ? !orcidSearchResults.equals(message.orcidSearchResults) : message.orcidSearchResults != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = messageVersion != null ? messageVersion.hashCode() : 0;
        result = 31 * result + (orcidProfile != null ? orcidProfile.hashCode() : 0);
        result = 31 * result + (orcidSearchResults != null ? orcidSearchResults.hashCode() : 0);
        result = 31 * result + (errorDesc != null ? errorDesc.hashCode() : 0);
        return result;
    }

    public static OrcidMessage unmarshall(String orcidMessageString) {
        Reader reader = new StringReader(orcidMessageString);
        return unmarshall(reader);
    }

    public static OrcidMessage unmarshall(Reader reader) {
        try {
            JAXBContext context = JAXBContext.newInstance(OrcidMessage.class.getPackage().getName());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (OrcidMessage) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            // XXX Should be more specific exception
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }

}
