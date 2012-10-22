using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;


public partial class GetCategories : System.Web.UI.Page
{
//    protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; ";
   protected string cnString = "server=fb6a5a0b-b681-4e41-8f5e-a07501561d4d.mysql.sequelizer.com;database=dbfb6a5a0bb6814e418f5ea07501561d4d;uid=qynlpebmiicoqgjk;pwd=LB3SWR5n5DfBJN7i6fb6e2XbL5BodrVzm4XhCwK38uDSMM57v3YL8MZSM2g2oQiM";
   
    protected void Page_Load(object sender, EventArgs e)
    {
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
        if ( statsInd == 0  )
		{
			DateTime tm = new DateTime();
            string dateStr = String.Format("{0:yyyy-M-d}", DateTime.Now.AddDays(-1));
            
			sqlstring  = "SELECT categories.name as catName, dateAdded " +
                            " from readJokes, jokes, categories where " +
							" readJokes.jokeId=jokes.id and " +
							" AppId=" + AppId +
							" and jokes.categoryId=categories.id and dateAdded>='" + dateStr
                                + "' ";
      
        }
		/*
		MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
		
        try
        {
            DataSet ds = new DataSet();
            dr.Fill(ds);
			
			for (int dateInd = 0 ; dateInd < 2 ; dateInd ++)
			{
				
				Dictionary<string, int> dictionary = new Dictionary<string, int>();
				DateTime dateStr = DateTime.Parse(DateTime.Now.AddDays(-dateInd).ToShortDateString());
				Response.Write(dateStr.ToString()+"<BR>");
				foreach (DataRow row in ds.Tables[0].Rows)
				{
				  DateTime dateAdded = DateTime.Parse(row["dateAdded"].ToString());
					//Response.Write(   + " " + dateStr +"<BR>");
					if( dateAdded.Equals(dateStr) )
					{					
						string catName = row["catName"].ToString();
						if ( dictionary.ContainsKey( catName ) )
						{
							dictionary[catName] = dictionary[catName] + 1;
						}
						else
						{
							dictionary.Add(catName, 0);
						}
					}
				}
				
				
				int ind = 0;
				if (statsInd == 0)
				{
					Response.Write("<table dir='rtl'>");
					Response.Write("<tr>" +
									 "<td>category</td>" +
									 "<td>num of views</td>" +
									 "</tr>");
					foreach (KeyValuePair<string, int> pair in dictionary)
					{
						Response.Write("<tr><td>"+
											pair.Key +
											"</td><td>" + 
											pair.Value +
											"</td>"
										);

					}
					Response.Write("</table>");

				}
			}
           
			
		}
		finally
        {
            //  Explicitly dispose the SelectCommand instance
            dr.SelectCommand.Dispose();
            dr.Dispose();
        }
*/
Response.Write("table>");
        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }
}