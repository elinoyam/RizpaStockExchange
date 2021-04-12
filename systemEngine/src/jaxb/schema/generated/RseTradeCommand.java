//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.03.29 at 04:11:37 PM IDT 
//


package jaxb.schema.generated;

import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element ref="{}rse-symbol"/>
 *         &lt;element ref="{}rse-company-name"/>
 *         &lt;element ref="{}rse-price"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "rse-trade-command")
public class RseTradeCommand {

    @XmlElement(name = "rse-dir", required = true)
    protected String rseDir;
    @XmlElement(name = "rse-quantity", required = true)
    protected int rseQuantity;
    @XmlElement(name = "rse-symbol", required = true)
    protected String rseSymbol;
    @XmlElement(name = "rse-price", required = true)
    protected float rsePrice;
    @XmlElement(name = "rse-type", required = true)
    protected String rseType;
    @XmlElement(name = "rse-date-and-time",required = true)
    protected String rseDateTime;

    public String getRseDir() {
        return rseDir;
    }

    public int getRseQuantity() {
        return rseQuantity;
    }

    public String getRseSymbol() {
        return rseSymbol;
    }

    public float getRsePrice() {
        return rsePrice;
    }

    public String getRseType() {
        return rseType;
    }

    public String getRseDateTime() {
        return rseDateTime;
    }

    public void setRseDir(String rseDir) {
        this.rseDir = rseDir;
    }

    public void setRseQuantity(int rseQuantity) {
        this.rseQuantity = rseQuantity;
    }

    public void setRseSymbol(String rseSymbol) {
        this.rseSymbol = rseSymbol;
    }

    public void setRsePrice(float rsePrice) {
        this.rsePrice = rsePrice;
    }

    public void setRseType(String rseType) {
        this.rseType = rseType;
    }

    public void setRseDateTime(String rseDateTime) {
        this.rseDateTime = rseDateTime;
    }
}
