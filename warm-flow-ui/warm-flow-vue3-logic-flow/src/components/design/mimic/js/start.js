import {BaseNodeModel} from "@/components/design/mimic/js/baseNodeModel";
import {BaseNodeView} from "@/components/design/mimic/js/baseNodeView";

class StartModel extends BaseNodeModel {}

class StartView extends BaseNodeView {}

export default {
  type: "start",
  model: StartModel,
  view: StartView,
};

