package com.zhonghui_manage.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhonghui_common.dto.BackLoginDto;
import com.zhonghui_common.dto.BackMenuDto;
import com.zhonghui_common.dto.JsonResult;
import com.zhonghui_manage.utils.CurrentUtil;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/home")
@Api(value="主页相关的接口",tags={"主页相关的接口"})
public class HomeController {

	/**
	  * 设置旧系统菜单选中
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/setSelectMenuForBack")
	public JsonResult<String> setSelectMenuForBack(HttpServletRequest request, String parentId, String parentName,
			String menuId, String menuName, String path,String project_name) throws Exception {

		BackLoginDto currentUser = CurrentUtil.getCurrentUser();

		BackMenuDto menu2 = new BackMenuDto();
		menu2.setIndex(Integer.valueOf(parentId));
		menu2.setLabel(parentName);
		menu2.setLevel(2);
		currentUser.setSelectParent(menu2);

		BackMenuDto menu3 = new BackMenuDto();
		menu3.setIndex(Integer.valueOf(menuId));
		menu3.setLabel(menuName);
		menu3.setLevel(3);
		menu3.setParentIndex(Integer.valueOf(parentId));
		menu3.setLink(path);
		menu3.setProject_name(project_name);
		currentUser.setSelectChild(menu3);

		request.getSession().setAttribute(BackLoginDto.SESSION_INFO_KEY, currentUser);
		return JsonResult.initSuccessResult("");
	}


}