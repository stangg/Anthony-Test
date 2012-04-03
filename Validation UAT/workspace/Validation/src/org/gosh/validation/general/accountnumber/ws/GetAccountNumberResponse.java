
package org.gosh.validation.general.accountnumber.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetAccountNumberResult" type="{http://tempuri.org/.NetWebService/WS-GetAccountNumber}Report" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getAccountNumberResult"
})
@XmlRootElement(name = "GetAccountNumberResponse")
public class GetAccountNumberResponse {

    @XmlElement(name = "GetAccountNumberResult")
    protected Report getAccountNumberResult;

    /**
     * Gets the value of the getAccountNumberResult property.
     * 
     * @return
     *     possible object is
     *     {@link Report }
     *     
     */
    public Report getGetAccountNumberResult() {
        return getAccountNumberResult;
    }

    /**
     * Sets the value of the getAccountNumberResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Report }
     *     
     */
    public void setGetAccountNumberResult(Report value) {
        this.getAccountNumberResult = value;
    }

}
