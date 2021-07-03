package com.ctrip.apollo.portal.controller;

import com.ctrip.apollo.Apollo;
import com.ctrip.apollo.portal.PortalSettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/envs")
public class EnvController {

  @Autowired
  private PortalSettings portalSettings;

  @RequestMapping("")
  public List<Apollo.Env> envs(){
    return portalSettings.getEnvs();
  }

}
