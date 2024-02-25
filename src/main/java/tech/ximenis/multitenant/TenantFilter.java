package tech.ximenis.multitenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Order(2)
public class TenantFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String tenantId = request.getHeader(TenantContext.TENANT_HEADER);
            if (Objects.nonNull(tenantId) && !tenantId.isEmpty()) {
                TenantContext.setTenantId(tenantId);
            }
            filterChain.doFilter(request, response);
        }catch (Exception e){
            log.error("Error setting tenant", e);
        } finally {
            TenantContext.clear();
        }
    }
}