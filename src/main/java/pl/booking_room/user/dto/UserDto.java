package pl.booking_room.user.dto;


import lombok.Builder;
import lombok.Data;
import pl.booking_room.user.model.UserEntity;

@Data
@Builder
public class UserDto {

    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;

    public static UserDto create(UserEntity userEntity){
        return UserDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .build();
    }
}
