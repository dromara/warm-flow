package com.ruoyi.flow.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.core.service.FormService;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 表单Controller
 *
 * @author liangli
 * @date 2024-09-05
 */
@Validated
@RestController
@RequestMapping("/flow/form")
public class FormController extends BaseController {

    @Resource
    private FormService formService;

    /**
     * 表单列表
     *
     * @param form 携带的查询参数
     * @return {@link TableDataInfo}
     * @author liangli
     * @date 2024/9/5 15:07
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:list')")
    @GetMapping(value = "/list")
    public TableDataInfo list(FlowForm form) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Page<Form> page = Page.pageOf(pageDomain.getPageNum(), pageDomain.getPageSize());
        page = formService.orderByCreateTime().desc().page(form, page);

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getList());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 已发布表单列表
     *
     * @param form 携带的查询参数
     * @return {@link TableDataInfo}
     * @author vanlin
     * @date 2024/11/11 15:07
     **/
    @GetMapping(value = "/publishedList")
    public TableDataInfo publishedList(FlowForm form) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Page<Form> page = Page.pageOf(pageDomain.getPageNum(), pageDomain.getPageSize());
        form.setIsPublish(1); //已发布
        page = formService.orderByCreateTime().desc().page(form, page);

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getList());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 查询表单详情
     *
     * @param id 表单ID
     * @return {@link R< Form>}
     * @author liangli
     * @date 2024/9/5 15:41
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:query')")
    @GetMapping(value = "/{id}")
    public R<Form> getForm(@PathVariable("id") Long id) {
        return R.ok(formService.getById(id));
    }

    /**
     * 提交表单
     *
     * @param form 表单ID对应的表单内容
     * @return {@link R<Boolean>}
     * @author liangli
     * @date 2024/9/5 15:07
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:add')")
    @Log(title = "提交表单", businessType = BusinessType.INSERT)
    @PostMapping("/saveFormContent")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> saveFormContent(@RequestBody FlowForm form) {
        return R.ok(formService.updateById(FlowEngine.newForm().setFormContent(form.getFormContent()).setId(form.getId())));
    }

    /**
     * 提交表单
     *
     * @param form 携带的查询参数
     * @return {@link R<Boolean>}
     * @author liangli
     * @date 2024/9/5 15:07
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:add')")
    @Log(title = "提交表单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> add(@RequestBody FlowForm form) {
        return R.ok(formService.save(form));
    }

    /**
     * 发布表单
     *
     * @param id 表单ID
     * @return {@link R<Boolean>}
     * @author liangli
     * @date 2024/9/5 15:26
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:publish')")
    @Log(title = "发布表单", businessType = BusinessType.INSERT)
    @GetMapping("/publish/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> publish(@PathVariable("id") Long id) {
        return R.ok(formService.publish(id));
    }

    /**
     * 取消表单
     *
     * @param id 表单ID
     * @return {@link R<Boolean>}
     * @author liangli
     * @date 2024/9/5 15:26
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:unPublish')")
    @Log(title = "取消发布表单", businessType = BusinessType.INSERT)
    @GetMapping("/unPublish/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> unPublish(@PathVariable("id") Long id) {
        return R.ok(formService.unPublish(id));
    }

    /**
     * 修改表单
     *
     * @param form 表单
     * @return {@link R<Boolean>}
     * @author liangli
     * @date 2024/9/5 15:26
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:edit')")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> edit(@RequestBody FlowForm form) {
        return R.ok(formService.updateById(form));
    }

    /**
     * 删除表单
     *
     * @param ids 表单ID
     * @return {@link R<Boolean>}
     * @author liangli
     * @date 2024/9/5 16:19
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:remove')")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> remove(@PathVariable List<Long> ids) {
        return R.ok(formService.removeByIds(ids));
    }

    /**
     * 复制表单
     *
     * @param id 表单ID
     * @return {@link R<Boolean>}
     * @author liangli
     * @date 2024/9/5 16:19
     **/
    @PreAuthorize("@ss.hasPermi('flow:form:add')")
    @Log(title = "流程定义", businessType = BusinessType.INSERT)
    @PostMapping("/copyForm/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> copyForm(@PathVariable("id") Long id) {
        return R.ok(formService.copyForm(id));
    }
}
