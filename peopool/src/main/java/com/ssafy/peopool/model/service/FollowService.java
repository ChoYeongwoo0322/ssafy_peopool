package com.ssafy.peopool.model.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ssafy.peopool.model.FolCard;
import com.ssafy.peopool.model.Follow;

public interface FollowService {
	

	// 팔로잉 추가
	boolean registerFollow(Follow follow) throws SQLException;

	// 팔로우 삭제
	boolean deleteFollow(Follow follow) throws SQLException;
	
	// 회원탈퇴시 팔로우 기록 삭제
	boolean deleteFollower(Follow follow) throws SQLException;
	
	// 회원탈퇴시 팔로우 기록 삭제
	boolean deleteFollowing(Follow follow) throws SQLException;

	// 팔로워 숫자 조회
	int getFollowerCount(String index, String type) throws SQLException;
	
	int getFollowingCount(String index, String type) throws SQLException;

	// 팔로워 조회
	List<FolCard> getFollowing(String index, String type) throws SQLException;

	// 팔로잉 조회
	List<FolCard> getFollower(String index, String type) throws SQLException;
	
	// 팔로우 체크
	Follow getCheck(Follow follow) throws SQLException;

}
