package com.okr.objective.service;

import com.okr.member.bo.RoleBO;
import com.okr.member.service.RoleService;
import com.okr.objective.bean.DataObjectiveBean;
import com.okr.objective.bean.ObjectiveBean;
import com.okr.objective.bo.ObjectiveBO;
import com.okr.objective.dao.ObjectiveDAO;
import com.okr.objective.form.ObjectiveForm;
import com.okr.utils.CommonService;
import com.okr.utils.Constants;
import com.okr.utils.DataTableResults;
import com.okr.utils.Mixin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ObjectiveService {
    @Autowired
    private ObjectiveDAO objectiveDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RoleService roleService;

    public ObjectiveBO findById(Integer objectiveId) {
        return objectiveDAO.findById(objectiveId).orElse(null);
    }

    /**
     * getDatatables
     *
     * @param objectiveForm
     * @return
     */
    public DataTableResults<ObjectiveBean> getDatatables(ObjectiveForm objectiveForm) {
        DataTableResults<ObjectiveBean> results = objectiveDAO.getDatatables(commonService, objectiveForm);
        List<ObjectiveBean> beans = results.getData();

        List<ObjectiveBean> beans2 = new ArrayList<>();
        for (ObjectiveBean bean: beans){
            if (bean.getIdParent() != null  && bean.getLevel().equals(Constants.LEVEL.TWO)){
                Optional<ObjectiveBO> objectiveBO = objectiveDAO.findById(bean.getIdParent());
            }
            beans2.add(bean);
        }
        results.setData(beans2);
        return results;
    }

    /**
     *
     * @param objectiveForm
     * @return
     */
    public List<DataObjectiveBean> getObjectiveWithParent(ObjectiveForm objectiveForm) {
        // Gọi hàm lấy danh sách Objective từ db
        List<ObjectiveBean> beans = objectiveDAO.getObjectWithParent(commonService, objectiveForm);
        // Lọc xem là mục tiêu cấp 1 hay 2
        List<ObjectiveBean> beansLevel1 = beans.stream().filter(e -> e.getLevel() == 1).collect(Collectors.toList());
        List<ObjectiveBean> beansLevel2 = beans.stream().filter(e -> e.getLevel() == 2).collect(Collectors.toList());
        List<ObjectiveBean> beansLevel3 = beans.stream().filter(e -> e.getLevel() == 3).collect(Collectors.toList());
        List<ObjectiveBean> beansLevel4 = beans.stream().filter(e -> e.getLevel() == 4).collect(Collectors.toList());
        // Tạo 1 data kết quả trả về
        List<DataObjectiveBean> dataObjectiveBeans = new ArrayList<>();
        //Set giá trị cho các mục tiêu cấp 1
        for (ObjectiveBean bean : beansLevel1) {
            dataObjectiveBeans.add(new DataObjectiveBean(bean, null, null, null));
        }
        // Khời tạo 1 list danh sách mục tiêu con
        List<ObjectiveBean> listChild1 = new ArrayList<>();

        // Thêm list mục tiêu con vào từng mục tiêu cha nếu có
        for (ObjectiveBean bean : beansLevel2) {
            for (DataObjectiveBean dataObjectiveBean : dataObjectiveBeans) {
                if (bean.getIdParent().equals(dataObjectiveBean.getParentObject().getIdParent() + 1)) {
                    listChild1.add(bean);
                    dataObjectiveBean.setListChildObjective1(listChild1);
                }
            }
        }

        // Khời tạo 1 list danh sách mục tiêu con
        List<ObjectiveBean> listChild2 = new ArrayList<>();
        for (ObjectiveBean bean : beansLevel3) {
            for (DataObjectiveBean dataObjectiveBean : dataObjectiveBeans) {
                if (bean.getIdParent().equals(dataObjectiveBean.getParentObject().getIdParent() + 2)) {
                    listChild2.add(bean);
                    dataObjectiveBean.setListChildObjective2(listChild2);
                }
            }
        }

        // Khời tạo 1 list danh sách mục tiêu con
        List<ObjectiveBean> listChild3 = new ArrayList<>();
        for (ObjectiveBean bean : beansLevel4) {
            for (DataObjectiveBean dataObjectiveBean : dataObjectiveBeans) {
                if (bean.getIdParent().equals(dataObjectiveBean.getParentObject().getIdParent() + 3)) {
                    listChild3.add(bean);
                    dataObjectiveBean.setListChildObjective3(listChild3);
                }
            }
        }

        return dataObjectiveBeans;
    }

    /**
     * saveOrUpdate
     *
     * @param entity
     */
    @Transactional
    public void saveOrUpdate(ObjectiveBO entity) {
        objectiveDAO.save(entity);
    }

    /**
     * delete
     *
     * @param entity
     */
    public void delete(ObjectiveBO entity) {
        objectiveDAO.delete(entity);
    }

}
