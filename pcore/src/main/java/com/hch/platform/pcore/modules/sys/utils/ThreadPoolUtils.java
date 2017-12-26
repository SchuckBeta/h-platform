package com.hch.platform.pcore.modules.sys.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hch.platform.pconfig.common.Global;

public class ThreadPoolUtils {
	public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Integer.parseInt(Global.getConfig("threadMaxNum")));
}
