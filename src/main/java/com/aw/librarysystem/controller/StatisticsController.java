package com.aw.librarysystem.controller;

import com.aw.librarysystem.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public String showStatisticsPage(@RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate,
                                     Model model) {

        model.addAttribute("overdueRecords", statisticsService.getOverdueRecords());

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            model.addAttribute("dateRangeStats", statisticsService.getBorrowReturnStats(start, end));
        }

        return "admin/statistics";
    }
}