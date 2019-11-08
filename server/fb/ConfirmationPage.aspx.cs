using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Web;
using System.Web.SessionState;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.HtmlControls;
using MySql.Data.MySqlClient;


using System.Data.Odbc;
using System.Net;
using System.Text;
using System.IO;

public partial class MyWebForm1 : System.Web.UI.Page
{
    protected const int ADD_JOKE = 1;
    protected const int ADD_MESSAGE = 2;
    protected const int UNDER_CONSTRUCTION = 3;
    protected const int LIKE_US = 4;

    private void Page_Load(object sender, System.EventArgs e)
    {
        if (!IsPostBack)
        {
            string typeStr = Request.QueryString["type"];
            int typeInt = Convert.ToInt32(typeStr);
            switch (typeInt)
            {
                case ADD_JOKE:
                    message.Text = "<img id='confirmationText' alt='' src='images/site/add_joke_confirmation.PNG' />";
                    picture.Text = "<img id='confirmationPic' alt='' src='images/site/monkey-with-sunglasses-hi.png' />";
                    space1.Text = "<div style='height:50px'> </div>";
                    space2.Text = "<div style='height:50px'> </div>";
                    break;

                case ADD_MESSAGE:
                    message.Text = "<img id='confirmationText' alt='' src='images/site/add_message_confirmation.PNG' />";
                    picture.Text = "<img id='confirmationPic' alt='' src='images/site/monkey-wrench-hi.png' />";
                    space1.Text = "<div style='height:50px'> </div>";
                    space2.Text = "<div style='height:50px'> </div>";
                    break;

                case UNDER_CONSTRUCTION:
                    message.Text = "<img id='confirmationText' alt='' src='images/site/under_construction.PNG' />";
                    picture.Text = "<img id='confirmationPic' alt='' src='images/site/monkey-wrench-hi.png' />";
                    space1.Text = "<div style='height:50px'> </div>";
                    space2.Text = "<div style='height:50px'> </div>";
                    break;

                case LIKE_US:
                    message.Text = "<img id='confirmationText' alt='' src='images/site/like_us_message.PNG' />";
                    picture.Text = "<img id='confirmationPic' alt='' src='images/site/monkey-hi2.png' />";
                    space1.Text = "<div style='height:35px'> </div>";
                    space2.Text = "<div style='height:25px'> </div>";
                    break;

                default:
                    break;
            }
        }
    }
}
