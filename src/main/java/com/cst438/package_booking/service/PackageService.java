package com.cst438.package_booking.service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cst438.package_booking.domain.Car;
import com.cst438.package_booking.domain.Flight;
import com.cst438.package_booking.domain.FlightInfo;
import com.cst438.package_booking.domain.Hotel;
import com.cst438.package_booking.domain.PackageInfo;
import com.cst438.package_booking.domain.SearchDetails;

@Service
public class PackageService {
	private static final Logger log = LoggerFactory.getLogger(PackageService.class);
	
	@Autowired
	CarService carService;
	
	@Autowired
	FlightService flightService;
	
	@Autowired 
	HotelService hotelService;
	
	public PackageService(CarService carService, FlightService flightService, HotelService hotelService) {
		this.carService = carService;
		this.flightService = flightService;
		this.hotelService = hotelService;
	}
	
	public List<PackageInfo> getPackages(SearchDetails searchDetails){
		log.info("getPackages method was called...");
		
		long nights = ChronoUnit.DAYS.between(
				searchDetails.getDepartureDate(), 
				searchDetails.getReturnDate());
		
		List<PackageInfo> packages = new ArrayList<PackageInfo>();
		List<Car> cars = carService.getTestCars(searchDetails.getDestinationLocation());
		List<FlightInfo> flights = flightService.getTestFlights(searchDetails.getDepartureLocation(),
				searchDetails.getDestinationLocation(),
				searchDetails.getDepartureDate());
		List<Hotel> hotels = hotelService.getTestHotels(
				searchDetails.getDestinationLocation(),
				searchDetails.getDepartureDate(),
				(int)nights);
		
		packages = getCombinations(cars, flights, hotels);
		
		return packages;
	}
	
	List<PackageInfo> getCombinations(List<Car> cars, List<FlightInfo> flights, List<Hotel> hotels) {
		List<PackageInfo> packages = new ArrayList<PackageInfo>();
		
		for(Car c: cars) {
			for(FlightInfo f: flights) {
				for(Hotel h: hotels) {
					packages.add(new PackageInfo(c, f, h));
				}
			}
		}
		
		return packages;
	}

}