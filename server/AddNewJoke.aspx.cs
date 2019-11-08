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
using System.Net.Mail;
using System.IO;
using System.Net;
using System.Configuration;


public partial class MyWebForm2: System.Web.UI.Page
{
	protected string cnString;
    protected string FROM_EMAIL_ADDRESS = "mayaron.sw@gmail.com";
    protected string ADMIN_TO_EMAIL_ADDRESS = "elad.fiul@gmail.com";
    //protected string FilePath = @"d:\inetpub\vhosts\mayaron.com\httpdocs\jokes\App_Data";
	
	private void Page_Load(object sender, System.EventArgs e)
	{
		cnString  = ConfigurationSettings.AppSettings["connectionstring"].ToString();

        lblOutput.Text = "hello";
        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);

		string _JokeHeadline = Request.Form["JokeHeadline"];
		string _JokeText = Request.Form["JokeText"];
		string _JokeCategory = Request.Form["JokeCategory"];
        string _JokePic = Request.Form["PicName"];
        string _JokeVideo = Request.Form["VideoUrl"];       
		string _UserName = Request.Form["UserName"];
		string _UserEmail = Request.Form["UserEmail"];


        string sqlString = "insert into jokes(headline, joke, pic, video, categoryId, status, UserEmail, userName) value('" +
												Server.HtmlEncode(_JokeHeadline) + "','" +
												Server.HtmlEncode(_JokeText) + "','" +
												_JokePic + "','" +
                                                _JokeVideo + "'," +
												_JokeCategory + ", 'Pending','"+
												_UserEmail + "','"+
												_UserName + 
												"')";


        MySqlConnection cn = new MySqlConnection(cnString);

		try
		{
			
			cn.Open();
			MySqlCommand command = new MySqlCommand(sqlString, cn);

			command.ExecuteNonQuery();
			
			//lblOutput.Text =sqlString;
         //   _SendEmailToAdmin(_JokeHeadline, _JokeText);
			lblOutput.Text = "Joke was successfully insert";
		}
		catch (Exception _Exception)
		{
			//Response.Write(_Exception.ToString());
			lblOutput.Text =_Exception.ToString();
		}
		finally
		{
            cn.Close();
		}
	}
/*
    private void _SendEmailToAdmin(string a_jokeHeadline, string a_jokeText)
    {
        MailMessage msg = new MailMessage(FROM_EMAIL_ADDRESS, ADMIN_TO_EMAIL_ADDRESS);
        msg.Subject = "בדיחה חדשה נוספה לצחוקים"; 
        msg.Body = a_jokeHeadline + "\n" + a_jokeText;
        SmtpClient SMTPServer = new SmtpClient("smtp.gmail.com", 587)
		{
			UseDefaultCredentials = false,
			Credentials = new NetworkCredential(FROM_EMAIL_ADDRESS, "mayaron1!"),
			EnableSsl = true,
			DeliveryMethod = SmtpDeliveryMethod.Network
		};
        try
        {
            SMTPServer.Send(msg);
            //lblOutput.Text = "Sent msg to " + ADMIN_TO_EMAIL_ADDRESS;
        }
        catch (Exception ex)
        {
            using (StreamWriter writer = new StreamWriter(FilePath + @"\log.txt", true))
            {
                writer.WriteLine("{0} download data: {1}", DateTime.Now, ex.ToString());
                writer.Close();
            }
            //lblOutput.Text = "Exception occured while sending msg to " + ADMIN_TO_EMAIL_ADDRESS;
        }
    }
	*/
	/*
	public static RestResponse _SendEmailToAdmin(string a_jokeHeadline, string a_jokeText) {
		RestClient client = new RestClient();
		client.BaseUrl = "https://api.mailgun.net/v2";
		client.Authenticator = new HttpBasicAuthenticator(
			"api","key-0oz3u5rjyrlhnhx87296tpe6bzemrla5");
		RestRequest request = new RestRequest();
		request.AddParameter("domain",
							"samples.mailgun.org", ParameterType.UrlSegment);
		request.Resource = "{domain}/messages";
		request.AddParameter("from", "Server <server@samples.mailgun.org>");
		request.AddParameter("to", "shaysheffer@gmail.com");
        request.AddParameter("subject", "בדיחה חדשה נוספה לצחוקים");
		request.AddParameter("text", a_jokeHeadline + "\n" + a_jokeText);
		request.Method = Method.POST;
		return client.Execute(request);
	}
	*/
}

