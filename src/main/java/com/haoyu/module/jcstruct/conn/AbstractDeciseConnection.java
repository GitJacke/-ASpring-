package com.haoyu.module.jcstruct.conn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import com.haoyu.module.jcstruct.interceptor.CheckBeans;
import com.haoyu.module.jcstruct.interceptor.CheckInterceptor;
import com.haoyu.module.jcstruct.interceptor.MappedCheckInterceptor;
import com.haoyu.module.jcstruct.refresh.RefreshService;

public abstract class AbstractDeciseConnection<T> implements DeciseConnection<T>, RefreshService
{

	private CheckBeans checkBeans;

	public CheckBeans getCheckBeans()
	{
		return checkBeans;
	}

	@Override
	public void refresh(ApplicationContext applicationContext)
	{
		Map<String, MappedCheckInterceptor> checksProxy = applicationContext.getBeansOfType(MappedCheckInterceptor.class);
		if (!CollectionUtils.isEmpty(checksProxy)) {

			Map<String, Set<CheckInterceptor>> checkInterceptorListMapping = new HashMap<String, Set<CheckInterceptor>>();
			Set<CheckInterceptor> globalList = new HashSet<CheckInterceptor>();

			for (MappedCheckInterceptor checkProxy : checksProxy.values()) {
				String[] ids = checkProxy.getIncludePatterns();
				if (ArrayUtils.isNotEmpty(ids)) {
					if (isContainGlobal(ids)) {
						analysisGlobal(checkProxy, globalList);
						continue;
					}

					// 在全局里面存在，则不需要加入到单个ID中
					if (!globalList.contains(checkProxy.getCheckInterceptor())) {
						for (String id : ids) {
							// 解析id
							analysisCommon(id, checkProxy, checkInterceptorListMapping);
						}
					}

				}

			}

			// 处理转换
			Iterator<Entry<String, Set<CheckInterceptor>>> iterator = checkInterceptorListMapping.entrySet().iterator();
			Map<String, CheckInterceptor[]> checkInterceptorMapping = new HashMap<String, CheckInterceptor[]>();
			Entry<String, Set<CheckInterceptor>> entry = null;
			while (iterator.hasNext()) {
				entry = iterator.next();
				CheckInterceptor[] tmp = new CheckInterceptor[entry.getValue().size()];
				entry.getValue().toArray(tmp);
				checkInterceptorMapping.put(entry.getKey(), tmp);
			}
			checkInterceptorListMapping.clear();
			checkInterceptorListMapping = null;

			// 全局转换
			CheckInterceptor[] globalCheck = new CheckInterceptor[globalList.size()];
			globalList.toArray(globalCheck);
			globalList.clear();
			globalList = null;

			checkBeans = new CheckBeans(globalCheck, checkInterceptorMapping);

		}
	}

	private boolean isContainGlobal(String[] ids)
	{
		for (String id : ids) {
			return "*".equals(id);
		}
		return false;
	}

	private void analysisGlobal(MappedCheckInterceptor checkProxy, Set<CheckInterceptor> globalList)
	{
		globalList.add(checkProxy.getCheckInterceptor());
	}

	private void analysisCommon(String id, MappedCheckInterceptor checkProxy, Map<String, Set<CheckInterceptor>> checkInterceptorListMapping)
	{
		if (checkInterceptorListMapping.containsKey(id)) {
			checkInterceptorListMapping.get(id).add(checkProxy.getCheckInterceptor());
		} else {
			Set<CheckInterceptor> set = new HashSet<CheckInterceptor>();
			set.add(checkProxy.getCheckInterceptor());
			checkInterceptorListMapping.put(id, set);
		}
	}
}
