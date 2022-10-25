package pl.booking_room.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.booking_room.room.model.RoomEntity;
import pl.booking_room.room.model.RoomStandardEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

    private Long id;
    private int capacityRoom;
    private RoomStandardEnum roomStandardEnum;
    private int maxPeople;
    private int numberOfBeds;

    public static RoomDto create(RoomEntity roomEntity){

        return RoomDto.builder()
                .id(roomEntity.getId())
                .capacityRoom(roomEntity.getCapacityRoom())
                .roomStandardEnum(roomEntity.getRoomStandard())
                .maxPeople(roomEntity.getMaxPeople())
                .numberOfBeds(roomEntity.getNumberOfBeds())
                .build();

    }



}
