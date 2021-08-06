package com.okr.member.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangeRoleForm {
    private Integer idMember;
    private String codeRole;
}
