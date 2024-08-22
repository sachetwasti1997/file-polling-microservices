package com.camel.file_polling_microservice.dto;

import lombok.Data;

@Data
public class NameAddress {
    private String name;
    private String phoneNo;
    private String city;
    private String postalCode;
    private String country;
}
