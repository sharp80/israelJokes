using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using System.Configuration;


public partial class AddJokeToFav : System.Web.UI.Page
{
    protected string cnString ;

    protected void Page_Load(object sender, EventArgs e)
    {
		cnString = ConfigurationSettings.AppSettings["connectionstring"].ToString();

        string JokeId = Request.QueryString["jokeId"];
		string UserId = Request.QueryString["UserId"];
		string AddToFavorite = Request.QueryString["AddToFavorite"];

        try
        {
			string sqlstringUpdate ;
			if (AddToFavorite.Equals("true"))
			{
				sqlstringUpdate = "INSERT INTO favorites(JokeId, UserId) VALUES(" + JokeId + ",'" + UserId + "')";
			}
			else
			{
				sqlstringUpdate = "DELETE FROM favorites WHERE UserId='" + UserId + "' AND JokeId="  + JokeId ;
			}
			//Response.Write(sqlstringUpdate);
	        MySqlConnection cn = new MySqlConnection(cnString);
			cn.Open();
			MySqlCommand command = new MySqlCommand(sqlstringUpdate, cn);
			command.ExecuteNonQuery();
			//  Explicitly dispose the SelectCommand instance
			cn.Close();
            
        }
		catch (Exception _Exception)
		{
			Response.Write(_Exception.ToString());
		}
		
        finally
        {
        }

        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }
}