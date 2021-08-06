package com.okr.objective.dao;

import com.okr.objective.bean.ObjectiveBean;
import com.okr.objective.bo.ObjectiveBO;
import com.okr.objective.form.ObjectiveForm;
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
public interface ObjectiveDAO extends JpaRepository<ObjectiveBO, Integer> {
    public List<ObjectiveBO> findAll();

    List<ObjectiveBO> findAllByIdParent(Integer idParent);

    List<ObjectiveBO> findByIdMember(Integer idMember);

    /**
     * get data by datatable
     *
     * @param commonService
     * @param formData
     * @return
     */
    default DataTableResults<ObjectiveBean> getDatatables(CommonService commonService, ObjectiveForm formData) {
        List<Object> paramList = new ArrayList<>();

        String sql = " SELECT ";
        sql += "        o.id AS id          ";
        sql += "       ,o.objective_name AS objectiveName  ";
        sql += "       ,o.level AS level        ";
        sql += "       ,o.start_date AS startDate         ";
        sql += "       ,o.end_date AS endDate          ";
        sql += "       ,o.id_parent AS idParent          ";
        sql += "       ,o.status AS status          ";
        sql += "       ,o.id_member AS idMember          ";
        sql += "       ,o.list_role AS listRole          ";
        sql += "       FROM objectives o ";

        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");


//        Mixin.filter(formData.getId(), strCondition, paramList, "id");
//        Mixin.filter(formData.getObjectiveName(), strCondition, paramList, "objective_name");
//        Mixin.filter(formData.getLevel(), strCondition, paramList, "level");

        String orderBy = " ORDER BY id DESC";
        return commonService.findPaginationQueryCustom(sql + strCondition.toString(), orderBy, paramList, ObjectiveBean.class, formData.getPage(), formData.getRecordPage());
    }

    /**
     * get data by datatable
     *
     * @param commonService
     * @param formData
     * @return
     */
    default List<ObjectiveBean> getObjectWithParent(CommonService commonService, ObjectiveForm formData) {
        List<Object> paramList = new ArrayList<>();

        String sql = " SELECT ";
        sql += "        id AS id          ";
        sql += "       ,objective_name AS objectiveName  ";
        sql += "       ,level AS level        ";
        sql += "       ,start_date AS startDate         ";
        sql += "       ,end_date AS endDate          ";
        sql += "       ,id_parent AS idParent          ";
        sql += "       ,status AS status          ";
        sql += "       ,id_member AS idMember          ";
        sql += "       ,list_role AS listRole          ";
        sql += "       FROM objectives ";
        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");
        if ((formData.getCodeRole() != null)) {
            strCondition.append(" AND ").append("\'" + formData.getCodeRole() + "\'").append(" in ").append(" (list_role)");
        }
        return commonService.list(sql + strCondition.toString(), paramList, ObjectiveBean.class);
    }
}
