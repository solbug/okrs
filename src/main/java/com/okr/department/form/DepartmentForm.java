package com.okr.department.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentForm {
    private Integer id;
    private String departmentName;
    private Integer page;
    private Integer recordPage;
}
