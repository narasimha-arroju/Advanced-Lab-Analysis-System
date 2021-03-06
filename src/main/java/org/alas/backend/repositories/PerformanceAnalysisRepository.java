package org.alas.backend.repositories;

import org.alas.backend.documents.PerformanceAnalysis;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceAnalysisRepository extends ReactiveMongoRepository<PerformanceAnalysis, String> {
}
