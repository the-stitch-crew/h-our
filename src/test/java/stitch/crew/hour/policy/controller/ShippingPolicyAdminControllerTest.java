package stitch.crew.hour.policy.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.policy.dto.ShippingPolicyCreateRequest;
import stitch.crew.hour.policy.dto.ShippingPolicyResponse;
import stitch.crew.hour.policy.dto.ShippingPolicyUpdateRequest;
import stitch.crew.hour.policy.service.ShippingPolicyAdminService;
import stitch.crew.hour.util.TestUtil;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShippingPolicyAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ShippingPolicyAdminController 클래스의")
class ShippingPolicyAdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    private ShippingPolicyAdminService shippingPolicyAdminService;

    @MockitoBean
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockitoBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    TestingAuthenticationToken adminAuthentication = TestUtil.createAdminAuthentication("admin@test.com");

    @Test
    @DisplayName("GET /api/admin/shipping-policies 엔드포인트는 배송 정책 목록을 반환한다")
    void getShippingPolicies() throws Exception {
        given(shippingPolicyAdminService.getShippingPolicies())
                .willReturn(List.of(response(1L, 3500L, 2000L, true)));

        mockMvc.perform(get("/api/admin/shipping-policies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SuccessCode.SHIPPING_POLICY_READ.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SHIPPING_POLICY_READ.getSuccessMessage()))
                .andExpect(jsonPath("$.data[0].shippingPolicyId").value(1L))
                .andExpect(jsonPath("$.data[0].deliveryFee").value(3500L))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/admin/shipping-policies/{shippingPolicyId} 엔드포인트는 배송 정책 상세를 반환한다")
    void getShippingPolicy() throws Exception {
        given(shippingPolicyAdminService.getShippingPolicy(1L))
                .willReturn(response(1L, 3500L, 2000L, true));

        mockMvc.perform(get("/api/admin/shipping-policies/{shippingPolicyId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SuccessCode.SHIPPING_POLICY_READ.name()))
                .andExpect(jsonPath("$.data.shippingPolicyId").value(1L))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/admin/shipping-policies/{shippingPolicyId} 엔드포인트는 존재하지 않는 id면 404를 반환한다")
    void getShippingPolicyNotFound() throws Exception {
        willThrow(new BusinessException(ErrorCode.SHIPPING_POLICY_NOT_FOUND))
                .given(shippingPolicyAdminService)
                .getShippingPolicy(1L);

        mockMvc.perform(get("/api/admin/shipping-policies/{shippingPolicyId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ErrorCode.SHIPPING_POLICY_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.SHIPPING_POLICY_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("POST /api/admin/shipping-policies 엔드포인트는 배송 정책을 생성한다")
    void createShippingPolicy() throws Exception {
        ShippingPolicyCreateRequest request = new ShippingPolicyCreateRequest(3500L, 2000L, true);
        given(shippingPolicyAdminService.createShippingPolicy(request))
                .willReturn(response(1L, 3500L, 2000L, true));

        mockMvc.perform(
                        post("/api/admin/shipping-policies")
                                .with(csrf())
                                .principal(adminAuthentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SuccessCode.SHIPPING_POLICY_CREATED.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SHIPPING_POLICY_CREATED.getSuccessMessage()))
                .andExpect(jsonPath("$.data.deliveryFee").value(3500L))
                .andDo(print());
    }

    @Test
    @DisplayName("PUT /api/admin/shipping-policies/{shippingPolicyId} 엔드포인트는 배송 정책을 수정한다")
    void updateShippingPolicy() throws Exception {
        ShippingPolicyUpdateRequest request = new ShippingPolicyUpdateRequest(4000L, null, false);
        given(shippingPolicyAdminService.updateShippingPolicy(1L, request))
                .willReturn(response(1L, 4000L, null, false));

        mockMvc.perform(
                        put("/api/admin/shipping-policies/{shippingPolicyId}", 1L)
                                .with(csrf())
                                .principal(adminAuthentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SuccessCode.SHIPPING_POLICY_UPDATED.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SHIPPING_POLICY_UPDATED.getSuccessMessage()))
                .andExpect(jsonPath("$.data.deliveryFee").value(4000L))
                .andDo(print());
    }

    @Test
    @DisplayName("PATCH /api/admin/shipping-policies/{shippingPolicyId}/active 엔드포인트는 배송 정책을 활성화한다")
    void activateShippingPolicy() throws Exception {
        given(shippingPolicyAdminService.activateShippingPolicy(1L))
                .willReturn(response(1L, 3500L, 2000L, true));

        mockMvc.perform(
                        patch("/api/admin/shipping-policies/{shippingPolicyId}/active", 1L)
                                .with(csrf())
                                .principal(adminAuthentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SuccessCode.SHIPPING_POLICY_ACTIVATED.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SHIPPING_POLICY_ACTIVATED.getSuccessMessage()))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /api/admin/shipping-policies/{shippingPolicyId} 엔드포인트는 배송 정책을 삭제한다")
    void deleteShippingPolicy() throws Exception {
        mockMvc.perform(
                        delete("/api/admin/shipping-policies/{shippingPolicyId}", 1L)
                                .with(csrf())
                                .principal(adminAuthentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(SuccessCode.SHIPPING_POLICY_DELETED.name()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SHIPPING_POLICY_DELETED.getSuccessMessage()))
                .andDo(print());
    }

    private ShippingPolicyResponse response(Long id, Long deliveryFee, Long extraFee, Boolean isActive) {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 1, 0);
        return new ShippingPolicyResponse(id, deliveryFee, extraFee, isActive, now, now, null);
    }
}
