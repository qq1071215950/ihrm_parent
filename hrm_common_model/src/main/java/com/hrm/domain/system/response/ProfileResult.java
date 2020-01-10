package com.hrm.domain.system.response;

import com.hrm.domain.system.Permission;
import com.hrm.domain.system.Role;
import com.hrm.domain.system.User;
import lombok.Getter;
import lombok.Setter;
import org.crazycake.shiro.AuthCachePrincipal;

import java.io.Serializable;
import java.util.*;

/**
 * @author jiange
 * @version 1.0
 * @date 2020/1/9 10:43
 */
@Setter
@Getter
public class ProfileResult implements Serializable, AuthCachePrincipal {
    private String mobile;
    private String username;
    private String company;
    private String companyId;
    private Map<String,Object> roles = new HashMap<>();

    public ProfileResult(User user){
        this.mobile = user.getMobile();
        this.username = user.getUsername();
        this.company = user.getCompanyName();
        this.companyId = user.getCompanyId();
        Set<Role> roles = user.getRoles();

        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        for (Role role: roles){
            Set<Permission> permissions = role.getPermissions();
            for (Permission pern: permissions){
                String code = pern.getCode();
                if(pern.getType() == 1) {
                    menus.add(code);
                }else if(pern.getType() == 2) {
                    points.add(code);
                 }else {
                    apis.add(code);
                }
            }
        }
        this.roles.put("menus",menus);
        this.roles.put("points",points);
        this.roles.put("apis",apis);
    }

    public ProfileResult(User user, List<Permission> list) {
        this.mobile = user.getMobile();
        this.username = user.getUsername();
        this.company = user.getCompanyName();

        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        for (Permission permission : list){
            String code = permission.getCode();
            if(permission.getType() == 1) {
                menus.add(code);
            }else if(permission.getType() == 2) {
                points.add(code);
            }else {
                apis.add(code);
            }
        }
        this.roles.put("menus",menus);
        this.roles.put("points",points);
        this.roles.put("apis",apis);
    }

    @Override
    public String getAuthCacheKey() {
        return null;
    }
}
