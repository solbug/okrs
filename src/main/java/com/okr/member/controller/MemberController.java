package com.okr.member.controller;

import com.okr.department.bo.DepartmentBO;
import com.okr.department.service.DepartmentService;
import com.okr.mail.MailService;
import com.okr.member.bean.MemberBean;
import com.okr.member.bo.AuthorityBO;
import com.okr.member.bo.MemberBO;
import com.okr.member.bo.RoleBO;
import com.okr.member.form.*;
import com.okr.member.service.AuthorityService;
import com.okr.member.service.MemberService;
import com.okr.member.service.RoleService;
import com.okr.team.bo.TeamBO;
import com.okr.team.service.TeamService;
import com.okr.utils.Constants;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import com.okr.utils.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MailService mailService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeamService teamService;

    /**
     * findById
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/member/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Manager','Leader','Member')")
    public @ResponseBody
    Response findById(@PathVariable Integer id) {

        MemberBO memberBO = memberService.findById(id);
        if (memberBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        return Response.success("Get Detail Success").withData(memberBO);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager','Leader','Member')")
    @RequestMapping(value = "/member/get-all", method = RequestMethod.GET)
    public @ResponseBody
    DataTableResults<MemberBean> processSearch(MemberForm form) {
        DataTableResults<MemberBean> results = memberService.getDataTables(form);
        List<MemberBean> beans = results.getData();
        results.setData(beans);
        return results;
    }

    // L???y chi ti???t member
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @PostMapping(path = "/member/details")
    public MemberBO getDetailsMember(@RequestBody ProfileMemberForm member) {
        return memberService.findById(member.getId());
    }

    // profile member
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @GetMapping(path = "/member/profile")
    public MemberBO getProfileMember(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        MemberBO memberBO = memberService.findOne(email);
        return memberBO;
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping(path = "/member/add")
    public Response saverMember(@RequestBody MemberForm member) {

        if (Mixin.isNullOrEmpty(member.getEmail())) {
            return Response.error("Vui l??ng ??i???n email ????ng nh???p v??o h??? th???ng");
        }

        if (Mixin.isNullOrEmpty(member.getPassword())) {
            return Response.error("Vui l??ng ??i???n m???t kh???u");
        }
        // Validate email
        if (member.getEmail() != null && !member.getEmail().trim().equals("")) {
            String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
            Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(member.getEmail());
            if (!matcher.find()) {
                return Response.custom(Constants.RESPONSE_TYPE.ERROR, "Email " + member.getEmail() + " kh??ng h???p l???");
            }

            // Ki???m tra t???n t???i c???a email
            MemberBO isExistUser = memberService.findByEmail(member.getEmail());
            if (isExistUser != null) {
                return Response.error("Email " + member.getEmail() + " ???? ???????c ????ng k??. Vui l??ng th??? l???i v???i email kh??c");
            }
        }
        MemberBO memberBO = memberService.save(member);
        mailService.sendEmail(member.getEmail(), "Ch??o b???n " + member.getMemberName() + " " + "\n" +
                "\n" +
                "T??n t??i kho???n : " + member.getEmail() + "\n" +
                "\n" +
                "M???t kh???u: " + member.getPassword(), "T??i kho???n ???????c t???o th??nh c??ng");
        // Update b???n ghi v??o authority
        // L???y id_role theo code truy???n v??o
        RoleBO roleBO = roleService.findByCode(member.getCodeRole());
        AuthorityBO authorityBO = new AuthorityBO();
        authorityBO.setIdRole(roleBO.getId());
        // L???y l???i memberBo => L???y id b???n ghi
        memberBO = memberService.findByEmail(member.getEmail());
        Integer memberId = memberBO.getId();

        authorityBO.setIdMember(memberId);
        authorityService.saveOrUpdate(authorityBO);
        MemberBean memberBean = new MemberBean();
        memberBean.setCodeRole(roleBO.getCode());
        BeanUtils.copyProperties(memberBO, memberBean);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Th??m t??i kho???n th??nh c??ng").withData(memberBean);
    }

    @PreAuthorize("hasAnyRole('Admin')")
    @PostMapping(path = "/member/update")
    public Response updateMember(@RequestBody MemberForm memberForm) throws Exception {
        Integer id = Mixin.NVL(memberForm.getId());
        MemberBO memberBO = memberService.findById(id);
        if (memberBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        memberBO.setIdDepartment(memberForm.getIdDepartment());
        DepartmentBO departmentBO = departmentService.findById(memberForm.getIdDepartment());
        memberBO.setIdTeam(memberForm.getIdTeam());
        TeamBO teamBO = teamService.findById(memberBO.getIdTeam());
        memberBO.setMemberName(memberForm.getMemberName());
        memberBO.setGender(memberForm.getGender());
        memberBO.setDepartmentName(departmentBO.getDepartmentName());
        memberBO.setTeamName(teamBO.getTeamName());

        // L???y member id t??? token
        Integer memberId = memberBO.getId();
        String codeRole = memberForm.getCodeRole();
        RoleBO roleBO = roleService.findByCode(codeRole);
        // L???y role id t??? role Code
        Integer roleId = roleBO.getId();
        AuthorityBO authorityBO = authorityService.findByMemberId(memberId);
        authorityBO.setIdRole(roleId);
        // Update l???i role id c???a b???n ghi trong DB
        authorityService.saveOrUpdate(authorityBO);
        memberService.saveOrUpdate(memberBO);
        MemberBean memberBean = new MemberBean();
        memberBean.setCodeRole(roleBO.getCode());
        BeanUtils.copyProperties(memberBO, memberBean);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "C???p nh???t th??ng tin th??nh c??ng")
                .withData(memberBean);
    }

    //Update
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @PostMapping(path = "/member/update-profile")
    public Response processSearchVisitor(HttpServletRequest request, @RequestBody MemberForm memberForm) throws Exception {
        Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        MemberBO memberBO = memberService.findByEmail(email);
        memberBO.setMemberName(memberForm.getMemberName());
        memberBO.setGender(memberForm.getGender());
        memberBO.setIdTeam(memberForm.getIdTeam());
        memberService.saveOrUpdate(memberBO);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "C???p nh???t th??ng tin th??nh c??ng")
                .withData(memberBO);
    }

    // Resert password
    @PreAuthorize("hasRole('Admin')")
    @PostMapping(path = "/member/resert")
    public Response resertPassword(@RequestBody MemberForm form) {
        Integer id = Mixin.NVL(form.getId());
        MemberBO memberBO = memberService.findById(id);
        if (memberBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        memberBO.setPassword(encoder.encode("123"));
        memberService.saveOrUpdate(memberBO);
        mailService.sendEmail(memberBO.getEmail(), "M???t kh???u c???a b???n ???????c thay ?????i th??nh : " + 123, "Econet: Thay ?????i m???t kh???u th??nh c??ng t??i kho???n " + memberBO.getEmail());
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Resert success");
    }

    // ?????i m???t kh???u
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @PostMapping(path = "/member/change-password")
    public Response changePassword(HttpServletRequest request, @RequestBody ChangePasswordForm form) {
        // L???y email
        Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        MemberBO memberBO = memberService.findOne(email);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // check tr??ng password c?? kh??ng
        boolean isMatchsOldPass = encoder.matches(form.getOldPassword(), memberBO.getPassword());
        if (isMatchsOldPass) {
            memberBO.setPassword(encoder.encode(form.getNewPassword()));
            memberService.saveOrUpdate(memberBO);
        } else {
            return Response.success(Constants.RESPONSE_CODE.ERROR, "M???t kh???u b???n nh???p kh??ng tr??ng kh???p v???i m???t kh???u c??. Xin th??? l???i");
        }
        if (!form.getNewPassword().equals(form.getRePassword())) {
            return Response.success(Constants.RESPONSE_CODE.ERROR, "Th??ng tin kh??ng tr??ng kh???p");
        }
        mailService.sendEmail(memberBO.getEmail(), "M???t kh???u c???a b???n ???????c thay ?????i th??nh : " + form.getNewPassword(), "Econet: Thay ?????i m???t kh???u th??nh c??ng t??i kho???n " + memberBO.getEmail());
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Thay ?????i m???t kh???u th??nh c??ng");
    }

    // Qu??n m???t kh???u
    @PostMapping(path = "/member/forgot-password")
    public Response forgotPassword(@RequestBody ForgotPasswordForm form) {

        // Random code
        int min = 200;
        int max = 100000;
        int userCode = (int) (Math.random() * (max - min + 1) + min);

        // l???y member
        MemberBO memberBO = memberService.findByEmail(form.getEmail());
        if (memberBO == null) {
            return Response.error("Email " + form.getEmail() + " Kh??ng t???n t???i. Vui l??ng s??? d???ng ch??nh x??c Email");
        }
        // Update tr?????ng memberCode
        memberBO.setMemberCode(userCode);
        memberService.saveOrUpdate(memberBO);

        mailService.sendEmail(memberBO.getEmail(), "M?? code c???a b???n l?? : " + userCode, "Econet: Th??ng b??o thay ?????i m???t kh???u " + memberBO.getEmail());
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "G???i m?? th??nh c??ng v??? email c???a b???n : " + memberBO.getEmail());
    }

    /**
     * Ki???m tra t???n t???i c???a code so v???i email qu??n m???t kh???u
     *
     * @param form
     * @return
     */
    @PostMapping(path = "/member/check-code")
    public Response checkUserCodeIsExist(@RequestBody ForgotPasswordForm form) {
        // l???y member
        MemberBO memberBO = memberService.findByEmail(form.getEmail());
        if (memberBO == null) {
            return Response.error("Email " + form.getEmail() + " kh??ng t???n t???i. Vui l??ng s??? d???ng ch??nh x??c Email");
        }
        if (!memberBO.getMemberCode().equals(form.getMemberCode())) {
            return Response.error("Vui l??ng nh???p m?? code ch??nh x??c");
        }
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Ki???m tra x??c th???c th??nh c??ng");
    }

    // ?????i m???t kh???u khi qu??n m???t kh???u
    @PostMapping(path = "/member/reset-password")
    public Response changeNewPassWord(@RequestBody ForgotPasswordForm form) {
        // L???y member
        MemberBO memberBO = memberService.findByEmail(form.getEmail());
        if (!memberBO.getMemberCode().equals(form.getMemberCode())) {
            return Response.error("Vui l??ng nh???p m?? code ch??nh x??c");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // set password
        memberBO.setPassword(encoder.encode(form.getNewPassword()));
        memberService.saveOrUpdate(memberBO);
        mailService.sendEmail(memberBO.getEmail(), "M???t kh???u c???a b???n ???????c thay ?????i th??nh : " + form.getNewPassword(), "Econet: Thay ?????i m???t kh???u th??nh c??ng t??i kho???n " + memberBO.getEmail());
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Thay ?????i m???t kh???u th??nh c??ng");
    }

    /**
     * delete
     *
     * @param memberId
     * @return
     */
    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping(path = "/member/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Response delete(@PathVariable Integer memberId) {

        MemberBO bo;
        if (memberId > 0L) {
            bo = memberService.findById(memberId);
            if (bo == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
            AuthorityBO authorityBOS = authorityService.findByMemberId(bo.getId());
            if (authorityBOS != null) {
                authorityService.delete(authorityBOS.getIdMember());
            }
            memberService.delete(bo);
            return Response.success(Constants.RESPONSE_CODE.DELETE_SUCCESS);
        } else {
            return Response.error(Constants.RESPONSE_CODE.ERROR);
        }
    }


}
