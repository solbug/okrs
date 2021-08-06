package com.okr.department.dao;

import com.okr.department.bean.DepartmentBean;
import com.okr.department.bo.DepartmentBO;
import com.okr.department.form.DepartmentForm;
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
public interface DepartmentDAO extends JpaRepository<DepartmentBO, Integer> {

    /**
     * List all Department
     */
    List<DepartmentBO> findAll();

    /**
     * get data by datatable
     *
     * @param commonService
     * @param formData
     * @return
     */
    default DataTableResults<DepartmentBean> getDatatables(CommonService commonService, DepartmentForm formData) {
        List<Object> paramList = new ArrayList<>();
        String sql = " SELECT ";
        sql += "        id As id           ";
        sql += "       ,department_name AS departmentName      ";
        sql += "       FROM departments ";

        StringBuilder strCondition = new StringBuilder(" WHERE 1 = 1 ");
        Mixin.filter(formData.getId(), strCondition, paramList, "id");
        Mixin.filter(formData.getDepartmentName(), strCondition, paramList, "department_name ");

        String orderBy = " ORDER BY id DESC";
        return commonService.findPaginationQueryCustom(sql + strCondition.toString(), orderBy, paramList, DepartmentBean.class, formData.getPage(), formData.getRecordPage());
    }
}