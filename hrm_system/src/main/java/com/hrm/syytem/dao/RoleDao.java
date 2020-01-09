package com.hrm.syytem.dao;

import com.hrm.domain.system.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/8 15:13
 */
public interface RoleDao extends JpaRepository<Role,String>, JpaSpecificationExecutor<Role> {

}
