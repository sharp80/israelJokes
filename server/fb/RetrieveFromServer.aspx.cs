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


public partial class RetrieveFromServer : System.Web.UI.Page
{
    protected string cnString;

    protected void Page_Load(object sender, EventArgs e)
    {
		cnString  = ConfigurationSettings.AppSettings["connectionstring"].ToString();

        string type = Request.QueryString["type"];
        if (type == null)
        {
            // Put a default value
            type = "next";
        }
        string categoryId = Request.QueryString["CategoryId"];
		string lastJokeId = Request.QueryString["lastJokeId"];
        string rate = Request.QueryString["rate"];
        string numOfVotes = Request.QueryString["numOfVotes"];       

        string sqlstring = "";

        switch (type)
        {
            case "specific":
                sqlstring = "SELECT * FROM jokes where categoryId=" + categoryId + " and id = " + lastJokeId + " and status='Active' ORDER BY id LIMIT 1";
                break;

            case "next":
                sqlstring = "SELECT * FROM jokes where categoryId=" + categoryId + " and id < " + lastJokeId + " and status='Active' ORDER BY id DESC LIMIT 1";
                break;

            case "next_rated":
                sqlstring = "SELECT * from (SELECT * from jokes where categoryId=" + categoryId + " and jokes.status = 'Active' ORDER BY StarRating DESC,numOfVotes DESC,id DESC) as A where " +
                            "(A.StarRating = " + rate + " AND A.numOfVotes = " + numOfVotes + " AND A.id < " + lastJokeId + ") OR " +
                            "(A.StarRating = " + rate + " AND A.numOfVotes < " + numOfVotes + ") OR " +
                            "(A.StarRating < " + rate + ") LIMIT 1";
                break;

            case "prev":
                sqlstring = "SELECT * FROM jokes where categoryId=" + categoryId + " and id > " + lastJokeId + " and status='Active' ORDER BY id LIMIT 1";
                break;

            case "prev_rated":
                sqlstring = "SELECT * from (SELECT * from jokes where categoryId=" + categoryId + " and jokes.status = 'Active' ORDER BY StarRating,numOfVotes,id) as A where " +
                            "(A.StarRating = " + rate + " AND A.numOfVotes = " + numOfVotes + " AND A.id > " + lastJokeId + ") OR " +
                            "(A.StarRating = " + rate + " AND A.numOfVotes > " + numOfVotes + ") OR " +
                            "(A.StarRating > " + rate + ") LIMIT 1";
                break;

            case "rand":
                sqlstring = "SELECT * FROM jokes where status='Active' ORDER BY RAND() LIMIT 1";
                break;

            case "highlight":
                sqlstring = "SELECT * FROM jokes where id = " + lastJokeId + " and status='Active' ORDER BY id LIMIT 1";
                break;

            case "highlight_next":
                sqlstring = "SELECT * from (SELECT * from jokes where jokes.status = 'Active' ORDER BY StarRating DESC,numOfVotes DESC,id DESC LIMIT 10) as TOP10 where " +
                            "(TOP10.StarRating = " + rate + " AND TOP10.numOfVotes = " + numOfVotes + " AND TOP10.id < " + lastJokeId + ") OR " +
                            "(TOP10.StarRating = " + rate + " AND TOP10.numOfVotes < " + numOfVotes + ") OR " +
                            "(TOP10.StarRating < " + rate + ") LIMIT 1";
                break;

            case "highlight_prev":
                sqlstring = "SELECT * from (SELECT * from (SELECT * from jokes where jokes.status = 'Active' ORDER BY StarRating DESC,numOfVotes DESC,id DESC LIMIT 10) as TOP10 ORDER BY TOP10.StarRating, TOP10.numOfVotes, TOP10.id) as TOP10INV where " +
                            "(TOP10INV.StarRating = " + rate + " AND TOP10INV.numOfVotes = " + numOfVotes + " AND TOP10INV.id > " + lastJokeId + ") OR " +
                            "(TOP10INV.StarRating = " + rate + " AND TOP10INV.numOfVotes > " + numOfVotes + ") OR " +
                            "(TOP10INV.StarRating > " + rate + ") LIMIT 1";
                break;

            case "debug":
                //sqlstring = "SELECT * FROM jokes where id = " + lastJokeId;
				sqlstring = "DELETE FROM favorites where favorites.userId = 'ELAD_TEST2'";
                break;

            case "specific_list":
                sqlstring = "SELECT * from jokes where jokes.categoryId =" + categoryId + " and jokes.status = 'Active' ORDER BY id DESC";
                break;

            /*
            case "highlight_list":
                sqlstring = "SELECT * from jokes where jokes.status = 'Active' ORDER BY rating DESC,id DESC LIMIT 10";
                break;
            */

            case "highlight_list":
                sqlstring = "SELECT * from jokes where jokes.status = 'Active' ORDER BY StarRating DESC, numOfVotes DESC, id DESC LIMIT 10";
                break;
        }

        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
        try
        {
            DataSet ds = new DataSet();
            dr.Fill(ds);
            string jsonString = JsonConvert.SerializeObject(ds.Tables[0]);
            Response.Write(jsonString);
        }
        catch(Exception _Exception)
		{
			Response.Write(sqlstring + "<BR>");
			Response.Write(_Exception.ToString());
            string jsonString = JsonConvert.SerializeObject("");
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
