import { RectNodeModel } from '@logicflow/core'
import {setCommonStyle} from "@/components/design/common/js/tool.js";
export class GatewayModel extends RectNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.width = 70;
    this.height = 30;
    this.radius = 5;
  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node", "mimic");
  }

  getTextStyle() {
    const style = super.getTextStyle();
    style.display = 'none';
    return style;
  }
}

