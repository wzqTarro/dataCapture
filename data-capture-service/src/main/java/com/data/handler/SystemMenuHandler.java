package com.data.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.data.bean.SystemFunction;
import com.data.bean.SystemMenu;
import com.data.constant.dbSql.QueryId;
import com.data.service.impl.CommonServiceImpl;
import com.data.utils.FastJsonUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 目录handler类
 * @author Alex
 *
 */
@Component
public class SystemMenuHandler extends CommonServiceImpl {

	public Mono<ServerResponse> buildMenuList(ServerRequest request) {
		String roleId = request.pathVariable("roleId");
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		Map<String, Boolean> authMap = new HashMap<>(10);
		List<SystemFunction> functionList = queryListByObject(QueryId.QUERY_FUNCTION_BY_ROLE_ID, params);
		for(SystemFunction function : functionList) {
			authMap.put(function.getFunctionAuth(), true);
		}
		List<SystemMenu> roleMenuList = queryListByObject(QueryId.QUERY_ROLE_MENU_BY_ROLE_ID, params);
		Set<SystemMenu> parentMenuSet = new HashSet<>(10);
		for(SystemMenu menu : roleMenuList) {
			if("00".equals(menu.getIsParent())) {
				parentMenuSet.add(menu);
			}
		}
		List<SystemMenu> userMenuList = new ArrayList<>(10);
		userMenuList = buildChildMenu(userMenuList, parentMenuSet, roleMenuList);
		Map<String, Object> menuMap = new HashMap<>(4);
		menuMap.put("menuList", userMenuList);
		menuMap.put("auth", authMap);
		Flux<String> menuMessage = Flux.just(FastJsonUtil.objectToString(menuMap));
		return ServerResponse.ok().body(menuMessage, String.class);
	}
	
	private List<SystemMenu> buildChildMenu(List<SystemMenu> userMenuList, Set<SystemMenu> parentMenuSet, List<SystemMenu> roleMenuList) {
		Map<String, Object> parentMenuMap = new HashMap<>(10);
		for(SystemMenu menu : parentMenuSet) {
			parentMenuMap.put(menu.getMenuId(), menu);
		}
		
		for(int i = 0, size = roleMenuList.size(); i < size; i++) {
			SystemMenu childMenu = roleMenuList.get(i);
			if("01".equals(childMenu.getIsParent())) {
				String parentId = childMenu.getParentId();
				SystemMenu parentMenu = (SystemMenu) parentMenuMap.get(parentId);
				List<SystemMenu> childMenuList = parentMenu.getChildMenuList();
				if(childMenuList == null) {
					childMenuList = new ArrayList<>(10);
				}
				childMenuList.add(childMenu);
				parentMenu.setChildMenuList(childMenuList);
			}
		}
		
		for(Map.Entry<String, Object> entry : parentMenuMap.entrySet()) {
			userMenuList.add((SystemMenu) entry.getValue());
		}
		
		return userMenuList;
	}
}
