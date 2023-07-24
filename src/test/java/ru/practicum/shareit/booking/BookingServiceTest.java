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
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
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
import static org.mockito.ArgumentMatchers.eq;
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
        User booker = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
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
        User booker = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
        bookings.add(booking);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId())).thenReturn(bookings);

        List<Booking> getBooking = bookingService.findAllByBooker(booker.getId());

        Assertions.assertThat(getBooking.size()).isEqualTo(1);
    }


    @Test
    public void testFindAllByBookerPagingWrong() {
        int from = -1;
        int size = 0;
        Long userId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllByBooker(userId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));

    }

    @Test
    public void testFindAllByOwnerIdIsNull() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User requester = UserTestData.getUserOne();
        User owner = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        bookings.add(booking);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(null)).thenReturn(bookings);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllByOwner(null, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("UserId не может быть null")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));

    }

    @Test
    public void testFindAllByOwner() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(requester, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        Page<Booking> bookingPage = new PageImpl<>(bookings, pageable, 2);

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(), pageable))
                .thenReturn(bookingPage.toList());

        List<Booking> result = bookingService.findAllByOwner(owner.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }


    @Test
    public void testFindAllByOwnerPagingWrong() {
        int from = -1;
        int size = 0;
        Long ownerId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllByOwner(ownerId, from, size));

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
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(requester, item);
        bookings.add(booking2);
        bookings.add(booking);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllByOwner(
                owner.getId(),
                state,
                from,
                size)
        );

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Unknown state: UNKNOWN")
                .asInstanceOf(InstanceOfAssertFactories.type(WrongBookingStatus.class));


    }

    @Test
    public void testFindAllCurrentByBookerWrongPaging() {
        int from = -1;
        int size = 0;
        Long userId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllCurrentByBooker(userId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFindAllCurrentByBookerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllCurrentByOwner(owner.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testFindAllCurrentByOwnerWrongPaging() {
        int from = -1;
        int size = 0;
        Long ownerId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllCurrentByOwner(ownerId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFindAllCurrentByOwnerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllCurrentByBooker(owner.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testFindAllPastByBookerWrongPage() {
        int from = -1;
        int size = 0;
        Long userId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllPastByBooker(userId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFindAllPastByBookerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User booker = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
        Booking booking2 = BookingTestData.getBookingTwo(booker, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                eq(booker.getId()),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllPastByBooker(booker.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testFindAllPastByOwnerWrongPage() {
        int from = -1;
        int size = 0;
        Long ownerId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllPastByOwner(ownerId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFindAllPastByOwnerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllPastByOwner(owner.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testFindAllFutureByBookerWrongPage() {
        int from = -1;
        int size = 0;
        Long userId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllFutureByBooker(userId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFindAllFutureByBookerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User booker = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
        Booking booking2 = BookingTestData.getBookingTwo(booker, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                eq(booker.getId()),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllFutureByBooker(booker.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testFindAllFutureByOwnerWrongPage() {
        int from = -1;
        int size = 0;
        Long ownerId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllFutureByOwner(ownerId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFindAllFutureByOwnerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User booker = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
        Booking booking2 = BookingTestData.getBookingTwo(booker, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                eq(booker.getId()),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllFutureByOwner(booker.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testAllWaitingByBookerWrongPage() {
        int from = -1;
        int size = 0;
        Long userId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllWaitingByBooker(userId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFindAllWaitingByBookerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User booker = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
        Booking booking2 = BookingTestData.getBookingTwo(booker, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                eq(booker.getId()),
                eq(WAITING),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllWaitingByBooker(booker.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testFindAllWaitingByOwnerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartAsc(
                eq(owner.getId()),
                eq(WAITING),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllWaitingByOwner(owner.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testAllWaitingByOwnerWrongPage() {
        int from = -1;
        int size = 0;
        Long ownerId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllWaitingByOwner(ownerId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testAllRejectedByBookerWrongPage() {
        int from = -1;
        int size = 0;
        Long bookerId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllRejectedByBooker(bookerId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFinAllRejectedByOwnerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartAsc(
                eq(owner.getId()),
                eq(REJECTED),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllRejectedByOwner(owner.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testAllRejectedByOwnerWrongPage() {
        int from = -1;
        int size = 0;
        Long ownerId = 1L;
        assertThrows(RuntimeException.class, () -> PageRequest.of(from, size));

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllRejectedByOwner(ownerId, from, size));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Неверный номер страницы")
                .asInstanceOf(InstanceOfAssertFactories.type(ValidateException.class));
    }

    @Test
    public void testFinAllRejectedByBookerPaging() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User booker = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, booker);
        Booking booking = BookingTestData.getBookingOne(booker, item);
        Booking booking2 = BookingTestData.getBookingTwo(booker, item);
        bookings.add(booking2);
        bookings.add(booking);
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                eq(booker.getId()),
                eq(REJECTED),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllRejectedByBooker(booker.getId(), from, size);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }


    @Test
    public void findAllByOwnerALL() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "ALL";
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                eq(owner.getId()),
                eq(pageable))
        )
                .thenReturn(bookings);
        List<BookingResponseDto> bookingList = bookingService.findAllByOwner(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByOwnerCURRENT() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "CURRENT";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllByOwner(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByOwnerPAST() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "PAST";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllByOwner(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByOwnerFUTURE() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "FUTURE";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllByOwner(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByOwnerWAITING() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "WAITING";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartAsc(
                eq(owner.getId()),
                eq(WAITING),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllByOwner(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByOwnerREJECTED() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "REJECTED";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartAsc(
                eq(owner.getId()),
                eq(REJECTED),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllByOwner(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByBookingALL() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "ALL";
        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(
                eq(owner.getId()),
                eq(pageable))
        )
                .thenReturn(bookings);
        List<BookingResponseDto> bookingList = bookingService.findAllBookings(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByBookingCURRENT() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "CURRENT";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllBookings(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByBookingPAST() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "PAST";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllBookings(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByBookingFUTURE() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "FUTURE";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                eq(owner.getId()),
                any(LocalDateTime.class),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllBookings(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByBookingWAITING() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "WAITING";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                eq(owner.getId()),
                eq(WAITING),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllBookings(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void findAllByBookingREJECTED() {
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(owner, item);
        bookings.add(booking2);
        bookings.add(booking);
        String state = "REJECTED";

        Pageable pageable = PageRequest.of(from, size);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                eq(owner.getId()),
                eq(REJECTED),
                eq(pageable))
        )
                .thenReturn(bookings);

        List<BookingResponseDto> bookingList = bookingService.findAllBookings(owner.getId(), state, from, size);

        Assertions.assertThat(bookingList.size()).isEqualTo(2);


    }

    @Test
    public void testFindAllBookingsWrongStatus() {
        int from = 0;
        int size = 10;
        String state = "UNKNOWN";
        List<Booking> bookings = new ArrayList<>();
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        Booking booking = BookingTestData.getBookingOne(owner, item);
        Booking booking2 = BookingTestData.getBookingTwo(requester, item);
        bookings.add(booking2);
        bookings.add(booking);

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> bookingService.findAllBookings(
                owner.getId(),
                state,
                from,
                size)
        );

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Unknown state: UNKNOWN")
                .asInstanceOf(InstanceOfAssertFactories.type(WrongBookingStatus.class));

    }

    @Test
    public void testHGetNextBookingForItem() {
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        List<Booking> bookings = BookingTestData.createBookingList(owner, item);

        when(bookingRepository.findAllByItemId(
                item.getId())
        ).thenReturn(bookings);

        Booking booking = bookingService.getNextBookingForItem(item.getId());

        Assertions.assertThat(booking).isEqualTo(bookings.get(3));
    }

    @Test
    public void testGetLastBookingForItem() {
        User owner = UserTestData.getUserOne();
        User requester = UserTestData.getUserTwo();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(requester);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        List<Booking> bookings = BookingTestData.createBookingList(owner, item);

        when(bookingRepository.findAllByItemId(
                item.getId())
        ).thenReturn(bookings);

        Booking booking = bookingService.getLastBookingForItem(item.getId());

        Assertions.assertThat(booking).isEqualTo(bookings.get(1));
    }


}