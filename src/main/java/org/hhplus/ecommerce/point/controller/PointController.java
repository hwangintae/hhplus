package org.hhplus.ecommerce.point.controller;

import org.hhplus.ecommerce.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/point"))
public class PointController {

    @GetMapping("/ping")
    public ApiResponse<String> ping(@RequestParam String text) {
        return ApiResponse.ok(text);
    }
}
