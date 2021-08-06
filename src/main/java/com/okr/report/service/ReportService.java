package com.okr.report.service;

import com.okr.department.dao.DepartmentDAO;
import com.okr.member.dao.MemberDAO;
import com.okr.objective.dao.ObjectiveDAO;
import com.okr.report.bean.ReportBean;
import com.okr.team.dao.TeamDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    private DepartmentDAO departmentDAO;

    @Autowired
    private TeamDAO teamDAO;

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private ObjectiveDAO objectiveDAO;

    public ReportBean getRepor() {
        ReportBean reportBean = new ReportBean();
        reportBean.setTotalDepartment((int) departmentDAO.count());
        reportBean.setTotalTeam((int) teamDAO.count());
        reportBean.setTotalMember((int) memberDAO.count());
        reportBean.setTotalObjective((int) objectiveDAO.count());
        return reportBean;
    }
}
