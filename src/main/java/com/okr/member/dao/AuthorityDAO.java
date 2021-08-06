package com.okr.member.dao;

import com.okr.member.bo.AuthorityBO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityDAO extends JpaRepository<AuthorityBO, Integer> {
    AuthorityBO findByIdMember(Integer idMember);
}
