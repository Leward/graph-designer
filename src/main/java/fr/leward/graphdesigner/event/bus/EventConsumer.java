package fr.leward.graphdesigner.event.bus;

@FunctionalInterface
public interface EventConsumer<T extends Event> {
    void consume(T event);
}
