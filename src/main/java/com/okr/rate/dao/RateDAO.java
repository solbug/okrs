
package com.okr.rate.dao;

import com.okr.rate.bean.RateBean;
import com.okr.rate.bo.RateBO;
import com.okr.rate.form.RateForm;
import com.okr.utils.CommonService;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public interface RateDAO extends JpaRepository<RateBO, Integer> {

    /**
     * List all RateBO
     */
    List<RateBO> findAll();

    List<RateBO> findByIdObjective(Integer idObjective);

    Integer countByIdObjective(Integer idObjective);

    /**
     * get data by datatable
     *
     * @param commonService
     * @param formData
     * @return
     */
    default DataTableResults<RateBean> getDatatables(CommonService commonService, RateForm formData) {
        List<Object> paramList = new ArrayList<>();

        String sql = " SELECT ";
        sql += "        id as id           ";
        sql += "       ,comment AS comment     ";
        sql += "       ,rate_name AS rateName  ";
        sql += "       ,id_objective AS idObjective ";
        sql += "       ,id_member AS idMember ";
        sql += "       FROM rates ";

        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");


        Mixin.filter(formData.getId(), strCondition, paramList, "id");
        Mixin.filter(formData.getComment(), strCondition, paramList, "comment");
        Mixin.filter(formData.getRateName(), strCondition, paramList, "rate_name");
        Mixin.filter(formData.getIdMember(), strCondition, paramList, "id_member");
        Mixin.filter(formData.getIdObjective(), strCondition, paramList, "id_objective");

        String orderBy = " ORDER BY id DESC";
        return commonService.findPaginationQueryCustom(sql + strCondition.toString(), orderBy, paramList, RateBean.class, formData.getPage(), formData.getRecordPage());
    }

    /**
     * Tìm đánh giá theo mục tiêu và người đánh giá
     *
     * @param idObjective
     * @param idMember
     * @return
     */
    RateBO findByIdObjectiveAndIdMember(Integer idObjective, Integer idMember);
}
