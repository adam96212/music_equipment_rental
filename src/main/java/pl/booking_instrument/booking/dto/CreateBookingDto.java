package pl.booking_instrument.booking.dto;

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
    private List<Long> instrumentIds;
    private String startDate;
    private String endDate;


}
