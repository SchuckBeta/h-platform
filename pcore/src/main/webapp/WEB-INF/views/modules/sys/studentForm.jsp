

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <%--<meta name="decorator" content="site-decorator"/>--%>
    <title>学生门户-学生库详情</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/common/common-css/pagenation.css"/>
    <link rel="stylesheet" type="text/css" href="/common/common-css/backtable.css"/>
    <link rel="stylesheet" type="text/css" href="/css/studentForm.css"/>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.css"/>
    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.js"></script>
    <link href="${ctxStatic}/jquery-jbox/2.3/Skins/Bootstrap/jbox.min.css" rel="stylesheet"/>
    <script src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/common/initiate.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxStatic}/My97DatePicker/WdatePicker.js"></script>
    <script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/js/common.js" type="text/javascript"></script>
 <%--   <script type="text/javascript" src="/js/user/userPhotoUpload.js?v=17.5.31"></script> <!-- 图片上传 -->
    <script type="text/javascript" src="/common/common-js/ajaxfileupload.js"></script>--%>
    <script type="text/javascript">
        var validate;
        $(document).ready(function () {
            //增加学院下拉框change事件
            $("#collegeId").change(function () {
                var parentId = $(this).val();
                //根据当前学院id 更改
                $("#professionalSelect").empty();
                $("#professionalSelect").append('<option value="">--请选择--</option>');
                $.ajax({
                    type: "post",
                    url: "/a/sys/office/findProfessionals",
                    data: {"parentId": parentId},
                    async: true,
                    success: function (data) {
                        $.each(data, function (i, val) {
                            if (val.id == "${studentExpansion.user.professional}") {
                                $("#professionalSelect").append('<option selected="selected" value="' + val.id + '">' + val.name + '</option>')
                            } else {
                                $("#professionalSelect").append('<option value="' + val.id + '">' + val.name + '</option>')
                            }

                        })
                    }
                });

            })
            $("#collegeId").trigger('change');

            console.log("oldLoginName" , "${studentExpansion.user.loginName}");
            //校验初始化
            validate = $("#inputForm").validate({
                rules: {
                    "user.mobile": {
                        phone_number: true,//自定义的规则
                        remote: {
                            async: false,
                            url: "/a/sys/user/checkMobile",     //后台处理程序
                            type: "get",               //数据发送方式
                            dataType: "json",           //接受数据格式
                            data: {                     //要传递的数据
                                mobile: function () {
                                    return $("input[name='user.mobile']").val();
                                },
                                id: function () {
                                    return $("#userid").val();
                                }
                            }
                        }
                    },
                    "user.no": {
                        numberLetter : true,
                        remote: {
                            async: false,
                            url: "/a/sys/user/checkNo",     //后台处理程序
                            type: "post",               //数据发送方式
                            dataType: "json",           //接受数据格式
                            data: {                     //要传递的数据
                            	userid : "${studentExpansion.user.id}",
                                no : function () {
                                    return $("input[name='user.no']").val();
                                }
                            }
                        }
                    }
                },
                messages: {
                    "user.mobile": {
                        phone_number: "请输入正确的手机号码",
                        remote: "手机号已存在"
                    },
                    "user.no": {
                        remote: "该学号已被占用"
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element);
                }
            });
            //添加自定义验证规则
            jQuery.validator.addMethod("phone_number", function (value, element) {
                var length = value.length;
                return this.optional(element) || (length == 11 && mobileRegExp.test(value));
            }, "手机号码格式错误");

            //添加自定义验证规则
            jQuery.validator.addMethod("numberLetter", function (value, element) {
                var length = value.length;
                return  this.optional(element) ||numberLetterExp.test(value);
            }, "只能输入数字和字母");

            jQuery.validator.addMethod("isIdCardNo", function (value, element) {
                return this.optional(element) || IDCardExp.test(value);
            }, "身份证号码不正确");

            if($("#idType").val()=="1"){
                $("#userIdNumber").rules("add",{isIdCardNo:true});
            }

            $("#idType").change(function(){
                if($(this).val()=='1'){
                    $("#userIdNumber").rules("add",{isIdCardNo:true});
                }else{
                    $("#userIdNumber").rules("remove","isIdCardNo");
                    $("#userIdNumber").removeClass("error");
                    $("#userIdNumber").next("label").hide();
                }
            })

            //现状增加change事件，当选择毕业时，毕业时间必填
            $("#currState").change(function(){
                if($(this).val()=='2'){
                    $("#graduation").addClass("required");
                }else{
                    $("#graduation").val("");
                    $("#graduation").removeClass("required");
                    $("#graduation").removeClass("error");
                    $("#graduation").next("label").hide();
                }
            })
        });

    </script>
    <script type="text/javascript">
        function saveform() {
            if (validate.form()) {  //表单提交 调用校验
                $("#inputForm").submit();
            }
        }
    </script>
    <style>
        .form-search input, select{
            width: 100%;
            max-width: 100%;
        }
        .info-card .user-label-control{
            width: 100px;
        }
        .info-card .user-val{
            margin-left: 100px;
        }
    </style>
</head>
<body>

<div class="container">
    <input type="hidden" id="userid" value="${studentExpansion.user.id }"/>  <!--图片上传 mobile校验隐藏域  -->
    <div class="edit-bar clearfix" style="margin-top: 10px">
        <div class="edit-bar-left">
            <span>学生库</span>
            <i class="line"></i>
        </div>
    </div>
    <sys:frontTestCut width="200" height="200" btnid="upload" imgId="fileId" column="user.photo"  filepath="user"  className="modal-avatar"></sys:frontTestCut>
    <form:form id="inputForm" modelAttribute="studentExpansion" action="${ctx}/sys/studentExpansion/save" method="post"
               class="form-horizontal">
        <form:hidden path="id" id="studentId"/>
        <div style="text-align: right">
            <input id="savebtn" type="button" onclick="saveform()" value="保存" style="float:none" class="btn btn-md btn-save"
                   style="margin-right: 5px;"/>
            <input type="button" onclick="history.go(-1);" class="btn btn-back" value="返回">
        </div>
        <div class="row">
            <div class="left-aside">
                <div class="user-info">
                    <div class="user-inner">
                        <div class="user-pic">
                            <input type="file" style="display: none" id="fileToUpload" name="fileName"/>
                            <form:input type="hidden" path="user.photo" id="photo" />
                            <div class="img-content" style="background:none;">
                                <c:choose>
                                    <c:when test="${user.photo!=null && user.photo!='' }">
                                        <img src="${fns:ftpImgUrl(user.photo) }" class="img-responsive" id="fileId" />
                                    </c:when>
                                    <c:otherwise>
                                        <img src="/img/u4110.png" id="fileId" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                                <div class="up-content text-center">
                                    <input type="button" id="upload"  class="btn" style="" value="更新照片" />
                                </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="user-info-content clearfix">
                <div class="user-info-form">
                    <p class="ud-title">基本信息</p>
                    <div class="row">
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="userName" class="control-label"><i class="icon-require">*</i>姓名：</label>
                                <div class="input-box">
                                    <form:input id="userName" type="text" path="user.name" htmlEscape="false"
                                                class="form-control input-medium required"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label class="control-label"><i class="icon-require">*</i>性别：</label>
                                <div class="input-box">
                                    <form:radiobuttons path="user.sex" items="${fns:getDictList('sex')}" itemLabel="label" itemValue="value" class="required"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="idType" class="control-label"><i class="icon-require">*</i>证件类型：</label>
                                <div class="input-box">
                                    <form:select id="idType" path="user.idType"
                                                 class="input-xlarge form-control required">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('id_type')}"
                                                      itemLabel="label" itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="userIdNumber" class="control-label"><i
                                        class="icon-require">*</i>证件号：</label>
                                <div class="input-box">
                                    <form:input id="userIdNumber"  path="user.idNumber" htmlEscape="false"
                                                maxlength="128" class="input-xlarge required form-control"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="userCountry" class="control-label">国家/地区：</label>
                                <div class="input-box">
                                    <form:input id="userCountry" type="text" path="user.country" htmlEscape="false"
                                                maxlength="11"
                                                class="input-xlarge form-control"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="userNational" class="control-label">民族：</label>
                                <div class="input-box">
                                    <form:input id="userNational" path="user.national" htmlEscape="false" type="text"
                                                class="input-xlarge inputSp  form-control"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="mobile" class="control-label"><i class="icon-require">*</i>手机号：</label>
                                <div class="input-box">
                                    <form:input path="user.mobile" htmlEscape="false" id="mobile"
                                                maxlength="255" class="required form-control"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="userPolitical" class="control-label">
                                    政治面貌：
                                </label>
                                <div class="input-box">
                                    <form:input id="userPolitical" path="user.political" type="text"
                                                class="input-xlarge inputSp  form-control"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="userEmail" class="control-label"><i class="icon-require">*</i>电子邮箱：</label>
                                <div class="input-box">
                                    <form:input id="userEmail" path="user.email" htmlEscape="false" type="email"
                                                maxlength="255"
                                                class="input-xlarge form-control required"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="form-group">
                                <label for="userBirthday" class="control-label">出生年月：</label>
                                <div class="input-box">
                                    <input name="user.birthday" id="userBirthday" type="text" maxlength="20"
                                           class="input-medium isTime form-control Wdate"
                                           style="height: 34px"
                                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"
                                           value='<fmt:formatDate value="${studentExpansion.user.birthday}" pattern="yyyy-MM-dd"/>'

                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="address" class="control-label">联系地址：</label>
                                <div class="input-box">
                                    <form:input id="address" type="text" path="address" htmlEscape="false"
                                                class="input-xlarge form-control"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="user-info-content user-info-school clearfix">
                <div class="user-info-form">
                    <div class="row">
                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="collegeId" class="control-label"><i
                                        class="icon-require">*</i>院系名称：</label>
                                <div class="input-box">
                                    <form:select path="user.office.id" class="input-xlarge required form-control"
                                                 id="collegeId">
                                        <form:option value="" label="请选择"/>
                                        <form:options items="${fns:findColleges()}" itemLabel="name"
                                                      itemValue="id" htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="professionalSelect" class="control-label">专业：</label>
                                <div class="input-box">
                                    <form:select path="user.professional" class="input-xlarge form-control"
                                                 id="professionalSelect">
                                        <form:option value="" label="请选择"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="tClass" class="control-label">班级：</label>
                                <div class="input-box">
                                    <form:input  path="tClass" htmlEscape="false"  maxlength="20"  class="form-control"/>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="row">
                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="userNo" class="control-label"><i
                                        class="icon-require">*</i>学号：</label>
                                <div class="input-box">
                                    <form:input id="userNo"  path="user.no" htmlEscape="false"  maxlength="15"
                                                class="required form-control"/>
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="instudy" class="control-label">在读学位：</label>
                                <div class="input-box">
                                    <form:select id="instudy" path="instudy" class="input-xlarge  form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('degree_type')}"
                                                      itemLabel="label" itemValue="value" htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="userEducation" class="control-label">学历：</label>
                                <div class="input-box">
                                    <form:select id="userEducation" path="user.education"
                                                 class="input-xlarge form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('enducation_level')}"
                                                      itemLabel="label" itemValue="value" htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="userDegree" class="control-label">学位：</label>
                                <div class="input-box">
                                    <form:select id="userDegree" path="user.degree" class="input-xlarge form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('degree_type')}"
                                                      itemLabel="label" itemValue="value" htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="currState" class="control-label">
                                    <i  class="icon-require">*</i>现状：
                                </label>
                                <div class="input-box">
                                    <form:select id="currState" path="currState" class="input-xlarge form-control required">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('current_sate')}"
                                                      itemLabel="label" itemValue="value" htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="graduation" class="control-label">
                                   毕业时间：
                                </label>
                                <div class="input-box">
                                    <input id="graduation" name="graduation" style="height: 34px;" class="Wdate  form-control "
                                           value="<fmt:formatDate value='${studentExpansion.graduation}' pattern='yyyy-MM-dd'/>"
                                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">

                        <div class="col-sm-4">
                            <div class="form-group">
                                <label for="temporaryDate" class="control-label">休学时间：</label>
                                <div class="input-box">
                                    <input id="temporaryDate" name="temporaryDate"
                                           class="input-medium Wdate form-control"
                                           style="height: 34px;"
                                           value="<fmt:formatDate value="${studentExpansion.temporaryDate}" pattern="yyyy-MM-dd"/>"
                                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="form-group">
                                <label for="user.domainIdList" class="control-label">技术领域：</label>
                                <div class="input-box">
                                    <form:checkboxes path="user.domainIdList" items="${allDomains}"
                                                     itemLabel="label" itemValue="value"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
    <c:if test="${studentExpansion.user.id!=null}">
    <div class="user-details-info">
                     <div class="user-detail-group">
                         <div class="ud-inner">
                             <p class="ud-title">项目经历</p>
                             <div class="info-cards">
                                 <c:forEach items="${projectExpVo}" var="projectExp">
                                 <div class="info-card">
                                     <p class="info-card-title"><c:if test="${projectExp.finish==0 }"><span style="color: #e9432d;">【进行中】</span></c:if>${projectExp.proName}</p>
                                     <div class="row">
                                         <div class="col-sm-12">
                                             <div class="ic-box">
                                                 <label class="user-label-control">项目周期：</label>
                                                 <div class="user-val">
                                                     <fmt:formatDate value="${projectExp.startDate }" pattern="yyyy/MM/dd" />
                                                     -
                                                     <fmt:formatDate value="${projectExp.endDate }" pattern="yyyy/MM/dd" />
                                                 </div>
                                             </div>
                                         </div>
                                         <div class="col-sm-6">
                                             <div class="ic-box">
                                                 <label class="user-label-control">项目名称：</label>
                                                 <div class="user-val"> ${projectExp.name }</div>
                                             </div>
                                         </div>
                                        <div class="col-md-6">
                                             <div class="ic-box">
                                                 <label class="user-label-control">担任角色：</label>
                                                 <div class="user-val">
                                                 	<c:if test="${cuser==projectExp.leaderId}">项目负责人</c:if>
                                                 	<c:if test="${cuser!=projectExp.leaderId&&projectExp.userType=='1'}">组成员</c:if>
                                                 	<c:if test="${cuser!=projectExp.leaderId&&projectExp.userType=='2'}">导师</c:if>
                                                 </div>
                                             </div>
                                         </div>
                                         <div class="col-sm-6">
                                             <div class="ic-box">
                                                 <label class="user-label-control">项目级别：</label>
                                                 <div class="user-val"> ${projectExp.level }</div>
                                             </div>
                                         </div>
                                         <div class="col-md-6">
                                             <div class="ic-box">
                                                 <label class="user-label-control">项目结果：</label>
                                                 <div class="user-val">
                                                         ${projectExp.result }
                                                 </div>
                                             </div>
                                         </div>
                                     </div>
                                 </div>
                                 </c:forEach>
                             </div>
                             <p class="ud-title">大赛经历</p>
                             <div class="info-cards">
                                 <c:forEach items="${gContestExpVo}" var="gContest">
                                 <div class="info-card info-match">
                                     <p class="info-card-title"><c:if test="${gContest.finish==0 }"><span style="color: #e9432d;">【进行中】</span></c:if>${gContest.type}</p>
                                     <div class="row">
                                         <div class="col-md-6">
                                             <div class="ic-box">
                                                 <label class="user-label-control">参赛项目名称：</label>
                                                 <div class="user-val">
                                                         ${gContest.pName }
                                                 </div>
                                             </div>
                                         </div>
                                         <div class="col-sm-6">
                                             <div class="ic-box">
                                                 <label class="user-label-control">参赛时间：</label>
                                                 <div class="user-val"><fmt:formatDate value="${gContest.createDate }"  pattern="yyyy/MM/dd" /></div>
                                             </div>
                                         </div>
                                         
										<div class="col-md-6">
                                             <div class="ic-box">
                                                 <label class="user-label-control">担任角色：</label>
                                                 <div class="user-val">
                                                 	<c:if test="${cuser==gContest.leaderId}">项目负责人</c:if>
                                                 	<c:if test="${cuser!=gContest.leaderId&&gContest.userType=='1'}">组成员</c:if>
                                                 	<c:if test="${cuser!=gContest.leaderId&&gContest.userType=='2'}">导师</c:if>
                                                 </div>
                                             </div>
                                         </div>
                                         <div class="col-md-6">
                                             <div class="ic-box">
                                                 <label class="user-label-control">获奖情况：</label>
                                                 <div class="user-val">
                                                   ${gContest.award}
                                                 </div>
                                             </div>
                                         </div>
                                     </div>
                                 </div>
                                 </c:forEach>
                             </div>
                         </div>
                     </div>
                 </div>
    </c:if>
</div>
<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>

</body>
</html>