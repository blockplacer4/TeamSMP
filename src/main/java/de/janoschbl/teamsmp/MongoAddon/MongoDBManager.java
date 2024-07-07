package de.janoschbl.teamsmp.MongoAddon;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoDBManager {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> teamCollection;

    public MongoDBManager(String connectionString, String dbName) {
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase(dbName);
        teamCollection = database.getCollection("teams");
    }

    public void addTeam(Team team) {
        teamCollection.insertOne(team.toDocument());
    }


    public Team getTeamById(ObjectId id) {
        Document doc = teamCollection.find(Filters.eq("_id", id)).first();
        if (doc != null) {
            return Team.fromDocument(doc);
        }
        return null;
    }


    public Team getTeamByName(String name) {
        Document doc = teamCollection.find(Filters.eq("name", name)).first();
        if (doc != null) {
            return Team.fromDocument(doc);
        }
        return null;
    }


    public List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        for (Document doc : teamCollection.find()) {
            teams.add(Team.fromDocument(doc));
        }
        return teams;
    }


    public void updateTeam(ObjectId id, Team updatedTeam) {
        teamCollection.updateOne(Filters.eq("_id", id), new Document("$set", updatedTeam.toDocument()));
    }


    public void deleteTeam(ObjectId id) {
        teamCollection.deleteOne(Filters.eq("_id", id));
    }


    public void addMemberToTeam(ObjectId teamId, UUID member) {
        teamCollection.updateOne(Filters.eq("_id", teamId), new Document("$addToSet", new Document("members", member.toString())));
    }


    public void removeMemberFromTeam(ObjectId teamId, UUID member) {
        teamCollection.updateOne(Filters.eq("_id", teamId), new Document("$pull", new Document("members", member.toString())));
    }


    public void close() {
        mongoClient.close();
    }
}
