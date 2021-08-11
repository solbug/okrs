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
        sql += "        id AS id          ";
        sql += "       ,objective_name AS objectiveName  ";
        sql += "       ,level AS level        ";
        sql += "       ,start_date AS startDate         ";
        sql += "       ,end_date AS endDate          ";
        sql += "       ,description AS description          ";
        sql += "       ,id_team AS idTeam          ";
        sql += "       ,id_department AS idDepartment          ";
        sql += "       ,id_parent AS idParent          ";
        sql += "       ,status AS status          ";
        sql += "       ,id_member AS idMember          ";
        sql += "       FROM objectives ";

        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");


        Mixin.filter(formData.getId(), strCondition, paramList, "id");
        Mixin.filter(formData.getObjectiveName(), strCondition, paramList, "objective_name");
        Mixin.filter(formData.getLevel(), strCondition, paramList, "level");
        Mixin.filter(formData.getIdTeam(), strCondition, paramList, "id_team");
        Mixin.filter(formData.getIdDepartment(), strCondition, paramList, "id_department");
        Mixin.filter(formData.getIdParent(), strCondition, paramList, "id_parent");
        Mixin.filter(formData.getIdMember(), strCondition, paramList, "id_member");
        String orderBy = " ORDER BY id ASC";
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
        sql += "       ,description AS description          ";
        sql += "       ,id_department AS idDepartment          ";
        sql += "       ,id_team AS idTeam          ";
        sql += "       ,id_parent AS idParent          ";
        sql += "       ,status AS status          ";
        sql += "       ,id_member AS idMember          ";
        sql += "       FROM objectives ";


        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");
        if ((formData.getIdDepartment() != null)) {
            strCondition.append(" AND ").append("\'" + formData.getIdDepartment() + "\'").append(" in ").append(" (id_department)");
        }
        if ((formData.getIdTeam() != null)) {
            strCondition.append(" AND ").append("\'" + formData.getIdTeam() + "\'").append(" in ").append(" (id_team)");
        }
        if ((formData.getIdMember() != null)) {
            strCondition.append(" AND ").append("\'" + formData.getIdMember() + "\'").append(" in ").append(" (id_member)");
        }
        return commonService.list(sql + strCondition.toString(), paramList, ObjectiveBean.class);
    }
}
