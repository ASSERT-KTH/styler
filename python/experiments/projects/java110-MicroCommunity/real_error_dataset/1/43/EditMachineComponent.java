package com.java110.web.components.machine;

import com.java110.core.context.IPageData;
import com.java110.web.smo.machine.IEditMachineSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 编辑小区组件
 */
@Component("editMachine")
public class EditMachineComponent {

    @Autowired
    private IEditMachineSMO editMachineSMOImpl;

    /**
     * 添加小区数据
     * @param pd 页面数据封装
     * @return ResponseEntity 对象
     */
    public ResponseEntity<String> update(IPageData pd){
        return editMachineSMOImpl.updateMachine(pd);
    }

    public IEditMachineSMO getEditMachineSMOImpl() {
        return editMachineSMOImpl;
    }

    public void setEditMachineSMOImpl(IEditMachineSMO editMachineSMOImpl) {
        this.editMachineSMOImpl = editMachineSMOImpl;
    }
}
