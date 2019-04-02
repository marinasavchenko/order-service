package com.onlinestore.orderservice.services;

import com.onlinestore.orderservice.clients.Client;
import com.onlinestore.orderservice.domain.Order;
import com.onlinestore.orderservice.repositories.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

	private static final String ANY_ORDER_ID = "741";
	private static final String ANY_CUSTOMER_ID = "1234";

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private Client customerClient;

	@Mock
	private Order order;

	private OrderService orderService;

	@Before
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