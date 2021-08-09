package com.okr.member.service;

import com.okr.member.bean.MemberBean;
import com.okr.member.bo.MemberBO;
import com.okr.member.dao.MemberDAO;
import com.okr.member.form.MemberForm;
import com.okr.utils.CommonService;
import com.okr.utils.DataTableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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


    private Set<SimpleGrantedAuthority> getAuthority(MemberBO memberBO) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        memberBO.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getNameRole()));
        });
        return authorities;
    }


//    /**
//     * getDatatables
//     *
//     * @param memberForm
//     * @return
//     */
//    public DataTableResults<MemberBean> getDatatables(MemberForm memberForm) {
//        return memberDAO.getDatatables(commonService, memberForm);
//    }

    @Override
    public MemberBO save(MemberForm form) {
        MemberBO memberBO = new MemberBO();
        memberBO.setEmail(form.getEmail());
        memberBO.setMemberName(form.getMemberName());
        memberBO.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
        memberBO.setIdTeam(form.getIdTeam());
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
    public List<MemberBO> findAll() {
        List<MemberBO> list = new ArrayList<>();
        memberDAO.findAll().iterator().forEachRemaining(list::add);
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


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberBO memberBO = memberDAO.findByEmail(email);
        if (memberBO == null) {
            throw new UsernameNotFoundException("Tên tài khoản hoặc mật khẩu không chính xác");
        }
        return new User(memberBO.getEmail(), memberBO.getPassword(), getAuthority(memberBO));
    }
}
