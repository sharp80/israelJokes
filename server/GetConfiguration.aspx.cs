﻿﻿﻿﻿﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;


public partial class GetConfiguration : System.Web.UI.Page
{
    protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; ";

    protected void Page_Load(object sender, EventArgs e)
    {
        string AppId = Request.QueryString["AppId"];
		if (AppId == null)
			AppId = "";
		
		string VersionNum = Request.QueryString["version"];
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
			Response.Write("{");
			Response.Write("\"AlboAds\":\"false\",");
			//Response.Write("\"AlboAdditionalApi\":\"&sheffer=12\",");
			//Response.Write("\"YoutubPrefix\":\"http://www.youtube-nocookie.com/embed/\",");
			Response.Write("\"YoutubPrefix\":\"http://www.youtube.com/watch?v=\",");
			Response.Write("\"YoutubPrefixForFacebook\":\"https://www.youtube.com/v/\",");
			
			Response.Write("\"ImagesUrlPrefix\":\"http://israelijokes.mayaron.com/images/imagesstack/\",");
			Response.Write("\"SiteUrlPrefix\":\"https://israelijokes.apphb.com/server/\",");
			//Response.Write("\"IconsUrlPrefix\":\"http://israelijokes.mayaron.com/images/icons/\"");
			Response.Write("\"IconsUrlPrefix\":\"https://israelijokes.apphb.com/server/fb/images/icons/\"");	
			
			Response.Write("}");
		}

    }
}
