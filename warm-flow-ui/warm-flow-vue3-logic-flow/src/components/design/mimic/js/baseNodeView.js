import { HtmlNode } from "@logicflow/core";
import baseNode from '../vue/baseNode.vue'
import { createApp, h } from 'vue';
import {ClickOutside} from "element-plus";

export class BaseNodeView extends HtmlNode {

  constructor(props) {
    super(props)
    this.isMounted = false
    this.r = h(baseNode, {
      text: props.model.inputData,
      permissionFlag: props.model.properties.permissionFlag,
      type: props.model.type,
      onUpdateNodeName: (nodeName) => {
        props.model.text.value = nodeName
        props.graphModel.eventCenter.emit("update:nodeName", {id: props.model.id, nodeName: nodeName});
      },
      onEditNode: () => {
        props.graphModel.eventCenter.emit("edit:node", {id: props.model.id, click: true});
      },
      onDeleteNode: () => {
        props.model.graphModel.deleteNode(props.model.id); // 删除节点
      }
    })
    this.app = createApp({
      render: () => this.r
    })
    this.app.directive('click-outside', ClickOutside);
  }

  shouldUpdate() {
    const currentProperties = JSON.parse(this.currentProperties)
    if (this.preProperties) {
      const preProperties = JSON.parse(this.preProperties)
      let flag = false
      if (preProperties.permissionFlag && preProperties.permissionFlag !== currentProperties.permissionFlag ) {
        this.preProperties = this.currentProperties;
        flag = true
      }
      if (this.preText && this.preText !== this.props.model.text.value ) {
        this.preText = this.props.model.text.value
        flag = true
      }
      return flag
    }

    this.preProperties = this.currentProperties;
    this.preText = this.props.model.text.value
    return true;
  }


  setHtml(rootEl) {
    if (!this.isMounted) {
      this.isMounted = true
      const node = document.createElement('div')
      rootEl.appendChild(node)
      this.app.mount(node)
    } else {
      this.r.component.props.text = this.props.model.text.value
      this.r.component.props.permissionFlag = this.props.model.properties.permissionFlag
    }
  }
}

