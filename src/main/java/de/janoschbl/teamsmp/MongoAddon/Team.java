package de.janoschbl.teamsmp.MongoAddon;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {
    private ObjectId id;
    private String name;
    private String tag;
    private UUID leader;
    private List<UUID> members;

    public Team(String name, String tag, UUID leader) {
        this.name = name;
        this.tag = tag;
        this.leader = leader;
        this.members = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public UUID getLeader() {
        return leader;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID member) {
        if (!members.contains(member)) {
            members.add(member);
        }
    }

    public void removeMember(UUID member) {
        members.remove(member);
    }

    public Document toDocument() {
        return new Document("name", name)
                .append("tag", tag)
                .append("leader", leader.toString())
                .append("members", members.stream().map(UUID::toString).toList());
    }

    public static Team fromDocument(Document document) {
        Team team = new Team(
                document.getString("name"),
                document.getString("tag"),
                UUID.fromString(document.getString("leader"))
        );
        List<String> memberStrings = (List<String>) document.get("members");
        for (String member : memberStrings) {
            team.addMember(UUID.fromString(member));
        }
        return team;
    }
}
