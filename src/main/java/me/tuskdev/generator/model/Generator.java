package me.tuskdev.generator.model;

import io.github.sasuked.bukkitsql.query.QueryAdapter;
import io.github.sasuked.bukkitsql.query.QueryResponse;
import me.tuskdev.generator.hologram.Hologram;
import me.tuskdev.generator.util.Coordinates;

import java.util.UUID;

public class Generator implements QueryAdapter {

    private UUID owner;
    private Coordinates coordinates;
    private String type;
    private int level = 1, items = 0;
    private long time = System.currentTimeMillis();

    // HOLOGRAM
    private Hologram hologram;

    public Generator(UUID owner, Coordinates coordinates, String type) {
        this.owner = owner;
        this.coordinates = coordinates;
        this.type = type;
    }

    public Generator() {}

    public UUID getOwner() {
        return owner;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public QueryAdapter accept(QueryResponse response) {
        this.owner = UUID.fromString(response.get("owner"));
        this.coordinates = new Coordinates(response.get("world_name"), response.get("block_x"), response.get("block_y"), response.get("block_z"));
        this.type = response.get("type");
        this.level = response.get("level");
        this.items = response.get("items");
        return this;
    }
}
