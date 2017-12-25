package com.oseasy.initiate.modules.excellent.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 优秀展示Entity.
 * @author 9527
 * @version 2017-06-23
 */
public class ExcellentShow extends DataEntity<ExcellentShow> {

	private static final long serialVersionUID = 1L;
	private String coverImg;		// 封面图片
	private String type;		// 类别：0-项目，1-大赛，2-科研成果
	private String foreignId;		// 项目、大赛、科研成果id
	private String content;		// 页面内容
	private String isRelease;		// 是否发布：0-否，1-是
	private String isTop;		// 是否置顶：0-否，1-是
	private String isComment;		// 是否可评论：0-否，1-是
	private String views;		// 浏览量
	private String likes;		// 点赞量
	private String comments;		// 评论量
	private Date releaseDate;//发布时间
	private List<String> keywords;
	public ExcellentShow() {
		super();
	}

	public ExcellentShow(String id) {
		super(id);
	}

	
	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	@Length(min=0, max=128, message="封面图片长度必须介于 0 和 128 之间")
	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	@Length(min=0, max=64, message="类别：0-项目，1-大赛，2-科研成果长度必须介于 0 和 64 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=0, max=64, message="项目、大赛、科研成果id长度必须介于 0 和 64 之间")
	public String getForeignId() {
		return foreignId;
	}

	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Length(min=0, max=64, message="是否发布：0-否，1-是长度必须介于 0 和 64 之间")
	public String getIsRelease() {
		return isRelease;
	}

	public void setIsRelease(String isRelease) {
		this.isRelease = isRelease;
	}

	@Length(min=0, max=64, message="是否置顶：0-否，1-是长度必须介于 0 和 64 之间")
	public String getIsTop() {
		return isTop;
	}

	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}

	@Length(min=0, max=64, message="是否可评论：0-否，1-是长度必须介于 0 和 64 之间")
	public String getIsComment() {
		return isComment;
	}

	public void setIsComment(String isComment) {
		this.isComment = isComment;
	}

	@Length(min=0, max=11, message="浏览量长度必须介于 0 和 11 之间")
	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	@Length(min=0, max=11, message="点赞量长度必须介于 0 和 11 之间")
	public String getLikes() {
		return likes;
	}

	public void setLikes(String likes) {
		this.likes = likes;
	}

	@Length(min=0, max=11, message="评论量长度必须介于 0 和 11 之间")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}