package fatturaXML;

import java.math.BigDecimal;

/**
 * classe per inserire una riga del documento
 * @author f.andolfi
 */
public class RigaDocumento {

	private int _numeroLinea;
	private int _quantita;

	private String _descrizione;
	private String _tipo;
	private String _tipoRA;
	private String _causaleRA;
	
	private BigDecimal _importoUnitario;
	private BigDecimal _importoTotale;
	
	private BigDecimal _aliquota_R_A;
	private BigDecimal _importo_R_A;

	private BigDecimal _aliquotaIVA;
	private BigDecimal _importoIVA;

	private NaturaType _naturaEsenzioneIVA = null;
	private String _naturaEsenzioneRiferimentoNormativo = null;

	/**
	 * imposta i dati di una riga per acconto parcella
	 * @param tipoDoc
	 * @param descrizione
	 * @param importo
	 */
	public RigaDocumento(TipoDocumentoType tipoDoc, String descrizione, double importo) {
		if(tipoDoc.equals(TipoDocumentoType.TD_03) == false)
			throw new IllegalArgumentException(COMMON.SHOULDBE_DOC_KIND_IT+TipoDocumentoType.getDescription(TipoDocumentoType.TD_03.toString()));
		_tipo = tipoDoc.toString();
		_numeroLinea = 1;
		_descrizione = descrizione;
		_importoUnitario = new BigDecimal(COMMON.decimalformat.format(importo));
		_importoTotale = _importoUnitario;
		//di default
		_aliquotaIVA = new BigDecimal(COMMON.decimalformat.format(0));
		_aliquota_R_A = new BigDecimal(COMMON.decimalformat.format(0));
		//non soggetto iva
		_naturaEsenzioneIVA = NaturaType.N_2;
		_naturaEsenzioneRiferimentoNormativo = RigaRiepilogo.get_RifEsenzioneMedica();
	}

	/**
	 * imposta i dati di una riga per saldo parcella<p>
	 * l'importo unitario e' obbligatoriamente importo totale diviso quantita'<p>
	 * (per importi differenti su stesso articolo usare righe differenti)<p>
	 * iva sempre zero
	 * @param tipo accetta:
	 * <li>TipoDocumentoType.TD_06 saldo
	 * <li>TipoDocumentoType.TD_04 nota credito
	 * @param descrizione
	 * @param qt
	 * @param importo
	 */
	public RigaDocumento(TipoDocumentoType tipo, int riga, String descrizione, int qt, double importo) {
		if(tipo.equals(TipoDocumentoType.TD_06) == false &&
				tipo.equals(TipoDocumentoType.TD_04) == false	)
			throw new IllegalArgumentException(COMMON.SHOULDBE_DOC_KIND_IT +
					TipoDocumentoType.getDescription(TipoDocumentoType.TD_06.toString()) +
					COMMON.OR_IT +
					TipoDocumentoType.getDescription(TipoDocumentoType.TD_04.toString()) );
		_tipo = tipo.toString();
		_numeroLinea = riga;
		_descrizione = descrizione;
		if(qt != 0)
			_importoUnitario = new BigDecimal(COMMON.decimalformat.format(importo / (double) qt ));
		else
			_importoUnitario = new BigDecimal(COMMON.decimalformat.format(0));
		_quantita = qt;
		_importoTotale = new BigDecimal(COMMON.decimalformat.format(importo));
		//di default
		_aliquota_R_A = new BigDecimal(COMMON.decimalformat.format(0));

		//non soggetto iva
		_aliquotaIVA = new BigDecimal(COMMON.decimalformat.format(0));
		_naturaEsenzioneIVA = NaturaType.N_2;
		_naturaEsenzioneRiferimentoNormativo = RigaRiepilogo.get_RifEsenzioneMedica();
	}

	/**
	 * imposta i dati di una riga per saldo fattura<p>
	 * l'importo unitario e' obbligatoriamente importo totale diviso quantita'<p>
	 * (per importi differenti su stesso articolo usare righe differenti)<p>
	 * @param tipo accetta:
	 * <li>TipoDocumentoType.TD_01 Fattura TD_04 nota credito
	 * @param descrizione String
	 * @param qt int quantita' merce (0-xx)
	 * @param importo double
	 * @param dettaglioIVA double[] {qt, importo} 
	 * @param naturaEsenzioneIVA double[] String identificativo del tipo di iva, String riferimentoNormativo}
	 */
	public RigaDocumento(TipoDocumentoType tipo, int riga, String descrizione, int qt, double importo, double[] dettaglioIVA, String[] naturaEsenzioneIVA) {
		if(tipo.equals(TipoDocumentoType.TD_01) == false && tipo.equals(TipoDocumentoType.TD_04) == false )
			throw new IllegalArgumentException(
					COMMON.SHOULDBE_DOC_KIND_IT +
					TipoDocumentoType.getDescription(TipoDocumentoType.TD_01.toString()) + " OR " +
					TipoDocumentoType.getDescription(TipoDocumentoType.TD_04.toString()) );
		_tipo = tipo.toString();
		_numeroLinea = riga;
		_descrizione = descrizione;
		if(qt != 0)
			_importoUnitario = new BigDecimal(COMMON.decimalformat.format(importo / (double) qt ));
		else
			_importoUnitario = new BigDecimal(COMMON.decimalformat.format(0));
		_quantita = qt;
		_importoTotale = new BigDecimal(COMMON.decimalformat.format(importo));
		_aliquota_R_A = new BigDecimal(COMMON.decimalformat.format(0));
		_aliquotaIVA = new BigDecimal(COMMON.decimalformat.format(dettaglioIVA[0]));
		_importoIVA = new BigDecimal(COMMON.decimalformat.format(dettaglioIVA[1]));
		if(naturaEsenzioneIVA != null) {
			_naturaEsenzioneIVA = NaturaType.fromValue(naturaEsenzioneIVA[0]);
			_naturaEsenzioneRiferimentoNormativo = naturaEsenzioneIVA[1];
		}
	}

	/**
	 * imposta i dati per riga ritenuta (su saldo)<p>
	 * NON PUO' ESSERE LA PRIMA RIGA
	 * iva sempre zero
	 * @param tipo accetta:
	 * <li>TipoRitenutaType.RT_01 ritenuta
	 * @param descrizione
	 * @param qt
	 * @param importo
	 * @param aliquotaRA
	 * @param importoRA
	 */
	public RigaDocumento(TipoRitenutaType tipo, int riga, String descrizione, double importo, double aliquotaRA, double importoRA, String causaleRA) {
		if(tipo.equals(TipoRitenutaType.RT_01) == false &&
		   tipo.equals(TipoRitenutaType.RT_02) == false)
			throw new IllegalArgumentException(COMMON.SHOULDBE_DOC_KIND_IT +
					TipoRitenutaType.getDescription(TipoRitenutaType.RT_01.toString()) + COMMON.OR_IT +
					TipoRitenutaType.getDescription(TipoRitenutaType.RT_02.toString()) );
//		System.out.println("RigaDocumento.RigaDocumento() RITENUTA ");
		_tipo = tipo.toString();
		_numeroLinea = riga;
		_descrizione = descrizione;
		_importoUnitario = new BigDecimal(COMMON.decimalformat.format(importo));
		_quantita = 1;
		_importoTotale = new BigDecimal(COMMON.decimalformat.format(importo));
		//di default
		_aliquotaIVA = new BigDecimal(COMMON.decimalformat.format(0));
		//ritenuta
		_aliquota_R_A = new BigDecimal(COMMON.decimalformat.format(aliquotaRA));
		_importo_R_A = new BigDecimal(COMMON.decimalformat.format(importoRA));
		_tipoRA = TipoRitenutaType.RT_01.toString();
		_causaleRA = causaleRA;
		//non soggetto iva
		_naturaEsenzioneIVA = NaturaType.N_2;
		_naturaEsenzioneRiferimentoNormativo = RigaRiepilogo.get_RifEsenzioneMedica();
	}

	public boolean isRitenuta() {
//		System.out.println("RigaDocumento.isRitenuta()" + _tipo + " " + _descrizione+" "+_tipo.startsWith("RT"));
		try {
			return _tipo.startsWith("RT");
		} catch (Exception e) { }
		return false;
	}
	public String get_causaleRA() {
		return _causaleRA;
	}
	public String get_tipoRA() {
		return _tipoRA;
	}
	public TipoDocumentoType get_tipo() {
		try {
			return TipoDocumentoType.fromValue(_tipo);
		} catch (Exception e) { }
		return TipoDocumentoType.TD_01;
	}
	public int get_numeroLinea() {
		return _numeroLinea;
	}

	public String get_descrizione() {
		return _descrizione;
	}

	public BigDecimal get_importoUnitario() {
		return _importoUnitario;
	}

	public BigDecimal get_importoTotale() {
		return _importoTotale;
	}

	public BigDecimal get_aliquotaRA() {
		return _aliquota_R_A;
	}

	public BigDecimal get_importoRA() {
		return _importo_R_A;
	}

	public BigDecimal get_aliquotaIVA() {
		return _aliquotaIVA;
	}

	public BigDecimal get_importoIVA() {
		return _importoIVA;
	}
	
	public NaturaType get_naturaEsenzioneIVA() {
		return _naturaEsenzioneIVA;
	}

	public String get_RiferimentoNormativoEsenzione() {
		return _naturaEsenzioneRiferimentoNormativo;
	}

	public BigDecimal get_quantita() {
		return new BigDecimal(COMMON.decimalformat.format(_quantita));
	}

	public void set_TipoDoc(TipoDocumentoType tipoDoc) {
		_tipo = tipoDoc.toString();
	}
}
