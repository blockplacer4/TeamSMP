package de.janoschbl.teamsmp.MongoAddon;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import org.bukkit.plugin.java.JavaPlugin;

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

    public void deleteTeam(Team team) {
        teamCollection.deleteOne(team.toDocument());
    }

    public Team getTeamByUser(String uuid) {
        Document doc = teamCollection.find(Filters.elemMatch("members", Filters.eq(uuid))).first();
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

    public void addMemberToTeam(ObjectId teamId, UUID member) {
        System.out.println(teamId);
        System.out.println(member);
        teamCollection.updateOne(Filters.eq("_id", teamId), new Document("$addToSet", new Document("members", member.toString())));
    }


    public void removeMemberFromTeam(ObjectId teamId, UUID member) {
        teamCollection.updateOne(Filters.eq("_id", teamId), new Document("$pull", new Document("members", member.toString())));
    }


    public void close() {
        mongoClient.close();
    }
}
