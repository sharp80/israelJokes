using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using Newtonsoft.Json;


public partial class RetrieveFromServer : System.Web.UI.Page
{
    protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; ";

    protected void Page_Load(object sender, EventArgs e)
    {
        //string sqlstring = "SELECT * FROM jokes where id=1";
        string sqlstring = "SELECT * FROM jokes ORDER BY RAND() LIMIT 10";

        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
        try
        {
            DataSet ds = new DataSet();
            dr.Fill(ds);
            foreach (DataRow row in ds.Tables[0].Rows)
            {
                 row["joke"] = HttpUtility.HtmlEncode(row["joke"]);
            }

            string jsonString = JsonConvert.SerializeObject(ds.Tables[0]);
            Response.Write( jsonString );
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
