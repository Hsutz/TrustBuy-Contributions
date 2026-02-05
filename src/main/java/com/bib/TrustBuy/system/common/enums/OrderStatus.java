package com.bib.TrustBuy.system.common.enums;

public enum OrderStatus {
    PENDING,    // default after checkout
    CANCELLED,  // user cancels before seller confirms
    PROCESSING, // for mixed states
    CONFIRMED,  // seller confirms
    SHIPPED,    // seller ships
    DELIVERED   // seller marks delivered
}
