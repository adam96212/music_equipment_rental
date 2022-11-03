package pl.booking_instrument.booking.dto;

import lombok.Builder;
import lombok.Data;
import pl.booking_instrument.booking.model.BookingEntity;
import pl.booking_instrument.instrument.model.InstrumentEntity;

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
    private String instruments;
    private LocalDate startDate;
    private LocalDate endDate;

    public static BookingDto create(BookingEntity bookingEntity){
        return BookingDto.builder()
                .id(bookingEntity.getId())
                .code(bookingEntity.getBookingCode())
                .userId(bookingEntity.getUser().getId())
                .instruments(instrumentsToString(bookingEntity.getInstruments()))
                .startDate(bookingEntity.getStartDate())
                .endDate(bookingEntity.getEndDate())
                .build();

    }

    private static String instrumentsToString(List<InstrumentEntity> instruments){
        StringJoiner stringJoiner = new StringJoiner(", ");
        instruments.forEach(booking -> stringJoiner.add(booking.getInstrumentWeight() + " " + booking.getInstrumentType() + " " + booking.getColour() + " " + booking.getPrice()));
        return stringJoiner.toString();
    }

}
