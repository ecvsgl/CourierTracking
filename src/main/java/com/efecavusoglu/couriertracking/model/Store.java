package com.efecavusoglu.couriertracking.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Objects;

@JsonDeserialize(builder = Store.Builder.class)
public class Store {
    private String storeName;
    private double latitude;
    private double longitude;

    //builder pattern -- delegating object creation to the builder alone.
    private Store(Builder builder) {
        this.storeName = builder.storeName;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
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

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Store build() {
            return new Store(this);
        }
    }

    public String getStoreName() {
        return storeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Store store)) return false;
        return Double.compare(latitude, store.latitude) == 0 && Double.compare(longitude, store.longitude) == 0 && Objects.equals(storeName, store.storeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, latitude, longitude);
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeName='" + storeName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
