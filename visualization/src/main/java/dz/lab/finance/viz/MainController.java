package dz.lab.finance.viz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/index")
public class MainController {
	
	private static final Log log = LogFactory.getLog(MainController.class);

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView printHello() {		
		log.info("HelloController called to handle GET");
		ModelMap model = new ModelMap();
		model.addAttribute("message", "Hello Spring MVC Framework!");
		return new ModelAndView("index", model);
	}
}
