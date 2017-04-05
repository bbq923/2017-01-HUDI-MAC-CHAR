package com.mapia.domain;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Jbee on 2017. 4. 5..
 */
@Component
public class Rooms {
    private volatile Map<Long, Room> rooms = new ConcurrentHashMap<>();
    private final AtomicLong roomIdentifier = new AtomicLong();

    public Collection<Room> getRooms() {
        return rooms.values();
    }

    public Room getRoom(long id) {
        return rooms.get(id);
    }

    public long createRoom(String title) {
        long roomId = roomIdentifier.incrementAndGet();
        rooms.put(roomId, new Room(roomId, title));
        return roomId;
    }

    public void delRoom(long id) {
        rooms.remove(id);
    }

    public boolean isExistRoom(long id) {
        Set<Long> keySet = rooms.keySet();
        return keySet.contains(id);
    }
}
