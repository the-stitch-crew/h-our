package stitch.crew.hour.category.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.category.dto.AdminCategoryDetailResponse;
import stitch.crew.hour.category.dto.AdminCategorySearchResponse;
import stitch.crew.hour.category.service.CategoryAdminService;
import stitch.crew.hour.category.service.CategoryService;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.SecurityConfig;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryAdminController.class)
@Import({
	SecurityConfig.class,
	JwtAuthenticationFilter.class,
	JwtAuthenticationEntryPoint.class,
	JwtAccessDeniedHandler.class
})
@AutoConfigureMockMvc
@DisplayName("CategoryAdminController 보안 설정은")
class CategoryAdminControllerSecurityTest {

	private static final String BASE_URL = "/api/admin/categories";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CategoryAdminService categoryAdminService;

	@MockitoBean
	private CategoryService categoryService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private UserService userService;

	@Test
	@DisplayName("It : 인증이 없으므로 카테고리 목록 조회 시 401을 반환")
	void It_인증이_없으므로_카테고리_목록_조회_시_401을_반환() throws Exception {
		mockMvc.perform(get(BASE_URL))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 카테고리 목록 조회 시 403을 반환")
	void It_어드민_권한이_없으므로_카테고리_목록_조회_시_403을_반환() throws Exception {
		String token = "valid-user-token";
		givenAuthentication(token, Role.USER);

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
	@DisplayName("It : 어드민 권한으로 카테고리 목록을 조회")
	void It_어드민_권한으로_카테고리_목록을_조회() throws Exception {
		String token = "valid-admin-token";
		Page<AdminCategorySearchResponse> response = new PageImpl<>(List.of());

		givenAuthentication(token, Role.ADMIN);
		given(categoryAdminService.getCategories(anyInt(), anyInt(), any(), any())).willReturn(response);

		mockMvc.perform(
				get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_READ.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_READ.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 카테고리 상세 조회 시 401을 반환")
	void It_인증이_없으므로_카테고리_상세_조회_시_401을_반환() throws Exception {
		mockMvc.perform(get(BASE_URL + "/{categoryId}", 1L))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 카테고리 상세 조회 시 403을 반환")
	void It_어드민_권한이_없으므로_카테고리_상세_조회_시_403을_반환() throws Exception {
		String token = "valid-user-token";
		givenAuthentication(token, Role.USER);

		mockMvc.perform(
				get(BASE_URL + "/{categoryId}", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 카테고리 상세를 조회")
	void It_어드민_권한으로_카테고리_상세를_조회() throws Exception {
		String token = "valid-admin-token";

		givenAuthentication(token, Role.ADMIN);
		given(categoryAdminService.getCategory(1L)).willReturn(detailResponse());

		mockMvc.perform(
				get(BASE_URL + "/{categoryId}", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_READ.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_READ.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 카테고리 생성 시 401을 반환")
	void It_인증이_없으므로_카테고리_생성_시_401을_반환() throws Exception {
		mockMvc.perform(
				multipart(BASE_URL)
					.file(requestPart())
			)
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 카테고리 생성 시 403을 반환")
	void It_어드민_권한이_없으므로_카테고리_생성_시_403을_반환() throws Exception {
		String token = "valid-user-token";
		givenAuthentication(token, Role.USER);

		mockMvc.perform(
				multipart(BASE_URL)
					.file(requestPart())
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 카테고리를 생성")
	void It_어드민_권한으로_카테고리를_생성() throws Exception {
		String token = "valid-admin-token";
		givenAuthentication(token, Role.ADMIN);

		mockMvc.perform(
				multipart(BASE_URL)
					.file(requestPart())
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_CREATED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_CREATED.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 카테고리 수정 시 401을 반환")
	void It_인증이_없으므로_카테고리_수정_시_401을_반환() throws Exception {
		mockMvc.perform(
				multipart(BASE_URL + "/{categoryId}", 1L)
					.file(requestPart())
					.with(request -> {
						request.setMethod("PUT");
						return request;
					})
			)
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 카테고리 수정 시 403을 반환")
	void It_어드민_권한이_없으므로_카테고리_수정_시_403을_반환() throws Exception {
		String token = "valid-user-token";
		givenAuthentication(token, Role.USER);

		mockMvc.perform(
				multipart(BASE_URL + "/{categoryId}", 1L)
					.file(requestPart())
					.with(request -> {
						request.setMethod("PUT");
						return request;
					})
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 카테고리를 수정")
	void It_어드민_권한으로_카테고리를_수정() throws Exception {
		String token = "valid-admin-token";
		givenAuthentication(token, Role.ADMIN);

		mockMvc.perform(
				multipart(BASE_URL + "/{categoryId}", 1L)
					.file(requestPart())
					.with(request -> {
						request.setMethod("PUT");
						return request;
					})
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_UPDATED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_UPDATED.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 카테고리 삭제 시 401을 반환")
	void It_인증이_없으므로_카테고리_삭제_시_401을_반환() throws Exception {
		mockMvc.perform(delete(BASE_URL + "/{categoryId}", 1L))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 카테고리 삭제 시 403을 반환")
	void It_어드민_권한이_없으므로_카테고리_삭제_시_403을_반환() throws Exception {
		String token = "valid-user-token";
		givenAuthentication(token, Role.USER);

		mockMvc.perform(
				delete(BASE_URL + "/{categoryId}", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 카테고리를 삭제")
	void It_어드민_권한으로_카테고리를_삭제() throws Exception {
		String token = "valid-admin-token";
		givenAuthentication(token, Role.ADMIN);

		mockMvc.perform(
				delete(BASE_URL + "/{categoryId}", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_DELETED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_DELETED.getSuccessMessage()));
	}

	private void givenAuthentication(
		String token,
		Role role
	) {
		String email = role.name().toLowerCase() + "@test.com";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody(email));
		given(userService.loadCurrentUserByEmail(email))
			.willReturn(new CurrentUser(1L, email, role));
	}

	private MockMultipartFile requestPart() {
		return new MockMultipartFile(
			"request",
			"",
			MediaType.APPLICATION_JSON_VALUE,
			"""
				{
				  "name": "가방"
				}
				""".getBytes(StandardCharsets.UTF_8)
		);
	}

	private AdminCategoryDetailResponse detailResponse() {
		return new AdminCategoryDetailResponse(
			1L,
			"가방",
			null,
			0L,
			0L,
			0L,
			0L,
			0L,
			0L,
			LocalDateTime.of(2026, 7, 9, 1, 0),
			LocalDateTime.of(2026, 7, 9, 1, 0),
			null
		);
	}
}
