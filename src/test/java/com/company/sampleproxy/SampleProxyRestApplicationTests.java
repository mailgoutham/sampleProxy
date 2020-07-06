package com.company.sampleproxy;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = SampleProxyRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SampleProxyRestApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	CamelContext camelContext;

	@Test
	public void postASuccessfulBook() throws IOException {
		//given
		String payload = IOUtils.toString(
					this.getClass().getResourceAsStream("/book.json"),
					"UTF-8"
			);
		HttpEntity<String> request = new HttpEntity(payload);
		//when
		ResponseEntity<String> response = restTemplate.postForEntity("/api/book", request, String.class);

		//then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("ResponseApi{code=200, message='Success'}");
	}


	@Test
	public void postInvalidRequestValidationFailure() throws IOException {

		//given
		String payload = IOUtils.toString(
				this.getClass().getResourceAsStream("/bookValidationFailure.json"),
				"UTF-8"
		);

		HttpEntity<String> request = new HttpEntity(payload);

		//when
		ResponseEntity<String> response = restTemplate.postForEntity("/api/book", request, String.class);

		//then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(response.getBody()).isEqualTo("ResponseApi{code=422, message='Some of the input fields have failed validation'}");
	}

	@Test
	public void postServerFailure() throws Exception {

		//given

		RouteDefinition routeDefinition = camelContext.getRouteDefinition(RestRouteBuilder.PROCESS_BOOK_ROUTE_ID);
		routeDefinition.adviceWith(camelContext, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				weaveById(RestRouteBuilder.FRONT_END_TO_BACK_END_TRANSFORMER_ID).replace().throwException(new Exception());
			}
		});

		String payload = IOUtils.toString(
				this.getClass().getResourceAsStream("/book.json"),
				"UTF-8"
		);

		HttpEntity<String> request = new HttpEntity(payload);


		//when
		ResponseEntity<String> response = restTemplate.postForEntity("/api/book", request, String.class);

		//then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).isEqualTo("ResponseApi{code=500, message='Server Error'}");
	}

}
