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
      chartStatusColor: props.model.properties.chartStatusColor,
      type: props.model.type,
      fill: props.model.getNodeStyle().fill,
      stroke: props.model.getNodeStyle().stroke,
      onUpdateNodeName: (nodeName) => {
        props.model.text.value = nodeName
        props.graphModel.eventCenter.emit("update:nodeName", {id: props.model.id, nodeName: nodeName});
      },
      onEditNode: () => {
        props.graphModel.eventCenter.emit("edit:node", {id: props.model.id, click: true});
      },
      onDeleteNode: () => {
        props.graphModel.eventCenter.emit("delete:node", {id: props.model.id});
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
      if (currentProperties.permissionFlag !== preProperties.permissionFlag) {
        this.preProperties = this.currentProperties;
        flag = true
      }
      if (this.props.model.text.value!== this.preText) {
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
    }
  }
}

