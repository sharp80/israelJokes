using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;

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
using Facebook_Graph_Toolkit;
using System.Configuration;
using CloudinaryDotNet;
using CloudinaryDotNet.Actions;

//public partial class MyWebForm1 : CanvasPage
public partial class MyWebForm1 : System.Web.UI.Page
{
    protected string cnString;
    protected string okValueReturnFromServer = "Joke was successfully insert";

    private void Page_Load(object sender, System.EventArgs e)
    {
		cnString  = ConfigurationSettings.AppSettings["connectionstring"].ToString();
        if (!IsPostBack)
        {
            string sqlstring = "SELECT * from categories";
            MySqlConnection cn = new MySqlConnection(cnString);
            cn.Open();

            MySqlDataAdapter dr = new MySqlDataAdapter(sqlstring, cn);
            DataSet ds = new DataSet();
            dr.Fill(ds);
            cn.Close();
            Response.Cache.SetCacheability(System.Web.HttpCacheability.NoCache);

            this.AppCategory1.DataSource = ds;
            this.AppCategory1.DataBind();
        }
    }

    public void SubmitButton_Click(object sender, System.EventArgs e)
    {
        //lblOutput.Text = " ";
	    if (this.captchaText.Text!=null && Page.IsValid && (this.captchaText.Text.ToString() ==
            Session["randomStr"].ToString()))
        {
            // The Code to insert data

        }
        else
        {
            lblOutput.Text = "הכנס שוב את המספר המופיע בריבוע הירוק בבקשה";
            return;
        }

        string _JokePic;
		HttpPostedFile myFile = fileUpload.PostedFile;
        int res = btnUpload_Click(myFile, sender, e);

        // File is not a valid picture file (JPG/JPEG/PNG/GIF)
        if (res == -1 )
        {
            return;
        }
        // There is no pic to upload - the field is empty
        else if (res == 1)
        {
            _JokePic = "";
        }
        // A picture was upload successfully
        else
        {
            _JokePic = fileUpload.PostedFile.FileName;
        }		
        string _JokeHeadline = this.headline.Text;
        string _JokeText = this.TheJoke.Text;
        
        string _JokeCategory = "none";
        if (AppCategory1.SelectedIndex > -1)
        {
            _JokeCategory = AppCategory1.SelectedItem.Value;
        }

        string _userName = userNameHidden.Value;

        string _JokeVideo = this.uTubeLink.Text;
        if (_JokeVideo != "")
        {
            _JokeVideo = _GetVideoCodeFromURL(_JokeVideo);
            if (_JokeVideo.Equals(""))
            {
                lblOutput.Text = "כתובת הוידאו איננה כתובת חוקית של אתר יוטיוב.";
                return;
            }
        }

        string result = _PostDataToServer(_JokeHeadline, _JokeText, _JokeCategory, _JokePic, _JokeVideo, _userName);

        // if we received from the server the OK value it means that the joke was insert successfully
        int firstChr = result.IndexOf(okValueReturnFromServer); 
        if (firstChr != -1)
        {
            //lblOutput.Text = "הבדיחה נקלטה במערכת. תודה!";
            //Facebook_Graph_Toolkit.Helpers.IframeHelper.IframeRedirect("ConfirmationPage.aspx?type=1", true, true);
            Response.Redirect(ConfigurationSettings.AppSettings["siteUrl"].ToString()+"fb/ConfirmationPage.aspx?type=1");
        }
        // else there was some kind of a problem inserting the joke into the DB
        else
        {
            lblOutput.Text = "הבדיחה לא נקלטה, אנא נסה שנית.";
        }

        FormReset();
    }

    private void FormReset()
    {
        AppCategory1.SelectedIndex = 0;
        this.headline.Text = "";
        this.TheJoke.Text = "";
        this.captchaText.Text = "";
    }

    private int btnUpload_Click(HttpPostedFile myFile, object sender, System.EventArgs e)
    {
        // Initialize variables
        string sSavePath;
        string sThumbExtension;
        int intThumbWidth;
        int intThumbHeight;

        // Set constant values
        sSavePath = "../images/imagesStack/";
        sThumbExtension = "";
        intThumbWidth = 160;
        intThumbHeight = 64;

        // TODO:: It is always not NULL. What does it check?
        // If file field isn’t empty
        if (myFile != null)
        {
            // Check file size (mustn’t be 0)
            int nFileLen = myFile.ContentLength;
            if (nFileLen == 0)
            {
                lblOutput.Text = "Corrupted File. File size is 0.";
                // There is no file upload. Return.
                return 1;
            }

            // Check file extension (must be JPG/PNG)
            if (System.IO.Path.GetExtension(myFile.FileName).ToLower() != ".jpg" &&
                System.IO.Path.GetExtension(myFile.FileName).ToLower() != ".jpeg" &&
                System.IO.Path.GetExtension(myFile.FileName).ToLower() != ".png" &&
                System.IO.Path.GetExtension(myFile.FileName).ToLower() != ".gif")
            {
                lblOutput.Text = "הקובץ חייב להיות אחד מהסוגים הבאים: JPG/JPEG/PNG/GIF";
                return -1;
            }

            // Read file into a data stream
            byte[] myData = new Byte[nFileLen];
            myFile.InputStream.Read(myData, 0, nFileLen);

            // Make sure a duplicate file doesn’t exist.  If it does, keep on appending an incremental numeric until it is unique
            // TODO:: How does he check it here??
            string sFilename = System.IO.Path.GetFileName(myFile.FileName);
			//lblOutput.Text =sFilename;
            int file_append = 0;
            while (System.IO.File.Exists(Server.MapPath(sSavePath + sFilename)))
            {
                file_append++;
                sFilename = System.IO.Path.GetFileNameWithoutExtension(myFile.FileName) + file_append.ToString() + ".jpg";
            }

            // Save the stream to disk
			//lblOutput.Text = Server.MapPath(sSavePath + sFilename);
			string filePath = Server.MapPath(sSavePath + sFilename);
            System.IO.FileStream newFile = new System.IO.FileStream(filePath, System.IO.FileMode.Create);
            newFile.Write(myData, 0, myData.Length);
            newFile.Close();
            Account account = new Account(
                ConfigurationSettings.AppSettings["cloudinary_cloud_name"].ToString(),
                ConfigurationSettings.AppSettings["cloudinary_api_key"].ToString(),
                ConfigurationSettings.AppSettings["cloudinary_api_secret"].ToString()
			);

            Cloudinary cloudinary = new Cloudinary(account);
            var uploadParams = new ImageUploadParams()
            {
                File = new FileDescription(filePath),
                PublicId = "imagesStack/"+System.IO.Path.GetFileNameWithoutExtension(sFilename)
            };
            var uploadResult = cloudinary.Upload(uploadParams);
			// Check whether the file is really a JPEG by opening it
            System.Drawing.Image.GetThumbnailImageAbort myCallBack = 
					new System.Drawing.Image.GetThumbnailImageAbort(ThumbnailCallback);
            Bitmap myBitmap;
            try
            {
                myBitmap = new Bitmap(filePath);

                // If jpg file is a jpeg, create a thumbnail filename that is unique.
                string sThumbFile = System.IO.Path.GetFileNameWithoutExtension(myFile.FileName) + sThumbExtension + ".jpg";

                // Save thumbnail and output it onto the webpage
               /* System.Drawing.Image myThumbnail = myBitmap.GetThumbnailImage
				(
					intThumbWidth, 
					intThumbHeight, 
					myCallBack, 
					IntPtr.Zero
				);
                myThumbnail.Save(Server.MapPath(sSavePath + sThumbFile));*/
				System.Drawing.Image OriginalImage = myBitmap;  
				int MaxHeight = intThumbHeight ;
				int NewWidth = intThumbWidth;
				MyCreateThumb
				(
					OriginalImage, 
					NewWidth,
					MaxHeight,
					Server.MapPath(sSavePath  + "/thumbs/"+ sThumbFile)
				);
                var uploadParamsThumb = new ImageUploadParams()
                {
                    File = new FileDescription(Server.MapPath(sSavePath + "/thumbs/" + sThumbFile)),
                    PublicId = "imagesStack/thumbs/" + System.IO.Path.GetFileNameWithoutExtension(sFilename)
                };
                cloudinary.Upload(uploadParamsThumb);
                //imgPicture.ImageUrl = sSavePath + sThumbFile;

                // Displaying success information
                //lblOutput.Text = "File uploaded successfully!";

                // Destroy objects
                //myThumbnail.Dispose();
                myBitmap.Dispose();
            }
            catch (ArgumentException errArgument)
            {
                // The file wasn't a valid jpg file
                //lblOutput.Text = "The file wasn't a valid jpg file. errArgument = " + errArgument;
                lblOutput.Text = "הקובץ לא תקין";
                System.IO.File.Delete(Server.MapPath(sSavePath + sFilename));
                return -1;
            }
        }
        else //fileUpload.PostedFile == null
        {
            // There is no file upload. Return.
            return 1;
        }
        return 0;
    }
	
	public void MyCreateThumb
	(
		System.Drawing.Image OriginalImage, 
		int MaxWidth, 
		int NewHeight,
		string outputFile
	)
	{
		System.Drawing.Image FullsizeImage = OriginalImage;

		// Prevent using images internal thumbnail
		FullsizeImage.RotateFlip(System.Drawing.RotateFlipType.Rotate180FlipNone);
		FullsizeImage.RotateFlip(System.Drawing.RotateFlipType.Rotate180FlipNone);

		int NewWidth = FullsizeImage.Width * NewHeight / FullsizeImage.Height;
		if (NewWidth > MaxWidth)
		{
			// Resize with height instead
			NewHeight = FullsizeImage.Height * MaxWidth / FullsizeImage.Width;
			NewWidth = MaxWidth;
		}

		System.Drawing.Image NewImage = FullsizeImage.GetThumbnailImage(NewWidth, NewHeight, null, IntPtr.Zero);

		// Clear handle to original file so that we can overwrite it if necessary
		FullsizeImage.Dispose();

		// Save resized picture
		// Use our method to retrieve a JPEG encoder
		ImageCodecInfo jpegEncoder = GetImageEncoder("JPEG");
		// Set the compression parameter of our encoder
		EncoderParameters parms = new EncoderParameters(1);
		parms.Param[0] = new EncoderParameter(System.Drawing.Imaging.Encoder.Compression, 40);
		NewImage.Save(outputFile, jpegEncoder, parms);
	}

    public bool ThumbnailCallback()
    {
        return false;
    }

    private string _GetVideoCodeFromURL(string a_videoURL)
    {
        //string videoCode = this.uTubeLink.Text;
        string videoCode = a_videoURL;
        int firstChr = videoCode.IndexOf("=");
        if (firstChr <= 0)
        {
            // Not a valid uTube URL
            return "";
        }
        int lastChr = videoCode.IndexOf("&");

        firstChr++;
        if (lastChr == -1)
        {
            // No '&' in the string
            videoCode = videoCode.Substring(firstChr);
        }
        else
        {
            // There is a '&' after the video code
            videoCode = videoCode.Substring(firstChr, lastChr - firstChr);
        }
        return videoCode;
    }

 /// <summary>
        /// Obtain an image encoder suitable for a specific graphics format.
        /// </summary>
        /// <param name="imageType">One of: BMP, JPEG, GIF, TIFF, PNG.</param>
        /// <returns>An ImageCodecInfo corresponding with the type requested,
        /// or null if the type was not found.</returns>
        private ImageCodecInfo GetImageEncoder(string imageType)
        {
            imageType = imageType.ToUpperInvariant();
            foreach (ImageCodecInfo info in ImageCodecInfo.GetImageEncoders())
            {
                if (info.FormatDescription == imageType)
                {
                    return info;
                }
            }
            return null;
        }
    private string _PostDataToServer(string a_headline, string a_jokeText, string a_jokeCategory, string a_jokePic, string a_jokeVideo, string a_userName)
    {
        // Create a request using a URL that can receive a post. 
        WebRequest request = WebRequest.Create(ConfigurationSettings.AppSettings["siteUrl"].ToString()+"AddNewJoke.aspx");

        // Set the Method property of the request to POST.
        request.Method = "POST";

        // Create POST data and convert it to a byte array.
        string postData = "JokeHeadline=" + a_headline;
        postData += "&JokeText=" + a_jokeText;
        postData += "&JokeCategory=" + a_jokeCategory;
        postData += "&PicName=" + a_jokePic;
        postData += "&VideoUrl=" + a_jokeVideo;
        postData += "&UserName=" + a_userName;
        byte[] byteArray = Encoding.UTF8.GetBytes(postData);

        // Set the ContentType property of the WebRequest.
        request.ContentType = "application/x-www-form-urlencoded";

        // Set the ContentLength property of the WebRequest.
        request.ContentLength = byteArray.Length;

        // Get the request stream.
        Stream dataStream = request.GetRequestStream();

        // Write the data to the request stream.
        dataStream.Write(byteArray, 0, byteArray.Length);

        // Close the Stream object.
        dataStream.Close();

        // Get the response.
        WebResponse response = request.GetResponse();

        // Display the status.
        //Console.WriteLine(((HttpWebResponse)response).StatusDescription);

        // Get the stream containing content returned by the server.
        dataStream = response.GetResponseStream();

        // Open the stream using a StreamReader for easy access.
        StreamReader reader = new StreamReader(dataStream);

        // Read the content.
        string responseFromServer = reader.ReadToEnd();

        // Display the content.
        //Console.WriteLine(responseFromServer);
        //lblOutput.Text = responseFromServer;

        // Clean up the streams.
        reader.Close();
        dataStream.Close();
        response.Close();

        return responseFromServer;
    }

}
