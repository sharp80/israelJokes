using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using Newtonsoft.Json;


public class BannerEntry
{
	public string image{ get; set; }
	public string clickURL{ get; set; }
	public string desc{ get; set; }

};

public partial class getbanners : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
		string ind = Request.QueryString["ind"];
		bool showAll = false;
		if (ind == null)
		{
			showAll = true;
		}
			
		
		string[] pkgArr = { "com.AppsBoard",
							"com.WhoAmI2",
							"com.IsraelTrivia",
							"com.IsraeliJokes",
							"com.CountriesTrivia"
							};
		string[] imageArr = {"http://jokes.mayaron.com/countries/appsBoard.png",
								"http://jokes.mayaron.com/countries/whoAmI.jpg",
								"http://jokes.mayaron.com/countries/trivia.jpg",
								"http://jokes.mayaron.com/countries/jokes.png",
								"http://jokes.mayaron.com/countries/countries.jpg"
								};
		string[] links = {	"https://play.google.com/store/apps/details?id=com.AppsBoard",
							"https://play.google.com/store/apps/details?id=com.WhoAmI2",
							"https://play.google.com/store/apps/details?id=com.IsraelTrivia",
							"https://play.google.com/store/apps/details?id=com.IsraeliJokes",
							"https://play.google.com/store/apps/details?id=com.CountriesTrivia"
							};
		string[] desc ={"לוח האפליקציות הישראלי",
						"מי בתמונה?",
						"טריויה ישראלית",
						"צחוקים",
						"מדינות ודגלים"
						};
		if (  showAll == false  )
		{
			int index = int.Parse(ind);
			BannerEntry bannerEntry = new BannerEntry();

			bannerEntry.image = imageArr[index];
			bannerEntry.clickURL = links[index];
			bannerEntry.desc		= desc[index];

			Response.Write(JsonConvert.SerializeObject( bannerEntry ));

		}
		else
		{
			List<BannerEntry> list = new List<BannerEntry>();
			string userPkg = Request.QueryString["pkg"];

			for (int index = 0 ; index < imageArr.Length ; index++)
			{
				if ( userPkg == pkgArr[index] ) 
					continue;
				BannerEntry bannerEntry = new BannerEntry();
				bannerEntry.image = imageArr[index];
				bannerEntry.clickURL = links[index];
				bannerEntry.desc		= desc[index];
				list.Add(bannerEntry);
			}
			Response.Write(JsonConvert.SerializeObject( list ));

		}
		Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);

    }
}