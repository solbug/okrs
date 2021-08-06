package com.okr.department.service;

import com.okr.department.bean.DepartmentBean;
import com.okr.department.bo.DepartmentBO;
import com.okr.department.dao.DepartmentDAO;
import com.okr.department.form.DepartmentForm;
import com.okr.utils.CommonService;
import com.okr.utils.DataTableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentDAO departmentDAO;

    @Autowired
    private CommonService commonService;

    /**
     * findById
     *
     * @param departmentId
     * @return
     */
    public DepartmentBO findById(Integer departmentId) {
        return departmentDAO.findById(departmentId).orElse(null);
    }

    /**
     * getDatatables
     *
     * @param departmentForm
     * @return
     */
    public DataTableResults<DepartmentBean> getDatatables(DepartmentForm departmentForm) {
        return departmentDAO.getDatatables(commonService, departmentForm);
    }

    /**
     * saveOrUpdate
     *
     * @param entity
     */
    @Transactional
    public void saveOrUpdate(DepartmentBO entity) {
        departmentDAO.save(entity);
    }

    /**
     * delete
     *
     * @param entity
     */
    public void delete(DepartmentBO entity) {
        departmentDAO.delete(entity);
    }
}
