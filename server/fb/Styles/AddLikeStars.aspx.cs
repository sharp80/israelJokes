using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

using MySql.Data.MySqlClient;
using System.Data;
using Newtonsoft.Json;


public class ReviewEntry
{
	public double reviewStars{ get; set; }
};

public partial class AddLikeStars : System.Web.UI.Page
{
    protected string cnString = "Data Source=localhost; DataBase=jokes; User ID=jokes_user; Password=jokesuser1; ";

    protected void Page_Load(object sender, EventArgs e)
    {
        string jokeId = Request.QueryString["jokeId"];
		string ratingFromUser = Request.QueryString["rating"];
        MySqlConnection cn = new MySqlConnection(cnString);

        try
        {
		
			DateTime dt = DateTime.Now;
			string date = String.Format("{0:s}", dt);
			string sqlstringInsert = "INSERT INTO jokesRating(JokeId,JokeRating,DateAdded) Values(" ;
			sqlstringInsert += jokeId+ ", '" + ratingFromUser + "','" + date + "')" ;
			//Response.Write(sqlstringInsert + "<br>");
			cn.Open();
            MySqlCommand command = new MySqlCommand(sqlstringInsert, cn);
            command.ExecuteNonQuery();
		    cn.Close();

		    string sqlstringSelect = "SELECT * FROM jokesRating where JokeId=" + jokeId + " order by DateAdded";
			MySqlDataAdapter dr = new MySqlDataAdapter(sqlstringSelect, cn);
            DataSet ds = new DataSet();

			dr.Fill(ds, "jokes");
			double oldRating = 0;
			double newRating = 0;
			int oldRatingNum = 0;
			int newRatingNum = 0;
			foreach( DataRow row in ds.Tables["jokes"].Rows )
			{
				String dateStr = row["DateAdded"].ToString() ;
				DateTime dateAdded = DateTime.Parse(dateStr);
				double rowRate =  double.Parse( row["JokeRating"].ToString()  );
				
				if ( dateAdded < DateTime.Now.AddDays(-7) )
				{
					oldRating += rowRate ;
					oldRatingNum++;
				}
				else
				{
					newRating += rowRate;                                     
					newRatingNum++;
				}
			}
			double totalRating = 0.0;
			if ( newRating > 0 && oldRatingNum > 0)
				totalRating = (double)newRating/ (double)newRatingNum * 0.7 + (double)oldRating / (double)oldRatingNum * 0.3;
			else if ( newRating > 0 )
				totalRating = (double)newRating/ (double)newRatingNum ;
			else if (oldRatingNum > 0 )
				totalRating = (double)oldRating  / (double)oldRatingNum;
			//Response.Write(totalRating + "<br>");
			
			
			string sqlstringUpdate = "UPDATE jokes set StarRating='"+totalRating+"' where id=" + jokeId ;
			//Response.Write(sqlstringUpdate + "<br>");
			cn.Open();
            MySqlCommand commandUpdate = new MySqlCommand(sqlstringUpdate, cn);
            commandUpdate.ExecuteNonQuery();
		    cn.Close();
			
			ReviewEntry obj = new ReviewEntry();
			obj.reviewStars = totalRating;
			Response.Write(JsonConvert.SerializeObject( obj ) );

		/*
            DataSet ds = new DataSet();
            dr.Fill(ds, "jokes");

            DataRow row = ds.Tables["jokes"].Rows[0];
            //Response.Write(row["rating"].ToString());
            int rating = Int32.Parse( row["rating"].ToString() );
            rating++;
            string sqlstringUpdate = "UPDATE jokes SET rating = " + rating + " where id =" + jokeId;

            cn.Open();
            MySqlCommand command = new MySqlCommand(sqlstringUpdate, cn);
            command.ExecuteNonQuery();*/
        
       
            //  Explicitly dispose the SelectCommand instance
            dr.SelectCommand.Dispose();
            dr.Dispose();
        }
		catch(Exception _Exception)
		{
			Response.Write(_Exception.ToString());
		}

        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }
}