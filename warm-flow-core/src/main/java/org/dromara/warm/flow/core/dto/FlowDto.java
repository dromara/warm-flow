package org.dromara.warm.flow.core.dto;


import lombok.Getter;
import lombok.Setter;
import org.dromara.warm.flow.core.entity.Form;

import java.io.Serializable;

/**
 * @author vanlin
 * @since 2024-9-24 11:11
 */
@Getter
@Setter
public class FlowDto implements Serializable {

    private Long id;

    private String formContent;

    private Form form;

    private Object data;

}
