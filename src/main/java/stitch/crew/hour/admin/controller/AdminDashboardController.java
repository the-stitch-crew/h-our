package stitch.crew.hour.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.admin.dto.AdminDashboardResponse;
import stitch.crew.hour.admin.service.AdminDashboardService;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ResponseEntity<ApiResponses<AdminDashboardResponse>> getDashboardInfo() {
        AdminDashboardResponse response = adminDashboardService.getDashboardInfo();
        return ApiResult.ok(SuccessCode.ADMIN_DASHBOARD_READ, response);
    }
}
