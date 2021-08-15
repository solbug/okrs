package com.okr.team.controller;

import com.okr.department.bo.DepartmentBO;
import com.okr.department.service.DepartmentService;
import com.okr.member.bean.MemberBean;
import com.okr.member.bo.MemberBO;
import com.okr.member.dao.MemberDAO;
import com.okr.member.service.MemberServiceImpl;
import com.okr.team.bean.TeamBean;
import com.okr.team.bo.TeamBO;
import com.okr.team.form.TeamForm;
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
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private MemberServiceImpl memberServiceImpl;

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private DepartmentService departmentService;

    /**
     * findById
     *
     * @param teamID
     * @return
     */
    @GetMapping(path = "/{teamID}")
    @PreAuthorize("hasAnyRole('Admin', 'Manager','Leader','Member')")
    public @ResponseBody
    Response findById(@PathVariable Integer teamID) {
        TeamBO teamBO = teamService.findById(teamID);
        if (teamBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        TeamBean teamBean = new TeamBean();
        BeanUtils.copyProperties(teamBO, teamBean);
        List<MemberBean> memberBeans = new ArrayList<>();
        List<MemberBO> memberBOS = memberServiceImpl.findByIdTeam(teamID);
        for (MemberBO bo : memberBOS) {
            MemberBean bean = new MemberBean();
            BeanUtils.copyProperties(bo, bean);
            memberBeans.add(bean);
        }
        teamBean.setListMember(memberBeans);
        return Response.success("Get detail success!").withData(teamBean);
    }


    /**
     * Lấy chi tiết 1 loại
     *
     * @param form
     * @return
     */
    @PreAuthorize("hasAnyRole('Admin', 'Manager','Leader','Member')")

    @GetMapping(path = "/get-detail")
    public @ResponseBody
    Response getDetailsVisitor(@RequestBody TeamForm form) {
        TeamBO teamBO = teamService.findById(form.getId());
        if (teamBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        TeamBean teamBean = new TeamBean();
        BeanUtils.copyProperties(teamBO, teamBean);
        List<MemberBean> memberBeans = new ArrayList<>();
        List<MemberBO> memberBOS = memberServiceImpl.findByIdTeam(form.getId());
        for (MemberBO bo : memberBOS) {
            MemberBean bean = new MemberBean();
            BeanUtils.copyProperties(bo, bean);
            memberBeans.add(bean);
        }
        teamBean.setListMember(memberBeans);
        return Response.success("Get detail success!").withData(teamBean);
    }

    /**
     * processSearch
     *
     * @param form
     * @return DataTableResults
     */
    @GetMapping(path = "/get-all")
    @PreAuthorize("hasAnyRole('Admin', 'Manager','Leader', 'Member')")
    public @ResponseBody
    DataTableResults<TeamBean> processSearch(TeamForm form) {
        DataTableResults<TeamBean> results = teamService.getDatatables(form);
        List<TeamBean> beans = results.getData();
        for (TeamBean teamBean : beans) {
            teamBean.setTotalRecod(memberDAO.countByIdTeam(teamBean.getId()));
        }
        results.setData(beans);
        return results;
    }

    /**
     * saveOrUpdate TeamBO
     *
     * @param form
     * @return
     * @throws Exception
     */
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    Response saveOrUpdate(@RequestBody TeamForm form) throws Exception {
        Integer id = Mixin.NVL(form.getId());
        TeamBO teamBO;
        if (id > 0L) {

            teamBO = teamService.findById(id);
            if (teamBO == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
        } else {
            teamBO = new TeamBO();
        }
        DepartmentBO departmentBO = departmentService.findById(form.getIdDepartment());
        teamBO.setTeamName(form.getTeamName());
        teamBO.setIdDepartment(form.getIdDepartment());
        teamBO.setDepartmentName(departmentBO.getDepartmentName());
        teamService.saveOrUpdate(teamBO);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS).withData(teamBO);
    }

    /**
     * delete
     *
     * @param teamId
     * @return
     */
    @DeleteMapping(path = "/{teamId}")
    @PreAuthorize("hasRole('Admin')")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Response delete(@PathVariable Integer teamId) {
        TeamBO bo;
        if (teamId > 0L) {
            bo = teamService.findById(teamId);
            if (bo == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
            List<MemberBO> isMemberWithTeam = memberServiceImpl.findByIdTeam(bo.getId());
            if (isMemberWithTeam.size() > 0) {
                return Response.custom("Vui lòng xóa danh sách member thuộc team " + bo.getTeamName() + " trước", Constants.RESPONSE_CODE.ERROR);
            }
            teamService.delete(bo);
            return Response.success(Constants.RESPONSE_CODE.DELETE_SUCCESS);
        } else {
            return Response.error(Constants.RESPONSE_CODE.ERROR);
        }
    }
}
