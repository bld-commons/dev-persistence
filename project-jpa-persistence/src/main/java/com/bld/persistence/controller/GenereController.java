package com.bld.persistence.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bld.commons.controller.SearchController;
import com.bld.persistence.core.domain.Genere;
import com.bld.persistence.parameter.GenereParameter;
import com.bld.persistence.response.GenereModel;

@RestController
@RequestMapping("/genere")
public class GenereController extends SearchController<Genere, Long, GenereModel, GenereParameter>{

}
