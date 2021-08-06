package com.okr.team.dao;

import com.okr.team.bean.TeamBean;
import com.okr.team.bo.TeamBO;
import com.okr.team.form.TeamForm;
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
public interface TeamDAO extends JpaRepository<TeamBO, Integer> {
    Integer countByIdDepartment(Integer idDepartment);

    /**
     * List all Team
     */
    public List<TeamBO> findAll();

    List<TeamBO> findByIdDepartment(Integer idDepartment);

    /**
     * get data by datatable
     *
     * @param commonService
     * @param formData
     * @return
     */
    default DataTableResults<TeamBean> getDatatables(CommonService commonService, TeamForm formData) {
        List<Object> paramList = new ArrayList<>();

        String sql = " SELECT ";
        sql += "        id AS id          ";
        sql += "       ,team_name AS `teamName`      ";
        sql += "       ,id_department AS `idDepartment`     ";
        sql += "      FROM teams ";

        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");

        Mixin.filter(formData.getId(), strCondition, paramList, "id");
        Mixin.filter(formData.getTeamName(), strCondition, paramList, "team_name");
        Mixin.filter(formData.getIdDepartment(), strCondition, paramList, "id_department");

        String orderBy = " ORDER BY id DESC";
        return commonService.findPaginationQueryCustom(sql + strCondition.toString(), orderBy, paramList, TeamBean.class, formData.getPage(), formData.getRecordPage());
    }
}
