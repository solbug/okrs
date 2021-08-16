package com.okr.member.service;

import com.okr.department.bo.DepartmentBO;
import com.okr.department.service.DepartmentService;
import com.okr.member.bean.MemberBean;
import com.okr.member.bo.MemberBO;
import com.okr.member.bo.RoleBO;
import com.okr.member.dao.MemberDAO;
import com.okr.member.form.MemberForm;
import com.okr.team.bo.TeamBO;
import com.okr.team.service.TeamService;
import com.okr.utils.CommonService;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(value = "userService")
public class MemberServiceImpl implements UserDetailsService, MemberService {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private CommonService commonService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeamService teamService;

    private Set<SimpleGrantedAuthority> getAuthority(MemberBO memberBO) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        memberBO.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getNameRole()));
        });
        return authorities;
    }

    @Override
    public MemberBO save(MemberForm form) {
        MemberBO memberBO = new MemberBO();
        memberBO.setEmail(form.getEmail());
        DepartmentBO departmentBO = departmentService.findById(form.getIdDepartment());
        memberBO.setIdDepartment(form.getIdDepartment());
        memberBO.setDepartmentName(departmentBO.getDepartmentName());
        TeamBO teamBO = teamService.findById(form.getIdTeam());
        memberBO.setIdTeam(form.getIdTeam());
        memberBO.setTeamName(teamBO.getTeamName());
        memberBO.setMemberName(form.getMemberName());
        memberBO.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
        memberBO.setGender(form.getGender());
        return memberDAO.save(memberBO);
    }

    @Override
    public void saveOrUpdate(MemberBO member) {
        memberDAO.save(member);
    }

    public List<MemberBO> findByIdTeam(Integer idTeam) {
        return memberDAO.findByIdTeam(idTeam);
    }


    @Override
    public MemberBO findOne(String email) {
        return memberDAO.findByEmail(email);
    }

    @Override
    public List<MemberBO> findAll(String search) {
        List<MemberBO> list = new ArrayList<>();
        if (StringUtils.isEmpty(search)) {
            memberDAO.findAll().iterator().forEachRemaining(list::add);
        } else {
            memberDAO.findByMemberNameContaining(search).iterator().forEachRemaining(list::add);

            memberDAO.findByDepartmentNameContaining(search).iterator().forEachRemaining(list::add);
        }
        return list;
    }

    @Override
    public void delete(MemberBO bo) {
        memberDAO.delete(bo);
    }

    @Override
    public MemberBO findById(Integer id) {
        return memberDAO.findById(id).orElse(null);
    }

    @Override
    public MemberBO findByEmail(String email) {
        return memberDAO.findByEmail(email);
    }

    @Override
    public DataTableResults<MemberBean> getDataTables(MemberForm memberForm) {
        List<Object> paramList = new ArrayList<>();

        String sql = " SELECT ";
        sql += "        members.id AS id          ";
        sql += "       ,email AS email      ";
        sql += "       ,gender AS gender     ";
        sql += "       ,member_name as memberName     ";
        sql += "       ,members.id_team AS idTeam      ";
        sql += "       ,members.team_name AS teamName     ";
        sql += "       ,members.id_department AS idDepartment    ";
        sql += "       ,members.department_name AS departmentName     ";
        sql += "       ,roles.name_role AS codeRole     ";
        sql += "      FROM members JOIN teams ON members.id_team = teams.id\n" +
                "JOIN departments ON teams.id_department = departments.id\n" +
                "JOIN authority ON members.id = authority.id_member\n" +
                "JOIN roles ON authority.id_role = roles.id";
        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");

        Mixin.filter(memberForm.getId(), strCondition, paramList, "members.id");
        Mixin.filter(memberForm.getMemberName(), strCondition, paramList, "member_name");
        Mixin.filter(memberForm.getIdTeam(), strCondition, paramList, "members.id_team");
        Mixin.filter(memberForm.getTeamName(), strCondition, paramList, "members.team_name");
        Mixin.filter(memberForm.getIdDepartment(), strCondition, paramList, "members.id_department");
        Mixin.filter(memberForm.getDepartmentName(), strCondition, paramList, "members.department_name");
        String orderBy = " ORDER BY id ASC";
        return commonService.findPaginationQueryCustom(sql + strCondition.toString(), orderBy, paramList, MemberBean.class, memberForm.getPage(), memberForm.getRecordPage());
    }


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberBO memberBO = memberDAO.findByEmail(email);
        if (memberBO == null) {
            throw new UsernameNotFoundException("Tên tài khoản hoặc mật khẩu không chính xác");
        }
        return new User(memberBO.getEmail(), memberBO.getPassword(), getAuthority(memberBO));
    }
}
