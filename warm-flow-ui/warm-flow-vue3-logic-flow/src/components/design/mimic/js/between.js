import {BaseNodeModel} from "@/components/design/mimic/js/baseNodeModel";
import {BaseNodeView} from "@/components/design/mimic/js/baseNodeView";

class BetweenModel extends BaseNodeModel {}

class BetweenView extends BaseNodeView {}

export default {
  type: "between",
  model: BetweenModel,
  view: BetweenView,
};
