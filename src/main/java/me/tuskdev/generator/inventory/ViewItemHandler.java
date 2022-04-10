package me.tuskdev.generator.inventory;

@FunctionalInterface
public interface ViewItemHandler {

    void handle(ViewSlotContext context);

}
