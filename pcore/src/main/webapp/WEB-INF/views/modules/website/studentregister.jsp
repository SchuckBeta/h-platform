<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="decorator" content="site-decorator"/>
    <!--公用重置样式文件-->
    <!--头部导航公用样式-->
    <!--学生登录页面样式表-->
    <link rel="stylesheet" type="text/css" href="/css/studentregister.css?v=1"/>

    <title>${frontTitle}</title>
    <style>
        .one {
            position: relative;
        }


        #sp {
            position: absolute;
            top: 27px;
        }

        #psw {
            position: absolute;
            top: 140px;
        }

        #rew {
            position: absolute;
            top: 200px;
        }

        #yz {
            position: absolute;
            top: 85px;
        }

        .form-group-ls {
            position: relative;
            margin-bottom: 18px;
        }

        .form-group-ls .btn-getpassword, .form-group-ls .get-code {
            display: block;
            position: absolute;
            padding: 0 8px;
            right: 0;
            top: 1px;
            height: 32px;
            line-height: 32px;
            text-decoration: none;
            color: #333;
            cursor: pointer;
        }

        .form-group-ls .btn-getpassword:hover, .form-group-ls .get-code:hover {
            text-decoration: underline;
        }

        .form-group-telphone .form-control {
            padding-right: 86px;
        }
        .btn-getYZM{
            display: block;
            position: absolute;
            right: 1px;
            top: 1px;
            border: 1px solid transparent;
            background-color: transparent;
            height: 32px;
            vertical-align: middle;
        }

    </style>

</head>
<body>
<!--顶部header公用部分-->
<div id="frontPath" class="hidden">${ctxFront}</div>
<div class="wholebox">
    <div class="content">
        <div class="mainbox">
            <h5>注册账号</h5>
            <div class="box">
                <div class="redtop"></div>
                <div class="fillboxone">
                    <form:form id="registerfm" action="${ctxFront}/register/saveRegister" method="post" class="one">
                        <div id="sp"></div>
                        <div class="mutip form-group-ls" style="margin-top: 30px;">
                            <%--<label><span class="help-inline"></span></label>--%>
                            <input type="text" class="form-control" name="mobile" placeholder="请输入手机号"/>
                        </div>

                        <div id="yz"></div>
                        <div class="mutip form-group-ls">
                            <%--<label><span class="help-inline"></span></label>--%>
                            <input type="button" class="btn btn-getYZM" id="OK" value="获取验证码"/>
                            <input type="text" id="yzma" class="form-control" name="yanzhengma" placeholder="请输入验证码"/>
                        </div>

                        <div id="psw"></div>
                        <div class="mutip setpasscode">
                            <%--<label><span class="help-inline"></span></label>--%>
                            <input type="password" id="password" class="form-control" name="password" placeholder="请输入6~14位数字、字母"/>
                        </div>
                        <div id="rew"></div>
                        <div class="mutip" style="margin-bottom: 25px;">
                            <%--<label><span class="help-inline"></span></label>--%>
                            <input type="password" id="repassword" class="form-control" name="confirm_password" placeholder="请确认登录密码"/>
                            <!-- <sapn id=""></sapn> -->
                        </div>
                        <!-- <button id="btRegister" type="buttom">注册</button> -->
                        <div id="messageBox"
                             style="height: 36px; padding: 0px; background: orangered; position: relative;  margin-bottom: 0px; ${empty message ? 'display: none' : ''};"
                             class="alert alert-error">
                            <button type="button"
                                    onclick="javascript:$(this).parent().remove();"
                                    data-dismiss="alert"
                                    style="width: 16px; height: 16px; line-height: 16px; text-align: center; top: -8px; right: 5px; position: absolute; border-radius: 50%;border-width:0; display: none;"
                                    class="close">×
                            </button>
                            <label id="loginError"
                                   style="font-size: 14px !important; color: white; background: none; padding: 0px; margin-left: 5px; line-height: 30px; font-weight: normal; text-align: left;"
                                   class="error">${message}</label>
                        </div>
                        <input type="button" class="btn btn-block btn-primary-oe" id="btRegister" value="注    册" style="margin-top: 20px;"/>
                        <div class="links"><a class="right" href="/f/toLogin">已有账号，去登录</a></div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="/common/common-js/jquery.min.js" type="text/javascript" charset="utf-8"></script>
<script src="/common/common-js/jquery.validate.min.js" type="text/javascript" charset="utf-8"></script>
<script src="/static/modules/cms/front/register/studentResgister.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    /* var a=$('<a href="/f/toLogin">登录</a>');
     a.css({
     "position":"absolute",
     "right":"56px",
     "top":"45px",
     "color":"#df4526",
     "font-size":"14px"
     })
     $('.mid').append(a);
     $('.user').append('<a href="/f/toLogin">登录</a>');*/
</script>
</body>

</html>
