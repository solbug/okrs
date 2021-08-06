
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

    Integer countByIdTeam(Integer idTeam);
    /**
     * List all Member
     */
    List<MemberBO> findAll();

    List<MemberBO> findByIdTeam(Integer idTeam);

//    /**
//     * get data by datatable
//     *
//     * @param commonService
//     * @param formData
//     * @return
//     */
//    default DataTableResults<MemberBean> getDatatables(CommonService commonService, MemberForm formData) {
//        List<Object> paramList = new ArrayList<>();
//
//        String sql = " SELECT ";
//        sql += "        id AS id          ";
//        sql += "       ,member_name as memberName      ";
//        sql += "       ,gender as gender  ";
//        sql += "       ,email AS email ";
//        sql += "       ,`password` AS `password` ";
//        sql += "       ,id_team as idTeam       ";
//        sql += "      FROM members ";
//
//        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");
//
//
//        Mixin.filter(formData.getId(), strCondition, paramList, "id");
//        Mixin.filter(formData.getMemberName(), strCondition, paramList, "member_name");
//        Mixin.filter(formData.getGender(), strCondition, paramList, "gender");
//        Mixin.filter(formData.getEmail(), strCondition, paramList, "email");
//        Mixin.filter(formData.getPassword(), strCondition, paramList, "password");
//        String orderBy = " ORDER BY id DESC";
//        return commonService.findPaginationQueryCustom(sql + strCondition.toString(), orderBy, paramList, MemberBean.class, formData.getPage(), formData.getRecordPage());
//    }

    MemberBO findByEmail(String email);
}
