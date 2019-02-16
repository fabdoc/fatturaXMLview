package fatturaXML;


import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import fatturaXML.*;

public class Fattura2PDF {

	private FatturaXMLreader fattura;
	public Fattura2PDF(FatturaXMLreader f) {
		fattura = f;
	}

	/**
	 * get pdf for id num document
	 * @param id doc num
	 * @return boolean true if ok
	 */
	public boolean getPDFdoc(int id, String filename) {
		if(fattura == null) return false;
		
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		// Create a new empty document
		PDDocument document = new PDDocument();

		// Create a new blank page and add it to the document
		PDPage page = new PDPage();
		document.addPage( page );

		try {
			PDRectangle rect = page.getMediaBox();
            // rect can be used to get the page width and height

			// Create a new font object selecting one of the PDF base fonts
			PDFont fontPlain = PDType1Font.HELVETICA;
			PDFont fontBold = PDType1Font.HELVETICA_BOLD;
//			PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
//			PDFont fontMono = PDType1Font.COURIER;

			// Start a new content cos which will "hold" the content that's about to be created
			PDPageContentStream cos = new PDPageContentStream(document, page);

			int line = 1;

//			// Define a text content cos using the selected font, move the cursor and draw some text
//			cos.beginText();
//			cos.setFont(fontPlain, 11);
//			cos.newLineAtOffset(rect.getLowerLeftX(), rect.getHeight() - 50*(++line));
//			System.out.println("Fattura2PDF.getPDFdoc()"+rect.getLowerLeftX()+"  "+(rect.getHeight() - 50*(++line)));
//			cos.showText("Hello World");
//			cos.endText();
//
//			int columnTwoRightX = 470;
//	        int fontSize = 12;
//	        
//			cos.beginText();
//			cos.setFont(fontItalic, fontSize);
//			// format the double value with thousands separator and  two decimals
//			String text = String.format("%,.2f", 123456f);
//			// get the width of the formated value
//			float textWidth = getTextWidth(fontItalic, fontSize, text);
//			// align the position to (right alignment minus text width)
//			cos.newLineAtOffset(columnTwoRightX - textWidth, 700);
//			cos.showText(text);
//			// align the positon back to columnTwoRightX plus offset for column three
////			cos.newLineAtOffset(textWidth + columnThreeOffsetX, 0);
////			cos.showText("description ");
//			cos.endText();
//			
//			
//			cos.beginText();
//			cos.setFont(fontBold, 11);
//			cos.newLineAtOffset(100, rect.getHeight() - 50*(++line));
//			cos.showText("Bold");
//			cos.endText();
//
//			cos.beginText();
//			cos.setFont(fontMono, 11);
//			cos.setNonStrokingColor(Color.BLUE);
//			cos.newLineAtOffset(100, rect.getHeight() - 50*(++line));
//			cos.showText("Monospaced blue");
//			cos.endText();
//
//			drawRectangle(cos, 200, 150 , 200, 150, Color.orange, false);
//			
//			drawCircle(cos, 100, 100, 20, Color.cyan, false);
//			drawCircle(cos, 300, 100, 20, Color.cyan, true);
//			
//			drawLine(cos, 1, new Point(0,0), new Point((int) (rect.getWidth()/2), (int) (rect.getHeight()/2)), Color.yellow);


			String[] cedData = fattura.getDatiFornitore(true);
//			//cedente:
			if(cedData[12] != null && cedData[12].length() > 2) cedData[12] = "P.IVA: "+cedData[12];
			if(cedData[13] != null && cedData[13].length() > 2) cedData[13] = "C.F.: "+cedData[13];

			//titolo
			if(cedData[0].isEmpty() && cedData[1].isEmpty())
				addText(cos, cedData[2], fontBold, 11,
						rect.getLowerLeftX()+10,  rect.getHeight() - 11*(++line),
						Color.black, SwingConstants.LEFT);
			else
				addText(cos, cedData[0]+" "+cedData[1], fontBold, 11,
					rect.getLowerLeftX()+10,  rect.getHeight() - 11*(++line),
					Color.black, SwingConstants.LEFT);
			//indirizzo
			addText(cos, cedData[3]+" "+cedData[4],
					fontPlain, 11,
					rect.getLowerLeftX()+11, rect.getHeight() - 11*(++line),
					Color.black, SwingConstants.LEFT);
			//cap citta prov e stato
			addText(cos, cedData[5]+" "+cedData[6]+" "+cedData[7]+" "+cedData[8],
					fontPlain, 11,
					rect.getLowerLeftX()+11, rect.getHeight() - 11*(++line),
					Color.black, SwingConstants.LEFT);
			//recapiti
			addText(cos, ((cedData[ 9] == null ? "" : cedData[ 9])+" "+
						 (cedData[10] == null ? "" : cedData[10]) +" "+
						 (cedData[11] == null ? "" : cedData[11])).trim(),
					fontPlain, 10,
					rect.getLowerLeftX()+10, rect.getHeight() - 11*(++line),
					Color.black, SwingConstants.LEFT);
			//piva
			if(cedData[12] != null)
				addText(cos, cedData[12],
					fontPlain, 10,
					rect.getLowerLeftX()+10, rect.getHeight() - 11*(++line),
					Color.black, SwingConstants.LEFT);
			//cf
			if(cedData[13] != null)
				addText(cos, cedData[13],
					fontPlain, 11,
					rect.getLowerLeftX()+10, rect.getHeight() - 11*(++line),
					Color.black, SwingConstants.LEFT);
			String[] docum = fattura.getDatiDocumento(id);
			//dati trasmissione
			line = 1;
			addText(cos, docum[0]+" n."+docum[2],
					fontPlain, 12,
					rect.getUpperRightX()-10, rect.getHeight() - 12*(++line),
					Color.black, SwingConstants.RIGHT);
			addText(cos, "data "+docum[1],
					fontPlain, 12,
					rect.getUpperRightX()-10, rect.getHeight() - 13*(++line),
					Color.black, SwingConstants.RIGHT);
			line++;
			addText(cos, "Firma digitale: "+(fattura.isSigned() ? "Presente" : "Assente"),
					fontPlain, 10,
					rect.getUpperRightX()-10, rect.getHeight() - 11*(++line),
					Color.gray, SwingConstants.RIGHT);
			addText(cos, "Codice di Trasmissione: "+fattura.getDatiTrasmissione().getProgressivoInvio(),
					fontPlain, 10,
					rect.getUpperRightX()-10, rect.getHeight() - 11*(++line),
					Color.gray, SwingConstants.RIGHT);
			
			//destinatario
			addText(cos, "Destinatario:", fontPlain, 8,
					rect.getUpperRightX()/2,  rect.getHeight() - 12*(++line),
					Color.gray, SwingConstants.LEFT);
			String[] destData = fattura.getDatiDestinatario(true);
			if(destData[9].length() > 2) destData[9] = "P.IVA: "+destData[9];
			if(destData[10].length() > 2) destData[10] = "C.F.: "+destData[10];
			//titolo
			if(destData[0].isEmpty() && destData[1].isEmpty())
				addText(cos, destData[2], fontBold, 12,
						rect.getUpperRightX()/2,  rect.getHeight() - 12*(++line),
						Color.black, SwingConstants.LEFT);
			else
				addText(cos, destData[0]+" "+destData[1], fontBold, 12,
					rect.getUpperRightX()/2,  rect.getHeight() - 12*(++line),
					Color.black, SwingConstants.LEFT);
			//indirizzo
			addText(cos, destData[3]+" "+destData[4],
					fontPlain, 12,
					rect.getUpperRightX()/2, rect.getHeight() - 12*(++line),
					Color.black, SwingConstants.LEFT);
			//cap citta prov e stato
			addText(cos, destData[5]+" "+destData[6]+" "+destData[7]+" "+destData[8],
					fontPlain, 12,
					rect.getUpperRightX()/2, rect.getHeight() - 12*(++line),
					Color.black, SwingConstants.LEFT);
			
			//divisa (a sinistra)
			addText(cos, "Prezzi espressi in "+docum[4],
					fontPlain, 8,
					rect.getLowerLeftX()+10, rect.getHeight() - 12*(line),
					Color.gray, SwingConstants.LEFT);
			if(fattura.getNonConforme() != null) {
				addText(cos, "Documento NON CONFORME: "+fattura.getNonConforme(),
						fontBold, 10,
						rect.getLowerLeftX()+10, rect.getHeight() - 13*(line),
						Color.red, SwingConstants.LEFT);
			}
			//cf
			if(destData[10] != null)
				addText(cos, destData[10],
					fontPlain, 12,
					rect.getUpperRightX()/2, rect.getHeight() - 12*(++line),
					Color.black, SwingConstants.LEFT);
			//piva
			if(destData[9] != null)
				addText(cos, destData[9],
					fontPlain, 12,
					rect.getUpperRightX()/2, rect.getHeight() - 12*(++line),
					Color.black, SwingConstants.LEFT);

			line++;
			//table
			drawRectangle(cos,
					rect.getLowerLeftX()+5, rect.getHeight() - 12*(line),
					rect.getUpperRightX()-10,  14.5f*(line+10),
					Color.gray, 0.5f);
			drawLine(cos, 13,
					rect.getLowerLeftX()+4, rect.getHeight() - 10.1f*(line+3),
					rect.getUpperRightX()-4,  rect.getHeight() - 10.1f*(line+3),
					Color.lightGray);
			float middle = (rect.getUpperRightX()-rect.getLowerLeftX())/2f;

			line+=2;

			//rows
			List<DettaglioLineeType> linee = fattura.getRigheDocumento(id);
			int qt = linee.size();
			BigDecimal mioTot = new BigDecimal("0");
			for (int row = 0; row < qt; row++) {
				DettaglioLineeType r;
				try {
					r = linee.get(row);
					mioTot = mioTot.add(r.getPrezzoTotale());
				}
				catch (Exception e1) {	}
			}
			int max_per_page = 25;
			line = addRows(linee, line, cos, rect, fontPlain, 0, max_per_page);

			line = 45;
			int lineRiep = line;
			String[] headers = new String[]{"Aliquota IVA","Esigibilità","Imponibile","Imposta","Spese Acc.","Rif.amministrativi"};
			int[] post = new int[]{SwingConstants.LEFT,SwingConstants.LEFT, SwingConstants.RIGHT,SwingConstants.RIGHT,SwingConstants.RIGHT,SwingConstants.LEFT, SwingConstants.RIGHT};

			float[] xxt = new float[]{
					rect.getLowerLeftX()+10,
					rect.getLowerLeftX()+70,
					rect.getLowerLeftX()+middle/2+middle/3,
					rect.getLowerLeftX()+middle,
					rect.getLowerLeftX()+middle+middle/4,
					rect.getLowerLeftX()+middle+middle/4+10,
					};

			List<DatiRiepilogoType> riep = fattura.getDatiRiepilogo(id);
			BigDecimal imponibile = new BigDecimal("0");
			BigDecimal impostaIva = new BigDecimal("0");
			BigDecimal ritenuta = new BigDecimal("0");
			BigDecimal ritAliquota = new BigDecimal("0");
			BigDecimal cassa = new BigDecimal("0");
			BigDecimal bollo = new BigDecimal("0");
			BigDecimal arrotondamento = new BigDecimal("0");
			BigDecimal scontoMaggiorazione = new BigDecimal("0");
			String cassaname = "";
			String cassaimposta = "";
			DatiRitenutaType rit = fattura.getDatiRitenuta(id);
			if(rit != null) {
				ritenuta = ritenuta.add(rit.getImportoRitenuta());
				ritAliquota = rit.getAliquotaRitenuta();
			}
			DatiCassaPrevidenzialeType cas = fattura.getDatiCassa(id);
			if(cas != null) {
				cassaname = TipoCassaType.getDescriptionShort(cas.getTipoCassa().name())+" "+cas.getAlCassa()+"%";
				cassaimposta = cas.getImportoContributoCassa().toString();
				cassa = cas.getImportoContributoCassa();
			}
			BigDecimal spese = new BigDecimal("0");
			String[] datiiva = new String[6];
			int qtiva = 0;
			for (int rot = 0; rot < 6; rot++) {
				DatiRiepilogoType rp;
				try {
					rp = riep.get(rot);
					datiiva[0] = rp.getAliquotaIVA().toString();
					if(rp.getEsigibilitaIVA() != null) {
						datiiva[1] = EsigibilitaIVAType.getDescription(rp.getEsigibilitaIVA().value());
					} else
						datiiva[1] = "" ;
					if(rp.getImponibileImporto() != null) {
						datiiva[2] = rp.getImponibileImporto().toString();
						imponibile = imponibile.add(rp.getImponibileImporto());
					} else
						datiiva[2] = "" ;
					if(rp.getImposta() != null) {
						datiiva[3] = rp.getImposta().toString();
						impostaIva = impostaIva.add(rp.getImposta());
					} else
						datiiva[3] = "" ;
					if(rp.getSpeseAccessorie() != null) {
						datiiva[4] = rp.getSpeseAccessorie().toString();
						spese = spese.add(rp.getSpeseAccessorie());
					} else
						datiiva[4] = "";
					if(rp.getRiferimentoNormativo() != null) {
						datiiva[5] = rp.getRiferimentoNormativo().toString();
					} else
						datiiva[5] = "" ;
					qtiva++;
				}
				catch (Exception e) {
					for (int i = 0; i < datiiva.length; i++) {
						datiiva[i] = "";
					}
				}
				line++;
				for (int i = 0; i < datiiva.length -1; i++) {
					addText(cos, datiiva[i],
							fontPlain, 8,
							xxt[i], rect.getHeight() - 11.1f*(line),
							Color.black, post[i]);
				}
				float newr = addCRText(cos, datiiva[datiiva.length-1],
						fontPlain, 7,
						xxt[datiiva.length-1], rect.getHeight() - 11.1f*(line), 7, 110f);
				if(newr/10 > 1) {
					line += newr/10 -1;
					qtiva+= newr/10 -1;
				}
			}
			line = lineRiep;
			//table dettagli iva
			drawRectangle(cos,
					rect.getLowerLeftX()+5, rect.getHeight() - 11 * line,
					rect.getUpperRightX() - 100,  11 *(qtiva + 1),
					Color.gray, 0.5f);
			drawLine(cos, 13,
					rect.getLowerLeftX() + 4.1f, rect.getHeight() - 11f*(line) - 2f,
					rect.getUpperRightX() - 95, rect.getHeight() - 11f * (line) - 2f,
					Color.lightGray);
			for (int i = 0; i < headers.length; i++) {
				addText(cos, headers[i],
						fontPlain, 7,
						xxt[i], rect.getHeight() - 11.1f*(line),
						Color.black, post[i]);
			}
			//verifica:
			if(imponibile.compareTo(mioTot) > 0) {
				if(imponibile.add(new BigDecimal(- cassa.doubleValue() - spese.doubleValue())).equals(mioTot)) {
					imponibile = mioTot;
				}
			}
			line = 65;
			//totali:
			addText(cos, "Imponibile",
					fontPlain, 12,
					rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.05f*(line),
					Color.black, SwingConstants.RIGHT);
			addText(cos, imponibile.toString(),
					fontPlain, 12,
					rect.getUpperRightX()-10, rect.getHeight() - 11.05f*(line++),
					Color.black, SwingConstants.RIGHT);
			if(cassaname.isEmpty() == false) {
				addText(cos, cassaname+" "+cassaimposta,
						fontPlain, 11,
						rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.1f*(line),
						Color.black, SwingConstants.RIGHT);
				addText(cos, cassa.toString(),
						fontPlain, 11,
						rect.getUpperRightX()-10, rect.getHeight() - 11.1f*(line++),
						Color.black, SwingConstants.RIGHT);
			}
			if(impostaIva.doubleValue() != 0) {
				addText(cos, "Imposte",
						fontPlain, 12,
						rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.1f*(line),
						Color.black, SwingConstants.RIGHT);
				addText(cos, impostaIva.toString(),
						fontPlain, 12,
						rect.getUpperRightX()-10, rect.getHeight() - 11.1f*(line++),
						Color.black, SwingConstants.RIGHT);
			}
			addText(cos, "Totale",
					fontBold, 12,
					rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.2f*(line),
					Color.black, SwingConstants.RIGHT);
			addText(cos, imponibile.add(impostaIva).toString(),
					fontBold, 12,
					rect.getUpperRightX()-10, rect.getHeight() - 11.2f*(line++),
					Color.black, SwingConstants.RIGHT);
			if(ritenuta.doubleValue() != 0) {
				addText(cos, "Ritenuta Acc. "+ritAliquota.toString()+"%",
						fontPlain, 12,
						rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.2f*(line),
						Color.black, SwingConstants.RIGHT);
				addText(cos, ritenuta.toString(),
						fontPlain, 12,
						rect.getUpperRightX()-10, rect.getHeight() - 11.2f*(line++),
						Color.black, SwingConstants.RIGHT);
			}
			if(fattura.getDatiArrotondamento(id) != null) {
				arrotondamento = fattura.getDatiArrotondamento(id);
				addText(cos, "Arrotondamento",
						fontPlain, 12,
						rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.2f*(line),
						Color.black, SwingConstants.RIGHT);
				addText(cos, arrotondamento.toString(),
						fontPlain, 12,
						rect.getUpperRightX()-10, rect.getHeight() - 11.2f*(line++),
						Color.black, SwingConstants.RIGHT);
			}
			if(fattura.getDatiSconto(id) != null) {
				for (ScontoMaggiorazioneType scmag : fattura.getDatiSconto(id)) {
					if(scmag.getImporto() != null)
						scontoMaggiorazione = scontoMaggiorazione.add(scmag.getImporto());
				}
				if(scontoMaggiorazione.doubleValue() != 0d) {
					addText(cos, scontoMaggiorazione.doubleValue() > 0d ? "Maggiorazione applicata" : "Sconto applicato",
							fontPlain, 12,
							rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.23f*(line),
							Color.black, SwingConstants.RIGHT);
					addText(cos, scontoMaggiorazione.toString(),
							fontPlain, 12,
							rect.getUpperRightX()-10, rect.getHeight() - 11.23f*(line++),
							Color.black, SwingConstants.RIGHT);
				}
			}
			if(fattura.getDatiBollo(id) != null) {
				addText(cos, "Imposta di Bollo",
						fontPlain, 12,
						rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.25f*(line),
						Color.black, SwingConstants.RIGHT);
				bollo = fattura.getDatiBollo(id).getImportoBollo();
				addText(cos, bollo.toString(),
						fontPlain, 12,
						rect.getUpperRightX()-10, rect.getHeight() - 11.25f*(line++),
						Color.black, SwingConstants.RIGHT);
			}
			
			String doc = docum[0].toUpperCase();
			addText(cos, "TOTALE "+doc,
					fontBold, 12,
					rect.getUpperRightX()-middle/4-20, rect.getHeight() - 11.3f*(line),
					Color.black, SwingConstants.RIGHT);
			addText(cos,
					imponibile.add(impostaIva).subtract(ritenuta).add(bollo).add(arrotondamento).add(scontoMaggiorazione).toString(),
					fontBold, 12,
					rect.getUpperRightX()-10, rect.getHeight() - 11.3f*(line++),
					Color.black, SwingConstants.RIGHT);
			line++;
			DatiTrasportoType dtra = fattura.getDatiTrasporto(id);
			if(dtra != null) {
				//%consegna %trasporto %imballaggio %pagamento
				try {
					addText(cos, "Consegna",
							fontPlain, 7,
							rect.getLowerLeftX()+10, rect.getHeight() - 11.3f*(line++),
							Color.black, SwingConstants.LEFT);
					addText(cos, FatturaXMLreader.getITAData(dtra.getDataOraConsegna()),
							fontPlain, 7,
							rect.getLowerLeftX()+10, rect.getHeight() - 11.2f*(line--),
							Color.black, SwingConstants.LEFT);
				}
				catch (Exception e) { }
				try {
					addText(cos, dtra.getCausaleTrasporto(),
							fontPlain, 7,
							rect.getLowerLeftX()+middle/4, rect.getHeight() - 11.3f*(line++),
							Color.black, SwingConstants.LEFT);
					addText(cos, dtra.getDescrizione(),
							fontPlain, 7,
							rect.getLowerLeftX()+middle/4, rect.getHeight() - 11.3f*(line--),
							Color.black, SwingConstants.LEFT);
				}
				catch (Exception e) { }
			}
			//dati pagamento-----------------------
			List<DatiPagamentoType> pagamento = fattura.getDatiPagamento(id);
			try {
				int x = 20;
				float y = 8;
				int l = pagamento.size() +1;
				for (DatiPagamentoType datipag : pagamento) {
					String iban = datipag.getDettaglioPagamento().get(0).getIBAN();
					if(iban == null) iban = "";
					String datapag = FatturaXMLreader.getITAData(datipag.getDettaglioPagamento().get(0).getDataScadenzaPagamento());
					CondizioniPagamentoType condPag = datipag.getCondizioniPagamento();
					ModalitaPagamentoType modPag = datipag.getDettaglioPagamento().get(0).getModalitaPagamento();
					if(datapag.isEmpty() == false || iban.isEmpty() == false || condPag.toString().isEmpty() == false) {
						addText(cos, "Pagamenti:",
								fontBold, 7,
								rect.getLowerLeftX()+x,  y*l--,
								Color.black, SwingConstants.LEFT);
						StringBuilder sb = new StringBuilder();
						
						if(datapag.length() > 0)
							sb.append(" Scadenza: ").append(datapag);
						if(iban.length() > 0)
							sb.append(" IBAN:").append(iban);
						if(condPag.toString().length() > 0)
							sb.append(" condizioni: ").append(CondizioniPagamentoType.getDescription(condPag.toString()))
							  .append(" ");
						if(modPag.toString().length() > 0)
							sb.append("(").append(ModalitaPagamentoType.getDescription(modPag.toString())).append(")");
						if(sb.length() > 0)
						addText(cos, sb.toString(),
								fontPlain, 7,
								rect.getLowerLeftX()+x,  y*l--,
								Color.black, SwingConstants.LEFT);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
 
			if(docum[3] != null && docum[3].length() > 0) {
				addText(cos, docum[3],
						fontPlain, 7,
						rect.getLowerLeftX()+10, rect.getHeight() - 11.3f*(++line),
						Color.black, SwingConstants.LEFT);
			}
			if(qt > max_per_page) {
				cos.close();
				PDPage page1 = new PDPage();
				document.addPage( page1 );
				line = 1;
				addText(cos, "Righe dettagio rimanenti:",
						fontPlain, 7,
						rect.getLowerLeftX()+10, rect.getHeight() - 11.3f*(++line),
						Color.gray, SwingConstants.LEFT);
				cos = new PDPageContentStream(document, page1);
				
				line = addRows(linee, ++line, cos, rect, fontPlain, max_per_page);
			}
//			// Make sure that the content cos is closed:
			cos.close();
			// Save the newly created document
			document.save(filename);
			// finally make sure that the document is properly closed.
			document.close();

		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return true;
	}
	
	private int addRows(List<DettaglioLineeType> linee, int line, PDPageContentStream cos, PDRectangle rect, PDFont fontPlain, int... x) throws IOException {
		String[] headers = {"Beni e Descrizione","Qt.","Importo","Totale","Aliq.IVA"};
		float middle = (rect.getUpperRightX()-rect.getLowerLeftX())/2f;
		float[] xxt = {
				rect.getLowerLeftX()+10,
				rect.getUpperRightX()-middle/2.2f,
				rect.getUpperRightX()-middle+middle/2.6f,//qt
				rect.getUpperRightX()-middle+middle/5*4,
				rect.getUpperRightX()-10,
				};
		int[] post = {SwingConstants.LEFT, SwingConstants.RIGHT,SwingConstants.RIGHT,SwingConstants.RIGHT,SwingConstants.RIGHT, SwingConstants.RIGHT};
		for (int i = 0; i < headers.length; i++) {
			addText(cos, headers[i],
					fontPlain, 8,
					xxt[i], rect.getHeight() - 11*(line),
					Color.black, post[i]);
		}
		int y = x.length;
		int max = 0;
		if(y == 1)
			max = linee.size();
		else
			max = x[1];
		int fontsize = 8;
		for (int row = x[0]; row < max; row++) {
			DettaglioLineeType r;
			try {
				r = linee.get(row);
			}
			catch (Exception e1) {
				r = new DettaglioLineeType();
				r.setDescrizione(" ");
				r.setQuantita(null);
				r.setPrezzoUnitario(new BigDecimal("0"));
				r.setPrezzoTotale(new BigDecimal("0"));
				r.setAliquotaIVA(new BigDecimal("0"));
				r.setNumeroLinea(-1); 
			}
			String codice = "";
			try {
				try {
					codice = r.getCodiceArticolo().get(0).getCodiceValore()+" ";
				} catch (Exception e) {					
					codice = r.getCodiceArticolo().get(0).getCodiceTipo()+" ";
				}
			}
			catch (Exception e) { }
			String descr = r.getDescrizione();
			String[] rowdata = {
					codice+descr,
					(r.getNumeroLinea() == -1 || r.getQuantita() == null || r.getQuantita().doubleValue() < 0.01) ? "" : getQuantita(r.getQuantita()),
					(r.getNumeroLinea() == -1 || r.getPrezzoUnitario().doubleValue() == 0d) ? "" : getPrezzo(r.getPrezzoUnitario()),
					(r.getNumeroLinea() == -1 || r.getPrezzoTotale().doubleValue() == 0d) ? "" : getPrezzo(r.getPrezzoTotale()),
					(r.getNumeroLinea() == -1 || r.getPrezzoTotale().doubleValue() == 0d) ? "" : getPrezzo(r.getAliquotaIVA())		
			};
			line++;
			float newr = addCRText(cos, rowdata[0], fontPlain, fontsize, post[0], rect.getHeight() - 11*(line) - 2f, 10, middle+middle/5);
			
			for (int i = 1; i < headers.length; i++) {
				addText(cos, rowdata[i],
						fontPlain, fontsize,
						xxt[i], rect.getHeight() - 11*(line) - 2f,
						Color.black, post[i]);
			}
			if(newr/10 > 1)
				line += newr/10 -1;
		}
		return line;
	}

	private String getQuantita(BigDecimal val) {
		try {
			int qt1 = val.intValue();
			double qt2 = val.doubleValue();
			if(qt1 == qt2)
				return String.valueOf(qt1);
			return String.valueOf(qt2);
		} catch (Exception e) {
		}
		return val.toString();
	}

	private String getPrezzo(BigDecimal val) {
		try {
			StringBuilder s = new StringBuilder(val.toString());
			int point = s.indexOf(".");
			if(val.doubleValue() != 0 && point > -1)
				while (s.length() > point + 3 && s.charAt(s.length()-1) == '0')
					s.setLength(s.length()-1);
			return s.toString();
		} catch (Exception e) { }
		return val.toString();
	}

	public static void drawRectangle(PDPageContentStream cos, float x, float y, float w, float h, Color c, float filled) throws IOException {
		//draw rectangle
		if(filled == 0)
			cos.setNonStrokingColor(c);
		else
			cos.setStrokingColor(c);
		if(filled > 0)
			cos.setLineWidth(filled);
		cos.addRect(x, y, w, -h);
		if(filled == 0)
			cos.fill();
		else
			cos.closeAndStroke();
	}
	
	public static void drawLine(PDPageContentStream cos, int width,
			float startX, float startY,
			float endX,float endY,
			Color c) throws IOException {
		cos.setStrokingColor(c);
		cos.setLineWidth(width);
		cos.moveTo(startX, startY);
		cos.lineTo(endX, endY);
		cos.closeAndStroke();
	}

	/**
	 * draw circle
	 * @param cos PDPageContentStream
	 * @param cx x pos
	 * @param cy y pos
	 * @param r raggio
	 * @param red 0-255
	 * @param green 0-255
	 * @param blue 0-255
	 * @throws IOException
	 */
	public static void drawCircle(PDPageContentStream cos, int cx, int cy, int r, Color c, boolean filled) throws IOException {
	    final float k = 0.552284749831f;
	    if(filled)
	    	cos.setNonStrokingColor(c);
	    else
	    	cos.setStrokingColor(c);
	    cos.moveTo(cx - r, cy);
	    cos.curveTo(cx - r, cy + k * r, cx - k * r, cy + r, cx, cy + r);
	    cos.curveTo(cx + k * r, cy + r, cx + r, cy + k * r, cx + r, cy);
	    cos.curveTo(cx + r, cy - k * r, cx + k * r, cy - r, cx, cy - r);
	    cos.curveTo(cx - k * r, cy - r, cx - r, cy - k * r, cx - r, cy);
	    if(filled)
	    	cos.fill();
	    else
	    	cos.closeAndStroke();
	}
	
	public static float addCRText(PDPageContentStream cos, String text, PDFont font, int fontSize,
			 					float startX, float startY, float leading, float width) throws IOException {
		List<String> lines = new ArrayList<String>();
		int lastSpace = -1;
		while ( text.length() > 0 ) {
			text = text.replaceAll("\\u00a0"," ");
			int spaceIndex = text.indexOf(' ', lastSpace + 1);
			if ( spaceIndex < 0 ) spaceIndex = text.length();
			String subString = text.substring(0, spaceIndex);
			float size;
			try {
				size = fontSize * font.getStringWidth(subString) / 1000;
			} catch (Exception e) {
				size = fontSize * getSize(font, subString, e) / 1000;
			}
//			System.out.printf("'%s' - %f of %f\n", subString, size, width);
			if ( size > width ) {
				if ( lastSpace < 0 ) lastSpace = spaceIndex;
				subString = text.substring(0, lastSpace);
				lines.add(subString);
				text = "     "+text.substring(lastSpace).trim();
//				System.out.printf("'%s' is line\n", subString);
				lastSpace = -1;
			}
			else if ( spaceIndex == text.length() ) {
				lines.add(text);
//				System.out.printf("'%s' is line\n", text);
				text = "";
			}
			else {
				lastSpace = spaceIndex;
			}
		}
		cos.beginText();
		cos.setFont(font, fontSize);
		cos.newLineAtOffset(startX+8, startY);
		for (String line : lines) {
			cos.showText(line);
			cos.newLineAtOffset(0, -leading);
		}
		cos.endText();
		return lines.size() * leading;
	}
	
	private static int getSize(PDFont font, String subString, Exception e) {
		try {
			int i = Integer.parseInt(e.getMessage().substring(2, 6), 16);
			if(Character.isWhitespace((char)i))
				subString = subString.replace((char) i, ' ');
			float sz = font.getStringWidth(subString);
			return (int) sz;
		} catch (IOException e1) {
			
		}
		return 0;
	}
	
	public static void addText(PDPageContentStream cos, String text, PDFont font, int fontSize,
			float posX, float posY,	Color c, int align) {
		try {
			cos.beginText();
			cos.setFont(font, fontSize);
			//remove strange chars
			text = text.replaceAll("\\u00a0"," ");
			// get the width of the formated value
			float textWidth = //getTextWidth(font, fontSize, text);
					(font.getStringWidth(text) / 1000.0f) * fontSize;
			// align the position to (right alignment minus text width)
			float xx = 0;
			switch (align) {
			case SwingConstants.RIGHT:
				xx = posX - textWidth;
				break;
			case SwingConstants.CENTER:
				xx = posY - textWidth/2f;
				break;
			default:
				xx = posX;
				break;
			}
			cos.setNonStrokingColor(c);
			//		System.out.println("Fattura2PDF.getPDFdoc()"+xx+"  "+posY);
			cos.newLineAtOffset(xx, posY);
			cos.showText(text);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				cos.endText();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
