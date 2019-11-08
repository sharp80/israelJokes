<%@ Page Language="C#" AutoEventWireup="true" CodeFile="AddJokePage.aspx.cs" Inherits="MyWebForm1" %>

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="en">
    <head>
        <link rel="stylesheet" type="text/css" href="style.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />

        <script src="http://code.jquery.com/jquery-latest.js"></script>
        <script src="Scripts/ajax.js" type="text/javascript"></script>

        <title> צחוקים - הוספת בדיחה</title>
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
                FB.getLoginStatus(function (response) {
                    updateUserName();
                });
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

        <script language="javascript">
            $(document).ready(
		        function () {
		            $("#lblOutput").click(function () {
		                document.lblOutput.text = "";
		            })
		        })

		        function updateUserName() {
		            FB.api('/me', function (response) {
		                //alert("response.name=" + response.name);
		                document.getElementById("userNameHidden").value = response.name;
		            });
		        }

		        function textCounter(field, countfield, maxlimit) {
		            if (field.value.length > maxlimit)
		                field.value = field.value.substring(0, maxlimit);
		            else
		                countfield.value = maxlimit - field.value.length;
		        }

		        function enableSpecificInputs(categoryInput) {
		            alert("categoryInput.selectedIndex=" + categoryInput.selectedIndex);
		        }    
        </script>

        <div id="box">
            <div id="header">
                <table>
                    <tr>
                        <td style="width:450px;">
                            <img id="mainIcon" alt="" src="images/site/jokes.png" align="left"/>                 
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
                                    <a href="<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/ConfirmationPage.aspx?type=3" >
                                        <img id="iphoneIcon" alt="" title="הורד את האפליקציה לאייפון" src="images/site/iPhone-Icon.png" align="right" />
                                    </a>
                                    <a href="<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/CategoriesPage.aspx" >
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
                        <table width="100%" dir='rtl' >
                            <tr>
                                <td style="width:15%">
                                    <img alt="" src="images/site/category.PNG" align="right" />
                                </td>
                                <td>
                                    <asp:listbox id="AppCategory1" runat="server" Rows="1" DataTextField="name" DataValueField="id" onchange="enableSpecificInputs(this)"></asp:listbox>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <img alt="" src="images/site/picture.PNG" align="right"/>
                                </td>
                                <td>
                                    <input id="fileUpload" type="file" name="fileUpload" runat="server" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <img alt="" src="images/site/header.PNG" align="right"/>
                                </td>
                                <td>
                                    <asp:TextBox id="headline" style="width:220px" runat="server"
                                        onkeyup="textCounter(headline, this.form.remLen_short, 30);" onkeydown="textCounter(headline, this.form.remLen_short, 30);"/>
                                    <input readonly="readonly" type="text" name="remLen_short" size="1" maxlength="1" value="30" style="text-align:center;background:gray;color:black" />
                                </td>
                            </tr>
                            <tr>
                                <td style="vertical-align:top">
                                    <img alt="" src="images/site/joke.PNG" align="right"/>
                                </td>
                                <td>
                                    <asp:TextBox id="TheJoke" rows="5" TextMode="MultiLine" Font-Names="Arial" Font-Size="Medium" name="TheJoke" runat="server" style="resize:none; height:150px; width:400px" />
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

                        <div style="height:10px"> </div>

                        <table width="100%">
                            <tr align="left" >
<%--                                <td width="40%" style="height:115px">--%>
                                <td style="width:40%;height:115px">
                                    <input id="userNameHidden" type="hidden" runat="server" />
<%--                                    <asp:imagebutton id="Imagebutton1" runat="server" ImageUrl="images/site/add_joke_icon.png" width="95%"  OnClientClick="getUserName()" OnClick="SubmitButton_Click"/>--%>
                                    <asp:imagebutton id="Imagebutton1" runat="server" ImageUrl="images/site/add_joke_icon.png" OnClick="SubmitButton_Click" style="width:95%" />
                                </td>
<%--                                <td width="60%">--%>
                                <td style="width:60%;">
                                    <asp:RequiredFieldValidator runat="server" id="RequiredFieldValidator3" controltovalidate="headline" errormessage="רשום כותרת לבדיחה/תמונה בבקשה" Display="None" style="text-align:right"/>
                                    <asp:RequiredFieldValidator runat="server" id="RequiredFieldValidator2" controltovalidate="captchaText" errormessage="רשום את המספר המופיע בירוק בבקשה"  Display="None"/> 

                                    <asp:RegularExpressionValidator 
                                        id="RegularExpressionValidator" runat="server" 
                                        ControlToValidate="headline" 
                                        ErrorMessage="הכותרת יכולה להכיל עד 30 תווים בלבד"
                                        ValidationExpression="^[א-ת\s\S\*#@\?\-\.\,\!\:\n\r\t ]{1,30}$"
                                        Display="None" />

                                    <asp:RegularExpressionValidator 
                                        id="RegularExpressionValidator1" runat="server" 
                                        ControlToValidate="TheJoke" 
                                        ErrorMessage="הבדיחה יכולה להכיל עד 1000 תווים חוקיים בלבד"
                                        ValidationExpression="^[א-ת\s\S\*#@\?\-\.\,\!\:\n\r\t ]{0,1000}$"
                                        Display="None" />

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