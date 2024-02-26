package com.github.victorximenis.multitenant.service;

import com.github.victorximenis.multitenant.model.Tenant;

import java.util.List;

public interface TenantService {

    public Tenant getTenant(String tenantIdentifier);

    public List<Tenant> getAllTenants();

    public Tenant saveTenant(Tenant tenant);

}
