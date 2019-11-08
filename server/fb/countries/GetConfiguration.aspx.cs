using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using Newtonsoft.Json;


public class ConfigurationEntry
{
	public bool AlboBanners{ get; set; }
	public int numOfBanners{ get; set; }

};

public partial class GetConfiguration : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
		ConfigurationEntry configurationEntry = new ConfigurationEntry();
		configurationEntry.AlboBanners = false;
		configurationEntry.numOfBanners = 4;
		Response.Write(JsonConvert.SerializeObject( configurationEntry ));

	    Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }
}