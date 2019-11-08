<%@ Page Language="C#" AutoEventWireup="true" CodeFile="JokePage.aspx.cs" Inherits="MyWebForm1" %>

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="en">
    <head>
        <link rel="stylesheet" type="text/css" href="style.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <script src="https://code.jquery.com/jquery-latest.js"></script>
        <script src="Scripts/ajax.js" type="text/javascript"></script>
        <script type="text/javascript" src="Scripts/jquery.rater.js"></script>
        <title> צחוקים - עמוד בדיחה</title>

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
                    markJokeRead();
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
            var type = "<%=Type%>";
            var jokeId = "<%=JokeID%>";
            var categoryId = "<%=CategoryID%>";
            var rate = "<%=Rate%>";
            var jokeText = "<%=JokeText%>";
            var jokePic = "<%=JokePic%>";
            var jokeVideo = "<%=JokeVideo%>";
            var jokeHeader = "<%=JokeHeader%>";
            var starRating = "<%=StarRating%>";
            var numOfVotes = "<%=NumOfVotes%>";

            var rootURL = "<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/";
            var uTubePrefixEmbed = "https://www.youtube-nocookie.com/embed/";
            //var uTubePrefixShare = "https://youtu.be/";
            var uTubePrefixShare = "https://www.youtube.com/v/";
            var uTubePrefixShareImg = "https://img.youtube.com/vi/";
            var uTubeOptions = "?rel=0&autoplay=1";
            var getDataFromServerURL = rootURL + "RetrieveFromServer.aspx";

            var jokeParams = new Array(4);
            var JOKE_ID_POS = 0;
            var CATEGORY_ID_POS = 1;
            var JOKE_HEADER_POS = 2;
            var JOKE_TEXT_POS = 3;
            var JOKE_RATE_POS = 4;
            var JOKE_PIC_POS = 5;
            var JOKE_VIDEO_POS = 6;
            var JOKE_STAR_RATING_POS = 7;
            var JOKE_NUM_OF_VOTES_POS = 8;

            var MAX_NUM_OF_CHARS_FOR_SMALL_IMG = 10;
            var MAX_NUM_OF_CHARS_FOR_MEDIUM_IMG = 20;
            var MAX_NUM_OF_CHARS_FOR_BIG_IMG = 30;

            var isLikeClicked = false;
            var isShareClicked = false;
            var isRightArrowEnable = true;
            var isLeftArrowEnable = true;
            var isSortedByRating;

            $(document).ready(
		    function () {
		        if (type == "specific") {
		            UpdateCategoryIconSize();
		        }

                isSortedByRating = isCookieExist("sort", "true");

		        getDataFromServer(type);
		        updateCurrentJoke();
		        //updateLikeIcon();
		        updateShareIcon();
		        updateArrowIcons();
                

		        $("#leftArrow").mousedown(function () {
		            if (isLeftArrowEnable) {
		                leftArrowIconElement = document.getElementById("leftArrow");
		                leftArrowIconElement.src = "images/site/left_arrow_dark.png";
		            }
		        })
		        $("#leftArrow").mouseup(function () {
		            if (isLeftArrowEnable) {
		                leftArrowIconElement = document.getElementById("leftArrow");
		                leftArrowIconElement.src = "images/site/left_arrow.png";
		            }
		        })
		        $("#leftArrow").click(function () {
		            if (isLeftArrowEnable) {
		                if (type == "rand") {
		                    getDataFromServer("rand");
		                }
		                else if (type == "specific") {
                            if (isSortedByRating == false) {
		                        getDataFromServer("next");
                            }
                            else {
                                getDataFromServer("next_rated");
                            }

		                }
		                else {
		                    getDataFromServer("highlight_next");
		                }
		                updateCurrentJoke();
		                //updateLikeIcon();
		                updateShareIcon();
		                updateArrowIcons();
		                markJokeRead();
		            }
		        })


		        $("#rightArrow").mousedown(function () {
		            if (isRightArrowEnable) {
		                rightArrowIconElement = document.getElementById("rightArrow");
		                rightArrowIconElement.src = "images/site/right_arrow_dark.png";
		            }
		        })
		        $("#rightArrow").mouseup(function () {
		            if (isRightArrowEnable) {
		                rightArrowIconElement = document.getElementById("rightArrow");
		                rightArrowIconElement.src = "images/site/right_arrow.png";
		            }
		        })
		        $("#rightArrow").click(function () {
		            if (isRightArrowEnable) {
		                if (type == "specific") {
                            if (isSortedByRating == false) {
		                        getDataFromServer("prev");
                            }
                            else {
                                getDataFromServer("prev_rated");
                            }
		                } else if (type == "highlight") {
		                    getDataFromServer("highlight_prev");
		                }
		                updateCurrentJoke();
		                //updateLikeIcon();
		                updateShareIcon();
		                updateArrowIcons();
		                markJokeRead();
		            }
		        })

                /*
		        $("#likeIcon").click(function () {
		            if (isLikeClicked == false) {
		                $.post(rootURL + "AddLike.aspx?jokeId=" + jokeId);

		                likeIconElement = document.getElementById("likeIcon");
		                likeIconElement.src = "images/site/add_like2.png";
		                likeIconElement.title = "אהבתי את הבדיחה";
		                isLikeClicked = true;
		                //setCookie("like", jokeId);
                        setCookie(jokeId, "liked");

                        // Update rate of a joke
                        isToAddOne = true;
                        updateNumberOfLikes(isToAddOne);
		            }
		        })
                */

		        $("#shareIcon").click(function () {
		            if (isShareClicked == false) {
		                postonwall();

		                shareIconElement = document.getElementById("shareIcon");
		                shareIconElement.src = "images/site/share_icon_done.png";
		                shareIconElement.title = "הבדיחה שותפה עם חברים";
		                isShareClicked = true;
		            }

		        })
		    })

            /*
            function updateLikeIcon() {
                likeIconElement = document.getElementById("likeIcon");
                if (isCookieExist(jokeId, "liked")) {
                    likeIconElement.src = "images/site/add_like2.png";
                    likeIconElement.title = "אהבתי את הבדיחה";
                    isLikeClicked = true;
                }
                else {
                    likeIconElement.src = "images/site/Thumb-Up-icon.png";
                    likeIconElement.title = "עשה לייק לבדיחה";
                    isLikeClicked = false;
                }

                // Update rate of a joke
                isToAddOne = false;
                updateNumberOfLikes(isToAddOne);
            }
            */

            function updateShareIcon() {
                shareIconElement = document.getElementById("shareIcon");
                shareIconElement.src = "images/site/share-icon.png";
                shareIconElement.title = "שתף חברים בבדיחה";
                isShareClicked = false;
            }

            function updateArrowIcons() {
                rightArrowIconElement = document.getElementById("rightArrow");
                leftArrowIconElement = document.getElementById("leftArrow");

                // If we are in random mode so we always disable the prev arrow and always enable the next arrow
                if (type == "rand") {
                    rightArrowIconElement.src = "images/site/right_arrow_gray.png";
                    rightArrowIconElement.style.cursor='no-drop';
                    isRightArrowEnable = false;                  
                    leftArrowIconElement.src = "images/site/left_arrow.png";
                    leftArrowIconElement.style.cursor='pointer';
                    isLeftArrowEnable = true;
                }
                // If we are in specific mode so we check if there are more jokes before and after the current joke
                else {
                    var prevType;
                    var nextType;

                    if (type == "specific") {
                        if (isSortedByRating == false) {
                            prevType = "prev";
                            nextType = "next";
                        }
                        else {
                            prevType = "prev_rated";
                            nextType = "next_rated";                           
                        }
                    }
                    else if (type == "highlight") {
                        prevType = "highlight_prev";
                        nextType = "highlight_next";
                    }

                    getDataFromServer(prevType);
                    if (jokeParams[JOKE_ID_POS] == "") {
                        rightArrowIconElement.src = "images/site/right_arrow_gray.png";
                        rightArrowIconElement.style.cursor='no-drop';
                        isRightArrowEnable = false;      
                    }
                    else {
                        rightArrowIconElement.src = "images/site/right_arrow.png";
                        rightArrowIconElement.style.cursor='pointer';
                        isRightArrowEnable = true;
                    }

                    getDataFromServer(nextType);
                    if (jokeParams[JOKE_ID_POS] == "") {
                        leftArrowIconElement.src = "images/site/left_arrow_gray.png";
                        leftArrowIconElement.style.cursor='no-drop';
                        isLeftArrowEnable = false;

                    }
                    else {
                        leftArrowIconElement.src = "images/site/left_arrow.png";
                        leftArrowIconElement.style.cursor='pointer';
                        isLeftArrowEnable = true;
                    }
                }
            }

            function updateCurrentJoke() {
                jokeId = jokeParams[JOKE_ID_POS];
                categoryId = jokeParams[CATEGORY_ID_POS];
                jokeText = jokeParams[JOKE_TEXT_POS];
                jokePic = jokeParams[JOKE_PIC_POS];
                jokeVideo = jokeParams[JOKE_VIDEO_POS];
                rate = jokeParams[JOKE_RATE_POS];
                jokeHeader = jokeParams[JOKE_HEADER_POS];
                starRating = jokeParams[JOKE_STAR_RATING_POS];
                numOfVotes = jokeParams[JOKE_NUM_OF_VOTES_POS];

                // Update header of a joke
                var headerClassId;
                if (jokeHeader.length < MAX_NUM_OF_CHARS_FOR_SMALL_IMG) {
                    headerClassId = "headerjokeOuterBoxSmall";
                }
                else if (jokeHeader.length < MAX_NUM_OF_CHARS_FOR_MEDIUM_IMG) {
                    headerClassId = "headerjokeOuterBoxMedium";
                }
                else {
                    headerClassId = "headerjokeOuterBoxBig";
                }

                headerElementBox = document.getElementById("jokePage");
                headerElementBox.className = headerClassId;

                newHeaderElement = document.getElementById("jokeHeaderDiv");
                newHeaderElement.textContent = jokeHeader; // For FF
                newHeaderElement.innerText = jokeHeader; // For IE

                // Update text of a joke
                newTextElement = document.getElementById("jokeTextDiv");
                newTextElement.textContent = jokeText; // For FF
                newTextElement.innerText = jokeText; // For IE

                // Update pic of a joke
                newPicElement = document.getElementById("jokePicture");
                if (jokePic != "") {
                    newPicElement.style.visibility = "visible";
                    newPicElement.src = "<%=ConfigurationSettings.AppSettings["imagesUrl"].ToString()%>" + jokePic;
                }
                else {
                    newPicElement.style.visibility = "hidden";
                    newPicElement.src = "";
                    newPicElement.style.width = '0px';
                    newPicElement.style.height = '0px';
                }

                // Update video of a joke
                newVideoElement = document.getElementById("jokeUTubeVideo");
                if (jokeVideo != "") {
                    newVideoElement.src = uTubePrefixEmbed + jokeVideo + uTubeOptions;
                    newVideoElement.style.width = '339px';
                    newVideoElement.style.height = '260px';
                }
                else {
                    newVideoElement.src = "";
                    newVideoElement.style.width = '0px';
                    newVideoElement.style.height = '0px';
                }

                // Update star rating of a joke
                // TODO:: Open for star rating
                if (starRating > 5)
                        starRating = 5;
                starRatingElement = document.getElementById("starRatingDiv");
                starRatingElement.innerHTML = "<div id='testRater' class='stat'>" +
                                              "<div class='statVal'>" +
			                                  "<span class='ui-rater'>" +
				                              "<span class='ui-rater-starsOff' style='width:90px;'>" +
                                              "<span class='ui-rater-starsOn' style='width:" + 18*starRating + "px'></span></span>" +
                                              "<span class='ui-rater-rating'>" + Math.round(starRating*100)/100 + "</span>" +
                                              "&#160;(<span class='ui-rater-rateCount'>" + numOfVotes + "</span>)" +
			                                  "</span></div></div>";                                                                
                
                if (!isCookieExist(jokeId, "liked")) {
                    $(function() {
                        $('#testRater').rater({ postHref: '<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>addLikeStars.aspx', step: '2', jokeId: jokeId });
                    });
                }
            }

            /*
            function updateNumberOfLikes(a_isToAddOne) {
                if (a_isToAddOne == true) {
                    rateInt = parseInt(rate);
                    rateInt = rateInt + 1;   
                    rate = rateInt.toString();
                }

                likeElement = document.getElementById("numOfLikes");
                likeElement.textContent = rate; // For FF
                likeElement.innerText = rate; // For IE
            }
            */

            function setCookie(c_name, value) {
                var c_value = escape(value);
                document.cookie = c_name + "=" + c_value;
            }

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

            function getDataFromServer(type) {
                var req = createXHR();
                req.onreadystatechange = function () {
                    if (req.readyState == 4) {
                        if (req.status == 200) {
                            var doc = req.responseText;
                            if (req.status != 404) {
                                var newJokeId = "";
                                var newJokeCategoryId = "";
                                var newJokeHeader = "";
                                var newJokeText = "";
                                var newJokeRate = "";
                                var newJokePic = "";
                                var newJokeVideo = "";
                                var newJokeStartRating = "";
                                var newJokeNumOfVotes = "";

                                //alert(doc);
                                if (doc != "[]") {
                                    var doc = eval('(' + req.responseText + ')');
                                    var newJokeId = doc[0].id;
                                    var newJokeCategoryId = doc[0].categoryId;
                                    var newJokeHeader = unquoteXml(doc[0].headline);
                                    var newJokeText = unquoteXml(doc[0].joke);
                                    var newJokeRate = doc[0].rating;
                                    var newJokePic = doc[0].pic;
                                    var newJokeVideo = doc[0].video;
                                    var newJokeStartRating = doc[0].StarRating;
                                    var newJokeNumOfVotes = doc[0].numOfVotes;
                                }

                                /*
                                alert("newJokeId=" + newJokeId + "\n" +
                                "newJokeCategoryId=" + newJokeCategoryId + "\n" +
                                "newJokeHeader=" + newJokeHeader + "\n" +
                                "newJokeText=" + newJokeText + "\n" +
                                "newJokeRate=" + newJokeRate + "\n" +
                                "newJokePic=" + newJokePic + "\n" +
                                "newJokeVideo=" + newJokeVideo + "\n" +
                                "newJokeStartRating=" + newJokeStartRating + "\n" +
                                "newJokeNumOfVotes=" + newJokeNumOfVotes );
                                */

                                jokeParams[JOKE_ID_POS] = newJokeId;
                                jokeParams[CATEGORY_ID_POS] = newJokeCategoryId;
                                jokeParams[JOKE_HEADER_POS] = newJokeHeader;
                                jokeParams[JOKE_TEXT_POS] = newJokeText;
                                jokeParams[JOKE_RATE_POS] = newJokeRate;
                                jokeParams[JOKE_PIC_POS] = newJokePic;
                                jokeParams[JOKE_VIDEO_POS] = newJokeVideo;
                                jokeParams[JOKE_STAR_RATING_POS] = newJokeStartRating;
                                jokeParams[JOKE_NUM_OF_VOTES_POS] = newJokeNumOfVotes;
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
                var specificUrl = getDataFromServerURL + "?CategoryId=" + categoryId + "&lastJokeId=" + jokeId + "&type=" + type + "&rate=" + starRating + "&numOfVotes=" + numOfVotes;
                req.open("GET", specificUrl, false);
                //alert(specificUrl); 
                req.send(null);
            }

            //+-------------------------------------------------------------
            //| unquoteXml removes the quote marks, which is useful when
            //| the returned stuff wants to be parsed as xml.
            //+-------------------------------------------------------------
            function unquoteXml(xmlStr) {
                var result = new EditableString(xmlStr);
                result = result.replaceAll("&lt;", "<");
                result = result.replaceAll("&gt;", ">");
                result = result.replaceAll("&quot;", "\"");
                result = result.replaceAll("&apos;", "'");
                result = result.replaceAll("&#39;", "'");
                //result = result.replaceAll("\r\n", "<p></p>");
                return result.data;
            }

            //+-------------------------------------------------------------
            //| Object type editableString is a string that can be edited with
            //| a number of useful methods contained below.
            //+-------------------------------------------------------------
            function EditableString(str) {
                this.data = str;
            }

            //+-------------------------------------------------------------
            //| replaceAll replaces all source strings with destination strings,
            //| returning a new EditableString containing the result.
            //+-------------------------------------------------------------
            EditableString.prototype.replaceAll = function (srcStr, dstStr) {
                this.pat = new RegExp(srcStr, "g");
                var newStr = this.data.replace(this.pat, dstStr);
                return new EditableString(newStr);
            }

            function postonwall() {
                var nameStr = jokeHeader;
                var captionStr = "אפליקציית צחוקים";
                var sourceStr = "";
                var linkStr = "";
                var iconStr = '<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/images/site/jokes_icon2.png';
                var pictureStr = '<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/images/site/jokes_icon2_small.png';

                if (jokePic != "") {
                    pictureStr = '<%=ConfigurationSettings.AppSettings["imagesUrl"].ToString()%>' + jokePic;
                    linkStr = rootURL + "JokePage.aspx?type=specific&categoryId=" + categoryId + "&JokeId=" + jokeId; 
                }
                else if (jokeVideo != "") {
                    sourceStr = uTubePrefixShare + jokeVideo + uTubeOptions;
                    pictureStr = uTubePrefixShareImg + jokeVideo + "/0.jpg";
                    linkStr = rootURL + "JokePage.aspx?type=specific&categoryId=" + categoryId + "&JokeId=" + jokeId; 
                }
                else {
                    sourceStr = "";
                    linkStr = '<%=ConfigurationSettings.AppSettings["fbAppUrl"].ToString()%>';
                    nameStr = "צחוקים";
                    captionStr = "האפליקציה שתפיל אתכם מצחוק!";
                }
/*
				alert(	"message: " + jokeText + "\n" +
						"name: " + nameStr + "\n" +
                        "caption: " + captionStr + "\n" +
                        "picture: " + pictureStr + "\n" +
                        "source: " + sourceStr + "\n" +
                        "link: " + linkStr);
*/
                FB.api('/me/feed', 'post',
                    {
						application: '170936369682906',
                        message: jokeText,
                        name: nameStr,
                        caption: captionStr,
                        picture: pictureStr,
                        source: sourceStr,
                        link: linkStr,
						icon: iconStr,
                        description: 'בצחוקים תמצאו בדיחות, תמונות מצחיקות ועוד הרבה הפתעות. אהבת את הבדיחה? ניתן לעשות לה לייק ולשתף חברים. תמיכה גם לאנדרואיד ולאייפון',
                        actions: [{ name: 'היכנס עכשיו לצחוקים', link: '<%=ConfigurationSettings.AppSettings["fbAppUrl"].ToString()%>' }],
                    }, function (response) {
                        if (!response || response.error) {
                            alert('הבדיחה לא שותפה. אנא וודא שיש לך את כל ההרשאות.');
                        } else {
                            //alert('Post ID: ' + response.id);
                            //alert('הבדיחה שותפה עם חברים!');
                        }
                    });
            }

            function UpdateCategoryIconSize() {
                var t = new Image();
                var ratio;
                img_element = document.getElementById("categoryIconOnJokePage");
                t.src = (img_element.getAttribute ? img_element.getAttribute("src") : false) || img_element.src;
                //ratio = (t.height / t.width);
                img_element.style.height = '100px';
                newWidth = (100 / (t.height / t.width));
                img_element.style.width = newWidth + "px";
                img_element.style.padding = '20px 150px 20px 40px';
            }

            function loadImage() {
                //alert("Image is loaded");
                PicElement = document.getElementById("jokePicture");
                var t = new Image();
                t.src = (PicElement.getAttribute ? PicElement.getAttribute("src") : false) || PicElement.src;
                ratio = (t.height / t.width);
                //alert(  "t.width=" + t.width + " t.height=" + t.height);
                //alert("ratio=" + ratio);

                PicElement.style.width = '400px';
                newHeight = ratio * 400;
                PicElement.style.height = newHeight + "px";
            }

            function markJokeRead() {
                FB.api('/me', function(response) {
                    //alert("response.id=" + response.id + " jokeId=" + jokeId);

                    $.ajax({
                        type: "GET",
                        url: "<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>UpdateJokeRead.aspx?userId=" + response.id + "&jokeId=" + jokeId + "&appId=1",
                        dataType: "html"
                    });
                });
            }
            
        </script>


        <div id="box">
            <div id="header">
                <table>
                    <tr>
                        <td style="width:450px;height:140px;">
                            <asp:Label id="categoryIcon" runat="server"></asp:Label>                  
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
                                    <a href="<%=ConfigurationSettings.AppSettings["siteUrl"].ToString()%>fb/CategoriesPage.aspx" >
                                        <img id="homeIcon" alt="" title="חזור לעמוד הראשי" src="images/site/home.png" align="right" />
                                    </a>
                                    <asp:Label id="FixJokeIcon" runat="server"></asp:Label>
                                </td>
                            </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>

            <div id='jokePage' class='headerjokeOuterBoxBig'>
                <table class='headerjokeInnerBox'>
                    <tr>
                        <td dir='rtl'>
                            <div id='jokeHeaderDiv'>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>

            <div class='jokeOuterBox'>
                <div id='jokeInnerDiv' class='jokeInnerBox'>
                    <div id='jokeTextDiv' dir='rtl'>
                    </div>
                    <div id='jokePicDiv' style="text-align:center;">
                        <img id='jokePicture' alt='' src='' onload="loadImage()" />
                    </div>
                    <div id='jokeVideoDiv' style="text-align:center;">                  
                        <iframe id='jokeUTubeVideo' src="" frameborder="0" allowfullscreen ></iframe>
                    </div>
                </div>
            </div>

            <div id='starRatingDiv' style="text-align:center;"></div>

            <form id="Form1" runat="server">
                <table id="footerJoke">
                    <tr align="center">
                        <td>
                            <img id='leftArrow' alt='' src='images/site/left_arrow.png' />
                        </td>
<%--                       <td>
                            <img id='likeIcon' alt='' src='images/site/Thumb-Up-icon.png' />
                        </td>--%>
                        <td>
                            <img id='shareIcon' title='שתף חברים בבדיחה' alt='' src='images/site/share-icon.png' />
                        </td>
                        <td>
                            <img id='rightArrow' alt='' src='images/site/right_arrow.png' />
                        </td>
                    </tr>
                </table>
            </form>      
<%--            <div id='numOfLikes'></div>--%>
            
        </div>
    </body>
</html>