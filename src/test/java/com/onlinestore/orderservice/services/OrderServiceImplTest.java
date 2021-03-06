package com.onlinestore.orderservice.services;

import com.onlinestore.orderservice.clients.Client;
import com.onlinestore.orderservice.domain.Customer;
import com.onlinestore.orderservice.domain.Order;
import com.onlinestore.orderservice.exceptions.OrderNotFoundException;
import com.onlinestore.orderservice.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

	private static final String ANY_ORDER_ID = "741";
	private static final String ANY_CUSTOMER_ID = "1234";

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private Client customerClient;

	@Spy
	private Order order;

	@Spy
	private Customer customer;

	private OrderService orderService;

	@BeforeEach
	public void setUp() throws Exception {
		orderService = new OrderServiceImpl(orderRepository, customerClient);
	}

	@Test
	public void shouldGetAllOrders() throws Exception {
		orderService.getAllOrders();
		verify(orderRepository).findAll();
	}

	@Test
	public void shouldGetOrdersByCustomerId() throws Exception {
		orderService.getOrdersByCustomerId(ANY_CUSTOMER_ID);
		verify(orderRepository).findByCustomerId(ANY_CUSTOMER_ID);
	}

	@Test
	public void shouldGetSpecifiedOrder() throws Exception {
		when(orderRepository.findByCustomerIdAndOrderId(ANY_CUSTOMER_ID, ANY_CUSTOMER_ID)).thenReturn(Optional.of(order));
		when(customerClient.getCustomer(ANY_CUSTOMER_ID)).thenReturn(customer);

		orderService.getOrder(ANY_CUSTOMER_ID, ANY_CUSTOMER_ID);

		verify(orderRepository).findByCustomerIdAndOrderId(ANY_CUSTOMER_ID, ANY_CUSTOMER_ID);
	}

	@Test
	public void shouldThrowExceptionWhenEmptyOptional() throws Exception {
		when(orderRepository.findByCustomerIdAndOrderId(ANY_CUSTOMER_ID, ANY_CUSTOMER_ID)).thenReturn(Optional.empty());

		assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(ANY_CUSTOMER_ID, ANY_CUSTOMER_ID));
	}

	@Test
	public void shouldAddCustomerInfoToOrder() throws Exception {
		when(orderRepository.findByCustomerIdAndOrderId(ANY_CUSTOMER_ID, ANY_CUSTOMER_ID)).thenReturn(Optional.of(order));
		when(customerClient.getCustomer(ANY_CUSTOMER_ID)).thenReturn(customer);

		orderService.getOrder(ANY_CUSTOMER_ID, ANY_CUSTOMER_ID);

		verify(customerClient).getCustomer(ANY_CUSTOMER_ID);
		verify(order).withCustomerAddress(any());
		verify(order).withCustomerName(any());
	}

	@Test
	public void shouldSaveOrder() throws Exception {
		orderService.saveOrder(order);
		verify(orderRepository).save(order);
	}

	@Test
	public void shouldDeleteOrder() throws Exception {
		orderService.deleteOrder(ANY_ORDER_ID);
		verify(orderRepository).deleteById(ANY_ORDER_ID);
	}

}