package com.okr.member.controller;

import com.okr.config.TokenProvider;
import com.okr.member.bean.MemberBean;
import com.okr.member.bo.MemberBO;
import com.okr.member.bo.RoleBO;
import com.okr.member.form.LoginMemberForm;
import com.okr.member.service.MemberService;
import com.okr.utils.Constants;
import com.okr.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody
    Response login(@RequestBody LoginMemberForm form) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        form.getEmail(),
                        form.getPassword()
                )
        );
        // Lấy token của người dùng
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);

        // Gán lại giá trị cho MemberBean để hiển thị lên thông tin
        MemberBean memberBean = new MemberBean();
        memberBean.setToken(token);
        memberBean.setEmail(form.getEmail());
        // Từ email lấy thông tin của member
        MemberBO memberBO = memberService.findOne(form.getEmail());
        memberBean.setId(memberBO.getId());
        memberBean.setMemberName(memberBO.getMemberName());
        memberBean.setEmail(memberBO.getEmail());
        Optional<RoleBO> roleBOOptional = memberBO.getRoles().stream().findFirst();
        memberBean.setCodeRole(roleBOOptional.get().getCode());
        return Response.success(Constants.RESPONSE_TYPE.SUCCESS, "Đăng nhập thành công với vai trò " + roleBOOptional.get().getNameRole()).withData(memberBean);

    }

}
