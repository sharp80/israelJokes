using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using System.Configuration;

public partial class GetUsers : System.Web.UI.Page
{
    protected string cnString;
    protected void Page_Load(object sender, EventArgs e)
    {
		cnString = ConfigurationSettings.AppSettings["connectionstring"].ToString();
        string action = Request.QueryString["id"];
		if (action != null)
		{
			if (action.Equals("mayaron1!") == false)
				return;
		}
		else
		{
			return;
		}
		string appIdStr = Request.QueryString["AppId"];
		if (appIdStr == null)
			appIdStr="0";
		int AppId = Int32.Parse(appIdStr);

        string sqlstring ="";
        int statsInd = Int32.Parse(Request.QueryString["statInd"]);
        if ( statsInd == 1  )
            sqlstring  = "SELECT UserId, count(JokeId), AppId as cnt, " +
                            "max(dateAdded)as lastDate, min(dateAdded) as "+
                            "firstAdded from ReadJokes group by UserId order by lastDate, cnt";
        else if (statsInd == 2 || statsInd==3)
            sqlstring = "SELECT UserId from ReadJokes where AppId="+AppId;
     

        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
        try
        {
            DataSet ds = new DataSet();
            dr.Fill(ds);
			int ind = 0;
            if (statsInd == 3)
            {
				Dictionary<string, int> dictionary = new Dictionary<string, int>();
				foreach (DataRow row in ds.Tables[0].Rows)
				{
					string userId = row["UserId"].ToString();
					if ( dictionary.ContainsKey( userId ) )
					{
						dictionary[userId] = dictionary[userId] + 1;
					}
					else
					{
						dictionary.Add(userId, 0);
					}
				}
                Response.Write("Num of users that read a joke:" + dictionary.Count()  + "<BR>");
            }
            else
            {
                Response.Write("<table>");
                Response.Write("<tr><td>#</td>" +
                                     "<td>uid</td>" +
                                    "<TD>First Read</td>" +
                                    "<TD>Last Read</td>" +
                                    "<td>Jokes Read Num</td>" + 
									"</tr>");
                foreach (DataRow row in ds.Tables[0].Rows)
                {
                    ind++;
                    string htmlStr = "<tr><td>" + ind.ToString() + ")</td><td> " +
                                    row["UserId"] + "</td>";
                    if (statsInd == 1)
                    {
                        htmlStr += "<TD>" + row["firstAdded"] + "</td>" +
                                    "<TD>" + row["lastDate"] + "</td>";
                    }
                    htmlStr += "<td>" + row["cnt"] + "</td></tr>";
                    Response.Write(htmlStr);
                }
                Response.Write("</table>");
            }
			
            DateTime tm = new DateTime();
            string dateStr = String.Format("{0:yyyy-M-d}", DateTime.Now);
            sqlstring = "SELECT count(dateAdded) as cnt FROM ReadJokes " +
                                "where dateAdded='" + dateStr
                                + "'";

            sqlstring = "SELECT dateAdded, count(JokeId) as cnt "+
                                " from ReadJokes where AppId="+AppId+" group by dateAdded ";
	
           
            DataSet ds1 = new DataSet();
            MySqlDataAdapter dr1 = new MySqlDataAdapter(sqlstring, cn);
            dr1.Fill(ds1,"cntTable");
            foreach (DataRow row in ds1.Tables["cntTable"].Rows)
            {
                Response.Write("Number of jokes read at: " + row["dateAdded"] + " : " + row["cnt"] + "<BR>");
            }
			
		}
		finally
        {
            //  Explicitly dispose the SelectCommand instance
            dr.SelectCommand.Dispose();
            dr.Dispose();
        }

        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }
}