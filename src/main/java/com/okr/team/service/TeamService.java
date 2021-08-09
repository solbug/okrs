package com.okr.team.service;

import com.okr.team.bean.TeamBean;
import com.okr.team.bo.TeamBO;
import com.okr.team.dao.TeamDAO;
import com.okr.team.form.TeamForm;
import com.okr.utils.CommonService;
import com.okr.utils.DataTableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TeamService {
    @Autowired
    private TeamDAO teamDAO;

    @Autowired
    private CommonService commonService;

    public TeamBO findById(Integer teamId) {
        return teamDAO.findById(teamId).orElse(null);
    }

    /**
     * find by Department
     *
     * @param idDepartment
     * @return
     */
    public List<TeamBO> findByIdDepartment(Integer idDepartment) {
        return teamDAO.findByIdDepartment(idDepartment);
    }

    /**
     * getDatatables
     *
     * @param teamForm
     * @return
     */

    public DataTableResults<TeamBean> getDatatables(TeamForm teamForm) {
        return teamDAO.getDatatables(commonService, teamForm);
    }

    /**
     * saveOrUpdate
     *
     * @param entity
     */
    @Transactional
    public void saveOrUpdate(TeamBO entity) {
        teamDAO.save(entity);
    }

    /**
     * delete
     *
     * @param entity
     */
    public void delete(TeamBO entity) {
        teamDAO.delete(entity);
    }

}
