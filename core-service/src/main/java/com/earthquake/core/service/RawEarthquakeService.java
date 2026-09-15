package com.earthquake.core.service;

import com.earthquake.core.domain.Earthquake;

public interface RawEarthquakeService {
    Earthquake saveEarthquake(Earthquake earthquake);
}
