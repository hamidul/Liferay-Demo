package com.proliferay.context.contributor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.template.TemplateContextContributor;

/**
 * @author hamidul
 */
@Component(
	immediate = true,
	property = {"type=" + TemplateContextContributor.TYPE_GLOBAL},
	service = TemplateContextContributor.class
)
public class DemoTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects, HttpServletRequest request) {
		
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
		
		String today = sdf.format(date);

		contextObjects.put("today", today); 
	}

}