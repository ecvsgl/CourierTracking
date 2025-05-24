package com.efecavusoglu.couriertracking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "courier_store_entries")
@Getter
@Setter
@NoArgsConstructor
public class CourierStoreEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private StoreEntity store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="courier_location_id", nullable = false, unique = true)
    private CourierLocationEntity courierLocation;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CourierStoreEntryEntity that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(courierId, that.courierId) && Objects.equals(store, that.store) && Objects.equals(courierLocation, that.courierLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courierId, store, courierLocation);
    }

    @Override
    public String toString() {
        return "CourierStoreEntryEntity{" +
                "id=" + id +
                ", courierId='" + courierId + '\'' +
                ", store=" + store +
                ", courierLocation=" + courierLocation +
                '}';
    }

    //Builder pattern
    private CourierStoreEntryEntity(Builder builder) {
        this.courierId = builder.courierId;
        this.store = builder.store;
        this.courierLocation = builder.courierLocation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String courierId;
        private StoreEntity store;
        private CourierLocationEntity courierLocation;

        public Builder() {}

        public Builder courierId(String courierId) {
            this.courierId = courierId;
            return this;
        }

        public Builder store(StoreEntity store) {
            this.store = store;
            return this;
        }

        public Builder courierLocation(CourierLocationEntity courierLocation) {
            this.courierLocation = courierLocation;
            return this;
        }
    }
}
