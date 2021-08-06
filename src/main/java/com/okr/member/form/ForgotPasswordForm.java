package com.okr.member.form;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ForgotPasswordForm {
    private String email;
    // Mã đăng nhập của user
    private Integer memberCode;

    private String newPassword;
}
