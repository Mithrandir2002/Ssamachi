package com.earthquake.core.raw;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawIngestionPayloadRepository extends MongoRepository<RawIngestionPayload, String> {
}
