package com.java110.web.components.org;

import com.java110.core.context.IPageData;
import com.java110.web.smo.org.IEditOrgSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 编辑小区组件
 */
@Component("editOrg")
public class EditOrgComponent {

    @Autowired
    private IEditOrgSMO editOrgSMOImpl;

    /**
     * 添加小区数据
     * @param pd 页面数据封装
     * @return ResponseEntity 对象
     */
    public ResponseEntity<String> update(IPageData pd){
        return editOrgSMOImpl.updateOrg(pd);
    }

    public IEditOrgSMO getEditOrgSMOImpl() {
        return editOrgSMOImpl;
    }

    public void setEditOrgSMOImpl(IEditOrgSMO editOrgSMOImpl) {
        this.editOrgSMOImpl = editOrgSMOImpl;
    }
}
