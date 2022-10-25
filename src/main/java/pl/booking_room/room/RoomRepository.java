package pl.booking_room.room;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.booking_room.room.model.RoomEntity;
import pl.booking_room.room.model.RoomStandardEnum;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    List<RoomEntity> findAllByRoomStandardAndMaxPeopleAndNumberOfBeds(RoomStandardEnum roomStandardEnum, Integer maxPeople, Integer numberOfBeds);
    List<RoomEntity> findAllByMaxPeopleAndNumberOfBeds(Integer maxPeople, Integer numberOfBeds);
    List<RoomEntity> findAllByRoomStandard(RoomStandardEnum roomStandardEnum);
    List<RoomEntity> findAllByMaxPeople(Integer maxPeople);
    List<RoomEntity> findAllByNumberOfBeds(Integer numberOfBeds);
}
