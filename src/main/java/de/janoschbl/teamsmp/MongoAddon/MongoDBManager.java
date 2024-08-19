package de.janoschbl.teamsmp.MongoAddon;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class MongoDBManager {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> teamCollection;
    private final MongoCollection<Document> configCollection;

    public MongoDBManager(String connectionString, String dbName) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(dbName);
        teamCollection = database.getCollection("teams");
        configCollection = database.getCollection("config");
    }

    public void addTeam(Team team) {
        teamCollection.insertOne(team.toDocument());
    }

    public void deleteTeam(Team team) {
        teamCollection.deleteOne(team.toDocument());
    }

    public Team getTeamByName(String name) {
        Document doc = teamCollection.find(Filters.eq("name", name)).first();
        if (doc != null) {
            return Team.fromDocument(doc);
        }
        return null;
    }

    public void addMemberToTeam(ObjectId teamId, UUID member) {
        teamCollection.updateOne(Filters.eq("_id", teamId), new Document("$addToSet", new Document("members", member.toString())));
    }


    public List<String> getAllTeams() {
        Bson projection = fields(include("name"));

        MongoIterable<Document> documents = teamCollection.find().projection(projection);

        List<String> names = new ArrayList<>();
        for (Document doc : documents) {
            String name = doc.getString("name");
            if (name != null) {
                names.add(name);
            }
        }
        return names;
    }

    public Set<UUID> getAllMembers() {
        Set<UUID> members = new HashSet<>();

        teamCollection.aggregate(List.of(
                new Document("$unwind", "$members"),
                new Document("$group", new Document("_id", "$members"))
        )).forEach(doc -> {
            String memberString = doc.getString("_id");
            members.add(UUID.fromString(memberString));
        });

        return members;
    }

    public Team getTeamByUUID(UUID member) {
        String memberString = member.toString();
        Document doc = teamCollection.find(Filters.eq("members", memberString)).first();
        if (doc != null) {
            return Team.fromDocument(doc);
        }
        return null;
    }


    public void removeMemberFromTeam(ObjectId teamId, UUID member) {
        teamCollection.updateOne(Filters.eq("_id", teamId), new Document("$pull", new Document("members", member.toString())));
    }

    public void addHeartToTeam(ObjectId teamId) {
        teamCollection.updateOne(Filters.eq("_id", teamId), Updates.inc("hearts", 1));
    }

    public void removeHeartFromTeam(ObjectId teamId) {
        teamCollection.updateOne(Filters.eq("_id", teamId), Updates.inc("hearts", -1));
    }

    public void setWorldBoarderCounter(Integer wb_size) {
        configCollection.updateOne(Filters.eq("_id", 2104), Updates.set("worldBorderBlocks", wb_size));
    }

    public void setBlockPerPlayer(Integer blocks) {
        configCollection.updateOne(Filters.eq("_id", 2104), Updates.set("blockPerPlayer", blocks));
    }

    public void setBlockPerPlayerDeath(Integer blocks) {
        configCollection.updateOne(Filters.eq("_id", 2104), Updates.set("blockPerPlayerDeath", blocks));
    }

    public void addConfig(Config config) {
        configCollection.insertOne(config.toDocument());
    }

    public Config getConfig() {
        Document doc = configCollection.find(Filters.eq("_id", 2104)).first();
        System.out.println("Config found");
        if (doc != null) {
            return Config.fromDocument(doc);
        }
        return null;
    }

    public void close() {
        mongoClient.close();
    }

}
