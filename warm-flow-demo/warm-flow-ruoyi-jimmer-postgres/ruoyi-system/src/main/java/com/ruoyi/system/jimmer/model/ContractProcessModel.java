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
@Table(name = "contract_process")
public interface ContractProcessModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id();

    @Column(name = "contract_name")
    @Nullable
    String contractName();

    @Column(name = "contract_type")
    @Nullable
    String contractType();

    @Column(name = "structure_and_nature")
    @Nullable
    String structureAndNature();

    @Column(name = "proposed_conditions")
    @Nullable
    String proposedConditions();

    @Column(name = "negotiation_content")
    @Nullable
    String negotiationContent();

    @Column(name = "file_id")
    @Nullable
    String fileId();

    @Column(name = "negotiation_result")
    @Nullable
    String negotiationResult();

    @Column(name = "adjustment_scheme")
    @Nullable
    String adjustmentScheme();

    @Column(name = "sign_date")
    @Nullable
    Date signDate();

    @Column(name = "signer")
    @Nullable
    String signer();

    @Column(name = "filing_date")
    @Nullable
    Date filingDate();

    @Column(name = "filing_department")
    @Nullable
    String filingDepartment();

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
