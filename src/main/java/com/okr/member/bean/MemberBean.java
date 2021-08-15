package com.okr.member.bean;

import com.okr.objective.bean.ObjectiveBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberBean {

    private Integer id;
    private String email;
    private Boolean gender;
    private String memberName;
    private String password;
    private Integer idTeam;
    private String teamName;
    private Integer idDepartment;
    private String departmentName;
    private String token;
    private List<ObjectiveBean> listObjective;
    private String codeRole;
}
