import {HtmlNodeModel} from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";
import {hideText} from "@/components/design/mimic/js/mimic.js";

export class BaseNodeModel extends HtmlNodeModel {

  setAttributes() {
    this.width = 220;
    this.height = 80;
    this.radius = 20;
    this.inputData = this.text.value

  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }

  getData () {
    const data = super.getData()
    data.text.value = this.inputData
    return data
  }

  getTextStyle() {
    return hideText(super.getTextStyle());
  }

}
