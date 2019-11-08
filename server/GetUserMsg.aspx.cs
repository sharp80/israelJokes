using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using System.Configuration;


public partial class GetUserMsg : System.Web.UI.Page
{
    protected string cnString ;

    protected void Page_Load(object sender, EventArgs e)
    {
		cnString = ConfigurationSettings.AppSettings["connectionstring"].ToString();
        string AppId = Request.QueryString["AppId"];
		if (AppId == null)
			AppId = "";
		
		string VersionNum = Request.QueryString["AppVer"];
		int VersionInt = -1;
		if (VersionNum != null)
		{
			VersionInt = Int32.Parse( VersionNum );
		}
		if ( VersionInt == -1) // android <= 8 versions dont send version number
		{
			try
			{
				//Response.Write("AlboAds=true;");
				
			}
			catch (Exception _Exception)
			{
				//Response.Write(_Exception.ToString());
				//Response.Write(_Exception.ToString());
			}	
			finally
			{
			}
		}
		else
		{
			Response.Write("[");
			Response.Write("{");
			/*Response.Write("\"MsgId\":\"10\",");
			Response.Write("\"MsgStr\":\"דגכדגע\nמה המצב?\nדגעדגע\n \",");
			Response.Write("\"MsgTitle\":\"שי שפר היה פה\"");
			Response.Write("}");
			
			Response.Write(",{");
			Response.Write("\"MsgId\":\"11\",");
			Response.Write("\"MsgStr\":\"דעו הודעהn \",");
			Response.Write("\"MsgTitle\":\"ששדכ ש שד דשהכ\"");
			*/
			Response.Write("}");

			
			Response.Write("]");
			
		}


    }
}