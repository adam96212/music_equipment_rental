package pl.booking_room.booking.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateBookingDto {

    private Long userId;
    private List<Long> roomIds;
    private String startDate;
    private String endDate;


}
