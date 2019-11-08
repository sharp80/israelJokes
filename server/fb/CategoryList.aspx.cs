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


public partial class MyWebForm1 : System.Web.UI.Page
{
    protected string cnString ;
    //protected static bool m_isInRam = false;
    //protected const int NUM_OF_CHARS_TO_SEE_IN_JOKE_LIST = 100;
    public string CategoryID = "";

    private void Page_Load(object sender, System.EventArgs e)
    {
		cnString  = ConfigurationSettings.AppSettings["connectionstring"].ToString();

        if (!IsPostBack)
        {
            CategoryID = Request.QueryString["categoryId"];

            if (CategoryID != null)
            {
                _GenerateSpecificList(CategoryID);
            }
            else
            {
                _GenerateHighlightList();
            }
        }

    }

    private void _GenerateSpecificList(string a_categoryId)
    {
        string iconSqlString = "SELECT icon from categories where id =" + a_categoryId;
        //string jokesSqlString = "SELECT * from jokes where jokes.categoryId =" + a_categoryId + " and jokes.status = 'Active' ORDER BY id DESC";
            
        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter CategoryIconDataAdapter = new MySqlDataAdapter(iconSqlString, cn);
        //MySqlDataAdapter jokeDataAdapter = new MySqlDataAdapter(jokesSqlString, cn);
        try
        {
            string str = "";
            string iconstr = "";
            DataSet ds = new DataSet();
            DataRow row;

            // Category Icon
            str = "";
            CategoryIconDataAdapter.Fill(ds, "categories");
            row = ds.Tables["categories"].Rows[0];
            iconstr = "images/icons/" + (string)row["icon"];
            str = "<img id='categoryPageIcon' src='" + iconstr + "' />";
            categoryIcon.Text = str;

            /*
            // Jokes list
            DataSet ds2 = new DataSet();
            jokeDataAdapter.Fill(ds2, "jokes");
            str = "";
            str += "<div id='listOfJokesDiv'>";
            str += "<table id='listOfJokesTable' align=right cellpadding=10px style='width:100%;'>";
            foreach (DataRow row2 in ds2.Tables["jokes"].Rows)
            {
                int jokeid = (int)row2["id"];
                string header = (string)row2["headline"];
                string joke = (string)row2["joke"];
                float rateFloat = (float)row2["rating"];
                int rate = (int)rateFloat;

                string jokeInitial;
                if (joke.Length > NUM_OF_CHARS_TO_SEE_IN_JOKE_LIST)
                {
                    int index = joke.IndexOf(" ", NUM_OF_CHARS_TO_SEE_IN_JOKE_LIST);
                    if ((joke.Length > index) && (index != -1))
                    {
                        jokeInitial = joke.Remove(index);
                        jokeInitial += "...";
                    }
                    else
                    {
                        jokeInitial = joke;
                    }
                }
                else
                {
                    jokeInitial = joke;
                }                   

                str += "<tr>";
                str += "<td class='jokeRowInList' align=right dir=rtl>";
                str += "<a href='http://jokes.mayaron.com/JokePage.aspx?type=specific&categoryId=" + a_categoryId + "&JokeId=" + jokeid + "'>";
                //str += "<a href=http://apps.facebook.com/israelijokes/JokePage.aspx?type=specific&categoryId=" + a_categoryId + "&JokeId=" + jokeid + " target='_top'>";

                str += "<table cellspacing='5px'>";
                str += "<tr>";
                str += "<td class='headerJokeInList'>" + header + "</td>";
                str += "<td><img id='LikeIconInList' src=images/site/Thumb-Up-icon.png></td>";
                str += "<td><label id='rateString'>" + rate + "</label></td>";
                str += "</tr>";
                str += "</table>";
                str += "<div class='jokeTextInList' >" + jokeInitial + " <br></div>";
                str += "</a>";
                str += "</td>";
                str += "</tr>";
            }
            str += "</table>";
            str += "</  div>";
            jokes.Text = str;
             */
        }
        finally
        {
            CategoryIconDataAdapter.SelectCommand.Dispose();
            CategoryIconDataAdapter.Dispose();

            //jokeDataAdapter.SelectCommand.Dispose();
            //jokeDataAdapter.Dispose();

            cn.Close();
        }

        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }

    private void _GenerateHighlightList()
    {
        //string jokesSqlString = "SELECT * from Jokes where jokes.status = 'Active' ORDER BY rating DESC,id DESC LIMIT 10";

        //MySqlConnection cn = new MySqlConnection(cnString);
        //MySqlDataAdapter jokeDataAdapter = new MySqlDataAdapter(jokesSqlString, cn);
        //try
        //{
            string str = "";
            string iconstr = "";

            // Category Icon
            str = "";
            iconstr = "images/site/ten_best_icon.png";
            str = "<img id=highlightIcon src='" + iconstr + "' />";
            categoryIcon.Text = str;

            /*
            // Jokes list
            DataSet jokeListDs = new DataSet();
            jokeDataAdapter.Fill(jokeListDs, "jokes");
            str = "";
            str += "<div id='listOfJokesDiv'>";
            str += "<table id='listOfJokesTable' align=right cellpadding=10px style='width:100%;'>";
            foreach (DataRow row in jokeListDs.Tables["jokes"].Rows)
            {
                int jokeId = (int)row["id"];
                //int categoryId = (int)row["categoryId"];
                string header = (string)row["headline"];
                string joke = (string)row["joke"];
                float rateFloat = (float)row["rating"];
                int rate = (int)rateFloat;

                string jokeInitial;
                if (joke.Length > NUM_OF_CHARS_TO_SEE_IN_JOKE_LIST)
                {
                    int index = joke.IndexOf(" ", NUM_OF_CHARS_TO_SEE_IN_JOKE_LIST);
                    if ((joke.Length > index) && (index != -1))
                    {
                        jokeInitial = joke.Remove(index);
                        jokeInitial += "...";
                    }
                    else
                    {
                        jokeInitial = joke;
                    }
                }
                else
                {
                    jokeInitial = joke;
                }

                str += "<tr>";
                str += "<td class='jokeRowInList' align=right dir=rtl>";
                //str += "<a href=http://jokes.mayaron.com/JokePage.aspx?JokeId=" + jokeId + "&rate=" + rate + ">";
                //str += "<a href=http://apps.facebook.com/israelijokes/JokePage.aspx?type=highlight&JokeId=" + jokeId + "&rate=" + rate + " target='_top'>";
                str += "<a href='http://jokes.mayaron.com/JokePage.aspx?type=highlight&JokeId=" + jokeId + "'>";
            
                str += "<table cellspacing='5px'>";
                str += "<tr>";
                str += "<td class='headerJokeInList'>" + header + "</td>";
                str += "<td><img id='LikeIconInList' src=images/site/Thumb-Up-icon.png></td>";
                str += "<td><label id='rateString'>" + rate + "</label></td>";
                str += "</tr>";
                str += "</table>";
                str += "<div class='jokeTextInList' >" + jokeInitial + " <br></div>";
                str += "</a>";
                str += "</td>";
                str += "</tr>";
            }
            str += "</table>";
            str += "</  div>";
            jokes.Text = str;
             */
        //}
        //finally
        //{
        //    jokeDataAdapter.SelectCommand.Dispose();
        //    jokeDataAdapter.Dispose();

        //    cn.Close();
        //}

        //Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }

}
