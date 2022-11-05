package pl.booking_instrument.instrument.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstrumentDto {

    private String instrumentName;
    private String instrumentType;
    private String colour;
    private int price;
}
