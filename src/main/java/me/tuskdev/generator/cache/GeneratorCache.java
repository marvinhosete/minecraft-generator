package me.tuskdev.generator.cache;

import com.google.common.collect.ImmutableSet;
import me.tuskdev.generator.model.Generator;
import me.tuskdev.generator.util.Coordinates;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class GeneratorCache {

    private final Map<Coordinates, Generator> MACHINES = new HashMap<>();

    public void insert(Generator generator) {
        MACHINES.put(generator.getCoordinates(), generator);
    }

    public Generator select(Coordinates coordinates) {
        return MACHINES.get(coordinates);
    }

    public Generator selectIf(Predicate<Coordinates> predicate) {
        for (Coordinates coordinates : MACHINES.keySet()) {
            if (predicate.test(coordinates)) return MACHINES.get(coordinates);
        }

        return null;
    }

    public void delete(Coordinates coordinates) {
        MACHINES.remove(coordinates);
    }

    public void deleteIfAnd(Predicate<Coordinates> predicate, Consumer<Generator> consumer) {
        Iterator<Map.Entry<Coordinates, Generator>> iterator = MACHINES.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Coordinates, Generator> entry = iterator.next();
            if (predicate.test(entry.getKey())) {
                consumer.accept(entry.getValue());
                iterator.remove();
            }
        }
    }

    public void deleteAnd(Consumer<Generator> consumer) {
        MACHINES.values().forEach(consumer);
        MACHINES.clear();
    }

    public Set<Generator> getAll() {
        return Collections.synchronizedSet(ImmutableSet.copyOf(MACHINES.values()));
    }

}
