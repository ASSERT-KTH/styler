package com.java110.api.listener.fee;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.api.bmo.fee.IFeeBMO;
import com.java110.api.listener.AbstractServiceApiDataFlowListener;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.core.smo.fee.IFeeConfigInnerServiceSMO;
import com.java110.core.smo.fee.IFeeInnerServiceSMO;
import com.java110.core.smo.common.ICarInoutInnerServiceSMO;
import com.java110.core.smo.community.IRoomInnerServiceSMO;
import com.java110.entity.center.AppService;
import com.java110.entity.order.Orders;
import com.java110.core.event.service.api.ServiceDataFlowEvent;
import com.java110.utils.constant.CommonConstant;
import com.java110.utils.constant.ServiceCodeConstant;
import com.java110.utils.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @ClassName PayFeeListener
 * @Description TODO 预交费（临时停车费）侦听
 * @Author wuxw
 * @Date 2019/6/3 13:46
 * @Version 1.0
 * add by wuxw 2019/6/3
 **/
@Java110Listener("payFeePreTempCarInoutListener")
public class PayFeePreTempCarInoutListener extends AbstractServiceApiDataFlowListener {

    private static Logger logger = LoggerFactory.getLogger(PayFeePreTempCarInoutListener.class);

    @Autowired
    private IFeeBMO feeBMOImpl;

    @Autowired
    private IFeeInnerServiceSMO feeInnerServiceSMOImpl;

    @Autowired
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;

    @Autowired
    private ICarInoutInnerServiceSMO carInoutInnerServiceSMOImpl;

    @Autowired
    private IFeeConfigInnerServiceSMO feeConfigInnerServiceSMOImpl;


    @Override
    public String getServiceCode() {
        return ServiceCodeConstant.SERVICE_CODE_PAY_FEE_PRE_TEMP_CAR_INOUT;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public void soService(ServiceDataFlowEvent event) {

        logger.debug("ServiceDataFlowEvent : {}", event);

        DataFlowContext dataFlowContext = event.getDataFlowContext();
        AppService service = event.getAppService();

        String paramIn = dataFlowContext.getReqData();

        //校验数据
        validate(paramIn);
        JSONObject paramObj = JSONObject.parseObject(paramIn);

        HttpHeaders header = new HttpHeaders();
        dataFlowContext.getRequestCurrentHeaders().put(CommonConstant.HTTP_ORDER_TYPE_CD, "D");
        JSONArray businesses = new JSONArray();
        paramObj.put("cycles", 0);
        //添加单元信息
        businesses.add(feeBMOImpl.addFeeTempDetail(paramObj, dataFlowContext));
        businesses.add(feeBMOImpl.modifyTempFee(paramObj, dataFlowContext));
        businesses.add(feeBMOImpl.modifyTempCarInout(paramObj, dataFlowContext));

        dataFlowContext.getRequestCurrentHeaders().put(CommonConstant.ORDER_PROCESS,Orders.ORDER_PROCESS_ORDER_PRE_SUBMIT);
        ResponseEntity<String> responseEntity = feeBMOImpl.callService(dataFlowContext, service.getServiceCode(), businesses);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            dataFlowContext.setResponseEntity(responseEntity);
            return;
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        paramOut.put("receivableAmount", paramObj.getString("receivableAmount"));

        responseEntity = new ResponseEntity<>(paramOut.toJSONString(), HttpStatus.OK);
        dataFlowContext.setResponseEntity(responseEntity);
    }



    /**
     * 刷入order信息
     *
     * @param orders  订单信息
     * @param headers 头部信息
     */
    protected void freshOrderProtocol(JSONObject orders, Map<String, String> headers) {
        feeBMOImpl.freshOrderProtocol(orders, headers);
        orders.put("orderProcess", Orders.ORDER_PROCESS_ORDER_PRE_SUBMIT);

    }


    /**
     * 数据校验
     *
     * @param paramIn "communityId": "7020181217000001",
     *                "memberId": "3456789",
     *                "memberTypeCd": "390001200001"
     */
    private void validate(String paramIn) {
        Assert.jsonObjectHaveKey(paramIn, "communityId", "请求报文中未包含communityId节点");
        Assert.jsonObjectHaveKey(paramIn, "receivedAmount", "请求报文中未包含receivedAmount节点");
        Assert.jsonObjectHaveKey(paramIn, "feeId", "请求报文中未包含feeId节点");

        JSONObject paramInObj = JSONObject.parseObject(paramIn);
        Assert.hasLength(paramInObj.getString("communityId"), "小区ID不能为空");
        Assert.hasLength(paramInObj.getString("receivedAmount"), "实收金额不能为空");
        Assert.hasLength(paramInObj.getString("feeId"), "费用ID不能为空");

    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }


    public IFeeInnerServiceSMO getFeeInnerServiceSMOImpl() {
        return feeInnerServiceSMOImpl;
    }

    public void setFeeInnerServiceSMOImpl(IFeeInnerServiceSMO feeInnerServiceSMOImpl) {
        this.feeInnerServiceSMOImpl = feeInnerServiceSMOImpl;
    }

    public IFeeConfigInnerServiceSMO getFeeConfigInnerServiceSMOImpl() {
        return feeConfigInnerServiceSMOImpl;
    }

    public void setFeeConfigInnerServiceSMOImpl(IFeeConfigInnerServiceSMO feeConfigInnerServiceSMOImpl) {
        this.feeConfigInnerServiceSMOImpl = feeConfigInnerServiceSMOImpl;
    }

    public IRoomInnerServiceSMO getRoomInnerServiceSMOImpl() {
        return roomInnerServiceSMOImpl;
    }

    public void setRoomInnerServiceSMOImpl(IRoomInnerServiceSMO roomInnerServiceSMOImpl) {
        this.roomInnerServiceSMOImpl = roomInnerServiceSMOImpl;
    }
}
