/**
 *
 */
package com.hch.platform.pcore.modules.cms.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="template")
public class CmsTemplate  {

	private String value;
	private String name;
	private String jsonparam;	
	private String content;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getJsonparam() {
		return jsonparam;
	}
	public void setJsonparam(String jsonparam) {
		this.jsonparam = jsonparam;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}	

}