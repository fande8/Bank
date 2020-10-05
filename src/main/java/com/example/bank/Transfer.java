package com.example.bank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Transfer {
    private @Id @GeneratedValue
    Long id;

    private Long source;
    private Long destination;
    private int amount;
    private State state = State.Pending;

    public Transfer(Long source, Long destination, int amount) {
        this.source = source;
        this.destination = destination;
        this.amount = amount;
    }

    public Transfer() {}

    public Long getId() {
        return id;
    }

    public Long getSource() { return source; }

    public Long getDestination() {
        return destination;
    }

    public int getAmount() { return amount; }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}