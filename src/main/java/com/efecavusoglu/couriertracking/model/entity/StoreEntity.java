package com.efecavusoglu.couriertracking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StoreEntity that)) return false;
        return Double.compare(latitude, that.latitude) == 0 && Double.compare(longitude, that.longitude) == 0 && Objects.equals(id, that.id) && Objects.equals(storeName, that.storeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storeName, latitude, longitude);
    }

    @Override
    public String toString() {
        return "StoreEntity{" +
                "id=" + id +
                ", storeName='" + storeName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    //Builder Pattern
    private StoreEntity(Builder builder) {
        this.storeName = builder.storeName;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String storeName;
        private double latitude;
        private double longitude;

        public Builder() {}

        public Builder storeName(String storeName) {
            this.storeName = storeName;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double lng) {
            this.longitude = lng;
            return this;
        }

        public StoreEntity build() {
            return new StoreEntity(this);
        }
    }
}
