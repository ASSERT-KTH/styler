package com.ctrip.framework.apollo.portal.service.txtresolver;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.dto.ItemChangeSets;
import com.ctrip.framework.apollo.core.dto.ItemDTO;
import com.ctrip.framework.apollo.core.utils.StringUtils;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component("fileTextResolver")
public class FileTextResolver implements ConfigTextResolver {


  @Override
  public ItemChangeSets resolve(long namespaceId, String configText, List<ItemDTO> baseItems) {
    ItemChangeSets changeSets = new ItemChangeSets();
    if (StringUtils.isEmpty(configText)) {
      return changeSets;
    }
    if (CollectionUtils.isEmpty(baseItems)) {
      changeSets.addCreateItem(createItem(namespaceId, configText));
    } else {
      ItemDTO beforeItem = baseItems.get(0);
      if (!configText.equals(beforeItem.getValue())) {//update
        changeSets.addUpdateItem(createItem(namespaceId, configText));
      }
    }

    return changeSets;
  }

  private ItemDTO createItem(long namespaceId, String value) {
    ItemDTO item = new ItemDTO();
    item.setNamespaceId(namespaceId);
    item.setValue(value);
    item.setKey(ConfigConsts.CONFIG_FILE_CONTENT_KEY);
    return item;
  }
}
