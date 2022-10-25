package pl.booking_room.room.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.booking_room.booking.model.BookingEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_seq")
    private Long id;

    private int capacityRoom;

    private RoomStandardEnum roomStandard;

    private int maxPeople;

    private int numberOfBeds;

    @ManyToMany(mappedBy = "rooms", cascade = CascadeType.ALL)
    private List<BookingEntity> bookings;


}
