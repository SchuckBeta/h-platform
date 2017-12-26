<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
    <%@ include file="/WEB-INF/views/include/backtable.jsp"%>
    <link rel="stylesheet" type="text/css" href="/css/credit-module-cri.css">
    <style>
        .table th{
            background: none;
        }
    </style>
     <script type="text/javascript">
     	var scoreReg=/^(\d{1,2}(\.\d{0,1})?|100|100.0)$/;
	    jQuery.validator.addMethod("ckScore", function(value, element) {
	        return this.optional(element) || scoreReg.test(value);    
	    }, "0到100最多一位小数");
	    $(document).ready(function() {
	    	$.ajax({
	            type: "GET",
	            url: "getScores",
	            data: "confId="+$("#affirmConfId").val(),
	            dataType: "json",
	            success: function (data) {
	                if(data){
	                	$.each(data,function(i,v){
	                		$("input[name='"+v.category+""+v.result+"']").val(v.score);
	                	});
	                }
	            }
	        });
	    	$("#inputForm").validate({
    				submitHandler : function(form) {
    					var dataJson=[];
    					$("#dataTb").find("input").each(function(i,v){
    						var data={};
    						data.category=$(this).attr("category");
    						data.result=$(this).attr("result");
    						data.score=$(this).val();
    						dataJson.push(data);
    					});
    					$("#dataJson").val(JSON.stringify(dataJson));
						form.submit();
					},
					errorPlacement : function(error, element) {
						error.appendTo(element.parent());
					}
    			});
	
	    });
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>${titleName}认定标准</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form  id="inputForm"
			modelAttribute="scoAffirmCriterion" action="save" method="post">
    	<input type="hidden" id="affirmConfId" name="affirmConfId" value="${confId}" />
    	<input type="hidden" id="dataJson" name="dataJson"  />
        <table class="table table-bordered table-hover table-credit-setting table-theme-default table-vertical-middle" style="margin-bottom: 20px;">
            <thead>
            <tr>
                <th>${categoryName }</th>
                <th colspan="${fn:length(resultList)}">结果及认定学分标准</th>
            </tr>
            </thead>
            <tbody id="dataTb">
            <c:forEach items="${categoryList}" var="category">
	            <tr>
	            	<td>${category.label}</td>
	            	<c:forEach items="${resultList}" var="result">
	            		 <td>
		            		 <div class="form-period-point form-period-point-other">
		            		 	${result.label}<input name="${category.value }${result.value}" class="required ckScore" category="${category.value }" result="${result.value }" type="text">学分
		            		 </div>
	            		 </td>
	            	</c:forEach>
	            </tr>
            </c:forEach>
            </tbody>
        </table>
        <div class="text-center">
            <button type="submit" class="btn-oe btn-primary-oe">保存</button>
            <button type="button" class="btn" onclick="javascript:location.href='${ctx}/sco/scoAffirmConf'">返回</button>
        </div>
    </form:form>
</div>
</body>
</html>