import { RectNodeModel } from '@logicflow/core'
import {setCommonStyle} from "../../common/js/tool";
export class GatewayModel extends RectNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.width = 76;
    this.height = 30;
    // 全圆角胶囊（height/2），替代原 radius=5 的方角矩形，贴近现代 chip 质感
    this.radius = 15;
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

