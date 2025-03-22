package NLP;

import NLP.DataNLP.NLPMain;
import NLP.Restructure.Restructure;
import NLP.Xmi.XmiProcessor;
import NLP.Xmi.XmiProcessor2;

/**
 * Die Klasse `NLPStructureBuilder` führt die Verarbeitungspipeline für die NLP-Analyse aus.
 * Sie steuert die Ausführung der folgenden Schritte:
 * 1. `XmiProcessor`: Lädt und verarbeitet XMI-Dateien und speichert sie in MongoDB.
 * 2. `NLPMain`: Führt NLP-Analysen durch, indem es auf die in MongoDB gespeicherten XMI-Daten zugreift.
 * 3. `Restructure`: Strukturiert die extrahierten NLP-Daten um und speichert sie in einer neuen MongoDB-Collection.
 *
 * Die einzelnen Schritte können über die entsprechenden Flags (`runXmiProcessor`, `runNLPMain`, `runRestructure`)
 * aktiviert oder deaktiviert werden.
 */
public class NLPStructureBuilder {
    /**
     * Der Einstiegspunkt für die NLP-Verarbeitungspipeline.
     * Die einzelnen Verarbeitungsschritte können über die entsprechenden Flags gesteuert werden.
     *
     */
    public static void runNLPAnalysis() {
        // Flags zur Steuerung der Verarbeitungsschritte
        boolean runXmiProcessor = false;  // Setze auf `false`, um XmiProcessor zu deaktivieren
        boolean runXmiProcessor2 = false; // Setze auf `false`, um XmiProcessor2 zu deaktivieren
        boolean runNLPMain = true;        // Setze auf `false`, um NLPMain zu deaktivieren
        boolean runRestructure = true;    // Setze auf `false`, um Restructure zu deaktivieren

        try {
            if (runXmiProcessor) {
                System.out.println("Starte XmiProcessor...");
                XmiProcessor.startProcess(); // Führt die Verarbeitung der XMI-Dateien aus
                System.out.println("XmiProcessor abgeschlossen.");
            }
            if (runXmiProcessor2) {
                System.out.println("Starte XmiProcessor2...");
                XmiProcessor2.startProcess(); // Führt die Verarbeitung der XMI-Dateien aus
                System.out.println("XmiProcessor abgeschlossen.");
            }

            if (runNLPMain) {
                System.out.println("Starte NLPMain...");
                NLPMain.startProcess(); // Führt die NLP-Analyse durch
                System.out.println("NLPMain abgeschlossen.");
            }

            if (runRestructure) {
                System.out.println("Starte Restructure...");
                Restructure.startRestructure(); // Restrukturiert die NLP-Daten
                System.out.println("Restructure abgeschlossen.");
            }

            System.out.println("Alle aktivierten Prozesse wurden erfolgreich durchgeführt.");
        } catch (Exception e) {
            System.err.println("Fehler beim Ausführen der NLP-Pipeline: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

