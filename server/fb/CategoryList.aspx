<%@ Page Language="C#" AutoEventWireup="true" CodeFile="CategoryList.aspx.cs" Inherits="MyWebForm1" %>

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="en">
    <head>
        <link rel="stylesheet" type="text/css" href="style.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <script src="https://code.jquery.com/jquery-latest.js"></script>
        <script src="Scripts/ajax.js" type="text/javascript"></script>
        <title> צחוקים - רשימת בדיחות לקטגוריה</title>

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

        <script>
            var categoryId = "<%=CategoryID%>";
            var jokesListJason;
            var numOfJokes;
            var isSortedByRating = false;
            var NUM_OF_CHARS_TO_SEE_IN_JOKE_LIST = 100;

            var rootURL = "<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/";
            var getJokesUrl = rootURL + "RetrieveFromServer.aspx";

            $(document).ready(
		    function () {
		        if (categoryId != "") {
		            // Specific category list
		            getDataFromServer("specific_list");

		            // If cookie exist and is true it means that the sorting is by rating
		            //isSortedByRating = isCookieExist("sort", "true");
		            isSortedByRating = isCookieExist("sort");
		            //alert("isSortedByRating=" + isSortedByRating);

		            // If cookie exist then sort by rating high to low
		            if (isSortedByRating) {
		                //jokesListJason.sort(sort_by('rating', true, parseInt));
		                jokesListJason.sort(sort_by('StarRating', 'numOfVotes', true, parseInt));
		            }

		            UpdateCategoryIconSize();
		        }
		        else {
		            // Highlight category list
		            getDataFromServer("highlight_list");

		            // Hide the sorting icon
		            sortIconElement = document.getElementById("sortIcon");
		            sortIconElement.style.visibility = "hidden";
		        }

		        buildListOfJokesTable();

		        $("#sortIcon").click(function () {
		            clearJokesTable();

		            if (isSortedByRating == false) {
		                // Was sorted by chronology and now should be sorted by rating
		                isSortedByRating = true;
		                // Sort by rating high to low
		                //jokesListJason.sort(sort_by('rating', true, parseInt));
		                jokesListJason.sort(sort_by('StarRating', 'numOfVotes', true, parseInt));
		            }
		            else {
		                // Was sorted by rating and now should be sorted by chronology
		                isSortedByRating = false;
		                // Sort by id high to low
		                jokesListJason.sort(sort_by('id', 'id', true, parseInt));
		            }
		            //alert("isSortedByRating=" + isSortedByRating);

		            buildListOfJokesTable();

		            // Set cookie to remember configuration
		            setCookie("sort", isSortedByRating, 365);
		        })
		    })

		    function getDataFromServer(type) {
		        var req = createXHR();
		        req.onreadystatechange = function () {
		            if (req.readyState == 4) {
		                if (req.status == 200) {
		                    if (req.status != 404) {
		                        jokesListJason = eval('(' + req.responseText + ')');
		                        //alert(req.responseText);
		                        numOfJokes = jokesListJason.length;

                                /*
		                        for (i = 0; i < numOfJokes; i++) {
		                            alert("jokesListJason[" + i + "]=" +   jokesListJason[i].id + " " +
		                            jokesListJason[i].headline + " " +
		                            jokesListJason[i].joke + " " +
                                    jokesListJason[i].pic + " " +
                                    jokesListJason[i].video + " " +
                                    jokesListJason[i].categoryId + " " +
                                    jokesListJason[i].rating + " " +
                                    jokesListJason[i].status + " " +
                                    jokesListJason[i].UserName + " " +
                                    jokesListJason[i].UserEmail + " " +
                                    jokesListJason[i].StarRating + " " +
                                    jokesListJason[i].numOfVotes);
		                        }
                                */
		                    }
		                    else {
		                        storage.innerHTML = "page not found";
		                    }
		                }
		                else {
		                    storage.innerHTML = "Error: returned status code " + req.status + " " + req.statusText;
		                }
		            }
		        }
		        var specificUrl = getJokesUrl + "?type=" + type + "&CategoryId=" + categoryId;
		        req.open("GET", specificUrl, false);
		        //alert(specificUrl); 
		        req.send(null);
		    }

		    function buildListOfJokesTable() {
            	var jokesTableTr;
            	var jokesTableTd;
                var jokesInnerTable;
                var jokesInnerTableTr;
                var jokesInnerTableHeaderTd;
                var jokesInnerTableHeaderStringLabel;
                var jokesInnerTableLikeIconTd;
                var jokesInnerTableRateStringTd;
                var jokesInnerTableRateStringLabel;
                var jokesInnerTableStartRatingTd;
                var jokesTableJokeTextDiv;
		        var jokesTable = document.getElementById("listOfJokesTable");
		        var jokeInitial;
		        var jokeReference;

                for (i = 0; i < numOfJokes; i++) {
                    jokeInitial = CreateInitialJokeText(jokesListJason[i].joke);
                    if (categoryId != "") {
                        jokeReference = "<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/JokePage.aspx?type=specific&categoryId=" + categoryId + "&JokeId=" + jokesListJason[i].id;
                    }
                    else {
                        jokeReference = "<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/JokePage.aspx?type=highlight&JokeId=" + jokesListJason[i].id;
                    } 

                    //Create all elements for joke number i
                    jokesTableTr = document.createElement("tr");

                    jokesTableTd = document.createElement("td");
                    jokesTableTd.className = 'jokeRowInList';
                    jokesTableTd.setAttribute("align", "right");
                    jokesTableTd.setAttribute("dir", "rtl");

                    jokesInnerTable = document.createElement("table");
                    //jokesInnerTable.cellspacing = '5px';
                    jokesInnerTable.setAttribute("cellpadding", "3px");

                    jokesInnerTableTr = document.createElement("tr");

                    jokesInnerTableHeaderTd = document.createElement("td");

                    jokesInnerTableHeaderStringLabel = document.createElement("label");
                    jokesInnerTableHeaderStringLabel.innerHTML = "<a class='headerJokeInList' href='" + jokeReference + "'>" + jokesListJason[i].headline;

                    // TODO:: OPEN FOR STAR RATING
                    if (jokesListJason[i].StarRating > 5)
                        jokesListJason[i].StarRating = 5;
                    jokesInnerTableStartRatingTd = document.createElement("td");
                    jokesInnerTableStartRatingTd.setAttribute("dir", "ltr");
                    jokesInnerTableStartRatingTd.innerHTML =    //"<div id='startRater' class='stat'>" +
                                                                //"<label for='rating'>Rating</label>" +
		                                                        "<div class='statVal'>" +
			                                                    "<span class='ui-rater'>" +
				                                                "<span class='ui-rater-starsOff' style='width:90px;'>" +
                                                                "<span class='ui-rater-starsOn' style='width:" + 18*jokesListJason[i].StarRating + "px'></span></span>" +
                                                                "<span class='ui-rater-rating'>" + Math.round(jokesListJason[i].StarRating*100)/100 + "</span>" +
                                                                "&#160;(<span class='ui-rater-rateCount'>" + jokesListJason[i].numOfVotes + "</span>)" +
			                                                    "</span></div></div>";

                    // TODO:: ERASE WHEN STAR RATING IS READY
                    /*
                    jokesInnerTableLikeIconTd = document.createElement("td");
                    jokesInnerTableLikeIconTd.innerHTML = "<img id='LikeIconInList' src=images/site/Thumb-Up-icon.png>";
                    jokesInnerTableRateStringTd = document.createElement("td");
                    */
                    // TODO:: ERASE WHEN STAR RATING IS READY
                    /*
                    jokesInnerTableRateStringLabel = document.createElement("label");
                    jokesInnerTableRateStringLabel.id = 'rateString';
                    jokesInnerTableRateStringLabel.innerHTML = jokesListJason[i].rating;
                    */

                    jokesTableJokeTextDiv = document.createElement("div");
                    jokesTableJokeTextDiv.innerHTML = "<a class='jokeTextInList' href='" + jokeReference + "'>" + jokeInitial;

                    // Construct the tree of all elements of joke i
                    jokesInnerTableHeaderTd.appendChild(jokesInnerTableHeaderStringLabel);
                    // TODO:: ERASE WHEN STAR RATING IS READY
                    //jokesInnerTableRateStringTd.appendChild(jokesInnerTableRateStringLabel);

                    jokesInnerTableTr.appendChild(jokesInnerTableHeaderTd);
                    // TODO:: ERASE WHEN STAR RATING IS READY
                    //jokesInnerTableTr.appendChild(jokesInnerTableLikeIconTd);
                    //jokesInnerTableTr.appendChild(jokesInnerTableRateStringTd);

                    // TODO:: OPEN FOR STAR RATING
                    jokesInnerTableTr.appendChild(jokesInnerTableStartRatingTd);

                    jokesInnerTable.appendChild(jokesInnerTableTr);

                    jokesTableTd.appendChild(jokesInnerTable);
                    jokesTableTd.appendChild(jokesTableJokeTextDiv);

                    jokesTableTr.appendChild(jokesTableTd);

                    jokesTable.appendChild(jokesTableTr);
                }
            }

            function CreateInitialJokeText(a_jokeText) {
                var jokeInitial;

                if (a_jokeText.length > NUM_OF_CHARS_TO_SEE_IN_JOKE_LIST)
                {
                    var index = a_jokeText.indexOf(" ", NUM_OF_CHARS_TO_SEE_IN_JOKE_LIST);
                    if ((a_jokeText.length > index) && (index != -1))
                    {
                        jokeInitial = a_jokeText.substr(0, index);
                        jokeInitial += "...";
                    }
                    else
                    {
                        jokeInitial = a_jokeText;
                    }
                }
                else
                {
                    jokeInitial = a_jokeText;
                }

                return jokeInitial;
            }

            function UpdateCategoryIconSize() {
                var t = new Image();
                //var ratio;
                img_element = document.getElementById("categoryPageIcon");
                t.src = (img_element.getAttribute ? img_element.getAttribute("src") : false) || img_element.src;
                //ratio = (t.height / t.width);
                img_element.style.height = '100px';
                newWidth = (100 / (t.height / t.width));
                img_element.style.width = newWidth + "px";
                img_element.style.padding = '20px 150px 20px 40px';
            }

            function clearJokesTable() {
                var jokesTable = document.getElementById("listOfJokesTable");
                while (jokesTable.childNodes.length >= 1) {
                    jokesTable.removeChild(jokesTable.firstChild);
                }
            }

            // Here's a more flexible version, which allows you to create 
            // reusable sort functions, and sort by any field
            var sort_by = function(field1, field2, reverse, primer){

                var key1 = function (x) { return primer ? primer(x[field1]) : x[field1] };
                var key2 = function (x) { return primer ? primer(x[field2]) : x[field2] };

                return function (a,b) {
                    var A = key1(a), B = key1(b);
                    var C = key2(a), D = key2(b);
                    //return (A < B ? -1 : (A > B ? 1 : 0)) * [1,-1][+!!reverse];                  
                    return (A < B ? -1 : (A > B ? 1 : (C < D ? -1 : (C > D ? 1 : 0)))) * [1, -1][+!!reverse];                  
               }
            }

            function setCookie(c_name, value, exdays) {
                var exdate = new Date();
                exdate.setDate(exdate.getDate() + exdays);
                var c_value = escape(value) + ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
                document.cookie = c_name + "=" + c_value;
            }

            function isCookieExist(c_name) {
                var i, x, y, ARRcookies = document.cookie.split(";");
                for (i = 0; i < ARRcookies.length; i++) {
                    x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
                    y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
                    x = x.replace(/^\s+|\s+$/g, "");
                    if (x == c_name) {
                        return unescape(y);
                    }
                }
                return false;
            }

            /*
            function isCookieExist(c_name, value) {
                var i, x, y, ARRcookies = document.cookie.split(";");
                for (i = 0; i < ARRcookies.length; i++) {
                    x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
                    y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
                    x = x.replace(/^\s+|\s+$/g, "");
                    if ((x == c_name) && (y == value)) {
                        return true;
                    }
                }
                return false;
            }
            */
        </script>

        <div id="box">
            <div id="header">
                <table>
                    <tr>
                        <td style="width:450px;">
                            <asp:Label id="categoryIcon" runat="server"></asp:Label>              
                        </td>
                        <td style="width:200px;">
                            <table cellspacing="10px" >
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
                                    <a href="<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/CategoriesPage.aspx" >
                                        <img id="homeIcon" alt="" title="חזור לעמוד הראשי" src="images/site/home.png" align="right" />
                                    </a>
                                    <img id="sortIcon" alt="" title="מיין לפי סדר כרונולוגי/פופולאריות" src="images/site/sort-by-icon.png" align="right" />
                                </td>
                            </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
<%--            <asp:Label id="jokes" runat="server"></asp:Label>--%>
            <div id='listOfJokesDiv'>
                <table id='listOfJokesTable' align='right' cellpadding='10px' style='width:100%;'></table>
            </div>
        </div>
    </body>
</html>