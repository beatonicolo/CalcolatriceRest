package beato.test.rs.calcolatrice;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken

import beato.calc.util.Expression;
import beato.calc.util.Product;
import beato.calc.util.QualifiedExpression;

@Path("/CalcolatriceService")
public class CalcolatriceService {
	
	// JDBC driver name and database URL
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		static final String DB_URL = "jdbc:mysql://localhost/HISTORYCALC?useLegacyDatetimeCode=false&serverTimezone=CET";
		//  Database credentials
		static final String USER = "username";
		static final String PASS = "password";
		//queries
		static final String updateQ = "UPDATE storico SET op1=?, op2=?, operator=?, result=?, stamp=? WHERE id=?";
		static final String deleteQ= "DELETE FROM storico WHERE id=?";
	
	@GET
	public String welcome() {	
		return "Service Started";
	}
	
	@GET
	@Path("/calcola")
	public String writeSimpleString() {	
		return "stringa di prova 4484598655";
	}
	
	@GET
	@Path("/calcola/{exp}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String sendBackExp(@PathParam("exp") String exp) {
		
		Gson gson = new GsonBuilder().create();
		Expression userExp = gson.fromJson(exp, Expression.class);
		
		if(userExp.getOperator()=='+')
			userExp.setResult(userExp.getOp1()+userExp.getOp2());
		else if(userExp.getOperator()=='-')
			userExp.setResult(userExp.getOp1()-userExp.getOp2());
		else if(userExp.getOperator()=='*')
			userExp.setResult(userExp.getOp1()*userExp.getOp2());
		else if(userExp.getOperator()=='/')
			userExp.setResult((userExp.getOp1())/(userExp.getOp2()));
		else if(userExp.getOperator()=='d')
			userExp.setResult((userExp.getOp1())/(userExp.getOp2()));
		else if (userExp.getOperator()==' '||userExp.getOperator()=='=')
			userExp.setResult(userExp.getOp2());
		
		String jsonResponse = gson.toJson(userExp);
		return jsonResponse;
	}
	
	@POST
	@Path("/calcola")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String sendBackPostExp(String jsonExp) {
		
		Gson gson = new GsonBuilder().create();
		Expression userExp = gson.fromJson(jsonExp, Expression.class);
		
		userExp.setResult(calculate(userExp.getOp1(),userExp.getOp2(),userExp.getOperator()));
		
		String jsonResponse = gson.toJson(userExp);
		return jsonResponse;
	}
	
	private double calculate(double op1,double op2,char operator) {
		double res=0.0;
		if(operator=='+')
			res=op1+op2;
		else if(operator=='-')
			res=op1-op2;
		else if(operator=='*')
			res=op1*op2;
		else if(operator=='/')
			res=op1/op2;
		else if(operator=='d')
			res=op1/op2;
		else if (operator==' '||operator=='=')
			res=op2;
		return res;
	}
	
	//PUT passo qualified expression, andrò a sustuire nel db l'operation id
	@PUT
	@Path("/calcola")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.application_)
	public String updateExp(String jsonExp) {
		
		Gson gson = new GsonBuilder().create();
		QualifiedExpression userExp = gson.fromJson(jsonExp, QualifiedExpression.class);
		
		userExp.setResult(calculate(userExp.getOp1(),userExp.getOp2(),userExp.getOperator()));
		
		updateRecord(userExp);
		
		return "modifica nel db effettuata";
	}
	
	private void updateRecord(QualifiedExpression exp) {
		
		Connection conn = null;
		PreparedStatement prpstmt = null;
		
		java.util.Date date=new java.util.Date();
		Timestamp timestamp = new Timestamp( date.getTime());
		
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			
			prpstmt=conn.prepareStatement(updateQ);
			prpstmt.setDouble(1, exp.getOp1());
			prpstmt.setDouble(2, exp.getOp2());
			prpstmt.setString(3, ""+exp.getOperator());
			prpstmt.setDouble(4, exp.getResult());
			prpstmt.setTimestamp(5, timestamp);
			prpstmt.setInt(6, exp.getId());
			
			prpstmt.executeUpdate();
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
		         if(prpstmt!=null)
		            prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
		         se.printStackTrace();}
		}	
	}
	
	@DELETE
	@Path("/calcola/{id}")
	public String deleteRecord(@PathParam("id") String id){
		
		Connection conn = null;
		PreparedStatement prpstmt = null;
		
		java.util.Date date=new java.util.Date();
		Timestamp timestamp = new Timestamp( date.getTime());
		
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			
			prpstmt=conn.prepareStatement(deleteQ);
			prpstmt.setInt(1, Integer.valueOf(id));
			
			prpstmt.executeUpdate();
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
		         if(prpstmt!=null)
		            prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
		         se.printStackTrace();}
		}		
		return "cancellazione entry effettuata";
	}
	
	@OPTIONS
	@Path("/calcola")
	public String listaOperazioniSupportate() {
		return "sono supportate le seguenti operazioni:\n GET\n POST\n PUT\n DELETE\n";
	}
	
	@POST
	@Path("/calcolaTotale")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String calcolaTotale(String carrellojson) {
		
		double totale=0.0;
		Gson gson = new GsonBuilder().create();
		Product[] arrCarrello = gson.fromJson(carrellojson, Product[].class);
		List<Product> listaCarrello = new ArrayList( Arrays.asList(arrCarrello));
		
		for (int i=0; i<listaCarrello.size();i++)
			totale+=(listaCarrello.get(i).getPrezzoU()*listaCarrello.get(i).getQuantita());
		
		String totaleParsed=gson.toJson(totale);
		return totaleParsed;
	}
	

}
