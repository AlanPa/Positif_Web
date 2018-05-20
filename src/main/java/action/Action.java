/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entities.Client;
import entities.Employe;
import entities.Medium;
import entities.Voyance;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import service.Service;

/**
 *
 * @author alan
 */
public class Action 
{
    
    public static String Inscription(HttpServletRequest request) throws ParseException
    {       
            String nom=request.getParameter("nom");
            String prenom=request.getParameter("prenom");
            String adresseMail=request.getParameter("mail");
            String adressePostale=request.getParameter("adresse");
            String civilite=request.getParameter("civilite");
            String numeroTel=request.getParameter("tel");
            try
            {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str_date=request.getParameter("date");
            Date dateNaissance=sdf.parse(str_date);
            
            if(Service.obtenirClient(adresseMail) != null)
            {
                return("False");
            }
            else
            {
                System.out.println(str_date);
                Client cl = new Client(civilite,nom,prenom,dateNaissance,adressePostale,numeroTel,adresseMail);
                Service.creerClient(cl);
                 return("True");
            }
            }
            catch( ParseException e)
            {
                 System.out.println("Erreur");
              return "Error" ;
            }      
            
            
        
    }
    
    public static String Connexion(HttpServletRequest request, HttpSession session){
        String mail = request.getParameter("mail");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonConnex = new JsonObject();
        JsonObject container = new JsonObject();
        String[] tab = mail.split("@");
        int size = tab.length;
        if (tab[size-1].equals("posit.if")){

            Employe e = (Employe) Service.obtenirEmploye(mail);
            if (e != null)
            {
                session.setAttribute("nom",e.getNom());  
                session.setAttribute("prenom",e.getPrenom());
                jsonConnex.addProperty("type", "Employe");
                
                //jsonConnex.addProperty("listeMedium", e.getListMedium());
                JsonArray listeMed = new JsonArray();
                 for (Medium m: e.getListMedium()){
                  JsonObject medium = new JsonObject();
                  medium.addProperty("date",m.getNom());
                  listeMed.add(medium);
                  
              }
                jsonConnex.add("listeMed", listeMed);
                
                JsonArray historique = new JsonArray();
                for (Voyance v: e.getListeVoyances()){
                  JsonObject voyance = new JsonObject();
                  voyance.addProperty("date",v.getHeureDebut().getTime());
                  voyance.addProperty("duree", (v.getHeureFin().getTime()-(v.getHeureDebut()).getTime()) );
                  voyance.addProperty("nom", v.getMedium().getNom());
                  historique.add(voyance);
                  
              }
                jsonConnex.add("historique", historique);
                container.add("connex", jsonConnex);
            }
            else
            {
                jsonConnex.addProperty("type", "error");
                container.add("connex", jsonConnex);
            }

        }
        else{
            Client c = Service.obtenirClient(mail); 
            if (c != null)
            {
              session.setAttribute("nom",c.getNom());  
              session.setAttribute("prenom",c.getPrenom());
              jsonConnex.addProperty("type", "Client");
              jsonConnex.addProperty("nom",c.getNom());  
              jsonConnex.addProperty("prenom",c.getPrenom());
              jsonConnex.addProperty("couleur", c.getCouleur());
              jsonConnex.addProperty("animal",c.getAnimalTotem());
              jsonConnex.addProperty("zodiaque",c.getSigneZodiaque());
              jsonConnex.addProperty("chinois",c.getSigneChinois());
              
              JsonArray historique = new JsonArray();
              for (Voyance v: c.getListVoyances()){
                  JsonObject voyance = new JsonObject();
                  voyance.addProperty("date",v.getHeureDebut().getTime());
                  voyance.addProperty("duree", (v.getHeureFin().getTime()-(v.getHeureDebut()).getTime()) );
                  voyance.addProperty("nom", v.getMedium().getNom());
                  historique.add(voyance);
                  
              }
              jsonConnex.add("historique", historique);
              container.add("connex", jsonConnex);
            }
            else
            {
              jsonConnex.addProperty("type", "error");
              jsonConnex.addProperty("nom","error");  
              jsonConnex.addProperty("prenom","error");
              jsonConnex.addProperty("couleur", "error");
              jsonConnex.addProperty("animal","error");
              jsonConnex.addProperty("zodiaque","error");
              jsonConnex.addProperty("chinois","error"); 
              container.add("connex", jsonConnex); 
            }
            
            
        }
        System.out.println(container);
        return(gson.toJson(container));
}
    
    public static String ObtenirClient(HttpServletRequest request, HttpSession session){
        
        String mail = session.getAttribute("mail").toString();
        Client c = Service.obtenirClient(mail); 
        
        JsonObject infosPers = new JsonObject();
        JsonObject containter = new JsonObject();
        
        infosPers.addProperty("type", "Client");
        infosPers.addProperty("nom",c.getNom());  
        infosPers.addProperty("prenom",c.getPrenom());
        infosPers.addProperty("couleur", c.getCouleur());
        infosPers.addProperty("animal",c.getAnimalTotem());
        infosPers.addProperty("zodiaque",c.getSigneZodiaque());
        infosPers.addProperty("chinois",c.getSigneChinois());
        
        
        
    }
}
