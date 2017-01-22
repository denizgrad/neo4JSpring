package com.metu.ceng553.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.metu.ceng553.service.AppService;

@Controller
public class AppController {
	public static AppService ap = new AppService();
   @RequestMapping(value = "/", method = RequestMethod.GET)
   public String student() {
      return "queries";
   }
   
   @RequestMapping(value = "/q1", method = RequestMethod.POST)
   public String q1(ModelMap model) {
      model.addAttribute("result", ap.sql1());
      return "result";
   }
   @RequestMapping(value = "/q2", method = RequestMethod.POST)
   public String q2(ModelMap model) {
      model.addAttribute("result", ap.sql2());
      return "result";
   }
   @RequestMapping(value = "/q3", method = RequestMethod.POST)
   public String q3(ModelMap model) {
      model.addAttribute("result", ap.sql3());
      return "result";
   }
   @RequestMapping(value = "/q4", method = RequestMethod.POST)
   public String q4(ModelMap model) {
      model.addAttribute("result", ap.sql4());
      return "result";
   }
   @RequestMapping(value = "/q5", method = RequestMethod.POST)
   public String q5(ModelMap model) {
      model.addAttribute("result", ap.sql5());
      return "result";
   }
   @RequestMapping(value = "/q6", method = RequestMethod.POST)
   public String q6(ModelMap model) {
      model.addAttribute("result", ap.sql6());
      return "result";
   }
}