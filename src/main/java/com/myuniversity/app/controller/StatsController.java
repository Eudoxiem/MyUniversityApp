package com.myuniversity.app.controller;

import com.myuniversity.app.dto.stats.StatsCoursDTO;
import com.myuniversity.app.dto.stats.StatsGeneralesDTO;
import com.myuniversity.app.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/general")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public StatsGeneralesDTO getStatsGenerales() {
        return statsService.getStatsGenerales();
    }

    @GetMapping("/cours")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSEUR')")
    public List<StatsCoursDTO> getStatsCours() {
        return statsService.getStatsCours();
    }
}
