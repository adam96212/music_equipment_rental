package pl.booking_room.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.booking_room.booking.dto.BookingDto;
import pl.booking_room.booking.dto.CreateBookingDto;
import pl.booking_room.booking.model.BookingEntity;
import pl.booking_room.room.RoomRepository;
import pl.booking_room.room.model.RoomEntity;
import pl.booking_room.user.UserRepository;
import pl.booking_room.user.model.UserEntity;

import javax.annotation.PostConstruct;
import javax.persistence.metamodel.ListAttribute;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public void createBooking(CreateBookingDto createBookingDto) {
        BookingEntity booking = new BookingEntity();
        Optional<UserEntity> userEntityById = userRepository.findUserEntityById(createBookingDto.getUserId());

        if (createBookingDto.getRoomIds().isEmpty() || userEntityById.isEmpty()) {
            throw new RuntimeException();
        }

        UserEntity user = userEntityById.get();
        booking.setUser(user);

        List<RoomEntity> rooms = createBookingDto.getRoomIds().stream()
                .map(roomRepository::findById).map(Optional::get)
                .collect(Collectors.toUnmodifiableList());

        booking.setBookingCode(UUID.randomUUID());
        booking.setRooms(rooms);
        booking.setStartDate(LocalDate.parse(createBookingDto.getStartDate()));
        booking.setEndDate(LocalDate.parse((createBookingDto.getEndDate())));

        bookingRepository.save(booking);

    }

    public List<BookingDto> getBookingsForUserById(Long id) {
        return bookingRepository.findAllByUserId(id).stream()
                .map(BookingDto::create)
                .collect(Collectors.toUnmodifiableList());
    }

    public Page<BookingDto> getBookingsPage(Pageable pageable) {
        List<BookingEntity> bookings = bookingRepository.findAll();

        int pageS = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startIt = currentPage * pageS;
        List<BookingEntity> result;

        if(bookings.size() < startIt) {
            result = List.of();
        }
        else {
            int toIndex = Math.min(startIt + pageS, bookings.size());
            result = bookings.subList(startIt, toIndex);
        }

        List<BookingDto> collect = result.stream()
                .map(BookingDto::create)
                .collect(Collectors.toUnmodifiableList());

        return new PageImpl<>(collect, PageRequest.of(currentPage, pageS), bookings.size());

    }

    public void deleteBooking(Long id)
    {
        if (bookingRepository.existsById(id))
        {
            bookingRepository.deleteById(id);
        }
    }

    @PostConstruct
    public void init() {
        createBooking(CreateBookingDto.builder().userId(2L).roomIds(List.of(1L)).startDate("2022-01-24").endDate("2022-01-31").build());
        createBooking(CreateBookingDto.builder().userId(2L).roomIds(List.of(2L)).startDate("2022-02-01").endDate("2022-02-21").build());
        createBooking(CreateBookingDto.builder().userId(4L).roomIds(List.of(4L)).startDate("2022-01-24").endDate("2022-01-31").build());
        createBooking(CreateBookingDto.builder().userId(3L).roomIds(List.of(5L)).startDate("2022-02-15").endDate("2022-02-21").build());
        createBooking(CreateBookingDto.builder().userId(3L).roomIds(List.of(3L)).startDate("2022-01-24").endDate("2022-01-31").build());
        createBooking(CreateBookingDto.builder().userId(5L).roomIds(List.of(5L)).startDate("2022-03-01").endDate("2022-03-21").build());
        createBooking(CreateBookingDto.builder().userId(6L).roomIds(List.of(6L)).startDate("2022-01-24").endDate("2022-01-31").build());
        createBooking(CreateBookingDto.builder().userId(6L).roomIds(List.of(7L)).startDate("2022-05-12").endDate("2022-05-21").build());
        createBooking(CreateBookingDto.builder().userId(6L).roomIds(List.of(8L)).startDate("2022-10-15").endDate("2022-06-23").build());
        createBooking(CreateBookingDto.builder().userId(6L).roomIds(List.of(9L)).startDate("2022-02-01").endDate("2022-02-21").build());
    }

}