import java.util.ArrayList;

class AuthenticationManager {

    static ArrayList Usernames;                                 //werden mit SQLDatenbank ausgetauscht
    static ArrayList Passwords;



    public boolean register(String Name, String Password){
        for (int i = 0; i < Usernames.size(); i++){             //Checken ob name schon vorhanden
            if (Usernames.get(i).equals(Name)){
                return false;                                   // Falls vorhander return false
            }
        }
        Usernames.add(Name);                                    // Name in Liste einfügen
        Passwords.add(Password.hashCode());                     // Passworthash einfügen

        return true;
    }



    public static boolean login(String Name, String Password){
        int id = 0;                                             // position in Arraylist
        for (int i = 0; i < Usernames.size(); i++){             // falls name in Arraylist id = position
            if (Usernames.get(i) == Name){
                id = i;
                continue;
            }

        }
        if (Passwords.get(id).equals(Password.hashCode()) ){    // checken ob an position id der hashwert des passwords ist
            return true;                                        // falls ja return true
        }

        return false;                                           // falls name nicht in liste oder ppasswort fallsch wird false returned
    }


}