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
 * <p>Classe Java per TipoRitenutaType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoRitenutaType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;length value="4"/>
 *     &lt;enumeration value="RT01"/>
 *     &lt;enumeration value="RT02"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TipoRitenutaType")
@XmlEnum
public enum TipoRitenutaType {


    /**
     * Ritenuta di acconto persone fisiche
     * 
     */
    @XmlEnumValue("RT01")
    RT_01("RT01"),

    /**
     * Ritenuta di acconto persone giuridiche
     * 
     */
    @XmlEnumValue("RT02")
    RT_02("RT02");
    private final String value;

    TipoRitenutaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TipoRitenutaType fromValue(String value) {
        for (TipoRitenutaType tr: TipoRitenutaType.values()) {
            if (tr.value.equals(value)  || tr.value.equals(value.replaceAll("_", ""))) {
                return tr;
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * cerca descrizione corrispondente alla sigla
     * @param value accetta formato TD_xx e TDxx
     * @return descrizione o illegal argument exception
     */
    public static String getDescription(String value) {
    	String[]	ref		= {
    			"Ritenuta di acconto persone fisiche",
    			"Ritenuta di acconto persone giuridiche"
    	};
    	for (TipoRitenutaType c: TipoRitenutaType.values()) {
//    		System.out.println("TipoDocumentoType.getDescription()"+c+" "+value+" "+c.ordinal());
    		if (c.toString().equals(value) || c.value.equals(value)) {
    			return ref[c.ordinal()];
    		}
    	}
    	throw new IllegalArgumentException(value);
    }
}
