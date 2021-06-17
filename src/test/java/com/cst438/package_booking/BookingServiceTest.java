package com.cst438.package_booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.QueryTimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.cst438.package_booking.domain.Booking;
import com.cst438.package_booking.domain.Car;
import com.cst438.package_booking.domain.FlightInfo;
import com.cst438.package_booking.domain.Hotel;
import com.cst438.package_booking.domain.PackageInfo;
import com.cst438.package_booking.domain.Room;
import com.cst438.package_booking.domain.SearchDetails;
import com.cst438.package_booking.repository.BookingRepository;
import com.cst438.package_booking.service.BookingService;
import com.cst438.package_booking.service.CarService;
import com.cst438.package_booking.service.FlightService;
import com.cst438.package_booking.service.HotelService;

@SpringBootTest
class BookingServiceTest {
	
	@MockBean
	private BookingRepository mockBookingRepository;
	
	@MockBean
	private CarService mockCarService;
	
	@MockBean
	private FlightService mockFlightService;
	
	@MockBean
	private HotelService mockHotelService;
	
	@Autowired
	private BookingService bookingService;
	
	@BeforeEach
	public void setupEach() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	void getBookingForUser_userId_returnBookingList() {
		List<Booking> testBookings = new ArrayList<Booking>();
		Booking testBooking = new Booking(1,3,"Las Vegas", 321);
		
		testBookings.add(testBooking);
		given(mockBookingRepository.findByUserId(321)).willReturn(testBookings);
		
		bookingService = new BookingService(mockBookingRepository, mockCarService, mockFlightService, mockHotelService);
		
		List<Booking> userBookings = bookingService.getBookingsForUser(321);
		assertEquals(userBookings.get(0), testBookings.get(0));
	}
	
	@Test
	void getBookingForUser_userId_throwException() {
		List<Booking> testBookings = new ArrayList<Booking>();
		Booking testBooking = new Booking(1,3,"Las Vegas", 321);
		
		testBookings.add(testBooking);
		when(mockBookingRepository.findByUserId(321)).thenThrow(QueryTimeoutException.class);
		
		bookingService = new BookingService(mockBookingRepository, mockCarService, mockFlightService, mockHotelService);
		
		List<Booking> userBookings = bookingService.getBookingsForUser(321);
		assertEquals(null, userBookings);
	}
	
	@Test
	void createBooking_withDetails_returnsBooking() {
		int testUserId = 321;
		SearchDetails testSearchDetails = new SearchDetails(1, "City1", "City2", 
				LocalDate.of(2021, 6, 6), LocalDate.of(2021, 6, 10), 2, 2);
		
		Car c = new Car("RentalCom1", "Luxury Sports Car", 1234.0);
		
		FlightInfo f = new FlightInfo(123, "Airline1", "City1", "City2", LocalDateTime.of(2021, 6, 6, 5, 30), 2345.6);
		
		Room r = new Room(678.9, 4, "King");
		List<Room> rooms = new ArrayList<Room>();
		rooms.add(r);
		
		Hotel h = new Hotel(123, "Hotel1");
		h.setRooms(rooms);
		
		PackageInfo pk = new PackageInfo(c, f,h);
		
		given(mockCarService.createBooking(eq(321), any(Car.class), any(Booking.class))).willReturn(true);
		given(mockFlightService.createBooking(eq(321), any(FlightInfo.class), any(Booking.class))).willReturn(true);
		given(mockHotelService.createBooking(eq(321), any(Hotel.class), any(Booking.class))).willReturn(true);
		when(mockBookingRepository.save(any(Booking.class))).then(i -> i.getArgument(0, Booking.class));
		
		bookingService = new BookingService(mockBookingRepository, mockCarService, mockFlightService, mockHotelService);
		
		Booking bk = bookingService.createBooking(testUserId, testSearchDetails, pk);
		assertEquals(testUserId, bk.getUserId());
	}
	
	@Test
	void cancelBooking() {
		int testUserId = 321;
		SearchDetails testSearchDetails = new SearchDetails(1, "City1", "City2", 
				LocalDate.of(2021, 6, 6), LocalDate.of(2021, 6, 10), 2, 2);
		
		Car c = new Car("RentalCom1", "Luxury Sports Car", 1234.0);
		
		FlightInfo f = new FlightInfo(123, "Airline1", "City1", "City2", LocalDateTime.of(2021, 6, 6, 5, 30), 2345.6);
		
		Room r = new Room(678.9, 4, "King");
		List<Room> rooms = new ArrayList<Room>();
		rooms.add(r);
		
		Hotel h = new Hotel(123, "Hotel1");
		h.setRooms(rooms);
		
		PackageInfo pk = new PackageInfo(c, f,h);
		
		bookingService = new BookingService(mockBookingRepository, mockCarService, mockFlightService, mockHotelService);
		
		Booking bk = bookingService.bookingMapper(testUserId, testSearchDetails, pk);
		
	}
	
//	@Test
//	void createBooking_extBookingFailed_returnsNull() {
//		int testUserId = 321;
//		SearchDetails testSearchDetails = new SearchDetails(1, "City1", "City2", 
//				LocalDate.of(2021, 6, 6), LocalDate.of(2021, 6, 10), 2, 2);
//		
//		Car c = new Car("RentalCom1", "Luxury Sports Car", 1234.0);
//		
//		FlightInfo f = new FlightInfo(123, "Airline1", "City1", "City2", LocalDateTime.of(2021, 6, 6, 5, 30), 2345.6);
//		
//		Room r = new Room(678.9, 4, "King");
//		List<Room> rooms = new ArrayList<Room>();
//		rooms.add(r);
//		
//		Hotel h = new Hotel(123, "Hotel1");
//		h.setRooms(rooms);
//		
//		PackageInfo pk = new PackageInfo(c, f,h);
//		bookingService = new BookingService(mockBookingRepository,
//				mockCarService, mockFlightService, mockHotelService);
//		
//		given(mockCarService.createBooking(eq(321), any(Car.class), any(Booking.class))).willReturn(false);
//		given(mockFlightService.createBooking(eq(321), any(FlightInfo.class), any(Booking.class))).willReturn(true);
//		given(mockHotelService.createBooking(eq(321), any(Hotel.class), any(Booking.class))).willReturn(true);
//		when(mockBookingRepository.save(any(Booking.class))).then(i -> i.getArgument(0, Booking.class));
//		
//		Booking bk = bookingService.createBooking(testUserId, testSearchDetails, pk);
//		assertEquals(bk, null);
//	}


}