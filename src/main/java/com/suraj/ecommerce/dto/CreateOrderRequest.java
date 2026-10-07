
package com.suraj.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;

public class CreateOrderRequest {

    @NotBlank(message = "Shipping address is required")
    @Size(
            min = 10,
            max = 500,
            message = "Shipping address must be between 10 and 500 characters"
    )
    private String shippingAddress;

    private boolean confirmPriceChanges = false;

private Map<Long, BigDecimal> confirmedPrices;

public Map<Long, BigDecimal> getConfirmedPrices() {
    return confirmedPrices;
}

public void setConfirmedPrices(Map<Long, BigDecimal> confirmedPrices) {
    this.confirmedPrices = confirmedPrices;
}

    public CreateOrderRequest() {
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public boolean isConfirmPriceChanges() {
        return confirmPriceChanges;
    }

    public void setConfirmPriceChanges(boolean confirmPriceChanges) {
        this.confirmPriceChanges = confirmPriceChanges;
    }
}