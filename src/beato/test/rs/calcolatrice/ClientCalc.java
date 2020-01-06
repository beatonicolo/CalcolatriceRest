package beato.test.rs.calcolatrice;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beato.calc.util.Expression;
import beato.calc.util.Product;
import beato.calc.util.QualifiedExpression;

public class ClientCalc {
	
	private Client client;
	private String REST_SERVICE_URL = "http://localhost:8080/CalcolatriceRest/rest/CalcolatriceService/calcola";

	   private void init(){
		      this.client = ClientBuilder.newClient();
		   }
	   
	   public static void main(String[] args){
		      ClientCalc tester = new ClientCalc();
		      //initialize the tester
		      tester.init();
		      //test prima operazione
		      //tester.testOp();
		      //tester.insertExp();
		      //tester.insertExpPost();
		      //tester.testPut();
		      //tester.testDelete();
		      
		      Product p1= new Product();
		      p1.setId(1);
		      p1.setNomeC("nome1");
		      p1.setPrezzoU(1.0);
		      p1.setQuantita(11);
		      Product p2= new Product();
		      
		      List<Product> l=new ArrayList<Product>();
		      l.add(p1);
		      l.add(p2);
		      
		      Gson gson = new GsonBuilder().create();
		      
		      String listParsed= gson.toJson(l);
		      
		      System.out.println(listParsed);
		      
		      
		      
		      }
	   
	 /*  private void testOp() {
		   String result="stringa inizializzata al valore di default";
		   result=client.target(REST_SERVICE_URL).path("/{expression}").resolveTemplate("expression", "33+4").request().get(String.class);
		   System.out.println(result);
	   }*/
	   
	   private void insertExp() {
		   
		   System.out.println("TEST GET");
		   
		   
		   Scanner sc = new Scanner(System.in);
		
		   System.out.println("inserire op1:");
		   double op1 = sc.nextDouble();
		   System.out.println("inserire op2:");
		   double op2= sc.nextDouble();
		  System.out.println("inserire operatore");
		   char operatore = sc.next().charAt(0);
		   
		   if (operatore=='/')
			   operatore='d';
		   
		   Expression exp=new Expression(op1,op2,operatore);
		   //Gson gson = new Gson();
		   Gson gson = new GsonBuilder().create();
		   String jsonExp = gson.toJson(exp);
		   
		  String output=client.target(REST_SERVICE_URL).path("/{exp}").resolveTemplate("exp", jsonExp).request(MediaType.APPLICATION_JSON).get(String.class);
		  double result= ((Expression)gson.fromJson(output, Expression.class)).getResult();
		  System.out.println("stampa del gson:"+output);
		  System.out.println("risultato operazione "+result);
		  
		sc.close();
	   }
	   
	   private void insertExpPost() {
		   
		   
		   System.out.println("TEST POST");
		   
		   Scanner sc = new Scanner(System.in);
			
			   System.out.println("inserire op1:");
			   double op1 = sc.nextDouble();
			   System.out.println("inserire op2:");
			   double op2= sc.nextDouble();
			  System.out.println("inserire operatore");
			   char operatore = sc.next().charAt(0);
			   
			   if (operatore=='/')
				   operatore='d';
			   
			   sc.close();
			   
			   Expression exp=new Expression(op1,op2,operatore);
			  
			   Gson gson = new GsonBuilder().create();
			   String jsonExp = gson.toJson(exp);
			   
			   String output=client.target(REST_SERVICE_URL).request(MediaType.APPLICATION_JSON).post(Entity.entity(jsonExp, MediaType.APPLICATION_JSON),String.class);
			   double result= ((Expression)gson.fromJson(output, Expression.class)).getResult();
			  System.out.println("stampa del gson:"+output);
			  System.out.println("risultato operazione "+result);
			  
		   }
	   
	   
	   private void testPut() {
		   
		   System.out.println("TEST PUT");
		   Scanner sc = new Scanner(System.in);
			
		   System.out.println("inserire op1:");
		   double op1 = sc.nextDouble();
		   System.out.println("inserire op2:");
		   double op2= sc.nextDouble();
		  System.out.println("inserire operatore");
		   char operatore = sc.next().charAt(0);
		   System.out.println("inserire id da sostituire:");
		   int id = sc.nextInt();
		   
		   if (operatore=='/')
			   operatore='d';
		   
		   sc.close();
		   
		   QualifiedExpression exp=new QualifiedExpression(id,op1,op2,operatore);
		  
		   Gson gson = new GsonBuilder().create();
		   String jsonExp = gson.toJson(exp);
		   
		   String output=client.target(REST_SERVICE_URL).request().put(Entity.entity(jsonExp, MediaType.APPLICATION_JSON),String.class);
		  System.out.println("stampa del gson:"+output);
		   
	   }
	   
	   private void testDelete() {
		   
		   System.out.println("TEST DELETE");
		   
		   Scanner sc = new Scanner(System.in);
			
		   System.out.println("inserire ID operazione entry da eliminare:");
		   int id = sc.nextInt();
		   sc.close();
		   String output=client.target(REST_SERVICE_URL).path("/{id}").resolveTemplate("id", ""+id).request().delete(String.class);
		   
		   System.out.println("output modifica:"+ output);
	   }
}
