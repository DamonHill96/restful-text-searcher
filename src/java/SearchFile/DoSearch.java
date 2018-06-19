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
import javax.swing.text.Document;
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
import org.apache.commons.text.StringEscapeUtils;
/**
 * REST Web Service
 *
 * @author Damon
 */
@Path("/searchfile/{searchterm}")

public class DoSearch extends Thread implements Runnable{

    private static final Object LOCK_OBJECT = new Object();
    @Context
    private UriInfo context;
    
    /**
     * Creates a new instance of DoSearch
     */
    public DoSearch() {
       
    }
    public DoSearch(String fileName, String searchTerm){
       try{
                String dir = System.getProperty("user.home");
                text = new Scanner(new File(dir,fileName));
                lines = new Scanner(new File(dir, fileName)); // Used for finding no of lines. hopefully works
                this.searchTerm = searchTerm;
                
         }catch(FileNotFoundException e){
                System.err.println("We couldn't find the file! Does it exist?");
                System.exit(0);
        
         }
    }

    /**
     * Retrieves representation of an instance of SearchFile.DoSearch
     *
     * @param searchterm;
     * @return an instance of java.lang.String
     */
    private String searchTerm;
    private int indexFile = 0;
    
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
        this.indexFile = index;
        System.out.println(context.getPathParameters());
        System.out.println("path: " + context.getAbsolutePath());
        if (filesToSearch.isEmpty()) {
            return ("No text files found! Are they in your user directory?");

        }

        //If search term is empty
        if (searchterm.equals("")) {
            return ("You didn't type in a phrase to search for!");

        }
        
        DoSearch doSearch = null;
        
            doSearch = new DoSearch(filesToSearch.get(index), searchterm);
            doSearch.readText();
         //   Document xmlString = stringToXML(Arrays.toString(doSearch.getBuiltString())); //stores the xml
            String string;
            StringBuffer sb = new StringBuffer();
            sb.append("<result>").append(Arrays.toString(doSearch.getBuiltString())).append("</result>");
            string = sb.toString();
           
                     
            if (searchterm.equals("a")) {
            return "<test>yep</test>";
        } else {
            return string;
        }
            
        }          
        
      
    private Scanner text;
    private Scanner lines;

    private int noOfLines = 0;
    private boolean isFound = false; //If search term is never found, this never changes 
    private StringBuffer[] str = null; //Thread safe

    public StringBuffer[] readText() { // fileRead corresponds to which file is being read
        synchronized (LOCK_OBJECT) {
            setNoOfLines(0); //Needed to make sure it resets every loop!
            setFound(false);
            try {

                int occupiedSpace = 0; //used to clear null from sb
                int lineNo = 0;
                int posIndex = 1;
                int noOfWordsInLine;
                int index = 0;

                //Messy, but makes things easier
                while (lines.hasNextLine()) {
                    String temp = lines.nextLine();
                    noOfLines++;
                }
                setNoOfLines(noOfLines);
                posIndex = 0; //Reset for new line
                str = new StringBuffer[noOfLines];
                while (text.hasNextLine()) {
                    String currentLine = text.nextLine(); //read into var
                    String[] word = currentLine.split(" "); //Checks for space to end word
                    String[] wordMem;
                    lineNo++;

                    noOfWordsInLine = 0; // reuse this as well
                    index = 0;

                    for (String check : word) {
                        noOfWordsInLine++;

                    }
                    wordMem = new String[noOfWordsInLine + 1]; // Stores this for recall when search term is found
                    //Does this every line
                    for (String i : word) { //Checks each word; makes it easier to output whole line

                        wordMem[index] = i;
                        index++;
                    }
                    //Checks to see how many occurrences there are
                    int noOfTimesFound = 0;
                    StringBuffer result = new StringBuffer();
                    for (int a = 0; a < wordMem.length - 1; a++) {
                        if (wordMem[a].equals(searchTerm)) {
                            noOfTimesFound++;
                            if (noOfTimesFound > 1) {
                                result.append(", ");
                            }
                            result.append(a + 1);
                        }

                    }
                    // each array index is all line matches for a file - 2 matches = 2 indices

                    if (noOfTimesFound == 1) {
                        isFound = true;
                        occupiedSpace++;
                        setFound(isFound); //Now we don't need to say it failed!
                        // System.out.println("success");

                        str[posIndex] = handleFinishedLine(wordMem);
                        str[posIndex].append(System.getProperty("line.separator"));
                        str[posIndex].append("Word was found on Line " + getLine(lineNo) + " ");
                        str[posIndex].append("Position " + getPos(result));

                        posIndex++; // next line please
                        System.out.println("\n");
                    } else if (noOfTimesFound > 1) {
                        occupiedSpace++;
                        setFound(true); //Now we don't need to say it failed!

                        str[posIndex] = handleFinishedLine(wordMem);
                        str[posIndex].append(System.getProperty("line.separator"));
                        str[posIndex].append("Word was found on Line " + getLine(lineNo) + " ");
                        str[posIndex].append("Position " + getPos(result));
                        
                        posIndex++;
                        System.out.println("\n");
                    } else if (noOfTimesFound == 0) {
                        // str[posIndex].append("We couldn't find the word in the file.");
                    }
                }

                StringBuffer[] finalstr = new StringBuffer[occupiedSpace];
                for (int a = 0; a < occupiedSpace; a++) {
                    finalstr[a] = str[a];

                }
                setBuiltString(finalstr);
                setNoOfLines(occupiedSpace);
                return finalstr;
            } catch (IllegalStateException e1) {
                System.err.println("We tried to read the file, but it was closed!");
                System.exit(0);
            } finally {
                text.close();
            }
            return str;
        }
    }

    public StringBuffer handleFinishedLine(String[] pos) {

        //  System.out.println("Search term was found on line " + line + ". Word is in position number " + res);
        StringBuffer strbuild = new StringBuffer();
        for (int y = 0; y < pos.length - 1; y++) {
            System.out.print(pos[y] + " ");
            strbuild.append(pos[y] + " ");
        }
        
        return strbuild;
    }

    public int getLine(int line) {
        return line;
    }

    public StringBuffer getPos(StringBuffer res) {
        return res;
    }

    public void setNoOfLines(int noOfLines) {
        this.noOfLines = noOfLines;
    }

    public int getNoOfLines() {
        return this.noOfLines;
    }

    public void setFound(boolean isFound) {
        this.isFound = isFound;
    }

    public boolean getFound() {
        return this.isFound;
    }

    public void setBuiltString(StringBuffer str[]) {
        this.str = str;
    }

    public StringBuffer[] getBuiltString() {
        return this.str;
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
