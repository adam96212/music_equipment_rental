package pl.booking_instrument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.booking_instrument.booking.BookingService;
import pl.booking_instrument.booking.dto.BookingDto;
import pl.booking_instrument.booking.dto.CreateBookingDto;
import pl.booking_instrument.instrument.InstrumentService;
import pl.booking_instrument.instrument.dto.InstrumentDto;
import pl.booking_instrument.instrument.dto.InstrumentSearchRequest;
import pl.booking_instrument.user.UserException;
import pl.booking_instrument.user.UserService;
import pl.booking_instrument.user.dto.CreateUserDto;
import pl.booking_instrument.user.dto.UserDto;
import pl.booking_instrument.user.model.UserEntity;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AppController {
    private final Cache cache;
    private final InstrumentService instrumentService;
    private final UserService userService;
    private final BookingService bookingService;

    private InstrumentSearchRequest searchRequestDtoGlobal;
    private Map<String, String> errors = new HashMap<>();

    @Autowired
    public AppController(Cache cache, InstrumentService instrumentService, UserService userService, BookingService bookingService) {
        this.cache = cache;
        this.bookingService= bookingService;
        this.instrumentService = instrumentService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam("page")Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size)
    {
        int currentPage = page.orElse(1);
        int pageS = size.orElse(10);

        Page<InstrumentDto> instrumentsPage = instrumentService.getInstruments(PageRequest.of(currentPage - 1, pageS));
        model.addAttribute("instruments", instrumentsPage);

        int totalPages = instrumentsPage.getTotalPages();
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
    public String searchInstruments(@ModelAttribute InstrumentSearchRequest instrumentSearchRequest)
    {
        List<InstrumentDto> instruments = instrumentService.searchInstruments(instrumentSearchRequest);
        searchRequestDtoGlobal = instrumentSearchRequest;
        System.out.println(instruments.toString());
        cache.setInstruments(instruments);

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
        InstrumentSearchRequest request = searchRequestDtoGlobal != null ? searchRequestDtoGlobal : new InstrumentSearchRequest();
        model.addAttribute("searchRequest", request);
        model.addAttribute("instruments", cache.getInstruments());
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
                                @RequestParam("instrumentsPage") Optional<Integer> instrumentsPage,
                                @RequestParam("instrumentsName") Optional<String> instrumentsName)
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

        int currentPage = instrumentsPage.orElse(1);
        int pageSize = (5);

        Page<InstrumentDto> instruments = instrumentService.getInstruments(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("instruments", instruments);

        int totalPages = instruments.getTotalPages();
        if (totalPages > 0)
        {
            List<Integer> instrumentsPageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("instrumentsPageNumbers", instrumentsPageNumbers);
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

    @GetMapping(path = "/deleteInstrument/{id}")
    public String deleteInstrument(@PathVariable Long id)
    {
        instrumentService.deleteInstrument(id);
        return "redirect:/admin";
    }
}
