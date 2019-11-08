<%@ Page Language="C#" AutoEventWireup="true" CodeFile="CategoriesPage.aspx.cs" Inherits="CategoriesPage" %>

    <!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="en">
    <head>
        <link rel="stylesheet" type="text/css" href="style.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <script src="https://code.jquery.com/jquery-latest.js"></script>

<%--		
        siteUrl = ConfigurationSettings.AppSettings["siteUrl"].ToString();
        <meta property="og:title" content="צחוקים" />
        <meta property="og:type" content="game" />
        <meta property="og:image" content = siteUrl+"fb/images/site/jokes_icon2.png" />
        <meta property="og:site_name" content="צחוקים" />
        <meta property="fb:admins" content="586093716" />
        <meta property="fb:app_id" content="170936369682906" />
--%>

        <script src="Scripts/ajax.js" type="text/javascript"></script>

        <title> צחוקים - עמוד ראשי</title>

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

    <div id="fb-root"></div>
    <script>
			//alert("first thing");
			window.fbAsyncInit = function() {
			FB.init({
				appId      : '170936369682906',	// App ID
				xfbml      : true,
				version    : 'v2.1'
			});

            // Additional initialization code here

            FB.getLoginStatus(function (response) {
                if (response.status == 'connected') {
                    var user_id = response.authResponse.userID;
                    var page_id = "170936369682906"; // App ID
                    //var page_id = "121031254688991"; // App ID
                    //alert("user_id=" + user_id + " page_id=" + page_id);
                    var fql_query = "SELECT uid FROM page_fan WHERE page_id =" + page_id + " and uid=" + user_id;
                    var the_query = FB.Data.query(fql_query);

                    //the_query.wait(function (rows) {
					//	alert("rows.length=" + rows.length);
                    //    if (rows.length == 1 && rows[0].uid == user_id) {
                    //        //$("#container_like").show();
                    //alert("YOU LIKE US!");

//                            //here you could also do some ajax and get the content for a "liker" instead of simply showing a hidden div in the page.
//
//                      } else {
//                            //$("#container_notlike").show();
//                            //and here you could get the content for a non liker in ajax...
//                            //alert("YOU DON'T LIKE US YET!");
//                            window.location = "https://israelijokes.apphb.com/server/fb//ConfirmationPage.aspx?type=4";
//                        }
//                    });
                } else {
                    // user is not logged in
                }
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


   <script>
   			//alert("first thing");

       var categoriesJason;
       var numOfCategories;


       var NUM_OF_ICONS_PER_LINE = 4;

       var CATEGORIES_IDS = 
       {
			   "video":       1,
		       "pictures":    2,
		       "strange":     4,
		       "general":     6,
		       "olds":        7,
		       "adults":      10,
		       "children":    25,
		       "animals":     24,
		       "army":        23,
		       "blondes":     16,
		       "family":      22,
		       "status":      18,
		       "races":       19,
		       "religon":     21,
		       "arsim":       12,
		       "questions":   15,
		       "noris":       20,
		       "politics":    13,
		       "work":        17
       };

       var categoriesOrder = new Array
                                (
                                    CATEGORIES_IDS.adults,
                                    CATEGORIES_IDS.general,
					                CATEGORIES_IDS.pictures,
                    		        CATEGORIES_IDS.children,
									CATEGORIES_IDS.blondes,
                                    CATEGORIES_IDS.family,
									CATEGORIES_IDS.animals,
                             		CATEGORIES_IDS.status,
									CATEGORIES_IDS.video, 
									CATEGORIES_IDS.strange,
									CATEGORIES_IDS.olds,
									CATEGORIES_IDS.army, 
									CATEGORIES_IDS.races, 
									CATEGORIES_IDS.religon,
									CATEGORIES_IDS.arsim,  
									CATEGORIES_IDS.questions,
									CATEGORIES_IDS.noris, 
									CATEGORIES_IDS.politics,
									CATEGORIES_IDS.work 
                                );

       var rootURL = '<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>'
       var getDataFromServerURL = rootURL + "ShowAllCategories.aspx";
		//alert("alert");
       console.log(getDataFromServerURL);

       $(document).ready(
		    function () {
				//alert("ready");
		        getDataFromServer();
		        buildCategoriesTable();
		        UpdateIconSizes();
		    })

		function getDataFromServer() {
		    var req = createXHR();
		    req.onreadystatechange = function () {
		        if (req.readyState == 4) {
		            if (req.status == 200) {
		                if (req.status != 404) {
		                    console.log(req.responseText)
		                    categoriesJason = eval('(' + req.responseText + ')');
		                    //alert(req.responseText);
		                    numOfCategories = categoriesJason.length;
		                }
		                else {
		                    storage.innerHTML = "page not found";
		                }
		            }
		            else {
		                //alert("Error: returned status code " + req.status + " " + req.statusText);
		                storage.innerHTML = "Error: returned status code " + req.status + " " + req.statusText;
		            }
		        }
		    }
		    var specificUrl = getDataFromServerURL + "?version=1&AppId=1";
		    req.open("GET", specificUrl, false);
		  //  alert(specificUrl); 
		    req.send(null);
		}

		function buildCategoriesTable() {
		    var index = 0;
		    var categoriesTableTr;
		    var categoriesTableTd;
		    var categoriesTable = document.getElementById("categoriesTable");
		    //alert(categoriesTableTd)
			console.log("numOfCategories:"+numOfCategories)
		    for (i = 0; i < numOfCategories; i++) {
		        if (index == 0) {
		            categoriesTableTr = document.createElement("tr");
		        }
		        
		        categoriesTableTd = document.createElement("td");
		        categoriesTableTd.align = "center";
		        console.log("before start of " + i + " iter"); 
		            
				for (j = 0; j < numOfCategories; j++) {
					console.log("start of " + i + " iter"); 
		            if (categoriesOrder[i] == categoriesJason[j].id) {
		                console.log(categoriesJason[j].icon)
		                categoriesTableTd.innerHTML = "<a href='<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/CategoryList.aspx?categoryId=" + categoriesJason[j].id + "'>" +
                                                    "<img id='iconNumber" + i + "' alt='' src='<%=ConfigurationSettings.AppSettings["iconsUrl"].ToString()%>" + categoriesJason[j].icon + "' style='padding:20px;' />";
						console.log("end of " + i + " iter"); 
						break;
					}

		        }

		        categoriesTableTr.appendChild(categoriesTableTd);
		        categoriesTable.appendChild(categoriesTableTr);

		        index++;
		        if (index == NUM_OF_ICONS_PER_LINE) {
		            index = 0;
		        }
		    }
		}

       function UpdateIconSizes() {
           var t = new Image();
           var ratio;
           for (ind = 0; ind < numOfCategories; ind++) {
               img_element = document.getElementById("iconNumber" + ind);
               t.src = (img_element.getAttribute ? img_element.getAttribute("src") : false) || img_element.src;
               ratio = (t.height / t.width);
               img_element.style.height = '100px';
               newWidth = 100 / ratio;
               img_element.style.width = newWidth;
           }
       }
        </script>

        <div id="box">
            <div id="header">
                <table>
                    <tr>
                        <td style="width:450px;">
                            <img id="mainIcon" alt="" src="images/site/jokes.png" align="left" />
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
                                </td>
                            </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="CategoriesDiv">
<%--                <asp:Label id="categoriesTable" runat="server"></asp:Label>--%>
                <table id="categoriesTable"></table>
            </div>

            <div class="MainPageFooterDiv">
                <table id="footerMain">
                    <tr>
                        <td>
                            <a href="<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/JokePage.aspx?type=rand" >
                                <img class="footerIcon" alt="" src="images/site/random_joke.PNG"/>
                            </a>
                        </td>
                        <td>
                            <a href="<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/AddJokePage.aspx" >
                                <img class="footerIcon" alt="" src="images/site/add_joke.PNG"/>
                            </a>
                        </td>
                        <td>
                            <a href="<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/CategoryList.aspx" >
                                <img class="footerIcon" alt="" src="images/site/first_ten.PNG"/>
                            </a
                        </td>
                    </tr>
                </table>
            </div>
        </div>
