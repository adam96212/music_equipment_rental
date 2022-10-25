package pl.booking_room.booking.dto;

import lombok.Builder;
import lombok.Data;
import pl.booking_room.booking.model.BookingEntity;
import pl.booking_room.room.model.RoomEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Data
@Builder
public class BookingDto {

    private Long id;
    private UUID code;
    private Long userId;
    private String rooms;
    private LocalDate startDate;
    private LocalDate endDate;

    public static BookingDto create(BookingEntity bookingEntity){
        return BookingDto.builder()
                .id(bookingEntity.getId())
                .code(bookingEntity.getBookingCode())
                .userId(bookingEntity.getUser().getId())
                .rooms(roomsToString(bookingEntity.getRooms()))
                .startDate(bookingEntity.getStartDate())
                .endDate(bookingEntity.getEndDate())
                .build();

    }

    private static String roomsToString(List<RoomEntity> rooms){
        StringJoiner stringJoiner = new StringJoiner(", ");
        rooms.forEach(booking -> stringJoiner.add(booking.getRoomStandard() + " " + booking.getMaxPeople() + " " + booking.getNumberOfBeds() + " " + booking.getCapacityRoom()));
        return stringJoiner.toString();
    }

}
