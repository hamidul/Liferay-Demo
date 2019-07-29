package com.proliferay;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author hamidul
 */
public class SampleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_log.info("##########THE BUNDLE IS STARTING############");
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		_log.info("##########THE BUNDLE IS STOPPING############");
	}

	private static final Log _log = LogFactoryUtil.getLog(SampleActivator.class);
}