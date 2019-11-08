<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Admin.aspx.cs" Inherits="Admin" %>

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


        function updateButtonClicked() {
            var sqlString = "";
            var jokesString = "";
            //alert("deleteButtonClicked, NumOfFields = " + numOfFields);
            for (i = 0; i < numOfFields; i++) {
                if (document.getElementById("checkboxRow" + i).checked == true) {
                    //alert("Row number " + i + " is checked");
                    id = document.getElementById("jokeIdField" + i).value;
                    jokeHeader = document.getElementById("jokeHeader" + i).value;
                    jokeCategoryId = document.getElementById("categoryIdInput" + i).value;
                    jokeRating = document.getElementById("ratingInput" + i).value;
                    jokeStatus = document.getElementById("jokeStatus" + i).value;
                    userName = document.getElementById("userName" + i).value;
                    userEmail = document.getElementById("userEmail" + i).value;
                    sqlString += "UPDATE jokes SET categoryId=" + jokeCategoryId + ", rating=" + jokeRating + ", status='" + jokeStatus + "', UserEmail='" + userEmail + "', userName='" + userName + "' WHERE id=" + id + ";";
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
                <asp:Button id="Button1" runat="server" Text="Display All Messages" onclick="MessagesButton_Click" ></asp:Button>
            </td>
            <td>
                <asp:Button id="Button2" runat="server" Text="Clear All" onclick="ClearButton_Click" ></asp:Button>
            </td>
            <td>
                <asp:Button id="Button3" runat="server" Text="Update" OnClientClick="updateButtonClicked()" onclick="ReloadPage" ></asp:Button>
            </td>
            <td>
                <asp:Button id="Button4" runat="server" Text="Delete" OnClientClick="deleteButtonClicked()" onclick="ReloadPage"></asp:Button>
            </td>
        </tr>
        </table>
        <asp:Label id="numberOfMessages" runat="server" Width="100%" style="color:red;text-align:left;direction:ltr;" ></asp:Label>
        <br />
        <asp:Label id="messages" runat="server" Width="100%" Height="30px"></asp:Label>
        <br />
        <asp:Label id="numberOfJokes" runat="server" Width="100%" Height="30px" style="color:red;text-align:left;direction:ltr;" ></asp:Label>
        <br />
        <asp:Label id="jokes" runat="server" Width="100%" Height="30px" ></asp:Label>
        <asp:Label id="debug" runat="server" Width="100%" Height="30px" ></asp:Label>
        <input id="sqlString" type="hidden" runat="server" />
        <input id="numOfFields" type="hidden" runat="server" />
        <input id="tableType" type="hidden" runat="server" />
    </form>
</body>
</html>
