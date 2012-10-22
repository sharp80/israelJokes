using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using Newtonsoft.Json;

public partial class ShowAllCategories : System.Web.UI.Page
{
  //  protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; ";
   protected string cnString = "server=fb6a5a0b-b681-4e41-8f5e-a07501561d4d.mysql.sequelizer.com;database=dbfb6a5a0bb6814e418f5ea07501561d4d;uid=qynlpebmiicoqgjk;pwd=LB3SWR5n5DfBJN7i6fb6e2XbL5BodrVzm4XhCwK38uDSMM57v3YL8MZSM2g2oQiM";

    protected void Page_Load(object sender, EventArgs e)
    {
		String version = Request.QueryString["version"];
		String AppId = Request.QueryString["AppId"]; // TODO - not needed for now

		string sqlstring ;
		if (version == null)
		{
			sqlstring = "SELECT * FROM categories where id <> 2 order by ordering DESC";

		}
		else if( AppId.Equals("2") )
		{
			sqlstring = "SELECT * FROM categories where off=0 order by ordering DESC";
		}
		else 
		{
			sqlstring = "SELECT * FROM categories order by ordering DESC";
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