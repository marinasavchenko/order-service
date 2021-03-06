package com.onlinestore.orderservice.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinestore.orderservice.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWireMock
public class CustomerClientTest {
	private static final String ANY_CUSTOMER_ID = "26";
	private static final String ANY_CUSTOMER_NAME = "Jane";
	private static final String ANY_CUSTOMER_CITY = "New York";
	private static final String CUSTOMER_ID_URI = "/v1/customers/" + ANY_CUSTOMER_ID;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RestTemplate restTemplate;

	private DiscoveryClient discoveryClient = mock(DiscoveryClient.class);

	private Client client;

	private String jsonCustomer;

	private List<ServiceInstance> serviceInstanceList;


	@BeforeEach
	public void setup() throws JsonProcessingException {
		client = new CustomerClient(discoveryClient, restTemplate);
		jsonCustomer = objectMapper.writeValueAsString(new Customer(ANY_CUSTOMER_ID, ANY_CUSTOMER_NAME, ANY_CUSTOMER_CITY));
		serviceInstanceList = new ArrayList<>();
		serviceInstanceList.add(new DefaultServiceInstance("", "localhost", 8080, false));
	}

	@Test
	public void shouldReturnCustomerFromCustomerService() throws Exception {
		when(discoveryClient.getInstances("customerservice")).thenReturn(serviceInstanceList);
		stubFor(get(CUSTOMER_ID_URI)
				.willReturn(aResponse()
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
						.withBody(jsonCustomer)));

		Customer customer = this.client.getCustomer(ANY_CUSTOMER_ID);
		assertThat(ANY_CUSTOMER_CITY).isEqualTo(customer.getCustomerAddress());
		assertThat(ANY_CUSTOMER_NAME).isEqualTo(customer.getCustomerName());
	}
}