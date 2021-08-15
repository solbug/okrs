
package com.okr.member.dao;

import com.okr.member.bean.MemberBean;
import com.okr.member.bo.MemberBO;
import com.okr.member.form.MemberForm;
import com.okr.utils.CommonService;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface MemberDAO extends JpaRepository<MemberBO, Integer> {

    List<MemberBO> findByMemberNameContaining(String name);

    List<MemberBO> findByDepartmentNameContaining(String department);

    Integer countByIdTeam(Integer idTeam);

    /**
     * List all Member
     */
    List<MemberBO> findAll();

    List<MemberBO> findByIdTeam(Integer idTeam);

    MemberBO findByEmail(String email);
}
