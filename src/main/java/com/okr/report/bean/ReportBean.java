package com.okr.report.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportBean {

    private Integer totalDepartment;
    private Integer totalTeam;
    private Integer totalMember;
    private Integer totalObjective;

}
