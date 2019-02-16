package fatturaXML;

import java.math.BigDecimal;

/**
 * classe per creare un tag di riepilogo
 * @author f.andolfi
 */
public class RigaRiepilogo {
	private BigDecimal _aliquotaIVA = new BigDecimal(COMMON.decimalformat.format(0));
	private BigDecimal _imponibileImporto;
	private BigDecimal _imposta = new BigDecimal(COMMON.decimalformat.format(0));
	private EsigibilitaIVAType _esigibilitaIVA = EsigibilitaIVAType.I;
	private NaturaType _naturaIVA;
	private String _RiferimentoNormativo;
	
	/**
	 * riga per inserire un riepilogo di acconto o saldo parcella: esenzione e riferimento impostati in automatico
	 * @param imponibile
	 */
	public RigaRiepilogo(double imponibile) {
		_imponibileImporto = new BigDecimal(COMMON.decimalformat.format(imponibile));
		_naturaIVA = NaturaType.N_2;
		_RiferimentoNormativo = get_RifEsenzioneMedica();
	}

	/**
	 * riga per inserire un riepilogo di fattura
	 * @param imponibile
	 * @param aliquotaIVA
	 * @param naturaIVA
	 * @param riferimentoNormativo
	 */
	public RigaRiepilogo(double imponibile, BigDecimal aliquotaIVA, BigDecimal ImpostaIVA, NaturaType naturaIVA, String riferimentoNormativo) {
		_imponibileImporto = new BigDecimal(COMMON.decimalformat.format(imponibile));

		_naturaIVA = naturaIVA;
		_RiferimentoNormativo = riferimentoNormativo;

		_aliquotaIVA = new BigDecimal(COMMON.decimalformat.format(aliquotaIVA));
		System.out.println("RigaRiepilogo.RigaRiepilogo()"+ImpostaIVA);
		if(ImpostaIVA != null)
			_imposta = new BigDecimal(COMMON.decimalformat.format(ImpostaIVA));
	}
	
	public BigDecimal get_aliquotaIVA() {
		return _aliquotaIVA;
	}
	
	public EsigibilitaIVAType get_esigibilitaIVA() {
		return _esigibilitaIVA;
	}
	
	public BigDecimal get_imponibileImporto() {
		return _imponibileImporto;
	}
	
	public BigDecimal get_imposta() {
		return _imposta;
	}
	
	public NaturaType get_naturaIVA() {
		return _naturaIVA;
	}

	public void set_RiferimentoNormativo(String rif) {
		this._RiferimentoNormativo = rif;
	}

	public String get_RiferimentoNormativo() {
		return _RiferimentoNormativo;
	}
	
	/**
	 * per cambiare riferimento standard di esenzione medica:
	 * <br>il default e'<br>
	 * "Esente I.V.A. ai sensi dell'Art.10 comma 1, n.18 del D.P.R. n.633/1972 e successive modificazioni"
	 * @param rif
	 */
	public static void set_riferimentoStandardEsenzioneMedica(String rif) {
		RigaRiepilogo._riferimentoStandardEsenzioneMedica = rif;
	}

	public static String get_RifEsenzioneMedica() {
		return _riferimentoStandardEsenzioneMedica;
	}
	
	private static String _riferimentoStandardEsenzioneMedica
	= "Esente I.V.A. ai sensi dell'Art.10 comma 1, n.18 del D.P.R. n.633/1972 e successive modificazioni";
}
