package edu.fisa.lab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@GetMapping("ci")
	public String get() {
		return "step06_ci_test get 요청";
	}
	
}
