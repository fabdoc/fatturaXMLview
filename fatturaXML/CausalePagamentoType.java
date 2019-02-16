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
 * <p>Classe Java per CausalePagamentoType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CausalePagamentoType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="A"/>
 *     &lt;enumeration value="B"/>
 *     &lt;enumeration value="C"/>
 *     &lt;enumeration value="D"/>
 *     &lt;enumeration value="E"/>
 *     &lt;enumeration value="G"/>
 *     &lt;enumeration value="H"/>
 *     &lt;enumeration value="I"/>
 *     &lt;enumeration value="L"/>
 *     &lt;enumeration value="M"/>
 *     &lt;enumeration value="N"/>
 *     &lt;enumeration value="O"/>
 *     &lt;enumeration value="P"/>
 *     &lt;enumeration value="Q"/>
 *     &lt;enumeration value="R"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="T"/>
 *     &lt;enumeration value="U"/>
 *     &lt;enumeration value="V"/>
 *     &lt;enumeration value="W"/>
 *     &lt;enumeration value="X"/>
 *     &lt;enumeration value="Y"/>
 *     &lt;enumeration value="Z"/>
 *     &lt;enumeration value="L1"/>
 *     &lt;enumeration value="M1"/>
 *     &lt;enumeration value="O1"/>
 *     &lt;enumeration value="V1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CausalePagamentoType")
@XmlEnum
public enum CausalePagamentoType {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    G("G"),
    H("H"),
    I("I"),
    L("L"),
    M("M"),
    N("N"),
    O("O"),
    P("P"),
    Q("Q"),
    R("R"),
    S("S"),
    T("T"),
    U("U"),
    V("V"),
    W("W"),
    X("X"),
    Y("Y"),
    Z("Z"),
    @XmlEnumValue("L1")
    L_1("L1"),
    @XmlEnumValue("M1")
    M_1("M1"),
    @XmlEnumValue("O1")
    O_1("O1"),
    @XmlEnumValue("V1")
    V_1("V1");
    private final String value;

    CausalePagamentoType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CausalePagamentoType fromValue(String v) {
        for (CausalePagamentoType c: CausalePagamentoType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

  /**
   * cerca descrizione corrispondente alla sigla
   * @param value accetta formato LETTERA_xx e LETTERAxx
   * @return descrizione o illegal argument exception
   */
  public static String getDescription(String value) {
  	String[]	ref		= {
  		/*a*/ "prestazioni di lavoro autonomo rientranti nell’esercizio di arte o professione abituale",//a
 		/*b*/ "utilizzazione economica, da parte dell’autore o dell’inventore, di opere dell’ingegno, di brevetti industriali e di processi, formule o informazioni relativi ad esperienze acquisite in campo industriale, commerciale o scientifico",
 		/*c*/ "utili derivanti da contratti di associazione in partecipazione e da contratti di cointeressenza, quando l’apporto è costituito esclusivamente dalla prestazione di lavoro",
 		/*d*/ "utili spettanti ai soci promotori ed ai soci fondatori delle società di capitali",
 		/*e*/ "levata di protesti cambiari da parte dei segretari comunali",
// 		/*f*/ "prestazioni rese dagli sportivi con contratto di lavoro autonomo",
 		/*g*/ "indennità corrisposte per la cessazione di attività sportiva professionale",
 		/*h*/ "indennità corrisposte per la cessazione dei rapporti di agenzia delle persone fisiche e delle società di persone con esclusione delle somme maturate entro il 31 /12/2003, già imputate per competenza e tassate come reddito d’impresa",
 		/*i*/ "indennità corrisposte per la cessazione da funzioni notarili",
// 		/*j*/ "compensi corrisposti ai raccoglitori occasionali di tartufi non identificati ai fini dell’imposta sul valore aggiunto, in relazione alla cessione di tartufi",
// 		/*k*/ "indennità corrisposte ai giudici onorari di pace e ai vice procuratori onorari",
 		/*L*/ "utilizzazione economica, da parte di soggetto diverso dall’autore o dall’inventore, di opere dell’ingegno, di brevetti industriali e di processi, formule e informazioni relative a esperienze acquisite in campo industriale, commerciale o scientifico, che sono percepiti dagli aventi causa a titolo gratuito (eredi e legatari dell’autore e inventore)",
 		/*M*/ "prestazioni di lavoro autonomo non esercitate abitualmente,",
 		/*N*/ "indennità di trasferta, rimborso forfetario di spese, premi e compensi erogati nell’esercizio diretto di attività sportive dilettantistiche o in relazione a rapporti di collaborazione coordinata e continuativa di carattere amministrativo-gestionale di natura non professionale resi a favore di società e associazioni sportive dilettantistiche o di cori, bande e filodrammatiche da parte del direttore e dei collaboratori tecnici",
 		/*O*/ "prestazioni di lavoro autonomo non esercitate abitualmente, per le quali non sussiste l’obbligo di iscrizione alla gestione separata (Circ. INPS n. 104/2001 Nota 1)",
 		/*P*/ "compensi corrisposti a soggetti non residenti privi di stabile organizzazione per l'uso o la concessione in uso di attrezzature industriali, commerciali o scientifiche che si trovano nel territorio dello stato ovvero a società svizzere o stabili organizzazioni di società svizzere, ecc.,",
 		/*Q*/ "provvigioni corrisposte ad agente o rappresentante di commercio monomandatario",
 		/*R*/ "provvigioni corrisposte ad agente o rappresentante di commercio plurimandatario",
 		/*S*/ "provvigioni corrisposte a commissionario",
 		/*T*/ "provvigioni corrisposte a mediatore",
 		/*U*/ "provvigioni corrisposte a procacciatore di affari",
 		/*V*/ "provvigioni corrisposte a incaricato per le vendite a domicilio; provvigioni corrisposte a incaricato per la vendita porta a porta e per la vendita ambulante di giornali quotidiani e periodici (L. 25 febbraio 1987, n. 67)",
 		/*W*/ "corrispettivi erogati nel 2013 per prestazioni relative a contratti d’appalto cui si sono resi applicabili le disposizioni contenute nell’art.25-ter del D.P.R. n. 600 del 1973",
 		/*X*/ "canoni corrisposti nel 2004 da società o enti residenti ovvero da stabili organizzazioni di società estere di cui all'art 26-quater, comma 1, lett. a) e b), D.P.R. 600/1973, a società o stabili organizzazioni di società, situate in altro stato membro dell'Unione Europea in presenza dei requisiti di cui al citato art 26-quater, del D.P.R. 600/1973, per i quali è stato effettuato, nell’anno 2006, il rimborso della ritenuta ai sensi dell'art 4 del D. Lgs. 30/05/2005, n. 143",
 		/*Y*/ "canoni corrisposti dal 1° gennaio 2005 al 26 luglio 2005 da società o enti residenti ovvero da stabili organizzazioni di società estere di cui all’art. 26 quater, comma 1, lett. a) e b) del D.P.R. 600/1973, a società o stabili organizzazioni di società, situate in altro stato membro dell’Unione Europea in presenza dei requisiti di cui al citato art. 26 quater, del D.P.R. 600/1973, per i quali è stato effettuato, nell’anno 2006, il rimborso della ritenuta ai sensi dell’art. 4 del D. Lgs. 30 maggio 2005, n. 143",
 		/*Z*/ "titolo diverso dai precedenti",
 		/*L1*/ "utilizzazione economica di opere dell’ingegno, di brevetti industriali e di processi, formule e informazioni relative ad esperienze acquisite in campo industriale, commerciale o scientifico percepiti da soggetti che abbiano acquisito a titolo oneroso i diritti alla loro utilizzazione",
 		/*m1*/ "prestazioni di lavoro autonomo non esercitate abitualmente,derivanti dall’assunzione di obblighi di fare, di non fare o permettere",
 		/*O1*/ "redditi derivanti dall’assunzione di obblighi di fare, di non fare o permettere, per le quali non sussiste l’obbligo di iscrizione alla gestione separata (Circ. INPS n. 104/2001 Nota 1)",
 		/*V1*/ "redditi derivanti da attività commerciali non esercitate abitualmente (ad esempio, provvigioni corrisposte per prestazioni occasionali ad agente o rappresentante di commercio, mediatore, procacciatore d’affari)",
 	};

 	for (CausalePagamentoType c: CausalePagamentoType.values()) {
//  		System.out.println("TipoDocumentoType.getDescription()"+c+" "+value+" "+c.ordinal());
  		if (c.toString().equals(value) || c.value.equals(value)) {
  			return ref[c.ordinal()];
  		}
  	}
  	throw new IllegalArgumentException(value);
  }
  
//  public static void main(String[] args) {
//	  for (CausalePagamentoType c: CausalePagamentoType.values()) {
//		System.out.println(c.ordinal()+"\t"+c.value+" \t"+CausalePagamentoType.getDescription(c.name()));
//	  }
//  }
}
