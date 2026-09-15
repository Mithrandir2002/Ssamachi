package com.earthquake.core.repository;

import com.earthquake.core.domain.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EarthquakeRepository extends JpaRepository<Earthquake, String> {
    // TODO: derived/native query methods for filtering by date range, magnitude range
    // and spatial "nearby" search will be added when the query logic is implemented.
    Optional<Earthquake> findEarthquakeById(String id);

    @Modifying
    @Query(value = """
    UPDATE earthquakes
    SET location = geography::Point(:latitude, :longitude, 4326)
    WHERE id = :id
    """, nativeQuery = true)
    void updateLocation(String id, double latitude, double longitude);
}
