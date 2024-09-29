package faang.school.projectservice.repository;

import faang.school.projectservice.model.GoogleCalendarToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleCalendarTokenRepository extends JpaRepository<GoogleCalendarToken, Long> {
    @Query(nativeQuery = true, value = """
            SELECT ct.* FROM google_calendar_token ct
            WHERE ct.project_id = :projectId
            """)
    Optional<GoogleCalendarToken> findByProjectId(@Param("projectId") long projectId);
}
