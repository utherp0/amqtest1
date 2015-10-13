package org.uth.amq.tests;

import java.util.Properties;
import javax.jms.*;
import javax.naming.*;

public class ExampleProducer
{
  private String queueName = "org.uth.testqueue1";
  private String user = "youruser";
  private String password = "yourpassword";
  
  private String url = "tcp://hostname:61616";
	
  private boolean transacted;
  private boolean isRunning = false;
	
  public static void main(String[] args) throws NamingException, JMSException
  {
    ExampleProducer producer = new ExampleProducer();
    producer.run();
  }
	
  public ExampleProducer() {}
	
  public void run() throws NamingException, JMSException
  {		
    //JNDI properties
    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
    props.setProperty(Context.PROVIDER_URL, url);
		
    //specify queue propertyname as queue.jndiname
    props.setProperty("queue.slQueue", queueName);
		
    javax.naming.Context ctx = new InitialContext(props);
    ConnectionFactory connectionFactory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
    Connection connection = connectionFactory.createConnection(user, password);
    connection.start();
		
    Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
		
    Destination destination = (Destination)ctx.lookup("slQueue");
    MessageProducer producer = session.createProducer(destination);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    for( int loop = 0; loop < 100; loop++ )
    {
      TextMessage message = session.createTextMessage("Test Message " + loop);
      producer.send(message);
    }
		
    producer.close();
    session.close();
    connection.close();
  }
}

