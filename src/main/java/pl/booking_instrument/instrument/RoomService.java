package pl.booking_instrument.instrument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.booking_instrument.booking.model.BookingEntity;
import pl.booking_instrument.instrument.dto.CreateInstrumentDto;
import pl.booking_instrument.instrument.dto.InstrumentDto;
import pl.booking_instrument.instrument.dto.InstrumentSearchRequest;
import pl.booking_instrument.instrument.model.InstrumentTypeEnum;
import pl.booking_instrument.instrument.model.InstrumentEntity;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }

    public InstrumentDto addRoom(CreateInstrumentDto createInstrumentDto) {
        InstrumentEntity room = new InstrumentEntity();
        room.setInstrumentWeight(createInstrumentDto.getInstrumentWeight());
        room.setInstrumentType(InstrumentTypeEnum.valueOf(createInstrumentDto.getInstrumentType()));
        room.setColour(createInstrumentDto.getColour());
        room.setPrice(createInstrumentDto.getPrice());

        roomRepository.save(room);
        return InstrumentDto.create(room);
    }

    public Page<InstrumentDto> getRooms(Pageable pageable){
        List<InstrumentEntity> rooms = roomRepository.findAll();
        int pageS = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startIt = currentPage * pageS;
        List<InstrumentEntity> result;

        if (rooms.size() < startIt){
            result = List.of();
        }
        else{
            int toIndex = Math.min(startIt + pageS, rooms.size());
            result = rooms.subList(startIt,toIndex);
        }

        List<InstrumentDto> collect = result.stream()
                .map(InstrumentDto::create)
                .collect(Collectors.toUnmodifiableList());
        return new PageImpl<>(collect, PageRequest.of(currentPage, pageS), rooms.size());
    }

    public List<InstrumentDto> searchRooms(InstrumentSearchRequest request){
        List<InstrumentEntity> rooms = obtainRoomsByInstrumentType(request);

        rooms = filterByAvailability(request, rooms);

        return rooms.stream()
                .map(InstrumentDto::create)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<InstrumentEntity> filterByAvailability(InstrumentSearchRequest request, List<InstrumentEntity> rooms) {
        LocalDate from = request.getStart() != null && !request.getStart().isBlank() ? LocalDate.parse(request.getStart()) : null;
        LocalDate to = request.getEnd() != null && !request.getEnd().isBlank() ? LocalDate.parse(request.getEnd()) : null;

        if(from != null && to != null) {
            return rooms.stream().filter(room -> isRoomAvailable(room.getBookings(), from, to)).collect(Collectors.toUnmodifiableList());
        }

        return rooms;
    }

    private List<InstrumentEntity> obtainRoomsByInstrumentType(InstrumentSearchRequest request) {
        List<InstrumentEntity> rooms;
        InstrumentTypeEnum instrumentTypeEnum = request.getInstrumentType() != null && !request.getInstrumentType().isBlank() ? InstrumentTypeEnum.valueOf(request.getInstrumentType()) : null;

        if (instrumentTypeEnum != null) {
            rooms = roomRepository.findAllByInstrumentType(instrumentTypeEnum);
        }
        else {
            rooms = roomRepository.findAll();
        }
        return rooms;

    }

    private boolean isRoomAvailable(List<BookingEntity> bookings, LocalDate from, LocalDate to)
    {
        boolean isAvailable = true;
        for (BookingEntity booking : bookings)
        {
            if (isOverlapping(from, to, booking.getStartDate(), booking.getEndDate()))
            {
                isAvailable = false;
            }
        }
        return isAvailable;
    }

    private boolean isOverlapping(LocalDate start_1, LocalDate end_1, LocalDate start_2, LocalDate end_2)
    {
        return !start_1.isAfter(end_2) && !start_2.isAfter(end_1);
    }

    public void deleteRoom(Long id) {
        if(roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        }
    }

    @PostConstruct
    public void init(){
        addRoom(CreateInstrumentDto.builder().instrumentWeight(35).instrumentType("WIND").colour(3).price(2).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(45).instrumentType("WIND").colour(6).price(3).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(24).instrumentType("WIND").colour(4).price(2).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(20).instrumentType("WIND").colour(3).price(3).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(40).instrumentType("WIND").colour(2).price(1).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(60).instrumentType("WIND").colour(5).price(3).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(70).instrumentType("WIND").colour(6).price(4).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(80).instrumentType("WIND").colour(8).price(4).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(15).instrumentType("WIND").colour(1).price(1).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(66).instrumentType("WIND").colour(3).price(3).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(77).instrumentType("WIND").colour(4).price(2).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(98).instrumentType("WIND").colour(8).price(6).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(105).instrumentType("WIND").colour(10).price(8).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(15).instrumentType("WIND").colour(1).price(1).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(35).instrumentType("WIND").colour(2).price(1).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(25).instrumentType("WIND").colour(2).price(1).build());
        addRoom(CreateInstrumentDto.builder().instrumentWeight(40).instrumentType("WIND").colour(3).price(2).build());
    }

}
