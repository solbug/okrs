package com.okr.member.service;

import com.okr.member.bo.AuthorityBO;
import com.okr.member.dao.AuthorityDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityDAO authorityDAO;

    @Transactional
    public void saveOrUpdate(AuthorityBO entity) {
        authorityDAO.save(entity);
    }

    /**
     * Hàm lấy bản ghi role theo code HR hoặc CANDICATE
     *
     * @param memberId
     * @return
     */
    public AuthorityBO findByMemberId(Integer memberId) {
        return authorityDAO.findByIdMember(memberId);
    }
}
