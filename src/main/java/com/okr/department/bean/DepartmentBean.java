package com.okr.department.bean;

import com.okr.team.bean.TeamBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentBean {
    private Integer id;
    private String departmentName;
    private List<TeamBean> listTeam;
    private Integer totalRecord;

}
