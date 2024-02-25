package tech.ximenis.multitenant.persistence;

import tech.ximenis.multitenant.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findTenantByTenantId(String id);
}
