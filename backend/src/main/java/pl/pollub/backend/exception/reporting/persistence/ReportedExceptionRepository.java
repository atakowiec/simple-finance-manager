package pl.pollub.backend.exception.reporting.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for stored exception reports.
 */
@Repository
public interface ReportedExceptionRepository extends JpaRepository<ReportedExceptionEntity, Long> {
}

