package com.logistics.shipment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ShipmentApplication {

    public static void main(String[] args) {

        TimeZone.setDefault(
                TimeZone.getTimeZone("UTC")
        );

        SpringApplication.run(
                ShipmentApplication.class,
                args
        );
    }
}