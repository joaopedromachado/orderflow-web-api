package br.com.orderflow.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

@Entity
@Table(name = "tb_address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id")
    private UUID addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "cep")
    private String postalCode;

    private String street;

    private String complement;

    private String neighborhood;

    private String city;

    private String state;

    private String region;

    private String number;

    private boolean defaultAddress;

    @PrePersist
    void onCreate() {
        if (ObjectUtils.isEmpty(this.complement)) this.complement = "N/A";
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", userId=" + user.getUserId() +
                ", postalCode='" + postalCode + '\'' +
                ", street='" + street + '\'' +
                ", complement='" + complement + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", region='" + region + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    public Address(Builder builder) {
        this.addressId = builder.addressId;
        this.user = builder.user;
        this.postalCode = builder.postalCode;
        this.street = builder.street;
        this.complement = builder.complement;
        this.neighborhood = builder.neighborhood;
        this.city = builder.city;
        this.state = builder.state;
        this.region = builder.region;
        this.number = builder.number;
        this.defaultAddress = builder.defaultAddress;
    }

    public Address() {
    }

    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public static class Builder {
        private UUID addressId;
        private User user;
        private String postalCode;
        private String street;
        private String complement;
        private String neighborhood;
        private String city;
        private String state;
        private String region;
        private String number;
        private boolean defaultAddress;

        public Builder addressId(UUID addressId) {
            this.addressId = addressId;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder complement(String complement) {
            this.complement = complement;
            return this;
        }

        public Builder neighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder defaultAddress(boolean defaultAddress) {
            this.defaultAddress = defaultAddress;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}
