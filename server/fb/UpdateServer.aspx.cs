using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using MySql.Data.MySqlClient;

public partial class UpdateServer : System.Web.UI.Page
{
    protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; ";

    protected void Page_Load(object sender, EventArgs e)
    {    
        string sqlString = null;
        if (Request.QueryString["action"] == "ADD_JOKE")
        {
            sqlString = "insert into jokes(joke) value('" + Request.QueryString["jokeText"] + "')";
            Response.Write(sqlString);
        }

        MySqlConnection cn = new MySqlConnection(cnString);

        try
        {
            cn.Open();
            MySqlCommand command = new MySqlCommand(sqlString, cn);
            command.ExecuteNonQuery();
        }
        catch (Exception _Exception)
        {
            Response.Write(_Exception.ToString());
        }
        finally
        {
            cn.Close();
        }
    }
}