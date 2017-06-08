package com.haoyu.module.jcstruct;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.haoyu.module.jcstruct.interceptor.MappedCheckInterceptor;

/**
 * 默认解析cjava自定义checks, 类似于spring拦截器标签实现
 * 
 * 
 * 
 * @author DELL
 *
 */
public class CjavaCheckBeanDefinitionParser implements BeanDefinitionParser
{

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext)
	{
		CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
		parserContext.pushContainingComponent(compDefinition);

		List<Element> interceptors = DomUtils.getChildElementsByTagName(element, "bean", "ref", "check");
		for (Element interceptor : interceptors) {
			RootBeanDefinition mappedInterceptorDef = new RootBeanDefinition(MappedCheckInterceptor.class);
			mappedInterceptorDef.setSource(parserContext.extractSource(interceptor));
			mappedInterceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

			ManagedList<String> includeIds = null;
			Object interceptorBean;
			if ("check".equals(interceptor.getLocalName())) {
				includeIds = getIncludeIds(interceptor, "contains");
				Element beanElem = DomUtils.getChildElementsByTagName(interceptor, "bean", "ref").get(0);
				interceptorBean = parserContext.getDelegate().parsePropertySubElement(beanElem, null);
			} else {
				interceptorBean = parserContext.getDelegate().parsePropertySubElement(interceptor, null);
			}
			mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(0, includeIds);
			mappedInterceptorDef.getConstructorArgumentValues().addIndexedArgumentValue(1, interceptorBean);

			String beanName = parserContext.getReaderContext().registerWithGeneratedName(mappedInterceptorDef);
			parserContext.registerComponent(new BeanComponentDefinition(mappedInterceptorDef, beanName));
		}

		parserContext.popAndRegisterContainingComponent();
		return null;
	}

	private ManagedList<String> getIncludeIds(Element interceptor, String elementName)
	{
		List<Element> paths = DomUtils.getChildElementsByTagName(interceptor, elementName);
		ManagedList<String> patterns = new ManagedList<String>(paths.size());
		for (Element path : paths) {
			String id = path.getAttribute("id");
			if (StringUtils.hasText(id)) {
				String[] tmps = id.split(",");
				for (String tmp : tmps) {
					patterns.add(tmp);
				}
			}

		}
		return patterns;
	}

}
