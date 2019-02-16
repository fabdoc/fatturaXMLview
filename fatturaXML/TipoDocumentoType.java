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
 * <p>Classe Java per TipoDocumentoType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoDocumentoType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;length value="4"/>
 *     &lt;enumeration value="TD01"/>
 *     &lt;enumeration value="TD02"/>
 *     &lt;enumeration value="TD03"/>
 *     &lt;enumeration value="TD04"/>
 *     &lt;enumeration value="TD05"/>
 *     &lt;enumeration value="TD06"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TipoDocumentoType")
@XmlEnum
public enum TipoDocumentoType {


    /**
     * Fattura
     * 
     */
    @XmlEnumValue("TD01")
    TD_01("TD01"),

    /**
     * Acconto / anticipo su fattura
     * 
     */
    @XmlEnumValue("TD02")
    TD_02("TD02"),

    /**
     * Acconto / anticipo su parcella
     * 
     */
    @XmlEnumValue("TD03")
    TD_03("TD03"),
//===============================================
    /**
     * Nota di credito
     * 
     */
    @XmlEnumValue("TD04")
    TD_04("TD04"),

    /**
     * Nota di debito
     * 
     */
    @XmlEnumValue("TD05")
    TD_05("TD05"),
//================================================
    /**
     * Parcella
     * 
     */
    @XmlEnumValue("TD06")
    TD_06("TD06");
    private final String value;

    TipoDocumentoType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    /**
     * cerca descrizione corrispondente alla sigla
     * @param value accetta formato TD_xx e TDxx
     * @return descrizione o illegal argument exception
     */
    public static TipoDocumentoType fromValue(String v) {
        for (TipoDocumentoType c: TipoDocumentoType.values()) {
        	if (c.value.equals(v)) {
                return c;
            }
        	if (c.value.equals(v.replaceAll("_", ""))) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    /**
     * cerca descrizione corrispondente alla sigla
     * @param value accetta formato TD_xx e TDxx
     * @return descrizione o illegal argument exception
     */
    public static String getDescription(String value) {
    	String[]	ref		= {
    			"Fattura",
    			"Acconto / Anticipo su Fattura",
    			"Acconto / Anticipo su Parcella",
    			"Nota di Credito",
    			"Nota di Debito",
    			"Parcella"
    	};
    	for (TipoDocumentoType c: TipoDocumentoType.values()) {
//    		System.out.println("TipoDocumentoType.getDescription()"+c+" "+value+" "+c.ordinal());
    		if (c.toString().equals(value) || c.value.equals(value)) {
    			return ref[c.ordinal()];
    		}
    	}
    	throw new IllegalArgumentException(value);
    }
}
