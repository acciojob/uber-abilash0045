package com.driver.services.impl;
import com.driver.model.*;
import com.driver.repository.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private CabRepository cabRepository;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		deleteCustomer(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		TripBooking tripBooking = new TripBooking();

		List<Driver> driverList = driverRepository2.findAll();

		Driver driver = null;

		for (Driver driver1 : driverList){
			if (driver1.getCab().getAvailable() == true){
				if (driver == null || driver1.getDriverId() < driver.getDriverId()){
					driver = driver1;
				}
			}
		}
		if (driver == null){
			throw new Exception("No cab available!");
		}

		Customer customer = customerRepository2.findById(customerId).get();

		tripBooking.setCustomer(customer);
		tripBooking.setDriver(driver);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setBill(distanceInKm*10);
		tripBooking.setStatus(TripStatus.CONFIRMED);

		driver.getCab().setAvailable(false);
		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);

		customer.getTripBookingList().add(tripBooking);
		customerRepository2.save(customer);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);

		Driver driver = tripBooking.getDriver();
		Cab cab = driver.getCab();
		cab.setAvailable(true);
		driverRepository2.save(driver);
		Customer customer = tripBooking.getCustomer();
		customerRepository2.save(customer);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly

		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);

		Driver driver = tripBooking.getDriver();
		Cab cab = driver.getCab();
		cab.setAvailable(true);
		driverRepository2.save(driver);
		Customer customer = tripBooking.getCustomer();
		customerRepository2.save(customer);
	}
}
