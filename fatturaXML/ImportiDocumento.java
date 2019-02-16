package fatturaXML;

import java.math.BigDecimal;

public class ImportiDocumento {

	TipoDocumentoType _tipoDocumento;
	private BigDecimal _saldo;
	private BigDecimal _bollo;
	private BigDecimal _arrotondamento;
	private BigDecimal _sconto;
	
	/**
	 * imposta gruppo importi per una parcella di acconto
	 * @param tipoDocumento
	 * @param acconto
	 * @param bollo
	 */
	public ImportiDocumento(TipoDocumentoType tipoDocumento, double acconto, double bollo) {
//		System.out.println("ImportiDocumento.ImportiDocumento() ACCONTO PARCELLA");
		if(tipoDocumento.equals(TipoDocumentoType.TD_03) == false)
			throw new IllegalArgumentException(COMMON.SHOULDBE_DOC_KIND_IT+TipoDocumentoType.getDescription(TipoDocumentoType.TD_03.toString()));
		_saldo = new BigDecimal(COMMON.decimalformat.format(acconto));

		_bollo = new BigDecimal(COMMON.decimalformat.format(Math.abs(bollo)));
		if(bollo < 0) {
			_sconto = new BigDecimal(COMMON.decimalformat.format(Math.abs(bollo)));
		}
		else {
			_sconto = new BigDecimal(COMMON.decimalformat.format(0));
		}
		_arrotondamento = new BigDecimal(0);
		
		_tipoDocumento = TipoDocumentoType.TD_03;
	}

	/**
	 * imposta gruppo di importo per una parcella di saldo
	 * @param tipoDocumento
	 * @param saldo
	 * @param bollo
	 * @param sconto
	 */
	public ImportiDocumento(TipoDocumentoType tipoDocumento, double saldo, double bollo, double sconto) {
//		System.out.println("ImportiDocumento.ImportiDocumento() SALDO PARCELLA");
		if(tipoDocumento.equals(TipoDocumentoType.TD_06) == false)
			throw new IllegalArgumentException(COMMON.SHOULDBE_DOC_KIND_IT+TipoDocumentoType.getDescription(TipoDocumentoType.TD_06.toString()));
		_saldo = new BigDecimal(COMMON.decimalformat.format(saldo));
		_bollo = new BigDecimal(COMMON.decimalformat.format(Math.abs(bollo)));
		if(bollo < 0) {
			_sconto = new BigDecimal(COMMON.decimalformat.format(Math.abs(bollo+sconto)));
		}
		else {
			_sconto = new BigDecimal(COMMON.decimalformat.format(sconto));
		}
		_tipoDocumento = TipoDocumentoType.TD_06;
	}
	
	/**
	 * imposta gruppo di importi per il riepilogo di una fattura
	 * @param tipoDocumento
	 * @param saldo (lordo)
	 * @param bollo
	 * @param sconto
	 * @param arrotondamento
	 */
	public ImportiDocumento(TipoDocumentoType tipoDocumento, double saldo, double bollo, double sconto, double arrotondamento) {
//		System.out.println("ImportiDocumento.ImportiDocumento() FATTURA");
		if( tipoDocumento.equals(TipoDocumentoType.TD_01) == false && tipoDocumento.equals(TipoDocumentoType.TD_04) == false )
			throw new IllegalArgumentException(COMMON.SHOULDBE_DOC_KIND_IT+TipoDocumentoType.getDescription(TipoDocumentoType.TD_01.toString())
					+COMMON.OR_IT+TipoDocumentoType.getDescription(TipoDocumentoType.TD_04.toString()));
		_saldo = new BigDecimal(COMMON.decimalformat.format(saldo));
		_bollo = new BigDecimal(COMMON.decimalformat.format(bollo));
		_sconto = new BigDecimal(COMMON.decimalformat.format(sconto));
		_arrotondamento =  new BigDecimal(COMMON.decimalformat.format(arrotondamento));
		_tipoDocumento = tipoDocumento;
	}
	
	public BigDecimal get_saldo() {
		return _saldo;
	}
	
	public BigDecimal get_bollo() {
		return _bollo;
	}
	
	public BigDecimal get_sconto() {
		return _sconto;
	}
	
	public BigDecimal get_arrotondamento() {
		return _arrotondamento;
	}

	public TipoDocumentoType get_tipoDocumento() {
		return _tipoDocumento;
	}
}
