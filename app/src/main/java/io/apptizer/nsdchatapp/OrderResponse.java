package io.apptizer.nsdchatapp;

import java.util.List;

public class OrderResponse {
    private List<Order> orders;

    public OrderResponse() {
    }

    public OrderResponse(List<Order> orders) {
        this.orders = orders;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orders=" + orders +
                '}';
    }
}
