/**
 * 
 */
package com.hch.platform.pcore.common.supcan.freeform;

import com.hch.platform.pcore.common.supcan.common.Common;
import com.hch.platform.pcore.common.supcan.common.properties.Properties;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 硕正FreeForm
 * @author WangZhen

 */
@XStreamAlias("FreeForm")
public class FreeForm extends Common {

	public FreeForm() {
		super();
	}
	
	public FreeForm(Properties properties) {
		this();
		this.properties = properties;
	}
	
}
