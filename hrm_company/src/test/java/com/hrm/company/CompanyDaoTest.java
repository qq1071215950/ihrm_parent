package com.hrm.company;

import com.hrm.company.dao.CompanyDao;
import com.hrm.domain.company.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/7 14:27
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CompanyDaoTest {
    @Autowired
    private CompanyDao companyDao;

    @Test
    public void testFind(){
        Company company = companyDao.findById("1").get();
        System.out.println(company.toString());
    }
}
