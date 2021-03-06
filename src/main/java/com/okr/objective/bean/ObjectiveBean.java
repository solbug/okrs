package com.okr.objective.bean;

import com.okr.rate.bean.RateBean;
import com.okr.rate.bo.RateBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectiveBean {

    private Integer id;
    private Date startDate;
    private Date endDate;
    private Integer level;
    private String objectiveName;
    private String description;
    private Boolean status;
    private Integer idParent;
    private Integer idMember;
    private String memberName;
    private Integer idTeam;
    private Integer idDepartment;
    private List<RateBean> listRate;
}
