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
    this.radius = 10;
    this.text.value = ""
  }
  getNodeStyle() {
    return setCommonStyle(super.getNodeStyle(), this.properties, "node");
  }
}

class StartView extends HtmlNode {

  constructor(props) {
    super(props)
    this.root = document.createElement('div')
    this.root.style.width = '100%';
    this.root.style.height = '100%';
    this.vueComponent = baseNode
  }
  setHtml(rootEl) {
    rootEl.appendChild(this.root)
    if (this.vm) {
      this.vm.mount(this.root)
    } else {
      this.vm = createApp({
        render: () =>
            h(this.vueComponent, {
              props: {
                model: this.props.model,
                graphModel: this.props.graphModel,
                disabled: this.props.graphModel.editConfigModel.isSilentMode,
                isSelected: this.props.model.isSelected,
                isHovered: this.props.model.isHovered,
                properties: this.props.model.getProperties()
              }
            }),
        components: {
          UserFilled // 手动注册 UserFilled 组件
        }
      })
      this.vm.mount(this.root)
    }
  }
}

export default {
  type: "start",
  model: StartModel,
  view: StartView,
};

