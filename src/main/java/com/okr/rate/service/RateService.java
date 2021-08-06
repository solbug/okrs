package com.okr.rate.service;

import com.okr.rate.bean.RateBean;
import com.okr.rate.bo.RateBO;
import com.okr.rate.dao.RateDAO;
import com.okr.rate.form.RateForm;
import com.okr.utils.CommonService;
import com.okr.utils.DataTableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RateService {

    @Autowired
    private RateDAO rateDAO;

    @Autowired
    private CommonService commonService;

    /**
     * findById
     *
     * @param idRate
     * @return
     */
    public RateBO findById(Integer idRate) {
        return rateDAO.findById(idRate).orElse(null);
    }

    /**
     * getDatatables
     *
     * @param rateForm
     * @return
     */
    public DataTableResults<RateBean> getDatatables(RateForm rateForm) {
        return rateDAO.getDatatables(commonService, rateForm);
    }

    /**
     * saveOrUpdate
     *
     * @param entity
     */
    @Transactional
    public void saveOrUpdate(RateBO entity) {
        rateDAO.save(entity);
    }

    /**
     * delete
     *
     * @param entity
     */
    public void delete(RateBO entity) {
        rateDAO.delete(entity);
    }

    public List<RateBO> findByIdObjective(Integer idObjective) {
        return rateDAO.findByIdObjective(idObjective);
    }
}
