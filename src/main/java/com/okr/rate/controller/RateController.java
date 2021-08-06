package com.okr.rate.controller;


import com.okr.rate.bean.RateBean;
import com.okr.rate.bo.RateBO;
import com.okr.rate.form.RateForm;
import com.okr.rate.service.RateService;
import com.okr.utils.Constants;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import com.okr.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/rate")
public class RateController {
    @Autowired
    private RateService rateService;

    /**
     * findById
     *
     * @param idRate
     * @return
     */
    @GetMapping(path = "/{idRate}")
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    public @ResponseBody
    Response findById(@PathVariable Integer idRate) {

        RateBO rateBO = rateService.findById(idRate);
        if (rateBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        return Response.success("Get detail success").withData(rateBO);
    }

    /**
     * processSearch
     *
     * @param form
     * @return DataTableResults
     */
    @GetMapping(path = "/get-all")
    @PreAuthorize("hasAnyRole('Admin')")
    public @ResponseBody
    DataTableResults<RateBean> processSearch(RateForm form) {

        return rateService.getDatatables(form);
    }

    /**
     * saveOrUpdate RateBo
     *
     * @param form
     * @return
     * @throws Exception
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    public @ResponseBody
    Response saveOrUpdate(@RequestBody RateForm form) throws Exception {
        Integer id = Mixin.NVL(form.getId());
        RateBO rateBO;
        if (id > 0L) {
            rateBO = rateService.findById(id);
            if (rateBO == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
        } else {
            rateBO = new RateBO();
        }
        rateBO.setRateName(form.getRateName());
        rateBO.setComment(form.getComment());
        rateBO.setIdMember(form.getIdMember());
        rateBO.setIdObjective(form.getIdObjective());
        rateService.saveOrUpdate(rateBO);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS).withData(rateBO);
    }

    /**
     * delete
     *
     * @param rateId
     * @return
     */
    @DeleteMapping(path = "/{rateId}")
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Response delete(@PathVariable Integer rateId) {

        RateBO bo;
        if (rateId > 0L) {
            bo = rateService.findById(rateId);
            if (bo == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
            rateService.delete(bo);
            return Response.success(Constants.RESPONSE_CODE.DELETE_SUCCESS);
        } else {
            return Response.error(Constants.RESPONSE_CODE.ERROR);
        }
    }
}
