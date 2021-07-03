package com.java110.web.components.org;

import com.java110.core.context.IPageData;
import com.java110.web.smo.org.IDeleteOrgSMO;
import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Component;

/**
 * 添加组织管理组件
 */
@Component("deleteOrg")
public class DeleteOrgComponent {

@Autowired
private IDeleteOrgSMO deleteOrgSMOImpl;

/**
 * 添加组织管理数据
 * @param pd 页面数据封装
 * @return ResponseEntity 对象
 */
public ResponseEntity<String> delete(IPageData pd){
        return deleteOrgSMOImpl.deleteOrg(pd);
    }

public IDeleteOrgSMO getDeleteOrgSMOImpl() {
        return deleteOrgSMOImpl;
    }

public void setDeleteOrgSMOImpl(IDeleteOrgSMO deleteOrgSMOImpl) {
        this.deleteOrgSMOImpl = deleteOrgSMOImpl;
    }
            }
