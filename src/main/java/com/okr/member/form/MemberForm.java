package com.okr.member.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberForm {

    private Integer id;
    private String email;
    private Boolean gender;
    private String memberName;
    private String password;
    private Integer idTeam;
    private String teamName;
    private Integer idDepartment;
    private String departmentName;
    private String codeRole;
    private Integer page;
    private Integer recordPage;

}
