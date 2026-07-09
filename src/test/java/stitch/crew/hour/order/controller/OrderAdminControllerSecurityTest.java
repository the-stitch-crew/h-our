package stitch.crew.hour.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.SecurityConfig;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.dto.AdminOrderDetailResponse;
import stitch.crew.hour.order.dto.AdminOrderSearchResponse;
import stitch.crew.hour.order.service.OrderAdminService;
import stitch.crew.hour.order.service.OrderService;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(OrderAdminController.class)
@Import({
	SecurityConfig.class,
	JwtAuthenticationFilter.class,
	JwtAuthenticationEntryPoint.class,
	JwtAccessDeniedHandler.class
})
@AutoConfigureMockMvc
@DisplayName("OrderAdminController 보안 설정은")
class OrderAdminControllerSecurityTest {

	private static final String BASE_URL = "/api/admin/orders";
	private static final UUID ORDER_NUMBER = UUID.fromString("11111111-1111-1111-1111-111111111111");

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderAdminService orderAdminService;

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private UserService userService;

	@Test
	@DisplayName("It : 인증이 없으므로 주문 목록 조회 시 401을 반환")
	void it_rejects_get_orders_without_authentication() throws Exception {
		mockMvc.perform(get(BASE_URL))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 주문 목록 조회 시 403을 반환")
	void it_rejects_get_orders_without_admin_authority() throws Exception {
		String token = "valid-user-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
		given(userService.loadCurrentUserByEmail("user@test.com"))
			.willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

		mockMvc.perform(
				get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 주문 목록을 조회")
	void it_returns_get_orders_with_admin_authority() throws Exception {
		String token = "valid-admin-token";
		Page<AdminOrderSearchResponse> response = new PageImpl<>(List.of());

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
		given(userService.loadCurrentUserByEmail("admin@test.com"))
			.willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
		given(orderAdminService.getOrders(anyInt(), anyInt(), any())).willReturn(response);

		mockMvc.perform(
				get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.ORDER_READ_SUCCESS.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.ORDER_READ_SUCCESS.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 주문 상세 조회 시 401을 반환")
	void it_rejects_get_order_detail_without_authentication() throws Exception {
		mockMvc.perform(get(BASE_URL + "/{orderNumber}", ORDER_NUMBER))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 주문 상세 조회 시 403을 반환")
	void it_rejects_get_order_detail_without_admin_authority() throws Exception {
		String token = "valid-user-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
		given(userService.loadCurrentUserByEmail("user@test.com"))
			.willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

		mockMvc.perform(
				get(BASE_URL + "/{orderNumber}", ORDER_NUMBER)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 주문 상세를 조회")
	void it_returns_get_order_detail_with_admin_authority() throws Exception {
		String token = "valid-admin-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
		given(userService.loadCurrentUserByEmail("admin@test.com"))
			.willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
		given(orderAdminService.getOrder(ORDER_NUMBER)).willReturn(emptyDetailResponse());

		mockMvc.perform(
				get(BASE_URL + "/{orderNumber}", ORDER_NUMBER)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.ORDER_READ_SUCCESS.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.ORDER_READ_SUCCESS.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 주문 취소 시 401을 반환")
	void it_rejects_cancel_order_without_authentication() throws Exception {
		mockMvc.perform(patch(BASE_URL + "/{orderNumber}/cancel", ORDER_NUMBER))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 주문 취소 시 403을 반환")
	void it_rejects_cancel_order_without_admin_authority() throws Exception {
		String token = "valid-user-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
		given(userService.loadCurrentUserByEmail("user@test.com"))
			.willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

		mockMvc.perform(
				patch(BASE_URL + "/{orderNumber}/cancel", ORDER_NUMBER)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 주문을 취소")
	void it_returns_cancel_order_with_admin_authority() throws Exception {
		String token = "valid-admin-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
		given(userService.loadCurrentUserByEmail("admin@test.com"))
			.willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));

		mockMvc.perform(
				patch(BASE_URL + "/{orderNumber}/cancel", ORDER_NUMBER)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.ORDER_CANCELED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.ORDER_CANCELED.getSuccessMessage()));
	}

	private AdminOrderDetailResponse emptyDetailResponse() {
		return new AdminOrderDetailResponse(
			ORDER_NUMBER,
			"ORDERED",
			0,
			0L,
			"주문자",
			"01000000000",
			"수령자",
			"01000000000",
			"주소",
			"12345",
			null,
			LocalDateTime.now(),
			List.of()
		);
	}
}
