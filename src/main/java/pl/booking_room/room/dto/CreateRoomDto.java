package pl.booking_room.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomDto {

    private int capacityRoom;
    private String roomStandard;
    private int maxPeople;
    private int numberOfBeds;
}
