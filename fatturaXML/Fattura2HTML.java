package fatturaXML;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import fatturaXML.*;

public class Fattura2HTML {

	FatturaXMLreader fattura;
	public Fattura2HTML(FatturaXMLreader f) {
		fattura = f;
	}

	/**
	 * get html for id num document
	 * @param id doc num
	 * @return string html
	 */
	public String getHTMLdoc(int id) {
		if(fattura == null) return null;
		try {
			//leggo il modello
			InputStream in = getClass().getResourceAsStream("/resources/modello.html"); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String line;
			while ( (line = reader.readLine()) != null ) {
				sb.append(line);
			}
			//procedo:
			String html = sb.toString();
			String cedente = "ced";
			String dest = "com";
			String[] cedanagraf  = {"cognome","nome","denominazione","indirizzo", "numero", "cap", "citta","prov","nazione","tel","fax","email","partitaiva","codicefiscale"};
			String[] destanagraf = {"cognome","nome","denominazione","indirizzo", "numero", "cap", "citta","prov","nazione","partitaiva","codicefiscale"};
			String[] cedData = fattura.getDatiFornitore(true);
			String[] destData = fattura.getDatiDestinatario(true);
			//cedente:
			if(cedData[12] != null && cedData[12].length() > 2) cedData[12] = "P.IVA: "+cedData[12];
			if(cedData[13] != null && cedData[13].length() > 2) cedData[13] = "C.F.: "+cedData[13];
			for (int i = 0; i < cedanagraf.length; i++) {
				String key = "%"+cedente+cedanagraf[i];
				try {
					html = html.replaceFirst(key, cedData[i]);
				}
				catch (Exception e) {
					html = html.replaceFirst(key, "");
				}
			}
			//destin:
			if(destData[9].length() > 2) destData[9] = "P.IVA: "+destData[9];
			if(destData[10].length() > 2) destData[10] = "C.F.: "+destData[10];
			for (int i = 0; i < destanagraf.length; i++) {
				String key = "%"+dest+destanagraf[i];
				html = html.replaceFirst(key, destData[i]);
			}
//			System.out.println("FatturaXML.getHTMLdoc()\n"+html);
			String[] doc = {"tipo", "datadocumento","numerodocumento", "annotazioni", "divisa"};
			String[] docum = fattura.getDatiDocumento(id);
			for (int i = 0; i < doc.length; i++) {
				String key = "%"+doc[i];
				html = html.replaceFirst(key, docum[i]);
			}
			//il modello accetta 14 linee, se sono di piu' farà due pagine:
			List<DettaglioLineeType> linee = fattura.getRigheDocumento(id);
			int qt = linee.size();
			if(qt > 13) {
				System.err.println("TODO MULTIPAGE");
			}
			BigDecimal mioTot = new BigDecimal("0");
			for (int row = 0; row < 14; row++) {
				DettaglioLineeType r;
				try {
					r = linee.get(row);
					mioTot = mioTot.add(r.getPrezzoTotale());
				}
				catch (Exception e1) {
					r = new DettaglioLineeType();
					r.setDescrizione("&nbsp;");
					r.setQuantita(null);
					r.setPrezzoUnitario(new BigDecimal("0"));
					r.setPrezzoTotale(new BigDecimal("0"));
					r.setAliquotaIVA(new BigDecimal("0"));
					r.setNumeroLinea(-1); 
				}
				String codice = "";
				try {
					codice = r.getCodiceArticolo().get(0).getCodiceTipo()+" ";
				}
				catch (Exception e) { }
				String descr = r.getDescrizione();
//				if(r.numeroLinea == 1)
//				System.out.println("FatturaXML.getHTMLdoc()linea : "+r.numeroLinea + " " + r.quantita);
				html = html.replaceFirst("%descrizione", codice+descr);
				if(r.getQuantita() == null || r.getNumeroLinea() == -1)
					html = html.replaceFirst("%qt", "");
				else
					html = html.replaceFirst("%qt", r.getQuantita().toString());
				html = html.replaceFirst("%impriga", r.getNumeroLinea() == -1 ? "" : r.getPrezzoUnitario().toString());
				html = html.replaceFirst("%totriga", r.getNumeroLinea() == -1 ? "" : r.getPrezzoTotale().toString());
				html = html.replaceFirst("%iva", r.getNumeroLinea() == -1 ? "" : r.getAliquotaIVA().toString());
			}
			List<DatiRiepilogoType> riep = fattura.getDatiRiepilogo(id);
			BigDecimal imponibile = new BigDecimal("0");
			BigDecimal impostaIva = new BigDecimal("0");
			BigDecimal ritenuta = new BigDecimal("0");
			BigDecimal ritAliquota = new BigDecimal("0");
			BigDecimal cassa = new BigDecimal("0");
			DatiRitenutaType rit = fattura.getDatiRitenuta(id);
			if(rit != null) {
				ritenuta = ritenuta.add(rit.getImportoRitenuta());
				ritAliquota = rit.getAliquotaRitenuta();
			}
			DatiCassaPrevidenzialeType cas = fattura.getDatiCassa(id);
			if(cas != null) {
				html = html.replaceFirst("%CASSANAME", TipoCassaType.getDescriptionShort(cas.getTipoCassa().name())+" "+cas.getAlCassa()+"%");
				html = html.replaceFirst("%cassaimposta", cas.getImportoContributoCassa().toString());
				cassa = cas.getImportoContributoCassa();
			} else {
				html = html.replaceFirst("%CASSANAME", "");
				html = html.replaceFirst("%cassaimposta", "");
			}
			BigDecimal spese = new BigDecimal("0");
			for (int rot = 0; rot < 6; rot++) {
				DatiRiepilogoType rp;
				try {
					rp = riep.get(rot);
					html = html.replaceFirst("%alqiva", rp.getAliquotaIVA().toString());
					if(rp.getEsigibilitaIVA() != null) {
						html = html.replaceFirst("%esigibilita", EsigibilitaIVAType.getDescription(rp.getEsigibilitaIVA().value()));
					} else
						html = html.replaceFirst("%esigibilita", "");
					if(rp.getImponibileImporto() != null) {
						html = html.replaceFirst("%imponibile", rp.getImponibileImporto().toString());
						imponibile = imponibile.add(rp.getImponibileImporto());
					} else
						html = html.replaceFirst("%imponibile", "");
					if(rp.getImposta() != null) {
						html = html.replaceFirst("%imposta", rp.getImposta().toString());
						impostaIva = impostaIva.add(rp.getImposta());
					}
					else
						html = html.replaceFirst("%imposta", "");
					if(rp.getSpeseAccessorie() != null) {
						html = html.replaceFirst("%spese", rp.getSpeseAccessorie().toString());
						spese = spese.add(rp.getSpeseAccessorie());
					}
					else
						html = html.replaceFirst("%spese", "");
					if(rp.getRiferimentoNormativo() != null) {
						html = html.replaceFirst("%rifamm", rp.getRiferimentoNormativo().toString());
					} else
						html = html.replaceFirst("%rifamm", "");
				}
				catch (Exception e) {
					html = html.replaceFirst("%alqiva", "");
					html = html.replaceFirst("%esigibilita", "");
					html = html.replaceFirst("%imponibile", "");
					html = html.replaceFirst("%imposta", "");
					html = html.replaceFirst("%spese", "");
					html = html.replaceFirst("%rifamm", "");
				}
			}
			//verifica:
			if(imponibile.compareTo(mioTot) > 0) {
				if(imponibile.add(new BigDecimal(- cassa.doubleValue() - spese.doubleValue())).equals(mioTot)) {
					imponibile = mioTot;
				}
			}
			//
			html = html.replaceFirst("%totale" , imponibile.toString());
			html = html.replaceFirst("%totimposta" , impostaIva.toString());
			html = html.replaceFirst("%risultivato" , imponibile.add(impostaIva).toString());
			html = html.replaceFirst("%bollo", fattura.getDatiBollo(id) != null ? fattura.getDatiBollo(id).getImportoBollo().toString() : "");
			html = html.replaceFirst("%alqritacc", ritenuta.toString().equals("0") ? "" : ritAliquota.toString()+"%");
			html = html.replaceFirst("%ritenuta", ritenuta.toString().equals("0") ? "" : "- "+ritenuta.toString());
			html = html.replaceFirst("%risultato", imponibile.add(impostaIva).subtract(ritenuta).toString());
			
			DatiTrasportoType dtra = fattura.getDatiTrasporto(id);
			if(dtra != null) {
				//%consegna %trasporto %imballaggio %pagamento
				try {
					html = html.replaceFirst("%consegna", String.format("Consegna<br>%s",FatturaXMLreader.getITAData(dtra.getDataOraConsegna())));
				}
				catch (Exception e) { }
				try {
					html = html.replaceFirst("%trasporto",dtra.getCausaleTrasporto());
				}
				catch (Exception e) { }
				try {
					html = html.replaceFirst("%imballaggio",dtra.getDescrizione());
				}
				catch (Exception e) { }
			}
			//pulisco i residui
			html = html.replaceFirst("%consegna", "");
			html = html.replaceFirst("%trasporto","");
			html = html.replaceFirst("%imballaggio","");
			List<DatiPagamentoType> pagamento = fattura.getDatiPagamento(id);
			try {
				for (DatiPagamentoType datipag : pagamento) {
					String iban = datipag.getDettaglioPagamento().get(0).getIBAN();
					if(iban == null) iban = "";
					String datapag = FatturaXMLreader.getITAData(datipag.getDettaglioPagamento().get(0).getDataScadenzaPagamento());
					html = html.replaceFirst("%pagamento",
							String.format("Pagamenti:<br>scadenza: %s<br>IBAN:%s",
									datapag , iban
							));
				}
			}
			catch (Exception e) {
				html = html.replaceFirst("%pagamento", "");
			}
			html = html.replaceFirst("%digitalsign", fattura.isSigned() ? "Presente" : "Assente");
			html = html.replaceFirst("%codiceunivoco", fattura.getDatiTrasmissione().getProgressivoInvio());
			return html;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
