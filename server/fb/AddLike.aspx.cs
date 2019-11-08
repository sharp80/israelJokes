using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;


public partial class AddLike : System.Web.UI.Page
{
    protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; ";

    protected void Page_Load(object sender, EventArgs e)
    {
        string jokeId = Request.QueryString["jokeId"];
        string sqlstringSelect = "SELECT * FROM jokes where id=" + jokeId;

        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter dr = new MySqlDataAdapter(sqlstringSelect, cn);

        try
        {
            DataSet ds = new DataSet();
            dr.Fill(ds, "jokes");

            DataRow row = ds.Tables["jokes"].Rows[0];
            //Response.Write(row["rating"].ToString());
            int rating = Int32.Parse( row["rating"].ToString() );
            rating++;
            string sqlstringUpdate = "UPDATE jokes SET rating = " + rating + " where id =" + jokeId;

            cn.Open();
            MySqlCommand command = new MySqlCommand(sqlstringUpdate, cn);
            command.ExecuteNonQuery();
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