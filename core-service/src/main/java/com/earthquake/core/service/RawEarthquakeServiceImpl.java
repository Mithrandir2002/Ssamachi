package com.earthquake.core.service;


import com.earthquake.core.domain.Earthquake;
import com.earthquake.core.repository.EarthquakeRepository;
import org.springframework.stereotype.Service;

@Service
public class RawEarthquakeServiceImpl implements RawEarthquakeService {

    private final EarthquakeRepository earthquakeRepository;

    public RawEarthquakeServiceImpl (EarthquakeRepository earthquakeRepository) {
        this.earthquakeRepository = earthquakeRepository;
    }

    @Override
    public Earthquake saveEarthquake(Earthquake earthquake) {
        return earthquakeRepository.findById(earthquake.getId())
                .map(existing -> saveIfNewer(existing, earthquake))
                .orElseGet(() -> earthquakeRepository.save(earthquake));
    }

    private Earthquake saveIfNewer(Earthquake existing, Earthquake incoming) {
        if (isNewer(incoming.getUpdatedTime(), existing.getUpdatedTime())) {
            return earthquakeRepository.save(incoming);
        }

        // Equal timestamps and late/undated messages must not overwrite newer data.
        return existing;
    }

    private boolean isNewer(java.time.LocalDateTime incoming, java.time.LocalDateTime existing) {
        // A timestamped revision supersedes a legacy row with no USGS updated_time.
        return incoming != null && (existing == null || incoming.isAfter(existing));
    }
}
