package com.bib.TrustBuy.system.exception;

public class NoItemsSelectedException extends RuntimeException {
    public NoItemsSelectedException() {
        super("No selected items to checkout");
    }
}
