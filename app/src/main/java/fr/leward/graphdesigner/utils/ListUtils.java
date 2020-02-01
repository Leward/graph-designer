package fr.leward.graphdesigner.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    /**
     *
     * @param syncFrom the source list for the synchronization
     * @param syncTo the destination list for the synchronization
     */
    public static void syncLists(List syncFrom, List syncTo) {
        List addList = new ArrayList<>();
        List removeList = new ArrayList<>();

        for(Object item : syncFrom) {
            if(!syncTo.contains(item)) {
                addList.add(item);
            }
        }

        for(Object item : syncTo) {
            if(!syncFrom.contains(item)) {
                removeList.add(item);
            }
        }

        syncTo.addAll(addList);
        syncTo.removeAll(removeList);
    }

}
