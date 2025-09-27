# Parliament_Browser_12_1

# NLP Prozessbeschreibung

Dieses Projekt beinhaltet eine NLP-Pipeline, die XMI-Dateien verarbeitet, Speeches analysiert und das Ergebnis in einer MongoDB-Datenbank speichert. Die wichtigsten Komponenten des Systems sind die `XmiProcessor`-Klassen, die für die Verarbeitung der XMI-Daten verantwortlich sind. Diese Klassen benötigen spezielle Cookies, um auf die Dateien zuzugreifen.

## Wichtige Cookies
Für den Zugriff auf das GitLab-Repository, von dem XMI-Dateien heruntergeladen werden, sind drei wichtige Cookies erforderlich:

- **known_sign_in**: Wird für die Authentifizierung verwendet.
- **session_id**: Wird für die Sitzung verwendet.
- **preferred_language**: Gibt die bevorzugte Sprache für die Anwendung an.

### Cookies einsehen
Um diese Cookies zu erhalten, müssen Sie sich in GitLab anmelden und dann die Entwicklertools in Ihrem Browser öffnen (F12). Gehen Sie dann zu **Application** -> **Storage** -> **Cookies**, um die Cookies zu sehen. Die relevanten Cookies sind:

- **_gitlab_session**: Sitzungs-ID
- **known_sign_in**: Authentifizierung
- **preferred_language**: Bevorzugte Sprache

## Klassenbeschreibung

### 1. **`Extraction`**
Diese Klasse stellt statische Methoden zur Extraktion von Informationen aus einem `JCas` (Java Common Analysis Structure) zur Verfügung. Die wichtigsten Methoden sind:

- `extractTopics(JCas jcas)`: Extrahiert Themen aus dem JCAS.
- `extractTokens(JCas jcas)`: Extrahiert Tokens.
- `extractPOSTags(JCas jcas)`: Extrahiert POS-Tags (Parts of Speech).
- `extractDependencies(JCas jcas)`: Extrahiert Abhängigkeiten.
- `extractNamedEntities(JCas jcas)`: Extrahiert benannte Entitäten.
- `extractAnalysisResults(JCas jcas)`: Extrahiert Analyseergebnisse.
- `extractLemmas(JCas jcas)`: Extrahiert Lemmas.
- `extractSentences(JCas jcas)`: Extrahiert Sätze.

### 2. **`Initialize`**
Diese Klasse initialisiert die NLP-Pipeline, indem sie die verschiedenen Komponenten (z.B. spaCy, GerVader, ParlBERT) hinzufügt und die Verbindung zu Docker und den erforderlichen Treibern herstellt.

- `Initialize()`: Konstruktor.
- `processJCas(JCas jcas)`: Startet den Verarbeitungsprozess für ein JCAS.
- `initComposer()`, `initDockerDriver()`, `addSpacyComponent()`, `addGerVaderComponent()`, `addParlBERTComponent()`: Initialisieren verschiedene NLP-Komponenten.

### 3. **`NLPMain`**
Diese Klasse enthält die Hauptlogik für die Ausführung der NLP-Analyse.

- `main(String[] args)`: Startet die Anwendung.
- `runNLPProcess()`: Führt den NLP-Verarbeitungsprozess aus.

### 4. **`JCasConverter`**
Diese Klasse konvertiert eine `Speech`-Instanz in ein `JCas`-Objekt.

- `convert(Speech speech)`: Konvertiert eine Speech in ein JCAS.

### 5. **`CaseSerialization`**
Stellt Methoden zur Verfügung, um das `JCas` zu speichern und zu laden.

- `saveCas(JCas jcas, File file)`: Speichert das `JCAS` in einer Datei.
- `loadCas(File file)`: Lädt ein `JCAS` aus einer Datei.

### 6. **`Speech`**
Eine Klasse, die eine Rede modelliert.

- `Speech(String id, String text)`: Konstruktor zur Erstellung einer Speech mit ID und Text.
- `getId()`, `setId(String id)`: Getter und Setter für die ID der Rede.
- `getText()`, `setText(String text)`: Getter und Setter für den Text der Rede.

### 7. **`Restructure`**
Diese Klasse verarbeitet und strukturiert die Analyseergebnisse und speichert sie in der MongoDB.

- `processDependencies(Document casDoc)`: Verarbeitet Abhängigkeiten.
- `processNamedEntities(Document casDoc)`: Verarbeitet benannte Entitäten.
- `processPosTags(Document casDoc)`: Verarbeitet POS-Tags.
- `processSentences(Document casDoc)`: Verarbeitet Sätze.
- `processTopics(Document casDoc)`: Verarbeitet Themen.
- `log(String message)`, `logError(String message, Exception e)`: Loggt Nachrichten und Fehler.

### 8. **`XmiParser`**
Diese Klasse parst XMI-Dateien und extrahiert verschiedene Analyseergebnisse.

- `parseXmiToDocument(String filePath, String speechId)`: Parsen einer XMI-Datei in ein `Document`.
- Weitere Methoden extrahieren Tokens, POS-Tags, Lemmas, Abhängigkeiten, benannte Entitäten, Themen und Sätze.

### 9. **`XmiProcessor`**
Die Hauptklasse zur Verarbeitung von XMI-Dateien. Diese Klasse verwendet Cookies, um die XMI-Dateien von GitLab herunterzuladen.

- `setCookies(Scanner scanner)`: Setzt die benötigten Cookies (z.B. `known_sign_in`, `session_id`).
- `downloadFileWithCookies(String fileUrl, String outputFile)`: Lädt eine XMI-Datei mit Cookies herunter.
- `existsInDatabase(String speechId)`: Überprüft, ob eine Analyse für eine Rede bereits in der Datenbank existiert.
- `runNLPProcess()`: Startet den Verarbeitungsprozess.

### 10. **`XmiProcessor2`**
Eine alternative Version der `XmiProcessor`-Klasse, die ebenfalls XMI-Dateien verarbeitet und die gleichen Methoden wie `XmiProcessor` enthält.

## Ausführung der Anwendung


Um die Anwendung auszuführen, stellen Sie sicher, dass die benötigten Cookies (z.B. `known_sign_in`, `session_id`) korrekt gesetzt sind und das die geeigneten Docker Initialisiert sind um die NLP-Verarbeitung zu ermöglichen. Diese Cookies können über die Entwicklertools im Browser extrahiert werden. Verwenden Sie dann die `XmiProcessor`-Klasse oder `XmiProcessor2`, um XMI-Dateien zu verarbeiten und die Analyseergebnisse zu speichern.
Falls Sie Ihre eigene Datenbank verwenden wollen, müssen Sie die mongodb.properties-Datei anpassen, da die Datenbank-Credentials aus diesem Dokument gezogen werden.

Die Anwendung startet damit, die Protokoll-XMLs und die MdB-Stammdaten-XMLs aus der Bundestags-Webseite herunterzuladen, diese werden unter src/main/resources abgespeichert.
Danach fängt das Parsen der Dokumente an, hier werden die Reden, Sitzungen, MdBs, Redner, Tagesordnungspunkte, Kommentare und Fraktionen auf unsere Klassenstrukturen abgebildet.
Anschließend werden diese Daten in eigenen Collections in der Datenbank abgespeichert und die NLP-Analyse der Reden beginnt. Hier werden die Analysen von Prof. Abrami geparst und in der Collection abgespeichert und die restlichen, unverarbeiteten Reden werden auf ihre NLP-Daten analysiert.
Nachdem die Anwendung mit der Analyse fertig ist startet der Javalin-Webservice auf dem Port 8080 (siehe server.properties). Für die Nutzung der Webseite sehen Sie sich bitte das Benutzerhandbuch an.

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert.
