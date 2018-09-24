package com.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
//@CrossOrigin(origins="*", maxAge=3600)
public class OrderController {
	
	private static Logger logger = LoggerFactory.getLogger(OrderController.class);

}
