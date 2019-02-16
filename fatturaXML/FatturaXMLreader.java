package fatturaXML;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.UnmarshalException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import fatturaXML.*;

public class FatturaXMLreader
{
	FatturaElettronicaType fattura;
	private boolean firmaP7M = false;
	private String nonConforme = null;

	public static void main(String[] args) throws UnmarshalException, NullPointerException {
		if(args.length == 0) {
			System.out.println("usage: FatturaXML nomeFileFattura.xml [noatt]\nnoatt per vedere solo documento originale e non file allegato");
			System.exit(0);
		}
		boolean no_attachment = false;
		if(args.length == 2 && args[1].equals("noatt"))
			no_attachment = true;
		FatturaXMLreader f = new FatturaXMLreader(args[0]);
		int qt = f.getDocumentSize();
		for (int id = 0; id < qt; id++) {			
			File fft;
			try {
				File fa = f.getAttachment(id);
				if(fa == null || no_attachment) {
					fft = File.createTempFile("fattura"+id,".pdf");
					Fattura2PDF pdf = new Fattura2PDF(f);
					if(pdf.getPDFdoc(id, fft.getAbsolutePath())) {
						Desktop.getDesktop().open(fft);
					}
				}
				else {
					Desktop.getDesktop().open(fa);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * read a XML file and create dataType obj
	 * @param file
	 * @throws UnmarshalException 
	 */
	public FatturaXMLreader(String file) throws UnmarshalException, NullPointerException {
		InputStream fis = null;
		try {
			// create JAXBContext which will be used to create a Binder
			JAXBContext jc = JAXBContext.newInstance("fatturaXML");

			fis = new FileInputStream(file);
			if(file != null && file.toLowerCase().endsWith("p7m")) {
				//win openssl.exe smime -decrypt -verify -inform DER -in "test.xml.p7m" -noverify -out "test.xml"
				//other openssl pkcs12 -in <test.xml.p7m> -out <key_file.pem> -nodes
				try {
					String content = new String(Files.readAllBytes(Paths.get(file)));
					int start = content.indexOf("<?xml ");
					String test1 = content.substring(start);
					String end = "FatturaElettronica>";
					int end1 = test1.lastIndexOf(end);
					String xml1 = test1.substring(0, end1+end.length());
					xml1 = xml1.replaceAll("[^\\x10-\\x7F]", "");
					// create new file input stream
					fis = new ByteArrayInputStream(xml1.getBytes(StandardCharsets.UTF_8));
					firmaP7M = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// unmarshaller obj to convert xml data to java content tree
			Unmarshaller u = jc.createUnmarshaller();
			
			// unmarshaller xml data to java content tree
			@SuppressWarnings("unchecked")
			JAXBElement<FatturaElettronicaType> node = null;
			try {
				node = (JAXBElement<FatturaElettronicaType>) u.unmarshal(fis);
			} catch (Exception e) {
				//correzione x xml fatti a cazzo, aggiungo uno spazio dove c'e' errore
				if(e.toString().contains("SAXParseException")) {
					try {
						String content = new String(Files.readAllBytes(Paths.get(file)));
						String[] t1 = e.toString().split("lineNumber: ");
						int line = Integer.parseInt(t1[1].split(";")[0]);
						String[] t2 = e.toString().split("columnNumber: ");
						int col = Integer.parseInt(t2[1].split(";")[0])-1;
						if(line == 1) {
							content = content.substring(0, col) +" " + content.substring(col);
							// create new file input stream
							fis = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
							node = (JAXBElement<FatturaElettronicaType>) u.unmarshal(fis);
							nonConforme = "Linea: "+line+" Colonna: "+(col+1);
						}	
//						System.out.println("FatturaXMLreader.FatturaXMLreader()"+content);						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else if(e.toString().contains("FileMetadati")) {
					JOptionPane.showMessageDialog(null, "<html><big>Questo è un file MetaDati,<BR>non è una fattura elettronica!", "Errore", JOptionPane.ERROR_MESSAGE);
				}
			}
			fattura = node.getValue();

//			FatturaElettronicaHeaderType header = fattura.getFatturaElettronicaHeader();
//			System.out.printf("header:datiTrasmissione: idTrasmittente.id paese %s\t codiceFiscale %s\n",
//					header.getDatiTrasmissione().getIdTrasmittente().getIdPaese(),
//					header.getDatiTrasmissione().getIdTrasmittente().getIdCodice()
//					);
//			System.out.printf("\nheader:datiTrasmissione: CodiceDest %s \t PEC %s nprogressivo %s\n",
//					header.getDatiTrasmissione().getCodiceDestinatario(),
//					header.getDatiTrasmissione().getPECDestinatario(),
//					header.getDatiTrasmissione().getProgressivoInvio()
//					);
//			if(header.getDatiTrasmissione().getContattiTrasmittente() != null)
//				System.out.printf("\t\tcontattiTrasmittente tel %s\t email %s\n",
//					header.getDatiTrasmissione().getContattiTrasmittente().getTelefono(),
//					header.getDatiTrasmissione().getContattiTrasmittente().getEmail());
//			System.out.println("\t\tformatoTras \t " + header.getDatiTrasmissione().getFormatoTrasmissione());
//
//			System.out.printf("\nheader:\tCEDENTE.anagrafica idiva %s %s albo %s num %s data %s\n",
//					header.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese(),
//					header.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice(),
//					header.getCedentePrestatore().getDatiAnagrafici().getAlboProfessionale(),
//					header.getCedentePrestatore().getDatiAnagrafici().getNumeroIscrizioneAlbo(),
//					header.getCedentePrestatore().getDatiAnagrafici().getDataIscrizioneAlbo()
//					);
//			System.out.printf("\t\t codeori %s cg %s no %s\n",
//					header.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCodEORI(),
//					header.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCognome(),
//					header.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getNome());
//			String rf = header.getCedentePrestatore().getDatiAnagrafici().getRegimeFiscale().name();
//			System.out.printf("\t\t denom %s ->regimeFisc %s [%s]\n",
//					header.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione(), rf,
//					RegimeFiscaleType.getDescription(rf)
//					);
//			System.out.printf("\t\t.Sede \t%s, num %s\n",
//					header.getCedentePrestatore().getSede().getIndirizzo(),
//					header.getCedentePrestatore().getSede().getNumeroCivico());
//
//			System.out.printf("\t\t\t%s %s %s %s\n", 
//					header.getCedentePrestatore().getSede().getCAP(),
//					header.getCedentePrestatore().getSede().getComune(),
//					header.getCedentePrestatore().getSede().getProvincia(),
//					header.getCedentePrestatore().getSede().getNazione());
//			
//			if(header.getCedentePrestatore().getContatti() != null) {
//				System.out.printf("\t\t\t%s %s %s \n", 
//					header.getCedentePrestatore().getContatti().getTelefono(),
//					header.getCedentePrestatore().getContatti().getFax(),
//					header.getCedentePrestatore().getContatti().getEmail());
//			}
			
//			System.out.printf("\nheader:\tCESSIONARIO.ID %s\tCF %s \tCodEori %s \n",
//					header.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA(),
//					header.getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale(),
//					header.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getCodEORI()
//					);
//					
//			System.out.println("\t\t.AnagDenom \t" + header.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getDenominazione());
//			System.out.printf("\t\t.Anag.\ttit %s, CG %s, no %s\n",
//					header.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getTitolo(),
//					header.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getCognome(),
//					header.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getNome()
//					);
//			System.out.printf("\t\t.Sede indir \t%s, num %s\n",
//					header.getCessionarioCommittente().getSede().getIndirizzo(),
//					header.getCessionarioCommittente().getSede().getNumeroCivico());
//			System.out.printf("\t\t\t%s %s %s %s\n", 
//					header.getCessionarioCommittente().getSede().getCAP(),
//					header.getCessionarioCommittente().getSede().getComune(),
//					header.getCessionarioCommittente().getSede().getProvincia(),
//					header.getCessionarioCommittente().getSede().getNazione());
//			System.out.println("\t\t.Stabile \t" + header.getCessionarioCommittente().getStabileOrganizzazione());
//			if(header.getCessionarioCommittente().getRappresentanteFiscale() != null) {
//				System.out.println("header:rapFisc \t" + header.getCessionarioCommittente().getRappresentanteFiscale().getCognome());
//			}
//			System.out.println("1nheader:SoggEmittente \t" + header.getSoggettoEmittente());
//			
//			System.out.println("header:rappreFisc  \t" + header.getRappresentanteFiscale());
//			System.out.println("header:sogEmittente\t " + header.getSoggettoEmittente());
//
//			Iterator<FatturaElettronicaBodyType>it = fattura.getFatturaElettronicaBody().iterator();
//			while(it.hasNext()) {
//				FatturaElettronicaBodyType body = it.next();
//				DatiGeneraliDocumentoType datiGenDoc = body.getDatiGenerali().getDatiGeneraliDocumento();
//				System.out.printf("body:datiGenDoc tipo %s [%s], divisa %s\n",
//						datiGenDoc.getTipoDocumento().value(),
//						TipoDocumentoType.getDescription(datiGenDoc.getTipoDocumento().value()),
//						datiGenDoc.getDivisa());
//				System.out.printf("\t\tdata %s, numero %s\n",
//						getITAData(datiGenDoc.getData()),
//						datiGenDoc.getNumero());
//				if(datiGenDoc.getDatiRitenuta() != null)
//				System.out.printf("\t\tritenuta %s\n",
//						datiGenDoc.getDatiRitenuta()
//						//tipo,//importo//aliquota//causale
//						);
//				System.out.printf("\t\tdatiBollo %s\t art73 %s\n",
//						datiGenDoc.getDatiBollo(), datiGenDoc.getArt73()
//						);
//				System.out.printf("\t\tscontoMaggioraz %s\n",
//						datiGenDoc.getScontoMaggiorazione()
//						);
//				System.out.printf("\t\tdata cassaPrev %s \n",
//						datiGenDoc.getDatiCassaPrevidenziale()
//						);
//				
//				System.out.printf("\t\tcausale %s\n\n",
//						datiGenDoc.getCausale()
//						);
//				List<DatiPagamentoType> pagamento = body.getDatiPagamento();
//				for (DatiPagamentoType datipag : pagamento) {					
//					System.out.printf("body:dati pagamenti %s\n",
//							datipag.getDettaglioPagamento().get(0).getDataScadenzaPagamento()
//							);
//				}
//				DatiBeniServiziType beni = body.getDatiBeniServizi();
//				for (DettaglioLineeType linea : beni.getDettaglioLinee()) {
//					System.out.printf("%s linea beni: %s euro:%s\t qt: euro: %s = %s iva %s rit:%s\n",
//							linea.getNumeroLinea(),
//							linea.getDescrizione(),
//							linea.getPrezzoUnitario(), linea.getQuantita(), linea.getPrezzoTotale(),
//							linea.getAliquotaIVA(), linea.getRitenuta());
//				}
//				List<DatiRiepilogoType> riepilogo = body.getDatiBeniServizi().getDatiRiepilogo();
//				for ( DatiRiepilogoType riep : riepilogo) {
//					System.out.println("FatturaXML.FatturaXML()"+riep);
//					System.out.printf("\nriepologo iva: aliq %s %s %s   imponibile:%s imposta:%s  spese acc:%s rif.amm.%s\n",
//							riep.getAliquotaIVA(),
//							riep.getEsigibilitaIVA(), riep.getEsigibilitaIVA() != null ? EsigibilitaIVAType.getDescription(riep.getEsigibilitaIVA().name()) : "--",
//							riep.getImponibileImporto(), riep.getImposta(),
//							riep.getSpeseAccessorie(), riep.getRiferimentoNormativo()
//							);
//				}
//			}
			return;
		}
//		catch (NullPointerException ex) {
//			ex.printStackTrace();
//		}
		catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
//				System.out.println("FatturaXMLreader.FatturaXMLreader() close stream");
				fis.close();
			} catch (IOException e) { }
		}
		fattura = null;
	}

	public FatturaElettronicaType getFattura() {
		return fattura;
	}
	
	/**
	 * retrive from fatturaXML destinatario vatCode and FiscalCode
	 * @return String[] {partitaiva,codicefiscale}
	 */
	public String[] getIDdestinatario() {
		if(fattura == null) return new String[] {"ERRORE",""};
		String piva = "", cf = "";
		try {
			piva = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
		}
		catch (Exception e) {
			if(fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale().length() == 11)
				piva = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale();
		}
		try {
			if(fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale().length() == 16)
				cf = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale();
		} catch (Exception e) {
		}
		return new String[] {
				piva,
				cf
		};
	}

	/**
	 * retrive from fatturaXML destinatario name
	 * @return String name
	 */
	public String getNomeDestinatario() {
		if(fattura == null) return "ERRORE DECODIFICA";
		if(fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getDenominazione() != null)
			return fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getDenominazione();
		String res = String.format("%s %s %s",
				fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getTitolo(),
				fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getCognome(),
				fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getNome()).replaceAll("null ", "").trim();
		return res;
	}
	
	/**
	 * retrive from fatturaXML sender name
	 * @return String name
	 */
	public String getNomeFornitore() {
		if(fattura == null) return "ERRORE DECODIFICA";
		if(fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione() != null)
			return fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione();
		String res = String.format("%s %s %s",
				fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getTitolo(),
				fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCognome(),
				fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getNome()).replaceAll("null ", "").trim();
		return res;
	}

	/**
	 * retrive from fatturaXML sender vatCode and FiscalCode
	 * @return String with partitaIVA or CodiceFiscale
	 */
	public String getIDfornitore() {
		String piva = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
		String cf = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale();
		if(cf.equals(piva)) {
			if(cf.length() == 16)
				piva = "";
			else
				cf = "";
		}
		if(piva.isEmpty())
			return cf;
		return piva;
	}
	
	/**
	 * retrive sender data
	 * @return Strig[] {
	 * cognome, nome,
	 * indirizzo, cap, cit, prov, naz,
	 * tel, fax, email,
	 * piva, cf }
	 */
	public String[] getDatiFornitore(boolean completo) {
		if(fattura == null) return null;
		String cognome = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCognome();
		String nome = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getNome();
		String denominaz = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione();
		if(denominaz == null) {
			denominaz = "";
		}
		if(completo) {
			if(cognome == null) cognome = "";
			if(nome == null) nome = "";
		} else {
			if(cognome == null) {
				if(denominaz.indexOf(" ") > -1) {
					cognome = denominaz.substring(0, denominaz.lastIndexOf(" "));
					nome = denominaz.substring(denominaz.lastIndexOf(" ")+1);
				} else {
					cognome = denominaz;
					nome = "";
				}
			}
		}

		String ind = "";
		String cap = "";
		String cit = "";
		String prov = "";
		String naz = "";
		String num = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getNumeroCivico();
		try {
			ind = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getIndirizzo();
			if(num != null)
				ind = ind + ", " + num;
			if(ind.length() == 2) ind = "";
			cap = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getCAP();
			cit = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getComune();
			prov = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getProvincia();
			naz = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getSede().getNazione();
		}
		catch (Exception e) {
//			e.printStackTrace();
		}
		if(num == null) num = "";
		if(prov == null) prov = "";
		String tel = "";
		String fax = "";
		String email = "";
		try {
			tel = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getContatti().getTelefono();
			fax = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getContatti().getFax();
			email = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getContatti().getEmail();
		}
		catch (Exception e) {
//			e.printStackTrace();
		}
		String piva = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
		String cf = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale();
		if(cf != null && cf.equals(piva)) {
			if(cf.length() == 16)
				piva = "";
			else
				cf = "";
		}
		if(fax == null) fax = "";
		if(completo) 
			return new String[] {
					cognome, nome, denominaz,
					ind, num, cap, cit, prov, naz,
					tel, fax, email,
					piva, cf
				};
		return new String[] {
			cognome, nome,
			ind, cap, cit, prov, naz,
			tel, fax, email,
			piva, cf
		};
	}
	
	/**
	 * retrive dest data
	 * @return Strig[] {
	 * cognome, nome,
	 * indirizzo, cap, cit, prov, naz,
	 * piva, cf }
	 */
	public String[] getDatiDestinatario(boolean completo) {
		if(fattura == null) return null;
		String cognome = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getCognome();
		String nome = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getNome();
		String denominaz = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getDenominazione();
		String idFiscale = null;
		try {
			idFiscale = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
		} catch (Exception e1) {
			// senza partita iva!
		}
		if(denominaz == null) {
			denominaz = "";
		}
		if(completo) {
			if(cognome == null) cognome = "";
			if(nome == null) nome = "";
		} else {
			if(cognome == null) {
				if(denominaz.indexOf(" ") > -1) {
					cognome = denominaz.substring(0, denominaz.lastIndexOf(" "));
					nome = denominaz.substring(denominaz.lastIndexOf(" ")+1);
				} else {
					cognome = denominaz;
					nome = "";
				}
			}
		}

		String ind = "";
		String cap = "";
		String cit = "";
		String prov = "";
		String naz = "";
		String num = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getNumeroCivico();
		try {
			ind = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getIndirizzo();
			if(num != null)
				ind = ind + ", " + num;
			if(ind.length() == 2) ind = "";
			cap = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getCAP();
			cit = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getComune();
			prov = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getProvincia();
			naz = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getNazione();
		}
		catch (Exception e) {
//			e.printStackTrace();
		}
		if(num == null) num = "";
		if(prov == null) prov = "";
		if(cap == null) cap = "";
		String piva;
		try {
			piva = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
		}
		catch (Exception e) {
			if(idFiscale != null)
				piva = idFiscale;
			else
				piva = "";
		}
		String cf = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale();
		if(cf != null && cf.equals(piva)) {
			if(cf.length() == 16)
				piva = "";
			else
				cf = "";
		}
		if(cf == null || cf.length() == 11) 
			cf = piva;
		else
			cf = "";
		
		if(completo) 
			return new String[] {
					cognome, nome, denominaz,
					ind, num, cap, cit, prov, naz,
					piva, cf
				};
		return new String[] {
			cognome, nome,
			ind, cap, cit, prov, naz,
			piva, cf
		};
	}
	
	public int getDocumentSize() {
		if(fattura == null) return -1;
		return fattura.getFatturaElettronicaBody().size();
	}

	/**
	 * get invoice kind, date and number
	 * @param fatt id number
	 * @return String[] {kind, data, numero, causale, divisa}
	 */
	public String[] getDatiDocumento(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		String tipo = TipoDocumentoType.getDescription(ft.getDatiGenerali().getDatiGeneraliDocumento().getTipoDocumento().value());
		String data = getITAData(ft.getDatiGenerali().getDatiGeneraliDocumento().getData());
		String numero = ft.getDatiGenerali().getDatiGeneraliDocumento().getNumero();
		String divisa = ft.getDatiGenerali().getDatiGeneraliDocumento().getDivisa();
		String causa = "";
		for (String s : ft.getDatiGenerali().getDatiGeneraliDocumento().getCausale()) {
			causa = causa + s + "<BR>";
		}
		if(causa.length() > 4) causa = causa.substring(0, causa.length()-4);
		return new String[] {tipo, data, numero, causa, divisa};
	}
	
	public DatiRitenutaType getDatiRitenuta(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		return ft.getDatiGenerali().getDatiGeneraliDocumento().getDatiRitenuta();
	}
	
	public DatiCassaPrevidenzialeType getDatiCassa(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		try {
			return ft.getDatiGenerali().getDatiGeneraliDocumento().getDatiCassaPrevidenziale().get(0);
		}
		catch (Exception e) { }
		return null;
	}
	
	public DatiBolloType getDatiBollo(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		return ft.getDatiGenerali().getDatiGeneraliDocumento().getDatiBollo();
	}
	
	public BigDecimal getDatiArrotondamento(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		return ft.getDatiGenerali().getDatiGeneraliDocumento().getArrotondamento();
	}
	
	public List<ScontoMaggiorazioneType> getDatiSconto(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		return ft.getDatiGenerali().getDatiGeneraliDocumento().getScontoMaggiorazione();
	}
	
	public List<DettaglioLineeType> getRigheDocumento(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		List<DettaglioLineeType> dett = ft.getDatiBeniServizi().getDettaglioLinee();
		return dett;
	}
	public List<DatiRiepilogoType> getDatiRiepilogo(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		List<DatiRiepilogoType> dett = ft.getDatiBeniServizi().getDatiRiepilogo();
		return dett;
	}
	public BigDecimal[] getTotali(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
	    return new BigDecimal[] { 
	    		ft.getDatiGenerali().getDatiGeneraliDocumento().getImportoTotaleDocumento(),
	    		ft.getDatiGenerali().getDatiGeneraliDocumento().getArrotondamento()
	    };
	}

	public DatiTrasportoType getDatiTrasporto(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		return ft.getDatiGenerali().getDatiTrasporto();
	}

	public List<DatiPagamentoType> getDatiPagamento(int id) {
		if(fattura == null) return null;
		FatturaElettronicaBodyType ft = fattura.getFatturaElettronicaBody().get(id);
		return ft.getDatiPagamento();
	}
	
	public DatiTrasmissioneType getDatiTrasmissione() {
		if(fattura == null) return null;
		return fattura.getFatturaElettronicaHeader().getDatiTrasmissione();
	}
	
	public String getNonConforme() {
		return nonConforme;
	}
	/**
	 * check if p7m present, or special signature PA present
	 * @return
	 */
	public boolean isSigned() {
		if(fattura == null) throw new RuntimeException("Fattura nulla");
		try {
			if(firmaP7M || fattura.getSignature().getSignedInfo().getSignatureMethod().getAlgorithm() != null)
				return true;
		} catch (Exception e) { }
		return false;
	}
	
	public File getAttachment(int id) {
		if(fattura == null) return null;
		List<AllegatiType> all = fattura.getFatturaElettronicaBody().get(id).getAllegati();
		for (AllegatiType allegato : all) {
			try {
				String ext = allegato.getFormatoAttachment();
				String compr = allegato.getAlgoritmoCompressione();
				if(compr != null) {
					String allegatoName = "fatturaAllegato"+allegato.getNomeAttachment();
					if(compr.startsWith(".") == false)
						 compr = "."+compr;
					File f = File.createTempFile(allegatoName, compr);
					FileOutputStream stream = new FileOutputStream(f);
					stream.write(allegato.getAttachment());
					stream.close();
					return f;
				} else {
				String allegatoName = "fatturaAllegato"+allegato.getNomeAttachment();
				if( ext != null && allegatoName.toLowerCase().endsWith(ext.toLowerCase()) ) {
					allegatoName = allegatoName.substring(0, allegatoName.lastIndexOf("."));
					ext = ext.toLowerCase();
				}
				if( ext == null && allegatoName.contains(".") ) {
					ext = allegatoName.substring(allegatoName.lastIndexOf(".")).toLowerCase();
					allegatoName = allegatoName.substring(0, allegatoName.lastIndexOf("."));
				}
				File f = File.createTempFile(allegatoName, "."+ext);
				FileOutputStream stream = new FileOutputStream(f);
				stream.write(allegato.getAttachment());
				stream.close();
				return f;
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	

	public static String getITAData(XMLGregorianCalendar data) {
		if (data == null) return "";
        return String.format("%02d-%02d-%04d", data.getDay(),data.getMonth(),data.getYear());
    }
}
