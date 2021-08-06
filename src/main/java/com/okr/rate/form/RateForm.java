package com.okr.rate.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateForm {
    private Integer id;
    private String comment;
    private String rateName;
    private Integer idObjective;
    private Integer idMember;
    private Integer page;
    private Integer recordPage;
}
