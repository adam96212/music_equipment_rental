package pl.booking_room.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.booking_room.user.dto.CreateUserDto;
import pl.booking_room.user.dto.UserDto;
import pl.booking_room.user.model.UserEntity;
import pl.booking_room.user.model.UserRoleEnum;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CreateUserDto createUser(CreateUserDto createUserDto){
        boolean emailIsCorrect = UserValidator.validateEmail(createUserDto.getEmail());
        boolean passwordIsSame = UserValidator.validPassword(createUserDto.getPassword(), createUserDto.getRepeatedPassword());

        if (!passwordIsSame){
            throw new UserException("Passwords are not the same");
        }

        if(!emailIsCorrect){
            throw new UserException("Wrong email");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(createUserDto.getUsername());
        userEntity.setEmail(createUserDto.getEmail());
        userEntity.setFirstName(createUserDto.getFirstname());
        userEntity.setLastName(createUserDto.getLastName());
        userEntity.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        userEntity.setRole(UserRoleEnum.REGULAR);
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setEnabled(true);

        userRepository.save(userEntity);

        return createUserDto;
    }

    public UserDto getUserById(Long id){
        Optional<UserEntity> userEntityOptional = userRepository.findUserEntityById(id);
        if (userEntityOptional.isPresent()){
            return UserDto.create(userEntityOptional.get());
        }
        else {
            throw new UserException("User does not exist");
        }
    }

    public UserEntity getUserByUsername(String username){
        if (username.equals("anonym")){
            return null;
        }

        Optional<UserEntity> userEntityOptional = userRepository.findUserEntityByUsername(username);
        if (userEntityOptional.isPresent()){
            return userEntityOptional.get();
        }
        else{
            throw new UserException("User does not exist");
        }
    }

    public Page<UserDto> getAllUsersPage(Pageable pageable){
        List<UserEntity> users = userRepository.findAll();
        int pageS = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startIt = currentPage * pageS;
        List<UserEntity> result;

        if (users.size() < startIt){
            result = List.of();
        }
        else{
            int toIndex = Math.min(startIt + pageS, users.size());
            result = users.subList(startIt, toIndex);
        }

        List<UserDto> collect = result.stream()
                .map(UserDto::create)
                .collect(Collectors.toUnmodifiableList());

        return new PageImpl<>(collect, PageRequest.of(currentPage, pageS), users.size());
    }

    private void createAdmin(CreateUserDto createUserDto){

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(createUserDto.getUsername());
        userEntity.setEmail(createUserDto.getEmail());
        userEntity.setFirstName(createUserDto.getFirstname());
        userEntity.setLastName(createUserDto.getLastName());
        userEntity.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        userEntity.setRole(UserRoleEnum.ADMIN);
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setEnabled(true);

        userRepository.save(userEntity);
    }

    public void deleteUser(Long id){
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
    }

    @PostConstruct
    public void init(){
        createAdmin(CreateUserDto.builder()
                .username("admin")
                .password("admin")
                .email("admin@ad.ad")
                .firstname("admin")
                .lastName("admin")
                .repeatedPassword("admin").build());

        createUser(CreateUserDto.builder()
                .username("a")
                .firstname("a")
                .lastName("a")
                .email("a@abc.abc")
                .password("a")
                .repeatedPassword("a").build());
        createUser(CreateUserDto.builder()
                .username("b")
                .firstname("b")
                .lastName("b")
                .email("b@abc.abc")
                .password("b")
                .repeatedPassword("b").build());
        createUser(CreateUserDto.builder()
                .username("c")
                .firstname("c")
                .lastName("c")
                .email("c@abc.abc")
                .password("c")
                .repeatedPassword("c").build());
        createUser(CreateUserDto.builder()
                .username("d")
                .firstname("d")
                .lastName("d")
                .email("d@abc.abc")
                .password("d")
                .repeatedPassword("d").build());
        createUser(CreateUserDto.builder()
                .username("e")
                .firstname("e")
                .lastName("e")
                .email("e@abc.abc")
                .password("e")
                .repeatedPassword("e").build());
        createUser(CreateUserDto.builder()
                .username("f")
                .firstname("f")
                .lastName("f")
                .email("f@abc.abc")
                .password("f")
                .repeatedPassword("f").build());
        createUser(CreateUserDto.builder()
                .username("g")
                .firstname("g")
                .lastName("g")
                .email("g@abc.abc")
                .password("g")
                .repeatedPassword("g").build());
        createUser(CreateUserDto.builder()
                .username("h")
                .firstname("h")
                .lastName("h")
                .email("h@abc.abc")
                .password("h")
                .repeatedPassword("h").build());
    }


}
