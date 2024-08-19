package de.janoschbl.teamsmp.MongoAddon;

import org.bson.Document;

public class Config {
    private Integer id;
    private int worldBorderBlocks;
    private int blockPerPlayer;
    private int blockPerPlayerDeath;

    public Config(int worldBorderBlocks, int blockPerPlayer, int blockPerPlayerDeath) {
        this.worldBorderBlocks = worldBorderBlocks;
        this.blockPerPlayer = blockPerPlayer;
        this.blockPerPlayerDeath = blockPerPlayerDeath;
    }

    public Integer getId() {
        return id;
    }

    public int getWorldBorderBlocks() {
        return worldBorderBlocks;
    }


    public int getBlockPerPlayer() {
        return blockPerPlayer;
    }

    public int getBlockPerPlayerDeath() {
        return blockPerPlayerDeath;
    }

    public Document toDocument() {
        return new Document("worldBorderBlocks", worldBorderBlocks)
                .append("_id", 2104)
                .append("blockPerPlayer", blockPerPlayer)
                .append("blockPerPlayerDeath", blockPerPlayerDeath);
    }

    public static Config fromDocument(Document document) {
        Config config = new Config(
                document.getInteger("worldBorderBlocks"),
                document.getInteger("blockPerPlayer"),
                document.getInteger("blockPerPlayerDeath")
        );

        config.id = document.getInteger("_id");
        return config;
    }
}
