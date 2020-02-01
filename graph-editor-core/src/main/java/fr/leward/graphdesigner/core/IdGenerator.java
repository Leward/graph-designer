package fr.leward.graphdesigner.core;

/**
 * Generates IDs which are unique as long as they originate from the same generator.
 */
public interface IdGenerator {
    /**
     * Generates the next ID
     * @return the next ID
     */
    long nextId();
}
