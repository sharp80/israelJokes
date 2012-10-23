﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using System.Configuration;


public partial class GetConfiguration : System.Web.UI.Page
{
    protected string cnString ;

    protected void Page_Load(object sender, EventArgs e)
    {
		cnString = ConfigurationSettings.AppSettings["connectionstring"].ToString();
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
			Response.Write("\"YoutubPrefix\":\"http://www.youtube-nocookie.com/embed/\",");
			Response.Write("\"YoutubPrefixForFacebook\":\"https://www.youtube.com/v/\",");
			Response.Write("\"ImagesUrlPrefix\":\"http://img.zappos.co.il/imageresize2.aspx?i=http://jokes.mayaron.com/Images/ImagesStack/\"");
			Response.Write("}");
		}

    }
}