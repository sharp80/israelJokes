<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Admin.aspx.cs" Inherits="Admin" validateRequest="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <title></title>
</head>
<body>
    <script type="text/javascript">
        var numOfFields = "<%=NumOfFields%>";
        function deleteButtonClicked() {
            tableType = document.getElementById("tableType").value;
            if (tableType == "jokes") {
                deleteButtonClickedForJokes();
            }
            else if (tableType == "messages") {
                deleteButtonClickedForMessages();
            }
            else {
                alert("Wrong table type: " + tableType);
            }
        }

        function deleteButtonClickedForJokes() {
            var sqlString = "";
            var jokesString = "";
            //alert("deleteButtonClicked, NumOfFields = " + numOfFields);
            for (i = 0; i < numOfFields; i++) {
                if (document.getElementById("checkboxRow" + i).checked == true) {
                    //alert("Row number " + i + " is checked");
                    id = document.getElementById("jokeIdField" + i).value;
                    jokeHeader = document.getElementById("jokeHeader" + i).value;
                    sqlString += "DELETE FROM jokes WHERE id=" + id + ";";
                    jokesString += "Joke ID: " + id + "  Joke: " + jokeHeader + "\n";
                }
            }
            if (sqlString != "") {
                var r = confirm("Do you really want to delete these jokes:\n" + jokesString);
                if (r == true) {
                    document.getElementById("sqlString").value = sqlString;
                }
                else {
                    document.getElementById("sqlString").value = "ABORT";
                    document.getElementById("numOfFields").value = numOfFields;
                }
            }
            else {
                document.getElementById("sqlString").value = "ABORT";
                document.getElementById("numOfFields").value = numOfFields;
            }
        }

        function deleteButtonClickedForMessages() {
            var sqlString = "";
            var messagesString = "";
            //alert("deleteButtonClicked, NumOfFields = " + numOfFields);
            for (i = 0; i < numOfFields; i++) {
                if (document.getElementById("checkboxRow" + i).checked == true) {
                    messageId = document.getElementById("messageIdField" + i).value;
                    jokeId = document.getElementById("jokeIdField" + i).value;
                    messageText = document.getElementById("messageText" + i).value;
                    sqlString += "DELETE FROM messages WHERE messageId=" + messageId + ";";
                    messagesString += "Message ID: " + messageId + " Joke ID: " + jokeId + " Message Text: " + messageText + "\n";
                }
            }
            //alert("sqlString=" + sqlString);
            if (sqlString != "") {
                var r = confirm("Do you really want to delete these messages:\n" + messagesString);
                if (r == true) {
                    document.getElementById("sqlString").value = sqlString;
                }
                else {
                    document.getElementById("sqlString").value = "ABORT";
                    document.getElementById("numOfFields").value = numOfFields;
                }
            }
            else {
                document.getElementById("sqlString").value = "ABORT";
                document.getElementById("numOfFields").value = numOfFields;
            }
        }


        function updateButtonClicked() 
		{
            var sqlString = "";
            var jokesString = "";
            //alert("updateButtonClicked, NumOfFields = " + numOfFields);
            for (i = 0; i < numOfFields; i++) {
                if (document.getElementById("checkboxRow" + i).checked == true) {
                    //alert("Row number " + i + " is checked");
                    id = document.getElementById("jokeIdField" + i).value;
                    jokeHeader = document.getElementById("jokeHeader" + i).value;
                    jokeText = document.getElementById("jokeText" + i).value;
					jokePic = document.getElementById("picName" + i).value;
                    jokeCategoryId = document.getElementById("categoryIdInput" + i).value;
                    jokeRating = document.getElementById("ratingInput" + i).value;
                    jokeStatus = document.getElementById("jokeStatus" + i).value;
                    userName = document.getElementById("userName" + i).value;
                    userEmail = document.getElementById("userEmail" + i).value;
                    sqlString += "UPDATE jokes SET headline='" + encodeStr(jokeHeader) + "', joke='" + encodeStr(jokeText) + "', pic='" + encodeStr(jokePic) + "', categoryId=" + jokeCategoryId + ", rating=" + jokeRating + ", status='" + jokeStatus + "', UserEmail='" + userEmail + "', userName='" + userName + "' WHERE id=" + id + ";";
                    jokesString += "Joke ID: " + id + "  Joke: " + jokeHeader + "\n";
                }
            }
            if (sqlString != "") {
                var r = confirm("Do you really want to update these jokes:\n" + jokesString);
                if (r == true) {
                    document.getElementById("sqlString").value = sqlString;
                }
                else {
                    document.getElementById("sqlString").value = "ABORT";
                    document.getElementById("numOfFields").value = numOfFields;
                }
            }
            else {
                document.getElementById("sqlString").value = "ABORT";
                document.getElementById("numOfFields").value = numOfFields;
            }
        } 
		
		function addButtonClicked() 
		{
			var sqlString = "insert into jokes (headline, joke, pic, video, categoryId, status, UserEmail, userName) values ";
            var jokesString = "";
            //alert("addButtonClicked, NumOfFields = " + numOfFields);
            for (i = 0; i < numOfFields; i++) 
			{
                if (document.getElementById("checkboxRow" + i).checked == true) 
				{
                    //alert("Row number " + i + " is checked");
                    var id = document.getElementById("jokeIdField" + i).value;
                    var jokeHeader = document.getElementById("jokeHeader" + i).value;
                    var jokeText = document.getElementById("jokeText" + i).value;
					var jokePic = document.getElementById("picName" + i).value;
					var jokeVideo = document.getElementById("videoUrl" + i).value;
					var jokeCategoryId = document.getElementById("categoryIdInput" + i).value;
					if (jokeCategoryId == '')
					{
						//alert("jokeCategoryId is empty");
						jokeCategoryId = 1;
					}
                    //jokeRating = document.getElementById("ratingInput" + i).value;
                    var jokeStatus = document.getElementById("jokeStatus" + i).value;
                    var userName = document.getElementById("userName" + i).value;
                    var userEmail = document.getElementById("userEmail" + i).value;
					
					// If there are more then 1 jokes to add, put a comma.
					if(jokesString != "")
						sqlString += ",";
				
					sqlString += 	"('" + 
									encodeStr(jokeHeader) + "','" +
									encodeStr(jokeText) + "','" +
									encodeStr(jokePic) + "','" +
									encodeStr(jokeVideo) + "'," +
									jokeCategoryId + ",'" +
									jokeStatus + "','" +
									userEmail + "','" +
									userName + 
									"')";
											
                    jokesString += "Joke ID: " + id + "  Joke: " + jokeHeader + "\n";
                }
            }
			sqlString += ";";
			
			//alert("sqlString = " + sqlString);
            if (jokesString != "") {
                var r = confirm("Do you really want to add these jokes:\n" + jokesString);
                if (r == true) {
                    document.getElementById("sqlString").value = sqlString;
                }
                else {
                    document.getElementById("sqlString").value = "ABORT";
                    document.getElementById("numOfFields").value = numOfFields;
                }
            }
            else {
                document.getElementById("sqlString").value = "ABORT";
                document.getElementById("numOfFields").value = numOfFields;
            }
		}
		
		
		function deleteDlJokes() 
		{
            var sqlString = "";
            var jokesString = "";
			var mydate= new Date()
			mydate.setDate(mydate.getDate()-1)
			sqlString += "DELETE FROM jokesdl WHERE DateAdded < " + mydate.getFullYear() + "-" +  (mydate.getMonth() + 1) + "-" +  mydate.getDate();
			var r = confirm("Do you really want to delete jokesDl?\n" + sqlString );
			if (r == true) {
				document.getElementById("sqlString").value = sqlString;
			}
			else 
			{
				document.getElementById("sqlString").value = "ABORT";
				document.getElementById("numOfFields").value = 0;
			}
        }
		
		function deleteReadJokes()
		{
            var sqlString = "";
            var jokesString = "";
			var mydate= new Date()
			mydate.setDate(mydate.getDate()-3)
			sqlString += "DELETE FROM ReadJokes WHERE AppId=2 and DateAdded < " + mydate.getFullYear() + "-" +  (mydate.getMonth() + 1) + "-" +  mydate.getDate();
			var r = confirm("Do you really want to delete jokesDl?\n" + sqlString );
			if (r == true) {
				document.getElementById("sqlString").value = sqlString;
			}
			else 
			{
				document.getElementById("sqlString").value = "ABORT";
				document.getElementById("numOfFields").value = 0;
			}
        }

        //+-------------------------------------------------------------
        //| unquoteXml removes the quote marks, which is useful when
        //| the returned stuff wants to be parsed as xml.
        //+-------------------------------------------------------------
        function decodeStr(xmlStr) {
            var result = new EditableString(xmlStr);
            result = result.replaceAll("&lt;", "<");
            result = result.replaceAll("&gt;", ">");
            result = result.replaceAll("&quot;", "\"");
            result = result.replaceAll("&apos;", "'");
            result = result.replaceAll("&#39;", "'");
            //result = result.replaceAll("\r\n", "<p></p>");
            return result.data;
        }

        function encodeStr(xmlStr) {
            var result = new EditableString(xmlStr);
            result = result.replaceAll("<", "&lt;");
            result = result.replaceAll(">", "&gt;");
            result = result.replaceAll("\"", "&quot;");
            //result = result.replaceAll("'", "&apos;"); 
            result = result.replaceAll("'", "&#39;");
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

    </script>

    <form id="Form1" runat="server">
        <table border='2' cellspacing="10" bgcolor="LightSkyBlue">
        <tr>
            <td>
                Joke ID
                <asp:TextBox id="JokeId" runat="server" style="width:50px"></asp:TextBox>
            </td>
            <td>
                Category
                <asp:listbox id="JokeCategory" runat="server" Rows="1" DataTextField="name" DataValueField="id" ></asp:listbox>
            </td>
            <td>
                Status
                <asp:listbox id="JokeStatus" runat="server" Rows="1" DataTextField="name" DataValueField="id" >
                    <asp:listitem>ALL</asp:listitem >
                    <asp:listitem>ACTIVE</asp:listitem >
                    <asp:listitem>PENDING</asp:listitem >
                </asp:listbox>
            </td>
            <td>
                <asp:Button id="SubmitButton" runat="server" Text="Submit" Width="110px" onclick="SubmitButton_Click" ></asp:Button>
            </td>
            <td>
                <asp:Button id="MessageButton" runat="server" Text="Display All Messages" onclick="MessagesButton_Click" ></asp:Button>
            </td>
			<td>
                <asp:Button id="AddJokeButton" runat="server" Text="Add New Jokes" onclick="AddJokesButton_Click" ></asp:Button>
            </td>
			<td>
                <asp:Button id="AddButton" runat="server" Text="Add" OnClientClick="addButtonClicked()" onclick="ReloadPage" ></asp:Button>
            </td>
            <td>
                <asp:Button id="UpdateButton" runat="server" Text="Update" OnClientClick="updateButtonClicked()" onclick="ReloadPage" ></asp:Button>
            </td>
		    <td>
                <asp:Button id="DeleteButton" runat="server" Text="Delete" OnClientClick="deleteButtonClicked()" onclick="ReloadPage"></asp:Button>
            </td>
			<td>
                <asp:Button id="ClearButton" runat="server" Text="Clear All" onclick="ClearButton_Click" ></asp:Button>
            </td>
			<td>
                <asp:Button id="DeleteDlJokesButton" runat="server" Text="Delete JokesDl" OnClientClick="deleteDlJokes()" onclick="ReloadPage" ></asp:Button>
            </td>
			<td>
                <asp:Button id="DeleteReadJokesButton" runat="server" Text="Delete JokesRead" OnClientClick="deleteReadJokes()" onclick="ReloadPage" ></asp:Button>
            </td>		
        </tr>
        </table>
        <asp:Label id="numberOfMessages" runat="server" Width="100%" style="color:red;text-align:left;direction:ltr;" ></asp:Label>
        <br />
        <asp:Label id="messages" runat="server" Width="100%" ></asp:Label>
        <br />
        <asp:Label id="numberOfJokes" runat="server" Width="100%" Height="30px" style="color:red;text-align:left;direction:ltr;" ></asp:Label>
        <br />
        <asp:Label id="jokes" runat="server" Width="100%" ></asp:Label>
        <asp:Label id="debug" runat="server" Width="100%" Height="30px" ></asp:Label>
        <input id="sqlString" type="hidden" runat="server" />
        <input id="numOfFields" type="hidden" runat="server" />
        <input id="tableType" type="hidden" runat="server" />
    </form>
</body>
</html>
