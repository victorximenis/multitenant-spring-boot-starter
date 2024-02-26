package com.github.victorximenis.multitenant;

import com.github.victorximenis.multitenant.util.LogBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TenantContext {

    private static final Logger log = LoggerFactory.getLogger(TenantContext.class);

    public static final String TENANT_HEADER = "X-TENANT-ID";

    public TenantContext() {}

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {

        if(log.isDebugEnabled()){
            log.debug(LogBuilder.of()
                    .header("Setting tenant context")
                    .row("Tenant: ", tenantId)
                    .build());
        }

        currentTenant.set(tenantId);
    }

    public static String getTenantId() {

        if(log.isDebugEnabled()){
            log.debug(LogBuilder.of()
                    .header("Getting tenant context")
                    .row("Tenant: ", currentTenant.get())
                    .build());
        }

        return currentTenant.get();
    }

    public static void clear(){
        currentTenant.remove();
    }
}
