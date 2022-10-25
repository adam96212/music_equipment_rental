package pl.booking_room.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.booking_room.user.model.UserEntity;

import java.util.Optional;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public ApplicationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findUserEntityByUsername(username);
        if (userEntityOptional.isPresent()){
            return userEntityOptional.get();
        }
        else
        {
            throw new UsernameNotFoundException("User with username: " + username + " does not exist");
        }
    }
}
