package com.pmo.dashboard.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pmo.dashboard.entity.CandidateInterview;
import com.pmo.dashboard.entity.CandidatePush;
import com.pmo.dashboard.entity.Demand;
import com.pmo.dashboard.entity.Employee;
import com.pmo.dashboard.entity.PageCondition;
import com.pmo.dashboard.entity.User;
import com.pmo.dashboard.util.Utils;
import com.pom.dashboard.service.DemandService;
import com.pom.dashboard.service.EmployeeService;
import com.pom.dashboard.service.RmCandidateService;

/**
 * RM的我的候选人controller
 * @author tianzhao
 *
 */
@Controller
@RequestMapping(value = "/rmCandidate")
public class RmCandidateController {
	
	@Resource
	RmCandidateService rmCandidateService;
	
	@Resource
	EmployeeService employeeService;
	
	@Resource
	DemandService demandService;

	@RequestMapping("/myCandidate")
	public String myCandidate(){
		return "/candidate/rmCandidate";
	}
	
	/**
	 * 获取Rm的部门下的已推送的候选人列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/getMyCandidate")
	@ResponseBody
	public Object getMyCandidate(PageCondition pageCondition,HttpServletRequest request){
		if("".equals(pageCondition.getCurrPage()) || pageCondition.getCurrPage() == null){
			pageCondition.setCurrPage(1);
		}
		User user = (User)request.getSession().getAttribute("loginUser");
		//表示已推送的候选人状态
		String status = "1";
		List<CandidatePush> candidatelist = new ArrayList<CandidatePush>();
		if(user != null){
			candidatelist = rmCandidateService.queryPushedCandidate(user.getCsDeptId(),status,pageCondition);
		}
		request.getSession().setAttribute("candidatelist", candidatelist);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("candidatelist", candidatelist);
		map.put("pageCondition", pageCondition);
		return map;
	}
	
	@RequestMapping("/loadInterviewer")
	@ResponseBody
	public Object loadInterviewer(HttpServletRequest request){
		User user = (User)request.getSession().getAttribute("loginUser");
		List<Employee> list = employeeService.queryEmployeeByCsSubDeptId(user.getCsDeptId());
		return list;
	}
	
	/**
	 * 新一轮面试
	 * @param pushId
	 * @param interviewDate
	 * @param interviewerId
	 * @param request
	 * @return
	 */
	@RequestMapping("/addInterview")
	@ResponseBody
	public Object addInterview(String pushId,String interviewDate,String interviewerId,HttpServletRequest request){
		List<CandidatePush> candidatelist = (List<CandidatePush>)request.getSession().getAttribute("candidatelist");
		CandidateInterview candidateInterview = new CandidateInterview();
		for (CandidatePush candidatePush : candidatelist) {
			if(candidatePush.getPushId().equals(pushId)){
				candidateInterview.setInterviewId(Utils.getUUID());
				candidateInterview.setCandidateId(candidatePush.getCandidateId());
				//面试官id
				candidateInterview.setInterviewerId(interviewerId);
				//面试日期
				candidateInterview.setInterviewDate(interviewDate);
				candidateInterview.setCssubDept(candidatePush.getCsSubDeptId());
				candidateInterview.setFatherInterviewId(candidateInterview.getInterviewId());
				candidateInterview.setInterviewSerial("1");
			}
		}
		try {
			rmCandidateService.addInterview(candidateInterview,pushId);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * 下一次面试
	 * @param pushId
	 * @param interviewDate
	 * @param interviewerId
	 * @param request
	 * @return
	 */
	@RequestMapping("/addNextInterview")
	@ResponseBody
	public Object addNextInterview(String pushId,String interviewDate,String interviewerId,HttpServletRequest request){
		List<CandidatePush> candidatelist = (List<CandidatePush>)request.getSession().getAttribute("candidatelist");
		CandidateInterview candidateInterview = new CandidateInterview();
		for (CandidatePush candidatePush : candidatelist) {
			if(candidatePush.getPushId().equals(pushId)){
				candidateInterview.setInterviewId(Utils.getUUID());
				candidateInterview.setCandidateId(candidatePush.getCandidateId());
				//面试官id
				candidateInterview.setInterviewerId(interviewerId);
				//面试日期
				candidateInterview.setInterviewDate(interviewDate);
				candidateInterview.setCssubDept(candidatePush.getCsSubDeptId());
				//candidateInterview.setFatherInterviewId(candidateInterview.getInterviewId());
				//candidateInterview.setInterviewSerial("1");
			}
		}
		try {
			rmCandidateService.addNextInterview(candidateInterview,pushId);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * 面试退回
	 * @param pushId
	 * @return
	 */
	@RequestMapping("/interviewBack")
	@ResponseBody
	public Object interviewBack(String pushId){
		try {
			rmCandidateService.interviewBack(pushId);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * 加载rm部门需求表
	 * @param pushId
	 * @param request
	 * @return
	 */
	@RequestMapping("/offerDemandList")
	@ResponseBody
	public Object offerDemandList(String pushId,HttpServletRequest request){
		List<CandidatePush> candidatelist = (List<CandidatePush>)request.getSession().getAttribute("candidatelist");
		Map<String, Object> map = new HashMap<String, Object>();
		for (CandidatePush candidatePush : candidatelist) {
			if(candidatePush.getPushId().equals(pushId)){
				List<Demand> list = demandService.queryOfferDemandList(candidatePush);
				map.put("list", list);
				return map;
			}
		}
		return map;
	}
	
	/**
	 * 候选人id与需求表绑定（往需求表里面更新候选人id）
	 * @param pushId
	 * @param demandId
	 * @param request
	 * @return
	 */
	@RequestMapping("/offerInterview")
	@ResponseBody
	public Object offerInterview(String pushId,String demandId,HttpServletRequest request){
		List<CandidatePush> candidatelist = (List<CandidatePush>)request.getSession().getAttribute("candidatelist");
		for (CandidatePush candidatePush : candidatelist) {
			if(candidatePush.getPushId().equals(pushId)){
				try {
					demandService.updateCandidateIdById(candidatePush.getCandidateId(),demandId,candidatePush.getPushId());
					return "1";
				} catch (Exception e) {
					return "0";
				}
			}
		}
		return "0";
	}
}
