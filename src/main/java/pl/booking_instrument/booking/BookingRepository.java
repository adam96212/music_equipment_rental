package pl.booking_instrument.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.booking_instrument.booking.model.BookingEntity;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByUserId(Long id);
}
