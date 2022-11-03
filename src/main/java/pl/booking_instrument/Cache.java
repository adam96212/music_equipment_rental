package pl.booking_instrument;

import lombok.Data;
import org.springframework.stereotype.Component;
import pl.booking_instrument.instrument.dto.InstrumentDto;

import java.util.List;

@Data
@Component("singleton")
public class Cache {
    private List<InstrumentDto> instruments;
}
