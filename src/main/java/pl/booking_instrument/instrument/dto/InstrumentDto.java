package pl.booking_instrument.instrument.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.booking_instrument.instrument.model.InstrumentEntity;
import pl.booking_instrument.instrument.model.InstrumentTypeEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentDto {

    private Long id;
    private int instrumentWeight;
    private InstrumentTypeEnum instrumentTypeEnum;
    private int colour;
    private int price;

    public static InstrumentDto create(InstrumentEntity instrumentEntity){

        return InstrumentDto.builder()
                .id(instrumentEntity.getId())
                .instrumentWeight(instrumentEntity.getInstrumentWeight())
                .instrumentTypeEnum(instrumentEntity.getInstrumentType())
                .colour(instrumentEntity.getColour())
                .price(instrumentEntity.getPrice())
                .build();

    }



}
