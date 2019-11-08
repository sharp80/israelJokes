using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using System.Collections.Specialized;
using MySql.Data.MySqlClient;
using System.Configuration;


public partial class UpdateJokeRead : System.Web.UI.Page
{
    protected string cnString;
    protected void Page_Load(object sender, EventArgs e)
    {
		cnString = ConfigurationSettings.AppSettings["connectionstring"].ToString();
        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);

        DateTime dt = DateTime.Now;
        string date = String.Format("{0:s}", dt);
        string sqlString = "insert into ReadJokes (userId, jokeId, appId, DateAdded) values ('" + 
                                                    Request.QueryString["userId"] + "','" +
                                                    Request.QueryString["jokeId"] + "','" +
                                                    Request.QueryString["appId"] + "','" + 
                                                    date + "'" +
                                                    ")";
       // Response.Write(sqlString + "<br>");

        MySqlConnection cn = new MySqlConnection(cnString);

        try
        {
            cn.Open();
            MySqlCommand command = new MySqlCommand(sqlString, cn);
            command.ExecuteNonQuery();
        }
        catch (Exception _Exception)
        {
            Response.Write(_Exception.ToString());
        }
        finally
        {
            cn.Close();
        }
    }
}