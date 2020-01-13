package com.hrm.employee.controller;

import com.hrm.common.controller.BaseController;
import com.hrm.common.entity.PageResult;
import com.hrm.common.entity.Result;
import com.hrm.common.entity.ResultCode;
import com.hrm.common.poi.ExcelExportUtil;
import com.hrm.common.poi.ExcelImportUtil;
import com.hrm.common.utils.BeanMapUtils;
import com.hrm.common.utils.DownloadUtils;
import com.hrm.domain.employee.*;
import com.hrm.domain.employee.response.EmployeeReportResult;
import com.hrm.domain.system.User;
import com.hrm.employee.service.*;
import net.sf.jasperreports.engine.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/employees")
public class EmployeeController extends BaseController {
    @Autowired
    private UserCompanyPersonalService userCompanyPersonalService;
    @Autowired
    private UserCompanyJobsService userCompanyJobsService;
    @Autowired
    private ResignationService resignationService;
    @Autowired
    private TransferPositionService transferPositionService;
    @Autowired
    private PositiveService positiveService;
    @Autowired
    private ArchiveService archiveService;


    /**
     * 打印pdf
     * @param id
     * @throws IOException
     */
    @RequestMapping(value = "/{id}/pdf", method = RequestMethod.GET)
    public void pdf(@PathVariable String id) throws IOException {
        //1.引入jasper文件
        Resource resource = new ClassPathResource("templates/profile.jasper");
        FileInputStream fis = new FileInputStream(resource.getFile());
        //2.构造数据
        //a.用户详情数据
        UserCompanyPersonal personal = userCompanyPersonalService.findById(id);
        //b.用户岗位信息数据
        UserCompanyJobs jobs = userCompanyJobsService.findById(id);
        //c.用户头像       域名 / id
        // todo 需要解决图片url问题
        String staffPhoto = "http://pkbivgfrm.bkt.clouddn.com/" + id;
        System.out.println(staffPhoto);
        //3.填充pdf模板数据，并输出pdf
        Map params = new HashMap();
        Map<String, Object> map1 = BeanMapUtils.beanToMap(personal);
        Map<String, Object> map2 = BeanMapUtils.beanToMap(jobs);
        params.putAll(map1);
        params.putAll(map2);
        params.put("staffPhoto", "staffPhoto");
        ServletOutputStream os = response.getOutputStream();
        try {
            JasperPrint print = JasperFillManager.fillReport(fis, params, new
                    JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(print, os);
        } catch (JRException e) {
            e.printStackTrace();
        } finally {
            os.flush();
        }
    }

    /**
     * 员工个人信息保存
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.PUT)
    public Result savePersonalInfo(@PathVariable(name = "id") String uid, @RequestBody Map map) throws Exception {
        UserCompanyPersonal sourceInfo = BeanMapUtils.mapToBean(map, UserCompanyPersonal.class);
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyPersonal();
        }
        sourceInfo.setUserId(uid);
        sourceInfo.setCompanyId(super.companyId);
        userCompanyPersonalService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工个人信息读取
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.GET)
    public Result findPersonalInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyPersonal info = userCompanyPersonalService.findById(uid);
        if(info == null) {
            info = new UserCompanyPersonal();
            info.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,info);
    }

    /**
     * 员工岗位信息保存
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.PUT)
    public Result saveJobsInfo(@PathVariable(name = "id") String uid, @RequestBody UserCompanyJobs sourceInfo) throws Exception {
        //更新员工岗位信息
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyJobs();
            sourceInfo.setUserId(uid);
            sourceInfo.setCompanyId(super.companyId);
        }
        userCompanyJobsService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工岗位信息读取
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.GET)
    public Result findJobsInfo(@PathVariable(name = "id") String uid) throws Exception {
        // todo 做了修改
        UserCompanyJobs info = userCompanyJobsService.findById(uid);
        if(info == null) {
            info = new UserCompanyJobs();
            info.setUserId(uid);
            info.setCompanyId(companyId);
        }
        return new Result(ResultCode.SUCCESS,info);
    }

    /**
     * 离职表单保存
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.PUT)
    public Result saveLeave(@PathVariable(name = "id") String uid, @RequestBody EmployeeResignation resignation) throws Exception {
        resignation.setUserId(uid);
        resignationService.save(resignation);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 离职表单读取
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.GET)
    public Result findLeave(@PathVariable(name = "id") String uid) throws Exception {
        EmployeeResignation resignation = resignationService.findById(uid);
        if(resignation == null) {
            resignation = new EmployeeResignation();
            resignation.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,resignation);
    }

    /**
     * 导入员工
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Result importDatas(@RequestParam(name = "file") MultipartFile attachment) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单保存
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.PUT)
    public Result saveTransferPosition(@PathVariable(name = "id") String uid, @RequestBody EmployeeTransferPosition transferPosition) throws Exception {
        transferPosition.setUserId(uid);
        transferPositionService.save(transferPosition);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单读取
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.GET)
    public Result findTransferPosition(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs jobsInfo = userCompanyJobsService.findById(uid);
        if(jobsInfo == null) {
            jobsInfo = new UserCompanyJobs();
            jobsInfo.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,jobsInfo);
    }

    /**
     * 转正表单保存
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.PUT)
    public Result savePositive(@PathVariable(name = "id") String uid, @RequestBody EmployeePositive positive) throws Exception {
        positiveService.save(positive);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 转正表单读取
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.GET)
    public Result findPositive(@PathVariable(name = "id") String uid) throws Exception {
        EmployeePositive positive = positiveService.findById(uid);
        if(positive == null) {
            positive = new EmployeePositive();
            positive.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,positive);
    }

    /**
     * 历史归档详情列表
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.GET)
    public Result archives(@PathVariable(name = "month") String month, @RequestParam(name = "type") Integer type) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 归档更新
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.PUT)
    public Result saveArchives(@PathVariable(name = "month") String month) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 历史归档列表
     */
    @RequestMapping(value = "/archives", method = RequestMethod.GET)
    public Result findArchives(@RequestParam(name = "pagesize") Integer pagesize, @RequestParam(name = "page") Integer page, @RequestParam(name = "year") String year) throws Exception {
        Map map = new HashMap();
        map.put("year",year);
        map.put("companyId",companyId);
        Page<EmployeeArchive> searchPage = archiveService.findSearch(map, page, pagesize);
        PageResult<EmployeeArchive> pr = new PageResult(searchPage.getTotalElements(),searchPage.getContent());
        return new Result(ResultCode.SUCCESS,pr);
    }

    /**
     * 导出excel
     * @param month
     * @throws Exception
     */
    @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable(name = "month") String month) throws Exception {
        //1.构造数据
        List<EmployeeReportResult> list =
                userCompanyPersonalService.findByReport(companyId, month + "%");
        //2.创建工作簿
       // XSSFWorkbook workbook = new XSSFWorkbook();
        // 处理百万数据 对象的替换
        SXSSFWorkbook workbook = new SXSSFWorkbook();// 预值
        //3.构造sheet
        String[] titles = {"编号", "姓名", "手机", "最高学历", "国家地区", "护照号", "籍贯",
                "生日", "属相", "入职时间", "离职类型", "离职原因", "离职时间"};
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        AtomicInteger headersAi = new AtomicInteger();
        for (String title : titles) {
            Cell cell = row.createCell(headersAi.getAndIncrement());
            cell.setCellValue(title);
        }
        AtomicInteger datasAi = new AtomicInteger(1);
        Cell cell = null;
        for (EmployeeReportResult report : list) {
            Row dataRow = sheet.createRow(datasAi.getAndIncrement());
            //编号
            cell = dataRow.createCell(0);
            cell.setCellValue(report.getUserId());
            //姓名
            cell = dataRow.createCell(1);
            cell.setCellValue(report.getUsername());
            //手机
            cell = dataRow.createCell(2);
            cell.setCellValue(report.getMobile());
            //最高学历
            cell = dataRow.createCell(3);
            cell.setCellValue(report.getTheHighestDegreeOfEducation());
            //国家地区
            cell = dataRow.createCell(4);
            cell.setCellValue(report.getNationalArea());
            //护照号
            cell = dataRow.createCell(5);
            cell.setCellValue(report.getPassportNo());
            //籍贯
            cell = dataRow.createCell(6);
            cell.setCellValue(report.getNativePlace());
            //生日
            cell = dataRow.createCell(7);
            cell.setCellValue(report.getBirthday());
            //属相
            cell = dataRow.createCell(8);
            cell.setCellValue(report.getZodiac());
            //入职时间
            cell = dataRow.createCell(9);
            cell.setCellValue(report.getTimeOfEntry());
            //离职类型
            cell = dataRow.createCell(10);
            cell.setCellValue(report.getTypeOfTurnover());
            //离职原因
            cell = dataRow.createCell(11);
            cell.setCellValue(report.getReasonsForLeaving());
            //离职时间
            cell = dataRow.createCell(12);
            cell.setCellValue(report.getResignationTime());
        }
       /* String fileName = URLEncoder.encode(month + "人员信息.xlsx", "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename=" + new
                String(fileName.getBytes("ISO8859-1")));
        response.setHeader("filename", fileName);
        workbook.write(response.getOutputStream());*/
        // 完成下载
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);
        new DownloadUtils().download(os,response,month+"人事报表.xlxs");

    }

    /**
     * 模板打印
     * @param month
     * @throws Exception
     */
    @RequestMapping(value = "/exportTemplate/{month}", method = RequestMethod.GET)
    public void exportTemplate(@PathVariable(name = "month") String month) throws Exception {
        //1.构造数据
        List<EmployeeReportResult> list =
                userCompanyPersonalService.findByReport(companyId, month + "%");
        //2.创建工作簿

        // 加载模板
        Resource resource = new ClassPathResource("excel-template/hr-demo.xlsx");
        FileInputStream fis = new FileInputStream(resource.getFile());
        // 根据模板创建工作簿
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);
        Row styleRow = sheet.getRow(2);
        // 抽取公共样式
        CellStyle[] styles = new CellStyle[styleRow.getLastCellNum()];
        for(int i=0;i<styleRow.getLastCellNum();i++) {
            styles[i] = styleRow.getCell(i).getCellStyle();
        }
        // 构建单元格
        AtomicInteger datasAi = new AtomicInteger(2);
        Cell cell = null;
        // todo 吃内存
        for (EmployeeReportResult report : list) {
            Row dataRow = sheet.createRow(datasAi.getAndIncrement());
            //编号
            cell = dataRow.createCell(0);
            cell.setCellValue(report.getUserId());
            cell.setCellStyle(styles[0]);
            //姓名
            cell = dataRow.createCell(1);
            cell.setCellValue(report.getUsername());
            cell.setCellStyle(styles[1]);
            //手机
            cell = dataRow.createCell(2);
            cell.setCellValue(report.getMobile());
            cell.setCellStyle(styles[2]);
            //最高学历
            cell = dataRow.createCell(3);
            cell.setCellValue(report.getTheHighestDegreeOfEducation());
            cell.setCellStyle(styles[3]);
            //国家地区
            cell = dataRow.createCell(4);
            cell.setCellValue(report.getNationalArea());
            cell.setCellStyle(styles[4]);
            //护照号
            cell = dataRow.createCell(5);
            cell.setCellValue(report.getPassportNo());
            cell.setCellStyle(styles[5]);
            //籍贯
            cell = dataRow.createCell(6);
            cell.setCellValue(report.getNativePlace());
            cell.setCellStyle(styles[6]);
            //生日
            cell = dataRow.createCell(7);
            cell.setCellValue(report.getBirthday());
            cell.setCellStyle(styles[7]);
            //属相
            cell = dataRow.createCell(8);
            cell.setCellValue(report.getZodiac());
            cell.setCellStyle(styles[8]);
            //入职时间
            cell = dataRow.createCell(9);
            cell.setCellValue(report.getTimeOfEntry());
            cell.setCellStyle(styles[9]);
            //离职类型
            cell = dataRow.createCell(10);
            cell.setCellValue(report.getTypeOfTurnover());
            cell.setCellStyle(styles[10]);
            //离职原因
            cell = dataRow.createCell(11);
            cell.setCellValue(report.getReasonsForLeaving());
            cell.setCellStyle(styles[11]);
            //离职时间
            cell = dataRow.createCell(12);
            cell.setCellStyle(styles[12]);
            cell.setCellValue(report.getResignationTime());
         }
        // 下载
        String fileName = URLEncoder.encode(month+"人员信息.xlsx", "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename=" + new
                String(fileName.getBytes("ISO8859-1")));
        response.setHeader("filename", fileName);
        wb.write(response.getOutputStream());
    }

    /*
    *  工具类导入导出
    * */
    @RequestMapping(value = "/exportUils/{month}", method = RequestMethod.GET)
    public void exportUils(@PathVariable(name = "month") String month) throws Exception {
        //1.构造数据
        List<EmployeeReportResult> list =
                userCompanyPersonalService.findByReport(companyId, month + "%");
        Resource resource = new ClassPathResource("excel-template/hr-demo.xlsx");
        FileInputStream fis = new FileInputStream(resource.getFile());
        // 根据模板创建工作簿
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        // 导出数据
        new ExcelExportUtil(EmployeeReportResult.class, 2, 2).export(response, fis, list, "人事报表.xlsx");
        // 导入数据
       // List<User> list2 = new ExcelImportUtil(User.class).readExcel(is, 1, 2);

    }
}
