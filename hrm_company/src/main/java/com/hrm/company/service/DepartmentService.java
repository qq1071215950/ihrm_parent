package com.hrm.company.service;

import com.hrm.common.service.BaseService;
import com.hrm.common.utils.IdWorker;
import com.hrm.company.dao.DepartmentDao;
import com.hrm.domain.company.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/7 17:10
 */
@Service
public class DepartmentService extends BaseService {

    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private IdWorker idWorker;

    // 保存部门
    public void save(Department department){
        String id = idWorker.nextId()+"";
        department.setId(id);
        departmentDao.save(department);

    }
    // 更新部门
    public void update(Department department) {
        Department department1 = departmentDao.findById(department.getId()).get();
        department1.setCode(department.getCode());
        department1.setIntroduce(department.getIntroduce());
        department1.setName(department.getName());
        departmentDao.save(department1);
    }
     // 根据id查询部门
     public Department findById(String id) {
         return departmentDao.findById(id).get();
     }

    // 查询部门列表
    public List<Department> findAll(String companyId){
        List<Department> list = departmentDao.findAll(getSpec(companyId));
        return list;
    }
    public void deleteById(String id){
        departmentDao.deleteById(id);
    }

    /**
     *
     * @param code
     * @param companyId
     * @return
     */
    public Department findByCode(String code, String companyId) {
        Department department = departmentDao.findByCodeAndCompanyId(code, companyId);
        return department;
    }
}
