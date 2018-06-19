/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Damon
 */
@Path("/getfiles/")

public class getNoOfFiles extends Thread implements Runnable{

    private static final Object LOCK_OBJECT = new Object();
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of DoSearch
     */
    public getNoOfFiles() {
       
    }
    public getNoOfFiles(String fileName, String searchTerm){
       
    }

    /**
     * Retrieves representation of an instance of SearchFile.DoSearch
     *
     * @param searchterm;
     * @return an instance of java.lang.String
     */
    private String searchTerm;
    private int index = 4;
    
    public ArrayList<String> getFileName() {
        ArrayList<String> textFilesToRead = new ArrayList<>();
        String dir = System.getProperty("user.home");
        File files = new File(dir);
        File[] allTextFiles = files.listFiles();

        for (File file : allTextFiles) {
            if (file.getName().endsWith(".txt")) {
                System.out.println(file.getName());
                textFilesToRead.add(file.getName());
            }
        }
        return textFilesToRead;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml(@PathParam("searchterm") String searchterm, @QueryParam("index") int index) {
        //TODO return proper representation object
        // throw new UnsupportedOperationException();

        ArrayList<String> filesToSearch = getFileName();     
        return "<nooffiles>" + filesToSearch.size() + "</nooffiles>";
        }          
         

    /**
     * PUT method for updating or creating an instance of DoSearch
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
