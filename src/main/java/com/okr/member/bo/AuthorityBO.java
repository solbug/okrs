package com.okr.member.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "authority")
public class AuthorityBO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_member")
	private Integer idMember;

	@Column(name = "id_role")
	private Integer idRole;


}