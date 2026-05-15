package com.ruoyi.system.jimmer.model;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Entity
@Table(name = "procurement_steps")
public interface ProcurementStepsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id();

    @Column(name = "purchase_name")
    @Nullable
    String purchaseName();

    @Column(name = "purchase_plan")
    @Nullable
    String purchasePlan();

    @Column(name = "urgent")
    @Nullable
    String urgent();

    @Column(name = "urgent_purchase_plan")
    @Nullable
    String urgentPurchasePlan();

    @Column(name = "on_demand_procurement")
    @Nullable
    String onDemandProcurement();

    @Column(name = "provide_items")
    @Nullable
    String provideItems();

    @Column(name = "product_inspection")
    @Nullable
    String productInspection();

    @Column(name = "issue_invoice")
    @Nullable
    String issueInvoice();

    @Column(name = "record_entry")
    @Nullable
    String recordEntry();

    @Column(name = "return_items")
    @Nullable
    String returnItems();

    @Column(name = "warehousing")
    @Nullable
    String warehousing();

    @Column(name = "receive_items")
    @Nullable
    String receiveItems();

    @Column(name = "maintain_records")
    @Nullable
    String maintainRecords();

    @Column(name = "instance_id")
    @Nullable
    Long instanceId();

    @Column(name = "node_code")
    @Nullable
    String nodeCode();

    @Column(name = "node_name")
    @Nullable
    String nodeName();

    @Column(name = "node_type")
    @Nullable
    Integer nodeType();

    @Column(name = "flow_status")
    @Nullable
    String flowStatus();

    @Column(name = "del_flag")
    @Nullable
    String delFlag();

    @Column(name = "create_by")
    @Nullable
    String createBy();

    @Column(name = "create_time")
    @Nullable
    Date createTime();

    @Column(name = "update_by")
    @Nullable
    String updateBy();

    @Column(name = "update_time")
    @Nullable
    Date updateTime();

    @Column(name = "remark")
    @Nullable
    String remark();

}
