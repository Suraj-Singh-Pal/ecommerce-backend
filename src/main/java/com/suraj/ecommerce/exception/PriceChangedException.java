
package com.suraj.ecommerce.exception;

import java.util.List;
import java.util.Map;

public class PriceChangedException extends RuntimeException {

    private final List<Map<String, Object>> priceChanges;

    public PriceChangedException(
            String message,
            List<Map<String, Object>> priceChanges
    ) {
        super(message);
        this.priceChanges = priceChanges;
    }

    public List<Map<String, Object>> getPriceChanges() {
        return priceChanges;
    }
}