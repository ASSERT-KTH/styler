package com.java110.common.smo.impl;


import com.java110.common.dao.IFileRelServiceDao;
import com.java110.core.base.smo.BaseServiceSMO;
import com.java110.core.smo.file.IFileRelInnerServiceSMO;
import com.java110.core.smo.user.IUserInnerServiceSMO;
import com.java110.dto.PageDto;
import com.java110.dto.file.FileRelDto;
import com.java110.utils.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 文件存放内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class FileRelInnerServiceSMOImpl extends BaseServiceSMO implements IFileRelInnerServiceSMO {

    @Autowired
    private IFileRelServiceDao fileRelServiceDaoImpl;

    @Autowired
    private IUserInnerServiceSMO userInnerServiceSMOImpl;

    @Override
    public List<FileRelDto> queryFileRels(@RequestBody FileRelDto fileRelDto) {

        //校验是否传了 分页信息

        int page = fileRelDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            fileRelDto.setPage((page - 1) * fileRelDto.getRow());
        }

        List<FileRelDto> fileRels = BeanConvertUtil.covertBeanList(fileRelServiceDaoImpl.getFileRelInfo(BeanConvertUtil.beanCovertMap(fileRelDto)), FileRelDto.class);


        return fileRels;
    }


    @Override
    public int queryFileRelsCount(@RequestBody FileRelDto fileRelDto) {
        return fileRelServiceDaoImpl.queryFileRelsCount(BeanConvertUtil.beanCovertMap(fileRelDto));
    }

    public IFileRelServiceDao getFileRelServiceDaoImpl() {
        return fileRelServiceDaoImpl;
    }

    public void setFileRelServiceDaoImpl(IFileRelServiceDao fileRelServiceDaoImpl) {
        this.fileRelServiceDaoImpl = fileRelServiceDaoImpl;
    }

    public IUserInnerServiceSMO getUserInnerServiceSMOImpl() {
        return userInnerServiceSMOImpl;
    }

    public void setUserInnerServiceSMOImpl(IUserInnerServiceSMO userInnerServiceSMOImpl) {
        this.userInnerServiceSMOImpl = userInnerServiceSMOImpl;
    }
}
