package com.java110.web.components.machine;

import com.java110.core.context.IPageData;
import com.java110.web.smo.machine.IAddMachineSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 添加设备组件
 */
@Component("addMachine")
public class AddMachineComponent {

    @Autowired
    private IAddMachineSMO addMachineSMOImpl;

    /**
     * 添加设备数据
     * @param pd 页面数据封装
     * @return ResponseEntity 对象
     */
    public ResponseEntity<String> save(IPageData pd){
        return addMachineSMOImpl.saveMachine(pd);
    }

    public IAddMachineSMO getAddMachineSMOImpl() {
        return addMachineSMOImpl;
    }

    public void setAddMachineSMOImpl(IAddMachineSMO addMachineSMOImpl) {
        this.addMachineSMOImpl = addMachineSMOImpl;
    }
}
