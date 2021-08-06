package com.okr.member.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Form dùng cho việc Change pasword
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordForm {
    private String oldPassword;
    private String newPassword;
    private String rePassword;
}
