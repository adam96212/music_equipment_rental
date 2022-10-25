package pl.booking_room.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomSearchRequest {
    private String roomStandard;
    private String start;
    private String end;
}
