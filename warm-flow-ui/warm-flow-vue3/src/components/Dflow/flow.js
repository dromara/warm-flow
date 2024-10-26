import { uuid } from '@/utils/tool';
function removeSubNode(nodeData, param){
    for(const j  in nodeData){
        const childNodes = nodeData[j]
        for(const k in childNodes){
            if(childNodes[k].nodeId == param.nodeId){
                childNodes.splice(k, 1);
                if(k== 0){
                    //删除的为子节点的第一个，表明这个分支都要删除
                    nodeData.splice(j, 1)
                }
            }
        }
    }
}

//移除节点， 同时删除子节点
export function removeNode(nodeData,param){
    for(const i in nodeData){
        if(nodeData[i].nodeId === param.nodeId){
            nodeData.splice(i,1);
            break;
            
        }else{
            let subNode =  nodeData[i].childNodes;
            if(subNode){
               
               removeSubNode(nodeData[i].childNodes, param);

                // 只有2个子节点， 设置允许删除
               if(subNode.length == 2 && subNode.nodeType == 'serial'){ 
                    subNode[1][0].enableDel = true;
               }
               
               if(subNode.length <= 1){
                    // 子节点是否只有2个(注意： 这里的数据结构为 [a,b],[c,d], 移除的子节点可能时 a或c, 但是数据长度不变)
                    nodeData.splice(i,1);
               }else{
                for(let m in subNode){
                    removeNode(subNode[m], param);
                }
               }
                
            }
            
         }
    }
    
}

// 添加节点，在 并行分支和 条件分支 也可以单独添加子节点
export function addNode(nodeData,param){
    for(const i in nodeData){
        if(nodeData[i].nodeId === param.nodeId){
            if(param.nodeType === 'serial'){
                nodeData.splice(parseInt(i)+1, 0, {
                        nodeId: uuid(),
                        nodeType: 'serial',
                        nodeName:"分支选择",
                        childNodes:[
                              [
                                {
                                  nodeId: uuid(),
                                  nodeType: 'serial-node',
                                  type: 'serial-node',
                                  nodeName:"条件",
                                  sortNum: 0,
                                  value: '',
                                  properties:{}
                                },
                              
                              ],
                              [
                                {
                                  nodeId:  uuid(),
                                  nodeType: 'serial-node',
                                  type: 'serial-node',
                                  nodeName:"条件",
                                  value: '其他条件走此流程',
                                  sortNum: 1,
                                  default: true,
                                  properties:{}
                                },
                             
                              ]
                        ]
                    
                })
            }else if(param.nodeType === 'parallel'){
                nodeData.splice(parseInt(i)+1, 0, {
                    nodeId: uuid(),
                    nodeType: 'parallel',
                    nodeName:"并行分支",
                    childNodes:[
                          [
                            {
                                nodeId: uuid(),
                                nodeName:'审批人1',
                                nodeType: 'between',
                                value: '',
                                type: 'between',
                                properties:{}
                            },
                          
                          ],
                          [
                            {
                                nodeId: uuid(),
                                nodeName:'审批人2',
                                nodeType: 'between',
                                type: 'between',
                                value: '',
                                properties:{}
                            },
                         
                          ]
                    ]
                
                })  
            }else{
                nodeData.splice(parseInt(i)+1, 0, {
                    nodeId: uuid(),
                    nodeName:'审批人',
                    nodeType:param.nodeType,
                    type: param.nodeType,
                    value: '',
                    properties:{}
                })
            }
            break;
            
        }else{
            let subNode =  nodeData[i].childNodes;
            for(let m in subNode){
                addNode(subNode[m], param);
            }

         }
    }
    
}

// 审批时，查看流程图，处理节点的状态， 暂时未用到
export function changeNodeStatus(nodeData, hisList){
    // 修改节点状态

    for(const i in nodeData){
        nodeData[i].nodeStatus = [];
        const hiss = hisList.filter(r => r.nodeCode == nodeData[i].nodeId);
        if(hiss && hiss.length > 0){
            for(const j in hiss){
                nodeData[i].nodeStatus.push(hiss[j].flowStatus);
            }
            
        }else{
            let subNode =  nodeData[i].childNodes;
            if(subNode){
               for(let m in subNode){
                   changeNodeStatus(subNode[m], hisList);
               }
            }
        }
            
    }

}


export function nodeList(nodeData, list){
    for(const i in nodeData){
       const node =  nodeData[i];
       if(node.nodeType == 'start' || node.nodeType == 'between'   // || node.nodeType == 'parallel'  || node.nodeType == 'serial' 
           || node.nodeType == 'parallel-node'){
           list.push({
               nodeId: node.nodeId,
               nodeName: node.nodeName,
               nodeType: node.nodeType,
           });
       }
       
       const childNodes = node.childNodes;
       if(childNodes != null && childNodes.length > 0){
            for(const j in childNodes){
                nodeList(childNodes[j], list);
            }    
       }
        

    }
}

export function getNode(flowData, nodeId){
    for(const i in flowData){
       const node =  flowData[i];
       if(node.nodeId == nodeId){
           return node;
       }
       const childNodes = node.childNodes;
       if(childNodes != null && childNodes.length > 0){
            for(const j in childNodes){
             return   getNode(childNodes[j], nodeId);
            }    
       }

    }
    return null;
}

// 查找节点的父元素
 function getBetweenNodes(nodeData, nodeList) {
    for(const i in nodeData){
        if(nodeData[i].nodeType === 'between' || nodeData[i].nodeType === 'start' || nodeData[i].nodeType === 'end'){
            nodeList.push(nodeData[i]);
        }
        
        let subNode =  nodeData[i].childNodes;
        if(subNode){
            for(const j in subNode){
                getBetweenNodes(subNode[j], nodeList);
            }
            
        }
    }
  }

 export function backNodeList  (flowData) {
    const nodeList = [];
    getBetweenNodes(flowData, nodeList);
    return nodeList;
  }
    