package com.okr.member.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "members")
public class MemberBO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private Boolean gender;

    @Column(name = "member_name")
    private String memberName;

    private String password;

    private Integer idTeam;

    private String teamName;

    private Integer idDepartment;

    private String departmentName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "authority", joinColumns = {
            @JoinColumn(name = "id_member")}, inverseJoinColumns = {
            @JoinColumn(name = "id_role")
    })
    private Set<RoleBO> roles;

    private Integer memberCode;


}