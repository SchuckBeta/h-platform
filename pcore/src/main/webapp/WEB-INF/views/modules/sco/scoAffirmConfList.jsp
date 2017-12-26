<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
	<%@ include file="/WEB-INF/views/include/backtable.jsp"%>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
	<style>
		.table th{
			background: none;
		}
		select.form-control{
			height: 30px;
		}
		.table-auto-button button{
			margin-bottom: 0;
		}
	</style>
    <script type="text/javascript">
    	function setV(id,cname){
    		location.href="${ctx}/sco/scoAffirmCriterionCouse/form?foreign_id="+id+"&fromPage=scoAffirmConf&cname="+encodeURIComponent(cname);
    	}
    	function procChange(id,ob){
    		$.ajax({
				type:'get',
				url:'${ctx}/sco/scoAffirmConf/updateProc?id='+id+'&proc='+$(ob).val(),
				success:function(data){
					top.$.jBox.tip(data.msg,'success',{persistent:true,opacity:0});
				}
			});
    	}
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>设置学分标准</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <c:if test="${not empty message}">
		<c:if test="${not empty type}"><c:set var="ctype" value="${type}"/></c:if>
		<c:if test="${empty type}"><c:set var="ctype" value="${fn:indexOf(message,'失败') eq -1?'success':'error'}"/></c:if>
		<div class="alert alert-${ctype}"><button data-dismiss="alert" class="close">×</button>${message}</div>
	</c:if>
    <form class="form-inline text-right">
        <button type="button" class="btn-oe btn-primary-oe" onclick="javascript:location.href='${ctx}/sco/scoAffirmConf/form'">创建认定项目</button>
    </form>
    <table class="table table-bordered table-condensed table-hover table-theme-default table-vertical-middle table-allow-credit table-auto-button">
        <thead>
        <tr>
            <th class="none-wrap">学分类型</th>
            <th class="none-wrap">学分项</th>
            <th class="none-wrap">课程/项目/竞赛类型</th>
            <th class="none-wrap">课程/项目/竞赛类别</th>
            <th class="none-wrap">学分认定流程</th>
            <!-- <th>备注</th> -->
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${map}" var="item">
				<c:forEach items="${item.value}" var="conf" varStatus="idx">
					<tr>
						<c:if test="${idx.index==0}">
							<td class="none-wrap" rowspan="${fn:length(item.value)}">${fns:getDictLabel(item.key, '0000000118', '')}</td>
						</c:if>
				            <td class="none-wrap">${fns:getDictLabel(conf.item, '0000000119', '')}</td>
				            <td>
					            <c:if test="${conf.item=='0000000128'}">
					           		${fns:getDictLabel(conf.category, 'project_style', '')}
					            </c:if>
					            <c:if test="${conf.item=='0000000129'}">
					           		${fns:getDictLabel(conf.category, 'competition_type', '')}
					            </c:if>
					            <c:if test="${conf.item=='0000000132'}">
					           		${fns:getDictLabel(conf.category, '0000000120', '')}
					            </c:if>

				            <td>
			            	 	<c:if test="${conf.item=='0000000128'}">
					           		${fns:getDictLabel(conf.subdivision, 'project_type', '')}
					            </c:if>
					            <c:if test="${conf.item=='0000000129'}">
					           		${fns:getDictLabel(conf.subdivision, 'competition_net_type', '')}
					            </c:if>
				            </td>
				            <td>
					            <c:if test="${item.key=='0000000122' or item.key=='0000000126' }">
					            	<select  class="form-control" onchange="procChange('${conf.id }',this)">
					                    <c:forEach var="actYw" items="${actYws }">
					                    	<c:if test="${actYw.id eq conf.procId}">
					                        	<option value="${actYw.id}" selected="selected">${actYw.group.name}</option>
					                    	</c:if>
					                    	<c:if test="${actYw.id ne conf.procId}">
					                        	<option value="${actYw.id}" >${actYw.group.name}</option>
					                    	</c:if>
					                    </c:forEach>
					                </select>
					            </c:if>
				            </td>
				            <%-- <td>${conf.remarks }</td> --%>
				            <td class="none-wrap">
					            <c:if test="${conf.type!='0000000122'}">
						            <c:if test="${conf.item!='0000000132'}">
							            <button type="button" class="btn-oe btn-primary-oe btn-sm-oe"
							            onclick="javascript:location.href='${ctx}/sco/scoAffirmCriterion/form?confId=${conf.id}&type=${item.key }&item=${conf.item }&category=${conf.category }'">设置学分标准</button>
						            </c:if>
					            </c:if>
					            <%-- <c:if test="${conf.type=='0000000122'}">
					            	<button type="button" class="btn-oe btn-primary-oe btn-sm-oe"
							            onclick="setV('${conf.id}','${fns:getDictLabel(conf.item, '0000000119', '')}')">设置学分标准</button>
					            </c:if> --%>
					            <c:if test="${conf.type=='0000000123' or conf.type=='0000000124' or conf.type=='0000000125'}">
					            <button type="button" class="btn-oe btn-primary-oe btn-sm-oe" onclick="javascript:location.href='${ctx}/sco/scoAllotRatio?confId=${conf.id}'">学分配比</button>
					            </c:if>
				                <button type="button" class="btn-oe btn-primary-oe btn-sm-oe" onclick="javascript:location.href='${ctx}/sco/scoAffirmConf/form?id=${conf.id}'">编辑</button>
				                <button type="button" class="btn btn-small" onclick="javascript:return confirmx('确认要删除吗？', function(){javascript:location.href='${ctx}/sco/scoAffirmConf/delete?id=${conf.id}'})">删除</button>
			                </td>
					</tr>
                </c:forEach>
		</c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>