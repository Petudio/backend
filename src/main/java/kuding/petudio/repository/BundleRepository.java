package kuding.petudio.repository;

import kuding.petudio.domain.Bundle;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
}
