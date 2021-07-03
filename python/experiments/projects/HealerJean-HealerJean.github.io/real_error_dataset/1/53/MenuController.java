package com.hlj.proj.controller.system;

import com.hlj.proj.api.system.MenuService;
import com.hlj.proj.dto.ResponseBean;
import com.hlj.proj.dto.system.MenuDTO;
import com.hlj.proj.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName MenuController
 * @Author TD
 * @Date 2019/3/18 13:35
 * @Description 菜单管理controller
 */
@RestController
@RequestMapping("/api/menus")
@Slf4j
public class MenuController {


    @Autowired
    private MenuService menuService;

    /**
     * 新增菜单
     *
     * @param menuDTO
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseBean addMenu(@RequestBody(required = false) MenuDTO menuDTO) {
        log.info("菜单管理--------新增菜单--------菜单信息：{}", menuDTO);
        menuService.addMenu(menuDTO, UserUtils.getAuthUser());
        return   ResponseBean.buildSuccess("新增菜单成功",menuDTO);
    }

    /**
     * 菜单删除
     *
     * @param menuIds
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseBean deleteMenu(@RequestBody(required = false) List<Long> menuIds) {
        log.info("菜单管理--------菜单删除--------菜单id：{}", menuIds);
        menuService.deleteMenu(menuIds);
        return   ResponseBean.buildSuccess("菜单删除成功");

    }

    /**
     * 菜单修改
     *
     * @param menuId
     * @param menuDTO
     * @return
     */
    @RequestMapping(value = "/{menuId}", method = RequestMethod.PUT)
    public ResponseBean updateMenu(@PathVariable Long menuId, @RequestBody(required = false) MenuDTO menuDTO) {
        log.info("菜单管理--------菜单修改--------菜单id：{}，修改信息：{}", menuId, menuDTO);
        menuDTO.setId(menuId);
        menuService.updateMenu(menuDTO, UserUtils.getAuthUser());
        return   ResponseBean.buildSuccess("菜单修改成功");
    }



    /**
     * 菜单详情查询
     *
     * @param menuId
     * @return
     */
    @RequestMapping(value = "/{menuId}", method = RequestMethod.GET)
    public ResponseBean getMenu(@PathVariable Integer menuId) {
        log.info("菜单管理--------菜单查询--------查询菜单id：{}", menuId);
        return   ResponseBean.buildSuccess("菜单查询成功", menuService.getMenu(menuId));
    }

    /**
     * 菜单列表查询
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseBean getUsers(MenuDTO dto) {
        log.info("菜单管理--------菜单列表查询--------查询条件：{}", dto);
        if (dto.getToTree() != null && dto.getToTree()) {
            return  ResponseBean.buildSuccess("菜单列表查询成功", menuService.getMenusNoPageToTree(dto));
        }
        return   ResponseBean.buildSuccess("菜单列表查询成功", menuService.getMenus(dto));
    }






}
