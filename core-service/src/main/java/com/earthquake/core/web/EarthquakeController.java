package com.earthquake.core.web;

import com.earthquake.core.dto.EarthquakeResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/earthquakes")
public class EarthquakeController {

    @GetMapping
    public ResponseEntity<Page<EarthquakeResponse>> list(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) Double minMagnitude,
            @RequestParam(required = false) Double maxMagnitude,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        throw new UnsupportedOperationException("TODO: implement earthquake list/search");
    }

    @GetMapping("/{id}")
    public ResponseEntity<EarthquakeResponse> getById(@PathVariable String id) {
        throw new UnsupportedOperationException("TODO: implement get earthquake by id");
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<EarthquakeResponse>> nearby(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radiusKm,
            @RequestParam(defaultValue = "20") int limit
    ) {
        throw new UnsupportedOperationException("TODO: implement nearby spatial search");
    }
}
