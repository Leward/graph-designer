package fr.leward.graphdesigner.event.bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Paul-Julien on 08/02/2015.
 */
public class EventStream<T extends Event> {

    private static final Logger log = LoggerFactory.getLogger(EventStream.class);
    private Collection<EventConsumer<T>> consumers = new ConcurrentLinkedQueue<EventConsumer<T>>();

    public void publish(T event) {
        log.debug("Published event of type " + event.getClass().getName());
        for(EventConsumer<T> consumer : consumers) {
            consumer.consume(event);
        }
    }

    public void subscribe(EventConsumer<T> consumer) {
        consumers.add(consumer);
    }

    public void unsubscribe(EventConsumer<T> consumer) {
        consumers.remove(consumer);
    }

}
