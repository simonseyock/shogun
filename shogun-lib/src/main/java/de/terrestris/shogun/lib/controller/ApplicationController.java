package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.Application;
import de.terrestris.shogun.lib.service.ApplicationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
@ConditionalOnExpression("${controller.applications.enabled:true}")
public class ApplicationController extends BaseController<ApplicationService, Application> { }
