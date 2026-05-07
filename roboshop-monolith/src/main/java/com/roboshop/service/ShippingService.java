package com.roboshop.service;

import com.roboshop.model.City;
import com.roboshop.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ShippingService {

    private final CityRepository cityRepository;

    // Base coordinates for warehouse (New York)
    private static final double WAREHOUSE_LAT = 40.7128;
    private static final double WAREHOUSE_LON = -74.0060;

    public ShippingService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public List<City> searchCities(String query) {
        return cityRepository.searchByCity(query);
    }

    public Optional<City> getCityById(Long id) {
        return cityRepository.findById(id);
    }

    public BigDecimal calculateShippingCost(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("City not found"));

        double distance = haversineDistance(
                WAREHOUSE_LAT, WAREHOUSE_LON,
                city.getLatitude().doubleValue(), city.getLongitude().doubleValue()
        );

        // Base cost $5 + $0.005 per km, min $5, max $50
        double cost = 5.0 + (distance * 0.005);
        cost = Math.max(5.0, Math.min(50.0, cost));

        return BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
