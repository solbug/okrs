package com.okr.objective.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataObjectiveBean {
    private ObjectiveBean parentObject;

    private List<ObjectiveBean> listChildObjective1;

    private List<ObjectiveBean> listChildObjective2;

    private List<ObjectiveBean> listChildObjective3;

}
