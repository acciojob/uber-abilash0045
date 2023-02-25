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
		List<Driver> driverList = driverRepository2.findAllDriversById();

		for (Driver driver : driverList){
			if (driver.getCab().getAvailable()){
				TripBooking tripBooking = new TripBooking();
				tripBooking.setFromLocation(fromLocation);
				tripBooking.setToLocation(toLocation);
				tripBooking.setDistanceInKm(distanceInKm);
				tripBooking.setCustomer(customerRepository2.findById(customerId).get());
				tripBooking.setDriver(driver);
				tripBooking.setStatus(TripStatus.CONFIRMED);

				int bill = driver.getCab().getPerKmRate()*distanceInKm;
				tripBooking.setBill(bill);

				Customer customer = customerRepository2.findById(customerId).get();
				List<TripBooking> customerTripBookingList = customer.getTripBookingList();
				customerTripBookingList.add(tripBooking);
				customer.setTripBookingList(customerTripBookingList);

				List<TripBooking> driverTripBookingList = driver.getTripBookingList();
				driverTripBookingList.add(tripBooking);
				driver.setTripBookingList(driverTripBookingList);

				customerRepository2.save(customer);
				driverRepository2.save(driver);

				return tripBooking;
			}
		}
		throw new Exception("No cab available!");
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
