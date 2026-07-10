package stitch.crew.hour.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.SecurityConfig;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.product.dto.AdminProductDetailResponse;
import stitch.crew.hour.product.dto.AdminProductSearchResponse;
import stitch.crew.hour.product.service.ProductAdminService;
import stitch.crew.hour.product.service.ProductService;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(ProductAdminController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class
})
@AutoConfigureMockMvc
@DisplayName("ProductAdminController 보안 설정은")
class ProductAdminControllerSecurityTest {

    private static final String BASE_URL = "/api/admin/products";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductAdminService productAdminService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("It : 인증이 없으므로 상품 목록 조회 시 401을 반환")
    void it_rejects_get_products_without_authentication() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
    }

    @Test
    @DisplayName("It : 어드민 권한이 없으므로 상품 목록 조회 시 403을 반환")
    void it_rejects_get_products_without_admin_authority() throws Exception {
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
    @DisplayName("It : 어드민 권한으로 상품 목록을 조회")
    void it_returns_get_products_with_admin_authority() throws Exception {
        String token = "valid-admin-token";
        Page<AdminProductSearchResponse> response = new PageImpl<>(List.of());

        given(jwtTokenProvider.validate(token)).willReturn(true);
        given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
        given(userService.loadCurrentUserByEmail("admin@test.com"))
                .willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
        given(productAdminService.getProducts(anyInt(), anyInt(), any(), any(), any(), any())).willReturn(response);

        mockMvc.perform(
                        get(BASE_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(SuccessCode.PRODUCT_READ_SUCCESS.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PRODUCT_READ_SUCCESS.getSuccessMessage()));
    }

    @Test
    @DisplayName("It : 인증이 없으므로 상품 상세 조회 시 401을 반환")
    void it_rejects_get_product_detail_without_authentication() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{productId}", 1L))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
    }

    @Test
    @DisplayName("It : 어드민 권한이 없으므로 상품 상세 조회 시 403을 반환")
    void it_rejects_get_product_detail_without_admin_authority() throws Exception {
        String token = "valid-user-token";

        given(jwtTokenProvider.validate(token)).willReturn(true);
        given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
        given(userService.loadCurrentUserByEmail("user@test.com"))
                .willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

        mockMvc.perform(
                        get(BASE_URL + "/{productId}", 1L)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }

    @Test
    @DisplayName("It : 어드민 권한으로 상품 상세를 조회")
    void it_returns_get_product_detail_with_admin_authority() throws Exception {
        String token = "valid-admin-token";

        given(jwtTokenProvider.validate(token)).willReturn(true);
        given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
        given(userService.loadCurrentUserByEmail("admin@test.com"))
                .willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
        given(productAdminService.getProduct(1L)).willReturn(emptyDetailResponse());

        mockMvc.perform(
                        get(BASE_URL + "/{productId}", 1L)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(SuccessCode.PRODUCT_READ_SUCCESS.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PRODUCT_READ_SUCCESS.getSuccessMessage()));
    }

    @Test
    @DisplayName("It : 인증이 없으므로 상품 상태 변경 시 401을 반환")
    void it_rejects_update_status_without_authentication() throws Exception {
        mockMvc.perform(
                        patch(BASE_URL + "/{productId}/status", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "status": "SOLD_OUT"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
    }

    @Test
    @DisplayName("It : 어드민 권한이 없으므로 상품 상태 변경 시 403을 반환")
    void it_rejects_update_status_without_admin_authority() throws Exception {
        String token = "valid-user-token";

        given(jwtTokenProvider.validate(token)).willReturn(true);
        given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
        given(userService.loadCurrentUserByEmail("user@test.com"))
                .willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

        mockMvc.perform(
                        patch(BASE_URL + "/{productId}/status", 1L)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "status": "SOLD_OUT"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }

    @Test
    @DisplayName("It : 어드민 권한으로 상품 상태를 변경")
    void it_returns_update_status_with_admin_authority() throws Exception {
        String token = "valid-admin-token";

        given(jwtTokenProvider.validate(token)).willReturn(true);
        given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
        given(userService.loadCurrentUserByEmail("admin@test.com"))
                .willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
        willDoNothing().given(productAdminService).updateStatus(anyLong(), any());

        mockMvc.perform(
                        patch(BASE_URL + "/{productId}/status", 1L)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "status": "SOLD_OUT"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(SuccessCode.PRODUCT_UPDATE_SUCCESS.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.PRODUCT_UPDATE_SUCCESS.getSuccessMessage()));
    }

    private AdminProductDetailResponse emptyDetailResponse() {
        return new AdminProductDetailResponse(
                1L,
                "상품",
                1_000L,
                null,
                "ACTIVATED",
                "요약",
                "설명",
                "카테고리",
                false,
                0L,
                0,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }
}
