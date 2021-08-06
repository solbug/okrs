package com.okr.member.dao;

import com.okr.member.bo.RoleBO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends JpaRepository<RoleBO, Integer> {
    RoleBO findByCode(String code);
}
