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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                session.setAttribute("mail", e.getMail());
                jsonConnex.addProperty("type", "Employe");
                
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
              session.setAttribute("mail", c.getAdresseMail());
              session.setAttribute("couleur", c.getCouleur());
              session.setAttribute("animal",c.getAnimalTotem());
              session.setAttribute("zodiaque",c.getSigneZodiaque());
              session.setAttribute("chinois",c.getSigneChinois());
              
              jsonConnex.addProperty("type", "Client");
              container.add("connex", jsonConnex);
            }
            else
            {
              jsonConnex.addProperty("type", "error");
              container.add("connex", jsonConnex); 
            }
            
            
        }
        System.out.println(container);
        return(gson.toJson(container));
}
    
    public static String ObtenirClient(HttpServletRequest request, HttpSession session){
        String prenom = session.getAttribute("prenom").toString();
        String nom = session.getAttribute("nom").toString();
        String animal = session.getAttribute("animal").toString();
        String zodiaque = session.getAttribute("zodiaque").toString();
        String chinois = session.getAttribute("chinois").toString();
        String couleur = session.getAttribute("couleur").toString();
        String mail = session.getAttribute("mail").toString();
        
        DateFormat f_date = new SimpleDateFormat("yyyy-MM-dd");
        Client cl = Service.obtenirClient(mail);
        String adresse = cl.getAdressePostale();
        String date = f_date.format(cl.getDateNaissance());
        String tel = cl.getNumeroTel();
        String civilite = cl.getCivilite();
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject infosPersonnes = new JsonObject();
        JsonObject container = new JsonObject();
        
        
        infosPersonnes.addProperty("prenom", prenom);
        infosPersonnes.addProperty("nom", nom);
        infosPersonnes.addProperty("animal", animal);
        infosPersonnes.addProperty("zodiaque", zodiaque);
        infosPersonnes.addProperty("chinois", chinois);
        infosPersonnes.addProperty("couleur", couleur);
        infosPersonnes.addProperty("mail", mail);
        infosPersonnes.addProperty("adresse", adresse);
        infosPersonnes.addProperty("tel", tel);
        infosPersonnes.addProperty("date", date);
        infosPersonnes.addProperty("civilite", civilite);
        
        container.add("infos", infosPersonnes);
        System.out.println(container);
        return gson.toJson(container);
    }
    
    public static String RecupererHistorique(HttpServletRequest request, HttpSession session)
    {
        Client c = Service.obtenirClient(session.getAttribute("mail").toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonConnex = new JsonObject();
        DateFormat f_date = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat f_duree = new SimpleDateFormat("HH:mm:ss");   
              JsonArray historique = new JsonArray();
              for (Voyance v: c.getListVoyances()){
                  JsonObject voyance = new JsonObject();
                  String date = f_date.format(v.getHeureDebut());
                  long duree = (v.getHeureFin().getTime()-(v.getHeureDebut()).getTime())/1000;
                  int heure = (int) (duree/3600);
                  int min = (int) (duree/60)-heure*60;
                  voyance.addProperty("date",date);
                  voyance.addProperty("duree", heure+"h"+min);
                  voyance.addProperty("nom", v.getMedium().getNom());
                  historique.add(voyance);
                  
              }
              jsonConnex.add("historique", historique);
              return(gson.toJson(historique));
    }

    public static void Deconnexion(HttpSession session) {
        session.removeAttribute("nom");
        session.removeAttribute("prenom");
        session.removeAttribute("couleur");
        session.removeAttribute("animal");
        session.removeAttribute("zodiaque");
        session.removeAttribute("chinois");
        session.removeAttribute("mail");
    }
    
    public static void Modification(HttpServletRequest request)
    {
        List <String> mod = new ArrayList <String>();
        mod.add(request.getParameter("prenom"));
        mod.add(request.getParameter("nom"));
        mod.add(request.getParameter("civilite"));
        mod.add(request.getParameter("mail"));
        mod.add(request.getParameter("adresse"));
        mod.add(request.getParameter("tel"));
        Client cl = Service.obtenirClient(request.getParameter("mail"));
        Service.modifierInfosClients(cl, mod);
    }

    public static String RecupererMedium(HttpServletRequest request, HttpSession session) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonConnex = new JsonObject();
  
        JsonArray historique = new JsonArray();
        for (Medium m: Service.getListeMediums())
        {
            JsonObject medium = new JsonObject();
            medium.addProperty("nom", m.getNom());
            System.out.println(m.getClass().toString());
            medium.addProperty("spec", m.getClass().toString().substring(15));
            medium.addProperty("id", m.getId());
            historique.add(medium);

        }
        jsonConnex.add("historique", historique);
        return(gson.toJson(historique));
    }
    
    public static void DemanderVoyance(HttpServletRequest request, HttpSession session)
    {
        System.out.println("id : "+request.getParameter("id"));
        Medium m = Service.obtenirMedium(Long.parseLong(request.getParameter("id")));        
        Client c = Service.obtenirClient(session.getAttribute("mail").toString());
        Service.demanderVoyance(c, m);
    }
}
