package com.camel.file_polling_microservice.utility;

import com.camel.file_polling_microservice.dto.NameAddress;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomObjectsGenerator {
    private static final String characters = "abcdefghijklmnopqrstuvwxyz";
    private static final String numbers = "1234567890";

    public NameAddress generateNameAddress() {
        Random random = new Random();
        int firstNameLength = random.nextInt(8);
        int lastNameLength = random.nextInt(8);

        StringBuilder name = new StringBuilder();
        createFields(name, firstNameLength, random, characters);
        name.append(" ");
        createFields(name, lastNameLength, random, characters);

        NameAddress nameAddress = new NameAddress();
//        nameAddress.setId(random.nextLong());
        nameAddress.setName(name.toString());

        StringBuilder phone = new StringBuilder();
        createFields(phone, 11, random, numbers);
        nameAddress.setPhoneNo(phone.toString());

        StringBuilder city = new StringBuilder();
        createFields(city, 8, random, characters);
        nameAddress.setCity(city.toString());

        StringBuilder postalCode = new StringBuilder();
        createFields(postalCode, 6, random, numbers);
        nameAddress.setPostalCode(postalCode.toString());

        StringBuilder country = new StringBuilder();
        createFields(country, 8, random, characters);
        nameAddress.setCountry(country.toString());

        return nameAddress;
    }

    private void createFields(StringBuilder name, int length, Random random, String characters) {
        if (characters.charAt(0) != '1')
            name.append(Character.toUpperCase(characters.charAt(random.nextInt(characters.length()))));
        for (int i=0; i<length-1; i++) {
            name.append(characters.charAt(random.nextInt(characters.length())));
        }
    }
}
