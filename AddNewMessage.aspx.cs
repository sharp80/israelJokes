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

public partial class MyWebForm3 : System.Web.UI.Page
{
	protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; charset=hebrew;";
    protected string FROM_EMAIL_ADDRESS = "support@mayaron.com";
    protected string ADMIN_TO_EMAIL_ADDRESS = "elad.fiul@gmail.com";
    protected string FilePath = @"d:\inetpub\vhosts\mayaron.com\httpdocs\jokes\App_Data";
	
	private void Page_Load(object sender, System.EventArgs e)
	{
		lblOutput.Text = "hello";
        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);

		string _JokeId = Request.Form["JokeId"];
		string _msgText = Request.Form["msgText"];

        DateTime dt = DateTime.Now;
        string date = String.Format("{0:s}", dt);
		string sqlString = "insert into messages(jokeId, messageText, DateAdded) value(" +
												Server.HtmlEncode(_JokeId) + ",'" +
												Server.HtmlEncode(_msgText) + "','" +
                                                date +"'" + 
                                                ")";

        MySqlConnection cn = new MySqlConnection(cnString);
		try
		{
			
			cn.Open();
			MySqlCommand command = new MySqlCommand(sqlString, cn);

			command.ExecuteNonQuery();

            _SendEmailToAdmin(_JokeId, _msgText);
            lblOutput.Text = "Message was successfully insert";
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

    private void _SendEmailToAdmin(string a_jokeId, string a_msgText)
    {
        MailMessage msg = new MailMessage(FROM_EMAIL_ADDRESS, ADMIN_TO_EMAIL_ADDRESS);
        msg.Subject = "הודעה חדשה נוספה לצחוקים";
        msg.Body = "Joke ID: " + a_jokeId + "\n" + a_msgText;
        SmtpClient SMTPServer = new SmtpClient("localhost");
        try
        {
            SMTPServer.Send(msg);
        }
        catch (Exception ex)
        {
            using (StreamWriter writer = new StreamWriter(FilePath + @"\log.txt", true))
            {
                writer.WriteLine("{0} download data: {1}", DateTime.Now, ex.ToString());
                writer.Close();
            }
        }
    }
}

