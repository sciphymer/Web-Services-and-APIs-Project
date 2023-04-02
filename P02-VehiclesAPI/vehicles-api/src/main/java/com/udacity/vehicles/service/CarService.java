package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final PriceClient pClient;
    private final MapsClient mClient;

    public CarService(CarRepository repository, PriceClient pClient, MapsClient mClient) {
        this.repository = repository;
        this.pClient = pClient;
        this.mClient = mClient;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        List<Car> tmpCar = repository.findAll();
        List<Car> cars = new ArrayList<>();
        for(Car car : tmpCar){
            Location locDetails = mClient.getAddress(car.getLocation());
            Location location = copyLocationDetailsToCar(locDetails, car);
            car.setPrice(pClient.getPrice(car.getId()));
            car.setLocation(location);
            cars.add(car);
        }

        return cars;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        Car car;
        Optional<Car> optCar = repository.findById(id);
        if(optCar.isPresent()){
             car = optCar.get();
             Location locDetails = mClient.getAddress(car.getLocation());
             Location location = copyLocationDetailsToCar(locDetails, car);
             car.setPrice(pClient.getPrice(id));
             car.setLocation(location);
        } else {
            throw new CarNotFoundException();
        }

        return car;
    }

    private static Location copyLocationDetailsToCar(Location locDetails, Car car){
        Location location = new Location();
        location = car.getLocation();
        location.setAddress(locDetails.getAddress());
        location.setCity(locDetails.getCity());
        location.setState(locDetails.getState());
        location.setZip(locDetails.getZip());
        return location;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         */
        Optional<Car> optCar = repository.findById(id);
        if(optCar.isPresent()){
            Car car = optCar.get();
            repository.delete(car);
        } else {
            throw new CarNotFoundException();
        }
    }
}
