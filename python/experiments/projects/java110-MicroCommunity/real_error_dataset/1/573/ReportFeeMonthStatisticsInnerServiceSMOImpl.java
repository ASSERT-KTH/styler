package com.java110.report.smo.impl;


import com.java110.core.base.smo.BaseServiceSMO;
import com.java110.dto.PageDto;
import com.java110.dto.reportFeeMonthStatistics.ReportFeeMonthStatisticsDto;
import com.java110.intf.IReportFeeMonthStatisticsInnerServiceSMO;
import com.java110.po.reportFeeMonthStatistics.ReportFeeMonthStatisticsPo;
import com.java110.report.dao.IReportFeeMonthStatisticsServiceDao;
import com.java110.utils.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 费用月统计内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class ReportFeeMonthStatisticsInnerServiceSMOImpl extends BaseServiceSMO implements IReportFeeMonthStatisticsInnerServiceSMO {

    @Autowired
    private IReportFeeMonthStatisticsServiceDao reportFeeMonthStatisticsServiceDaoImpl;


    @Override
    public int saveReportFeeMonthStatistics(@RequestBody ReportFeeMonthStatisticsPo reportFeeMonthStatisticsPo) {
        int saveFlag = 1;
        reportFeeMonthStatisticsServiceDaoImpl.saveReportFeeMonthStatisticsInfo(BeanConvertUtil.beanCovertMap(reportFeeMonthStatisticsPo));
        return saveFlag;
    }

    @Override
    public int updateReportFeeMonthStatistics(@RequestBody ReportFeeMonthStatisticsPo reportFeeMonthStatisticsPo) {
        int saveFlag = 1;
        reportFeeMonthStatisticsServiceDaoImpl.updateReportFeeMonthStatisticsInfo(BeanConvertUtil.beanCovertMap(reportFeeMonthStatisticsPo));
        return saveFlag;
    }

    @Override
    public int deleteReportFeeMonthStatistics(@RequestBody ReportFeeMonthStatisticsPo reportFeeMonthStatisticsPo) {
        int saveFlag = 1;
        reportFeeMonthStatisticsPo.setStatusCd("1");
        reportFeeMonthStatisticsServiceDaoImpl.updateReportFeeMonthStatisticsInfo(BeanConvertUtil.beanCovertMap(reportFeeMonthStatisticsPo));
        return saveFlag;
    }

    @Override
    public List<ReportFeeMonthStatisticsDto> queryReportFeeMonthStatisticss(@RequestBody ReportFeeMonthStatisticsDto reportFeeMonthStatisticsDto) {

        //校验是否传了 分页信息

        int page = reportFeeMonthStatisticsDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            reportFeeMonthStatisticsDto.setPage((page - 1) * reportFeeMonthStatisticsDto.getRow());
        }

        List<ReportFeeMonthStatisticsDto> reportFeeMonthStatisticss = BeanConvertUtil.covertBeanList(reportFeeMonthStatisticsServiceDaoImpl.getReportFeeMonthStatisticsInfo(BeanConvertUtil.beanCovertMap(reportFeeMonthStatisticsDto)), ReportFeeMonthStatisticsDto.class);

        return reportFeeMonthStatisticss;
    }


    @Override
    public int queryReportFeeMonthStatisticssCount(@RequestBody ReportFeeMonthStatisticsDto reportFeeMonthStatisticsDto) {
        return reportFeeMonthStatisticsServiceDaoImpl.queryReportFeeMonthStatisticssCount(BeanConvertUtil.beanCovertMap(reportFeeMonthStatisticsDto));
    }

    public IReportFeeMonthStatisticsServiceDao getReportFeeMonthStatisticsServiceDaoImpl() {
        return reportFeeMonthStatisticsServiceDaoImpl;
    }

    public void setReportFeeMonthStatisticsServiceDaoImpl(IReportFeeMonthStatisticsServiceDao reportFeeMonthStatisticsServiceDaoImpl) {
        this.reportFeeMonthStatisticsServiceDaoImpl = reportFeeMonthStatisticsServiceDaoImpl;
    }
}
