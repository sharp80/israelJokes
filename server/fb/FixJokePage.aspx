<%@ Page Language="C#" AutoEventWireup="true" CodeFile="FixJokePage.aspx.cs" Inherits="MyWebForm1" %>

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="en">
    <head>
        <link rel="stylesheet" type="text/css" href="style.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />


        <title> צחוקים - תיקון בדיחה</title>

        <script type="text/javascript">

                    var _gaq = _gaq || [];
                    _gaq.push(['_setAccount', 'UA-33179516-1']);
                    _gaq.push(['_trackPageview']);

                    (function () {
                        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
                    })();
        </script>

    </head>

    <body>
        <div id="fb-root"></div>
        <script>
            window.fbAsyncInit = function() {
			FB.init({
				appId      : '170936369682906',	// App ID
				xfbml      : true,
				version    : 'v2.1'
			});

                // Additional initialization code here
            };

            // Load the SDK Asynchronously
			(function(d, s, id){
				var js, fjs = d.getElementsByTagName(s)[0];
				if (d.getElementById(id)) {return;}
				js = d.createElement(s); js.id = id;
				js.src = "//connect.facebook.net/en_US/sdk.js";
				fjs.parentNode.insertBefore(js, fjs);
			}(document, 'script', 'facebook-jssdk'));
        </script>

        <script type="text/javascript">

            function script2() {
                if (document.getElementById("lblOutput").firstChild == null) {
                    //alert("null!");
                    document.getElementById("lblOutput").appendChild(document.createTextNode(""));
                } 
                else 
                {
                    //alert("not null!");
                    document.getElementById("lblOutput").firstChild.nodeValue = "";
                }
            }
        </script>

        <div id="box">
            <div id="header">
                <table>
                    <tr>
                        <td style="width:450px;">
                            <img id="mainIcon" alt="" src="images/site/fix_page_icon.png" align="left"/>                 
                        </td>
                        <td style="width:200px;">
                            <table cellspacing="10px">
                            <tr>
                                <td>
									<div class="fb-like" data-href="https://apps.facebook.com/israelijokes/" data-send="false" data-action="like" data-width="250" data-show-faces="false" data-font="arial" data-share="true"></div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <a href="http://app.appsflyer.com/israeli-jokes/?c=facebook-app" target=_blank>
                                        <img id="androidIcon" alt="" title="הורד את האפליקציה לאנדרואיד" src="images/site/android.png" align="right" />
                                    </a>
                                    <a href="http://itunes.apple.com/en/app/id511590756?mt=8" target=_blank>
                                        <img id="iphoneIcon" alt="" title="הורד את האפליקציה לאייפון" src="images/site/iPhone-Icon.png" align="right" />
                                    </a>
                                    <a href="http://jokes.mayaron.com/CategoriesPage.aspx" >
                                        <img id="homeIcon" alt="" title="חזור לעמוד הראשי" src="images/site/home.png" align="right" />
                                    </a>
                                </td>
                            </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>

           <div style="width:100%; height:450px; overflow:auto;">
                <div style="width:90%; height:100%; margin: 0px auto;">
                    <div style="height:10px"> </div>
                    <form id="Form1" method="post" runat="server" enctype="multipart/form-data" style="overflow:auto">
                        <table width="100%" dir='rtl'>
                            <tr>
                                <td style="width:15%;">
                                    <img alt="" src="images/site/tell_us_to_fix.PNG" style="margin-left:auto;margin-right:auto;display:block;" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <asp:Label id="jokeHeader" runat="server"></asp:Label>  
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <div style="width:65%;margin:0 auto;outline-style:solid;">
                                        <asp:TextBox id="messageText" rows="5" TextMode="MultiLine" Font-Names="Arial" Font-Size="Medium" name="TheJoke" runat="server" style="resize:none; height:200px; width:400px;" />
                                    </div>
                                </td>
                            </tr>
                        </table>
                        <table width="100%" dir='rtl' >
                            <tr>
                                <td style="width:50%">
                                    <img alt="" src="images/site/magic_number.PNG" align="right"/>
                                </td>
                                <td>
                                    <asp:TextBox ID="captchaText" runat="server" style="width:50px;"/>
                                    <img alt="" src="Turing.aspx" style="vertical-align:middle; width:40px; height:22px" />
                                </td>
                            </tr>
                        </table>

                        <div style="height:5px"> </div>

                        <table width="100%">
                            <tr align="left" >
<%--                                <td width="35%" style="height:115px">--%>
                                <td style="width:35%;height:115px">
<%--                                    <asp:imagebutton id="SubmitButton" runat="server" ImageUrl="images/site/send_for_fix.png" width="95%" OnClick="SubmitButton_Click"/>--%>
                                    <asp:imagebutton id="SubmitButton" runat="server" ImageUrl="images/site/send_for_fix.png" OnClick="SubmitButton_Click" style="width:95%"/>
                                </td>
<%--                                <td width="65%">--%>
                                <td style="width:65%">
                                    <asp:RequiredFieldValidator runat="server" id="RequiredFieldValidator3" controltovalidate="messageText" errormessage="רשום את ההודעה בבקשה" Display="None"/>
                                    <asp:RequiredFieldValidator runat="server" id="RequiredFieldValidator2" controltovalidate="captchaText" errormessage="רשום את המספר המופיע בירוק בבקשה"  Display="None"/>                                     

                                    <asp:RegularExpressionValidator 
                                        id="RegularExpressionValidator" runat="server" 
                                        ControlToValidate="messageText" 
                                        ErrorMessage="ההודעה יכולה להכיל בין 2 ל-300 תווים חוקיים בלבד"
                                        ValidationExpression="^[א-ת\s\S\*#@\?\-\.\,\!\:\n\r\t ]{2,300}$"
                                        Display="None"/>

                                    <div class='validationDiv'>
                                        <asp:ValidationSummary id="valSum" 
                                            DisplayMode="BulletList"
                                            EnableClientScript="true"
                                            HeaderText="אנא תקן את הדברים הבאים:"
                                            runat="server"/> 
                                        
                                        <asp:Label id="lblOutput" runat="server"></asp:Label>  
                                    </div>

                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>