package com.camel.file_polling_microservice.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "name_address")
@NamedQuery(name = "fetchAllRows", query = "Select x from NameAddress x")
public class NameAddress implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNo;
    private String city;
    private String postalCode;
    private String country;
}
