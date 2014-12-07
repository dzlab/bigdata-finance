package dz.lab.finance.viz;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import dz.lab.finance.model.Constants;
import dz.lab.finance.model.Rule;
import dz.lab.finance.model.Rule.Action;
import dz.lab.finance.model.Rule.Comparison;

@Controller
public class MainController {
	
	private static final Log log = LogFactory.getLog(MainController.class);
	
	@Autowired
	Producer<String, String> producer;
	
	@Autowired
	Gson gson;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView doGet(Rule rule) {		
		log.info("MainController called to handle GET: "+rule);
		ModelMap model = new ModelMap();
		model.addAttribute("currencies", new String[]{"EUR/USD", "EUR/CHF", "EUR/GBP", "EUR/JPY"});
		model.addAttribute("actions", Action.values());
		model.addAttribute("comparisons", Comparison.values());
		model.addAttribute("rule", new Rule());
		return new ModelAndView("index", model);
	}
	
	@RequestMapping(value = "/rule", method = RequestMethod.POST)
	public ModelAndView postRule(@ModelAttribute("rule") Rule rule, BindingResult bindingResult) {
		log.info("MainController called to handle rule POST: " + rule);
		if (bindingResult.hasErrors()) {
           log.info("Errors on current rule: " + bindingResult);
        }else {
        	log.info("Sending e");
        	String timestamp = String.valueOf(System.currentTimeMillis());
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(Constants.RULES_STREAM, timestamp, gson.toJson(rule));
			producer.send(data);
        }
        	
		ModelMap model = new ModelMap();
		model.addAttribute("message", "Hello Spring MVC Framework!");
		return new ModelAndView("index", model);
	}
}
