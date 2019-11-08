package com.IsraeliJokes;


public class MessageManager {
	
	String []m_AaddressTo;
	String m_Subject;
	String m_Body;
	
  
	public  MessageManager(	String Subject,
							String Body) 
    {
    	m_Subject = Subject;
    	m_Body = Body;
    }
    public void SendMessage( )
    {
    	String url = Category.m_SiteUrl + 
    					"/addMessage.aspx?" + 
    					m_Subject + 
    					m_Body;
		new UpdateServerAsync(null).execute( url );
    }

}
