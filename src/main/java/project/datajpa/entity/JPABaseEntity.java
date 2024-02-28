package project.datajpa.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class JPABaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    //저장하기 전 이벤트
    @PrePersist
    public void prePersist(){

        LocalDateTime now = LocalDateTime.now();

        createdDate = now;
        updatedDate = now;

    }

    //저장된 후 이벤트
    @PreUpdate
    public void preUpdate(){

        updatedDate = LocalDateTime.now();

    }


}
