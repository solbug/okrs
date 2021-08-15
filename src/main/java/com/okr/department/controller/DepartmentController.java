package com.okr.department.controller;

import com.okr.department.bean.DepartmentBean;
import com.okr.department.bo.DepartmentBO;
import com.okr.department.form.DepartmentForm;
import com.okr.department.service.DepartmentService;
import com.okr.team.bean.TeamBean;
import com.okr.team.bo.TeamBO;
import com.okr.team.dao.TeamDAO;
import com.okr.team.service.TeamService;
import com.okr.utils.Constants;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import com.okr.utils.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamDAO teamDAO;


    /**
     * findById
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('Admin','Manager','Leader','Member')")
    @GetMapping(path = "/{id}")
    public @ResponseBody
    Response findById(@PathVariable Integer id) {
        DepartmentBO departmentBO = departmentService.findById(id);
        if (departmentBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        DepartmentBean departmentBean = new DepartmentBean();
        departmentBean.setId(departmentBO.getId());
        departmentBean.setDepartmentName(departmentBO.getDepartmentName());
        List<TeamBean> teamBeans = new ArrayList<>();
        List<TeamBO> teamBOs = teamService.findByIdDepartment(id);
        for (TeamBO bo : teamBOs) {
            TeamBean bean = new TeamBean();
            BeanUtils.copyProperties(bo, bean);
            teamBeans.add(bean);
        }
        // Set lại giá trị cho list
        departmentBean.setListTeam(teamBeans);
        return Response.success("Get data success").withData(departmentBean);
    }


    /**
     * get detail
     *
     * @param form
     * @return
     */
    @PreAuthorize("hasAnyRole('Admin','Manager','Leader','Member')")
    @GetMapping(path = "/get-detail")
    public @ResponseBody
    Response getDetail(@RequestBody DepartmentForm form) {
        DepartmentBO departmentBO = departmentService.findById(form.getId());
        if (departmentBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        DepartmentBean departmentBean = new DepartmentBean();
        departmentBean.setId(departmentBO.getId());
        departmentBean.setDepartmentName(departmentBO.getDepartmentName());
        List<TeamBean> teamBeans = new ArrayList<>();
        List<TeamBO> teamBOs = teamService.findByIdDepartment(form.getId());
        for (TeamBO bo : teamBOs) {
            TeamBean bean = new TeamBean();
            BeanUtils.copyProperties(bo, bean);
            teamBeans.add(bean);
        }
        // Set lại giá trị cho list
        departmentBean.setListTeam(teamBeans);
        return Response.success("Get data success").withData(departmentBean);
    }

    /**
     * processSearch
     *
     * @param form
     * @return DataTableResults
     */
    @GetMapping(path = "/get-all")
    @PreAuthorize("hasAnyRole('Admin','Manager','Leader','Member')")
    public @ResponseBody
    DataTableResults<DepartmentBean> processSearch(DepartmentForm form) {
        DataTableResults<DepartmentBean> results = departmentService.getDatatables(form);
        List<DepartmentBean> beans = results.getData();
        for (DepartmentBean departmentBean : beans) {
            departmentBean.setTotalRecord(teamDAO.countByIdDepartment(departmentBean.getId()));
        }
        // xét lại giá trị cho list DepartmentBean
        results.setData(beans);
        return results;
    }

    /**
     * saveOrUpdate DepartmentBO
     *
     * @param form
     * @return
     * @throws Exception
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('Admin')")
    public @ResponseBody
    Response saveOrUpdate(@RequestBody DepartmentForm form) throws Exception {
        Integer id = Mixin.NVL(form.getId());
        DepartmentBO departmentBO;
        if (id > 0L) {

            departmentBO = departmentService.findById(id);
            if (departmentBO == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
        } else {
            departmentBO = new DepartmentBO();
        }
        departmentBO.setDepartmentName(form.getDepartmentName());
        departmentService.saveOrUpdate(departmentBO);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS).withData(departmentBO);
    }

    /**
     * Kiểm tra id có tồn tại không => Kiểm tra có Team nào thuộc phòng ban này không => thực hiện việc xóa
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('Admin')")
    public @ResponseBody
    Response delete(@PathVariable Integer id) {
        DepartmentBO bo;
        if (id > 0L) {
            bo = departmentService.findById(id);
            if (bo == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
            List<TeamBO> isTeamWithDepartment = teamService.findByIdDepartment(bo.getId());
            if (isTeamWithDepartment.size() > 0) {
                return Response.custom("Vui lòng xóa team thuộc phòng:  " + bo.getDepartmentName(), Constants.RESPONSE_CODE.ERROR);
            }
            departmentService.delete(bo);
            return Response.success(Constants.RESPONSE_CODE.DELETE_SUCCESS);
        } else {
            return Response.error(Constants.RESPONSE_CODE.ERROR);
        }
    }
}
