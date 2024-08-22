package com.camel.file_polling_microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutBoundNameAddress {
    private String name;
    private String address;
}
