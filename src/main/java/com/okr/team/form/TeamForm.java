package com.okr.team.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamForm {
    private Integer id;
    private String teamName;
    private Integer idDepartment;
    private Integer page;
    private Integer recordPage;
}
