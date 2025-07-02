import {HtmlNode, HtmlNodeModel} from "@logicflow/core";
import {setCommonStyle} from "@/components/design/common/js/tool.js";
import baseNode from '../vue/baseNode.vue'
import { createApp, h } from 'vue';
import { UserFilled } from '@element-plus/icons-vue'

class StartModel extends HtmlNodeModel {

  initNodeData(data) {
    super.initNodeData(data);
    this.width = 220;
    this.height = 80;
    this.radius = 20;
    this.processName = this.text.value; // 流程名称，默认值为"发起人"
    this.text.value = ""
  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }

  getProcessName() {
    return this.processName;
  }

}

class StartView extends HtmlNode {

  constructor(props) {
    super(props)
    this.isMounted = false
    this.r = h(baseNode, {
      text: props.model.getProcessName(),
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

