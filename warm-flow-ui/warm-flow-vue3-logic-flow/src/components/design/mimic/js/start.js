import {HtmlNode, HtmlNodeModel} from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";
import baseNode from '../vue/baseNode.vue'
import { createApp, h } from 'vue';
import {hideText} from "@/components/design/mimic/js/mimic.js";

class StartModel extends HtmlNodeModel {

  setAttributes() {
    this.width = 220;
    this.height = 80;
    this.radius = 20;
    debugger
    this.inputData = this.text.value

  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }

  getData () {
    debugger
    const data = super.getData()
    data.text.value = this.inputData
    return data
  }

  getTextStyle() {
    return hideText(super.getTextStyle());
  }

}

class StartView extends HtmlNode {

  constructor(props) {
    super(props)
    this.isMounted = false
    this.r = h(baseNode, {
      text: props.model.inputData,
      onBtnClick: (i) => {
        debugger
        props.model.text.value = i
      }
    })
    this.app = createApp({
      render: () => this.r
    })
  }
  setHtml(rootEl) {
    if (!this.isMounted) {
      this.isMounted = true
      const node = document.createElement('div')
      rootEl.appendChild(node)
      this.app.mount(node)
    } else {
      this.r.component.props.properties = this.props.model.getProperties()
    }
  }
}

export default {
  type: "start",
  model: StartModel,
  view: StartView,
};

