<%@ Page Language="C#" AutoEventWireup="true" CodeFile="index.aspx.cs" Inherits="MyWebForm1" %>

    <!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="en">
    <head>
        <link rel="stylesheet" type="text/css" href="style.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />


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

<%--
        <div style="margin:auto;width:728px;">
            <!-- code from sekindo -->
            <iframe scrolling="no" frameborder="0" width="728" height="90" marginheight="0" marginwidth="0" src="http://live.sekindo.com/live/liveView.php?s=31219&njs=1"></iframe>
            <!-- code from sekindo -->
        </div>
--%>

        <div style="margin:auto;height:600px;width:728px;">
		<iframe src="https://israelijokes.apphb.com/server/fb/CategoriesPage.aspx" frameborder="0" scrolling="no" marginwidth="1px" marginheight="1px" style="height:100%;width:100%"></iframe>
        
		</div>


    </body>
</html>