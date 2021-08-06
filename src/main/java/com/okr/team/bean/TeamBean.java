package com.okr.team.bean;

import com.okr.member.bean.MemberBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class TeamBean implements Serializable {

    private Integer id;
    private String teamName;
    private Integer idDepartment;
    private String nameDepartment;
    private List<MemberBean> listMember;
    private Integer totalRecod;
}