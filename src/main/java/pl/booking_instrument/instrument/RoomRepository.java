package pl.booking_instrument.instrument;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.booking_instrument.instrument.model.InstrumentEntity;
import pl.booking_instrument.instrument.model.InstrumentTypeEnum;

import java.util.List;

public interface RoomRepository extends JpaRepository<InstrumentEntity, Long> {

    //List<InstrumentEntity> findAllByRoomStandardAndMaxPeopleAndNumberOfBeds(InstrumentTypeEnum instrumentTypeEnum, Integer maxPeople, Integer numberOfBeds);
    //List<InstrumentEntity> findAllByMaxPeopleAndNumberOfBeds(Integer maxPeople, Integer numberOfBeds);
    List<InstrumentEntity> findAllByInstrumentType(InstrumentTypeEnum instrumentTypeEnum);
    //List<InstrumentEntity> findAllByMaxPeople(Integer maxPeople);
    //List<InstrumentEntity> findAllByNumberOfBeds(Integer price);
}
