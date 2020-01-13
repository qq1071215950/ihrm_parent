package com.hrm.syytem.controller;

import com.hrm.common.controller.BaseController;
import com.hrm.common.entity.PageResult;
import com.hrm.common.entity.Result;
import com.hrm.common.entity.ResultCode;
import com.hrm.common.utils.JwtUtil;
import com.hrm.domain.system.User;
import com.hrm.domain.system.response.ProfileResult;
import com.hrm.domain.system.response.UserResult;
import com.hrm.syytem.client.DepartmentFeginClient;
import com.hrm.syytem.service.PermissionService;
import com.hrm.syytem.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DepartmentFeginClient departmentFeginClient;


    /**
     * 保存用户头像
     * @param id
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/user/upload/{id}")
    public Result upload(@PathVariable String id,@RequestParam(name = "file")
            MultipartFile file) throws Exception {
    String image = userService.uploadImage(id, file);
    return new Result(ResultCode.SUCCESS,image);
 }
    /**
     * 导入excel
     * 文件上传
     * @return
     */
    @RequestMapping(value = "/user/import",method = RequestMethod.POST)
    public Result importUser(@RequestParam(name = "file") MultipartFile file) throws IOException {
        // 1 解析 excel
        // 1 工作簿 不同的实现对象 2007
        Workbook wb =  new XSSFWorkbook(file.getInputStream());
        // 获取sheet
        Sheet sheet = wb.getSheetAt(0);// 索引页
        // 循环得到行
        List<User> list = new ArrayList<>();
        for (int rowNum = 1 ; rowNum <= sheet.getLastRowNum(); rowNum++){
            Row row = sheet.getRow(rowNum);
            Object[] values = new Object[row.getLastCellNum()];
            for (int cellNum = 2; cellNum < row.getLastCellNum(); cellNum++){
                // 得到单元格
                Cell cell = row.getCell(cellNum);
                // 获取单元的内容
                Object value = getCellValue(cell);
                values[cellNum] = value;
            }
            // 2 获取用户数据列表
            User user = new User(values);
            list.add(user);
        }
        // 3 批量保存用户数据
        userService.svaeAll(list, companyId, companyName);
        return new Result(ResultCode.SUCCESS);
    }
    private static Object getCellValue(Cell cell){
        // 1 获取单元格的数据类型
        CellType cellType = cell.getCellType();
        Object value = null;
        switch (cellType){
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                // 日期和数字
                if (DateUtil.isCellDateFormatted(cell)){
                    value = cell.getDateCellValue();
                }else {
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA://公式
                value = cell.getCellFormula();
                break;
        }
        return value;
    }
    //测试通过系统微服务调用企业微服务方法
    @RequestMapping(value = "/test/{id}")
    public Result findDeptById(@PathVariable String id) throws Exception {
        Result dept = departmentFeginClient.findById(id);
        System.out.println(dept);
        return dept;
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Result save(@RequestBody User user) {
        //1.设置保存
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        // todo 解密处理的
        String password = new Md5Hash(user.getPassword(), user.getMobile(), 3).toString();// 参数 密码 盐值 加密次数
        user.setPassword(password);
        user.setLevel("user");
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
     * name 为资源访问表示
     */
    @RequiresPermissions(value = "api-user-delete")// 没有权限事，抛出异常
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE,name = "api-user-delete")
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
        // todo shiro的登录方式
        try {
            // 密码加密
           // password= new Md5Hash(password, mobile, 3).toString();// 参数 密码 盐值 加密次数
            // 1 构造登录令牌
            UsernamePasswordToken upToken = new UsernamePasswordToken(mobile, password);
            // 2 获取subject
            Subject subject = SecurityUtils.getSubject();
            // 3 调用login方法
            subject.login(upToken);
            // 4获取sessionid
            String sessionId = (String) subject.getSession().getId();
            // 5 构造返回结果
            return new Result(ResultCode.SUCCESS,sessionId);
        }catch (Exception e){
            return new Result(ResultCode.PASSWORD_IS_NOT_CORRECT);
        }

        // 查询用户


        // todo jwt的登录方式
       /*
       User user = userService.findByMobile(mobile);
       if (user == null || !(password.equals(user.getPassword()))){
            return new Result(ResultCode.PASSWORD_IS_NOT_CORRECT);
        }else {
            // todo 加入可访问的接口
            // 添加可访问的资源api
            StringBuilder sb = new StringBuilder();
            for (Role role: user.getRoles()){
                for (Permission permission: role.getPermissions()){
                    if (permission.getType()== PermissionConstants.PY_API){
                        sb.append(permission.getCode()).append(",");
                    }
                }
            }
            Map<String,Object> map = new HashMap<>();
            map.put("companyId",user.getCompanyId());
            map.put("apis",sb.toString());
            map.put("companyName",user.getCompanyName());

            String token = jwtUtil.createJWT(user.getId(), user.getUsername(), map);
            return new Result(ResultCode.SUCCESS,token);
        }*/
    }


    /**
     * 用户登录成功后获取用户信息
     * @param
     * @return
     */
    // todo 加入api接口访问权限控制报空指针问题

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public Result profile(HttpServletRequest request) {

        // todo Jwt的形式获取资源
       /* String userId = claims.getId();
        User user = userService.findById(userId);
        ProfileResult result = null;
        // 获取相应的权限
       if ("user".equals(user.getLevel())){
           result = new ProfileResult(user);
       }else {
            Map map = new HashMap();
            if ("coAdmin".equals(user.getLevel())){
                map.put("enVisible","1");
            }
            List<Permission> list = permissionService.findAll(map);
            result = new ProfileResult(user, list);
       }*/

       // todo shiro形式获取资源
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        ProfileResult result = (ProfileResult) principals.getPrimaryPrincipal();
        return new Result(ResultCode.SUCCESS,result);
    }


}
