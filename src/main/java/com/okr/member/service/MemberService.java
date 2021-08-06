package com.okr.member.service;

import com.okr.member.bo.MemberBO;
import com.okr.member.form.MemberForm;

import java.util.List;

public interface MemberService {
    MemberBO save(MemberForm form);

    void saveOrUpdate(MemberBO user);

    MemberBO findOne(String email);

    List<MemberBO> findAll();

    void delete(Integer id);

    MemberBO findById(Integer id);

    MemberBO findByEmail(String email);

}
