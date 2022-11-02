package pl.booking_instrument.instrument.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentSearchRequest {
    private String instrumentType;
    private String start;
    private String end;
}
