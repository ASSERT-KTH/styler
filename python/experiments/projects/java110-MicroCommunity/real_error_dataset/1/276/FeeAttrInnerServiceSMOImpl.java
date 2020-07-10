package com.java110.fee.smo.impl;


import com.java110.utils.util.BeanConvertUtil;
import com.java110.fee.dao.IFeeAttrServiceDao;
import com.java110.core.base.smo.BaseServiceSMO;
import com.java110.core.smo.fee.IFeeAttrInnerServiceSMO;
import com.java110.core.smo.user.IUserInnerServiceSMO;
import com.java110.dto.PageDto;
import com.java110.dto.fee.FeeAttrDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 费用属性内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class FeeAttrInnerServiceSMOImpl extends BaseServiceSMO implements IFeeAttrInnerServiceSMO {

    @Autowired
    private IFeeAttrServiceDao feeAttrServiceDaoImpl;

    @Autowired
    private IUserInnerServiceSMO userInnerServiceSMOImpl;

    @Override
    public List<FeeAttrDto> queryFeeAttrs(@RequestBody  FeeAttrDto feeAttrDto) {

        //校验是否传了 分页信息

        int page = feeAttrDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            feeAttrDto.setPage((page - 1) * feeAttrDto.getRow());
        }

        List<FeeAttrDto> feeAttrs = BeanConvertUtil.covertBeanList(feeAttrServiceDaoImpl.getFeeAttrInfo(BeanConvertUtil.beanCovertMap(feeAttrDto)), FeeAttrDto.class);



        return feeAttrs;
    }


    @Override
    public int queryFeeAttrsCount(@RequestBody FeeAttrDto feeAttrDto) {
        return feeAttrServiceDaoImpl.queryFeeAttrsCount(BeanConvertUtil.beanCovertMap(feeAttrDto));    }

    public IFeeAttrServiceDao getFeeAttrServiceDaoImpl() {
        return feeAttrServiceDaoImpl;
    }

    public void setFeeAttrServiceDaoImpl(IFeeAttrServiceDao feeAttrServiceDaoImpl) {
        this.feeAttrServiceDaoImpl = feeAttrServiceDaoImpl;
    }

    public IUserInnerServiceSMO getUserInnerServiceSMOImpl() {
        return userInnerServiceSMOImpl;
    }

    public void setUserInnerServiceSMOImpl(IUserInnerServiceSMO userInnerServiceSMOImpl) {
        this.userInnerServiceSMOImpl = userInnerServiceSMOImpl;
    }
}
