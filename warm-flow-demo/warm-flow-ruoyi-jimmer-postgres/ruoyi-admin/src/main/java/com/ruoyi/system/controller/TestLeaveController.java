package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.TestLeave;
import com.ruoyi.system.service.ITestLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * OA 请假申请Controller
 *
 * @author ruoyi
 * @date 2024-03-07
 */
@RestController
@RequestMapping("/system/leave")
public class TestLeaveController extends BaseController
{
    @Autowired
    private ITestLeaveService testLeaveService;

    /**
     * 查询OA 请假申请列表
     */
    @PreAuthorize("@ss.hasPermi('system:leave:list')")
    @GetMapping("/list")
    public TableDataInfo list(TestLeave testLeave)
    {
        startPage();
        List<TestLeave> list = testLeaveService.selectTestLeaveList(testLeave);
        return getDataTable(list);
    }

    /**
     * 导出OA 请假申请列表
     */
    @PreAuthorize("@ss.hasPermi('system:leave:export')")
    @Log(title = "OA 请假申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TestLeave testLeave)
    {
        List<TestLeave> list = testLeaveService.selectTestLeaveList(testLeave);
        ExcelUtil<TestLeave> util = new ExcelUtil<TestLeave>(TestLeave.class);
        util.exportExcel(response, list, "OA 请假申请数据");
    }

    /**
     * 获取OA 请假申请详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(testLeaveService.selectTestLeaveById(id));
    }

    /**
     * 新增OA 请假申请
     */
    @PreAuthorize("@ss.hasPermi('system:leave:add')")
    @Log(title = "OA 请假申请", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody TestLeave testLeave, String flowStatus)
    {
        return toAjax(testLeaveService.insertTestLeave(testLeave, flowStatus));
    }

    /**
     * 修改OA 请假申请
     */
    @PreAuthorize("@ss.hasPermi('system:leave:edit')")
    @Log(title = "OA 请假申请", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult edit(@RequestBody TestLeave testLeave)
    {
        return toAjax(testLeaveService.updateTestLeave(testLeave));
    }

    /**
     * 删除OA 请假申请
     */
    @PreAuthorize("@ss.hasPermi('system:leave:remove')")
    @Log(title = "OA 请假申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(testLeaveService.deleteTestLeaveByIds(ids));
    }

    /**
     * 暂存审批
     */
    @PreAuthorize("@ss.hasPermi('system:leave:submit')")
    @Log(title = "OA 请假申请", businessType = BusinessType.OTHER)
    @GetMapping(value = "/pending")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult pending(String id, String flowStatus) {
        return toAjax(testLeaveService.pending(id, flowStatus));
    }

    /**
     * 提交审批
     */
    @PreAuthorize("@ss.hasPermi('system:leave:submit')")
    @Log(title = "OA 请假申请", businessType = BusinessType.OTHER)
    @GetMapping(value = "/submit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(String id, String flowStatus) {
        return toAjax(testLeaveService.submit(id, flowStatus));
    }

    /**
     * 办理
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/handle")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult handle(@RequestBody TestLeave testLeave, Long taskId, String skipType, String message
            , String nodeCode, String flowStatus) {
        return toAjax(testLeaveService.handle(testLeave, taskId, skipType, message, nodeCode, flowStatus));
    }

    /**
     * 驳回上一个任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/rejectLast")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult rejectLast(@RequestBody TestLeave testLeave, Long taskId, String message
            , String flowStatus) {
        return toAjax(testLeaveService.rejectLast(testLeave, taskId, message, flowStatus));
    }

    /**
     * 拿回到最近办理的任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/taskBack")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult taskBack(@RequestBody TestLeave testLeave, Long taskId, String message
            , String flowStatus) {
        return toAjax(testLeaveService.taskBack(testLeave, taskId, message, flowStatus));
    }

    /**
     * 撤销流程
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @GetMapping("/revoke/{id}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult revoke(@PathVariable String id) {
        return toAjax(testLeaveService.revoke(id));
    }

    /**
     * 拿回到最近办理的任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @GetMapping("/taskBackByInsId/{id}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult taskBackByInsId(@PathVariable String id) {
        return toAjax(testLeaveService.taskBackByInsId(id));
    }

    /**
     * 终止流程，提前结束
     *
     * @param testLeave
     * @return
     */
    @PostMapping("/termination")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult termination(@RequestBody TestLeave testLeave) {
        return toAjax(testLeaveService.termination(testLeave));
    }

}
