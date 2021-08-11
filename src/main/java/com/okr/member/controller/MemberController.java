package com.okr.member.controller;

import com.okr.mail.MailService;
import com.okr.member.bean.MemberBean;
import com.okr.member.bo.AuthorityBO;
import com.okr.member.bo.MemberBO;
import com.okr.member.bo.RoleBO;
import com.okr.member.form.*;
import com.okr.member.service.AuthorityService;
import com.okr.member.service.MemberService;
import com.okr.member.service.RoleService;
import com.okr.utils.Constants;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import com.okr.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
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

    /**
     * findById
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/member/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Manager','Leader')")
    public @ResponseBody
    Response findById(@PathVariable Integer id) {

        MemberBO memberBO = memberService.findById(id);
        if (memberBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        return Response.success("Get Detail Success").withData(memberBO);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader')")
    @RequestMapping(value = "/member/get-all", method = RequestMethod.GET)
    public @ResponseBody
    DataTableResults<MemberBean> processSearch(MemberForm form) {
        DataTableResults<MemberBean> results = memberService.getDataTables(form);
        List<MemberBean> beans = results.getData();
        results.setData(beans);
        return results;
    }

    // Lấy chi tiết member
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
            return Response.error("Vui lòng điền email đăng nhập vào hệ thống");
        }

        if (Mixin.isNullOrEmpty(member.getPassword())) {
            return Response.error("Vui lòng điền mật khẩu");
        }
        // Validate email
        if (member.getEmail() != null && !member.getEmail().trim().equals("")) {
            String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
            Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(member.getEmail());
            if (!matcher.find()) {
                return Response.custom(Constants.RESPONSE_TYPE.ERROR, "Email " + member.getEmail() + " không hợp lệ");
            }

            // Kiểm tra tồn tại của email
            MemberBO isExistUser = memberService.findByEmail(member.getEmail());
            if (isExistUser != null) {
                return Response.error("Email " + member.getEmail() + " đã được đăng ký. Vui lòng thử lại với email khác");
            }
        }
        MemberBO memberBO = memberService.save(member);
        mailService.sendEmail(member.getEmail(), "Chào bạn " + member.getMemberName() + " " + "\n" +
                "\n" +
                "Tên tài khoản : " + member.getEmail() + "\n" +
                "\n" +
                "Mật khẩu: " + member.getPassword(), "Tài khoản được tạo thành công");
        // Update bản ghi vào authority
        // Lấy id_role theo code truyền vào
        RoleBO roleBO = roleService.findByCode(member.getCodeRole());
        AuthorityBO authorityBO = new AuthorityBO();
        authorityBO.setIdRole(roleBO.getId());
        // Lấy lại memberBo => Lấy id bản ghi
        memberBO = memberService.findByEmail(member.getEmail());
        Integer memberId = memberBO.getId();

        authorityBO.setIdMember(memberId);
        authorityService.saveOrUpdate(authorityBO);

        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Thêm tài khoản thành công").withData(memberBO);
    }

    @PreAuthorize("hasAnyRole('Admin')")
    @PostMapping(path = "/member/update")
    public Response updateMember(@RequestBody MemberForm memberForm) throws Exception {
        Integer id = Mixin.NVL(memberForm.getId());
        MemberBO memberBO = memberService.findById(id);
        if (memberBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        memberBO.setMemberName(memberForm.getMemberName());
        memberBO.setGender(memberForm.getGender());
        memberBO.setIdTeam(memberForm.getIdTeam());
        memberService.saveOrUpdate(memberBO);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Cập nhật thông tin thành công")
                .withData(memberBO);
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
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Cập nhật thông tin thành công")
                .withData(memberBO);
    }

    // Resert password
    @PreAuthorize("hasRole('Admin')")
    @PostMapping(path = "/member/resert-password")
    public Response resertPassword(@RequestBody MemberForm form) {
        Integer id = Mixin.NVL(form.getId());
        MemberBO memberBO = memberService.findById(id);
        if (memberBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        memberBO.setPassword(encoder.encode("123"));
        memberService.saveOrUpdate(memberBO);
        mailService.sendEmail(memberBO.getEmail(), "Mật khẩu của bạn được thay đổi thành : " + 123, "Econet: Thay đổi mật khẩu thành công tài khoản " + memberBO.getEmail());
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Resert success");
    }

    // Đổi mật khẩu
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @PostMapping(path = "/member/change-password")
    public Response changePassword(HttpServletRequest request, @RequestBody ChangePasswordForm form) {
        // Lấy email
        Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        MemberBO memberBO = memberService.findOne(email);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // check trùng password cũ không
        boolean isMatchsOldPass = encoder.matches(form.getOldPassword(), memberBO.getPassword());
        if (isMatchsOldPass) {
            memberBO.setPassword(encoder.encode(form.getNewPassword()));
            memberService.saveOrUpdate(memberBO);
        } else {
            return Response.success(Constants.RESPONSE_CODE.ERROR, "Mật khẩu bạn nhập không trùng khớp với mật khẩu cũ. Xin thử lại");
        }
        if (!form.getNewPassword().equals(form.getRePassword())) {
            return Response.success(Constants.RESPONSE_CODE.ERROR, "Thông tin không trùng khớp");
        }
        mailService.sendEmail(memberBO.getEmail(), "Mật khẩu của bạn được thay đổi thành : " + form.getNewPassword(), "Econet: Thay đổi mật khẩu thành công tài khoản " + memberBO.getEmail());
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Thay đổi mật khẩu thành công");
    }

    // Đổi role cho member
    @PreAuthorize("hasRole('Admin')")
    @PostMapping(path = "/member/update-role")
    public Response updateRoleMember(@RequestBody ChangeRoleForm form) {

        System.out.println("Start: ============> ");
        System.out.println(form.toString());
        System.out.println("End: ============> ");
        // Lấy email
        MemberBO memberBO = memberService.findById(form.getIdMember());
        // Lấy member id từ token
        Integer memberId = memberBO.getId();
        String codeRole = form.getCodeRole();
        RoleBO roleBO = roleService.findByCode(codeRole);
        // Lấy role id từ role Code
        Integer roleId = roleBO.getId();
        AuthorityBO authorityBO = authorityService.findByMemberId(memberId);
        authorityBO.setIdRole(roleId);
        // Update lại role id của bản ghi trong DB
        authorityService.saveOrUpdate(authorityBO);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Thay đổi role thành công");
    }

    // Quên mật khẩu
    @PostMapping(path = "/member/forgot-password")
    public Response forgotPassword(@RequestBody ForgotPasswordForm form) {

        // Random code
        int min = 200;
        int max = 100000;
        int userCode = (int) (Math.random() * (max - min + 1) + min);

        // lấy member
        MemberBO memberBO = memberService.findByEmail(form.getEmail());
        if (memberBO == null) {
            return Response.error("Email " + form.getEmail() + " Không tồn tại. Vui lòng sử dụng chính xác Email");
        }
        // Update trường memberCode
        memberBO.setMemberCode(userCode);
        memberService.saveOrUpdate(memberBO);

        mailService.sendEmail(memberBO.getEmail(), "Mã code của bạn là : " + userCode, "Econet: Thông báo thay đổi mật khẩu " + memberBO.getEmail());
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Gửi mã thành công về email của bạn : " + memberBO.getEmail());
    }

    /**
     * Kiểm tra tồn tại của code so với email quên mật khẩu
     *
     * @param form
     * @return
     */
    @PostMapping(path = "/member/check-code")
    public Response checkUserCodeIsExist(@RequestBody ForgotPasswordForm form) {
        // lấy member
        MemberBO memberBO = memberService.findByEmail(form.getEmail());
        if (memberBO == null) {
            return Response.error("Email " + form.getEmail() + " không tồn tại. Vui lòng sử dụng chính xác Email");
        }
        if (!memberBO.getMemberCode().equals(form.getMemberCode())) {
            return Response.error("Vui lòng nhập mã code chính xác");
        }
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Kiểm tra xác thực thành công");
    }

    // Đổi mật khẩu khi quên mật khẩu
    @PostMapping(path = "/member/reset-password")
    public Response changeNewPassWord(@RequestBody ForgotPasswordForm form) {
        // Lấy member
        MemberBO memberBO = memberService.findByEmail(form.getEmail());
        if (!memberBO.getMemberCode().equals(form.getMemberCode())) {
            return Response.error("Vui lòng nhập mã code chính xác");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // set password
        memberBO.setPassword(encoder.encode(form.getNewPassword()));
        memberService.saveOrUpdate(memberBO);
        mailService.sendEmail(memberBO.getEmail(), "Mật khẩu của bạn được thay đổi thành : " + form.getNewPassword(), "Econet: Thay đổi mật khẩu thành công tài khoản " + memberBO.getEmail());
        return Response.success(Constants.RESPONSE_CODE.SUCCESS, "Thay đổi mật khẩu thành công");
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
