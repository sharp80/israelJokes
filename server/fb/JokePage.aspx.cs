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

//using Facebook_Graph_Toolkit;


public partial class MyWebForm1 : System.Web.UI.Page
{
    public string Type = "";
    public string JokeID = "";
    public string CategoryID = "";
    public string Rate = "";
    public string JokeText = "";
    public string JokePic = "";
    public string JokeVideo = "";
    public string JokeHeader = "";
    public string StarRating = "";
    public string NumOfVotes = "";

    protected string cnString ;

    private void Page_Load(object sender, System.EventArgs e)
    {
		cnString  = ConfigurationSettings.AppSettings["connectionstring"].ToString();

        if (!IsPostBack)
        {
            Type = Request.QueryString["type"];
            JokeID = Request.QueryString["jokeId"];
            CategoryID = Request.QueryString["categoryId"];
            Rate = Request.QueryString["rate"];

            if (String.Compare(Type,"specific") == 0) 
            {
                _GenerateSpecificJoke();
            }
            else if (String.Compare(Type, "highlight") == 0) 
            {
                _GenerateHighlightJoke();   
            }
            else
            {
                _GenerateRandomJoke();
            }

            // Fix Joke Icon
            string str = "<a href='"+ ConfigurationSettings.AppSettings["siteUrl"].ToString() +"fb/FixJokePage.aspx?jokeId=" + JokeID + "' >";
            str += "<img id='AttentionIcon' alt='' title='דווח על תקלה בבדיחה' src='images/site/attn128.png' align='right' /></a>";
            FixJokeIcon.Text = str;

            Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
        }
    }

    /*
    public void ShareButton_Click(object sender, System.EventArgs e)
    {
        try
        {
            //Api.PostFeed(jokeText, null, null, new Facebook_Graph_Toolkit.FacebookObjects.PostAction("בואו לנסות את האפליקציה שתפיל אתכם מצחוק", "http://apps.facebook.com/israelijokes/"));
            Api.PostFeed(JokeText, null, null, null);
            Label1.Text = "JokeText= " + JokeText;
        }
        catch (Exception ex) 
        {
            //Label1.Text = "An error occured while trying to publish the message.<br />Error Details: " + ex.ToString(); 
        }
    }
    */

    private void _GenerateSpecificJoke()
    {
        string CategoryIconSqlstring = "SELECT icon from categories where id =" + CategoryID;

        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter CategoryIconDataAdapter = new MySqlDataAdapter(CategoryIconSqlstring, cn);
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
            str += "<a href='" + ConfigurationSettings.AppSettings["siteUrl"].ToString() + "fb/CategoryList.aspx?categoryId=" + CategoryID + "' >";
            str += "<img id='categoryIconOnJokePage' alt='' title='חזור לרשימת הבדיחות לקטגוריה' src='" + iconstr + "' />";
            str += "</a>";
            categoryIcon.Text = str;
        }
        finally
        {
            CategoryIconDataAdapter.SelectCommand.Dispose();
            CategoryIconDataAdapter.Dispose();

            cn.Close();
        }
    }

    private void _GenerateRandomJoke()
    {
        // Category Icon
        string str = "";
        str += "<img id=randomCategoryIcon alt='' src=images/site/random_icon.png align=left />";
        categoryIcon.Text = str;   
    }



    private void _GenerateHighlightJoke()
    {
        // Category Icon
        string str = "";
        str += "<a href='"+ConfigurationSettings.AppSettings["siteUrl"].ToString()+"fb/CategoryList.aspx' >";
        str += "<img id=highlightIcon src='images/site/ten_best_icon.png' />";
        str += "</a>";
        categoryIcon.Text = str;
    }

    /*
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

        str += "<div id='jokePage' class='" + className + "'>";
        str += "<table class='headerjokeInnerBox'><tr><td dir=rtl><div id='jokeHeaderDiv'>" + a_header + "</div></td></tr></table></div>";
        //jokeHeader.Text = str;
    }
     */
}
