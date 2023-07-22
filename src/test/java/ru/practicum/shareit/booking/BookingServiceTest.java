package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.exception.WrongBookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.testData.BookingTestData;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

public class BookingServiceTest {

    @Mock
    private JpaBookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    UserService userService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBooking() {
        User user = UserTestData.getUserOne();
        User userTwo = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, userTwo);
        Booking booking = BookingTestData.getBookingOne(user, item);

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking createBooking = bookingService.create(booking);

        Assertions.assertThat(booking).isEqualTo(createBooking);
        Assertions.assertThat(booking).isNotNull();


        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void testCreateBookingSetDefaultStatusIfRequired() {
        User user = UserTestData.getUserOne();
        User userTwo = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, userTwo);
        Booking booking = BookingTestData.getBookingOne(user, item);
        booking.setStatus(null);

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking createBooking = bookingService.create(booking);

        Assertions.assertThat(booking).isEqualTo(createBooking);
        Assertions.assertThat(booking).isNotNull();
        Assertions.assertThat(booking.getStatus()).isEqualTo(WAITING);

        verify(bookingRepository).save(any(Booking.class));

    }

    @Test
    public void testCreateBookingOwnerIsBooker() {
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(owner);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        item.setAvailable(false);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusHours(1));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.create(booking));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Вы не можете арендовать свою вещь")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));
    }

    @Test
    public void testCreateBookingItemIsNotAvailable() {
        User user = UserTestData.getUserOne();
        User userTwo = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, userTwo);
        item.setAvailable(false);
        Booking booking = BookingTestData.getBookingOne(user, item);


        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.create(booking));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Вы не можете забронировать эту вещь")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testCreateBookindThatDateEndIsAfterDateStart() {
        User user = UserTestData.getUserOne();
        User userTwo = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, userTwo);
        Booking booking = BookingTestData.getBookingOne(user, item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusHours(1));


        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.create(booking));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Дата окончания бронирования не может быть  раньше начала бронирования")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testApproveNotFoundBooking() {
        User user = UserTestData.getUserOne();
        User userTwo = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, userTwo);
        Booking booking = BookingTestData.getBookingOne(user, item);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.approve(booking.getId(), user.getId(), true));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Не найдено бронирование")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));


    }

    @Test
    public void testApproveOwnerIsBooker() {
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        User anotherUser = UserTestData.getUser();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.approve(booking.getId(), anotherUser.getId(), true));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Бронирование не найдено для пользователя")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));
    }

    @Test
    public void testApproveStatusNotFitForApprove() {
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);

        booking.setStatus(REJECTED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.approve(booking.getId(), owner.getId(), true));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Статус не может быть изменён")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testApproveOwnerIsBookerUser() {
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        User anotherUser = UserTestData.getUser();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, anotherUser);
        Booking booking = BookingTestData.getBookingOne(owner, item);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.approve(booking.getId(), owner.getId(), true));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Не найдено бронирование")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));
    }

    @Test
    public void testApprovePositive() {
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);

        booking.setStatus(WAITING);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingService.approve(booking.getId(), owner.getId(), true);

    }

    @Test
    public void testApproveNegative() {
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);

        booking.setStatus(WAITING);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking approveBooking = bookingService.approve(booking.getId(), owner.getId(), false);

        Assertions.assertThat(approveBooking).isEqualTo(booking);

    }


    @Test
    public void testFindAllByBookerUserIdIsNull() {
        List<Booking> bookings = new ArrayList<>();
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        bookings.add(booking);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(null)).thenReturn(bookings);


        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllByBooker(null));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("UserId не может быть null")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));

    }

    @Test
    public void testFindAllByBooker() {
        List<Booking> bookings = new ArrayList<>();
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        bookings.add(booking);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(owner.getId())).thenReturn(bookings);

        List<Booking> getBooking = bookingService.findAllByBooker(owner.getId());

        Assertions.assertThat(getBooking.size()).isEqualTo(1);
    }


    @Test
    public void testFindAllByBookerPagingWrong() {
        int from = -1;
        int size = 0;
        Long userId = 1L;
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllByBooker(userId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));

    }

    @Test
    public void testFindAllByBookerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User booker = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
        Booking booking2 = BookingTestData.getBookingTwo(requester, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        Page<Booking> bookingPage = new PageImpl<>(bookings, pageable, 2);


        when(bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageable))
                .thenReturn(bookingPage.toList());

        List<Booking> result = bookingService.findAllByBooker(booker.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);

    }

    @Test
    public void testFindAllByBookerPagingUserId() {
        int from = 0;
        int size = 10;
        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllByBooker(null, from, size));


        Assertions.assertThat(throwable)
                .hasMessageStartingWith("UserId не может быть null")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));
    }

    @Test
    public void testFindAllByOwnerWrongStatus() {
        int from = 0;
        int size = 10;
        String state = "UNKNOWN";
        List<Booking> bookings = new ArrayList<>();
        User booker = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
        Booking booking2 = BookingTestData.getBookingTwo(requester, item);
        bookings.add(booking2);
        bookings.add(booking);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllByOwner(booker.getId(), state, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Unknown state: UNKNOWN")
                .asInstanceOf(InstanceOfAssertFactories.type(WrongBookingStatus.class));


    }

    @Test
    public void testFindAllByOwnerAll() {

        // mock
    }

    @Test
    public void testFindAllByOwnerCurrent() {
        // mock
    }

    @Test
    public void testFindAllByOwnerPast() {
        // mock
    }

    @Test
    public void testFindAllByOwnerFuture() {
        // mock
    }

    @Test
    public void testFindAllByOwnerWaiting() {
        // mock
    }

    @Test
    public void testFindAllByOwnerRejected() {
        // mock
    }

    @Test
    public void testFindAllByBookerWrongStatus() {

    }

    @Test
    public void testFindAllByBookerAll() {
        // mock
    }

    @Test
    public void testFindAllByBookerCurrent() {
        // mock
    }

    @Test
    public void testFindAllByBookerPast() {
        // mock
    }

    @Test
    public void testFindAllByBookerFuture() {
        // mock
    }

    @Test
    public void testFindAllByBookerWaiting() {
        // mock
    }

    @Test
    public void testFindAllByBookerRejected() {
        // mock
    }

    @Test
    public void testFindAllCurrentByBookerWrongPaging() {
        int from = -1;
        int size = 0;
        Long userId = 1L;
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(from, size));

        IllegalArgumentException throwable = (IllegalArgumentException) Assertions.catchThrowable(() -> bookingService.findAllCurrentByBooker(userId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(IllegalArgumentException.class));
    }

//    @Test
//    public void testFindAllCurrentByBookerPaging() {
//        int from = 0;
//        int size = 10;
//        List<Booking> bookings = new ArrayList<>();
//        User booker = UserTestData.getUserOne();
//        User requester = UserTestData.getUserTwo();
//        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
//        Item item = ItemTestData.getItemOne(itemRequest, booker);
//        Booking booking = BookingTestData.getBookingOne(booker, item);
//        Booking booking2 = BookingTestData.getBookingTwo(booker, item);
//        bookings.add(booking2);
//        bookings.add(booking);
//        Pageable pageable = PageRequest.of(from, size);
//        Page<Booking> bookingPage = new PageImpl<>(bookings, pageable, 2);
//
//        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(),
//                LocalDateTime.now().minusHours(1),
//                LocalDateTime.now(),
//                pageable))
//                .thenReturn(bookingPage.toList());
//
//        List<Booking> result = bookingService.findAllCurrentByBooker(booker.getId(), from, size);
//        Assertions.assertThat(result.size()).isEqualTo(2);
//    }

    @Test
    public void testFindAllPastByBooker() {

    }
}
