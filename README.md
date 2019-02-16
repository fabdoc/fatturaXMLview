# fatturaXMLview
Visualizzatore Fattura XML italiana (Italian Only Invoice Viewer)

ho creato questo semplice visualizzatore che interpreta i file XML di fattura elettronica e li mostra come pdf o come html.

il main principale crea una finestra che accetta il drop di un file, e lo converte in PDF mostrandolo con il visualizzatore di sistema.

La conversione in pdf è molto migliorabile, perchè necessiterebbe apporto di persone che diano fatture xml di vario tipo e persone che migliorino output pdf.

essendo nuovo di github, non so neanche come impostare i dependency...

<dependency>
     <groupId>pdfbox-2.0.13.jar</groupId>
     <artifactId>...</artifactId>
     <version>2.0.13</version>
</dependency>
<dependency>
     <groupId>jaxb-api-2.3.0.jar</groupId>
     <artifactId>...</artifactId>
     <version>2.3.0</version>
</dependency>
