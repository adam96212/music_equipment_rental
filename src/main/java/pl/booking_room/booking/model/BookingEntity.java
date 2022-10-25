package pl.booking_room.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.booking_room.room.model.RoomEntity;
import pl.booking_room.user.model.UserEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_seq")
    private Long id;

    private UUID bookingCode;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToMany
    private List<RoomEntity> rooms;

    @ManyToOne
    private UserEntity user;
}
