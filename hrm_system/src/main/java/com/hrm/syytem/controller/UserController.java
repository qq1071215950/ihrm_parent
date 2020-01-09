package com.hrm.syytem.controller;

import com.hrm.common.controller.BaseController;
import com.hrm.common.entity.PageResult;
import com.hrm.common.entity.Result;
import com.hrm.common.entity.ResultCode;
import com.hrm.common.exception.CommonException;
import com.hrm.common.utils.JwtUtil;
import com.hrm.domain.system.User;
import com.hrm.domain.system.response.ProfileResult;
import com.hrm.domain.system.response.UserResult;
import com.hrm.syytem.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//1.解决跨域
@CrossOrigin
//2.声明restContoller
@RestController
//3.设置父路径
@RequestMapping(value="/sys")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 保存
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Result save(@RequestBody User user) {
        //1.设置保存的企业id
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        //2.调用service完成保存企业
        userService.save(user);
        //3.构造返回结果
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 查询企业的部门列表
     * 指定企业id
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Result findAll(int page, int size, @RequestParam Map map) {
        //1.获取当前的企业id
        map.put("companyId",companyId);
        //2.完成查询
        Page<User> pageUser = userService.findAll(map,page,size);
        //3.构造返回结果
        PageResult pageResult = new PageResult(pageUser.getTotalElements(),pageUser.getContent());
        return new Result(ResultCode.SUCCESS, pageResult);
    }

    /**
     * 根据ID查询user
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable(value = "id") String id) {
        // 查出相应的权限 roleIds
        User user = userService.findById(id);
        UserResult userResult = new UserResult(user);
        return new Result(ResultCode.SUCCESS, userResult);
    }

    /**
     * 修改User
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable(value = "id") String id, @RequestBody User user) {
        //1.设置修改的部门id
        user.setId(id);
        //2.调用service更新
        userService.update(user);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据id删除
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable(value = "id") String id) {
        userService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }


    /**
     * @param map 分配角色
     * @return
     */
    @RequestMapping(value = "/user/assignRoles", method = RequestMethod.PUT)
    public Result assignRoles(@RequestBody Map<String,Object> map) {
        //1.获取被分配的用户id
        String userId = (String) map.get("id");
        //2.获取到角色的id列表
        List<String> roleIds = (List<String>) map.get("roleIds");
        //3.调用service完成角色分配
        userService.assignRoles(userId,roleIds);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody Map<String,String> loginMap) {
        String mobile = loginMap.get("mobile");
        String password = loginMap.get("password");
        // 查询用户
        User user = userService.findByMobile(mobile);
        if (user == null || !(password.equals(user.getPassword()))){
            return new Result(ResultCode.PASSWORD_IS_NOT_CORRECT);
        }else {
            Map<String,Object> map = new HashMap<>();
            map.put("companyId",user.getCompanyId());
            map.put("companyName",user.getCompanyName());
            String token = jwtUtil.createJWT(user.getId(), user.getUsername(), map);
            return new Result(ResultCode.SUCCESS,token);
        }
    }


    /**
     * 用户登录成功后获取用户信息
     * @param
     * @return
     */
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public Result profile(HttpServletRequest request) {
        // 获取用户id
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        String token = authorization.replace("Bearer ", "");
        // 从请求头中获取token信息
        Claims claims = jwtUtil.parseJWT(token);
        if (claims == null) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        String userId = claims.getId();
        User user = userService.findById(userId);
        return new Result(ResultCode.SUCCESS,new ProfileResult(user));
    }


}
