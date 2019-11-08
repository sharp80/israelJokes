using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using Newtonsoft.Json;


public partial class RetrieveFromServerRand : System.Web.UI.Page
{
    protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; ";
    public enum eShowJokes
    {
        SHOW_UNREAD_JOKES,
        SHOW_READ_JOKES
    };
    protected void Page_Load(object sender, EventArgs e)
    {
		string action = Request.QueryString["action"];
        
        string categoryId = Request.QueryString["CategoryId"];
        string userId = Request.QueryString["userId"];
        eShowJokes readType = (eShowJokes)Int32.Parse(Request.QueryString["readType"]);
		string AppId =  Request.QueryString["AppId"];
		if ( AppId == null)
			AppId = "";
        string readTypeSql ="";
        string sqlstring;
        string SelectFields = "headline, joke, pic, categoryId, id, rating, video ";
		string  numOfJokes = Request.QueryString["jokesNum"];
		if (numOfJokes == null)
			numOfJokes = "5";
			
        if (categoryId.Equals("9999")) // favorites
        {
            string lastJokeId = Request.QueryString["lastJokeId"];

            sqlstring = "SELECT " + SelectFields + ", id as Fav  FROM jokes, favorites WHERE jokes.id>"
                    + lastJokeId + " AND userId ='" + userId + "' AND jokes.id = favorites.jokeId ORDER BY jokeId";
        }
        else if (categoryId.Equals("9998")) // top 10
        {
            string lastJokeId = Request.QueryString["lastJokeId"];

            sqlstring = " SELECT " + SelectFields + ", favorites.JokeId as Fav FROM ( SELECT " + SelectFields + " FROM Jokes WHERE jokes.status = 'Active' " +
               "ORDER BY rating DESC LIMIT 10 ) as TopJokes " +
               " LEFT JOIN favorites on TopJokes.id = favorites.JokeId and favorites.UserId='" + userId + "'" +
               " where TopJokes.id>" + lastJokeId  ;
        }
     
        else
        {
            if (readType == eShowJokes.SHOW_UNREAD_JOKES)
            {
                readTypeSql = " AND id NOT IN ( SELECT jokeId FROM ReadJokes where userId ='" + userId + "') ";
            }
            else  // (readType == eShowJokes.SHOW_READ_JOKES)
            {
                readTypeSql = " AND id IN ( SELECT jokeId FROM ReadJokes where userId ='" + userId + "') ";
            }

            string OrderBy = " RAND() ";
            string lastRating = "";
            if (action == null)
            {
                OrderBy = " RAND() ";
            }
            else if (action.Equals("lastRating"))
            {
                string LowestRating = Request.QueryString["LowestRating"];
               /* if (LowestRating.Equals("9999.0"))
                {
                   try
                    {
                        MySqlConnection cn1 = new MySqlConnection(cnString);
                        string sqlString = "DELETE FROM JokesDl WHERE UserId='" + userId + "'";
                        cn1.Open();
                        MySqlCommand command = new MySqlCommand(sqlString, cn1);
                        command.ExecuteNonQuery();
                        cn1.Close();
                    }
                    catch (Exception _Exception)
                    {
                        //Response.Write(_Exception.ToString());
                    }
                    finally
                    {
                      
                    }
                }*/
                //lastRating = " AND rating<=" + LowestRating;
                lastRating = "";
                OrderBy = " rating DESC ";
            }
            string rand = Request.QueryString["Rand"];
            string activeJokesqlString;
            if (rand == null)
            { // dont random category, only joke
                activeJokesqlString = " categoryId=" + categoryId + " AND status='Active' ";
            }
            else
            { // random category and random joke
                activeJokesqlString =  " status='Active' ";
            }
            string jokesDlSql = "SELECT jokeid from JokesDl WHERE userId='" + userId + "'";
            sqlstring = "SELECT " + SelectFields + ", favorites.JokeId as Fav FROM (SELECT " + 
				SelectFields + " FROM jokes WHERE id NOT IN (" +
                jokesDlSql + ") " + lastRating + readTypeSql + " AND " + activeJokesqlString +
                "ORDER BY " + OrderBy + " LIMIT " + numOfJokes + " ) as jokes left join favorites on jokes.id = favorites.JokeId and favorites.UserId='" + userId + "'";
            //Response.Write(sqlstring + "<BR>");
        }
        //Response.Write(sqlstring);
        // return;
        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
        try
        {
            DataSet ds = new DataSet();
            dr.Fill(ds);
            if (categoryId.Equals("9999") == false && categoryId.Equals("9998") == false) // dont waste time on favorites
            {
                foreach (DataRow row in ds.Tables[0].Rows)
                {
                    DateTime dt = DateTime.Now;
                  
                    string addJokesDlToDb = "INSERT INTO JokesDl (userId, jokeid, DateAdded) VALUES('" + userId + "'," + row["id"] + ",'" + 
                                                String.Format("{0:s}", dt)  + "')";
                    //Response.Write(addJokesDlToDb);
                    cn.Open();
                    MySqlCommand command = new MySqlCommand(addJokesDlToDb, cn);
                    command.ExecuteNonQuery();
                    cn.Close();
                }
				// iOS cannot get null in json - change it to -1
				if ( AppId.Equals("2") )
				{
					foreach (DataRow row in ds.Tables[0].Rows)
					{
						Response.Write(row["Fav"] + "<BR>");

						if (row["Fav"].ToString().Length == 0)
						{
							row["Fav"] = "-1";
						}
					}
				}
            }
			string jsonString = JsonConvert.SerializeObject(ds.Tables[0]);
            Response.Write(jsonString);
        }
        finally
        {
            //  Explicitly dispose the SelectCommand instance
            dr.SelectCommand.Dispose();
            dr.Dispose();
            cn.Close();
        }
        
        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }
}
