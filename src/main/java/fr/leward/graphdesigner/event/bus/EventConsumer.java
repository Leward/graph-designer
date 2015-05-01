package fr.leward.graphdesigner.event.bus;

/**
 * Created by Paul-Julien on 08/02/2015.
 */
public interface EventConsumer<T extends Event> {

    public void consume(T event);

}
