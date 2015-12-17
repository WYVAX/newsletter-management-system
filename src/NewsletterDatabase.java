import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;


public class NewsletterDatabase 
{
	public final String DATABASE_URL = "jdbc:derby:C:/TestDB;create=true";
	
	//Load driver and initialize DB
	public NewsletterDatabase()
	{
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			initialize("C:\\TestDB");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//Initialize DB to content in "User.del"
	public void initialize(String databaseDirectoryPath) throws Exception 
	{

		Connection databaseConnection = null;
		
		try
		{
			File databaseDirectory = verifyDatabaseDirectory(databaseDirectoryPath);
			databaseDirectoryPath = databaseDirectory.getCanonicalPath();

			System.setProperty("derby.system.home", databaseDirectoryPath);

			Properties databaseProperties = new Properties();
			databaseProperties.put("create", "true");

			databaseConnection = DriverManager.getConnection(DATABASE_URL, databaseProperties);

			if(tableExists("SUBSCRIBERS", databaseConnection) == false)
			{
								
				File userDataFile = verifyDataFile(databaseDirectory, "User.del");
				String userDataFilePath = userDataFile.getCanonicalPath();

				String createUserTableSQL = "CREATE TABLE SUBSCRIBERS(SUBSCRIBERS_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NAME VARCHAR(128) NOT NULL, EMAIL VARCHAR(64) NOT NULL, PRIMARY KEY(SUBSCRIBERS_ID), UNIQUE(EMAIL))";
				createTable(createUserTableSQL, databaseConnection);

				String loadOwnerTableSQL = "CALL SYSCS_UTIL.SYSCS_IMPORT_DATA(NULL, 'SUBSCRIBERS', 'NAME,EMAIL','1,2', '" + userDataFilePath + "', ';', '%', 'UTF-8', 0)";
				loadTable(loadOwnerTableSQL, databaseConnection);
			}

			
		}
		finally
		{
			if(databaseConnection != null) databaseConnection.close();
		}
		
	}

	//Return all entries in the DB
	public SubscriberList getSubscribers() throws SQLException
	{
		
		SubscriberList userSet = null;

		Connection databaseConnection = null;
		Statement queryStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			
			databaseConnection = DriverManager.getConnection(DATABASE_URL);

			queryStatement = databaseConnection.createStatement();
			
			
			String querySQL = "SELECT SUBSCRIBERS.NAME, SUBSCRIBERS.EMAIL FROM SUBSCRIBERS";
			resultSet = queryStatement.executeQuery(querySQL);
			

			SubscriberList users = new SubscriberList();

			while(resultSet.next())
			{
				String name = resultSet.getString("NAME");
				String email = resultSet.getString("EMAIL");
				users.add(new Subscriber(name, email));
			}
			userSet = users;
		}
		finally
		{
			if(resultSet != null) resultSet.close();
			if(queryStatement != null) queryStatement.close();
			if(databaseConnection != null) databaseConnection.close();
		}
		return userSet;
	}
	
	//Insert into DB
	public void insertUser(Subscriber subscriber) throws SQLException
	{
		Connection databaseConnection = null;
		Statement statement = null;
		int returnVal;
		
		try
		{
			databaseConnection = DriverManager.getConnection(DATABASE_URL);
			statement = databaseConnection.createStatement();
			String querySQL = "INSERT INTO SUBSCRIBERS (NAME, EMAIL) VALUES('" + subscriber.getName() + "', '" + subscriber.getEmail() + "')";
			returnVal = statement.executeUpdate(querySQL);
		
		}
		finally
		{
			if(statement != null) statement.close();
			if(databaseConnection != null) databaseConnection.close();
		}
	}
	
	//Delete from DB
	public void deleteUser(String email) throws SQLException
	{
		Connection databaseConnection = null;
		Statement statement = null;
		int returnVal;
		
		try
		{
			databaseConnection = DriverManager.getConnection(DATABASE_URL);
			statement = databaseConnection.createStatement();
			String querySQL = "DELETE FROM SUBSCRIBERS WHERE EMAIL = '" + email + "'";
			returnVal = statement.executeUpdate(querySQL);
		}
		finally
		{
			if(statement != null) statement.close();
			if(databaseConnection != null) databaseConnection.close();
		}
	}
	
	//Replace old email with a new one
	public void updateUser(String oldEmail, String newEmail) throws SQLException
	{
		Connection databaseConnection = null;
		Statement statement = null;
		int returnVal;
		
		try
		{
			databaseConnection = DriverManager.getConnection(DATABASE_URL);
			statement = databaseConnection.createStatement();
			String querySQL = "UPDATE SUBSCRIBERS SET EMAIL = '" + newEmail + "' WHERE EMAIL = '" + oldEmail + "'";
			returnVal = statement.executeUpdate(querySQL);
		}
		finally
		{
			if(statement != null) statement.close();
			if(databaseConnection != null) databaseConnection.close();
		}
	}

	//Send emails to all addresses in the database
	public void emailDatabase() throws SQLException
	{    
		String month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String subject = month + " " + year + " Newsletter";
		String message = "Generic Newsletter text for " + month + " " + year + ".";
		
		SubscriberList subscribers = getSubscribers();
		for(Subscriber S: subscribers)
		{
			Pigeon.emailer(S.getEmail(), subject, message);				
		}
	}
	
	//Make sure the DB string is valid
	private  File verifyDatabaseDirectory(String databaseDirectoryPath) throws Exception
	{
		if(databaseDirectoryPath != null && (databaseDirectoryPath = databaseDirectoryPath.trim()).length() > 0)
		{
			File databaseDirectory = new File(databaseDirectoryPath);

			if(databaseDirectory.exists())
			{
				if(databaseDirectory.isDirectory())
				{
					return databaseDirectory;
				}
				else
				{
					throw new Exception("The database directory path is not a valid directory path.");
				}
			}
			else
			{
				throw new Exception("The database directory does not exist.");
			}
		}
		else
		{
			throw new Exception("The database directory path is invalid.");
		}
	}
	
	private  boolean tableExists(String tableName, Connection databaseConnection) throws Exception
	{
		boolean tableExists = false;

		PreparedStatement tableExistsStatement = null;
		ResultSet resultSet = null;

		try
		{
			String tableExistsSQL = "SELECT COUNT(TABLENAME) AS TABLE_EXISTS FROM SYS.SYSTABLES WHERE TABLENAME = '" + tableName + "'";

			tableExistsStatement = databaseConnection.prepareStatement(tableExistsSQL);
			resultSet = tableExistsStatement.executeQuery();

			if(resultSet != null && resultSet.next())
			{
				int result = resultSet.getInt("TABLE_EXISTS");

				if(result > 0)
				{
					tableExists = true;
				}
			}
		}
		finally
		{
			if(resultSet != null)
			{
				resultSet.close();
			}

			if(tableExistsStatement != null)
			{
				tableExistsStatement.close();
			}
		}

		return tableExists;
	}
	
	private  File verifyDataFile(File databaseDirectory, String dataFilePath) throws Exception
	{
		File dataFile = new File(databaseDirectory, dataFilePath);

		if(dataFile.exists())
		{
			if(dataFile.isFile())
			{
				if(dataFile.canRead())
				{
					return dataFile;
				}
				else
				{
					throw new Exception("The data file cannot be read.");
				}
			}
			else
			{
				throw new Exception("The data file is not valid.");
			}
		}
		else
		{
			throw new Exception("The data file does not exist.");
		}
	}
	
	private  void createTable(String tableCreationSQL, Connection databaseConnection) throws Exception
	{
		Statement tableCreationStatement = null;
		try
		{
			tableCreationStatement = databaseConnection.createStatement();
			tableCreationStatement.executeUpdate(tableCreationSQL);
		}
		finally
		{
			if(tableCreationStatement != null) tableCreationStatement.close();
		}
	}	

	private void loadTable(String loadTableSQL, Connection databaseConnection) throws Exception
	{
		CallableStatement loadTableStatement = null;
		try
		{
			loadTableStatement = databaseConnection.prepareCall(loadTableSQL);
			loadTableStatement.execute();
		}
		finally
		{
			if(loadTableStatement != null) loadTableStatement.close();
		}
	}
	
	private String generateDatabaseRowID()
	{
		UUID randomUUID = UUID.randomUUID();
		String randomUUIDString = randomUUID.toString();
		String databaseRowID = randomUUIDString.replaceAll("-", "");
		return databaseRowID;
	}
}
