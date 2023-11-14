package kuding.petudio.repository;

import kuding.petudio.domain.Bundle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    Page<Bundle> findByIsPublicTrue(Pageable page);
    Optional<Bundle> findByRandomName(String randomName);
}
