package fatturaXML;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public interface COMMON {
	String SHOULDBE_DOC_KIND_IT = "Deve essere un documento ";
	String INCOHERENT_DOCKIND_IT = "Mancata coerenza Tipo Dato documento";
	String OR_IT = " oppure ";
	String ROWEXISTS_IT = "Riga già presente";

	DecimalFormat decimalformat = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));
	String INVALID_REGIME_IT = "Regime Fiscale non valido";
	String INVALID_DESTINATARIO_CODE_IT = "Codice Destinatario non valido";
	String INVALID_NATION_CODE_IT = "Codice Nazione cessionario non valida";
}
