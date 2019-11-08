using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Web;
using System.Web.SessionState;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.HtmlControls;
using MySql.Data.MySqlClient;

using System.Data.Odbc;
using System.Net;
using System.Text;
using System.IO;


public partial class MyWebForm1 : System.Web.UI.Page
{
    protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; charset=hebrew;";
    protected string okValueReturnFromServer = "Message was successfully insert";
    protected const int MAX_NUM_OF_CHARS_FOR_SMALL_IMG = 10;
    protected const int MAX_NUM_OF_CHARS_FOR_MEDIUM_IMG = 20;
    protected const int MAX_NUM_OF_CHARS_FOR_BIG_IMG = 30;

    private void Page_Load(object sender, System.EventArgs e)
    {
        if (!IsPostBack)
        {
            string jokeId = Request.QueryString["jokeId"];
            string jokeSqlstring = "SELECT * from Jokes where jokes.id =" + jokeId;
            
            MySqlConnection cn = new MySqlConnection(cnString);
            MySqlDataAdapter jokeDataAdapter = new MySqlDataAdapter(jokeSqlstring, cn);

            try
            {               
                DataSet ds = new DataSet();
                jokeDataAdapter.Fill(ds, "jokes");
                DataRow row = ds.Tables["jokes"].Rows[0];
                string header = (string)row["headline"];

                // Joke Header
                _GenerateHeaderJoke(header);
            }
            finally
            {
                jokeDataAdapter.SelectCommand.Dispose();
                jokeDataAdapter.Dispose();

                cn.Close();
            }
        } 
    }

    public void SubmitButton_Click(object sender, System.EventArgs e)
    {
        string jokeId = Request.QueryString["jokeId"];

        if (Page.IsValid && (this.captchaText.Text.ToString() ==
            Session["randomStr"].ToString()))
        {
            // The Code to insert data

        }
        else
        {
            lblOutput.Text = "הכנס שוב את המספר המופיע בריבוע הירוק בבקשה";
            return;
        }

        string _MessageText = this.messageText.Text;

        string result = _PostDataToServer(jokeId, _MessageText);

        // if we received from the server the OK value it means that the joke was insert successfully
        int firstChr = result.IndexOf(okValueReturnFromServer);
        if (firstChr != -1)
        {
            lblOutput.Text = "ההודעה נקלטה במערכת. תודה!";
            //Facebook_Graph_Toolkit.Helpers.IframeHelper.IframeRedirect("ConfirmationPage.aspx?type=2", true, true);
            Response.Redirect("http://jokes.mayaron.com/ConfirmationPage.aspx?type=2");
        }
        // else there was some kind of a problem inserting the joke into the DB
        else
        {
            lblOutput.Text = "ההודעה לא נקלטה, אנא נסה שנית.";
        }

        _FormReset();
        
    }

    private void _FormReset()
    {
        this.messageText.Text = "";
        this.captchaText.Text = "";
    }


    private string _PostDataToServer(string a_jokeId, string a_messageText)
    {
        // Create a request using a URL that can receive a post. 
        WebRequest request = WebRequest.Create("http://jokes.mayaron.com/AddNewMessage.aspx");

        // Set the Method property of the request to POST.
        request.Method = "POST";

        // Create POST data and convert it to a byte array.
        string postData = "JokeId=" + a_jokeId;
        postData += "&msgText=" + a_messageText;
        byte[] byteArray = Encoding.UTF8.GetBytes(postData);

        // Set the ContentType property of the WebRequest.
        request.ContentType = "application/x-www-form-urlencoded";

        // Set the ContentLength property of the WebRequest.
        request.ContentLength = byteArray.Length;

        // Get the request stream.
        Stream dataStream = request.GetRequestStream();

        // Write the data to the request stream.
        dataStream.Write(byteArray, 0, byteArray.Length);

        // Close the Stream object.
        dataStream.Close();

        // Get the response.
        WebResponse response = request.GetResponse();

        // Display the status.
        //Console.WriteLine(((HttpWebResponse)response).StatusDescription);

        // Get the stream containing content returned by the server.
        dataStream = response.GetResponseStream();

        // Open the stream using a StreamReader for easy access.
        StreamReader reader = new StreamReader(dataStream);

        // Read the content.
        string responseFromServer = reader.ReadToEnd();

        // Display the content.
        //Console.WriteLine(responseFromServer);
        //lblOutput.Text = responseFromServer;

        // Clean up the streams.
        reader.Close();
        dataStream.Close();
        response.Close();

        return responseFromServer;
    }

    private void _GenerateHeaderJoke(string a_header)
    {
        // Joke Header
        string str = "";
        string className = "";
        int headerLength = a_header.Length;
        if (headerLength < MAX_NUM_OF_CHARS_FOR_SMALL_IMG)
        {
            className = "headerjokeOuterBoxSmall";
        }
        else if (headerLength < MAX_NUM_OF_CHARS_FOR_MEDIUM_IMG)
        {
            className = "headerjokeOuterBoxMedium";
        }
        else
        {
            className = "headerjokeOuterBoxBig";
        }

        str += "<div class='" + className + "'>";
        str += "<table class='headerjokeInnerBox'><tr><td dir=rtl>" + a_header + "</td></tr></table></div>";
        jokeHeader.Text = str;
    }
}
