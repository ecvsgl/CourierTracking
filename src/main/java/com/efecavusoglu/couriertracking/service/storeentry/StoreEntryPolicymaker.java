package com.efecavusoglu.couriertracking.service.storeentry;

import com.efecavusoglu.couriertracking.exception.PolicyNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class to provide a StoreEntryPolicy.
 * Strategy Pattern implementation.
 */
@Component
@RequiredArgsConstructor
public class StoreEntryPolicymaker {

    private final List<StoreEntryPolicy> storeEntryPolicies;

    public StoreEntryPolicy getStoreEntryPolicy(Class<? extends StoreEntryPolicy> policyType) {
        return storeEntryPolicies.stream()
                .filter(policyType::isInstance)
                .map(policyType::cast)
                .findFirst()
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found for type: " + policyType.getName()));
    }
}
