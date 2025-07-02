import {HtmlNode, HtmlNodeModel, RectNode, RectNodeModel} from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";
import {hideText} from "@/components/design/mimic/js/mimic.js";
import {createApp, h} from "vue";
import baseNode from "@/components/design/mimic/vue/baseNode.vue";

class BetweenModel extends HtmlNodeModel {

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

class BetweenView extends HtmlNode {

  constructor(props) {
    super(props)
    this.isMounted = false
    this.r = h(baseNode, {
      text: props.model.inputData,
      onBtnClick: (i) => {
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
  type: "between",
  model: BetweenModel,
  view: BetweenView,
};
