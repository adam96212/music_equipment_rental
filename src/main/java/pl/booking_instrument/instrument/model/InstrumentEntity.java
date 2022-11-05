package pl.booking_instrument.instrument.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.booking_instrument.booking.model.BookingEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instrument_seq")
    private Long id;

    private String instrumentName;

    private InstrumentTypeEnum instrumentType;

    private String colour;

    private int price;

    @ManyToMany(mappedBy = "instruments", cascade = CascadeType.ALL)
    private List<BookingEntity> bookings;


}
