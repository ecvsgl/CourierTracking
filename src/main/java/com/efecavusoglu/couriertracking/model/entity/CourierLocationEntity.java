package com.efecavusoglu.couriertracking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "courier_locations")
@Getter
@Setter
@NoArgsConstructor
public class CourierLocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courierId;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CourierLocationEntity that)) return false;
        return Double.compare(latitude, that.latitude) == 0 && Double.compare(longitude, that.longitude) == 0 && Objects.equals(id, that.id) && Objects.equals(courierId, that.courierId) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courierId, latitude, longitude, timestamp);
    }

    @Override
    public String toString() {
        return "CourierLocationEntity{" +
                "id=" + id +
                ", courierId='" + courierId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                '}';
    }

    //Builder Pattern
    private CourierLocationEntity(Builder builder) {
        this.courierId = builder.courierId;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.timestamp = builder.timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String courierId;
        private double latitude;
        private double longitude;
        private LocalDateTime timestamp;

        public Builder() {}

        public Builder courierId(String courierId) {
            this.courierId = courierId;
            return this;
        }

        public Builder latitude(double lat) {
            this.latitude = lat;
            return this;
        }

        public Builder longitude(double lng) {
            this.longitude = lng;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public CourierLocationEntity build() {
            return new CourierLocationEntity(this);
        }
    }
}
