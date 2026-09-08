package com.earthquake.core.web;

import com.earthquake.core.dto.TopRegionResponse;
import com.earthquake.core.dto.TrendPointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @GetMapping("/trend")
    public ResponseEntity<List<TrendPointResponse>> trend(
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String region
    ) {
        throw new UnsupportedOperationException("TODO: implement trend aggregation");
    }

    @GetMapping("/top-regions")
    public ResponseEntity<List<TopRegionResponse>> topRegions(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String period
    ) {
        throw new UnsupportedOperationException("TODO: implement top-regions aggregation");
    }
}
