package agent.monitor.view;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import agent.monitor.metrics.MonitorEventStore;


@Controller
public class MonitorEventStoreViewController {

	private static final Logger log = LoggerFactory.getLogger(MonitorEventStoreViewController.class);
		
	@Autowired
	private MonitorEventStore eventStore;
		
	@GetMapping("/eventStore")
	public String getAgentNames(Model model) {
		log.debug("Creating event store view");
		EventView eventView = build(eventStore.getList());
		
		model.addAttribute("eventView", eventView);
		
		return "events";
	}

	private EventView build(ArrayList<JSONObject> list) {
		List<String> view = new ArrayList<String>();
		for (JSONObject item: list) {
			view.add(item.toString());
		}
		
		return new EventView(view);
	}

}
