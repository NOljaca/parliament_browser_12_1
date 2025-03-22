package Database;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.MemberInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.CommentInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
import Database.MongoDB_Impl.*;
import Helper.PictureExtractor;
import PropertyHandlers.DBConnectionProperties;
import Rest.JSON.SpeakerJSON;
import Rest.JSON.SpeechJSON;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Projections.*;

public class MongoDBHandler {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private String host;
    private String database;
    private String user;
    private String password;
    private String port;
    private String collection;
    private String configFilePath = "mongodb.properties";


    public MongoDBHandler() throws IOException {
        DBConnectionProperties dbConnectionProperties = new DBConnectionProperties(configFilePath);
        host = dbConnectionProperties.getHost();
        database = dbConnectionProperties.getDatabase();
        user = dbConnectionProperties.getUser();
        password = dbConnectionProperties.getPassword();
        port = dbConnectionProperties.getPort();
        collection = dbConnectionProperties.getCollection();

        String connectionString = String.format("mongodb://%s:%s@%s:%s/%s", user, password, host, port, database);
        MongoClientSettings mongoClientSettings =
                MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionString)).build();
        mongoClient = MongoClients.create(mongoClientSettings);
        mongoDatabase = mongoClient.getDatabase(database);
    }

    /**
     * This Method first checks if the collection with the given name exists in the database.
     * If the collection exists then the methods exists,
     * otherwise it creates the collection using the createCOllection
     * @param collection the name of the collection that exists or needs to be created
     *
     * @author Amal
     */

    private void createCollection(String collection) {
        ListCollectionsIterable<Document> documents = mongoDatabase.listCollections();
        for (Document document : documents) {
            if (collection.equals(document.getString("name"))) {
                break;
            }
        }
        mongoDatabase.createCollection(collection);
    }

    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    /**
     * This Method retrieves a collection by its name
     * @param collection the name of the collection to retrieve
     * @return a MongoCollection object representing the requested collection
     *
     * @author Amal
     */

    public MongoCollection<Document> getCollection(String collection) {
        return mongoDatabase.getCollection(collection);
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of SessionInt
     * Objects inside the collection and checks if all corresponding numbers exist
     * otherwise it creates a document for the not found number inside the collection
     * @param sessions a list of SessionInt to be added to the collection
     *
     * @author Amal
     */

    public void createSessions(Map<Integer, SessionInt> sessions) {
        createCollection("sessions");
        MongoCollection<Document> collection = mongoDatabase.getCollection("sessions");
        for (SessionInt session : sessions.values()) {
            Document filter = new Document("id", session.getId());

            if (collection.find(filter).first() == null) {
                Session_MongoDB_Impl sessionMongoDB = new Session_MongoDB_Impl(session);
                collection.insertOne(sessionMongoDB.toDocument());
            }

        }
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of AgendaInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param agendas a list of AgendaInt to be added to the collection
     *
     * @author Amal
     */

    public void createAgendas(List<AgendaInt> agendas) {
        System.out.println(agendas.size());
        createCollection("agendas");
        MongoCollection<Document> collection = mongoDatabase.getCollection("agendas");
        for (AgendaInt agenda : agendas) {
            Document filter = new Document("agendaId", agenda.getAgendaId());
            if (collection.find(filter).first() == null) {
                Agenda_MongoDB_Impl agendaMongoDB = new Agenda_MongoDB_Impl(agenda);
                collection.insertOne(agendaMongoDB.toDocument());
            }
        }
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of MemberInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param members a list of MemberInt to be added to the collection
     *
     * @author Amal
     */

    public void createMembers(Map<String, MemberInt> members) {
        createCollection("members");
        MongoCollection<Document> collection = mongoDatabase.getCollection("members");
        for (MemberInt member : members.values()) {
            Document filter = new Document("id", member.getId());
            if (collection.find(filter).first() == null) {
                Member_MongoDB_Impl memberMongoDB = new Member_MongoDB_Impl(member);
                collection.insertOne(memberMongoDB.toDocument());
            }
        }
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of FractionInt
     * Objects inside the collection and checks if all corresponding names exist
     * otherwise it creates a document for the not found name inside the collection
     * @param fractions a list of FractionInt to be added to the collection
     *
     * @author Amal
     */

    public void createFractions(Map<String, FractionInt> fractions) {
        createCollection("fractions");
        MongoCollection<Document> collection = mongoDatabase.getCollection("fractions");
        for (FractionInt fraction : fractions.values()) {
            Document filter = new Document("shortName", fraction.getShortName());
            if (collection.find(filter).first() == null) {
                Fraction_MongoDB_Impl fractionMongoDB = new Fraction_MongoDB_Impl(fraction);
                collection.insertOne(fractionMongoDB.toDocument());
            }
        }
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of SpeakerInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param speakers a list of SpeakerInt to be added to the collection
     *
     * @author Amal
     */

    public void createSpeakers(Map<String, SpeakerInt> speakers) {
        createCollection("speakers");
        MongoCollection<Document> collection = mongoDatabase.getCollection("speakers");
        for (SpeakerInt speaker : speakers.values()) {
            Document filter = new Document("id", speaker.getId());
            if (collection.find(filter).first() == null) {
                System.out.println("creating speaker " + speaker.getId() + " " + speaker.getName() + " " + speaker.getSurname());
                Speaker_MongoDB_Impl speakerMongoDB = new Speaker_MongoDB_Impl(speaker);
                collection.insertOne(speakerMongoDB.toDocument());
            }
        }
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of SpeakerInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param speeches a list of SpeakerInt to be added to the collection
     *
     * @author Amal
     */

    public void createSpeeches(List<SpeechInt> speeches) {
        createCollection("speeches");
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches");

        List<String> existingIds = collection.find().projection(new Document("id", true))
                .map(doc -> doc.getString("id"))
                .into(new ArrayList<>());

        List<Document> newSpeeches = speeches.stream()
                .filter(speech -> !existingIds.contains(speech.getId()))
                .map(speech -> new Speech_MongoDB_Impl(speech).toDocument())
                .toList();

        if (!newSpeeches.isEmpty()) {
            collection.insertMany(newSpeeches);
        }
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of CommentInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param comments a list of CommentInt to be added to the collection
     *
     * @author Amal
     */

    public void createComments(List<CommentInt> comments){
        createCollection("comments");
        MongoCollection<Document> collection = mongoDatabase.getCollection("comments");
        for (CommentInt comment : comments) {
            Document filter = new Document("id", comment.getId());
            if (collection.find(filter).first() == null) {
                Comment_MongoDB_Impl commentMongoDB = new Comment_MongoDB_Impl(comment);
                collection.insertOne(commentMongoDB.toDocument());
            }
        }
    }

    /**
     * This method fetches the speech-id, session and speaker of every speech-document in the 'speeches' collection.
     * @return speeches
     *
     * @author Amal
     */
    public List<SpeechInt> getSpeeches(){
        List<SpeechInt> speeches = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches");
        Document projection = new Document("id", 1).append("session", 1).append("speaker", 1);
        for (Document doc : collection.find().projection(projection).limit(100).sort(new Document("session", 1))) {
            SpeechInt speechMongoDB = new Speech_MongoDB_Impl(mongoDatabase, doc);
            speeches.add(speechMongoDB);
        }
        return speeches;
    }

    /**
     * This method fetches every session of the 'sessions' collection.
     * @return sessions
     *
     * @author Amal
     */
    public List<SessionInt> getSessions(){
        List<SessionInt> sessions = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("sessions");
        for (Document doc : collection.find()) {
            SessionInt sessionMongoDB = new Session_MongoDB_Impl(doc, mongoDatabase);
            sessions.add(sessionMongoDB);
        }
        return sessions;
    }

    /**
     * This method fetches the session with the given session-id.
     * @param sessionId session-id of wanted session
     * @return session with given id
     */
    public SessionInt getSessionById(String sessionId){
        MongoCollection<Document> collection = mongoDatabase.getCollection("sessions");
        Document filter = new Document("id", Integer.parseInt(sessionId));
        Document sessionDoc = collection.find(filter).first();
        return new Session_MongoDB_Impl(sessionDoc, mongoDatabase);
    }

    /**
     * This method fetches the session-date of each session in the 'sessions' collection.
     * @return speech-dates
     *
     * @author Amal
     */
    public List<String> getAllSpeechDates() {
        MongoCollection<Document> collection = mongoDatabase.getCollection("sessions");
        List<String> speechDates = new ArrayList<>();
        for (Document doc : collection.find()) {
            Date date = doc.getDate("date");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            speechDates.add(localDateTime.format(formatter));
        }
        return speechDates;
    }

    /**
     * This method fetches every speech of the given session
     * @param sessionId session-id of the session from which the speeches are wanted
     * @return speeches of given session
     *
     * @author Amal
     */
    public List<SpeechJSON> getSpeechesBySession(String sessionId){
        List<SpeechJSON> speeches = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches");
        Document filter = new Document("session", Integer.parseInt(sessionId));
        Document projection = new Document("id", 1).append("session", 1).append("speaker", 1).append("agenda", 1);
        for (Document doc : collection.find(filter).projection(projection)) {
            if (doc.getString("id").startsWith("ID")) {
                String speechId = doc.getString("id");
                String speakerId = doc.getString("speaker");
                String speakerName = getSpeakerNameAndSurname(speakerId);
                String agendaId = doc.getString("agenda");
                SpeechJSON speechJSON = new SpeechJSON(sessionId, speakerName, speechId, agendaId, speakerId);
                speeches.add(speechJSON);
            }
        }
        return speeches;
    }

    /**
     * This method fetches the name and surname for the wanted speaker
     * @param id id of wanted speaker
     * @return name and surname of speaker
     *
     * @author Amal
     */
    public String getSpeakerNameAndSurname(String id) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("speakers");
        Document filter = new Document("id", id);
        Document projection = new Document("name", 1).append("surname", 1);
        Document speakerDoc = collection.find().filter(filter).projection(projection).first();
        return speakerDoc.getString("name") + " " + speakerDoc.getString("surname");
    }

    /**
     * This method fetches the speech with given speech-id.
     * @param speechId id of wanted speech
     * @return wanted speech
     *
     * @author Amal
     */
    public SpeechInt getSpeechById(String speechId){
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches");
        Document filter = new Document("id", speechId);
        Document speechDoc = collection.find(filter).first();
        return new Speech_MongoDB_Impl(mongoDatabase, speechDoc);
    }

    /**
     * This Method retrieves all FractionInt Objects from the 'fractions' collection
     * @return a list of FractionInt objects representing all fractions in the collection
     *
     * @author Amal
     */

    public List<FractionInt> getFractions() {
        List<FractionInt> fractions = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("fractions");
        Document filter = new Document("shortName", 1);
        for (Document fraction : collection.find().projection(filter)) {
            FractionInt fractionMongoDB = new Fraction_MongoDB_Impl(mongoDatabase, fraction.getString("shortName"));
            fractions.add(fractionMongoDB);
        }
        return fractions;
    }

    /**
     * This Method retrieves all SpeakerInt Objects from the speakers collection
     * @return a list of SpeakerInt objects representing all Speakers in the collection
     *
     * @author Amal
     */

    public List<SpeakerInt> getAllSpeakers() {
        List<SpeakerInt> speakers = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speakers");
        Document projection = new Document("id", 1).append("name", 1).append("surname", 1)
                .append("fraction", 1).append("speeches", 1);
        FindIterable<Document> sortedSpeakers = collection.find().projection(projection).sort(new Document("fraction", 1).append("name", 1).append("surname", 1));
        for (Document speaker : sortedSpeakers) {
            String id = speaker.getString("id");
            String name = speaker.getString("name");
            String surname = speaker.getString("surname");
            String fraction = speaker.getString("fraction");
            List<String> speeches = speaker.getList("speeches", String.class);
            Document speakerDoc = new Document().append("id", id).append("name", name).append("surname", surname).append("fraction", fraction).append("speeches", speeches);
            SpeakerInt speakerInt = new Speaker_MongoDB_Impl(mongoDatabase, speakerDoc);
            speakers.add(speakerInt);
        }
        return speakers;
    }

    /**
     * This Method retrieves all SpeakerInt Objects from the speakers collection mapped to the SpeakerJSON-class
     * @return a list of SpeakerJSON objects representing all Speakers in the collection
     *
     * @author Amal
     */

    public List<SpeakerJSON> getAllSpeakersJson() {
        List<SpeakerJSON> speakers = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speakers");
        Document projection = new Document("id", 1).append("name", 1).append("surname", 1)
                .append("fraction", 1).append("speeches", 1);
        FindIterable<Document> sortedSpeakers = collection.find().projection(projection).sort(new Document("fraction", 1).append("name", 1).append("surname", 1));
        for (Document speaker : sortedSpeakers) {
            String id = speaker.getString("id");
            String name = speaker.getString("name");
            String surname = speaker.getString("surname");
            String fraction = speaker.getString("fraction");
            SpeakerJSON speakerJson = new SpeakerJSON(id, name + " " + surname, fraction);
            speakers.add(speakerJson);
        }
        return speakers;
    }

    /**
     * This method retrieves a specific Speaker object from the 'speakers' collection
     * @param id id of wanted speaker
     * @return wanted speaker-object
     *
     * @author Amal
     */

    public SpeakerInt getSpeakerByID(String id) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("speakers");
        Document filter = new Document("id", id);
        Document speakerDoc = collection.find(filter).first();
        return new Speaker_MongoDB_Impl(mongoDatabase, speakerDoc);
    }

    /**
     * Method for adding a document to given collection.
     * @param collection collection where the document needs to be put in
     * @param id id of document
     * @param document document
     * @param key key for document
     *
     * @author Amal
     */
    public void putDocument(MongoCollection<Document> collection, String id, Document document, String key) {
        Document filter = new Document("id", id);

        Document update = new Document("$set", new Document(key, document));

        collection.updateOne(filter, update);
    }

    /**
     * Method for updating an existing document
     * @param collection collection in which the old document is
     * @param oldDocumentId id of old document
     * @param newDocument new document
     *
     * @author Amal
     */
    public void updateDocument(MongoCollection<Document> collection, String oldDocumentId, Document newDocument) {
        Document filter = new Document("id", oldDocumentId);
        collection.updateOne(filter, new Document("$set", newDocument));
    }

    /**
     * Method for deleting a document
     * @param collection collection in which the document is
     * @param id id of document
     *
     * @author Amal
     */
    public void deleteDocument(MongoCollection<Document> collection, String id) {
        Document filter = new Document("id", id);
        collection.deleteOne(filter);
    }

    /**
     * Method for counting all documents in collection
     * @param collection
     * @return amount of documents in collection
     *
     * @author Amal
     */
    public long countDocuments(MongoCollection<Document> collection) {
        return collection.countDocuments();
    }

    /**
     * This method fetches the sentences with their respective sentiment-analysis of given speech.
     * @param id speech-id
     * @return sentences with sentiment-analysis
     *
     * @author Amal
     */
    public List<Map<String, Object>> getSentences(String id) {
        List<Map<String, Object>> sentences = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches2");
        Document filter = new Document("id", id);
        Document speechDoc = collection.find(filter).first();
        Document sentenceDoc = speechDoc.get("analysis", Document.class).get("sentences", Document.class);
        for (String sentence : sentenceDoc.keySet()) {
            Map<String, Object> sentenceMap = new HashMap<>();
            sentenceMap.put("sentence", sentence);
            sentenceMap.put("sentiment", sentenceDoc.getDouble(sentence));
            sentences.add(sentenceMap);
        }
        return sentences;
    }

    /**
     * This method fetches the named-entities of given speech
     * @param id speech-id
     * @return named-entities of speech
     *
     * @author Amal
     */
    public List<Map<String, Object>> getNamedEntities(String id) {
        List<Map<String, Object>> namedEntites = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches2");
        Document filter = new Document("id", id);
        Document speechDoc = collection.find(filter).first();
        Document namedEntitiesDoc = speechDoc.get("analysis", Document.class).get("namedEntities", Document.class);
        for (String namedEntity : namedEntitiesDoc.keySet()) {
            for (String namedEntityWord : namedEntitiesDoc.getList(namedEntity, String.class)) {
                Map<String, Object> namedEntityMap = new HashMap<>();
                namedEntityMap.put("namedEntity", namedEntityWord);
                namedEntityMap.put("category", namedEntity);
                namedEntites.add(namedEntityMap);
            }
        }
        return namedEntites;
    }

    /**
     * This method fetches the named-entity-category counts for given speeches
     * @param speechIdsList
     * @return
     * @author Muhammed
     */
    public List<Map<String, Object>> getNamedEntitesForSpeeches(List<String> speechIdsList) {
        List<Map<String, Object>> namedEntites = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches2");
        for (String speechId : speechIdsList) {
            Document filter = new Document("id", speechId);
            Document speechDoc = collection.find(filter).first();
            Document namedEntitiesDoc = speechDoc.get("analysis", Document.class).get("namedEntities", Document.class);
            for (String namedEntity : namedEntitiesDoc.keySet()) {
                for (String namedEntityWord : namedEntitiesDoc.getList(namedEntity, String.class)) {
                    Map<String, Object> namedEntityMap = new HashMap<>();
                    namedEntityMap.put("namedEntity", namedEntityWord);
                    namedEntityMap.put("category", namedEntity);
                    namedEntites.add(namedEntityMap);
                }
            }
        }
        return namedEntites;
    }

    /**
     *      * This method fetches the named-entity-category counts for given filters
     * @param fractionNames
     * @param speakerIds
     * @param dates
     * @return
     * @author Muhammed
     */
    public List<Map<String, Object>> getNamedEntitesForFilters(List<String> fractionNames, List<String> speakerIds, List<String> dates) {
        List<Integer> sessionIdsMatchingDates = new ArrayList<>();
        List<String> speakerIdsMatchingFractions = new ArrayList<>();
        List<String> speechIds = new ArrayList<>();
        findMatchingSessionIdsForDates(sessionIdsMatchingDates, dates);
        findMatchingSpeakerIdsForFractions(speakerIdsMatchingFractions, fractionNames, speakerIds);
        findMatchingSpeechIdsForAllFilters(speechIds, speakerIdsMatchingFractions, sessionIdsMatchingDates);
        return getNamedEntitesForSpeeches(speechIds);
    }

    /**
     * This method fetches the POS-tag-amounts for given speeches (selected by user) - for the charts.
     * @param speechIds speech-ids
     * @return map with pos-tags and their respective amounts in the speeches
     *
     * @author Amal
     */
    public Map<String, Integer> getPOSAmountsForSpeeches(List<String> speechIds) {
        Map<String, Integer> posAmounts = new HashMap<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches2");
        Document filter = new Document("id", new Document("$in", speechIds));
        Document projection = new Document("analysis.posTags", 1).append("_id", 0);
        FindIterable<Document> speechDocs = collection.find().filter(filter).projection(projection);
        return fillPosAmountsMap(posAmounts, speechDocs);
    }

    /**
     * This method fetches the POS-tag-amounts of speeches which correspond to given filters (selected by user) - for the charts.
     * @param fractionNames fraction-names
     * @param speakerIds speaker-ids
     * @param dates session-dates
     * @return map with pos-tags and their respective amounts in speech
     *
     * @author Amal
     */
    public Map<String, Integer> getPosAmounts(List<String> fractionNames, List<String> speakerIds, List<String> dates) {
        List<Integer> sessionIdsMatchingDates = new ArrayList<>();
        List<String> speakerIdsMatchingFractions = new ArrayList<>();
        List<String> speechIds = new ArrayList<>();
        findMatchingSessionIdsForDates(sessionIdsMatchingDates, dates);
        findMatchingSpeakerIdsForFractions(speakerIdsMatchingFractions, fractionNames, speakerIds);
        findMatchingSpeechIdsForAllFilters(speechIds, speakerIdsMatchingFractions, sessionIdsMatchingDates);
        return getPOSAmountsForSpeeches(speechIds);
    }

    /**
     * This method fetches sessions for the given dates.
     * @param sessionIdsMatchingDates list of session-ids which match the dates
     * @param dates session-dates
     *
     * @author Amal
     */
    private void findMatchingSessionIdsForDates(List<Integer> sessionIdsMatchingDates, List<String> dates) {
        if (!Objects.equals(dates.get(0), "")) {
            MongoCollection<Document> sessionsCollection = mongoDatabase.getCollection("sessions");
            for (String date : dates) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate localDate = LocalDate.parse(date, formatter);
                Date dateDate = Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
                Document filter = new Document("date", dateDate);
                Document projection = new Document("id", 1);
                Document sessionDoc = sessionsCollection.find(filter).projection(projection).first();
                assert sessionDoc != null;
                sessionIdsMatchingDates.add(sessionDoc.getInteger("id"));
            }
        }
    }

    /**
     * This method fetches the speaker-ids of speakers which contain the fraction-names.
     * @param speakerIdsMatchingFractions list of speakers matching the fraction-names
     * @param fractions fraction-names
     * @param speakers speaker-ids
     *
     * @author Amal
     */
    private void findMatchingSpeakerIdsForFractions(List<String> speakerIdsMatchingFractions, List<String> fractions, List<String> speakers) {
        //If user already selected speakers then fill the list with them, no further filtering needed.
        if (!Objects.equals(speakers.get(0), "")) {
            speakerIdsMatchingFractions.addAll(speakers);
            return;
        }
        if (fractions != null && !fractions.isEmpty()) {
            MongoCollection<Document> speakersCollection = mongoDatabase.getCollection("speakers");
            for (String fraction : fractions) {
                Document filter = new Document("fraction", fraction);
                Document projection = new Document("id", 1);
                for (Document speakerDoc : speakersCollection.find().filter(filter).projection(projection)) {
                    assert speakerDoc != null;
                    speakerIdsMatchingFractions.add(speakerDoc.getString("id"));
                }
            }
        }
    }

    /**
     * This method fetches all speech-ids which correspond to the combined filters.
     * @param speechIds speech-ids
     * @param speakerIdsMatchingFractions speaker-id filter
     * @param sessionIdsMatchingDates date-filter
     *
     * @author Amal
     */
    private void findMatchingSpeechIdsForAllFilters(List<String> speechIds, List<String> speakerIdsMatchingFractions, List<Integer> sessionIdsMatchingDates) {
        MongoCollection<Document> speechesCollection = mongoDatabase.getCollection("speeches");
        Document filter = new Document();
        if (sessionIdsMatchingDates != null && !sessionIdsMatchingDates.isEmpty()) {
            filter.append("session", new Document("$in", sessionIdsMatchingDates));
        }
        if (speakerIdsMatchingFractions != null && !speakerIdsMatchingFractions.isEmpty() && !Objects.equals(speakerIdsMatchingFractions.get(0), "")) {
            filter.append("speaker", new Document("$in", speakerIdsMatchingFractions));
        }
        for (Document speechDoc : speechesCollection.find(filter)) {
            speechIds.add(speechDoc.getString("id"));
        }
    }

    /**
     * Helper method for filling the pos-amounts-map.
     * @param posAmounts pos-amounts-map for the charts
     * @param speechDocs fetched speech-documents
     * @return map with pos-tags and their amounts
     *
     * @author Amal
     */
    private Map<String, Integer> fillPosAmountsMap(Map<String, Integer> posAmounts, FindIterable<Document> speechDocs) {
        for (Document speechDoc : speechDocs) {
            Document analysisDoc = speechDoc.get("analysis", Document.class);
            if (analysisDoc != null) {
                Document posDoc = analysisDoc.get("posTags", Document.class);
                for (String posType : posDoc.keySet()) {
                    Integer posAmount = posDoc.get(posType, List.class).size();
                    posAmounts.put(posType, posAmounts.getOrDefault(posType, 0) + posAmount);
                }
            }
        }
        return posAmounts;
    }

    /**
     * This method fetches the topic-values for given speeches (selected by user).
     * @param speechIds speech-ids
     * @return map with topics and their values.
     *
     * @author Amal
     */
    public Map<String, Double> getTopicsForSpeeches(List<String> speechIds) {
        MongoCollection<Document> speechCollection = mongoDatabase.getCollection("speeches2");
        Map<String, Double> topicsMap = new HashMap<>();
        Document filter = new Document("id", new Document("$in", speechIds));
        Document projection = new Document("analysis.topics", 1).append("_id", 0);
        FindIterable<Document> speechDocs = speechCollection.find(filter).projection(projection);

        return fillTopicsMap(topicsMap, speechDocs);
    }

    /**
     * This method fetches the POS-topic-amounts of speeches which correspond to given filters (selected by user) - for the charts.
     * @param fractionNames fraction-names
     * @param speakerIds speaker-ids
     * @param dates session-dates
     * @return map with topics and their respective amounts in speech
     *
     * @author Amal
     */
    public Map<String, Double> getTopics(List<String> fractionNames, List<String> speakerIds, List<String> dates) {
        List<Integer> sessionIdsMatchingDates = new ArrayList<>();
        List<String> speakerIdsMatchingFractions = new ArrayList<>();
        List<String> speechIds = new ArrayList<>();
        findMatchingSessionIdsForDates(sessionIdsMatchingDates, dates);
        findMatchingSpeakerIdsForFractions(speakerIdsMatchingFractions, fractionNames, speakerIds);
        findMatchingSpeechIdsForAllFilters(speechIds, speakerIdsMatchingFractions, sessionIdsMatchingDates);
        return getTopicsForSpeeches(speechIds);
    }

    /**
     * Helper method for filling the topics-map.
     * @param topicsMap topics-map for the charts
     * @param speechDocs fetched speech-documents
     * @return map with topics and their amounts
     *
     * @author Amal
     */
    private Map<String, Double> fillTopicsMap(Map<String, Double> topicsMap, FindIterable<Document> speechDocs) {
        for (Document speechDoc : speechDocs) {
            Document analysis = speechDoc.get("analysis", Document.class);
            if (analysis != null) {
                Document topics = analysis.get("topics", Document.class);
                if (topics != null) {
                    for (String topic : topics.keySet()) {
                        List<List<Object>> topicValue = topics.get(topic, List.class);
                        if (topicValue != null && !topicValue.isEmpty()) {
                            List<Object> topicValueMap = topicValue.get(0);
                            String key = topicValueMap.get(0).toString().replace("[", "");
                            Double value = ((Number) topicValueMap.get(1)).doubleValue();
                            topicsMap.put(key, topicsMap.getOrDefault(key, 0.0) + value);
                        }
                    }
                }
            }
        }
        return topicsMap;
    }

    /**
     * This method fetches the pos-amounts for every speech. Complex aggregation needed to be implemented because of the size of speeches in the database.
     * @return map with pos-tags with their amounts
     */
    public Map<String, Integer> getAggregatedPOSAmounts() {
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches2");

        List<Bson> pipeline = Arrays.asList(
                match(exists("analysis.posTags")),
                project(fields(
                        excludeId(),
                        computed("posArray", new Document("$objectToArray", "$analysis.posTags"))
                )),
                unwind("$posArray"),
                project(fields(
                        computed("pos", "$posArray.k"),
                        computed("count", new Document("$size", "$posArray.v"))
                )),
                group("$pos", sum("totalCount", "$count"))
        );

        Map<String, Integer> posAmounts = new HashMap<>();
        AggregateIterable<Document> result = collection.aggregate(pipeline);
        for (Document doc : result) {
            posAmounts.put(doc.getString("_id"), doc.getInteger("totalCount"));
        }
        return posAmounts;
    }

    /**
     * This method fetches the topic-amounts for every speech.
     * Complex aggregation needed to be implemented because of the size of speeches in the database.
     * @return map with topics with their amounts
     */
    public Map<String, Double> getAggregatedTopics() {
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches2");
        List<Bson> pipeline = new ArrayList<>();

        pipeline.add(match(exists("analysis.topics")));

        pipeline.add(project(fields(
                excludeId(),
                computed("topicsArray", new Document("$objectToArray", "$analysis.topics"))
        )));

        pipeline.add(unwind("$topicsArray"));

        pipeline.add(project(fields(
                computed("topicsList", new Document("$cond", Arrays.asList(
                        new Document("$isArray", "$topicsArray.v"),
                        new Document("$arrayElemAt", Arrays.asList("$topicsArray.v", 0)),
                        new ArrayList<>()
                )))
        )));

        pipeline.add(project(fields(
                computed("topic", new Document("$arrayElemAt", Arrays.asList("$topicsList", 0))),
                computed("value", new Document("$toDouble", new Document("$arrayElemAt", Arrays.asList("$topicsList", 1))))
        )));

        pipeline.add(group("$topic", sum("totalValue", "$value")));

        AggregateIterable<Document> result = collection.aggregate(pipeline);

        Map<String, Double> topicsMap = new HashMap<>();
        for (Document doc : result) {
            if (doc.getString("_id") != null) {
                String topicKey = doc.getString("_id").replace("[", "");
                Number totalValue = doc.get("totalValue", Number.class);
                topicsMap.put(topicKey, totalValue.doubleValue());
            }
        }
        return topicsMap;
    }

    /**
     * Fetches every speech which contains given text. Limited to 100 speeches due to performance issues.
     * @param text substring for search.
     * @return map with json-format which contains the list of speechJsons and the amount of speeches that were found.
     * @author Muhammed
     */
    public Map<String, Object> getSpeechesByText(String text) {
        List<SpeechJSON> speeches = new ArrayList<>();
        MongoCollection<Document> speechCollection = mongoDatabase.getCollection("speeches");
        FindIterable<Document> speechFind = speechCollection.find(
                new Document("content", new Document("$regex", text).append("$options", "i"))).limit(100);
        long amount = speechCollection.countDocuments(new Document("content", new Document("$regex", text).append("$options", "i")));
        for (Document document : speechFind) {
            String id = document.getString("id");
            String speakerId = document.getString("speaker");
            speeches.add(new SpeechJSON(id, speakerId));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("speeches", speeches);
        result.put("amount", amount);
        return result;
    }

    public void putSpeakerPictures() throws IOException {
        List<SpeakerInt> speakers = getAllSpeakers();
        MongoCollection<Document> speakerCollection = mongoDatabase.getCollection("speakers");
        PictureExtractor pictureExtractor = new PictureExtractor(this);
        for (SpeakerInt speaker : speakers) {
            Document filter = new Document("id", speaker.getId());
            String pictureUrl = pictureExtractor.getPictureUrl(speaker.getId());
            speakerCollection.updateOne(filter, new Document("$set", new Document("pictureUrl", pictureUrl)));
        }
    }

    /**
     * This method fetches the sentences with their respective sentiment-analysis of given speech.
     * @param id speech-id
     * @return sentences with sentiment-analysis
     *
     * @author Muhammed
     */
    public double getSentenceSentimentValueForSpeech(String id) {
        List<Map<String, Object>> sentences = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("speeches2");
        Document filter = new Document("id", id);
        Document speechDoc = collection.find(filter).first();
        if (speechDoc == null) {return 0;}
        Document analysisDoc = speechDoc.get("analysis", Document.class);
        if (analysisDoc == null) {return 0;}
        Document sentenceDoc = analysisDoc.get("sentences", Document.class);
        if (sentenceDoc == null) {return 0;}
        for (String sentence : sentenceDoc.keySet()) {
            Map<String, Object> sentenceMap = new HashMap<>();
            sentenceMap.put("sentiment", sentenceDoc.getDouble(sentence));
            sentences.add(sentenceMap);
        }
        double speechSentiment = 0;
        for (Map<String, Object> map : sentences) {
            speechSentiment = (double) map.get("sentiment") + speechSentiment;
        }
        return speechSentiment / sentences.size();
    }

    /**
     * This method fetches the sentiment-values for given speeches.
     * @param speechIds
     * @return
     * @author Muhammed
     */
    public Map<String, Object> getSentenceSentimentValuesForSpeeches(List<String> speechIds) {
        Map<String, Object> speechSentimentValue = new HashMap<>();
        for (String speechId : speechIds) {
            double sentimentValue = getSentenceSentimentValueForSpeech(speechId);
            speechSentimentValue.put(speechId, sentimentValue);
        }
        return speechSentimentValue;
    }

    /**
     * This method fetches the sentiment-values for speeches corresponding to the filters.
     * @param fractionNames
     * @param speakerIds
     * @param dates
     * @return sentiment-values
     * @author Muhammed
     */

    public Map<String, Object> getSentenceSentimentValues(List<String> fractionNames, List<String> speakerIds, List<String> dates) {
        List<Integer> sessionIdsMatchingDates = new ArrayList<>();
        List<String> speakerIdsMatchingFractions = new ArrayList<>();
        List<String> speechIds = new ArrayList<>();
        findMatchingSessionIdsForDates(sessionIdsMatchingDates, dates);
        findMatchingSpeakerIdsForFractions(speakerIdsMatchingFractions, fractionNames, speakerIds);
        findMatchingSpeechIdsForAllFilters(speechIds, speakerIdsMatchingFractions, sessionIdsMatchingDates);
        return getSentenceSentimentValuesForSpeeches(speechIds);
    }

    /**
     * This method fetches the sentiment-values for every speech.
     * Complex aggregation needed to be implemented because of the size of speeches in the database.
     * @return map with sentiments with their values
     * @author Muhammed
     */
    public Map<String, Object> getAllSentenceSentimentValuesAggregated() {
        Bson matchStage = Aggregates.match(Filters.exists("analysis.sentences", true));

        Bson projectToArrayStage = Aggregates.project(Projections.fields(
                Projections.include("id"),
                Projections.computed("sentencesArr", new Document("$objectToArray", "$analysis.sentences"))
        ));

        Bson projectAvgStage = Aggregates.project(Projections.fields(
                Projections.include("id"),
                Projections.computed("avgSentiment", new Document("$avg", "$sentencesArr.v"))
        ));

        List<Bson> pipeline = Arrays.asList(matchStage, projectToArrayStage, projectAvgStage);

        AggregateIterable<Document> result = getCollection("speeches2").aggregate(pipeline);

        Map<String, Object> speechSentimentMap = new HashMap<>();
        for (Document doc : result) {
            String speechId = doc.getString("id");
            Double avgSentiment = doc.getDouble("avgSentiment");
            speechSentimentMap.put(speechId, avgSentiment);
        }

        return speechSentimentMap;
    }

    /**
     * This method fetches the named-entity-category counts for every speech.
     * Complex aggregation needed to be implemented because of the size of speeches in the database.
     * @return map with named-entity-category counts
     * @author Muhammed
     */
    public List<Map<String, Object>> getAllNamedEntitiesAggregated() {
        MongoCollection<Document> collection = getCollection("speeches2");

        List<Bson> pipeline = Arrays.asList(
                // Only include documents where the namedEntities field exists.
                Aggregates.match(Filters.exists("analysis.namedEntities", true)),
                // Project a new field "namedEntitiesArray" which is the result of converting the namedEntities object to an array.
                Aggregates.project(Projections.fields(
                        Projections.include("id"),
                        Projections.computed("namedEntitiesArray", new Document("$objectToArray", "$analysis.namedEntities"))
                )),
                // Unwind the array so that each document represents one category with an array of named entity values.
                Aggregates.unwind("$namedEntitiesArray"),
                // Unwind the values array so that each document represents a single occurrence.
                Aggregates.unwind("$namedEntitiesArray.v"),
                // Group by the category (the key of each pair) and count the occurrences.
                Aggregates.group("$namedEntitiesArray.k", sum("totalCount", 1))
        );

        AggregateIterable<Document> result = collection.aggregate(pipeline);

        List<Map<String, Object>> aggregatedNamedEntities = new ArrayList<>();
        for (Document doc : result) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", doc.getString("_id"));
            map.put("count", doc.getInteger("totalCount"));
            aggregatedNamedEntities.add(map);
        }
        return aggregatedNamedEntities;
    }

}
