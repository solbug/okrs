package com.okr.report.controller;

import com.okr.report.bean.ReportBean;
import com.okr.report.service.ReportService;
import com.okr.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/report")
@PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public @ResponseBody
    Response getReport() {
        ReportBean reportBean = reportService.getRepor();
        return Response.success("Get data success").withData(reportBean);
    }
}
