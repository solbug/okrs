package com.okr.objective.controller;

import com.okr.member.service.MemberService;
import com.okr.objective.bean.ObjectiveBean;
import com.okr.objective.bo.ObjectiveBO;
import com.okr.objective.form.ObjectiveForm;
import com.okr.objective.service.ObjectiveService;
import com.okr.rate.bean.RateBean;
import com.okr.rate.bo.RateBO;
import com.okr.rate.service.RateService;
import com.okr.team.service.TeamService;
import com.okr.utils.Constants;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import com.okr.utils.Response;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/objective")
public class ObjectiveController {

    @Autowired
    private ObjectiveService objectiveService;

    @Autowired
    private RateService rateService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TeamService teamService;

    /**
     * findById
     *
     * @param objectiveId
     * @return
     */
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @GetMapping(path = "/{objectiveId}")
    public @ResponseBody
    Response findById(@PathVariable Integer objectiveId) {

        ObjectiveBO objectiveBO = objectiveService.findById(objectiveId);
        if (objectiveBO == null) {
            return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
        }
        ObjectiveBean objectiveBean = new ObjectiveBean();
        BeanUtils.copyProperties(objectiveBO, objectiveBean);
        List<RateBean> rateBeans = new ArrayList<>();
        List<RateBO> rateBOS = rateService.findByIdObjective(objectiveId);
        for (RateBO bo : rateBOS) {
            RateBean bean = new RateBean();
            BeanUtils.copyProperties(bo, bean);
            rateBeans.add(bean);
        }
        objectiveBean.setListRate(rateBeans);

        return Response.success("Get data success").withData(objectiveBean);
    }

    /**
     * processSearch
     *
     * @param form
     * @return DataTableResults
     */
    @PreAuthorize("hasRole('Manager')")
    @GetMapping(path = "/get-all")
    public @ResponseBody
    DataTableResults<ObjectiveBean> processSearch(ObjectiveForm form) {
        DataTableResults<ObjectiveBean> results = objectiveService.getDatatables(form);
        return results;
    }

    /**
     * @param form
     * @return DataTableResults
     */
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @GetMapping(path = "/get-objective")
    public @ResponseBody
    Response getMenuWithType(ObjectiveForm form) {
        return Response.success("Get list success")
                .withData(objectiveService.getObjectiveWithParent(form));
    }

    //Export Excel
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @GetMapping(path = "/export-excel")
    public @ResponseBody
    Response exportExcel(ObjectiveForm form) throws FileNotFoundException, IOException, ClassNotFoundException, InvalidFormatException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DataTableResults<ObjectiveBean> results = objectiveService.getObjective(form);
        List<ObjectiveBean> objectiveBeans = results.getData();
        XSSFWorkbook wb = new XSSFWorkbook(new File("okrs.xlsx"));
        XSSFSheet sheet = wb.getSheetAt(1);

        XSSFRelation row = null; //khởi tạo hàng
        XSSFCell cell = null; //khởi tạo ô dữ liệu
        int size = 4;
        cell = sheet.getRow(3).getCell(2);
        cell.setCellValue(objectiveBeans.get(0).getMemberName());
        cell = sheet.getRow(6).getCell(2);
        cell.setCellValue(sdf.format(objectiveBeans.get(0).getStartDate()));
        cell = sheet.getRow(6).getCell(3);
        cell.setCellValue(sdf.format(objectiveBeans.get(0).getEndDate()));
        cell = sheet.getRow(8).getCell(2);
        cell.setCellValue(objectiveBeans.get(0).getObjectiveName());

        FileOutputStream fileOutputStream = new FileOutputStream("objective.xlsx");
        wb.write(fileOutputStream);
        fileOutputStream.close();
        wb.close();
        return Response.success("Success");
    }

    /**
     * saveOrUpdate ObjectiveBO
     *
     * @param form
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Leader', 'Member')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    Response saveOrUpdate(HttpServletRequest request, @RequestBody ObjectiveForm form) {
        Integer id = Mixin.NVL(form.getId());
        ObjectiveBO objectiveBO;
        if (id > 0L) {
            objectiveBO = objectiveService.findById(id);
            if (objectiveBO == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
        } else {
            objectiveBO = new ObjectiveBO();
            objectiveBO.setStatus(Constants.ACTIVE.NOT_ACTICE);
        }
        if (form.getStatus() != null) {
            objectiveBO.setStatus(form.getStatus());
        }
        objectiveBO.setObjectiveName(form.getObjectiveName());
        // Nếu là phần tử cha thì set level là 1 còn là con thì level là 2
        if (form.getIdParent() == null || form.getIdParent() == 0) {
            objectiveBO.setLevel(Constants.LEVEL.ONE);
        } else if (form.getIdParent() == 1) {
            objectiveBO.setLevel(Constants.LEVEL.TWO);
        } else if (form.getIdParent() == 2) {
            objectiveBO.setLevel(Constants.LEVEL.THREE);
        } else {
            objectiveBO.setLevel(Constants.LEVEL.FOUR);
        }

        objectiveBO.setStartDate(form.getStartDate());
        objectiveBO.setEndDate(form.getEndDate());
        objectiveBO.setDescription(form.getDescription());
        objectiveBO.setIdParent(form.getIdParent());
        objectiveBO.setIdMember(form.getIdMember());
        objectiveBO.setIdDepartment(form.getIdDepartment());
        objectiveBO.setIdTeam(form.getIdTeam());
        objectiveService.saveOrUpdate(objectiveBO);
        return Response.success(Constants.RESPONSE_CODE.SUCCESS).withData(objectiveBO);
    }

    /**
     * delete
     *
     * @param objectId
     * @return
     */
    @PreAuthorize("hasRole('Manager')")
    @DeleteMapping(path = "/{objectId}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Response delete(@PathVariable Integer objectId) {

        ObjectiveBO bo;
        if (objectId > 0L) {
            bo = objectiveService.findById(objectId);
            if (bo == null) {
                return Response.warning(Constants.RESPONSE_CODE.RECORD_DELETED);
            }
            objectiveService.delete(bo);
            return Response.success("Delete objective success " + bo.getObjectiveName(), Constants.RESPONSE_CODE.DELETE_SUCCESS);
        } else {
            return Response.error(Constants.RESPONSE_CODE.ERROR);
        }
    }
}
