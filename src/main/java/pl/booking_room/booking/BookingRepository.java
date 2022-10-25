package pl.booking_room.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.booking_room.booking.model.BookingEntity;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByUserId(Long id);
}
