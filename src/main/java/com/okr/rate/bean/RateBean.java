package com.okr.rate.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateBean {
    private Integer id;
    private String comment;
    private String rateName;
    private Integer idObjective;
    private String nameObjective;
    private Integer idMember;
    private String nameMember;
}
