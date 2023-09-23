package kuding.petudio.repository;

import kuding.petudio.domain.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BundleRepository extends JpaRepository <Bundle, Long> {
}
