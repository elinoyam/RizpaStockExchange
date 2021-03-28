//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.03.28 at 05:32:00 AM IDT 
//


package jaxb.schema.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jaxb.schema.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RseCompanyName_QNAME = new QName("", "rse-company-name");
    private final static QName _RseSymbol_QNAME = new QName("", "rse-symbol");
    private final static QName _RsePrice_QNAME = new QName("", "rse-price");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jaxb.schema.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RseStocks }
     * 
     */
    public RseStocks createRseStocks() {
        return new RseStocks();
    }

    /**
     * Create an instance of {@link RseStock }
     * 
     */
    public RseStock createRseStock() {
        return new RseStock();
    }

    /**
     * Create an instance of {@link RizpaStockExchangeDescriptor }
     * 
     */
    public RizpaStockExchangeDescriptor createRizpaStockExchangeDescriptor() {
        return new RizpaStockExchangeDescriptor();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "rse-company-name")
    public JAXBElement<String> createRseCompanyName(String value) {
        return new JAXBElement<String>(_RseCompanyName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "rse-symbol")
    public JAXBElement<String> createRseSymbol(String value) {
        return new JAXBElement<String>(_RseSymbol_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "rse-price")
    public JAXBElement<Integer> createRsePrice(Integer value) {
        return new JAXBElement<Integer>(_RsePrice_QNAME, Integer.class, null, value);
    }

}
