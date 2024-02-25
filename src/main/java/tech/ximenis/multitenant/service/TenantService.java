package tech.ximenis.multitenant.service;

import tech.ximenis.multitenant.model.Tenant;

import java.util.List;

public interface TenantService {

    public Tenant getTenant(String tenantIdentifier);

    public List<Tenant> getAllTenants();

    public Tenant saveTenant(Tenant tenant);

}
