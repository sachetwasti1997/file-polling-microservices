package com.camel.file_polling_microservice.processor;

import com.camel.file_polling_microservice.dto.NameAddress;
import com.camel.file_polling_microservice.dto.OutBoundNameAddress;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundMessageProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(InboundMessageProcessor.class);
    @Override
    public void process(Exchange exchange) throws Exception {
        NameAddress fileData = exchange.getIn().getBody(NameAddress.class);
        LOGGER.info("The file data read is "+fileData);
        exchange.getIn().setBody(new OutBoundNameAddress(fileData.getName(), readOutBoundAddress(fileData)));
    }

    private String readOutBoundAddress(NameAddress nameAddress) {
        return "Phone: " +
                nameAddress.getPhoneNo() +
                ", " + nameAddress.getCity() + ", PIN: " +
                nameAddress.getPostalCode() + ", " +
                nameAddress.getCountry();
    }
}
