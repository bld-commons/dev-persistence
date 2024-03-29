package com.bld.persistence.controller;

import java.util.Date;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bld.commons.utils.data.CollectionResponse;
import com.bld.commons.utils.data.ObjectResponse;
import com.bld.commons.utils.json.annotations.DateTimeZone;
import com.bld.commons.utils.types.UpperLowerType;
import com.bld.persistence.common.Genere2Mapper;
import com.bld.persistence.common.GenereMapper;
import com.bld.persistence.core.domain.Genere;
import com.bld.persistence.parameter.GenereParameter;
import com.bld.persistence.response.GenereModel;
import com.bld.proxy.api.find.annotations.ApiFind;
import com.bld.proxy.api.find.annotations.ApiFindController;
import com.bld.proxy.api.find.annotations.ApiMapper;

import bld.commons.reflection.annotations.DateFilter;
import bld.commons.reflection.annotations.LikeString;

@ApiFindController
@RequestMapping("/genere/find")
@ApiFind(entity=Genere.class,id=Long.class)
@ApiMapper(GenereMapper.class)
public interface GenereFindController {

	
	@PostMapping(path = "/filter",consumes = "application/json",produces = "application/json")
	@ResponseBody
	@ApiMapper(value = Genere2Mapper.class,method = "convertToGenereModel")
	public List<GenereModel> findByFilter(@RequestBody GenereParameter parameter);
	
	
	@PostMapping(path = "/collection-response/filter",consumes = "application/json",produces = "application/json")
	@ResponseBody
	public CollectionResponse<GenereModel> collectionResponseByFilter(@RequestBody GenereParameter parameter);
	
	
	@PostMapping(path = "/single-result",consumes = "application/json",produces = "application/json")
	@ResponseBody
	public GenereModel singleResutlByFilter(@RequestBody GenereParameter parameter);
	
	
	@PostMapping(path = "/object-response/single-result",consumes = "application/json",produces = "application/json")
	@ResponseBody
	public ObjectResponse<GenereModel> objectResponseByFilter(@RequestBody GenereParameter parameter);
	
	
	@PostMapping(path = "/collection-response/filter",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public CollectionResponse<GenereModel> collectionResponseByFilter(@RequestParam("desGenere") @LikeString(upperLowerType = UpperLowerType.UPPER) String desGenere,@RequestAttribute(value="createTimestampTo",required = false)@DateTimeZone(format = "yyyy-MM-dd") @DateFilter(addDay = 1) Date createTimestampTo);
	
}
