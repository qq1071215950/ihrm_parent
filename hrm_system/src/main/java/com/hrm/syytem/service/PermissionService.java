package com.hrm.syytem.service;

import com.hrm.common.entity.ResultCode;
import com.hrm.common.exception.CommonException;
import com.hrm.common.service.BaseService;
import com.hrm.common.utils.BeanMapUtils;
import com.hrm.common.utils.IdWorker;
import com.hrm.common.utils.PermissionConstants;
import com.hrm.domain.system.Permission;
import com.hrm.domain.system.PermissionApi;
import com.hrm.domain.system.PermissionMenu;
import com.hrm.domain.system.PermissionPoint;
import com.hrm.syytem.dao.PermissionApiDao;
import com.hrm.syytem.dao.PermissionDao;
import com.hrm.syytem.dao.PermissionMenuDao;
import com.hrm.syytem.dao.PermissionPointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/8 15:33
 */
@Service
@Transactional
public class PermissionService extends BaseService {

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private PermissionMenuDao permissionMenuDao;

    @Autowired
    private PermissionPointDao permissionPointDao;

    @Autowired
    private PermissionApiDao permissionApiDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 1.保存
     */
    public void save(Map<String, Object> map) throws Exception {
        //设置主键的值
        String id = idWorker.nextId()+"";
        Permission parn = BeanMapUtils.mapToBean(map,Permission.class);
        parn.setId(id);
        int type = parn.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                PermissionMenu meau = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                meau.setId(id);
                permissionMenuDao.save(meau);
                break;
            case PermissionConstants.PY_POINT:
                PermissionPoint point = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                point.setId(id);
                permissionPointDao.save(point);
                break;
            case PermissionConstants.PY_API:
                PermissionApi api = BeanMapUtils.mapToBean(map, PermissionApi.class);
                api.setId(id);
                permissionApiDao.save(api);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
        //调用dao保存部门
        permissionDao.save(parn);
    }

    /**
     * 2.更新用户
     */
    public void update(Map<String, Object> map) throws Exception {
        // 1 权限
        Permission parn = BeanMapUtils.mapToBean(map,Permission.class);
        Permission permission=permissionDao.findById(parn.getId()).get();
        permission.setName(parn.getName());
        permission.setCode(parn.getCode());
        permission.setDescription(parn.getDescription());
        permission.setEnVisible(parn.getEnVisible());
        // 2 资源
        int type = parn.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                PermissionMenu meau = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                meau.setId(parn.getId());
                permissionMenuDao.save(meau);
                break;
            case PermissionConstants.PY_POINT:
                PermissionPoint point = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                point.setId(parn.getId());
                permissionPointDao.save(point);
                break;
            case PermissionConstants.PY_API:
                PermissionApi api = BeanMapUtils.mapToBean(map, PermissionApi.class);
                api.setId(parn.getId());
                permissionApiDao.save(api);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
        //调用dao保存部门
        permissionDao.save(permission);
    }

    /**
     * 3.根据id查询用户
     */
    public Map<String, Object> findById(String id) {
        // 1 查询权限
        Permission permission = permissionDao.findById(id).get();
        int type = permission.getType();
        // 2 根据权限的类型查询资源
        Object object = null;
        if (type == PermissionConstants.PY_MENU){
            object = permissionMenuDao.findById(id).get();
        }else if (type == PermissionConstants.PY_POINT){
            object = permissionPointDao.findById(id).get();
        }else if (type == PermissionConstants.PY_API){
            object = permissionApiDao.findById(id).get();
        }else {
            throw new CommonException(ResultCode.FAIL);
        }
        Map<String,Object> map = BeanMapUtils.beanToMap(object);
        map.put("name",permission.getName());
        map.put("type",permission.getType());
        map.put("code",permission.getCode());
        map.put("description",permission.getDescription());
        map.put("pid",permission.getPid());
        map.put("enVisible",permission.getEnVisible());
        // 构造map集合
        return map;
    }

    /**
     * 4.查询全部用户列表
     *      参数：map集合的形式
     *          hasDept
     *          departmentId
     *          companyId
     *
     */
    public List<Permission> findAll(Map<String,Object> map) {
        //1.需要查询条件
        Specification<Permission> spec = new Specification<Permission>() {
            /**
             * 动态拼接查询条件
             * @return
             */
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                //根据pid
                if(!StringUtils.isEmpty(map.get("pid"))) {
                    list.add(criteriaBuilder.equal(root.get("pid").as(String.class),(String)map.get("pid")));
                }
                //根据enVisible
                if(!StringUtils.isEmpty(map.get("enVisible"))) {
                    list.add(criteriaBuilder.equal(root.get("enVisible").as(String.class),(String)map.get("enVisible")));
                }
                if(!StringUtils.isEmpty(map.get("type"))) {
                   String ty = (String) map.get("type");
                   CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("type"));
                   if ("0".equals(ty)){
                        in.value(1).value(2);
                   }else {
                       in.value(Integer.parseInt(ty));
                   }
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };

        return permissionDao.findAll(spec);
    }

    /**
     * 5.根据id删除权限和资源
     */
    public void deleteById(String id) {
        // 1 权限
        Permission permission=permissionDao.findById(id).get();
        permissionDao.delete(permission);
        // 2 资源
        int type = permission.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                permissionMenuDao.deleteById(id);
                break;
            case PermissionConstants.PY_POINT:
                permissionPointDao.deleteById(id);
                break;
            case PermissionConstants.PY_API:
                permissionApiDao.deleteById(id);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
    }

}
