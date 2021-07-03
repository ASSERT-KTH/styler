package com.java110.web.components.machine;

import com.java110.core.context.IPageData;
import com.java110.web.smo.machine.IDeleteMachineSMO;
import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Component;

/**
 * 添加设备组件
 */
@Component("deleteMachine")
public class DeleteMachineComponent {

@Autowired
private IDeleteMachineSMO deleteMachineSMOImpl;

/**
 * 添加设备数据
 * @param pd 页面数据封装
 * @return ResponseEntity 对象
 */
public ResponseEntity<String> delete(IPageData pd){
        return deleteMachineSMOImpl.deleteMachine(pd);
    }

public IDeleteMachineSMO getDeleteMachineSMOImpl() {
        return deleteMachineSMOImpl;
    }

public void setDeleteMachineSMOImpl(IDeleteMachineSMO deleteMachineSMOImpl) {
        this.deleteMachineSMOImpl = deleteMachineSMOImpl;
    }
            }
