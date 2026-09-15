package com.earthquake.core.service;

import com.earthquake.core.domain.Earthquake;
import com.earthquake.core.repository.EarthquakeRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RawEarthquakeServiceImplTest {

    @Test
    void savesAnEventOnlyOnceWhenKafkaDeliversTheSameMessageTwice() {
        EarthquakeRepository repository = mock(EarthquakeRepository.class);
        RawEarthquakeService service = new RawEarthquakeServiceImpl(repository);
        LocalDateTime updatedTime = LocalDateTime.of(2026, 9, 15, 4, 30);
        Earthquake firstDelivery = Earthquake.builder()
                .id("us7000duplicate")
                .updatedTime(updatedTime)
                .build();
        Earthquake retryDelivery = Earthquake.builder()
                .id("us7000duplicate")
                .updatedTime(updatedTime)
                .build();

        // First delivery is new; the retry sees the row written by that delivery.
        when(repository.findById(firstDelivery.getId()))
                .thenReturn(Optional.empty(), Optional.of(firstDelivery));
        when(repository.save(firstDelivery)).thenReturn(firstDelivery);

        service.saveEarthquake(firstDelivery);
        service.saveEarthquake(retryDelivery);

        verify(repository, times(1)).save(firstDelivery);
        verify(repository, never()).save(retryDelivery);
    }
}
