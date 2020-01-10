package com.hrm.syytem.realm;

import com.hrm.common.realm.HrmRealm;
import com.hrm.domain.system.Permission;
import com.hrm.domain.system.User;
import com.hrm.domain.system.response.ProfileResult;
import com.hrm.syytem.service.PermissionService;
import com.hrm.syytem.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/9 17:15
 * 用户微服务的realm
 */
public class UserRealm extends HrmRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;
    // 自定义认证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 1 获取用户的手机号码和密码
        // 2 根据手机号码查询用户
        // 3 判断用户是否存在，用户密码和输入的是否一致
        // 4 构造返回数据 profileResult
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String mobile = upToken.getUsername();
        String password = new String( upToken.getPassword());
        //2.根据用户名查询数据库
        User user = userService.findByMobile(mobile);
        //3.判断用户是否存在或者密码是否一致
        if(user != null && user.getPassword().equals(password)) {
            //4.如果一致返回安全数据
            ProfileResult result = null;
            if ("user".equals(user.getLevel())){
                result = new ProfileResult(user);
            }else {
                Map map = new HashMap();
                if ("coAdmin".equals(user.getLevel())){
                    map.put("enVisible","1");
                }
                List<Permission> list = permissionService.findAll(map);
                result = new ProfileResult(user, list);
            }
            //构造方法：安全数据，密码，realm域名
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(result,user.getPassword(),this.getName());
            return info;
        }
        // 抛出异常
        return null;
    }

}
