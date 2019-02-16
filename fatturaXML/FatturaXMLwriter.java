package fatturaXML;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

public class FatturaXMLwriter {

	String _path;
	/**
	 * inizializza impostando il path di salvataggio
	 * @param path
	 */
	public FatturaXMLwriter(String path) {
		_path = path;
	}
	
	/**
	 * @param cedente {@link DatiFiscali}
	 * @param ordineAppartenenza String[] con i dati dell'ordine (ad es. dei medici) oppure null
	 * @param numFatt String
	 * @param dataFatt Calendar
	 * @param destinatario {@link  DatiFiscali}
	 * @param importi {@link ImportiDocumento}
	 * @param righe ArrayList<{@link RigaDocumento}>
	 * @param causale String
	 * @param datiPA String[] con i dati per la P.A.
	 * 
	 * @return String filename
	 * 
	 * @throws Exception 
	 */
	public String getXML(
			DatiFiscali cedente,
			String[] ordineAppartenenza,
			String numFatt,
			Calendar dataFatt,
			DatiFiscali destinatario,
			ImportiDocumento importi,
			ArrayList<RigaDocumento> righe,
			String causale,
			String[] datiPA) throws Exception
	{
		//controllo base
		if(righe.get(0).get_tipo().equals(importi.get_tipoDocumento()) == false) {
			throw new IllegalArgumentException(COMMON.INCOHERENT_DOCKIND_IT);
		}
		String ext = ".xml";

		String progressivo = numFatt.replaceAll("/", "");// + String.format("%X", dataFatt.get(Calendar.YEAR));
		String[] datiCedente = cedente.getDati();
		String[] datiDestinatario = destinatario.getDati();
		String filename = datiCedente[DatiFiscali.C_NAZIONE] + datiCedente[DatiFiscali.C_PARTITAIVA] + "_" + progressivo + ext;
		try {
			// create JAXBContext which will be used to create a Binder
			JAXBContext jc = JAXBContext.newInstance("fatturaXML");

			FatturaElettronicaType ftXML = new FatturaElettronicaType();
			//importante: va impostato in due posti differenti:
			if(datiPA != null)
				ftXML.setVersione(FormatoTrasmissioneType.FPA_12);
			else
				ftXML.setVersione(FormatoTrasmissioneType.FPR_12);
			ftXML.setFatturaElettronicaHeader(new FatturaElettronicaHeaderType());
			
			setDatiTrasmissione(ftXML,
					datiCedente[DatiFiscali.C_NAZIONE],
					(datiCedente[DatiFiscali.C_CODICEFISCALE] != null && datiCedente[DatiFiscali.C_CODICEFISCALE].length() > 10)
					  ? datiCedente[DatiFiscali.C_CODICEFISCALE] : datiCedente[DatiFiscali.C_PARTITAIVA],
					datiDestinatario[DatiFiscali.D_CODICEDESTINATARIO], null);

			ftXML.getFatturaElettronicaHeader().getDatiTrasmissione().setProgressivoInvio(progressivo);
			ftXML.getFatturaElettronicaHeader().getDatiTrasmissione().setContattiTrasmittente(new ContattiTrasmittenteType());
			//anche qui va messo il tipo
			if(datiPA != null)
				ftXML.getFatturaElettronicaHeader().getDatiTrasmissione().setFormatoTrasmissione(FormatoTrasmissioneType.FPA_12);
			else
				ftXML.getFatturaElettronicaHeader().getDatiTrasmissione().setFormatoTrasmissione(FormatoTrasmissioneType.FPR_12);
			
			setDatiCedente(ftXML, datiCedente);

			if(ordineAppartenenza != null && ordineAppartenenza[0].length() > 0)
				setDatiAlboProf(ftXML, ordineAppartenenza);

			setDatiDestinatario(ftXML, datiDestinatario);
			
			setDatiGenerali(ftXML, numFatt, dataFatt, datiCedente[DatiFiscali.P_DIVISA], importi, causale, datiPA);
			
			setDatiBeniServizi(ftXML, importi.get_tipoDocumento(), righe, importi.get_saldo());

			Marshaller msh = jc.createMarshaller();
			msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			msh.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2 http://www.fatturapa.gov.it/export/fatturazione/sdi/fatturapa/v1.2/Schema_del_file_xml_FatturaPA_versione_1.2.xsd");
			StringWriter sw = new StringWriter();
			msh.marshal(ftXML, sw);
			//non riesco a cambiare namespace, faccio a mano...
			String xs = sw.toString()
					.replaceFirst(" standalone=\"yes\"", "")
					;
			//System.out.println("FatturaXML.getXML()"+xs);
			File fl = new File(_path,filename);
			PrintWriter out = new PrintWriter(fl, "UTF-8");
			out.write(xs);
			out.close();
			return fl.getAbsolutePath();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String convertUnicode(String xs) {
		String[][] convert = new String[] [] { 
//		{"&",	"&amp;"},
//		{"<", 	"&lt;"},
//		{">", 	"&gt;"},
//		{"\"", 	"&quot;"},
//		{"\'", 	"&apos;"},
		{"à",	"a`"},//"&agrave;"},
		{"á",	"a'"},//"&aacute;"},
		{"è",	"e`"},//"&egrave;"},
		{"é",	"e'"},//"&eacute;"},
		{"ì",	"i`"},//"&igrave;"},
		{"í",	"i'"},//"&iacute;"},
		{"ò",	"o`"},//"&ograve;"},
		{"ó",	"o'"},//"&oacute;"},
		{"ù",	"u`"},//"&ugrave;"},
		{"ú",	"u'"},//"&uacute;"},
		};
		for (String[] strings : convert) {
			xs = xs.replaceAll(strings[0], strings[1]);
		}
		for (String[] strings : convert) {
			xs = xs.replaceAll(strings[0].toUpperCase(), strings[1].toUpperCase());
//			System.out.println("FatturaXMLwriter.convertUnicode()"+xs+" ---"+strings[0].toUpperCase());
		}
		return xs;
	}

	/**
	 * imposta albo Professionale di appartenenza
	 * @param ftXML
	 * @param alboProfessionale String[] con:
	 * <li>0: nome ordine
	 * <li>1: provincia
	 * <li>2: numero
	 * <li>3: data iscrizione in formato DataSQL 
	 */
	private void setDatiAlboProf(FatturaElettronicaType ftXML, String[] alboProfessionale) {
		if(alboProfessionale == null)
			return;
		DatiAnagraficiCedenteType anagr = ftXML.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici();
		anagr.setAlboProfessionale(alboProfessionale[0]);
		anagr.setProvinciaAlbo(alboProfessionale[1]);
		anagr.setNumeroIscrizioneAlbo(alboProfessionale[2]);
		try {
			DatatypeFactory dtf = DatatypeFactory.newInstance();
			anagr.setDataIscrizioneAlbo(dtf.newXMLGregorianCalendar(alboProfessionale[3]));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * imposta le righe e del documento e del riepilogo<br>
	 * si gestisce solo un iva unica, o 0 o altro
	 * @param ftXML
	 * @param righe
	 */
	
	private void setDatiBeniServizi(FatturaElettronicaType ftXML, TipoDocumentoType tipoDoc, List<RigaDocumento> righe, BigDecimal importoTotaleDocumento) {
		DatiBeniServiziType datiBeniServizi = new DatiBeniServiziType();
		List<DettaglioLineeType> listaRighe = new ArrayList<>();
		List<DatiRiepilogoType> riepilogo = new ArrayList<>();
		//System.out.println("FatturaXMLwriter.setDatiBeniServizi()");
		if(tipoDoc.equals(TipoDocumentoType.TD_03)) {
			//acconto
			addRigheAcconto_Riepilogo(righe, listaRighe, riepilogo, importoTotaleDocumento);
		}
		else {
			//saldo
			addRigheSaldoFt_Riepilogo(righe, listaRighe, riepilogo, ftXML.fatturaElettronicaBody.get(0).getDatiGenerali().getDatiGeneraliDocumento());
		}
		datiBeniServizi.dettaglioLinee = listaRighe;
		datiBeniServizi.datiRiepilogo = riepilogo;
		ftXML.getFatturaElettronicaBody().get(0).datiBeniServizi = datiBeniServizi;
	}

	/**
	 * imposta dati riepilogo
	 * @param righe
	 * @param listaRighe
	 * @param riepilogo
	 * @param totaleAcconto
	 */
	private void addRigheAcconto_Riepilogo(
									List<RigaDocumento> righe,
									List<DettaglioLineeType> listaRighe,
									List<DatiRiepilogoType> riepilogo,
									BigDecimal totaleAcconto)
	{
		double verificaTot = 0;

		for (RigaDocumento rigaDocumento : righe) {	
			DettaglioLineeType linea = new DettaglioLineeType();
			linea.setNumeroLinea(rigaDocumento.get_numeroLinea());
			linea.setDescrizione(rigaDocumento.get_descrizione());
			linea.setPrezzoUnitario(rigaDocumento.get_importoUnitario());
			if(rigaDocumento.get_importoUnitario().doubleValue() > 0d) {
				linea.setQuantita(rigaDocumento.get_quantita());
				linea.setPrezzoTotale(rigaDocumento.get_importoTotale());
				verificaTot += rigaDocumento.get_importoTotale().doubleValue();
			}
			
			linea.setAliquotaIVA(rigaDocumento.get_aliquotaIVA());
			linea.setNatura(rigaDocumento.get_naturaEsenzioneIVA());
			listaRighe.add(linea);
			
			DatiRiepilogoType rigaRiepilogo = new DatiRiepilogoType();
			RigaRiepilogo rRiep = new RigaRiepilogo(rigaDocumento.get_importoTotale().doubleValue());
			if(rigaDocumento.equals(righe.get(righe.size()-1))) {
				System.out.println("FatturaXMLwriter.addRigheAcconto_Riepilogo() last row");
				if( verificaTot == 0 ) {
					rigaRiepilogo.setImponibileImporto(totaleAcconto);
					linea.setPrezzoTotale(totaleAcconto);
					linea.setPrezzoUnitario(totaleAcconto);
				}
				else {
					rigaRiepilogo.setImponibileImporto(rRiep.get_imponibileImporto());
				}
			}
			rigaRiepilogo.setAliquotaIVA(rRiep.get_aliquotaIVA());
			rigaRiepilogo.setNatura(rRiep.get_naturaIVA());
			rigaRiepilogo.setRiferimentoNormativo(rRiep.get_RiferimentoNormativo());
			rigaRiepilogo.setEsigibilitaIVA(rRiep.get_esigibilitaIVA());
			rigaRiepilogo.setImposta(rRiep.get_imposta());
			
			riepilogo.add(rigaRiepilogo);
		}
	}

	private void addRigheSaldoFt_Riepilogo(List<RigaDocumento> righe, List<DettaglioLineeType> listaRighe, List<DatiRiepilogoType> riepilogo, DatiGeneraliDocumentoType dGenDoc) {
		DatiRiepilogoType drt = null;
		RigaRiepilogo rowRiepilogo = null;

		for (RigaDocumento rigaDocumento : righe) {	
			DettaglioLineeType linea = new DettaglioLineeType();
			if(isPresentLine(listaRighe, rigaDocumento.get_numeroLinea()))
				throw new IllegalArgumentException(COMMON.ROWEXISTS_IT);
			linea.setNumeroLinea(rigaDocumento.get_numeroLinea());
			linea.setDescrizione(rigaDocumento.get_descrizione());
			if(linea.getDescrizione().isEmpty())
				continue;
			linea.setPrezzoUnitario(rigaDocumento.get_importoUnitario());
			if(rigaDocumento.get_quantita().doubleValue() != 0d)
				linea.setQuantita(rigaDocumento.get_quantita());
			linea.setPrezzoTotale(rigaDocumento.get_importoTotale());
			
			linea.setAliquotaIVA(rigaDocumento.get_aliquotaIVA());
			if(rigaDocumento.get_naturaEsenzioneIVA() != null)
				linea.setNatura(rigaDocumento.get_naturaEsenzioneIVA());
			
			if(rigaDocumento.isRitenuta()) {
				DatiRitenutaType rat = null;
			    rat = new DatiRitenutaType();
				rat.setAliquotaRitenuta(rigaDocumento.get_aliquotaRA());
				rat.setImportoRitenuta(rigaDocumento.get_importoRA());
				rat.setTipoRitenuta(TipoRitenutaType.fromValue(rigaDocumento.get_tipoRA()));
				rat.setCausalePagamento(CausalePagamentoType.fromValue(rigaDocumento.get_causaleRA()));
				linea.setRitenuta(RitenutaType.SI);
			
				if(rat != null) {
//					System.out.println("is ritenuta ok");
					dGenDoc.setDatiRitenuta(rat);
				}
			}
			listaRighe.add(linea);
			
			DatiRiepilogoType existing = getRigaRiepilogo(riepilogo, rigaDocumento.get_aliquotaIVA(), rigaDocumento.get_naturaEsenzioneIVA());
			if(rigaDocumento.get_tipo().equals(TipoDocumentoType.TD_01)) {
				rowRiepilogo = new RigaRiepilogo(
						rigaDocumento.get_importoTotale().doubleValue(),
						rigaDocumento.get_aliquotaIVA(),
						rigaDocumento.get_importoIVA(),
						rigaDocumento.get_naturaEsenzioneIVA(),
						rigaDocumento.get_RiferimentoNormativoEsenzione());
			}
			else {
				rowRiepilogo = new RigaRiepilogo(rigaDocumento.get_importoTotale().doubleValue());
			}
			if(existing != null) {
				drt = existing;
				riepilogo.remove(drt);
			}
			else {
				DatiRiepilogoType rigaRiepilogo = new DatiRiepilogoType();
				rigaRiepilogo.setAliquotaIVA(rowRiepilogo.get_aliquotaIVA());
				rigaRiepilogo.setNatura(rowRiepilogo.get_naturaIVA());
				rigaRiepilogo.setEsigibilitaIVA(rowRiepilogo.get_esigibilitaIVA());
				rigaRiepilogo.setImponibileImporto(new BigDecimal(0));
				rigaRiepilogo.setImposta(new BigDecimal(0));
				drt = rigaRiepilogo;
			}
			drt.setImponibileImporto(drt.getImponibileImporto().add(rowRiepilogo.get_imponibileImporto()));
			drt.setImposta(drt.getImposta().add(rowRiepilogo.get_imposta()));
			riepilogo.add(drt);
		}
	}

	private DatiRiepilogoType getRigaRiepilogo(List<DatiRiepilogoType> riepilogo, BigDecimal aliquotaIVA, NaturaType naturaIVA) {
		for (DatiRiepilogoType dr : riepilogo) {
			if(dr.getAliquotaIVA().equals(aliquotaIVA)) { 
			   if(dr.getNatura() != null && naturaIVA != null && dr.getNatura().equals(naturaIVA) )
				return dr;
			}
		}
		return null;
	}

	private boolean isPresentLine(List<DettaglioLineeType> listaRighe, int numRow) {
		for (DettaglioLineeType row : listaRighe) {
			if(row.getNumeroLinea() == numRow)
				return true;
		}
		return false;
	}

	/**
	 * impostata i dati di trasmissione
	 * @param ftXML
	 * @param paese
	 * @param cf
	 * @param codiceDestinatario string codiceDestinatario o null per privati
	 * @param pecDestinatario string pecDestinatario o null
	 */
	private static void setDatiTrasmissione(FatturaElettronicaType ftXML, String paese, String cf, String codiceDestinatario, String pecDestinatario) {
		IdFiscaleType idt = new IdFiscaleType();
		idt.setIdPaese(paese);
		idt.setIdCodice(cf);
		if(ftXML.getFatturaElettronicaHeader().getDatiTrasmissione() == null)
			ftXML.getFatturaElettronicaHeader().setDatiTrasmissione(new DatiTrasmissioneType());
		ftXML.getFatturaElettronicaHeader().getDatiTrasmissione().setIdTrasmittente(idt);
		if(codiceDestinatario == null)
			ftXML.getFatturaElettronicaHeader().getDatiTrasmissione().setCodiceDestinatario("0000000");
		else
			ftXML.getFatturaElettronicaHeader().getDatiTrasmissione().setCodiceDestinatario(codiceDestinatario);
		try {
			//pec solo se valida: (indirizzo email e lungo almeno 7 caratteri
			InternetAddress emailAddr = new InternetAddress(pecDestinatario);
			emailAddr.validate();
			if(pecDestinatario.length() > 6) {
				//aggiungo pec
				ftXML.getFatturaElettronicaHeader().getDatiTrasmissione().setPECDestinatario(pecDestinatario);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 
	 * @param ftXML {@link FatturaElettronicaType} elemento base da compilare
	 * @param datiCedente
	 * <li>0: nazione es."IT"
	 * <li>1: partita iva
	 * <li>2: codice fiscale
	 * <li>3: cognome o denominazione
	 * <li>4: nome o null
	 * <li>5: regime fiscale
	 * <li>6: indirizzo sede
	 * <li>7: cap
	 * <li>8: comune
	 * <li>9: prov
	 * <li>10:nazione sede
	 */
	private static void setDatiCedente(FatturaElettronicaType ftXML, String[] datiCedente) {
		CedentePrestatoreType cedente = new CedentePrestatoreType();
		DatiAnagraficiCedenteType dati_anagr_Cedente = new DatiAnagraficiCedenteType();
		
		IdFiscaleType idFiscaleCedente = new IdFiscaleType();
		idFiscaleCedente.setIdPaese(datiCedente[DatiFiscali.C_NAZIONE]);
		idFiscaleCedente.setIdCodice(datiCedente[DatiFiscali.C_PARTITAIVA]);

		if(datiCedente[DatiFiscali.C_CODICEFISCALE] != null && datiCedente[DatiFiscali.C_CODICEFISCALE].length() > 10)
			dati_anagr_Cedente.setCodiceFiscale(datiCedente[DatiFiscali.C_CODICEFISCALE]);
		
		AnagraficaType anagr = new AnagraficaType();
		if(datiCedente[DatiFiscali.C_NOME] != null && datiCedente[DatiFiscali.C_NOME].isEmpty() == false) {
			anagr.setCognome(convertUnicode(datiCedente[DatiFiscali.C_COGNOME]));
			anagr.setNome(convertUnicode(datiCedente[DatiFiscali.C_NOME]));
		} else {
			anagr.setDenominazione(convertUnicode(datiCedente[DatiFiscali.C_DENOMINAZIONE]));
		}
		dati_anagr_Cedente.setAnagrafica(anagr);
		dati_anagr_Cedente.setIdFiscaleIVA(idFiscaleCedente);
		if(datiCedente[DatiFiscali.C_CODICEFISCALE].isEmpty() == false)
			dati_anagr_Cedente.codiceFiscale = datiCedente[DatiFiscali.C_CODICEFISCALE];
		dati_anagr_Cedente.regimeFiscale = RegimeFiscaleType.fromValue(datiCedente[DatiFiscali.C_REGIMEFISCALE]);
		cedente.setDatiAnagrafici(dati_anagr_Cedente);
		
		IndirizzoType sedeCedente = new IndirizzoType();
		sedeCedente.setIndirizzo(convertUnicode(datiCedente[DatiFiscali.SC_INDIRIZZO]));
		sedeCedente.setCAP(datiCedente[DatiFiscali.SC_CAP]);
		sedeCedente.setComune(convertUnicode(datiCedente[DatiFiscali.SC_COMUNE]));
		sedeCedente.setProvincia(datiCedente[DatiFiscali.SC_PROV]);
		sedeCedente.setNazione(datiCedente[DatiFiscali.SC_NAZIONE]);
		cedente.setSede(sedeCedente);
				
		
		ContattiType contattiCedente = new ContattiType();
		contattiCedente.setTelefono(datiCedente[DatiFiscali.SC_TELEFONO]);
		contattiCedente.setEmail(datiCedente[DatiFiscali.SC_EMAIL]);
		cedente.setContatti(contattiCedente);

		ftXML.getFatturaElettronicaHeader().setCedentePrestatore(cedente);
	}

	/**
	 * imposta il destinatario
	 * @param ftXML {@link FatturaElettronicaType} elemento base da compilare
	 * @param datiDestinatario
	 * <li>0: nazione es."IT"
	 * <li>1: partita iva
	 * <li>2: codice fiscale
	 * <li>3: cognome o denominazione
	 * <li>4: nome o null
	 * <li>5: codiceDestinatario  6 (x PA) o 7 (per PR) car o null per persone (=0000000)
	 * <li>6: indirizzo sede
	 * <li>7: cap
	 * <li>8: comune
	 * <li>9: prov
	 * <li>10:nazione sede
	 */
	private static void setDatiDestinatario(FatturaElettronicaType ftXML, String[] datiDestinatario) {
		CessionarioCommittenteType destinatario = new CessionarioCommittenteType();
		DatiAnagraficiCessionarioType dati_anagr_Destinatario = new DatiAnagraficiCessionarioType();
		
		if(datiDestinatario[DatiFiscali.C_PARTITAIVA] != null && datiDestinatario[DatiFiscali.C_PARTITAIVA].length() > 0) {
			IdFiscaleType idFiscaleDestinatario = new IdFiscaleType();
			idFiscaleDestinatario.setIdPaese(datiDestinatario[DatiFiscali.C_NAZIONE]);
			idFiscaleDestinatario.setIdCodice(datiDestinatario[DatiFiscali.C_PARTITAIVA]);
			dati_anagr_Destinatario.setIdFiscaleIVA(idFiscaleDestinatario);
		}

		if(datiDestinatario[DatiFiscali.C_CODICEFISCALE] != null && datiDestinatario[DatiFiscali.C_CODICEFISCALE].length() > 10 )
			dati_anagr_Destinatario.setCodiceFiscale(datiDestinatario[DatiFiscali.C_CODICEFISCALE]);
		
		AnagraficaType anagr = new AnagraficaType();
		if(datiDestinatario[DatiFiscali.C_NOME] != null && datiDestinatario[DatiFiscali.C_NOME].isEmpty() == false) {
			anagr.setCognome(convertUnicode(datiDestinatario[DatiFiscali.C_COGNOME]));
			anagr.setNome(convertUnicode(datiDestinatario[DatiFiscali.C_NOME]));
		} else {
			anagr.setDenominazione(convertUnicode(datiDestinatario[DatiFiscali.C_DENOMINAZIONE]));
		}
		dati_anagr_Destinatario.setAnagrafica(anagr);
		if(datiDestinatario[DatiFiscali.C_CODICEFISCALE].isEmpty() == false)
			dati_anagr_Destinatario.codiceFiscale = datiDestinatario[DatiFiscali.C_CODICEFISCALE];
		
		IndirizzoType sedeDestinatario = new IndirizzoType();
		sedeDestinatario.setIndirizzo(convertUnicode(datiDestinatario[DatiFiscali.SC_INDIRIZZO]));
		sedeDestinatario.setCAP(datiDestinatario[DatiFiscali.SC_CAP]);
		sedeDestinatario.setComune(convertUnicode(datiDestinatario[DatiFiscali.SC_COMUNE]));
		sedeDestinatario.setProvincia(datiDestinatario[DatiFiscali.SC_PROV]);
		sedeDestinatario.setNazione(datiDestinatario[DatiFiscali.SC_NAZIONE]);
		destinatario.setSede(sedeDestinatario);
		
//		dati_anagr_Destinatario.setIdFiscaleIVA(idFiscaleDestinatario);
		destinatario.setDatiAnagrafici(dati_anagr_Destinatario);
		
		ftXML.getFatturaElettronicaHeader().setCessionarioCommittente(destinatario);
//		System.out.println("FatturaXMLwriter.setDatiDestinatario()"+destinatario);
	}

	/**
	 * imposta i DatiGenerali
	 * @param ftXML
	 * @param numFatt
	 * @param dataFatt
	 * @param divisaValuta
	 * @param importi
	 * @param causale
	 * @param datiPA
	 */
	private void setDatiGenerali(
			FatturaElettronicaType ftXML, String numFatt, Calendar dataFatt,
			String divisaValuta, ImportiDocumento importi, String causale,
			String[] datiPA) {
		DatiGeneraliDocumentoType dGenDoc = new DatiGeneraliDocumentoType();
		//set tipo doc
		dGenDoc.setTipoDocumento(importi.get_tipoDocumento());
		//set divisa
		dGenDoc.setDivisa(divisaValuta);
		//set data documento
		try {			
			DatatypeFactory dtf = DatatypeFactory.newInstance();
			dGenDoc.setData(dtf.newXMLGregorianCalendarDate(dataFatt.get(Calendar.YEAR), dataFatt.get(Calendar.MONTH)+1, dataFatt.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		//set numero documento
		dGenDoc.setNumero(numFatt);
		//set importo
		dGenDoc.setImportoTotaleDocumento(importi.get_saldo());
		//set bollo se != 0
		if(importi.get_bollo().doubleValue() != 0d) {
			DatiBolloType bollo = new DatiBolloType();
			bollo.setImportoBollo(importi.get_bollo());
			bollo.setBolloVirtuale(BolloVirtualeType.SI);
			dGenDoc.setDatiBollo(bollo);
		}
		if(importi.get_arrotondamento() != null && importi.get_arrotondamento().doubleValue() != 0d) {
			dGenDoc.setArrotondamento(importi.get_arrotondamento());
		}
		if(importi.get_sconto().doubleValue() != 0d) {
			ScontoMaggiorazioneType sconto = new ScontoMaggiorazioneType();
			sconto.setTipo(importi.get_sconto().doubleValue() > 0 ? TipoScontoMaggiorazioneType.MG : TipoScontoMaggiorazioneType.SC);
			sconto.setImporto(importi.get_sconto());
			dGenDoc.getScontoMaggiorazione().add(sconto);
		}
		//causale
		if(causale != null && causale.isEmpty() == false) {
			dGenDoc.causale = new ArrayList<String>();
			dGenDoc.causale.add(causale);
		}
		
		DatiGeneraliType dGen = new DatiGeneraliType();
		dGen.setDatiGeneraliDocumento(dGenDoc);

	   //PA=Id.Ordine di Acquisto'\u0015'CUP'\u0015'CIG'\u0015'Id.Contratto'\u0015'DataContr'\u0015'Id.Ricezione
	   //1=CUP, 2=CIG
	   //0=OrdineAcquisto
	   if(datiPA != null) {
		   try {
			   String CUP = datiPA[1]; 
			   String CIG = datiPA[2];
			   if(CUP.isEmpty()) CUP = null;
			   if(CIG.isEmpty()) CIG = null;
			   //ordineacquisto
			   if(datiPA[0].isEmpty() == false) {
				   DatiDocumentiCorrelatiType ordineAcquisto = new DatiDocumentiCorrelatiType();
				   ordineAcquisto.setIdDocumento(datiPA[0]);
				   if(CUP != null) ordineAcquisto.setCodiceCUP(CUP);
				   if(CIG != null) ordineAcquisto.setCodiceCIG(CIG);
				   dGen.getDatiOrdineAcquisto().add(ordineAcquisto);
			   }
			   //contratto
			   if(datiPA[3].isEmpty() == false) {
				   DatiDocumentiCorrelatiType contratto = new DatiDocumentiCorrelatiType();
				   contratto.setIdDocumento(datiPA[3]);
				   if(datiPA[4] != null && datiPA[4].isEmpty() == false) 
					   try {
						   DatatypeFactory dtf = DatatypeFactory.newInstance();
						   contratto.setData(dtf.newXMLGregorianCalendar(datiPA[4]));
					   } catch (DatatypeConfigurationException e) {
						   e.printStackTrace();
					   }
				   if(CUP != null) contratto.setCodiceCUP(CUP);
				   if(CIG != null) contratto.setCodiceCIG(CIG);
				   dGen.getDatiContratto().add(contratto);
			   }
			   //ricezione
			   if(datiPA[5].isEmpty() == false) {
				   DatiDocumentiCorrelatiType datiRicezione = new DatiDocumentiCorrelatiType();
				   datiRicezione.setIdDocumento(datiPA[5]);
				   if(CUP != null) datiRicezione.setCodiceCUP(CUP);
				   if(CIG != null) datiRicezione.setCodiceCIG(CIG);
				   dGen.getDatiRicezione().add(datiRicezione);
			   }
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }
	   //fine
	   FatturaElettronicaBodyType body = new FatturaElettronicaBodyType();
	   body.setDatiGenerali(dGen);
	   ftXML.addFatturaElettronicaBody(body);
	}

	/*
	public static void main(String[] args) {
		  fatturaXML.FatturaXMLwriter ftEl = new fatturaXML.FatturaXMLwriter(System.getProperty("user.dir"));
		  ArrayList<RigaDocumento> righe = new ArrayList<>();
		  DatiFiscali df = new DatiFiscali(
			  "IT", "07869830013", "NDLFRZ66H25L219A",
			  //cognome nome o denominazione + null
			  "Andolfi", "Fabrizio",
			  //regime fiscale
			  "RF19",
			  //sede
			  "VIA SANTA MARIA 19", "10040", "GIVOLETTO", "TO", "IT",
			  "011-223344", "test@studio123.it",
			  //divisa (3 caratteri)
			  "EUR", true
	      );
		  DatiFiscali dd = new DatiFiscali(
				"", null, "NDLFRZ66H25L219A",
				//cognome nome o denominazione + null
				"Andolfi", "Fabrizio",
				//codicedesti
				"0000000",
				//sede
				"via santa maria 55", "10040", "Givoletto", "TO", "IT",
				"", "", "", false
		  );
		  //ACCONTO
//		  double importo = 100;
//		  RigaDocumento rigaAcconto = new RigaDocumento(TipoDocumentoType.TD_03, "Acconto per terapia odontoiatrica", importo);
//		  righe.add(rigaAcconto);
//		  System.out.println(
//		  ftEl.getXML(
//				  //cedente
//			new String[]{
//				  "IT", "07869830013", "NDLFRZ66H25L219A",
//				  //cognome nome o denominazione + null
//				  "Andolfi", "Fabrizio",
//				  //regime fiscale
//				  "RF19",
//				  //sede
//				  "VIA SANTA MARIA 19", "10040", "GIVOLETTO", "TO", "IT",
//				  "011-223344", "test@studio123.it",
//				  //divisa (3 caratteri)
//				  "EUR"
//			},
//			//n fatt
//			"123/G",
//			//data fatt
//			Calendar.getInstance(),
//			//destinatario
//			new String[]{
//					null, null, "NDLFRZ66H25L219A",
//					//cognome nome o denominazione + null
//					"andolfi", "Fabrizio",
//					//codicedesti
//					null,
//					//sede
//					"via santa maria 55", "10040", "Givoletto", "TO", "IT",
//			},
//		    //importi parcella
//		  	new ImportiDocumento(TipoDocumentoType.TD_03, importo, 2),
//		    righe)
//		  );
		  //SALDO
		  double importo = 825-400;
		  int row= 0;
		  RigaDocumento rigaSaldo1 = new RigaDocumento(
				  TipoDocumentoType.TD_06.toString(),
				  ++row,
				  "Terapia canalare di 2.2 e 2.3",
				  2,
				  600);
		  RigaDocumento rigaSaldo2 = new RigaDocumento(
				  TipoDocumentoType.TD_06.toString(),
				  ++row,
				  "Otturazione di 2.2 e 2.3",
				  2,
				  220);

		  RigaDocumento rigaDetrazAcc = new RigaDocumento(
				  TipoDocumentoType.TD_06.toString(),
				  ++row,
				  "Detrazione n.1 parcella di acconto",
				  1,
				  -400);
		  RigaDocumento rigaDetrazBollo = new RigaDocumento(
				  TipoDocumentoType.TD_06.toString(),
				  ++row,
				  "Sconto imposta di bollo",
				  1,
				  -2);

		  righe.add(rigaSaldo1);
		  righe.add(rigaSaldo2);
		  righe.add(rigaDetrazAcc);
		  righe.add(rigaDetrazBollo);
//		  double importo = 100.01;
//		  int row = 0;
//		  RigaDocumento rigaSaldo1 = new RigaDocumento(
//				  TipoDocumentoType.TD_06.toString(),
//				  ++row,
//				  "descrizione",
//				  1,
//				  100.01);
//		  righe.add(rigaSaldo1);
		  //posso inserire riga senza importo se quantita' e prezzo sono a zero
//		  RigaDocumento rigaSaldo2 = new RigaDocumento(
//				  TipoDocumentoType.TD_06,
//				  ++row,
//				  "descrizione2",
//				  0,
//				  0);
//		  righe.add(rigaSaldo2);

		  System.out.println(
				  ftEl.getXML(
						  //cedente
					df,
					null,
					//n fatt
					"23/ft",
					//data fatt
					Calendar.getInstance(),
				    //destinatario
					dd,
				    //importi parcella
				  	new ImportiDocumento(TipoDocumentoType.TD_06, importo, 2d, -5d),
				    righe, "Parcella odontoiatrica esente iva art....\nriga2\nriga3")
				  );

//		  double importo = 100.01;
//		  int row = 0;
//		  RigaDocumento rigaSaldo1 = new RigaDocumento(
//				  TipoDocumentoType.TD_01.toString(),
//				  ++row,
//				  "descrizione",
//				  1,
//				  100.00,
//				  new double[] {22,22},
//				  null);
//		  righe.add(rigaSaldo1);
//		  //posso inserire riga senza importo se quantita' e prezzo sono a zero
//		  RigaDocumento rigaSaldo2 = new RigaDocumento(
//				  TipoDocumentoType.TD_06.toString(),
//				  ++row,
//				  "descrizione2",
//				  0,
//				  0);
//		  righe.add(rigaSaldo2);
//
//		  DatiFiscali df = new DatiFiscali(
//						  "IT", "07869830013", "NDLFRZ66H25L219A",
//						  //cognome nome o denominazione + null
//						  "Andolfi", "Fabrizio",
//						  //regime fiscale
//						  "RF19",
//						  //sede
//						  "VIA SANTA MARIA 19", "10040", "GIVOLETTO", "TO", "IT",
//						  "011-223344", "test@studio123.it",
//						  //divisa (3 caratteri)
//						  "EUR", true
//					);
//		  DatiFiscali dd = new DatiFiscali(
//							"", "07869830013", "NDLFRZ66H25L219A",
//							//cognome nome o denominazione + null
//							"Andolfi", "Fabrizio",
//							//codicedesti
//							"1234567",
//							//sede
//							"via santa maria 55", "10040", "Givoletto", "TO", "IT",
//							"", "", "", false
//					);
//		  System.out.println(
//				  ftEl.getXML(
//					df,	  //cedente
//					"23/ft",//n fatt
//					//data fatt
//					Calendar.getInstance(),
//					dd, //destinatario
//				    //importi parcella
//				  	new ImportiDocumento(TipoDocumentoType.TD_01.toString(), importo, 0d, 0d, 0),
//				    righe,
//				    "fattura con iva art....\nriga2\nriga3")
//				  );
	}
*/

}
