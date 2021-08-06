package com.okr.objective.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectiveForm {
    private Integer id;
    private Date startDate;
    private Date endDate;
    private Integer level;
    private String objectiveName;
    private Boolean status;
    private Integer idParent;
    private Integer idMember;
    private String listRole;
    private String codeRole;
    private Integer page;
    private Integer recordPage;
}
