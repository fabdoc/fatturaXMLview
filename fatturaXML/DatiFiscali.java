package fatturaXML;

public class DatiFiscali {

	private String[] dati = new String[15];

	public DatiFiscali(String statoPIVA, String partitaiva, String codicefiscale,
			String cognome, String nome, String regimefiscale_o_codicefatturante,
			String indirizzo, String cap, String comune, String prov, String statoIndirizzo,
			String telefono, String email,
			String valuta, String pec,
			boolean fatturante) {
		dati[C_NAZIONE] = statoPIVA;
		dati[C_PARTITAIVA] = partitaiva;
		dati[C_CODICEFISCALE] = codicefiscale;
		dati[C_COGNOME] = cognome;
		dati[C_NOME] = nome;
		
		dati[C_REGIMEFISCALE] = regimefiscale_o_codicefatturante;
//		System.out.println("DatiFiscali.DatiFiscali()"+regimefiscale_o_codicefatturante);
		//un po' di controlli di coerenza:
		if( regimefiscale_o_codicefatturante == null || regimefiscale_o_codicefatturante.isEmpty() )
			throw new IllegalArgumentException(fatturante ? COMMON.INVALID_REGIME_IT : COMMON.INVALID_DESTINATARIO_CODE_IT);
		if(fatturante) {
			// il codice del regime fiscale deve essere di 4 caratteri
			if(regimefiscale_o_codicefatturante.length() != 4)
				throw new IllegalArgumentException(COMMON.INVALID_REGIME_IT);
		}
		else {
			//il codice destinatario deve essere lungo 6 caratteri per le Pubbliche amministrazioni
			//o 7 caratteri per i privati
//			System.out.println("DatiFiscali.DatiFiscali()"+regimefiscale_o_codicefatturante.length() );
			
			if(regimefiscale_o_codicefatturante.length() < 6 || regimefiscale_o_codicefatturante.length() > 7)
				throw new IllegalArgumentException(COMMON.INVALID_DESTINATARIO_CODE_IT);
			//paziente deve avere codice fiscale
			if(regimefiscale_o_codicefatturante.equals("0000000")) {
				if (dati[C_CODICEFISCALE] == null || dati[C_CODICEFISCALE].length() != 16)
					throw new IllegalArgumentException(COMMON.INVALID_DESTINATARIO_CODE_IT);
			}
			//cliente deve avere partita iva
			else {
				if (dati[C_PARTITAIVA] == null || dati[C_PARTITAIVA].length() != 11)
					throw new IllegalArgumentException(COMMON.INVALID_DESTINATARIO_CODE_IT);
				if (dati[C_NAZIONE] == null || dati[C_NAZIONE].isEmpty())
					throw new IllegalArgumentException(COMMON.INVALID_NATION_CODE_IT);
			}
		}
		//partitaiva
		dati[SC_INDIRIZZO] = indirizzo;
		dati[SC_CAP] = cap;
		dati[SC_COMUNE] = comune;
		dati[SC_PROV] = prov;
		dati[SC_NAZIONE] = statoIndirizzo;
		
		dati[SC_TELEFONO] = telefono;
		dati[SC_EMAIL] = email;
		
		dati[P_DIVISA] = valuta;
		dati[D_PEC] = pec;
	}

	public String[] getDati() {
		return dati;
	}
	
	public static final int
	//CEDENTE o committente
		C_NAZIONE = 0;
	public static final int C_PARTITAIVA = 1;
	public static final int C_CODICEFISCALE = 2;
	static final int C_COGNOME = 3;
	static final int C_DENOMINAZIONE = 3;
	static final int C_NOME = 4;
	static final int C_REGIMEFISCALE = 5;
	static final int //SEDE cedente o committente
	SC_INDIRIZZO = 6;
	static final int SC_CAP = 7;
	static final int SC_COMUNE = 8;
	static final int SC_PROV = 9;
	static final int SC_NAZIONE = 10;
	static final int //SEDE CONTATTI
	SC_TELEFONO = 11;
	static final int SC_EMAIL = 12;
	public static final int // DIVISA PAGAMENTO
	P_DIVISA = 13;
	public static final int //SOLO PER DESTINATARIO	
	D_CODICEDESTINATARIO = 5;
	static final int D_PEC = 14;
}
