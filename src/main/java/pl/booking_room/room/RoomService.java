package pl.booking_room.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.booking_room.booking.model.BookingEntity;
import pl.booking_room.room.dto.CreateRoomDto;
import pl.booking_room.room.dto.RoomDto;
import pl.booking_room.room.dto.RoomSearchRequest;
import pl.booking_room.room.model.RoomEntity;
import pl.booking_room.room.model.RoomStandardEnum;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }

    public RoomDto addRoom(CreateRoomDto createRoomDto) {
        RoomEntity room = new RoomEntity();
        room.setCapacityRoom(createRoomDto.getCapacityRoom());
        room.setRoomStandard(RoomStandardEnum.valueOf(createRoomDto.getRoomStandard()));
        room.setMaxPeople(createRoomDto.getMaxPeople());
        room.setNumberOfBeds(createRoomDto.getNumberOfBeds());

        roomRepository.save(room);
        return RoomDto.create(room);
    }

    public Page<RoomDto> getRooms(Pageable pageable){
        List<RoomEntity> rooms = roomRepository.findAll();
        int pageS = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startIt = currentPage * pageS;
        List<RoomEntity> result;

        if (rooms.size() < startIt){
            result = List.of();
        }
        else{
            int toIndex = Math.min(startIt + pageS, rooms.size());
            result = rooms.subList(startIt,toIndex);
        }

        List<RoomDto> collect = result.stream()
                .map(RoomDto::create)
                .collect(Collectors.toUnmodifiableList());
        return new PageImpl<>(collect, PageRequest.of(currentPage, pageS), rooms.size());
    }

    public List<RoomDto> searchRooms(RoomSearchRequest request){
        List<RoomEntity> rooms = obtainRoomsByRoomStandard(request);

        rooms = filterByAvailability(request, rooms);

        return rooms.stream()
                .map(RoomDto::create)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<RoomEntity> filterByAvailability(RoomSearchRequest request, List<RoomEntity> rooms) {
        LocalDate from = request.getStart() != null && !request.getStart().isBlank() ? LocalDate.parse(request.getStart()) : null;
        LocalDate to = request.getEnd() != null && !request.getEnd().isBlank() ? LocalDate.parse(request.getEnd()) : null;

        if(from != null && to != null) {
            return rooms.stream().filter(room -> isRoomAvailable(room.getBookings(), from, to)).collect(Collectors.toUnmodifiableList());
        }

        return rooms;
    }

    private List<RoomEntity> obtainRoomsByRoomStandard(RoomSearchRequest request) {
        List<RoomEntity> rooms;
        RoomStandardEnum roomStandardEnum = request.getRoomStandard() != null && !request.getRoomStandard().isBlank() ? RoomStandardEnum.valueOf(request.getRoomStandard()) : null;

        if (roomStandardEnum != null) {
            rooms = roomRepository.findAllByRoomStandard(roomStandardEnum);
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
        addRoom(CreateRoomDto.builder().capacityRoom(35).roomStandard("DELUXE").maxPeople(3).numberOfBeds(2).build());
        addRoom(CreateRoomDto.builder().capacityRoom(45).roomStandard("APARTMENT").maxPeople(6).numberOfBeds(3).build());
        addRoom(CreateRoomDto.builder().capacityRoom(24).roomStandard("STANDARD").maxPeople(4).numberOfBeds(2).build());
        addRoom(CreateRoomDto.builder().capacityRoom(20).roomStandard("STANDARD").maxPeople(3).numberOfBeds(3).build());
        addRoom(CreateRoomDto.builder().capacityRoom(40).roomStandard("DELUXE").maxPeople(2).numberOfBeds(1).build());
        addRoom(CreateRoomDto.builder().capacityRoom(60).roomStandard("STANDARD").maxPeople(5).numberOfBeds(3).build());
        addRoom(CreateRoomDto.builder().capacityRoom(70).roomStandard("APARTMENT").maxPeople(6).numberOfBeds(4).build());
        addRoom(CreateRoomDto.builder().capacityRoom(80).roomStandard("APARTMENT").maxPeople(8).numberOfBeds(4).build());
        addRoom(CreateRoomDto.builder().capacityRoom(15).roomStandard("STANDARD").maxPeople(1).numberOfBeds(1).build());
        addRoom(CreateRoomDto.builder().capacityRoom(66).roomStandard("DELUXE").maxPeople(3).numberOfBeds(3).build());
        addRoom(CreateRoomDto.builder().capacityRoom(77).roomStandard("DELUXE").maxPeople(4).numberOfBeds(2).build());
        addRoom(CreateRoomDto.builder().capacityRoom(98).roomStandard("APARTMENT").maxPeople(8).numberOfBeds(6).build());
        addRoom(CreateRoomDto.builder().capacityRoom(105).roomStandard("APARTMENT").maxPeople(10).numberOfBeds(8).build());
        addRoom(CreateRoomDto.builder().capacityRoom(15).roomStandard("STANDARD").maxPeople(1).numberOfBeds(1).build());
        addRoom(CreateRoomDto.builder().capacityRoom(35).roomStandard("STANDARD").maxPeople(2).numberOfBeds(1).build());
        addRoom(CreateRoomDto.builder().capacityRoom(25).roomStandard("STANDARD").maxPeople(2).numberOfBeds(1).build());
        addRoom(CreateRoomDto.builder().capacityRoom(40).roomStandard("STANDARD").maxPeople(3).numberOfBeds(2).build());
    }

}
