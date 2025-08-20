import {HtmlNodeModel} from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool";

export class BaseNodeModel extends HtmlNodeModel {

  setAttributes() {
    this.width = 220;
    this.height = 80;
    this.radius = 20;
    this.inputData = this.text.value

  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node", "mimic");
  }

  getData () {
    const data = super.getData()
    data.text.value = this.inputData
    return data
  }

  getTextStyle() {
    const style = super.getTextStyle();
    style.display = 'none';
    return style;
  }

}
