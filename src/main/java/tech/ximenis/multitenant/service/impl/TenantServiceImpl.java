package tech.ximenis.multitenant.service.impl;

import tech.ximenis.multitenant.persistence.TenantRepository;
import tech.ximenis.multitenant.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ximenis.multitenant.model.Tenant;

import java.util.List;

@Slf4j
@Service
public class TenantServiceImpl implements TenantService {

    private static final String TENANT_POOL_NAME_SUFFIX = "DataSource";

    @Autowired
    private TenantRepository tenantRepository;

    public Tenant getTenant(String tenantIdentifier){

        return this.tenantRepository.findTenantByTenantId(tenantIdentifier)
                .orElseThrow(() -> new RuntimeException(String.format("Error creating tenant %s", tenantIdentifier)));
    }

    public List<Tenant> getAllTenants(){
        return tenantRepository.findAll();
    }

    public Tenant saveTenant(Tenant tenant){
        return this.tenantRepository.save(tenant);
    }


}
