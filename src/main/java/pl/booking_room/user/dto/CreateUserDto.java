package pl.booking_room.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto
{
    private String email;
    private String username;
    private String password;
    private String repeatedPassword;
    private String firstname;
    private String lastName;
}
