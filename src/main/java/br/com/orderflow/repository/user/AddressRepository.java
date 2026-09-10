package br.com.orderflow.repository.user;

import br.com.orderflow.domain.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    Optional<Address> findByAddressIdAndUserUserId(UUID addressId, UUID userId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Address address
            SET address.defaultAddress = false
            WHERE address.user.userId = :userId
              AND address.defaultAddress = true
            """)
    void clearDefaultAddressForUser(@Param("userId") UUID userId);
}
