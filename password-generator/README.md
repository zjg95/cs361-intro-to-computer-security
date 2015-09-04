# password-generator

A program to generate passwords that can be pronounced, but do not contain dictionary words (EC.java). It generates n passwords of length k, using a reference file to determine how the structure of the language works.

#### Running the program
There are two .java files. Passwords.java is the main program. It parses the reference file, creates the array of "followers," and then generates the passwords. HatGrabber.java is a class that takes in an array of values, and picks an index based on which value is the most likely to be pulled from a hat. The program outputs to the console, and a file called "output."

#### Reference file
The reference file is used to ensure that the password can be pronounced. The program calculates how likely one letter is to follow another letter. The file that we chose to use as our reference is the 6th "Game of Thrones" book, "A dance with dragons." We have never actually read this book but we are fans of the show. We found the plain text on an internet forum and copied it into a file. There are 43,152 lines in this file. According to Microsoft Word, there are 426,452 words. https://archive.org/stream/1.AGameOfThrones/5.%20A%20Dance%20With%20Dragons_djvu.txt

#### Sample output:
Passwords are:
 arinddotec
 prwnttathe
 letstgemat
 oroubloint
 cesstenger
 beinghetti
 priorstrst
 prstexinex
 stardicour
 talnaletlu
 fosentoust
 iouristogt
 akeabusmes
 coggrintta
 atoulertut
