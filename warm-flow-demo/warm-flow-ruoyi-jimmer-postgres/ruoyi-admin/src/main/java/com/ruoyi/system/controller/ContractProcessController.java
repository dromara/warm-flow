package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.ContractProcess;
import com.ruoyi.system.service.IContractProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 合同流程Controller
 *
 * @author ruoyi
 * @date 2025-04-30
 */
@RestController
@RequestMapping("/system/process")
public class ContractProcessController extends BaseController
{
    @Autowired
    private IContractProcessService contractProcessService;

    /**
     * 查询合同流程列表
     */
    @PreAuthorize("@ss.hasPermi('system:process:list')")
    @GetMapping("/list")
    public TableDataInfo list(ContractProcess contractProcess)
    {
        startPage();
        List<ContractProcess> list = contractProcessService.selectContractProcessList(contractProcess);
        return getDataTable(list);
    }

    /**
     * 导出合同流程列表
     */
    @PreAuthorize("@ss.hasPermi('system:process:export')")
    @Log(title = "合同流程", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ContractProcess contractProcess)
    {
        List<ContractProcess> list = contractProcessService.selectContractProcessList(contractProcess);
        ExcelUtil<ContractProcess> util = new ExcelUtil<ContractProcess>(ContractProcess.class);
        util.exportExcel(response, list, "合同流程数据");
    }

    /**
     * 获取合同流程详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:process:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(contractProcessService.selectContractProcessById(id));
    }

    /**
     * 新增合同流程
     */
    @PreAuthorize("@ss.hasPermi('system:process:add')")
    @Log(title = "合同流程", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ContractProcess contractProcess, String flowStatus)
    {
        return toAjax(contractProcessService.insertContractProcess(contractProcess, flowStatus));
    }

    /**
     * 修改合同流程
     */
    @PreAuthorize("@ss.hasPermi('system:process:edit')")
    @Log(title = "合同流程", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ContractProcess contractProcess)
    {
        return toAjax(contractProcessService.updateContractProcess(contractProcess));
    }

    /**
     * 删除合同流程
     */
    @PreAuthorize("@ss.hasPermi('system:process:remove')")
    @Log(title = "合同流程", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(contractProcessService.deleteContractProcessByIds(ids));
    }

    /**
     * 办理
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/handle")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult handle(@RequestBody ContractProcess contractProcess, Long taskId, String skipType, String message
            , String nodeCode, String flowStatus) {
        return toAjax(contractProcessService.handle(contractProcess, taskId, skipType, message, nodeCode, flowStatus));
    }

    /**
     * 驳回上一个任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/rejectLast")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult rejectLast(@RequestBody ContractProcess contractProcess, Long taskId, String message
            , String flowStatus) {
        return toAjax(contractProcessService.rejectLast(contractProcess, taskId, message, flowStatus));
    }

    /**
     * 拿回到最近办理的任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @PostMapping("/taskBack")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult taskBack(@RequestBody ContractProcess contractProcess, Long taskId, String message
            , String flowStatus) {
        return toAjax(contractProcessService.taskBack(contractProcess, taskId, message, flowStatus));
    }

    /**
     * 撤销流程
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @GetMapping("/revoke/{id}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult revoke(@PathVariable String id) {
        return toAjax(contractProcessService.revoke(id));
    }

    /**
     * 拿回到最近办理的任务
     */
    @Log(title = "流程实例", businessType = BusinessType.OTHER)
    @GetMapping("/taskBackByInsId/{id}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult taskBackByInsId(@PathVariable String id) {
        return toAjax(contractProcessService.taskBackByInsId(id));
    }

    /**
     * 终止流程，提前结束
     *
     * @param contractProcess
     * @return
     */
    @PostMapping("/termination")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult termination(@RequestBody ContractProcess contractProcess) {
        return toAjax(contractProcessService.termination(contractProcess));
    }
}
