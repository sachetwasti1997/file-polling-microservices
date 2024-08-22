package com.camel.file_polling_microservice.file_tests;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
public class LegacyRouteFileTests {

    @Autowired
    CamelContext context;

    @EndpointInject("mock:result")
    protected MockEndpoint mockEndpoint;

    @Autowired
    ProducerTemplate producerTemplate;

    @Test
    public void testFileMove() throws Exception {
        //setup the mock
        String expectedBody = "This is an input file that will be processed and move to output file directory";
        mockEndpoint.expectedBodiesReceived(expectedBody);
        mockEndpoint.expectedMinimumMessageCount(1);

        //tweak the route definitions
        AdviceWith.adviceWith(context, "legacy-file-move-route-id", routeBuilder -> {
            routeBuilder.weaveByToUri("file:*").replace().to(mockEndpoint);
        });

        //start the context and validate if mock is asserted
        context.start();
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testFileMoveByMockingFromEndpoint() throws Exception {
        // setup the mock
        String expectedBody = "This is the mock data after mocking the from endpoint";
        mockEndpoint.expectedBodiesReceived(expectedBody);
        mockEndpoint.expectedMinimumMessageCount(1);

        // tweak the route endpoint
        AdviceWith.adviceWith(context, "legacy-file-move-route-id", routeBuilder -> {
            routeBuilder.replaceFromWith("direct:mock-start");
            //direct is an endpoint which does not have any other component other than sending the data directly
            // we want to send the data directly
            routeBuilder.weaveByToUri("file:*").replace().to(mockEndpoint);
        });

        // start the context and validate if the mock is working
        context.start();
        producerTemplate.sendBody("direct:mock-start", "This is the mock data after mocking the from endpoint");
        mockEndpoint.assertIsSatisfied();
    }

}
