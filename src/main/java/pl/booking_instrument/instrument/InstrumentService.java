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
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    @Autowired
    public InstrumentService(InstrumentRepository instrumentRepository){
        this.instrumentRepository = instrumentRepository;
    }

    public InstrumentDto addInstrument(CreateInstrumentDto createInstrumentDto) {
        InstrumentEntity instrument = new InstrumentEntity();
        instrument.setInstrumentName(createInstrumentDto.getInstrumentName());
        instrument.setInstrumentType(InstrumentTypeEnum.valueOf(createInstrumentDto.getInstrumentType()));
        instrument.setColour(createInstrumentDto.getColour());
        instrument.setPrice(createInstrumentDto.getPrice());

        instrumentRepository.save(instrument);
        return InstrumentDto.create(instrument);
    }

    public Page<InstrumentDto> getInstruments(Pageable pageable){
        List<InstrumentEntity> instruments = instrumentRepository.findAll();
        int pageS = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startIt = currentPage * pageS;
        List<InstrumentEntity> result;

        if (instruments.size() < startIt){
            result = List.of();
        }
        else{
            int toIndex = Math.min(startIt + pageS, instruments.size());
            result = instruments.subList(startIt,toIndex);
        }

        List<InstrumentDto> collect = result.stream()
                .map(InstrumentDto::create)
                .collect(Collectors.toUnmodifiableList());
        return new PageImpl<>(collect, PageRequest.of(currentPage, pageS), instruments.size());
    }

    public List<InstrumentDto> searchInstruments(InstrumentSearchRequest request){
        List<InstrumentEntity> instruments = obtainInstrumentsByInstrumentType(request);

        instruments = filterByAvailability(request, instruments);

        return instruments.stream()
                .map(InstrumentDto::create)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<InstrumentEntity> filterByAvailability(InstrumentSearchRequest request, List<InstrumentEntity> instruments) {
        LocalDate from = request.getStart() != null && !request.getStart().isBlank() ? LocalDate.parse(request.getStart()) : null;
        LocalDate to = request.getEnd() != null && !request.getEnd().isBlank() ? LocalDate.parse(request.getEnd()) : null;

        if(from != null && to != null) {
            return instruments.stream().filter(instrument -> isInstrumentAvailable(instrument.getBookings(), from, to)).collect(Collectors.toUnmodifiableList());
        }

        return instruments;
    }

    private List<InstrumentEntity> obtainInstrumentsByInstrumentType(InstrumentSearchRequest request) {
        List<InstrumentEntity> instruments;
        InstrumentTypeEnum instrumentTypeEnum = request.getInstrumentType() != null && !request.getInstrumentType().isBlank() ? InstrumentTypeEnum.valueOf(request.getInstrumentType()) : null;

        if (instrumentTypeEnum != null) {
            instruments = instrumentRepository.findAllByInstrumentType(instrumentTypeEnum);
        }
        else {
            instruments = instrumentRepository.findAll();
        }
        return instruments;

    }

    private boolean isInstrumentAvailable(List<BookingEntity> bookings, LocalDate from, LocalDate to)
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


    public void deleteInstrument(Long id) {
        if(instrumentRepository.existsById(id)) {
            instrumentRepository.deleteById(id);
        }
    }

    @PostConstruct
    public void init(){
        addInstrument(CreateInstrumentDto.builder().instrumentName("Electric piano").instrumentType("KEYBOARD").colour("White").price(2).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Cello").instrumentType("STRING").colour("Red").price(3).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Clarinet").instrumentType("WIND").colour("Yellow").price(2).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Violin").instrumentType("STRING").colour("Blue").price(3).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Oboe").instrumentType("WIND").colour("White").price(1).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Saxophone").instrumentType("WIND").colour("Blue").price(3).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Violin").instrumentType("STRING").colour("Blue").price(4).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Accordion").instrumentType("KEYBOARD").colour("Yellow").price(4).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Acoustic guitar").instrumentType("STRING").colour("Red").price(1).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Trumpet").instrumentType("WIND").colour("White").price(3).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Accordion").instrumentType("KEYBOARD").colour("White").price(2).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Clarinet").instrumentType("WIND").colour("Yellow").price(6).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Trumpet").instrumentType("WIND").colour("Black").price(8).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Electric guitar").instrumentType("STRING").colour("Orange").price(1).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Clarinet").instrumentType("WIND").colour("White").price(1).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Electric piano").instrumentType("KEYBOARD").colour("Black").price(1).build());
        addInstrument(CreateInstrumentDto.builder().instrumentName("Electric guitar").instrumentType("STRING").colour("Yellow").price(2).build());
    }
}
