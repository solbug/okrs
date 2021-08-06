package com.okr.objective.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "objectives")
public class ObjectiveBO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "end_date")
    private Date endDate;

    private Integer level;

    @Column(name = "objective_name")
    private String objectiveName;

    private Boolean status;

    private String listRole;

    private Integer idMember;

    private Integer idParent;


}