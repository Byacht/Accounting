package com.example.dn.accounting;

/**
 * Created by dn on 2016/8/19.
 */

public class AccountsMessage {
    private String events;
    private float cost;

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }
}
