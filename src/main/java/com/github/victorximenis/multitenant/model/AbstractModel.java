package com.github.victorximenis.multitenant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 9213555599511037793L;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}