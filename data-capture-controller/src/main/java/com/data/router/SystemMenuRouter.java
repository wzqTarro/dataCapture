package com.data.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.data.handler.SystemMenuHandler;

/**
 * 目录路由类
 * @author Alex
 *
 */
@Configuration
public class SystemMenuRouter {
	
	@Bean
	public RouterFunction<ServerResponse> systemMenuRouter(SystemMenuHandler menuHandler) {
		return RouterFunctions.route(RequestPredicates.POST("/menu/{roleId}")
					.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), menuHandler::buildMenuList);
	}

}
