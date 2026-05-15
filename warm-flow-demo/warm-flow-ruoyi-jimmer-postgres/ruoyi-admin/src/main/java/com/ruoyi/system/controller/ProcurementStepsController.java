package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.ProcurementSteps;
import com.ruoyi.system.service.IProcurementStepsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 企业采购流程表Controller
 *
 * @author ruoyi
 * @date 2025-05-08
 */
@RestController
@RequestMapping("/system/steps")
public class ProcurementStepsController extends BaseController
{
    @Autowired
    private IProcurementStepsService procurementStepsService;

    /**
     * 查询企业采购流程表列表
     */
    @PreAuthorize("@ss.hasPermi('system:steps:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProcurementSteps procurementSteps)
    {
        startPage();
        List<ProcurementSteps> list = procurementStepsService.selectProcurementStepsList(procurementSteps);
        return getDataTable(list);
    }

    /**
     * 导出企业采购流程表列表
     */
    @PreAuthorize("@ss.hasPermi('system:steps:export')")
    @Log(title = "企业采购流程表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProcurementSteps procurementSteps)
    {
        List<ProcurementSteps> list = procurementStepsService.selectProcurementStepsList(procurementSteps);
        ExcelUtil<ProcurementSteps> util = new ExcelUtil<ProcurementSteps>(ProcurementSteps.class);
        util.exportExcel(response, list, "企业采购流程表数据");
    }

    /**
     * 获取企业采购流程表详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:steps:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(procurementStepsService.selectProcurementStepsById(id));
    }

    /**
     * 新增企业采购流程表
     */
    @PreAuthorize("@ss.hasPermi('system:steps:add')")
    @Log(title = "企业采购流程表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProcurementSteps procurementSteps)
    {
        return toAjax(procurementStepsService.insertProcurementSteps(procurementSteps));
    }

    /**
     * 修改企业采购流程表
     */
    @PreAuthorize("@ss.hasPermi('system:steps:edit')")
    @Log(title = "企业采购流程表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProcurementSteps procurementSteps)
    {
        return toAjax(procurementStepsService.updateProcurementSteps(procurementSteps));
    }

    /**
     * 删除企业采购流程表
     */
    @PreAuthorize("@ss.hasPermi('system:steps:remove')")
    @Log(title = "企业采购流程表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(procurementStepsService.deleteProcurementStepsByIds(ids));
    }

    /**
     * 办理
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/handle")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult handle(@RequestBody ProcurementSteps procurementSteps, Long taskId, String skipType, String message
            , String nodeCode, String flowStatus) {
        return toAjax(procurementStepsService.handle(procurementSteps, taskId, skipType, message, nodeCode, flowStatus));
    }

    /**
     * 驳回上一个任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/rejectLast")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult rejectLast(@RequestBody ProcurementSteps procurementSteps, Long taskId, String message
            , String flowStatus) {
        return toAjax(procurementStepsService.rejectLast(procurementSteps, taskId, message, flowStatus));
    }

    /**
     * 拿回到最近办理的任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/taskBack")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult taskBack(@RequestBody ProcurementSteps procurementSteps, Long taskId, String message
            , String flowStatus) {
        return toAjax(procurementStepsService.taskBack(procurementSteps, taskId, message, flowStatus));
    }

    /**
     * 撤销流程
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @GetMapping("/revoke/{id}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult revoke(@PathVariable String id) {
        return toAjax(procurementStepsService.revoke(id));
    }

    /**
     * 拿回到最近办理的任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @GetMapping("/taskBackByInsId/{id}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult taskBackByInsId(@PathVariable String id) {
        return toAjax(procurementStepsService.taskBackByInsId(id));
    }

    /**
     * 终止流程，提前结束
     *
     * @param procurementSteps
     * @return
     */
    @PostMapping("/termination")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult termination(@RequestBody ProcurementSteps procurementSteps) {
        return toAjax(procurementStepsService.termination(procurementSteps));
    }
}
