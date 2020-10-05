package com.example.bank;

import javax.persistence.Embeddable;

@Embeddable
public class Money {
    private Currency currency;
    private int amount;

    Money(Currency currency, int amount) {
        this.currency = currency;
        this.amount = amount;
    }

    Money() {}

    public Currency getCurrency() {
        return currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
