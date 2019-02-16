//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.06.24 alle 02:37:24 PM CEST 
//


package fatturaXML;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per CondizioniPagamentoType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CondizioniPagamentoType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;minLength value="4"/>
 *     &lt;maxLength value="4"/>
 *     &lt;enumeration value="TP01"/>
 *     &lt;enumeration value="TP02"/>
 *     &lt;enumeration value="TP03"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CondizioniPagamentoType")
@XmlEnum
public enum CondizioniPagamentoType {


    /**
     * pagamento a rate
     * 
     */
    @XmlEnumValue("TP01")
    TP_01("TP01"),

    /**
     * pagamento completo
     * 
     */
    @XmlEnumValue("TP02")
    TP_02("TP02"),

    /**
     * anticipo
     * 
     */
    @XmlEnumValue("TP03")
    TP_03("TP03");
    private final String value;

    CondizioniPagamentoType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CondizioniPagamentoType fromValue(String v) {
        for (CondizioniPagamentoType c: CondizioniPagamentoType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    /**
     * cerca descrizione corrispondente alla sigla
     * @param value
     * @return descrizione o illegal argument exception
     */
    public static String getDescription(String value) {
    	String[]	ref		= {
    			"pagamento a rate",
    			"pagamento completo",
    			"anticipo"
    	};
    	for (CondizioniPagamentoType c: CondizioniPagamentoType.values()) {
//    		System.out.println("TipoDocumentoType.getDescription()"+c+" "+value+" "+c.ordinal());
    		if (c.toString().equals(value) || c.value().equals(value)) {
    			return ref[c.ordinal()];
    		}
    	}
    	throw new IllegalArgumentException(value);
    }
}
