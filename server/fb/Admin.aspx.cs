using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using MySql.Data.MySqlClient;
using System.Data;
using Newtonsoft.Json;
using System.Configuration;

public partial class Admin : System.Web.UI.Page
{
    public int NumOfFields;
	
	protected string cnString ;
    protected string action = "";
    protected enum JokeFields
    {
        JOKE_ID,
        HEADLINE,
        JOKE_TEXT,
        PIC_NAME,
        VIDEO_URL,
        CATEGORY_ID,
        RATING,
        STATUS,
        USER_NAME,
        USER_EMAIL,
		NUM_OF_JOKE_FIELDS
    };

    protected enum MessageFields
    {
        MESSAGE_ID,
        JOKE_ID,
        MESSAGE_TEXT
    };
	
	protected int NUM_OF_EMPTY_JOKES_TO_ADD = 5;

    protected void Page_Load(object sender, EventArgs e)
    {
		cnString  = ConfigurationSettings.AppSettings["connectionstring"].ToString();

        if (!IsPostBack)
        {
            action = Request.QueryString["id"];
            if (_IsActionValid())
            {
                /*
                buttons.Text = "<td><asp:Button id='SubmitButton' runat='server' Text='Submit' Width='110px' onclick='SubmitButton_Click' ></asp:Button></td>" +
                                "<td><asp:Button id='Button1' runat='server' Text='Display All Messages' onclick='MessagesButton_Click' ></asp:Button></td>" +
                                "<td><asp:Button id='Button2' runat='server' Text='Clear All' onclick='ClearButton_Click' ></asp:Button></td>";
                */
                string sqlstring = "SELECT * from categories";
                MySqlConnection cn = new MySqlConnection(cnString);
                cn.Open();

                MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
                DataSet ds = new DataSet();
                dr.Fill(ds);
                cn.Close();
                Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);

                DataRow newCategoryRow = ds.Tables[0].NewRow();

                newCategoryRow["id"] = "100";
                newCategoryRow["name"] = "All";

                ds.Tables[0].Rows.Add(newCategoryRow);

                this.JokeCategory.DataSource = ds;
                this.JokeCategory.DataBind();
            }
            else
            {
                SubmitButton.Enabled = false;
                MessageButton.Enabled = false;
                ClearButton.Enabled = false;
                UpdateButton.Enabled = false;
                DeleteButton.Enabled = false;
                DeleteDlJokesButton.Enabled = false;
                DeleteReadJokesButton.Enabled = false;
				AddJokeButton.Enabled = false;
				AddButton.Enabled = false;
            }
        }
    }

    public void SubmitButton_Click(object sender, System.EventArgs e)
    {
        _LoadPage();      
    }

    public void MessagesButton_Click(object sender, System.EventArgs e)
    {
        _DisplayAllMessagesFromDB();
    }

	public void AddJokesButton_Click(object sender, System.EventArgs e)
	{
		_CreateEmptyRowsForAddingJokes();
	}
	
    public void ClearButton_Click(object sender, System.EventArgs e)
    {
        _ClearAll();
    }

    public void ReloadPage(object sender, System.EventArgs e)
    {
        string sqlStringToApply = sqlString.Value;
        if (sqlStringToApply.Equals("ABORT") == false)
        {
            MySqlConnection conDatabase = new MySqlConnection(cnString);
            MySqlCommand cmdDatabase = new MySqlCommand(sqlStringToApply, conDatabase);
            conDatabase.Open();
            cmdDatabase.ExecuteNonQuery();
            conDatabase.Close();

            //debug.Text = sqlString.Value;
            //_ClearAll();
            if (tableType.Value == "jokes")
            {
                _LoadPage();
            }
			else if (tableType.Value == "AddJokes")
			{
				_CreateEmptyRowsForAddingJokes();
			}
            else
			{
                _DisplayAllMessagesFromDB();
            }
            
        }
        else
        {
            NumOfFields = Convert.ToInt32(numOfFields.Value);
        }
    }


    private void _LoadPage()
    {
        string sqlstring = _CreateSqlString();

        MySqlConnection cn = new MySqlConnection(cnString);
        MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
        try
        {
            int rowNum = 0;
            int colNum;
            string colstr;
            string str = "";
            DataSet ds = new DataSet();
            dr.Fill(ds, "jokes");
            str += "<table border='1' bgcolor=Ivory>";
            str += "<tr>";
            str += "<td dir=ltr>Checked</td>";
            str += "<td dir=ltr>Joke ID</td>";
            str += "<td dir=ltr>Headline</td>";
            str += "<td dir=ltr>Joke</td>";
            str += "<td dir=ltr>Pic Name</td>";
            str += "<td dir=ltr>Video URL</td>";
            str += "<td dir=ltr>Category ID</td>";
            str += "<td dir=ltr>Rating</td>";
            str += "<td dir=ltr>Status</td>";
            str += "<td dir=ltr>User Name</td>";
            str += "<td dir=ltr>User Email</td>";
            str += "</tr>";
            foreach (DataRow row in ds.Tables["jokes"].Rows)
            {
                colNum = 0;
                str += "<tr>";
                str += "<td><input type='checkbox' id='checkboxRow" + rowNum + "' /></td>";
                foreach (DataColumn col in row.Table.Columns)
                {
                    str += "<td dir=ltr>";
                    colstr = row[col.ToString()].ToString();
                    switch (colNum)
                    {
                        case (int)JokeFields.JOKE_ID:
                            str += "<input type='text' size='5' id='jokeIdField" + rowNum + "' value='" + colstr + "' readonly='readonly'></input>";
                            break;

                        case (int)JokeFields.HEADLINE:
                            str += "<textarea id='jokeHeader" + rowNum + "' rows='1' cols='30'>" + colstr + "</textarea>";
                            break;

                        case (int)JokeFields.JOKE_TEXT:
                            str += "<textarea id='jokeText" + rowNum + "' rows='5' cols='30'>" + colstr + "</textarea>";
                            break;
							
						case (int)JokeFields.PIC_NAME:
							str += "<textarea id='picName" + rowNum + "' rows='1' cols='30'>" + colstr + "</textarea>";
							break;

                        case (int)JokeFields.CATEGORY_ID:
                            str += "<input type='text' id='categoryIdInput" + rowNum + "' size='3' value='" + colstr + "'></input>";
                            break;

                        case (int)JokeFields.RATING:
                            str += "<input type='text' id='ratingInput" + rowNum + "' size='3' value='" + colstr + "'></input>";
                            break;

                        case (int)JokeFields.STATUS:
                            if (colstr.Equals("Active") == true)
                            {
                                str += "<select id='jokeStatus" + rowNum + "'><option value='Active' selected='selected'>Active</option><option value='Pending'>Pending</option></select>";
                            }
                            else
                            {
                                str += "<select id='jokeStatus" + rowNum + "'><option value='Active'>Active</option><option value='Pending' selected='selected'>Pending</option></select>";
                            }
                            break;

                        case (int)JokeFields.USER_NAME:
                            str += "<input type='text' id='userName" + rowNum + "' value='" + colstr + "'></input>";
                            break;

                        case (int)JokeFields.USER_EMAIL:
                            str += "<input type='text' id='userEmail" + rowNum + "' value='" + colstr + "'></input>";
                            break;

                        default:
                            str += "<input type='text' value='" + colstr + "'></input>";
                            break;
                    }

                    str += "&nbsp;</td>";
                    colNum++;
                }

                str += "</tr>";

                rowNum++;
            }

            str += "</table>";
            //Response.Write("Total number of rows:" + rowNum);
            numberOfJokes.Text = "Total number of rows:" + rowNum;
            jokes.Text = str;
            //Response.Write(str);

            NumOfFields = rowNum;
            tableType.Value = "jokes";
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


    private string _CreateSqlString()
    {
        string answer = "";

        string _JokeId = this.JokeId.Text;       
        bool isJokeIdEmpty = string.Compare(_JokeId, "") == 0;

        //Response.Write(isEqual);

        if (isJokeIdEmpty)
        {
            answer = _CreateSqlStringForCategory();
        }
        else
        {
            answer = _CreateSqlStringForSpecificJoke(_JokeId);
        }

        return answer;
    }

    private string _CreateSqlStringForCategory()
    {
        string sqlstring = "SELECT * FROM jokes";

        string _JokeCategory = "none";
        if (JokeCategory.SelectedIndex > -1)
        {
            _JokeCategory = JokeCategory.SelectedItem.Value;
        }

        string _JokeStatus = "none";
        if (JokeStatus.SelectedIndex > -1)
        {
            _JokeStatus = JokeStatus.SelectedItem.Value;
        }

        if ((_JokeCategory != "100") && (_JokeCategory != "none"))
        {
            sqlstring = sqlstring + " where jokes.categoryId = " + _JokeCategory;
        }

        if ((_JokeStatus != "ALL") && (_JokeStatus != "none"))
        {
            if (_JokeCategory != "100")
            {
                sqlstring = sqlstring + " and jokes.status = '" + _JokeStatus + "'";
            }
            else
            {
                sqlstring = sqlstring + " where jokes.status = '" + _JokeStatus + "'";
            }
        }

        return sqlstring;
    }


    private string _CreateSqlStringForSpecificJoke(string a_JokeId)
    {
        string sqlstring = "SELECT * FROM jokes where id = " + a_JokeId;
        return sqlstring;
    }
    
   
    private void _DisplayAllMessagesFromDB()
    {
        string messagesSqlString = "SELECT * from messages";
        MySqlConnection messagesCn = new MySqlConnection(cnString);
        MySqlDataAdapter messagesDr = new MySqlDataAdapter(messagesSqlString, messagesCn);

        int rowNum = 0;
        int colNum;
        string colstr;
        string str = "";
        DataSet messagesDs = new DataSet();
        messagesDr.Fill(messagesDs, "messages");
        str += "<table border='1' bgcolor=Ivory>";
        str += "<tr>";
        str += "<td dir=ltr>Checked</td>";
        str += "<td dir=ltr>Message ID</td>";
        str += "<td dir=ltr>Joke ID</td>";
        str += "<td dir=ltr>Message Text</td>";
        str += "<td dir=ltr>Date Added</td>";
        str += "</tr>";
        foreach (DataRow row in messagesDs.Tables["messages"].Rows)
        {
            colNum = 0;
            str += "<tr>";
            str += "<td><input type='checkbox' id='checkboxRow" + rowNum + "' /></td>";
            foreach (DataColumn col in row.Table.Columns)
            {
                str += "<td dir=ltr>";
                colstr = row[col.ToString()].ToString();
                switch (colNum)
                {
                    case (int)MessageFields.MESSAGE_ID:
                        str += "<input type='text' size='5' id='messageIdField" + rowNum + "' value='" + colstr + "' readonly='readonly'></input>";
                        break;

                    case (int)MessageFields.JOKE_ID:
                        str += "<input type='text' size='5' id='jokeIdField" + rowNum + "' value='" + colstr + "' readonly='readonly'></input>";
                        break;

                    case (int)MessageFields.MESSAGE_TEXT:
                        str += "<textarea id='messageText" + rowNum + "' rows='2' cols='30' readonly='readonly'>" + colstr + "</textarea>";
                        break;

                    default:
                        str += "<input type='text' value='" + colstr + "' readonly='readonly'></input>";
                        break;
                }
                str += "&nbsp;</td>";
                colNum++;
            }
            str += "</tr>";

            rowNum++;
        }

        str += "</table>";
        //Response.Write("Total number of rows:" + rowNum);
        numberOfMessages.Text = "Total number of rows:" + rowNum;
        messages.Text = str;
        NumOfFields = rowNum;
        tableType.Value = "messages";

        messagesDr.SelectCommand.Dispose();
        messagesDr.Dispose();
        messagesCn.Close();

        Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);
    }
	
	private void _CreateEmptyRowsForAddingJokes()
	{
		int jokeId;
		int rowNum;
		
		string str = "";
		str += "<table border='1' bgcolor=Ivory>";
		str += "<tr>";
		str += "<td dir=ltr>Checked</td>";
		str += "<td dir=ltr>Joke ID</td>";
		str += "<td dir=ltr>Headline</td>";
		str += "<td dir=ltr>Joke</td>";
		str += "<td dir=ltr>Pic Name</td>";
		str += "<td dir=ltr>Video URL</td>";
		str += "<td dir=ltr>Category ID</td>";
		str += "<td dir=ltr>Rating</td>";
		str += "<td dir=ltr>Status</td>";
		str += "<td dir=ltr>User Name</td>";
		str += "<td dir=ltr>User Email</td>";
		str += "</tr>";
		for(rowNum = 0; rowNum < NUM_OF_EMPTY_JOKES_TO_ADD; rowNum++)
		{
			str += "<tr>";
			str += "<td><input type='checkbox' id='checkboxRow" + rowNum + "' /></td>";
			for(int colNum = 0; colNum < (int)JokeFields.NUM_OF_JOKE_FIELDS; colNum++)
			{
				str += "<td dir=ltr>";
				switch (colNum)
				{
					case (int)JokeFields.JOKE_ID:
						jokeId = rowNum + 1;
						str += "<input type='text' size='5' id='jokeIdField" + rowNum + "' value='" + jokeId + "' readonly='readonly'></input>";
						break;

					case (int)JokeFields.HEADLINE:
						str += "<textarea id='jokeHeader" + rowNum + "' rows='1' cols='30'></textarea>";
						break;

					case (int)JokeFields.JOKE_TEXT:
						str += "<textarea id='jokeText" + rowNum + "' rows='5' cols='30'></textarea>";
						break;
						
					case (int)JokeFields.PIC_NAME:
						str += "<textarea id='picName" + rowNum + "' rows='1' cols='30'></textarea>";
						break;
						
					case (int)JokeFields.VIDEO_URL:
						str += "<textarea id='videoUrl" + rowNum + "' rows='1' cols='30'></textarea>";
						break;

					case (int)JokeFields.CATEGORY_ID:
						str += "<input type='text' id='categoryIdInput" + rowNum + "' size='3'></input>";
						break;

					case (int)JokeFields.RATING:
						str += "<input type='text' id='ratingInput" + rowNum + "' size='3'></input>";
						break;

					case (int)JokeFields.STATUS:
						str += "<select id='jokeStatus" + rowNum + "'><option value='Active'>Active</option><option value='Pending' selected='selected'>Pending</option></select>";
						break;

					case (int)JokeFields.USER_NAME:
						str += "<input type='text' id='userName" + rowNum + "'></input>";
						break;

					case (int)JokeFields.USER_EMAIL:
						str += "<input type='text' id='userEmail" + rowNum + "'></input>";
						break;

					default:
						str += "<input type='text'></input>";
						break;
				}

				str += "&nbsp;</td>";
			}
			
			str += "</tr>";
		}

		str += "</table>";
		//Response.Write("Total number of rows:" + rowNum);
		//numberOfJokes.Text = "Total number of rows:" + rowNum;
		
		NumOfFields = rowNum;
		jokes.Text = str;
		tableType.Value = "AddJokes";
		
		SubmitButton.Enabled = false;
		MessageButton.Enabled = false;
		//ClearButton.Enabled = false;
		UpdateButton.Enabled = false;
		DeleteButton.Enabled = false;
		DeleteDlJokesButton.Enabled = false;
		DeleteReadJokesButton.Enabled = false;
		//AddJokeButton.Enabled = false;
		//AddButton.Enabled = false;
            	
	}

    private Boolean _IsActionValid()
    {
        if ((action == null) || (action.Equals("benomri") == false))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private void _ClearAll()
    {
        jokes.Text = "";
        numberOfJokes.Text = "";
        messages.Text = "";
        numberOfMessages.Text = "";
        JokeId.Text = "";
        tableType.Value = "";
		
		SubmitButton.Enabled = true;
		MessageButton.Enabled = true;
		ClearButton.Enabled = true;
		UpdateButton.Enabled = true;
		DeleteButton.Enabled = true;
		DeleteDlJokesButton.Enabled = true;
		DeleteReadJokesButton.Enabled = true;
		AddJokeButton.Enabled = true;
		AddButton.Enabled = true;
    }
  
}