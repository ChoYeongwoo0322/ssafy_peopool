package com.ssafy.peopool.controller;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.peopool.model.Follow;
import com.ssafy.peopool.model.Individual;
import com.ssafy.peopool.model.ProfileOfEnterprise;
import com.ssafy.peopool.model.service.FollowService;
import com.ssafy.peopool.model.service.HashtagService;
//import com.ssafy.api.request.UserLoginPostReq;
//import com.ssafy.api.request.UserRegiserIdGetReq;
//import com.ssafy.api.request.UserRegisterPostReq;
//import com.ssafy.api.response.UserLoginPostRes;
//import com.ssafy.api.response.UserRes;
//import com.ssafy.api.service.UserService;
//import com.ssafy.common.auth.SsafyUserDetails;
//import com.ssafy.common.model.response.BaseResponseBody;
//import com.ssafy.common.util.JwtTokenUtil;
//import com.ssafy.db.entity.User;
//import com.ssafy.db.repository.UserRepositorySupport;
import com.ssafy.peopool.model.service.IndividualService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * ?????? ?????? API ?????? ????????? ?????? ???????????? ??????.
 */
@RestController
@RequestMapping("/ind")
@CrossOrigin(origins="*",allowedHeaders = "*")
public class IndividualController {
	
	private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	@Autowired
	IndividualService individualService;
	
	@Autowired
	FollowService followService;
	
	@Autowired
	HashtagService hashtagService;
	
	@ApiOperation(value = "?????? ?????? ?????? ??????", response = String.class)
	@GetMapping()
	public ResponseEntity<List<Individual>> getAllUser() throws SQLException{
		return new ResponseEntity<>(individualService.getAllUser(), HttpStatus.OK);
	}
	
	@ApiOperation(value = "?????? ?????? ?????? ??????", response = String.class)
	@GetMapping("{index}")
	public ResponseEntity<Individual> getIndividual(@PathVariable("index")int index) throws SQLException{
		return new ResponseEntity<>(individualService.getIndividual(index), HttpStatus.OK);
	}
	
	@ApiOperation(value = "?????? ?????? ??????", response = String.class)
	@PostMapping()
	public ResponseEntity<String> registerIndividual(@RequestBody Individual individual) throws SQLException{
		if(individualService.registerIndividual(individual)) {
            return new ResponseEntity<String>(SUCCESS, HttpStatus.OK);
        }
        return new ResponseEntity<String>(FAIL, HttpStatus.NO_CONTENT);
	}
	
	@ApiOperation(value = "?????? ?????? ?????? ??????", response = String.class)
	@PutMapping()
	public ResponseEntity<String> modifyIndividual(@RequestBody Individual individual) throws SQLException{
		if(individualService.modifyIndividual(individual)) {
            return new ResponseEntity<String>(SUCCESS, HttpStatus.OK);
        }
        return new ResponseEntity<String>(FAIL, HttpStatus.NO_CONTENT);
	}
	
	
	@ApiOperation(value = "?????? ?????? ??????", response = String.class)
	@DeleteMapping("{index}")
	public ResponseEntity<String> deleteIndividual(@PathVariable("index")int index) throws SQLException{
		
		Follow follow = new Follow();
		follow.setFol_type(0);
		follow.setFollowing(index);
		followService.deleteFollowing(follow);
		
		follow.setFol_type(1);
		follow.setFollower(index);
		followService.deleteFollower(follow);
		
		hashtagService.deleteWithdraw(index);
		
		if(individualService.deleteIndividual(index)) {
            return new ResponseEntity<String>(SUCCESS, HttpStatus.OK);
        }
        return new ResponseEntity<String>(FAIL, HttpStatus.NO_CONTENT);
	}
	
	@ApiOperation(value = "????????? ??????", response = String.class)
	@GetMapping("/findid")
	public ResponseEntity<Individual> findIndividualID(@RequestParam("name")String name, @RequestParam("phone")String phone) throws SQLException{
		return new ResponseEntity<>(individualService.findIndividualID(name, phone), HttpStatus.OK);
	}
	
	@ApiOperation(value = "???????????? ??????", response = String.class)
	@GetMapping("/findpw")
	public ResponseEntity<Individual> findIndividualPW(@RequestParam("id")String id, @RequestParam("phone")String phone) throws SQLException{
		return new ResponseEntity<>(individualService.findIndividualPW(id, phone), HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "????????? ?????? ??????", response = String.class)
	@PostMapping("/checkid")
	public ResponseEntity<String> getUserId(@RequestBody Individual individual) throws SQLException{
		Individual ind = individualService.getUserId(individual.getInd_id());
		return ResponseEntity.status(200).body(ind.getInd_id());
		
//		if(individualService.getUserId(id) != null) {
//            return new ResponseEntity<String>(SUCCESS, HttpStatus.OK);
//        }
//        return new ResponseEntity<String>(FAIL, HttpStatus.NO_CONTENT);
	}
}
