import java.util.*;
import edu.duke.*;
import java.io.*;

public class VigenereBreaker {
    
    private String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder s = new StringBuilder();
        for(int i= whichSlice;i<message.length();i=i+totalSlices)
        {
            s.append(message.charAt(i));
        }
        return s.toString();
    }

    private int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        for(int i=0;i<klength;i++)
        {
            String oneof = sliceString(encrypted,i,klength);
            CaesarCracker c = new CaesarCracker(mostCommon);
            key[i] = c.getKey(oneof);
        }
        //for(int i:key){System.out.println(i);}
        return key;
    }

    public void KNOW_ALL_breakVigenere () 
    {
        FileResource fr = new FileResource();
        char c ='e';
        int klen=4;
        int [] keys = tryKeyLength(fr.asString(),klen,c);
        VigenereCipher vc = new VigenereCipher(keys);
        System.out.println(vc.decrypt(fr.asString()));
    }
    
    
    public void test()
    {
       FileResource fr = new FileResource("dictionaries/English");
       HashSet<String> langDict=readDictionary(fr);
       System.out.println(mostCommonCharIn(langDict));      
    }
    
    private HashSet<String > readDictionary(FileResource fr)
    {
        HashSet<String> hs = new HashSet<String>();
        for(String s : fr.words())
        {
            if(!hs.contains(s))
            {
                hs.add(s.toLowerCase());
            }
        }
        return hs;
    }
    
    private char mostCommonCharIn(HashSet<String> dictionary)
    {
        String alphabet="abcdefghijklmnopqrstuvwxyz";
        int[] count = new int[alphabet.length()];
        for(String word:dictionary)
        {
            for(char character : word.toCharArray())
            {
                int index=alphabet.indexOf(character);
                if(index>0)
                {
                    count[index]++;
                }
            }
        }
        int max =0;
        int maxDex=0;
        for(int i=0;i<count.length;i++)
        {
            if(max<count[i])
            {
                max=count[i];
                maxDex=i;
            }
        }
        return alphabet.charAt(maxDex);
    }
    
    private int countWords(String message, HashSet<String> lexicon)
    {
        int count=0;
        for(String item : message.split("\\W"))
        {
            if(lexicon.contains(item.toLowerCase()))
            {
                count++;
            }
        }
        return count;
    }
    
    private String breakForLanguage(String encrypted, HashSet<String> ledi)
    {
        char c ='e';
        VigenereCipher vc ;
        int max=0;
        int now=0;
        int keep=0;
        String decryted="";
        for(int klen =1;klen<100;klen++)
        {
            vc= new VigenereCipher( tryKeyLength(encrypted,klen,c));
            now=countWords(vc.decrypt(encrypted),ledi);
            if(max<now)
            {
             max=now;
             keep=klen;
            }
        }
        int [] keys = tryKeyLength(encrypted,keep,c);
         vc= new VigenereCipher( keys );
         decryted = vc.decrypt(encrypted);
         return(decryted);
    }
    
    public void KEYLENGTH_UNKNOWN_breakVigenere () 
    {
        FileResource fr = new FileResource("dictionaries/English");
        HashSet<String> dic=readDictionary(fr);
        FileResource nfr =new FileResource();
        System.out.println(breakForLanguage (nfr.asString(),dic));
    }
           
    private String breakForAllLanguages(String encrypted, HashMap<String,HashSet<String>> language)
    {
        VigenereCipher vc ;
        int max=0;
        int now=0;
        int keep=0;
        char c ='a';
        String CurrLang="";
        for(String lan :language.keySet())
        {
            c= mostCommonCharIn(language.get(lan));
            for(int klen =1;klen<100;klen++)
            {
                vc= new VigenereCipher( tryKeyLength(encrypted,klen,c));
                now=countWords(vc.decrypt(encrypted),language.get(lan));
                if(max<now)
                {
                    max=now;
                    keep=klen;
                    CurrLang=lan;
                }
            }
            
        }
        int [] keys = tryKeyLength(encrypted,keep,c);
         vc= new VigenereCipher(keys);
         String decrypted = vc.decrypt(encrypted);
         //System.out.println("Key Length is -------------------------->"+keep);
         //System.out.println("Number of Valid Words for the Language>>>>>>>> "+CurrLang +"------------------->"+countWords(decrypted,language.get(CurrLang)));
        return(decrypted);
    }
    
    public void LANGUAGE_UNKNOWN_breakVigenere () 
    {
        HashMap<String,HashSet<String>> languageLexicon = new HashMap<String,HashSet<String>> ();
        DirectoryResource dr = new DirectoryResource();
        FileResource fr; 
        for(File f : dr.selectedFiles())
        {
            fr= new FileResource("dictionaries/"+f.getName()+"");
            HashSet<String> dic=readDictionary(fr);
            languageLexicon.put(f.getName(),dic);
        }
        FileResource nfr =new FileResource();
        System.out.println(breakForAllLanguages (nfr.asString(),languageLexicon));
    }
}
