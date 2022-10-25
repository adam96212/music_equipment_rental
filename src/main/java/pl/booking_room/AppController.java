package pl.booking_room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.booking_room.booking.BookingService;
import pl.booking_room.booking.dto.BookingDto;
import pl.booking_room.booking.dto.CreateBookingDto;
import pl.booking_room.room.RoomService;
import pl.booking_room.room.dto.RoomDto;
import pl.booking_room.room.dto.RoomSearchRequest;
import pl.booking_room.user.UserException;
import pl.booking_room.user.UserService;
import pl.booking_room.user.dto.CreateUserDto;
import pl.booking_room.user.dto.UserDto;
import pl.booking_room.user.model.UserEntity;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AppController {
    private final Cache cache;
    private final RoomService roomService;
    private final UserService userService;
    private final BookingService bookingService;

    private RoomSearchRequest searchRequestDtoGlobal;
    private Map<String, String> errors = new HashMap<>();

    @Autowired
    public AppController(Cache cache, RoomService roomService, UserService userService, BookingService bookingService) {
        this.cache = cache;
        this.bookingService= bookingService;
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam("page")Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size)
    {
        int currentPage = page.orElse(1);
        int pageS = size.orElse(10);

        Page<RoomDto> roomsPage = roomService.getRooms(PageRequest.of(currentPage - 1, pageS));
        model.addAttribute("rooms", roomsPage);

        int totalPages = roomsPage.getTotalPages();
        if (totalPages > 0)
        {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "home";
    }

    @GetMapping(path = "/login")
    public String getLoginView(Model model)
    {
        model.addAttribute("user", new UserEntity());
        return "login";
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute CreateUserDto createUserDto) {
        try
        {
            userService.createUser(createUserDto);
            return "redirect:/home";
        }
        catch (UserException ex)
        {
            errors.put("register", ex.getMessage());
            return "redirect:/register";
        }
        catch (DataIntegrityViolationException ex)
        {
            Throwable constraintException = ex.getCause();
            Throwable psqlException = constraintException.getCause();
            if (psqlException.getMessage().contains("email"))
            {
                errors.put("register", "There is an account associated with this email address");
            }
            else if(psqlException.getMessage().contains("username"))
            {
                errors.put("register", "Username is not available");
            }
            return "redirect:/register";
        }
    }

    @GetMapping(path = "/register")
    public String getRegisterView(Model model)
    {
        if ( errors.containsKey("register"))
        {
            String err = errors.get("register");
            model.addAttribute("errorMessage", err);
        }
        model.addAttribute("newUser", new CreateUserDto());
        return "register";
    }

    @PostMapping(path = "/filter")
    public String searchRooms(@ModelAttribute RoomSearchRequest roomSearchRequest)
    {
        List<RoomDto> rooms = roomService.searchRooms(roomSearchRequest);
        searchRequestDtoGlobal = roomSearchRequest;
        cache.setRooms(rooms);
        return "redirect:/search";
    }

    @PostMapping(path = "/createBooking")
    public String createBooking(@ModelAttribute CreateBookingDto createBookingDto)
    {
        bookingService.createBooking(createBookingDto);
        return "redirect:/profile";
    }

    @GetMapping(path = "/search")
    public String getBookingPanelView(Model model)
    {
        RoomSearchRequest request = searchRequestDtoGlobal != null ? searchRequestDtoGlobal : new RoomSearchRequest();
        model.addAttribute("searchRequest", request);
        model.addAttribute("rooms", cache.getRooms());
        return "search";
    }

    @GetMapping(path = "/profile")
    public String getProfileView(Model model)
    {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        UserEntity user = userService.getUserByUsername(username);
        List<BookingDto> bookings = bookingService.getBookingsForUserById(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        return "profile";
    }

    @GetMapping(path = "/admin")
    public String getAdminPanel(Model model,
                                @RequestParam("usersPage") Optional<Integer> usersPage,
                                @RequestParam("usersSize") Optional<Integer> usersSize,
                                @RequestParam("bookingsPage") Optional<Integer> bookingsPage,
                                @RequestParam("bookingsSize") Optional<Integer> bookingsSize,
                                @RequestParam("roomsPage") Optional<Integer> roomsPage,
                                @RequestParam("roomsSize") Optional<Integer> roomsSize)
    {
        int usersCurrentPage = usersPage.orElse(1);
        int usersPageSize = usersSize.orElse(5);

        Page<UserDto> users = userService.getAllUsersPage(PageRequest.of(usersCurrentPage - 1, usersPageSize));

        model.addAttribute("users", users);

        int totalUsersPages = users.getTotalPages();
        if (totalUsersPages > 0)
        {
            List<Integer> usersPageNumbers = IntStream.rangeClosed(1, totalUsersPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("usersPageNumbers", usersPageNumbers);
        }

        int bookingsCurrentPage = bookingsPage.orElse(1);
        int bookingsPageSize = bookingsSize.orElse(5);

        Page<BookingDto> bookings = bookingService.getBookingsPage(PageRequest.of(bookingsCurrentPage - 1, bookingsPageSize));

        model.addAttribute("bookings", bookings);

        int totalBookingsPages = users.getTotalPages();
        if (totalBookingsPages > 0)
        {
            List<Integer> bookingsPageNumbers = IntStream.rangeClosed(1, totalBookingsPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("bookingsPageNumbers", bookingsPageNumbers);
        }

        int currentPage = roomsPage.orElse(1);
        int pageSize = roomsSize.orElse(5);

        Page<RoomDto> rooms = roomService.getRooms(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("rooms", rooms);

        int totalPages = rooms.getTotalPages();
        if (totalPages > 0)
        {
            List<Integer> roomsPageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("roomsPageNumbers", roomsPageNumbers);
        }

        return "admin";
    }

    @GetMapping(path = "/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = (UserEntity) authentication.getPrincipal();
        if (!Objects.equals(user.getId(), id))
        {
            userService.deleteUser(id);
        }
        return "redirect:/admin";
    }

    @GetMapping(path = "/deleteBooking/{id}")
    public String deleteBooking(@PathVariable Long id)
    {
        bookingService.deleteBooking(id);
        return "redirect:/admin";
    }

    @GetMapping(path = "/deleteRoom/{id}")
    public String deleteRoom(@PathVariable Long id)
    {
        roomService.deleteRoom(id);
        return "redirect:/admin";
    }
}
