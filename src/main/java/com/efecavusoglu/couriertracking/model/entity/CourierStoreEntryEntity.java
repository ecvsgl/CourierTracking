package com.efecavusoglu.couriertracking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CourierStoreEntryEntity that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(courierId, that.courierId) && Objects.equals(store, that.store) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courierId, store, timestamp);
    }

    @Override
    public String toString() {
        return "CourierStoreEntryEntity{" +
                "id=" + id +
                ", courierId='" + courierId + '\'' +
                ", store=" + store +
                ", timestamp=" + timestamp +
                '}';
    }

    //Builder pattern
    private CourierStoreEntryEntity(Builder builder) {
        this.courierId = builder.courierId;
        this.store = builder.store;
        this.timestamp = builder.timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String courierId;
        private StoreEntity store;
        private LocalDateTime timestamp;

        public Builder() {}

        public Builder courierId(String courierId) {
            this.courierId = courierId;
            return this;
        }

        public Builder store(StoreEntity store) {
            this.store = store;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public CourierStoreEntryEntity build() {
            return new CourierStoreEntryEntity(this);
        }
    }
}
