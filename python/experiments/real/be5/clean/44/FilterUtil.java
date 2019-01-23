package com.developmentontheedge.be5.operation.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.developmentontheedge.be5.base.FrontendConstants.SEARCH_PARAM;
import static com.developmentontheedge.be5.base.FrontendConstants.SEARCH_PRESETS_PARAM;


public class FilterUtil
{
    public static Map<String, Object> getOperationParamsWithoutFilter(Map<String, Object> operationParams)
    {
        if (!operationParams.containsKey(SEARCH_PARAM))
        {
            return operationParams;
        }

        if (operationParams.get(SEARCH_PRESETS_PARAM) == null)
        {
            return Collections.emptyMap();
        }

        List<String> notFilterParams = Arrays.asList(((String) operationParams.get(SEARCH_PRESETS_PARAM)).split(","));

        return operationParams.entrySet()
                .stream()
                .filter(e -> notFilterParams.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
