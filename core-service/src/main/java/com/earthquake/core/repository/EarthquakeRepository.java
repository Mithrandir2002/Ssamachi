package com.earthquake.core.repository;

import com.earthquake.core.domain.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarthquakeRepository extends JpaRepository<Earthquake, String> {
    // TODO: derived/native query methods for filtering by date range, magnitude range
    // and spatial "nearby" search will be added when the query logic is implemented.
}
