package com.roboshop.repository;

import com.roboshop.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    @Query("SELECT c FROM City c WHERE LOWER(c.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<City> searchByCity(@Param("query") String query);

    List<City> findByCountryCode(String countryCode);
}
