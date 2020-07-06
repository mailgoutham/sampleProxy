package com.company.sampleproxy;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component()
public class RestRouteBuilder extends RouteBuilder {

    public static final String FRONT_END_TO_BACK_END_TRANSFORMER_ID = "frontEndToBackEndTransformerId";
    public static final String PROCESS_BOOK_ROUTE_ID = "processBookRouteId";

    @Autowired
    ResponseApiBuilder responseApiBuilder;

    @Override
    public void configure() {
        restConfiguration()
                .component("servlet")
                .dataFormatProperty("prettyPrint", "true")
                .bindingMode(RestBindingMode.json)
                .apiContextPath("/swagger")
                .apiContextRouteId("swagger")
                .contextPath("/api")
                .apiProperty("api.title", "Sample Proxy Service")
                .apiProperty("api.version", "1.0");

        XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
        xmlJsonFormat.setForceTopLevelObject(true);
        xmlJsonFormat.setTrimSpaces(true);
        xmlJsonFormat.setRootName("Book");

        onException(JsonValidationException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(UNPROCESSABLE_ENTITY.value()))
                .bean("responseApiBuilder","clientValidationFailure()");

        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(INTERNAL_SERVER_ERROR.value()))
                .bean("responseApiBuilder","serverError()");


        rest("/book")
                .post().consumes("application/json").bindingMode(RestBindingMode.json)
                .responseMessage().code(200).message("Send Book Successful").responseModel(ResponseApi.class).endResponseMessage()
                .responseMessage().code(422).message("Client Validation Failure").responseModel(ResponseApi.class).endResponseMessage()
                .responseMessage().code(500).message("Server Error").responseModel(ResponseApi.class).endResponseMessage()
                .route().routeId("sendBook")
                .to("direct:processBook");

        from("direct:processBook")
                .routeId(PROCESS_BOOK_ROUTE_ID)
                .marshal().json(JsonLibrary.Jackson)
                .to("json-validator:frontEndBookSchema.json")
                .unmarshal(xmlJsonFormat).id(FRONT_END_TO_BACK_END_TRANSFORMER_ID)
                .log(LoggingLevel.INFO, "${body}")
                .to("validator:backendSchema.xsd")
                .bean("responseApiBuilder","postSuccessful()");


    }
}
