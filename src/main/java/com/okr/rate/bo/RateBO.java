package com.okr.rate.bo;

import com.okr.member.bo.MemberBO;
import com.okr.objective.bo.ObjectiveBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rates")
public class RateBO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String comment;

    @Column(name = "rate_name")
    private String rateName;

    private Integer idObjective;

    private Integer idMember;


}