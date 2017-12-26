/**
 * 
 */
package com.oseasy.initiate.common.supcan.freeform;

import com.oseasy.initiate.common.supcan.common.Common;
import com.oseasy.initiate.common.supcan.common.properties.Properties;
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
