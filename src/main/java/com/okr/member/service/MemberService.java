package com.okr.member.service;

import com.okr.member.bean.MemberBean;
import com.okr.member.bo.MemberBO;
import com.okr.member.form.MemberForm;
import com.okr.utils.CommonService;
import com.okr.utils.DataTableResults;

import java.util.List;

public interface MemberService {
    MemberBO save(MemberForm form);

    void saveOrUpdate(MemberBO member);

    MemberBO findOne(String email);

    List<MemberBO> findAll(String search);

    void delete(MemberBO bo);

    MemberBO findById(Integer id);

    MemberBO findByEmail(String email);

    DataTableResults<MemberBean> getDataTables(MemberForm memberForm);

}
