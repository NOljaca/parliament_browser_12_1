package Database;

import Bundestag.Fractions.Int.FractionInt;
import Bundestag.Persons.Int.MemberInt;
import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.AgendaInt;
import Bundestag.Session.Int.CommentInt;
import Bundestag.Session.Int.SessionInt;
import Bundestag.Session.Int.SpeechInt;
import Database.MongoDB_Impl.*;
import PropertyHandlers.DBConnectionProperties;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.IOException;
import java.util.*;

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
     */

    public MongoCollection<Document> getCollection(String collection) {
        return mongoDatabase.getCollection(collection);
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of SessionInt
     * Objects inside the collection and checks if all corresponding numbers exist
     * otherwise it creates a document for the not found number inside the collection
     * @param sessions a list of SessionInt to be added to the collection
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
     * This method first checks if the collection exists and then iterates through a list of TagesordnungInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param agendas a list of TagesordnungInt to be added to the collection
     */

    public void createAgendas(List<AgendaInt> agendas) {
        createCollection("agendas");
        MongoCollection<Document> collection = mongoDatabase.getCollection("agendas");
        for (AgendaInt agenda : agendas) {
            Document filter = new Document("id", agenda.getId());
            if (collection.find(filter).first() == null) {
                Agenda_MongoDB_Impl agendaMongoDB = new Agenda_MongoDB_Impl(agenda);
                collection.insertOne(agendaMongoDB.toDocument());
            }
        }
    }

    /**
     * This method first checks if the collection exists and then iterates through a list of AbgeordneterInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param members a list of AbgeordneterInt to be added to the collection
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
     * This method first checks if the collection exists and then iterates through a list of FraktionInt
     * Objects inside the collection and checks if all corresponding names exist
     * otherwise it creates a document for the not found name inside the collection
     * @param fractions a list of FraktionInt to be added to the collection
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
     * This method first checks if the collection exists and then iterates through a list of RednerInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param speakers a list of RednerInt to be added to the collection
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
     * This method first checks if the collection exists and then iterates through a list of RedeInt
     * Objects inside the collection and checks if all corresponding IDs exist
     * otherwise it creates a document for the not found ID inside the collection
     * @param speeches a list of RedeInt to be added to the collection
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
     * This Method retrieves all FraktionInt Objects from the fraktionen collection
     * @return a list of FraktionInt objects representing all Fraktionen in the collection
     */

    public List<FractionInt> getFractions() {
        List<FractionInt> fractions = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("fractions");
        Document filter = new Document("shortName", 1);
        for (Document fraction : collection.find().projection(filter)) {
            FractionInt fractionMongoDB = new Fraction_MongoDB_Impl(mongoDatabase, fraction.getString("name"));
            fractions.add(fractionMongoDB);
        }
        return fractions;
    }

    /**
     * This Method retrieves all SpeakerInt Objects from the speakers collection
     * @return a list of SpeakerInt objects representing all Speakers in the collection
     */

    public List<SpeakerInt> getRednern() {
        List<SpeakerInt> rednern = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("redner");
        Document projection = new Document("id", 1).append("name", 1).append("nachname", 1)
                .append("fraktion", 1).append("reden", 1);
        FindIterable<Document> sortedRednern = collection.find().projection(projection).sort(new Document("fraktion", 1).append("name", 1).append("nachname", 1));
        for (Document redner : sortedRednern) {
            String id = redner.getString("id");
            String name = redner.getString("name");
            String nachname = redner.getString("nachname");
            String fraktion = redner.getString("fraktion");
            List<String> reden = redner.getList("reden", String.class);
            int redenSize = reden.size();
            Document rednerDoc = new Document().append("id", id).append("name", name).append("nachname", nachname).append("fraktion", fraktion).append("reden", redenSize);
            SpeakerInt speakerInt = new Speaker_MongoDB_Impl(mongoDatabase, rednerDoc);
            rednern.add(speakerInt);
        }
        return rednern;
    }

    /**
     * This method retrieves a specific Redner object from Rednercollection
     * @param id: Redner id of wanted Redner
     * @return Redner Object
     */

    public SpeakerInt getSpeakerByID(String id) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("redner");
        Document filter = new Document("id", id);
        Document rednerDocument = collection.find(filter).first();
        if (rednerDocument != null) {
            String name = rednerDocument.getString("name");
            String nachname = rednerDocument.getString("nachname");
            String fraktion = rednerDocument.getString("fraktion");
            List<String> reden = rednerDocument.getList("reden", String.class);
            String bildUrl = rednerDocument.getString("bildUrl");
            Document rednerDoc = new Document().append("id", id).append("name", name).append("nachname", nachname).append("fraktion", fraktion).append("redeIds", reden).append("bildUrl", bildUrl);
            return new Speaker_MongoDB_Impl(mongoDatabase, rednerDoc);
        }
        else {
            return null;
        }
    }

    public void putDocument(MongoCollection<Document> collection, String id, Document document, String key) {
        Document filter = new Document("id", id);

        Document update = new Document("$set", new Document(key, document));

        collection.updateOne(filter, update);
    }
}
