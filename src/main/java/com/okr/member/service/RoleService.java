package com.okr.member.service;

import com.okr.member.bo.RoleBO;
import com.okr.member.dao.RoleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleDAO roleDAO;

    // Lấy toàn bộ danh sách role
    public List<RoleBO> findAll() {
        List<RoleBO> list = new ArrayList<>();
        roleDAO.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Transactional
    public void saveOrUpdate(RoleBO entity) {
        roleDAO.save(entity);
    }

    /**
     * Hàm lấy bản ghi role theo code HR hoặc CANDICATE
     *
     * @param code
     * @return
     */
    public RoleBO findByCode(String code) {
        return roleDAO.findByCode(code);
    }
}
