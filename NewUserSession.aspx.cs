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
using System.Configuration;


using System.Data.Odbc;

public partial class NewUserSession: System.Web.UI.Page
{
    protected string cnString ;
	
	private void Page_Load(object sender, System.EventArgs e)
	{
		cnString = ConfigurationSettings.AppSettings["connectionstring"].ToString();
        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);

		string UserId = Request.QueryString["UserId"];

		string sqlString = "DELETE from JokesDl WHERE userId='"  + UserId +"'";

	  
		try
		{
			MySqlConnection cn = new MySqlConnection(cnString);
			cn.Open();
			MySqlCommand command = new MySqlCommand(sqlString, cn);

			command.ExecuteNonQuery();
			cn.Close();
		//	lblOutput.Text =sqlString;
			//lblOutput.Text = "OK";
            Response.Write("OK");
		}
		catch (Exception _Exception)
		{
			Response.Write(_Exception.ToString());
			//lblOutput.Text =_Exception.ToString();
		}
		finally
		{
		}
	}
}

