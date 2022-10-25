package pl.booking_room.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.booking_room.booking.model.BookingEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private UserRoleEnum role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BookingEntity> bookings;

    private boolean isAccountNonExpired;

    private boolean isEnabled;

    private boolean isAccountLocked;

    private boolean isCredentialsNonExpired;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return List.of(authority);
    }

    @Override
    public boolean isAccountNonExpired(){
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked(){
        return isAccountLocked;
    }

    @Override
    public boolean isEnabled(){
        return isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return isCredentialsNonExpired;
    }

}
