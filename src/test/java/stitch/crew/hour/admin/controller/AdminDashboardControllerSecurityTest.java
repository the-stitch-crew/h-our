package stitch.crew.hour.admin.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import stitch.crew.hour.admin.dto.AdminDashboardResponse;
import stitch.crew.hour.admin.service.AdminDashboardService;
import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.SecurityConfig;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.service.UserService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminDashboardController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class
})
@AutoConfigureMockMvc
@DisplayName("AdminDashboardController 보안 설정은")
class AdminDashboardControllerSecurityTest {

    private static final String URL = "/api/admin/dashboard";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminDashboardService adminDashboardService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("인증이 없으면 401을 반환한다")
    void it_rejects_without_authentication() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
    }

    @Test
    @DisplayName("USER 권한이면 403을 반환한다")
    void it_rejects_user_role() throws Exception {
        String token = "valid-user-token";
        given(jwtTokenProvider.validate(token)).willReturn(true);
        given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
        given(userService.loadCurrentUserByEmail("user@test.com"))
                .willReturn(new CurrentUser(
                        1L,
                        "user@test.com",
                        Role.USER
                        )
                );

        mockMvc.perform(get(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
    }

    @Test
    @DisplayName("ADMIN 권한이면 200을 반환한다")
    void it_allows_admin_role() throws Exception {
        String token = "valid-admin-token";
        given(jwtTokenProvider.validate(token)).willReturn(true);
        given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
        given(userService.loadCurrentUserByEmail("admin@test.com"))
                .willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
        given(adminDashboardService.getDashboardInfo()).willReturn(emptyDashboardResponse());

        mockMvc.perform(get(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(SuccessCode.ADMIN_DASHBOARD_READ.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.ADMIN_DASHBOARD_READ.getSuccessMessage()));
    }

    private AdminDashboardResponse emptyDashboardResponse() {
        return new AdminDashboardResponse(
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                List.of(),
                List.of()
        );
    }
}
