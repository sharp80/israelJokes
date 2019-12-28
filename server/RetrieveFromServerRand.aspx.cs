using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using Newtonsoft.Json;
using System.Configuration;


public partial class RetrieveFromServerRand : System.Web.UI.Page
{
    protected string cnString ;
    public enum eShowJokes
    {
        SHOW_UNREAD_JOKES,
        SHOW_READ_JOKES,
		SHOW_ALL_JOKES
    };
    protected void Page_Load(object sender, EventArgs e)
    {
		cnString = ConfigurationSettings.AppSettings["connectionstring"].ToString();
		string action = Request.QueryString["action"];
        string categoryId = Request.QueryString["CategoryId"];
        string userId = Request.QueryString["userId"];
        eShowJokes readType = (eShowJokes)Int32.Parse(Request.QueryString["readType"]);
		string AppId =  Request.QueryString["AppId"];
		if ( AppId == null)
			AppId = "";
        string readTypeSql ="";
        string sqlstring;
		string SelectFields ;
		string numOfJokes = Request.QueryString["jokesNum"];
		string stars = Request.QueryString["stars"];
		
		string SelectFieldsOuter = "headline, joke, pic, categoryId, id, rating, video ";
		if ( stars == null )
			stars = "false";
		if ( stars.Equals("true") )
			SelectFields = "headline, joke, pic, categoryId, id, StarRating as rating, video ";
		else 
			SelectFields = SelectFieldsOuter;

		
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
			string OrderBy ;
			if ( stars.Equals("true") )
				OrderBy = "StarRating";
			else
				OrderBy = "Rating";
            sqlstring = " SELECT " + SelectFieldsOuter + ", favorites.JokeId as Fav FROM ( SELECT " + SelectFields + " FROM jokes WHERE jokes.status = 'Active' " +
               "ORDER BY " + OrderBy + " DESC LIMIT 10 ) as TopJokes " +
               " LEFT JOIN favorites on TopJokes.id = favorites.JokeId and favorites.userId='" + userId + "'" +
               " where TopJokes.id>" + lastJokeId  ;
        }
     
        else
        {
            if (readType == eShowJokes.SHOW_UNREAD_JOKES)
            {
                readTypeSql = " AND id NOT IN ( SELECT jokeId FROM ReadJokes where userId ='" + userId + "') ";
            }
            else if (readType == eShowJokes.SHOW_READ_JOKES)
            {
                readTypeSql = " AND id IN ( SELECT jokeId FROM ReadJokes where userId ='" + userId + "') ";
            }
			else 
			{
				readTypeSql = " ";
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
            string jokesDlSql = "SELECT JokeId from JokesDl WHERE userId='" + userId + "'";
            sqlstring = "SELECT " + SelectFieldsOuter + ", favorites.JokeId as Fav FROM (SELECT " + 
				SelectFields + " FROM jokes WHERE id NOT IN (" +
                jokesDlSql + ") " + lastRating + readTypeSql + " AND " + activeJokesqlString +
                "ORDER BY " + OrderBy + " LIMIT " + numOfJokes + " ) as jokes left join favorites on jokes.id = favorites.JokeId and favorites.userId='" + userId + "'";
            
			//Response.Write(sqlstring + "<BR>");
        }
	//	sqlstring = "SELECT " + SelectFieldsOuter + " from JokesDl WHERE userId='" + userId + "'";
			
		//Response.Write(sqlstring);
        //return;
		//sqlstring = "SELECT headline, joke, pic, categoryId, id, rating, video , favorites.JokeId as Fav FROM (SELECT headline, joke, pic, categoryId, id, StarRating as rating, video FROM jokes WHERE id NOT IN (SELECT JokeId from JokesDl WHERE userId='5fede259-bc40-3589-a885-e8a3387ab59a') AND categoryId=24 AND status='Active' ORDER BY RAND() LIMIT 1 ) as jokes left join favorites on jokes.id = favorites.JokeId and favorites.userId='5fede259-bc40-3589-a885-e8a3387ab59a'";
        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
        try
        {
            DataSet ds = new DataSet();
            dr.Fill(ds);
            if (categoryId.Equals("9999") == false && categoryId.Equals("9998") == false  && categoryId.Equals("9997") == false ) // dont waste time on favorites
            {
                foreach (DataRow row in ds.Tables[0].Rows)
                {
                    DateTime dt = DateTime.Now;
                  
                    string addJokesDlToDb = "INSERT INTO JokesDl (userId, JokeId, DateAdded) VALUES('" + userId + "'," + row["id"] + ",'" + 
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
		catch (Exception _Exception) {
			Response.Write(_Exception.ToString());
		} finally {
            //  Explicitly dispose the SelectCommand instance
            dr.SelectCommand.Dispose();
            dr.Dispose();
            cn.Close();
        }
        
        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }
}
