package ecommerce.controller;

import ecommerce.rmi.Restaurant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.rmi.Naming;

@RestController
public class RestaurantController {

    @Value("${restaurant.rmi.url}")
    private String restaurantRmiUrl;

    @GetMapping("/restaurant/order")
    public String orderFood() {
        try {
            Restaurant restaurant = (Restaurant) Naming.lookup(restaurantRmiUrl);
            // call rmi
            return restaurant.orderFood("burger");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}