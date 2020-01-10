package com.hrm.syytem.client;

import com.hrm.common.entity.Result;
import com.hrm.domain.company.Department;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/10 14:16
 */
@FeignClient(value = "hrm-company")
public interface DepartmentFeginClient {
    //@RequestMapping注解用于对被调用的微服务进行地址映射
    @RequestMapping(value = "/company/department/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable("id") String id) throws Exception;


    @RequestMapping(value = "/company/department/search", method = RequestMethod.POST)
    public Department findByCode(@RequestParam(value = "code") String code, @RequestParam(value = "companyId") String companyId);

}
