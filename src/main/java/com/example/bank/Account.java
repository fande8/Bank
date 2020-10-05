package com.example.bank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Account {
    private @Id @GeneratedValue
    Long id;

    private String name;
    private Money balance;
    private final Boolean treasury;

    public Account(String name, Money balance, Boolean treasury) {
        this.name = name;
        this.balance = balance;
        this.treasury = treasury;
    }

    Account() {
        treasury = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getTreasury() {
        return treasury;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }
}
