package me.tuskdev.generator.controller;

import io.github.sasuked.bukkitsql.provider.AsynchronousConnectionProvider;
import io.github.sasuked.bukkitsql.provider.mysql.MysqlConnectionProvider;
import me.tuskdev.generator.model.Generator;
import me.tuskdev.generator.util.Coordinates;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;
import java.util.concurrent.Executors;

public class GeneratorController {

    private static final String QUERY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `generator` (`owner` CHAR(36) NOT NULL, `type` CHAR(16) NOT NULL, `level` INT NOT NULL, `items` INT DEFAULT 0, `amount` INT NOT NULL, `world_name` CHAR(16) NOT NULL, `block_x` INT NOT NULL, `block_y` INT NOT NULL, `block_z` INT NOT NULL, `chunk_x` INT NOT NULL, `chunk_z` INT NOT NULL);";
    private static final String QUERY_INSERT_GENERATOR = "INSERT INTO `generator`(`owner`, `type`, `level`, `amount`, `world_name`, `block_x`, `block_y`, `block_z`, `chunk_x`, `chunk_z`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String QUERY_UPDATE_AMOUNT = "UPDATE `generator` SET `amount` = ?  WHERE `world_name` = ? AND `block_x` = ? AND `block_y` = ? AND `block_z` = ?;";
    private static final String QUERY_UPDATE_ITEMS = "UPDATE `generator` SET `items` = ?  WHERE `world_name` = ? AND `block_x` = ? AND `block_y` = ? AND `block_z` = ?;";
    private static final String QUERY_UPDATE_LEVEL = "UPDATE `generator` SET `level` = ?  WHERE `world_name` = ? AND `block_x` = ? AND `block_y` = ? AND `block_z` = ?;";
    private static final String QUERY_SELECT_GENERATORS = "SELECT * FROM `generator` WHERE `world_name` = ? AND `chunk_x` = ? AND `chunk_z` = ?;";
    private static final String QUERY_DELETE_GENERATOR = "DELETE FROM `generator` WHERE `world_name` = ? AND `block_x` = ? AND `block_y` = ? AND `block_z` = ?;";

    private final AsynchronousConnectionProvider provider;

    public GeneratorController(ConfigurationSection configurationSection) {
        this.provider = new MysqlConnectionProvider(
                configurationSection.getString("host"),
                configurationSection.getInt("port"),
                configurationSection.getString("database"),
                configurationSection.getString("username"),
                configurationSection.getString("password")
        ).withAsyncExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        provider.executeAsync(QUERY_CREATE_TABLE);
    }

    public void insert(Generator generator) {
        Coordinates coordinates = generator.getCoordinates();

        provider.executeAsync(QUERY_INSERT_GENERATOR,
                generator.getOwner().toString(),
                generator.getType(),
                generator.getLevel(),
                generator.getAmount(),
                coordinates.getWorldName(),
                coordinates.getX(),
                coordinates.getY(),
                coordinates.getZ(),
                coordinates.getX() >> 4,
                coordinates.getZ() >> 4
        );
    }

    public Set<Generator> select(String worldName, int chunkX, int chunkZ) {
        return provider.selectAsync(QUERY_SELECT_GENERATORS, Generator.class, worldName, chunkX, chunkZ).join();
    }

    public void updateAmount(int amount, Coordinates coordinates) {
        provider.executeUpdate(QUERY_UPDATE_AMOUNT, amount, coordinates.getWorldName(), coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    public void updateItems(int items, Coordinates coordinates) {
        provider.executeUpdate(QUERY_UPDATE_ITEMS, items, coordinates.getWorldName(), coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    public void updateLevel(int level, Coordinates coordinates) {
        provider.executeUpdate(QUERY_UPDATE_LEVEL, level, coordinates.getWorldName(), coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    public void delete(Coordinates coordinates) {
        provider.executeAsync(QUERY_DELETE_GENERATOR, coordinates.getWorldName(), coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

}
