package com.github.victorximenis.multitenant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@Entity
@ToString
@Table(name = "t_tenant")
@RequiredArgsConstructor
public class Tenant extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;

    @Size(max = 36)
    @Column(name = "tenant_id")
    private String tenantId;

    private String username;

    private String password;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "driver_classname")
    private String driverClassName;

    @Column(name = "jdbc_url")
    private String jdbcUrl;

    @Column(name = "max_pool_size")
    private Integer maxPoolSize;

    @Column(name = "minimum_idle")
    private Integer minimumIdle;

    @Column(name = "idle_timeout")
    private Integer idleTimeout;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Tenant tenant = (Tenant) o;
        return getId() != null && Objects.equals(getId(), tenant.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
