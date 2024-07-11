package de.janoschbl.teamsmp.MongoAddon;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class Team {
    private ObjectId id;
    private String name;
    private String tag;
    private UUID leader;
    private String color;
    private Integer hearts;
    private List<UUID> members;

    public Team(String name, String tag, UUID leader, String color, Integer hearts) {
        this.name = name;
        this.tag = tag;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.color = color;
        this.hearts = hearts;
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

    public String getColor() {
        return color;
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

    public Integer getHearts() {
        return hearts;
    }

    public void removeMember(UUID member) {
        members.remove(member);
    }

    public Document toDocument() {
        return new Document("name", name)
                .append("tag", tag)
                .append("color", color)
                .append("hearts", hearts)
                .append("leader", leader.toString())
                .append("members", members.stream().map(UUID::toString).toList());
    }

    public static Team fromDocument(Document document) {
        Team team = new Team(
                document.getString("name"),
                document.getString("tag"),
                UUID.fromString(document.getString("leader")),
                document.getString("color"),
                document.getInteger("hearts")
        );

        team.id = document.getObjectId("_id");

        List<String> memberStrings = (List<String>) document.get("members");
        for (String member : memberStrings) {
            team.addMember(UUID.fromString(member));
        }
        return team;
    }
}