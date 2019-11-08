using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Net;
using System.Text;
using System.IO;

public partial class Debug : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {
        // Create a request using a URL that can receive a post. 
        WebRequest request = WebRequest.Create("http://jokes.mayaron.com/AddNewJoke.aspx");

        // Set the Method property of the request to POST.
        request.Method = "POST";

        // Create POST data and convert it to a byte array.
        string postData = "JokeHeadline=Test";
        postData += "&JokeText=Tes1";
        postData += "&JokeCategory=1";
        postData += "&PicName=''";
        postData += "&VideoUrl=''";
        postData += "&UserName=''";
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
        lblOutput.Text = responseFromServer;

        // Clean up the streams.
        reader.Close();
        dataStream.Close();
        response.Close();

        //return responseFromServer;
    }
}