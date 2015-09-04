# password-crack

A program to crack passwords. The password cracker seems to work fine, but it is kind of slow. For "passwd1" it can crack all the passwords except the last password. For "passwd2" though, things are slightly different. The whole run takes 17 minutes, and it will crack 16/20 passwords. Because our level 3 search takes so much time already, we decided to only have our cracker use 3 levels instead of going to deeper levels.

#### Running the program
There are 5 .java files. PasswordCrack.java is the main program. It loads in the dictionary file as well as the password list. It then goes user by user and attempts to crack the password for that user. Jcrypt.java was given to us by Dr. Young. StringTransformer.java takes in a given input string and a method to perform, and mangles the string. For example, it might delete the first/last character in the string, or reverse the string. Timer.java is used for timing. Level.java is a class that inherits from Thread. It recursively passes through all the mutations of a string and compares it to the encrypted answer.
