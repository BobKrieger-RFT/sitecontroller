package com.sitecontroller.sitecontroller.entity;

/* TO DO
import com.rft.common.model.entity.base.BaseEntity;
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.SuperBuilder;
//import org.hibernate.envers.Audited;

@Entity
@Table(name = "assets", indexes = {
        @Index(name = "idx_assets_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_assets_name", columnList = "name")
})
@EntityListeners(AuditListener.class)
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AssetEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String name;
    
    @Column(name = "mac_address", length = 17)
    private String macAddress;
}
    */

public class AssetEntity { //extends BaseEntity {

    //@Column(nullable = false, unique = true, length = 255)
    private String name;
    
    //@Column(name = "mac_address", length = 17)
    private String macAddress;
}