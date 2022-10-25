package pl.booking_room;

import lombok.Data;
import org.springframework.stereotype.Component;
import pl.booking_room.room.dto.RoomDto;

import java.util.List;

@Data
@Component("singleton")
public class Cache {
    private List<RoomDto> rooms;
}
