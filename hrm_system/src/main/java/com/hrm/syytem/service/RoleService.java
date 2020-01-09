package com.hrm.syytem.service;

import com.hrm.common.service.BaseService;
import com.hrm.common.utils.IdWorker;
import com.hrm.common.utils.PermissionConstants;
import com.hrm.domain.system.Permission;
import com.hrm.domain.system.Role;
import com.hrm.syytem.dao.PermissionDao;
import com.hrm.syytem.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/8 15:14
 */
@Service
public class RoleService extends BaseService {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private PermissionDao permissionDao;
    public void save(Role role) {
        //填充其他参数
        role.setId(idWorker.nextId() + "");
        role.setCompanyId("1");
        // todo 添加相应的权限
        roleDao.save(role);
    }

    public void update(Role role) {
        Role targer = roleDao.getOne(role.getId());
        targer.setDescription(role.getDescription());
        targer.setName(role.getName());
        roleDao.save(targer);
    }

    public Role findById(String id) {
        return roleDao.findById(id).get();
    }

    public void delete(String id) {
        roleDao.deleteById(id);
    }

    public Page<Role> findSearch(String companyId, int page, int size) {
        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query,
                                         CriteriaBuilder cb) {
                return cb.equal(root.get("companyId").as(String.class), companyId);
            }
        };
        return roleDao.findAll(specification, PageRequest.of(page - 1, size));
    }

    public void assignRoles(String roleId, List<String> permIds) {
        Role role = roleDao.findById(roleId).get();
        Set<Permission> perms = new HashSet<>();
        for (String permId: permIds){
            Permission permission = permissionDao.findById(permId).get();
            List<Permission> apilist = permissionDao.findByTypeAndPid(PermissionConstants.PY_API,permission.getPid());
            perms.addAll(apilist);
            perms.add(permission);
        }
        role.setPermissions(perms);
        roleDao.save(role);
    }
}
