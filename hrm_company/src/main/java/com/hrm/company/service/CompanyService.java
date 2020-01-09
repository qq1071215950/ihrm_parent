package com.hrm.company.service;

import com.hrm.common.utils.IdWorker;
import com.hrm.company.dao.CompanyDao;
import com.hrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/7 14:32
 */
@Service
public class CompanyService {

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private IdWorker idWorker;
    // 保存
    public Company add(Company company) {
        company.setId(idWorker.nextId() + "");
        company.setCreateTime(new Date());
        company.setState(1);//启用
        company.setAuditState("0"); //待审核
        company.setBalance(0d);
        return companyDao.save(company);
    }
    // 更新
    public Company update(Company company) {
        return companyDao.save(company);
    }
    // 删除
    public void deleteById(String id) {
        companyDao.deleteById(id);
    }
    // 根据id查询企业
    public Company findById(String id) {
        return companyDao.findById(id).get();
    }
    // 查询列表
    public List<Company> findAll() {
        return companyDao.findAll();
    }
}
